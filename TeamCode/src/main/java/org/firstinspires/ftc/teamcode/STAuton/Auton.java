package org.firstinspires.ftc.teamcode.STAuton;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.teamcode.robotParts_new.All_Parts;

@Autonomous(name = "Autonomous", group = "Autonomous")
//Naam van project
public class Auton extends LinearOpMode {
    private ElapsedTime runtime = new ElapsedTime();

    //Slaat op hoe lang de robot is geinitialiseerd

    double pos_y = 0;
    double pos_x = 0;

    boolean autoEnabled = true;
    boolean manual = false;
    boolean hasInit = false;

    double vy = 0; //velocity x
    double vx = 0; //velocity y
    double va = 0; //angular velocity

    double fl = 0; //front left motor
    double fr = 0; //front right motor
    double bl = 0; //back left motor
    double br = 0; //back right motor
    String stage = "init complete";
    double slides;
    double slidesPos = 0;
    double arm;
    double sampleStorage = 0.95 ;
    double intakeClaw = 0;
    double intakeOrientation = 0;
    //double pos_x = parts.motorPos(hardwareMap)[1]; //x position
    //double pos_y = parts.motorPos(hardwareMap)[3]; // y position
    BNO055IMU imu;
    Orientation angles;

    @Override
    public void runOpMode() throws InterruptedException {
        //All_Parts parts = null;
        All_Parts parts = new All_Parts();

        parts.init(hardwareMap);
        telemetry.addData("armpos", parts.getArmPos());
        telemetry.update();
        waitForStart();
        double startuptime = runtime.milliseconds();
        while (opModeIsActive()) {
            telemetry.addData("autonomous mode enabled", autoEnabled);
            telemetry.addData("has initialized", hasInit);
            pos_y = parts.posY();
            pos_x = parts.posX();
            BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
            parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
            imu = hardwareMap.get(BNO055IMU.class,  "imu");
            imu.initialize(parameters);
            if (autoEnabled) {
                double ms = runtime.milliseconds() - startuptime;
                //int stage = 0;
                //int stage = (int) Math.round(ms / 3000) - 1; //required for async execution. dont remove
                angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
                telemetry.addData("direction", angles.firstAngle);
                telemetry.addData("armpos", parts.getArmPos());
                telemetry.addData("stage", stage);
                telemetry.addData("Pos_y", pos_y);
                telemetry.addData("milliseconds,",ms);
                telemetry.addData("Slidespos", parts.getSlidesPos());
                switch (stage) {
                    case "init complete":
                        arm = 1;
                        if(parts.getArmPos() > 700){
                            stage ="arm weg";
                        }
                        break;
                    case "arm weg":  //test
                        slidesPos = 3750;
                        vy = -1;
                        arm = -1;
                        if (-pos_y>12000){stage="at location";}
                        break;
                    case "at location":
                        vy = 0;
                        if (parts.getSlidesPos()>3500){
                            stage="slides up";
                            startuptime = runtime.milliseconds();
                        }
                        break;
                    case "slides up":
                        sampleStorage = 0.5;
                        if (ms>1000){
                            stage = "sample scored";
                        }
                        break;
                    case "sample scored":
                        vx = 1;
                        sampleStorage = 0.9;
                        if(pos_x>=10000){
                            stage = "retreat complete";
                            startuptime = runtime.milliseconds();
                        }
                    case "retreat complete":
                        vx = 0;
                        slidesPos = 0;
                        va = 1;
                        if (angles.firstAngle > 89){stage = "driven to sample";}
                        break;
                    case "driven to sample":
                        va = 0;
                        arm = 1;
                        if (parts.getArmPos() > 600) {
                            stage = "arm down";
                            startuptime = runtime.milliseconds();
                        }
                        break;
                    case "arm down":
                        arm = -1;
                        intakeClaw = 0;
                        if (ms > 500) {stage = "sample picked up";}
                        break;




                    /*case "init complete":
                        vy = -1;
                        vx = 0;
                        va = 0;
                        if (pos_y<=-13000) {
                            stage = "bij basket";

                        }
                        break;
                    case "bij basket"://-14722
                        vy = 0;
                        slides = 0.9;
                        if (parts.getSlidesPos() > 3500) {
                            stage = "slides omhoog";
                            startuptime = ms;
                            ms = startuptime - ms;
                        }
                        break;
                    case "slides omhoog":
                        slides = 0.3;
                        sampleStorage = 0;
                        if (ms > 2000) {
                            stage = "first sample scored";
                        }
                        break;*/





                    /*case "first sample scored":
                        va = 1;
                        sampleStorage = 0.95;
                        slides = 0;
                        if (pos_y > -14000) {
                            stage = "draai naar samples";
                        }
                        break;
                    case "draai naar samples":
                        va = 0;
                        vy = 1;
                        intakeOrientation = 1;
                        intakeClaw = 0;
                        if (pos_y > -5000) {
                            stage = "bij first sample";
                            startuptime = ms;
                            ms = startuptime - ms;
                        }
                        break;
                    case "bij first sample":
                        arm = -1;
                        if (ms > 500) {
                            stage = "arm naar buiten";
                        }
                        break;
                    case "arm naar buiten":
                        arm = 0;
                        intakeClaw = 1;
                        slides = -0.9;
                        if (parts.getSlidesPos() < 300) {
                            stage = "slides naar beneden";
                            startuptime = ms;
                            ms = startuptime - ms;
                        }
                        break;
                    case "slides naar beneden":
                        arm = 1;
                        intakeOrientation = 0;
                        slides = 0;
                        if (ms > 1300) {
                            stage = "klaar voor sample drop";
                        }
                        break;
                    case "klaar voor sample drop":
                        intakeClaw = 0;
                        if (ms > 2000) {
                            stage = "sample dropped";
                        }
                        break;
                    case "sample dropped":
                        vx = 1;
                        if (ms > 2500) {
                            stage = "naar zijkant";
                        }
                        break;
                    case "naar zijkant":
                        if (ms < 3800){
                            arm = -1;
                        }
                        else {
                            arm = 0;
                        }
                        slides = 1;
                        if ((parts.getSlidesPos() > 3500) && (ms > 3800)) {
                        stage = "slides omhoog met tweede sample";
                        startuptime = ms;
                        ms = startuptime - ms;
                        }
                        break;
                    case "slides omhoog met tweede sample":
                        slides = 0.3;
                        sampleStorage = 0;
                        if (ms > 1000) {
                            stage = "second sample scored";
                        }
                        break;
                    case "done":
                        vx = 0;
                        vy = 0;
                        va = 0;
                        slides = 0;
                        break;*/
                    default:
                        vy = 0;
                        vx = 0;
                        va = 0;
                        break;
                }

            } else {
                double x = gamepad1.left_stick_x;
                double y = gamepad1.left_stick_y;

                telemetry.addData("gx", x);
                telemetry.addData("gy", y);
            }

            //if (gamepad1.a) autoEnabled = true;
            //if (gamepad1.b) autoEnabled = false;


            //handle movement
            if (!manual) {
                parts.drive0(vy, vx, va, 3);
                //parts.setSlidesPower(slides);
                parts.setSlidePosition(slidesPos);
                parts.setArmPower(arm / 2);
                parts.sampleBakje(sampleStorage);
                parts.intakeClaw(intakeClaw);
                parts.setIntakeOrientation(intakeOrientation);
            }/* else if (hasInit) {
                leftFront.setPower(fl / 1000);
                rightFront.setPower(fr / 1000);
                rightBack.setPower(br / 1000);
                leftBack.setPower(bl / 1000);
            }*/
            telemetry.addData("slidespower", parts.slidespower());
            telemetry.update();
        }
    }
}