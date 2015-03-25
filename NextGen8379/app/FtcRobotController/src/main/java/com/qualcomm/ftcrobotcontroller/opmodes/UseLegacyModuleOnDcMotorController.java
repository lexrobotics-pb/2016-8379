package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.hitechnic.HiTechnicNxtDcMotorController;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.LegacyModule;
import com.qualcomm.robotcore.util.TypeConversion;

/**
 * Use the legacy module to control the NXT DC Motor Controller. We will read the encoder values
 * from the DC Motor Controller in addition to running the motors.
 *
 * See http://www.hitechnic.com/blog/wp-content/uploads/HiTechnic-Motor-Controller-Specification.pdf
 * for documentation on how to operate the HiTechnic DC Motor Controller
 *
 * Using this method we should get an encoder value about every 100ms
 */
public class UseLegacyModuleOnDcMotorController extends OpMode {

  enum State {SWITCH_TO_READ_MODE, SWITCH_TO_WRITE_MODE}

  State state = State.SWITCH_TO_READ_MODE;

  LegacyModule legacyModule;

  int encoderValue = 0;

  int port = 2;
  int i2cAddr = 2;

  @Override
  public void start() {
    legacyModule = hardwareMap.legacyModule.get("Legacy Module");
    legacyModule.enableNxtI2ctWriteMode(port, i2cAddr, 0x46, new byte[]{0});
  }

  @Override
  public void run() {

    // wait until the port is ready
    if (legacyModule.isPortReady(port)) {

      switch (state) {
        case SWITCH_TO_READ_MODE:
          // reconfigure the port to read
          legacyModule.enableNxtI2cReadMode(port, i2cAddr, 0x50, 4);
          state = State.SWITCH_TO_WRITE_MODE;
          break;
        case SWITCH_TO_WRITE_MODE:
          // read the value that is currently in the cache before we reconfigure
          encoderValue = TypeConversion.byteArrayToInt(legacyModule.readI2c(port));

          // reconfigure the port to write
          legacyModule.enableNxtI2ctWriteMode(port, i2cAddr, 0x46, new byte[]{15});
          state = State.SWITCH_TO_READ_MODE;
          break;
      }
    }

    telemetry.addData("encoder", encoderValue);
  }

  @Override
  public void stop() {
    // reset the legacy module to the expected values
    legacyModule.enableNxtI2ctWriteMode(port, i2cAddr, HiTechnicNxtDcMotorController.MEM_START_ADDRESS, HiTechnicNxtDcMotorController.INIT_VALUES);
  }
}
