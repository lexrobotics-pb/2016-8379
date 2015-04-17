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

package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.Servo;

public class KickStand extends OpMode {

    DcMotor motorFrontRight;
    DcMotor motorBackRight;
    DcMotor motorThrower;
    DcMotor motorLift;
    DcMotor motorBackLeft;
    DcMotor motorFrontLeft;

    Servo grabber;
    Servo hood;
    Servo arm;
    Servo holder;
    Servo trigger;

	/**
	 * Constructor
	 */
	public KickStand() {

	}


	@Override
	public void start() {
        motorFrontRight = hardwareMap.dcMotor.get("motorFrontRight");
        motorBackRight = hardwareMap.dcMotor.get("motorBackRight");
        motorBackRight.setDirection(DcMotor.Direction.REVERSE); //reverses back right motor
        motorFrontLeft = hardwareMap.dcMotor.get("motorFrontLeft");
        motorFrontLeft.setDirection(DcMotor.Direction.REVERSE); //reverse front left motor
        motorBackLeft = hardwareMap.dcMotor.get("motorBackLeft");
	}

	@Override
	public void run() {
        if (this.time <= 5) {
            mecJustMove(78, 90, 0);
            armOut();
        } else if (this.time > 5 && this.time <= 9) {
            mecJustMove(78, 0, 0);
            armIn();
        } else if (this.time > 9 && this.time <= 12) {
            mecJustMove(78, 0, 0);
            armIn();
        } else if (this.time > 12 && this.time <= 18) {
            mecJustMove(78, -90, 0);
        } else if (this.time >18 && this.time<=18.5){
            mecJustMove(78,90,0);
        } else if(this.time > 18.5 && this.time <=21.5)
        {
            mecJustMove(78, 0, 0);
        }

	}

	@Override
	public void stop() {

	}

    public void mecJustMove(double speed, double degrees, double speedRotation)
    {
        speed/=100.0;
        speedRotation/=100.0;
        double radians = toRadians(degrees);
        motorFrontLeft.setPower(speed * Math.sin(radians + Math.PI/4) + speedRotation);
        motorFrontRight.setPower(speed * Math.cos(radians + Math.PI/4) - speedRotation);
        motorBackLeft.setPower(speed * Math.cos(radians + Math.PI/4) + speedRotation);
        motorBackRight.setPower(speed * Math.sin(radians + Math.PI/4) -  speedRotation);
    }

    private double toRadians (double degrees)
    {
        return degrees/180.0*Math.PI;
    }
    public void armOut(){
        double start = this.time;
        arm.setPosition(0.75);
        while(this.time-start<2)
        {
        }
        arm.setPosition(0.51);
    }

    public void armIn(){
        double start = this.time;
        arm.setPosition(-0.75);
        while(this.time-start<2)
        {
        }
        arm.setPosition(0.51);
    }

    public void switchAllToRead(){
        motorFrontLeft.setDeviceMode(DcMotorController.DeviceMode.READ_ONLY);
        motorFrontRight.setDeviceMode(DcMotorController.DeviceMode.READ_ONLY);
        motorBackLeft.setDeviceMode(DcMotorController.DeviceMode.READ_ONLY);
        motorBackRight.setDeviceMode(DcMotorController.DeviceMode.READ_ONLY);
    }

    public void switchAllToWrite(){
        motorFrontLeft.setDeviceMode(DcMotorController.DeviceMode.WRITE_ONLY);
        motorFrontRight.setDeviceMode(DcMotorController.DeviceMode.WRITE_ONLY);
        motorBackLeft.setDeviceMode(DcMotorController.DeviceMode.WRITE_ONLY);
        motorBackRight.setDeviceMode(DcMotorController.DeviceMode.WRITE_ONLY);
    }
}
