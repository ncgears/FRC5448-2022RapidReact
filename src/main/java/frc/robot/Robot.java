// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;
//test
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.cscore.UsbCamera;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;


/**
 * This drives the robot with Arcade steering (vs. tank drive) where the left joystick is throttle, and the right joystick is turning.
 */
public class Robot extends TimedRobot {
  private final WPI_VictorSPX m_leftfront = new WPI_VictorSPX(Constants.IDs.Victor.driveLeftFront);
  private final WPI_VictorSPX m_leftrear = new WPI_VictorSPX(Constants.IDs.Victor.driveLeftRear);
  private final WPI_VictorSPX m_rightfront = new WPI_VictorSPX(Constants.IDs.Victor.driveRightFront);
  private final WPI_VictorSPX m_rightrear = new WPI_VictorSPX(Constants.IDs.Victor.driveRightRear);
  private final MotorControllerGroup m_leftMotor = new MotorControllerGroup(m_leftfront, m_leftrear);
  private final MotorControllerGroup m_rightMotor = new MotorControllerGroup(m_rightfront, m_rightrear);
  private final DifferentialDrive m_robotDrive = new DifferentialDrive(m_leftMotor, m_rightMotor);
  private final Joystick m_leftJoystick = new Joystick(Constants.OI.leftJoy);
  private final Joystick m_rightJoystick = new Joystick(Constants.OI.rightJoy);
  private final AHRS m_gyro = new AHRS(SPI.Port.kMXP);
  private final DoubleSolenoid m_gearShift = new DoubleSolenoid(PneumaticsModuleType.CTREPCM, Constants.IDs.Solenoid.driveLowGear, Constants.IDs.Solenoid.driveHighGear);
  private final Solenoid m_collector = new Solenoid(PneumaticsModuleType.CTREPCM, Constants.IDs.Solenoid.collectorRaise);
  private final WPI_TalonSRX m_intake = new WPI_TalonSRX(Constants.IDs.Talon.intake);
  private final WPI_TalonSRX m_tunnel = new WPI_TalonSRX(Constants.IDs.Talon.tunnel);
  private final WPI_TalonSRX m_shooterLeft = new WPI_TalonSRX(Constants.IDs.Talon.shooterLeft);
  private final WPI_TalonSRX m_shooterRight = new WPI_TalonSRX(Constants.IDs.Talon.shooterRight);
  private final MotorControllerGroup m_shooter = new MotorControllerGroup(m_shooterLeft, m_shooterRight);
  private final Timer m_timer = new Timer();

  @Override
  public void robotInit() {
    //Disable LiveWindow data (not needed)
    LiveWindow.disableAllTelemetry();
    //Start Camera Server
    UsbCamera camera = CameraServer.startAutomaticCapture();
    camera.setResolution(640, 480);
    // Invert one side only usually, so that forward is green on the controller and backwards is red
    m_leftMotor.setInverted(Constants.DriveTrain.Left.isInverted);
    m_rightMotor.setInverted(Constants.DriveTrain.Right.isInverted);
    m_intake.setInverted(Constants.Collector.intakeIsInverted);
    m_intake.setNeutralMode(NeutralMode.Brake);
    m_gearShift.set(DoubleSolenoid.Value.kForward);
    m_tunnel.setInverted(Constants.Tunnel.tunnelIsInverted);
    m_tunnel.setNeutralMode(NeutralMode.Brake);
    m_shooterLeft.setInverted(Constants.Shooter.shooterLeftIsInverted);
    m_shooterRight.setInverted(Constants.Shooter.shooterRightIsInverted);
    m_shooterLeft.setNeutralMode(NeutralMode.Coast);
    m_shooterRight.setNeutralMode(NeutralMode.Coast);
  }

  //This runs at when the robot is disabled. Useful for resetting things.
  @Override
  public void disabledInit() {
    System.out.println("disabledInit: Resetting drivetrain to low gear");
    m_gearShift.set(DoubleSolenoid.Value.kForward); //reset to low gear
    System.out.println("disabledInit: Retracting collector");
    m_collector.set(!Constants.Collector.airStateDeployed); //retract the collector
  }

  //This runs at the beginning of teleop
  @Override
  public void teleopInit() {
  }

  //This runs every loop during teleop
  @Override
  public void teleopPeriodic() {
    // Drive with arcade drive.
    // That means that the Y axis drives forward
    // and backward, and the X turns left and right.
    if(Constants.OI.useTankDrive) {
      m_robotDrive.tankDrive(OI.deadband(-m_leftJoystick.getRawAxis(1)) * Constants.DriveTrain.kSpeedMultiplier, OI.deadband(-m_rightJoystick.getRawAxis(1)) * Constants.DriveTrain.kSpeedMultiplier);
    } else {
      m_robotDrive.arcadeDrive(OI.deadband(-m_leftJoystick.getRawAxis(1)) * Constants.DriveTrain.kSpeedMultiplier, OI.deadband(m_rightJoystick.getRawAxis(0)));
    }

    //Handle the gear shifting
    if(m_leftJoystick.getRawButtonPressed(Constants.Controllers.Ultrastik.BTN_4)) { //shift to low gear
      System.out.println("teleopPeriodic: Shifting to low gear -- I am STRONG!");
      m_gearShift.set(DoubleSolenoid.Value.kForward);
    } else if(m_leftJoystick.getRawButtonPressed(Constants.Controllers.Ultrastik.BTN_5)) { //shift to high gear
      System.out.println("teleopPeriodic: Shifting to high gear -- I am SPEED!");
      m_gearShift.set(DoubleSolenoid.Value.kReverse); 
    }

    //Handle the collector controls
    if(m_rightJoystick.getRawButtonPressed(Constants.Controllers.Ultrastik.BTN_4)) { //lower collector
      System.out.println("teleopPeriodic: Lowering Collector");
      m_collector.set(!Constants.Collector.airStateDeployed);
    } else if(m_rightJoystick.getRawButtonPressed(Constants.Controllers.Ultrastik.BTN_5)) { //raise collector
      System.out.println("teleopPeriodic: Raising Collector");
      m_collector.set(Constants.Collector.airStateDeployed);
    }

    //Handle the intake
    if(m_rightJoystick.getRawButtonPressed(Constants.Controllers.Ultrastik.BTN_2)) { //intake in
      System.out.println("teleopPeriodic: Intake In");
      m_intake.set(ControlMode.PercentOutput, Constants.Collector.kIntakeSpeed);
    } else if (m_rightJoystick.getRawButtonPressed(Constants.Controllers.Ultrastik.BTN_1)) { //intake out
      System.out.println("teleopPeriodic: Intake Out");
      m_intake.set(ControlMode.PercentOutput, Constants.Collector.kIntakeSpeed * -1);
    } else if (m_rightJoystick.getRawButtonReleased(Constants.Controllers.Ultrastik.BTN_2) || m_rightJoystick.getRawButtonReleased(Constants.Controllers.Ultrastik.BTN_1)) { //intake stop
      System.out.println("teleopPeriodic: Intake Stop");
      m_intake.set(ControlMode.PercentOutput, 0);
    }

    //Handle the tunnel
    if(m_rightJoystick.getRawButtonPressed(Constants.Controllers.Ultrastik.BTN_6)) { //start tunnel
      System.out.println("teleopPeriodic: Tunnel Start");
      m_tunnel.set(Constants.Tunnel.kTunnelSpeed);
    } else if (m_rightJoystick.getRawButtonReleased(Constants.Controllers.Ultrastik.BTN_6)) { //stop tunnel
      System.out.println("teleopPeriodic: Tunnel Stop");
      m_tunnel.stopMotor();
    }

    //Handle the shooter
    if(m_rightJoystick.getRawButtonPressed(Constants.Controllers.Ultrastik.BTN_7)) { //start shooter
      System.out.println("teleopPeriodic: Shooter Start");
      m_shooter.set(Constants.Shooter.kShooterSpeed);
    } else if (m_rightJoystick.getRawButtonReleased(Constants.Controllers.Ultrastik.BTN_7)) { //stop shooter
      System.out.println("teleopPeriodic: Shooter Stop");
      m_shooter.stopMotor();
    }
  }

  //This runs at the beginning of autonomous
  @Override
  public void autonomousInit() {
    m_timer.reset();
    m_timer.start();
  }

  //This runs every loop during autonomous
  @Override
  public void autonomousPeriodic() {
    if (!Constants.Auton.isDisabled) {
      switch (Constants.Auton.autonName) {
        case "Basic": //Basic Auton
          if (m_timer.get() < 2.0) { //2 seconds
            m_robotDrive.arcadeDrive(-Constants.Auton.kAutonDriveSpeed, 0.0); //drive forward at 50% speed
          } else {
            m_robotDrive.stopMotor(); //stop
          }
          break;
        case "ShootAndScoot": //Basic Auton
          m_shooter.set(Constants.Shooter.kShooterSpeed); //start the shooter
          //wait for shooter to get to speed, then run the tunnel until it is time to drive
          if (m_timer.get() > 0.5 && m_timer.get() < 2.0) { //at 0.5s, until 2s
            m_tunnel.set(Constants.Tunnel.kTunnelSpeed);
          } else {
            m_tunnel.stopMotor();
          }
          //stop the shooter and start driving
          if (m_timer.get() > 2.0 && m_timer.get() < 4.0) { //at 2s, until 4s
            m_shooter.stopMotor();
            m_robotDrive.arcadeDrive(-Constants.Auton.kAutonDriveSpeed, 0.0); //drive forward at 50% speed
          } else {
            m_robotDrive.stopMotor(); //stop
          }
          break;
        default:
          break;
      }
    }
  }
}
