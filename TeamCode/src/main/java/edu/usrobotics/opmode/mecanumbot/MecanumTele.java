package edu.usrobotics.opmode.mecanumbot;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import java.util.Arrays;

import edu.usrobotics.opmode.StateBasedOp;

/**
 * Created by dsiegler19 on 10/13/16.
 */
@TeleOp(name="Mecanum TeleOp", group="MecanumBot")
public class MecanumTele extends StateBasedOp {

    MecanumBotHardware robot = new MecanumBotHardware();

    private int bottomBlockLiftPos = 100;
    private int topBlockLiftPos = 750;

    private final int numHeights = 4;

    private boolean liftMotorBusy = false;
    private int targetLiftPos = 0;

    private final float[] HEIGHTS = {0f, 0.2f, 0.4f, 0.6f, 0.8f, 1f}; // top=0, bottom=640
    // negative power makes it go up

    float liftPower = 0f;

    private int[] encoderHeights;

    @Override
    public void init () {

        super.init();

        robot.init(hardwareMap);

        encoderHeights = new int[numHeights];

        calculateEncoderHeights();

    }

    @Override
    public void loop(){

        float frInputs = 0;
        float flInputs = 0;
        float brInputs = 0;
        float blInputs = 0;

        float creepyness = gamepad1.left_trigger;
        float multiplier = 1f - (creepyness * 0.66f);

        if(!liftMotorBusy){

            liftPower = gamepad2.right_stick_y * 0.4f;

            if(!robot.topLimitSwitch.getState()){

                liftPower = Math.max(liftPower, 0f);
                topBlockLiftPos = robot.blockLift.getCurrentPosition();
                calculateEncoderHeights();

            }

            if(!robot.bottomLimitSwitch.getState()){

                liftPower = Math.min(liftPower, 0f);
                bottomBlockLiftPos = robot.blockLift.getCurrentPosition();
                calculateEncoderHeights();

            }

            int currentPos = robot.blockLift.getCurrentPosition();
            int currentBestHeight = encoderHeights[0];
            boolean closeToCheckpoint = false;

            if(gamepad2.dpad_up){

                for(int canidateHeight : encoderHeights){

                    // I SWITCHED THE < TO THE > (CURRENT VERSION)

                    if(canidateHeight > currentPos && Math.abs(canidateHeight - currentPos) < Math.abs(currentBestHeight - currentPos)){

                        currentBestHeight = canidateHeight;

                    }

                }

                liftMotorBusy = true;
                targetLiftPos = currentBestHeight;
                liftPower = -0.2f;

            }

            if(gamepad2.dpad_down){

                for(int canidateHeight : encoderHeights){

                    if(canidateHeight < currentPos && Math.abs(canidateHeight - currentPos) > Math.abs(currentBestHeight - currentPos)){

                        currentBestHeight = canidateHeight;

                    }

                }

                liftMotorBusy = true;
                targetLiftPos = currentBestHeight;
                liftPower = 0.2f;

            }

            robot.blockLift.setPower(liftPower);

        }

        else {

            telemetry.addData("Target pos", targetLiftPos);

            if(liftPower < 0){

                // it is going up

            }

            if((Math.abs(robot.blockLift.getCurrentPosition() - targetLiftPos) < 5) || !robot.topLimitSwitch.getState() || !robot.bottomLimitSwitch.getState()){

                liftMotorBusy = false;

            }

        }

        if(gamepad2.b){

            robot.openGripper();

        }

        if(gamepad2.x){

            robot.closeGripper();

        }

        // Skid steering
        frInputs -= gamepad1.left_stick_x;
        brInputs -= gamepad1.left_stick_x;
        flInputs += gamepad1.left_stick_x;
        blInputs += gamepad1.left_stick_x;

        // Forward and backwards
        frInputs -= gamepad1.right_stick_y;
        flInputs -= gamepad1.right_stick_y;
        brInputs -= gamepad1.right_stick_y;
        blInputs -= gamepad1.right_stick_y;

        // Strafing
        frInputs -= gamepad1.right_stick_x;
        flInputs += gamepad1.right_stick_x;
        brInputs += gamepad1.right_stick_x;
        blInputs -= gamepad1.right_stick_x;

        frInputs *= multiplier;
        flInputs *= multiplier;
        brInputs *= multiplier;
        blInputs *= multiplier;

        DcMotorSimple.Direction frDirection = (frInputs >= 0 ?
                (robot.frCorrectDirection ? DcMotorSimple.Direction.FORWARD : DcMotorSimple.Direction.REVERSE) :
                (robot.frCorrectDirection ? DcMotorSimple.Direction.REVERSE : DcMotorSimple.Direction.FORWARD));
        DcMotorSimple.Direction flDirection = (flInputs >= 0 ?
                (robot.flCorrectDirection ? DcMotorSimple.Direction.FORWARD : DcMotorSimple.Direction.REVERSE) :
                (robot.flCorrectDirection ? DcMotorSimple.Direction.REVERSE : DcMotorSimple.Direction.FORWARD));
        DcMotorSimple.Direction brDirection = (brInputs >= 0 ?
                (robot.brCorrectDirection ? DcMotorSimple.Direction.FORWARD : DcMotorSimple.Direction.REVERSE) :
                (robot.brCorrectDirection ? DcMotorSimple.Direction.REVERSE : DcMotorSimple.Direction.FORWARD));
        DcMotorSimple.Direction blDirection = (blInputs >= 0 ?
                (robot.blCorrectDirection ? DcMotorSimple.Direction.FORWARD : DcMotorSimple.Direction.REVERSE) :
                (robot.blCorrectDirection ? DcMotorSimple.Direction.REVERSE : DcMotorSimple.Direction.FORWARD));

        robot.frontRight.setDirection(frDirection);
        robot.frontLeft.setDirection(flDirection);
        robot.backRight.setDirection(brDirection);
        robot.backLeft.setDirection(blDirection);

        float frPower = Math.min(Math.abs(frInputs), 1);
        float flPower = Math.min(Math.abs(flInputs), 1);
        float brPower = Math.min(Math.abs(brInputs), 1);
        float blPower = Math.min(Math.abs(blInputs), 1);

        robot.frontRight.setPower(frPower);
        robot.frontLeft.setPower(flPower);
        robot.backRight.setPower(brPower);
        robot.backLeft.setPower(blPower);

        robot.backLeft.getCurrentPosition();

        telemetry.addData("Block Lifter Pos", robot.blockLift.getCurrentPosition());
        telemetry.addData("Top height", topBlockLiftPos);
        telemetry.addData("Bottom height", bottomBlockLiftPos);
        telemetry.addData("Encoder heights", Arrays.toString(encoderHeights));

        telemetry.addData("Top limit switch pressed", !robot.topLimitSwitch.getState());
        telemetry.addData("Bottom limit switch pressed", !robot.bottomLimitSwitch.getState());

        telemetry.addData("Right gripper pos", robot.gripperRight.getPosition());
        telemetry.addData("Left gripper pos", robot.gripperLeft.getPosition());

        telemetry.addData("GP1 Right Stick X", gamepad1.right_stick_x);
        telemetry.addData("GP1 Right Stick Y", gamepad1.right_stick_y);
        telemetry.addData("GP1 Left Stick X", gamepad1.left_stick_x);

        telemetry.addData("frInputs", frInputs);
        telemetry.addData("flInputs", flInputs);
        telemetry.addData("brInputs", brInputs);
        telemetry.addData("blInputs", blInputs);

        telemetry.update();

    }

    private void calculateEncoderHeights(){

        int range = topBlockLiftPos - bottomBlockLiftPos;

        for(int i = 0; i < numHeights; i++){

            float height = HEIGHTS[i];
            int encoderHeight = bottomBlockLiftPos + (int) ((float) range * height);
            encoderHeights[i] = encoderHeight;

        }

    }

}
