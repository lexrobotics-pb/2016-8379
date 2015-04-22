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

import android.app.Activity;
import android.widget.TextView;
import android.widget.Toast;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.Dimmer;
import com.qualcomm.robotcore.util.RobotLog;
import com.qualcomm.robotcore.wifi.WifiDirectAssistant;

public class UpdateUI {
  /**
   * Callback methods
   */
  public class Callback {

    /**
     * callback method to restart the robot
     */
    public void restartRobot() {
      activity.runOnUiThread(new Runnable() {
        @Override
        public void run() {
          Toast.makeText(activity, "Restarting Robot", Toast.LENGTH_SHORT).show();
        }
      });

      // this call might be coming from the event loop, so we need to start
      // switch contexts before proceeding
      Thread t = new Thread() {
        @Override
        public void run() {
          try { Thread.sleep(1500); } catch (InterruptedException ignored) { }
          activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
              requestRobotRestart();
            }
          });

        }
      };
      t.start();
    }

    public void updateUi(final String opModeName, final Gamepad[] gamepads) {
      activity.runOnUiThread(new Runnable() {
        @Override
        public void run() {
          for (int i = 0; (i < textGamepad.length) && (i < gamepads.length); i++) {
            if (gamepads[i].id == Gamepad.ID_UNASSOCIATED) {
              textGamepad[i].setText(" "); // for some reason "" isn't working, android won't redraw the UI element
            } else {
              textGamepad[i].setText(gamepads[i].toString());
            }
          }

          textOpMode.setText("Op Mode: " + opModeName);

          // if there are no global error messages, getGlobalErrorMsg will return an empty string
          textErrorMessage.setText(RobotLog.getGlobalErrorMsg());
        }
      });
    }

    public void wifiDirectUpdate(final WifiDirectAssistant.Event event) {
      final String status = "Wifi Direct - ";

      switch (event) {
        case DISCONNECTED:
          updateWifiDirectStatus(status + "disconnected");
          break;
        case CONNECTED_AS_GROUP_OWNER:
          updateWifiDirectStatus(status + "enabled");
          break;
        case ERROR:
          updateWifiDirectStatus(status + "ERROR");
          break;
        case CONNECTION_INFO_AVAILABLE:
          WifiDirectAssistant wifiDirectAssistant = controllerService.getWifiDirectAssistant();
          displayDeviceName(wifiDirectAssistant.getDeviceName());
        default:
          break;
      }
    }

    public void robotUpdate(final String status) {
      DbgLog.msg(status);
      activity.runOnUiThread(new Runnable() {
        @Override
        public void run() {
          textRobotStatus.setText(status);


          // if there are no global error messages, getGlobalErrorMsg will return an empty string
          textErrorMessage.setText(RobotLog.getGlobalErrorMsg());
          if (RobotLog.hasGlobalErrorMsg()) {
            dimmer.longBright();
          }
        }
      });
    }

  }

  private static final int NUM_GAMEPADS = 2;

  protected TextView textDeviceName;
  protected TextView textWifiDirectStatus;
  protected TextView textRobotStatus;
  protected TextView[] textGamepad = new TextView[NUM_GAMEPADS];
  protected TextView textOpMode;
  protected TextView textErrorMessage;

  Restarter restarter;
  FtcRobotControllerService controllerService;

  Activity activity;
  Dimmer dimmer;

  public UpdateUI(Activity activity, Dimmer dimmer) {
    this.activity = activity;
    this.dimmer = dimmer;
  }

  public void setTextViews(TextView textWifiDirectStatus, TextView textRobotStatus,
               TextView[] textGamepad, TextView textOpMode, TextView textErrorMessage,
               TextView textDeviceName) {

    this.textWifiDirectStatus = textWifiDirectStatus;
    this.textRobotStatus = textRobotStatus;
    this.textGamepad = textGamepad;
    this.textOpMode = textOpMode;
    this.textErrorMessage = textErrorMessage;
    this.textDeviceName = textDeviceName;

  }

  public void setControllerService(FtcRobotControllerService controllerService) {
    this.controllerService = controllerService;
  }

  public void setRestarter(Restarter restarter) {
    this.restarter = restarter;
  }

  private void updateWifiDirectStatus(String status) {
    DbgLog.msg(status);
    final String finalStatus = status;
    activity.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        textWifiDirectStatus.setText(finalStatus);
      }
    });
  }

  private void displayDeviceName(final String name) {
    activity.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        textDeviceName.setText(name);
      }
    });
  }

  private void requestRobotRestart() {
    restarter.requestRestart();
  }

}
