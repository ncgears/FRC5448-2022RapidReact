package frc.robot;

//testing git push
public class Constants {
    //IDs of things on the robot
    public class IDs {
        //IDs of Talons
        public class Talon {
            public static final int intake = 5;
        }
        //IDs of Victors
        public class Victor {
            public static final int driveLeftRear = 1;
            public static final int driveLeftFront = 2;
            public static final int driveRightRear = 3;
            public static final int driveRightFront = 4;
        }
        //IDs of Solenoids
        public class Solenoid {
            public static final int driveLowGear = 0;
            public static final int driveHighGear = 1;
            public static final int collectorRaise = 2;
        }
    }

    //Drivetrain
    public class DriveTrain {
        public class Left {
            public static final boolean isInverted = false; //invert the left motors
        }
        public class Right {
            public static final boolean isInverted = true; //invert the right motors
        }
    }

    //Collector/Intake
    public class Collector {
        public static final boolean airStateDeployed = true; //state of the solenoid when collector deployed
        public static final double kIntakeSpeed = 0.5; //Speed of intake motors
        public static final boolean intakeIsInverted = false; //invert intake direction
    }

    //Auton
    public class Auton {
        public static final boolean isDisableld = false;
        public static final String autonName = "Basic";
    }

    //Operator Interface
    public class OI {
        public static final int leftJoy = 0;
        public static final int rightJoy = 1;
        public static final double kMaxDeadband = 0.9;
        public static final double kMinDeadband = 0.1;
    }

    //Controller Types (don't edit these)
    public static final class Controllers {
        public static final class Ultrastik { //Dual Ultimarc Utrastik 360
            static final int BTN_1 = 1;
            static final int BTN_2 = 2;
            static final int BTN_3 = 3;
            static final int BTN_4 = 4;
            static final int BTN_5 = 5;
            static final int BTN_6 = 6;
            static final int BTN_7 = 7;
            static final int BTN_8 = 8;
            static final int BTN_9 = 9;
            static final int BTN_10 = 10;
            static final int BTN_11 = 11;
            static final int BTN_12 = 12;
            static final int BTN_13 = 13;
            static final int BTN_14 = 14;
            static final int BTN_15 = 15;
        }
        public static final class Logitech { //Logitech F310 Controller
            static final int BTN_A = 1; //A Button
            static final int BTN_B = 2; //B Button
            static final int BTN_X = 3; //X Button
            static final int BTN_Y = 4; //Y Button
            static final int BTN_LB = 5; //Left Bumper (L1)
            static final int BTN_RB = 6; //Right Bumper (R1)
            static final int BTN_BACK = 7; //Back Button (Select)
            static final int BTN_START = 8; //Start Button
            static final int BTN_L = 9; //Left Stick Press (L3)
            static final int BTN_R = 10; //Right Stick Press (R3)
            static final int AXIS_LH = 0; //Left Analog Stick horizontal
            static final int AXIS_LV = 1; //Left Analog Stick vertical
            static final int AXIS_LT = 2; //Analog Left Trigger
            static final int AXIS_RT = 3; //Analog Right Trigger
            static final int AXIS_RH = 4; //Right Analog Stick horizontal
            static final int AXIS_RV = 5; //Right Analog Stick vertical
            static final int DPAD_UP = 0;
            static final int DPAD_UPRIGHT = 45;
            static final int DPAD_RIGHT = 90;
            static final int DPAD_DNRIGHT = 135;
            static final int DPAD_DN = 180;
            static final int DPAD_DNLEFT = 225;
            static final int DPAD_LEFT = 270;
            static final int DPAD_UPLEFT = 315;
            static final int DPAD_IDLE = -1; 
        }
    }
}
