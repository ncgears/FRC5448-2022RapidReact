package frc.robot;

public class OI {
    public static double deadband(double value) {
        double direction = Math.signum(value);
        value = (Math.abs(value) > Constants.OI.kMaxDeadband) ? direction * 1.0 : value;  //over the max
        value = (Math.abs(value) < Constants.OI.kMinDeadband) ? 0.0 : value; //under the min
        return value;
    }    
}
