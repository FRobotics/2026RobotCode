// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The methods in this class are called automatically corresponding to each mode, as described in
 * the TimedRobot documentation. If you change the name of this class or the package after creating
 * this project, you must also update the Main.java file in the project.
 */
public class Robot extends TimedRobot {

  private static final String kDefaultAuto = "--NONE--";
  // private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  //private final SendableChooser<String> m_chooser = new SendableChooser<>();

  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   * 
   * This is the class constructor.
   */
  public Robot() {

    MatchSystem.init();   // match system has to be first because of robotphase....
    SwerveTeleop.init();
    SwerveDrive.SwerveInit();
    SwerveOdometry.init();
    SwerveVision.init();
    IntakeSystem.init();
    AgitatorSystem.init();
    FeederSystem.init();
    TrajectorySystem.TrajectoryInit();
    String[] ourautos = AutoSystem.init();
    TurretLauncher.init();
    Climb.init();
    SupervisoryCmds.init();
 
    //m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    //m_chooser.addOption("My Auto", kCustomAuto);
    //SmartDashboard.putData("Auto choices", m_chooser);
    SmartDashboard.putStringArray("Auto List", ourautos);

    // --------indicate startup is done.
    MatchSystem.setRobotPhaseStartupComplete();

  }

  /**
   * This function is called every 20 ms, no matter the mode. Use this for items like diagnostics
   * that you want ran during disabled, autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before LiveWindow and
   * SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {

    double systemElapsedTimeSec = Timer.getFPGATimestamp();

    SwerveDrive.SwerveExec(systemElapsedTimeSec);
    SwerveOdometry.execute(systemElapsedTimeSec);
    SwerveVision.execute(systemElapsedTimeSec);
    IntakeSystem.executeLogic(systemElapsedTimeSec);
    AgitatorSystem.executeLogic(systemElapsedTimeSec);
    FeederSystem.executeLogic(systemElapsedTimeSec);
    TurretLauncher.executeLogic(systemElapsedTimeSec);
    Climb.executeLogic(systemElapsedTimeSec);
    return;    
  }

  /**
   * This autonomous (along with the chooser code above) shows how to select between different
   * autonomous modes using the dashboard. The sendable chooser code works with the Java
   * SmartDashboard. If you prefer the LabVIEW Dashboard, remove all of the chooser code and
   * uncomment the getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to the switch structure
   * below with additional strings. If using the SendableChooser make sure to add them to the
   * chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    // m_autoSelected = m_chooser.getSelected();
    m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);
    AutoSystem.ExecuteListInit(m_autoSelected);
    return;
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {
    double systemElapsedTimeSec = Timer.getFPGATimestamp();
    AutoSystem.ExecuteList(systemElapsedTimeSec);
    return;
  }

  /** This function is called once when teleop is enabled. */
  @Override
  public void teleopInit() {
    return;
  }

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {
    SwerveTeleop.SwerveExecute();
    return;
  }

  /** This function is called once when the robot is disabled. */
  @Override
  public void disabledInit() {
    return;
  }

  /** This function is called periodically when disabled. */
  @Override
  public void disabledPeriodic() {
    MatchSystem.disableExec();
    return;
  }

  /** This function is called once when test mode is enabled. */
  @Override
  public void testInit() {
    return;
  }

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {
    return;
  }

  /** This function is called once when the robot is first started up. */
  @Override
  public void simulationInit() {
    return;
  }

  /** This function is called periodically whilst in simulation. */
  @Override
  public void simulationPeriodic() {
    return;
  }
}
