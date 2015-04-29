/* Copyright (c) 2015 Qualcomm Technologies Inc

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

package com.qualcomm.ftccommon;

import com.qualcomm.robotcore.eventloop.EventLoopManager;
import com.qualcomm.robotcore.eventloop.opmode.OpModeManager;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareFactory;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.LegacyModule;
import com.qualcomm.robotcore.hardware.ServoController;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.qualcomm.robotcore.robocol.Command;
import com.qualcomm.robotcore.robocol.Telemetry;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

/**
 * Created by mollyn on 4/3/15.
 */
public class FtcEventLoopHandler {

  // Event loop manager
  EventLoopManager eventLoopManager;

  // Telemetry
  ElapsedTime telemetryTimer = new ElapsedTime();
  double telemetryInterval = 0.250; // in seconds

  UpdateUI.Callback callback;

  // Hardware Factory and Map
  HardwareFactory hardwareFactory;
  HardwareMap hardwareMap = new HardwareMap();

  public FtcEventLoopHandler(HardwareFactory hardwareFactory, UpdateUI.Callback callback) {
    this.hardwareFactory = hardwareFactory;
    this.callback = callback;
  }

  public void init(EventLoopManager eventLoopManager) {
    this.eventLoopManager = eventLoopManager;
  }

  public HardwareMap getHardwareMap() throws RobotCoreException, InterruptedException {
    hardwareMap = hardwareFactory.createHardwareMap(eventLoopManager);
    return hardwareMap;
  }

  public void displayGamePadInfo(String activeOpModeName) {
    // Get access to gamepad 1 and 2
    Gamepad gamepads[] = eventLoopManager.getGamepads();

    callback.updateUi(activeOpModeName, gamepads);
  }

  public Gamepad[] getGamepads() {
    return eventLoopManager.getGamepads();
  }

  public void sendTelemetryData(Telemetry telemetry) {
    if (telemetryTimer.time() > telemetryInterval) {
      telemetryTimer.reset();

      getRobotBatteryInfo();

      if (telemetry.hasData()) eventLoopManager.sendTelemetryData(telemetry);
      telemetry.clearData();
    }
  }

  private void getRobotBatteryInfo() {
    double minBatteryLevel = Double.MAX_VALUE;

    for (VoltageSensor sensor : hardwareMap.voltageSensor) {
      if (sensor.getVoltage() < minBatteryLevel){
        minBatteryLevel = sensor.getVoltage();
      }
    }

    String msg;
    if (hardwareMap.voltageSensor.size() == 0) {
      msg = "Robot Battery Level: " + "unknown";
    } else {
      BigDecimal rounded = new BigDecimal(minBatteryLevel).setScale(2, RoundingMode.HALF_UP);
      msg = "Robot Battery Level: " + rounded.doubleValue();
    }
    sendTelemetry(EventLoopManager.ROBOT_BATTERY_LEVEL_KEY, msg);
  }

  public void sendTelemetry(String tag, String msg) {
    Telemetry telemetry = new Telemetry();
    telemetry.setTag(tag);
    telemetry.addData(tag, msg);
    if (eventLoopManager != null) {
      eventLoopManager.sendTelemetryData(telemetry);
    }
  }

  public void shutdownMotorControllers() {
    for (Map.Entry<String, DcMotorController> mc : hardwareMap.dcMotorController.entrySet()) {
      String name = mc.getKey();
      DcMotorController controller = mc.getValue();
      DbgLog.msg("Stopping DC Motor Controller " + name);
      controller.close();
    }
  }

  public void shutdownServoControllers()  {
    for (Map.Entry<String, ServoController> sc : hardwareMap.servoController.entrySet()) {
      String name = sc.getKey();
      ServoController controller = sc.getValue();
      DbgLog.msg("Stopping Servo Controller " + name);
      controller.close();
    }
  }

  public void shutdownLegacyModules() {
    for (Map.Entry<String, LegacyModule> lm : hardwareMap.legacyModule.entrySet()) {
      String name = lm.getKey();
      LegacyModule module = lm.getValue();
      DbgLog.msg("Stopping Legacy Module" + name);
      module.close();
    }
  }

  public boolean droppedConnection() {
    return eventLoopManager.state == EventLoopManager.State.DROPPED_CONNECTION;
  }

  public void restartOpMode(String extra) {
    eventLoopManager.restartOpMode(extra);
  }

  public void restartRobot() {
    callback.restartRobot();
  }

  public void sendCommand(Command command) {
    eventLoopManager.sendCommand(command);
  }

  public String getOpMode(String extra) {
    if (eventLoopManager.state != EventLoopManager.State.RUNNING) {
      return OpModeManager.DEFAULT_OP_MODE_NAME;
    }
    return extra;
  }

  public void handleResumeOpMode(String newOpMode) {
    if (eventLoopManager.isWaitingForRestart()){
      eventLoopManager.restartOpMode(newOpMode);
    }
  }

}
