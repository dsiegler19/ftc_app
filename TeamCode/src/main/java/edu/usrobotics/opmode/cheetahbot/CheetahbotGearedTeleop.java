/*
Copyright (c) 2016 Robert Atkinson

All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted (subject to the limitations in the disclaimer below) provided that
the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list
of conditions and the following disclaimer.

Redistributions in binary form must reproduce the above copyright notice, this
list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

Neither the name of Robert Atkinson nor the names of his contributors may be used to
endorse or promote products derived from this software without specific prior
written permission.

NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package edu.usrobotics.opmode.cheetahbot;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@Disabled
@TeleOp(name="Cheetahbot Geared Teleop", group="Cheetabot")
public class CheetahbotGearedTeleop extends OpMode {

    private DcMotor leftMotor;
    private DcMotor rightMotor;

    private int gear;

    private boolean rightMotorReversed = false;
    private boolean leftMotorReversed = true;

    private long lastGearChange = -1L;

    @Override
    public void init() {

        gear = 1;

        telemetry.addData("Status", "Initialized");

        leftMotor = hardwareMap.dcMotor.get("left_motor");
        rightMotor = hardwareMap.dcMotor.get("right_motor");

        leftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        leftMotor.setDirection(rightMotorReversed ? DcMotorSimple.Direction.REVERSE : DcMotor.Direction.FORWARD);
        rightMotor.setDirection(leftMotorReversed ? DcMotorSimple.Direction.REVERSE : DcMotor.Direction.FORWARD);

    }

    @Override
    public void loop() {

        if(gamepad1.right_bumper && System.currentTimeMillis() - lastGearChange >= 1000L && gear < 4){

            gear++;
            lastGearChange = System.currentTimeMillis();

        }

        else if(gamepad1.left_bumper && System.currentTimeMillis() - lastGearChange >= 1000L && gear > -1){

            gear--;
            lastGearChange = System.currentTimeMillis();
            System.currentTimeMillis();

        }

        float throttle = gamepad1.right_trigger;
        float turning = gamepad1.right_stick_x;

        float leftMotorPower = 0f;
        float rightMotorPower = 0f;

        switch (gear){

            case -1:
                leftMotorPower = -0.33f + (0.33f * turning);
                rightMotorPower = -0.33f - (0.33f * turning);
                break;

            case 1:
                leftMotorPower = 0.33f + (0.33f * turning);
                rightMotorPower = 0.33f - (0.33f * turning);
                break;

            case 2:
                leftMotorPower = 0.66f + (0.66f * turning);
                rightMotorPower = 0.66f - (0.66f * turning);
                break;

            case 3:
                leftMotorPower = 1f + turning;
                rightMotorPower = 1f - turning;
                break;

        }

        leftMotor.setPower(leftMotorPower);
        rightMotor.setPower(rightMotorPower);

        telemetry.addData("Left stick y", gamepad1.left_stick_y);
        telemetry.addData("Right stick y", gamepad1.right_stick_y);
        telemetry.addData("Left motor power", leftMotor.getPower());
        telemetry.addData("Right motor power", rightMotor.getPower());

    }

}
