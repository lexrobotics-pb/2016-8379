/*
 * Copyright (c) 2014, 2015 Qualcomm Technologies Inc
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * (subject to the limitations in the disclaimer below) provided that the following conditions are
 * met:
 *
 * Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions
 * and the following disclaimer in the documentation and/or other materials provided with the
 * distribution.
 *
 * Neither the name of Qualcomm Technologies Inc nor the names of its contributors may be used to
 * endorse or promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS LICENSE. THIS
 * SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF
 * THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.qualcomm.ftcdriverstation;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.view.InputEvent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.qualcomm.ftccommon.CommandList;
import com.qualcomm.ftccommon.DbgLog;
import com.qualcomm.robotcore.eventloop.EventLoopManager;
import com.qualcomm.robotcore.eventloop.opmode.OpModeManager;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.configuration.Utility;
import com.qualcomm.robotcore.hardware.logitech.LogitechGamepadF310;
import com.qualcomm.robotcore.hardware.microsoft.MicrosoftGamepadXbox360;
import com.qualcomm.robotcore.robocol.Command;
import com.qualcomm.robotcore.robocol.Heartbeat;
import com.qualcomm.robotcore.robocol.PeerDiscoveryManager;
import com.qualcomm.robotcore.robocol.RobocolDatagram;
import com.qualcomm.robotcore.robocol.RobocolDatagramSocket;
import com.qualcomm.robotcore.robocol.Telemetry;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.ImmersiveMode;
import com.qualcomm.robotcore.util.RobotLog;
import com.qualcomm.robotcore.util.RollingAverage;
import com.qualcomm.robotcore.util.Util;
import com.qualcomm.robotcore.wifi.WifiDirectAssistant;
import com.qualcomm.robotcore.wifi.WifiDirectAssistant.WifiDirectAssistantCallback;

import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class FtcDriverStationActivity extends Activity
    implements WifiDirectAssistantCallback, SharedPreferences.OnSharedPreferenceChangeListener, OpModeSelectionDialogFragment.OpModeSelectionDialogListener {

  public static final double ASSUME_DISCONNECT_TIMER = 2.0; // in seconds

  private class SetupRunnable implements Runnable {

    @Override
    public void run() {
      try {
        if (socket != null) socket.close();

        socket = new RobocolDatagramSocket();
        socket.listen(wifiDirect.getGroupOwnerAddress());
        socket.connect(wifiDirect.getGroupOwnerAddress());
      } catch (SocketException e) {
        DbgLog.error("Failed to open socket: " + e.toString());
      }

      if (peerDiscoveryManager != null) { peerDiscoveryManager.stop(); }
      peerDiscoveryManager = new PeerDiscoveryManager(socket);
      peerDiscoveryManager.start(wifiDirect.getGroupOwnerAddress());

      // start the new event loops
      recvLoopService = Executors.newSingleThreadExecutor();
      recvLoopService.execute(new RecvLoopRunnable());
      // send loop will be started after peer discovery
      DbgLog.msg("Setup complete");
    }
  }

  private class SendLoopRunnable implements Runnable {
    private static final long GAMEPAD_UPDATE_THRESHOLD = 1000; // in milliseconds

    @Override
    public void run() {
      try {
        long now = SystemClock.uptimeMillis();

        // skip if we haven't received a packet in a while
        if (lastRecvPacket.time() > ASSUME_DISCONNECT_TIMER) {
          if (clientConnected) assumeClientDisconnect();
          return;
        }

        // send heartbeat
        if (heartbeatSend.getElapsedTime() > 0.1) {
          // generate a new heartbeat packet and send it
          heartbeatSend = new Heartbeat();
          RobocolDatagram packetHeartbeat = new RobocolDatagram(heartbeatSend);
          socket.send(packetHeartbeat);
        }

        // send gamepads
        for (Map.Entry<Integer, Integer> userEntry : userToGamepadMap.entrySet()) {

          int user = userEntry.getKey();
          int id = userEntry.getValue();

          Gamepad gamepad = gamepads.get(id);
          gamepad.user = (byte) user;

          // don't send stale gamepads
          if (now - gamepad.timestamp > GAMEPAD_UPDATE_THRESHOLD && gamepad.atRest()) continue;

          RobocolDatagram packetGamepad = new RobocolDatagram(gamepad);
          socket.send(packetGamepad);
        }

        // send commands
        Iterator<Command> i = pendingCommands.iterator();
        while (i.hasNext()) {
          // using an iterator so we can change the set while looping through all elements
          Command command = i.next();

          // if this command has exceeded max attempts, give up
          if (command.getAttempts() > MAX_COMMAND_ATTEMPTS) {
            String msg = String.format("Giving up on command %s after %d attempts",
                command.getName(), MAX_COMMAND_ATTEMPTS);
            showToast(msg, Toast.LENGTH_SHORT);
            i.remove();
            continue;
          }

          // log commands we initiated
          if (command.isAcknowledged() == false) {
            DbgLog.msg("    sending command: " + command.getName() + ", attempt: " + command.getAttempts());
          }

          // send the command
          RobocolDatagram packetCommand = new RobocolDatagram(command);
          socket.send(packetCommand);

          // if this is a command we handled, remove it
          if (command.isAcknowledged()) pendingCommands.remove(command);
        }
      } catch (RobotCoreException e) {
        e.printStackTrace();
      }
    }
  }

  private class RecvLoopRunnable implements Runnable {
    @Override
    public void run() {
      while (true) {
        RobocolDatagram packet = socket.recv();

        if (packet == null) {
          if (socket.isClosed())
            return;
          Thread.yield();
          continue;
        }
        lastRecvPacket.reset();

        switch (packet.getMsgType()) {
          case PEER_DISCOVERY:
            peerDiscoveryEvent(packet);
            break;
          case HEARTBEAT:
            heartbeatEvent(packet);
            break;
          case COMMAND:
            commandEvent(packet);
            break;
          case TELEMETRY:
            telemetryEvent(packet);
            break;
          default:
            DbgLog.msg("Unhandled message type: " + packet.getMsgType().name());
            break;
        }
      }
    }
  }

  private class OpModeCountDownTimer {
    private static final long MILLISECONDS_PER_SECOND = 1000;
    private static final long COUNTDOWN = 30 * MILLISECONDS_PER_SECOND;
    private static final long TICK      =  1 * MILLISECONDS_PER_SECOND;

    CountDownTimer timer = null;
    boolean running = false;

    public void start() {
      DbgLog.msg("Running current op mode for " + (COUNTDOWN / MILLISECONDS_PER_SECOND) + " seconds");
      running = true;
      runOnUiThread(new Runnable() {
        @Override
        public void run() {
          timer = new CountDownTimer(COUNTDOWN, TICK) {
            @Override
            public void onTick(long timeRemaining) {
              long timeRemainingInSeconds = timeRemaining / MILLISECONDS_PER_SECOND;
              String text = getString(R.string.label_stop) + " (" + timeRemainingInSeconds + ")";
              setTextView(buttonStop, text);
              DbgLog.msg("Running current op mode for " + timeRemainingInSeconds + " seconds");
            }

            @Override
            public void onFinish() {
              running = false;
              DbgLog.msg("Stopping current op mode, timer expired");
              handleOpModeStop();
            }
          }.start();
        }
      });
    }

    public void stop() {
      if (!running) return;

      DbgLog.msg("Stopping current op mode BEFORE timer expired");
      if (timer != null) timer.cancel();
    }
  }

  protected static final int MAX_COMMAND_ATTEMPTS = 10;

  protected static final int MAX_LOG_SIZE = 2 * 1024; // in kilobytes

  protected boolean clientConnected = false;

  protected Map<Integer, Gamepad> gamepads = new HashMap<Integer, Gamepad>();
  protected Map<Integer, Integer> userToGamepadMap = new HashMap<Integer, Integer>();

  protected Heartbeat heartbeatSend = new Heartbeat();
  protected Heartbeat heartbeatRecv = new Heartbeat();

  protected ScheduledExecutorService sendLoopService = Executors.newSingleThreadScheduledExecutor();
  protected ScheduledFuture<?> sendLoopFuture;

  protected ExecutorService recvLoopService;

  protected InetAddress remoteAddr;
  protected WifiDirectAssistant wifiDirect;
  protected RobocolDatagramSocket socket;
  protected String groupOwnerMac;
  protected PeerDiscoveryManager peerDiscoveryManager;


  protected String queuedOpMode = "";
  protected Set<String> opModes = new HashSet<String>();
  protected boolean opModeUseTimer = false;
  protected OpModeCountDownTimer opModeCountDown = new OpModeCountDownTimer();

  protected RollingAverage pingAverage = new RollingAverage(10);
  protected ElapsedTime lastUiUpdate = new ElapsedTime();
  protected ElapsedTime lastRecvPacket = new ElapsedTime();

  protected Set<Command> pendingCommands = Collections.newSetFromMap(new ConcurrentHashMap<Command, Boolean>());

  protected boolean setupNeeded = true;

  protected TextView textDeviceName;
  protected TextView textWifiDirectStatus;
  protected TextView textPingStatus;
  protected TextView textOpModeQueuedLabel;
  protected TextView textOpModeQueuedName;
  protected TextView textOpModeLabel;
  protected TextView textOpModeName;
  protected TextView textTelemetry;
  protected TextView systemTelemetry;
  protected TextView rcBatteryTelemetry;
  protected TextView robotBatteryTelemetry;
  protected ImmersiveMode immersion;

  protected Button buttonStart;
  protected Button buttonStartTimed;
  protected Button buttonSelect;
  protected Button buttonStop;

  protected Context context;
  protected Utility utility;
  protected SharedPreferences preferences;

  protected boolean enableNetworkTrafficLogging = false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_ftc_driver_station);

    context = this;
    utility = new Utility(this);

    textDeviceName = (TextView) findViewById(R.id.textDeviceName);
    textWifiDirectStatus = (TextView) findViewById(R.id.textWifiDirectStatus);
    textPingStatus = (TextView) findViewById(R.id.textPingStatus);

    textOpModeQueuedLabel = (TextView) findViewById(R.id.textOpModeQueueLabel);
    textOpModeQueuedName = (TextView) findViewById(R.id.textOpModeQueueName);
    textOpModeLabel = (TextView) findViewById(R.id.textOpModeLabel);
    textOpModeName = (TextView) findViewById(R.id.textOpModeName);
    textTelemetry = (TextView) findViewById(R.id.textTelemetry);
    systemTelemetry = (TextView) findViewById(R.id.textSystemTelemetry);
    rcBatteryTelemetry = (TextView) findViewById(R.id.rcBatteryTelemetry);
    robotBatteryTelemetry = (TextView) findViewById(R.id.robotBatteryTelemetry);
    immersion = new ImmersiveMode(getWindow().getDecorView());

    buttonStart = (Button) findViewById(R.id.buttonStart);
    buttonStartTimed = (Button) findViewById(R.id.buttonStartTimed);
    buttonSelect = (Button) findViewById(R.id.buttonSelect);
    buttonStop = (Button) findViewById(R.id.buttonStop);

    PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
    preferences = PreferenceManager.getDefaultSharedPreferences(this);
    preferences.registerOnSharedPreferenceChangeListener(this);

    wifiDirect = WifiDirectAssistant.getWifiDirectAssistant(getApplicationContext());
    wifiDirect.setCallback(this);
  }

  @Override
  protected void onStart() {
    super.onStart();

    RobotLog.writeLogcatToDisk(this, MAX_LOG_SIZE);

    wifiDirectStatus("Wifi Direct - Disconnected");

    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
    groupOwnerMac = sharedPref.getString(getString(R.string.pref_driver_station_mac), getString(R.string.pref_driver_station_mac_default));

    wifiDirect.enable();
    if (wifiDirect.isConnected() == false) {
      wifiDirect.discoverPeers();
    } else if (groupOwnerMac.equalsIgnoreCase(wifiDirect.getGroupOwnerMacAddress()) == false) {
      DbgLog.error("Wifi Direct - connected to " + wifiDirect.getGroupOwnerMacAddress() + ", expected " + groupOwnerMac);
      wifiDirectStatus("Wifi Direct - Error: Connected to wrong device");
      startActivity(new Intent(getBaseContext(), ConfigWifiDirectActivity.class));
      return;
    }

    DbgLog.msg("App Started");
  }

  @Override
  protected void onResume() {
    super.onResume();

    setupNeeded = true;
    enableNetworkTrafficLogging = preferences.getBoolean(getString(R.string.pref_log_network_traffic_key), false);
    wifiDirect.setCallback(this);
    if (wifiDirect.isConnected()) {
      // spoof a wifi direct event. Some devices won't send this event out,
      // so to complete our setup, we will spoof it to get all the necessary information.
      RobotLog.i("Spoofing a wifi direct event...");
      onWifiDirectEvent(WifiDirectAssistant.Event.CONNECTION_INFO_AVAILABLE);
    }
  }

  @Override
  protected void onPause() {
    super.onPause();
  }

  @Override
  protected void onStop() {
    super.onStop();

    wifiDirect.disable();

    // close the old event loops
    shutdown();

    DbgLog.msg("App Stopped");

    RobotLog.cancelWriteLogcatToDisk(this);
  }

  @Override
  public void onWindowFocusChanged(boolean hasFocus){
    super.onWindowFocusChanged(hasFocus);
    // When the window loses focus (e.g., the action overflow is shown),
    // cancel any pending hide action. When the window gains focus,
    // hide the system UI.
    if (hasFocus) {
      if (ImmersiveMode.apiOver19()){
        // Immersive flag only works on API 19 and above.
        immersion.hideSystemUI();
      }
    } else {
      immersion.cancelSystemUIHide();
    }
  }

  public void showToast(final String msg, final int duration) {
    showToast(Toast.makeText(context, msg, duration));
  }

  public void showToast(final Toast toast) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        toast.show();
      }
    });
  }

  @Override
  public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    if (key.equals(context.getString(R.string.pref_gamepad_type_key))) {
      gamepads.clear();
      userToGamepadMap.clear();
    } else if (key.equals(context.getString(R.string.pref_log_network_traffic_key))) {
      enableNetworkTrafficLogging = preferences.getBoolean(getString(R.string.pref_log_network_traffic_key), false);
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.ftc_driver_station, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_about:
        startActivity(new Intent(getBaseContext(), AboutActivity.class));
        return true;
      case R.id.action_settings:
        startActivity(new Intent(getBaseContext(), SettingsActivity.class));
        return true;
      case R.id.action_restart_robot:
        pendingCommands.add(new Command(CommandList.CMD_RESTART_ROBOT));
        return true;
      case R.id.action_exit_app:
        finish();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    // don't destroy assets on screen rotation
  }

  @Override
  public boolean dispatchGenericMotionEvent(MotionEvent event) {
    if (Gamepad.isGamepadDevice(event.getDeviceId())) {
      handleGamepadEvent(event);
      return true;
    }

    return super.dispatchGenericMotionEvent(event);
  }

  @Override
  public boolean dispatchKeyEvent(KeyEvent event) {
    if (Gamepad.isGamepadDevice(event.getDeviceId())) {
      handleGamepadEvent(event);
      return true;
    }

    return super.dispatchKeyEvent(event);
  }

  // Wifi Direct Assistant callback
  @Override
  public void onWifiDirectEvent(WifiDirectAssistant.Event event) {
    String msg;
    switch (event) {
      case PEERS_AVAILABLE:
        if (wifiDirect.getConnectStatus() == WifiDirectAssistant.ConnectStatus.CONNECTED ||
            wifiDirect.getConnectStatus() == WifiDirectAssistant.ConnectStatus.CONNECTING) {
          /*
           * We get extra an extra PEER_AVAILABLE event when first connecting, and right after
           * the connection is complete. Just ignore these events.
           */
          return;
        }

        if (groupOwnerMac.equals(getString(R.string.pref_driver_station_mac_default))) {
          wifiDirectStatus("Wifi Direct - Not Paired");
        } else {
          wifiDirectStatus("Wifi Direct - Searching for Robot Controller");
        }

        // look for driver station
        List<WifiP2pDevice> peers = wifiDirect.getPeers();
        for (WifiP2pDevice peer : peers) {
          if (peer.deviceAddress.equalsIgnoreCase(groupOwnerMac)) {
            // driver station found; connect
            wifiDirect.connect(peer);
            break;
          }
        }
        break;
      case CONNECTED_AS_GROUP_OWNER:
        DbgLog.error("Wifi Direct - connected as Group Owner, was expecting Peer");
        wifiDirectStatus("Wifi Direct - Error: Connected as Group Owner");
        startActivity(new Intent(getBaseContext(), ConfigWifiDirectActivity.class));
        break;
      case CONNECTING:
        wifiDirectStatus("Wifi Direct - Connecting");
        wifiDirect.cancelDiscoverPeers();
        break;
      case CONNECTED_AS_PEER:
        wifiDirect.cancelDiscoverPeers();
        wifiDirectStatus("Wifi Direct - Connected");
        break;
      case CONNECTION_INFO_AVAILABLE:
        wifiDirectStatus("Wifi Direct - Connected to " + wifiDirect.getGroupOwnerName());
        displayDeviceName(wifiDirect.getDeviceName());
        if (groupOwnerMac.equalsIgnoreCase(wifiDirect.getGroupOwnerMacAddress()) == false) {
          DbgLog.error("Wifi Direct - connected to " + wifiDirect.getGroupOwnerMacAddress() + ", expected " + groupOwnerMac);
          wifiDirectStatus("Wifi Direct - Error: Connected to wrong device");
          startActivity(new Intent(getBaseContext(), ConfigWifiDirectActivity.class));
          return;
        }

        synchronized (this) {
          if (wifiDirect.isConnected() && setupNeeded) {
            setupNeeded = false;
            (new Thread(new SetupRunnable())).start();
          }
        }
        break;
      case DISCONNECTED:
        msg = "Wifi Direct - Disconnected";
        wifiDirectStatus(msg);
        DbgLog.msg(msg);
        wifiDirect.discoverPeers();
        break;
      case ERROR:
        msg = "Wifi Direct - Error: " + wifiDirect.getFailureReason();
        wifiDirectStatus(msg);
        DbgLog.msg(msg);
        break;
      default:
        break;
    }
  }

  public void onClickButtonStart(View view) {
    handleOpModeStart(false);
  }

  public void onClickButtonStartTimed(View view) {
    handleOpModeStart(true);

  }

  public void onClickButtonSelect(View view) {
    // create an array of op modes
    String[] opModeStringArray = new String[opModes.size()];
    opModes.toArray(opModeStringArray);

    // display dialog to user
    OpModeSelectionDialogFragment opModeSelectionDialogFragment = new OpModeSelectionDialogFragment();
    opModeSelectionDialogFragment.setOnSelectionDialogListener(this);
    opModeSelectionDialogFragment.setOpModes(opModeStringArray);
    opModeSelectionDialogFragment.show(getFragmentManager(), "op_mode_selection");

    // selection will be return to onSelectionClick(...) callback

    // If the connection was dropped, the Start button will say "Resume". We should change it back
    // to "Start" if the user hits the "Select" button.
    setButtonText(buttonStart, "Start");
    setButtonText(buttonStartTimed, "Start w/ Timer");
  }

  public void onClickButtonStop(View view) {
    handleOpModeStop();
  }

  // Callback method, will be called when the user selects an option from the op mode selection dialog
  @Override
  public void onSelectionClick(String selection) {
    handleOpModeQueued(selection);
  }

  protected void shutdown() {
    if (recvLoopService != null) recvLoopService.shutdownNow();
    if (sendLoopFuture != null && !sendLoopFuture.isDone()) sendLoopFuture.cancel(true);

    // stop peer discovery
    if (peerDiscoveryManager != null) peerDiscoveryManager.stop();

    // close the socket as well
    if (socket != null) socket.close();

    // reset the client
    remoteAddr = null;

    // reset need for setup
    setupNeeded = true;

    // reset quick status
    pingStatus("");
  }

  protected void peerDiscoveryEvent(RobocolDatagram packet) {
    if (packet.getAddress().equals(remoteAddr)) return; // no action needed

    // update remoteAddr with latest address
    remoteAddr = packet.getAddress();
    DbgLog.msg("new remote peer discovered: " + remoteAddr.getHostAddress());

    try {
      socket.connect(remoteAddr);
    } catch (SocketException e) {
      DbgLog.error("Unable to connect to peer:" + e.toString());
    }

    // start send loop, if needed
    if (sendLoopFuture == null || sendLoopFuture.isDone()) {
      sendLoopFuture = sendLoopService.scheduleAtFixedRate(new SendLoopRunnable(), 0, 40, TimeUnit.MILLISECONDS);
    }

    assumeClientConnect();
  }

  protected void heartbeatEvent(RobocolDatagram packet) {
    try {
      heartbeatRecv.fromByteArray(packet.getData());
      double elapsedTime = heartbeatRecv.getElapsedTime();
      int sequenceNumber = heartbeatRecv.getSequenceNumber();
      pingAverage.addNumber((int) (elapsedTime * 1000));

      if (enableNetworkTrafficLogging) {
        DbgLog.msg(String.format("Network - num: %5d, time: %7.4f", sequenceNumber, elapsedTime));
      }

      // greater than one second since last UI update?
      if (lastUiUpdate.time() > 0.5) {
        lastUiUpdate.reset();

        pingStatus(String.format("Ping: %3dms", pingAverage.getAverage()));
      }
    } catch (RobotCoreException e) {
      DbgLog.logStacktrace(e);
    }
  }

  protected void commandEvent(RobocolDatagram packet) {
    try {
      Command command = new Command(packet.getData());

      if (command.isAcknowledged()) {
        pendingCommands.remove(command);
        return;
      }

      // we are to handle this command
      DbgLog.msg(" processing command: " + command.getName());
      command.acknowledge();
      pendingCommands.add(command);

      String name = command.getName();
      String extra = command.getExtra();

      if (name.equals(CommandList.CMD_REQUEST_OP_MODE_LIST_RESP)) {
        handleCommandRequestOpModeListResp(extra);
      } else if (name.equals(CommandList.CMD_SWITCH_OP_MODE_RESP)) {
        handleCommandSwitchOpModeResp(extra);
      } else {
        DbgLog.msg("Unable to process command " + name);
      }

    } catch (RobotCoreException e) {
      DbgLog.logStacktrace(e);
    }
  }

  protected void telemetryEvent(RobocolDatagram packet) {
    String telemetryString = "";
    Telemetry telemetry;
    SortedSet<String> keys;

    try {
      telemetry = new Telemetry(packet.getData());
    } catch (RobotCoreException e) {
      DbgLog.logStacktrace(e);
      return;
    }

    Map<String, String> strings = telemetry.getDataStrings();
    keys = new TreeSet<String>(strings.keySet());
    for (String key : keys) {
      telemetryString += strings.get(key) + "\n";
    }
    telemetryString += "\n";

    Map<String, Float> numbers = telemetry.getDataNumbers();
    keys = new TreeSet<String>(numbers.keySet());
    for (String key : keys) {
      telemetryString += key + ": " + numbers.get(key) + "\n";
    }
    String tag = telemetry.getTag();
    if (tag.equals(EventLoopManager.SYSTEM_TELEMETRY)) {
      DbgLog.msg("System Telemetry event: " + telemetryString);
      RobotLog.setGlobalErrorMsg(telemetryString);
      setTextView(systemTelemetry, telemetryString);
    }
    else if (tag.equals(EventLoopManager.RESTART_OPMODE)) {
      String msg = "Dropped connection...can resume op mode: " + telemetryString;
      DbgLog.msg(msg);
      setTextView(systemTelemetry, msg);
      handleRestartOpMode(telemetryString.trim());
    }
    else if (tag.equals(EventLoopManager.RC_BATTERY_LEVEL_KEY)) {
      DbgLog.msg("Battery Telemetry event: " + telemetryString);
      setTextView(rcBatteryTelemetry, telemetryString);
    }
    else if (tag.equals(EventLoopManager.ROBOT_BATTERY_LEVEL_KEY)) {
      DbgLog.msg("Battery Telemetry event: " + telemetryString);
      setTextView(robotBatteryTelemetry, telemetryString);
    }else {
      setTextView(textTelemetry, telemetryString);
    }
  }

  protected void handleRestartOpMode(String opModeName) {

    // if the connection was dropped while the robot was in "stop mode" we can just silently
    // recover, no need to ask the user for input.
    if (opModeName.equals(OpModeManager.DEFAULT_OP_MODE_NAME)) {
      pendingCommands.add(new Command(CommandList.CMD_CANCEL_OP_RESTART, OpModeManager.DEFAULT_OP_MODE_NAME));
      setTextView(systemTelemetry, "");
      return;
    }

    setButtonText(buttonStart, "Resume");
    setButtonText(buttonStartTimed, "Restart w/ new Timer");
    handleOpModeQueued(opModeName);
  }


  protected void assumeClientConnect() {
    DbgLog.msg("Assuming client connected");
    clientConnected = true;

    // request a list of available op modes
    pendingCommands.add(new Command(CommandList.CMD_REQUEST_OP_MODE_LIST));
  }

  protected void assumeClientDisconnect() {
    DbgLog.msg("Assuming client disconnected");
    clientConnected = false;

    opModeUseTimer = false;
    opModeCountDown.stop();
    queuedOpMode = "";
    opModes.clear();

    pingStatus("");
    pendingCommands.clear();
    remoteAddr = null;
    RobotLog.clearGlobalErrorMsg();

    setTextView(textOpModeQueuedName, "");
    setTextView(textOpModeName, "");
    setTextView(buttonStop, getString(R.string.label_stop));
    setTextView(textTelemetry, "");
    setTextView(systemTelemetry, "");
    setTextView(rcBatteryTelemetry, "");
    setTextView(robotBatteryTelemetry, "");

    setEnabled(buttonSelect, false);
    setEnabled(buttonStop, false);
    setEnabled(buttonStart, false);
    setEnabled(buttonStartTimed, false);
  }

  protected void handleOpModeQueued(String queuedOpMode) {
    this.queuedOpMode = queuedOpMode;
    setTextView(textOpModeQueuedName, queuedOpMode);

    setVisibility(textOpModeQueuedLabel, View.VISIBLE);
    setVisibility(textOpModeQueuedName, View.VISIBLE);

    setEnabled(buttonStart, true);
    setEnabled(buttonStartTimed, true);
  }

  protected void handleOpModeStop() {
    opModeUseTimer = false;
    opModeCountDown.stop();
    setTextView(buttonStop, getString(R.string.label_stop));
    pendingCommands.add(new Command(CommandList.CMD_SWITCH_OP_MODE, OpModeManager.DEFAULT_OP_MODE_NAME));
  }

  protected void handleOpModeStart(boolean useTimer) {
    opModeUseTimer = useTimer;
    opModeCountDown.stop();
    setTextView(buttonStop, getString(R.string.label_stop));

    pendingCommands.add(new Command(CommandList.CMD_SWITCH_OP_MODE, queuedOpMode));

    queuedOpMode = "";
    setTextView(textTelemetry, "");
    setTextView(systemTelemetry, "");
    setTextView(textOpModeQueuedName, "");
    setVisibility(textOpModeQueuedLabel, View.INVISIBLE);
    setVisibility(textOpModeQueuedName, View.INVISIBLE);

    setEnabled(buttonStart, false);
    setEnabled(buttonStartTimed, false);

    // If the connection was dropped, the Start button will say "Resume". We should change it back
    // to "Start" if the user hits the "Select" button.
    setButtonText(buttonStart, "Start");
    setButtonText(buttonStartTimed, "Start w/ Timer");
  }

  protected void handleCommandRequestOpModeListResp(String extra) {
    opModes = new HashSet<String>(Arrays.asList(extra.split(Util.ASCII_RECORD_SEPARATOR)));
    DbgLog.msg("Received the following op modes: " + opModes.toString());
    pendingCommands.add(new Command(CommandList.CMD_SWITCH_OP_MODE, OpModeManager.DEFAULT_OP_MODE_NAME));
  }

  protected void handleCommandSwitchOpModeResp(String extra) {
    DbgLog.msg("Robot Controller is running op mode: " + extra);

    setTextView(textOpModeName, extra);
    setEnabled(buttonSelect, true);
    setEnabled(buttonStop, true);

    if (opModeUseTimer) {
      opModeCountDown.start();
    }
  }

  protected void wifiDirectStatus(final String status) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        textWifiDirectStatus.setText(status);
      }
    });
  }

  protected void displayDeviceName(final String name) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        textDeviceName.setText(name);
      }
    });
  }

  protected void setButtonText(final Button button, final String text) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        button.setText(text);
      }
    });
  }

  protected void setTextView(final TextView textView, final String text) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        textView.setText(text);
      }
    });
  }

  protected void setEnabled(final View view, final boolean enabled) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        view.setEnabled(enabled);
      }
    });
  }

  protected void setVisibility(final View view, final int visibility) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        view.setVisibility(visibility);
      }
    });
  }

  protected void pingStatus(final String status) {
    setTextView(textPingStatus, status);
  }

  // needs to be synchronized since multiple gamepad events can come in at the same time
  protected synchronized void handleGamepadEvent(MotionEvent event) {
    Gamepad gamepad = gamepads.get(event.getDeviceId());
    if (gamepad == null) return; // we aren't tracking this gamepad

    gamepad.update(event);
    indicateGamepad(gamepad, event);
  }

  protected void indicateGamepad(Gamepad gamepad, InputEvent event){

    String info = gamepad.toString();
    info = info.substring(16);
    info = info.substring(0, 20) + "\n" + info.substring(20, 40) + "\n" + info.substring(40, 57) + "\n" + info.substring(58);
    info = String.format("Gamepad detected as %s (ID %d)", gamepads.get(event.getDeviceId()).type(), event.getDeviceId()) + "\n" + info;

    for (Map.Entry<Integer, Integer> entry : userToGamepadMap.entrySet()) {
      if (entry.getValue() == event.getDeviceId()){
        if (entry.getKey() == 1){
          TextView user1 = (TextView) findViewById(R.id.user1);
          animateInfo(user1, info, Color.argb(255, 0, 255, 144));
        } if (entry.getKey() == 2){
          TextView user2 = (TextView) findViewById(R.id.user2);
          animateInfo(user2, info, Color.argb(255, 0, 111, 255));
        }
      }
    }
  }

  protected void animateInfo(TextView user, String info, int color){
    user.setText(info);
    ObjectAnimator colorFade = ObjectAnimator.ofObject(user, "backgroundColor", new ArgbEvaluator(), color, Color.argb(255, 255, 255, 255));
    colorFade.setDuration(2000);
    colorFade.start();
  }

  // needs to be synchronized since multiple gamepad events can come in at the same time
  protected synchronized void handleGamepadEvent(KeyEvent event) {
    if (gamepads.containsKey(event.getDeviceId()) == false) {
      gamepads.put(event.getDeviceId(), new Gamepad());
    }

    Gamepad gamepad = gamepads.get(event.getDeviceId());

    gamepad.update(event);
    indicateGamepad(gamepad, event);

    if (gamepad.start && (gamepad.a || gamepad.b)) {
      int user = -1;

      if (gamepad.a) {
        user = 1;
      }

      if (gamepad.b) {
        user = 2;
      }
      assignNewGamepad(user, event.getDeviceId());
    }
  }

  protected void initGamepad(int user, int gamepadId) {
    String key = "";

    switch (user) {
      case 1: key = getString(R.string.pref_gamepad_type_key); break;
      case 2: key = getString(R.string.pref_gamepad_type_key); break;
    }

    String gamepadType = preferences.getString(key, getString(R.string.gamepad_default));

    Gamepad gamepad;

    if (gamepadType.equals(getString(R.string.gamepad_logitech_f310))) {
      gamepad = new LogitechGamepadF310();
    } else if (gamepadType.equals(getString(R.string.gamepad_microsoft_xbox_360))) {
      gamepad = new MicrosoftGamepadXbox360();
    } else {
      gamepad = new Gamepad();
    }

    gamepad.id = gamepadId;
    gamepad.timestamp = SystemClock.uptimeMillis();

    gamepads.put(gamepadId, gamepad);
  }

  protected void assignNewGamepad(int user, int gamepadId) {

    // search for duplicates and remove
    Set<Integer> duplicates = new HashSet<Integer>();
    for (Map.Entry<Integer, Integer> entry : userToGamepadMap.entrySet()) {
      if (entry.getValue() == gamepadId) duplicates.add(entry.getKey());
    }
    for (Integer i : duplicates) userToGamepadMap.remove(i);

    // add user to mapping and init gamepad
    userToGamepadMap.put(user, gamepadId);
    initGamepad(user, gamepadId);

    String msg = String.format("Gamepad %d detected as %s (ID %d)", user, gamepads.get(gamepadId).type(), gamepadId);
    DbgLog.msg(msg);
  }
}
