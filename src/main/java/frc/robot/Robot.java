// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
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
  private final WPI_TalonSRX m_bottomFeedFront = new WPI_TalonSRX(Constants.IDs.Talon.feedBottomFront);
  private final WPI_TalonSRX m_bottomFeedRear = new WPI_TalonSRX(Constants.IDs.Talon.feedBottomRear);
  private final WPI_TalonSRX m_topFeedFront = new WPI_TalonSRX(Constants.IDs.Talon.feedTopFront);
  private final WPI_TalonSRX m_topFeedRear = new WPI_TalonSRX(Constants.IDs.Talon.feedTopRear);
  private final MotorControllerGroup m_topFeeder = new MotorControllerGroup(m_topFeedFront, m_topFeedRear);
  private final MotorControllerGroup m_bottomFeeder = new MotorControllerGroup(m_bottomFeedFront, m_bottomFeedRear);
  private final WPI_TalonFX m_shooter = new WPI_TalonFX(Constants.IDs.Falcon.shooter);
  private final DifferentialDrive m_robotDrive = new DifferentialDrive(m_leftMotor, m_rightMotor);
  private final Joystick m_leftJoystick = new Joystick(Constants.OI.leftJoy);
  private final Joystick m_rightJoystick = new Joystick(Constants.OI.rightJoy);
  private final Joystick m_operJoystick = new Joystick(Constants.OI.operJoy);
  // private final AHRS m_gyro = new AHRS(SPI.Port.kMXP);
  private final Timer m_timer = new Timer();
  private double m_shooterSpeed = 0.0;

  @Override
  public void robotInit() {
    // Invert one side only usually, so that forward is green on the controller and backwards is red
    m_leftMotor.setInverted(Constants.DriveTrain.Left.isInverted);
    m_rightMotor.setInverted(Constants.DriveTrain.Right.isInverted);
    m_shooter.setInverted(Constants.Shooter.isInverted);
    m_topFeedFront.setInverted(Constants.Feeder.isInvertedTopFront);
    m_topFeedRear.setInverted(Constants.Feeder.isInvertedTopRear);
    m_bottomFeedFront.setInverted(Constants.Feeder.isInvertedBottomFront);
    m_bottomFeedRear.setInverted(Constants.Feeder.isInvertedTopRear);
  }

  //This runs at when the robot is disabled. Useful for resetting things.
  @Override
  public void disabledInit() {
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
    if(Constants.OI.useArcadeDrive) {
      m_robotDrive.arcadeDrive(OI.deadband(-m_leftJoystick.getRawAxis(Constants.OI.arcadeThrottleAxis)), OI.deadband(m_rightJoystick.getRawAxis(Constants.OI.arcadeTurnAxis)));
    } else { //tank drive
      m_robotDrive.tankDrive(OI.deadband(-m_leftJoystick.getRawAxis(Constants.OI.tankThrottleAxis)), OI.deadband(m_rightJoystick.getRawAxis(Constants.OI.tankThrottleAxis)));
    }

    //Handle the bottom feeder
    if(m_operJoystick.getRawButtonPressed(Constants.OI.btnBottomFeed)) { //start bottom feeder
      System.out.println("teleopPeriodic: Run bottom feeder");
      m_bottomFeeder.set(Constants.Feeder.kFeederSpeedBottom);
    } else if(m_operJoystick.getRawButtonReleased(Constants.OI.btnBottomFeed)) { //stop bottom feeder
      System.out.println("teleopPeriodic: Stop bottom feeder");
      m_bottomFeeder.set(0.0);
    }
    //Handle the top feeder
    if(m_operJoystick.getRawButtonPressed(Constants.OI.btnTopFeed)) { //start top feeder
      System.out.println("teleopPeriodic: Run top feeder");
      m_topFeeder.set(Constants.Feeder.kFeederSpeedTop);
    } else if(m_operJoystick.getRawButtonReleased(Constants.OI.btnTopFeed)) { //stop toip feeder
      System.out.println("teleopPeriodic: Stop top feeder");
      m_topFeeder.set(0.0);
    }

    //Handle the shooter 
    if(m_operJoystick.getRawButtonPressed(Constants.OI.btnShooter)) { //start shooter
      System.out.println("teleopPeriodic: Run shooter");
      if(Constants.Shooter.useShooterThrottle) {
        m_shooterSpeed = -m_rightJoystick.getRawAxis(3);
        System.out.println("teleopPeriodic: Shooter Speed " + m_shooterSpeed);
        m_shooter.set(ControlMode.PercentOutput, m_shooterSpeed);
      } else {
        m_bottomFeeder.set(Constants.Shooter.kShooterSpeed);
      }
    } else if(m_operJoystick.getRawButtonReleased(Constants.OI.btnShooter)) { //stop shooter
      System.out.println("teleopPeriodic: Stop shooter");
      m_shooterSpeed = 0.0;
      m_shooter.set(ControlMode.PercentOutput, 0.0);
    }
    if(m_operJoystick.getRawButton(Constants.OI.btnShooter)) { //holding the shooter button
      if (-m_operJoystick.getRawAxis(3) != m_shooterSpeed) {
        m_shooterSpeed = -m_rightJoystick.getRawAxis(3);
        // System.out.println("teleopPeriodic: Shooter Speed " + m_shooterSpeed);
        m_shooter.set(ControlMode.PercentOutput, m_shooterSpeed);
      }
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
    if (!Constants.Auton.isDisableld) {
      switch (Constants.Auton.autonName) {
        case "Basic": //Basic Auton
          if (m_timer.get() < 2.0) { //2 seconds
            m_robotDrive.arcadeDrive(0.5, 0.0); //drive forward at 50% speed
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
