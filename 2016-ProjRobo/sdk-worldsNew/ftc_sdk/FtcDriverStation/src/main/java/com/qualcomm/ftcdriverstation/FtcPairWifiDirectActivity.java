/* Copyright (c) 2014 Qualcomm Technologies Inc

All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted (subject to the limitations in the disclaimer below) provided that
the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list
of conditions and the following disclaimer.

Redistributions in binary form must reproduce the above copyright notice, this
list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

Neither the name of Qualcomm Technologies Inc nor the names of its contributors
may be used to endorse or promote products derived from this software without
specific prior written permission.

NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */

package com.qualcomm.ftcdriverstation;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.qualcomm.ftccommon.DbgLog;
import com.qualcomm.robotcore.wifi.WifiDirectAssistant;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class FtcPairWifiDirectActivity extends Activity implements OnClickListener, WifiDirectAssistant.WifiDirectAssistantCallback {

  private static final int WIFI_SCAN_RATE = 10 * 1000; // in milliseconds

  private WifiDirectAssistant wifiDirect;
  private SharedPreferences sharedPref;
  private String driverStationMac;
  private Handler wifiDirectHandler = new Handler();
  private WifiDirectRunnable wifiDirectRunnable = new WifiDirectRunnable();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_ftc_pair_wifi_direct);

    /*
     * Do not enable or disable WifiDirectAssistant in this app, FtcRobotController will manage
     * the Wifi Direct Assistant
     */
    wifiDirect = WifiDirectAssistant.getWifiDirectAssistant(this);
  }

  @Override
  public void onStart() {
    super.onStart();

    DbgLog.msg("Starting Pairing with Driver Station activity");

    sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
    driverStationMac = sharedPref.getString(getString(R.string.pref_driver_station_mac), getString(R.string.pref_driver_station_mac_default));

    wifiDirect.enable();
    wifiDirect.setCallback(this);
    updateDevicesList(wifiDirect.getPeers());

    wifiDirectHandler.postDelayed(wifiDirectRunnable, WIFI_SCAN_RATE);
  }

  @Override
  public void onStop() {
    super.onStop();
    wifiDirectHandler.removeCallbacks(wifiDirectRunnable);
    wifiDirect.cancelDiscoverPeers();
    wifiDirect.disable();
  }

  @Override
  public void onClick(View view) {

    if (view instanceof PeerRadioButton) {
      PeerRadioButton button = ((PeerRadioButton) view);

      if (button.getId() == 0) {
        driverStationMac = getString(R.string.pref_driver_station_mac_default);
      } else {
        driverStationMac = button.getPeerMacAddress();
      }

      SharedPreferences.Editor editor = sharedPref.edit();
      editor.putString(getString(R.string.pref_driver_station_mac), driverStationMac);
      editor.commit();

      DbgLog.msg("Setting Driver Station MAC address to " + driverStationMac);
    }
  }

  // Wifi Direct Assistant callback
  @Override
  public void onWifiDirectEvent(WifiDirectAssistant.Event event) {
    switch (event) {
      case PEERS_AVAILABLE:
        updateDevicesList(wifiDirect.getPeers());
        break;
    }
  }

  private void updateDevicesList(List<WifiP2pDevice> peers) {
    RadioGroup rg = ((RadioGroup) findViewById(R.id.radioGroupDevices));
    rg.clearCheck();
    rg.removeAllViews();

    // add none option
    PeerRadioButton b = new PeerRadioButton(this);
    final String none = getString(R.string.pref_driver_station_mac_default);
    b.setId(0);
    b.setText("None\nDo not pair with any device");
    b.setPadding(0 , 0, 0, 24);
    b.setOnClickListener(this);
    b.setPeerMacAddress(none);
    if (driverStationMac.equalsIgnoreCase(none)) {
      b.setChecked(true);
    }
    rg.addView(b);

    // add devices, sorted by SSID
    int i = 1;
    Map<String, String> namesAndAddresses = buildMap(peers);
    Set<String> nameSet = namesAndAddresses.keySet(); // keys in ascending order.

    for (String name : nameSet) {
      String deviceAddress = namesAndAddresses.get(name);

      b = new PeerRadioButton(this);
      b.setId(i++);
      b.setText(name + "\n" + deviceAddress);
      b.setPadding(0, 0, 0, 24);
      b.setPeerMacAddress(deviceAddress);

      if (deviceAddress.equalsIgnoreCase(driverStationMac)) {
        b.setChecked(true);
      }

      b.setOnClickListener(this);

      rg.addView(b);
    }
  }

  public Map<String, String> buildMap(List<WifiP2pDevice> peers){
    Map<String, String> map = new TreeMap<String, String>(); // automatically sorts entries by key.
    for (WifiP2pDevice peer : peers){
      map.put(peer.deviceName, peer.deviceAddress);
    }
    return map;
  }

  public class WifiDirectRunnable implements Runnable {
    @Override
    public void run() {
      wifiDirect.discoverPeers();
      wifiDirectHandler.postDelayed(wifiDirectRunnable, WIFI_SCAN_RATE);
    }
  }

  public static class PeerRadioButton extends RadioButton {

    private String peerMacAddress = "";

    public PeerRadioButton(Context context) {
      super(context);
    }

    public String getPeerMacAddress() {
      return peerMacAddress;
    }

    public void setPeerMacAddress(String peerMacAddress) {
      this.peerMacAddress = peerMacAddress;
    }
  }
}
