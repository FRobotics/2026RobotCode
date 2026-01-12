<?xml version='1.0' encoding='UTF-8'?>
<Project Type="Project" LVVersion="23008000">
	<Property Name="NI.LV.All.SourceOnly" Type="Bool">true</Property>
	<Item Name="My Computer" Type="My Computer">
		<Property Name="server.app.propertiesEnabled" Type="Bool">true</Property>
		<Property Name="server.control.propertiesEnabled" Type="Bool">true</Property>
		<Property Name="server.tcp.enabled" Type="Bool">false</Property>
		<Property Name="server.tcp.port" Type="Int">0</Property>
		<Property Name="server.tcp.serviceName" Type="Str">My Computer/VI Server</Property>
		<Property Name="server.tcp.serviceName.default" Type="Str">My Computer/VI Server</Property>
		<Property Name="server.vi.callsEnabled" Type="Bool">true</Property>
		<Property Name="server.vi.propertiesEnabled" Type="Bool">true</Property>
		<Property Name="specify.custom.address" Type="Bool">false</Property>
		<Item Name="NewAuto" Type="Folder" URL="../../2025ReefBot/NewAuto">
			<Property Name="NI.DISK" Type="Bool">true</Property>
		</Item>
		<Item Name="AutoEditor.ico" Type="Document" URL="../AutoEditor.ico"/>
		<Item Name="EditorInterface.vi" Type="VI" URL="../EditorInterface.vi"/>
		<Item Name="Dependencies" Type="Dependencies">
			<Item Name="vi.lib" Type="Folder">
				<Item Name="Acquire Semaphore.vi" Type="VI" URL="/&lt;vilib&gt;/Utility/semaphor.llb/Acquire Semaphore.vi"/>
				<Item Name="AddNamedSemaphorePrefix.vi" Type="VI" URL="/&lt;vilib&gt;/Utility/semaphor.llb/AddNamedSemaphorePrefix.vi"/>
				<Item Name="Clear Errors.vi" Type="VI" URL="/&lt;vilib&gt;/Utility/error.llb/Clear Errors.vi"/>
				<Item Name="Dflt Data Dir.vi" Type="VI" URL="/&lt;vilib&gt;/Utility/file.llb/Dflt Data Dir.vi"/>
				<Item Name="Error Cluster From Error Code.vi" Type="VI" URL="/&lt;vilib&gt;/Utility/error.llb/Error Cluster From Error Code.vi"/>
				<Item Name="FPGA_SystemAsync VI Agent.vi" Type="VI" URL="/&lt;vilib&gt;/Rock Robotics/SystemInterfaces/System/FPGA_SystemAsync VI Agent.vi"/>
				<Item Name="FPGA_SystemAsynch VI Registration.vi" Type="VI" URL="/&lt;vilib&gt;/Rock Robotics/SystemInterfaces/System/FPGA_SystemAsynch VI Registration.vi"/>
				<Item Name="FPGA_SystemERRWrongVersion.vi" Type="VI" URL="/&lt;vilib&gt;/Rock Robotics/SystemInterfaces/System/FPGA_SystemERRWrongVersion.vi"/>
				<Item Name="FPGA_SystemFPGA Ref Global.vi" Type="VI" URL="/&lt;vilib&gt;/Rock Robotics/SystemInterfaces/System/FPGA_SystemFPGA Ref Global.vi"/>
				<Item Name="FPGA_SystemFRC FPGA Ref.ctl" Type="VI" URL="/&lt;vilib&gt;/Rock Robotics/SystemInterfaces/System/FPGA_SystemFRC FPGA Ref.ctl"/>
				<Item Name="FPGA_SystemGet.vi" Type="VI" URL="/&lt;vilib&gt;/Rock Robotics/SystemInterfaces/System/FPGA_SystemGet.vi"/>
				<Item Name="FPGA_SystemOpen.vi" Type="VI" URL="/&lt;vilib&gt;/Rock Robotics/SystemInterfaces/System/FPGA_SystemOpen.vi"/>
				<Item Name="FPGA_SystemStart Async Agent.vi" Type="VI" URL="/&lt;vilib&gt;/Rock Robotics/SystemInterfaces/System/FPGA_SystemStart Async Agent.vi"/>
				<Item Name="FPGA_UtilitiesRead LocalTime.vi" Type="VI" URL="/&lt;vilib&gt;/Rock Robotics/SystemInterfaces/Utilities/FPGA_UtilitiesRead LocalTime.vi"/>
				<Item Name="Get File Extension.vi" Type="VI" URL="/&lt;vilib&gt;/Utility/libraryn.llb/Get File Extension.vi"/>
				<Item Name="GetNamedSemaphorePrefix.vi" Type="VI" URL="/&lt;vilib&gt;/Utility/semaphor.llb/GetNamedSemaphorePrefix.vi"/>
				<Item Name="NetComm_getFPGAFileName.vi" Type="VI" URL="/&lt;vilib&gt;/Rock Robotics/SystemInterfaces/NetworkCommunication/NetComm_getFPGAFileName.vi"/>
				<Item Name="NetComm_UnloadCPPStartupProgram.vi" Type="VI" URL="/&lt;vilib&gt;/Rock Robotics/SystemInterfaces/NetworkCommunication/NetComm_UnloadCPPStartupProgram.vi"/>
				<Item Name="NetComm_UsageReport_report.vi" Type="VI" URL="/&lt;vilib&gt;/Rock Robotics/SystemInterfaces/NetworkCommunication/NetComm_UsageReport_report.vi"/>
				<Item Name="NetComm_UsageReport_ResourceType.ctl" Type="VI" URL="/&lt;vilib&gt;/Rock Robotics/SystemInterfaces/NetworkCommunication/NetComm_UsageReport_ResourceType.ctl"/>
				<Item Name="NI_AALBase.lvlib" Type="Library" URL="/&lt;vilib&gt;/Analysis/NI_AALBase.lvlib"/>
				<Item Name="NI_Matrix.lvlib" Type="Library" URL="/&lt;vilib&gt;/Analysis/Matrix/NI_Matrix.lvlib"/>
				<Item Name="Not A Semaphore.vi" Type="VI" URL="/&lt;vilib&gt;/Utility/semaphor.llb/Not A Semaphore.vi"/>
				<Item Name="NT Check Errors.vi" Type="VI" URL="/&lt;vilib&gt;/Rock Robotics/Network Tables/NT Check Errors.vi"/>
				<Item Name="NT Datatype.ctl" Type="VI" URL="/&lt;vilib&gt;/Rock Robotics/Network Tables/NT Datatype.ctl"/>
				<Item Name="NT Get PublishHandle.vi" Type="VI" URL="/&lt;vilib&gt;/Rock Robotics/Network Tables/NT Get PublishHandle.vi"/>
				<Item Name="NT Get SubscribeHandle.vi" Type="VI" URL="/&lt;vilib&gt;/Rock Robotics/Network Tables/NT Get SubscribeHandle.vi"/>
				<Item Name="NT Get Topic.vi" Type="VI" URL="/&lt;vilib&gt;/Rock Robotics/Network Tables/NT Get Topic.vi"/>
				<Item Name="NT PublishOptions.ctl" Type="VI" URL="/&lt;vilib&gt;/Rock Robotics/Network Tables/NT PublishOptions.ctl"/>
				<Item Name="NT Read Boolean Array.vi" Type="VI" URL="/&lt;vilib&gt;/Rock Robotics/Network Tables/NT Read Boolean Array.vi"/>
				<Item Name="NT Read Boolean.vi" Type="VI" URL="/&lt;vilib&gt;/Rock Robotics/Network Tables/NT Read Boolean.vi"/>
				<Item Name="NT Read Float Array.vi" Type="VI" URL="/&lt;vilib&gt;/Rock Robotics/Network Tables/NT Read Float Array.vi"/>
				<Item Name="NT Read Float.vi" Type="VI" URL="/&lt;vilib&gt;/Rock Robotics/Network Tables/NT Read Float.vi"/>
				<Item Name="NT Read Integer Array.vi" Type="VI" URL="/&lt;vilib&gt;/Rock Robotics/Network Tables/NT Read Integer Array.vi"/>
				<Item Name="NT Read Integer.vi" Type="VI" URL="/&lt;vilib&gt;/Rock Robotics/Network Tables/NT Read Integer.vi"/>
				<Item Name="NT Read Number.vi" Type="VI" URL="/&lt;vilib&gt;/Rock Robotics/Network Tables/NT Read Number.vi"/>
				<Item Name="NT Read Numeric Array.vi" Type="VI" URL="/&lt;vilib&gt;/Rock Robotics/Network Tables/NT Read Numeric Array.vi"/>
				<Item Name="NT Read Raw.vi" Type="VI" URL="/&lt;vilib&gt;/Rock Robotics/Network Tables/NT Read Raw.vi"/>
				<Item Name="NT Read String Array.vi" Type="VI" URL="/&lt;vilib&gt;/Rock Robotics/Network Tables/NT Read String Array.vi"/>
				<Item Name="NT Read String.vi" Type="VI" URL="/&lt;vilib&gt;/Rock Robotics/Network Tables/NT Read String.vi"/>
				<Item Name="NT Read Value.vi" Type="VI" URL="/&lt;vilib&gt;/Rock Robotics/Network Tables/NT Read Value.vi"/>
				<Item Name="NT Read Variant.vi" Type="VI" URL="/&lt;vilib&gt;/Rock Robotics/Network Tables/NT Read Variant.vi"/>
				<Item Name="NT SubscribeOptions.ctl" Type="VI" URL="/&lt;vilib&gt;/Rock Robotics/Network Tables/NT SubscribeOptions.ctl"/>
				<Item Name="NT Write Boolean Array.vi" Type="VI" URL="/&lt;vilib&gt;/Rock Robotics/Network Tables/NT Write Boolean Array.vi"/>
				<Item Name="NT Write Boolean.vi" Type="VI" URL="/&lt;vilib&gt;/Rock Robotics/Network Tables/NT Write Boolean.vi"/>
				<Item Name="NT Write Float Array.vi" Type="VI" URL="/&lt;vilib&gt;/Rock Robotics/Network Tables/NT Write Float Array.vi"/>
				<Item Name="NT Write Float.vi" Type="VI" URL="/&lt;vilib&gt;/Rock Robotics/Network Tables/NT Write Float.vi"/>
				<Item Name="NT Write Integer Array.vi" Type="VI" URL="/&lt;vilib&gt;/Rock Robotics/Network Tables/NT Write Integer Array.vi"/>
				<Item Name="NT Write Integer.vi" Type="VI" URL="/&lt;vilib&gt;/Rock Robotics/Network Tables/NT Write Integer.vi"/>
				<Item Name="NT Write Number.vi" Type="VI" URL="/&lt;vilib&gt;/Rock Robotics/Network Tables/NT Write Number.vi"/>
				<Item Name="NT Write Numeric Array.vi" Type="VI" URL="/&lt;vilib&gt;/Rock Robotics/Network Tables/NT Write Numeric Array.vi"/>
				<Item Name="NT Write Raw.vi" Type="VI" URL="/&lt;vilib&gt;/Rock Robotics/Network Tables/NT Write Raw.vi"/>
				<Item Name="NT Write String Array.vi" Type="VI" URL="/&lt;vilib&gt;/Rock Robotics/Network Tables/NT Write String Array.vi"/>
				<Item Name="NT Write String.vi" Type="VI" URL="/&lt;vilib&gt;/Rock Robotics/Network Tables/NT Write String.vi"/>
				<Item Name="NT Write Value.vi" Type="VI" URL="/&lt;vilib&gt;/Rock Robotics/Network Tables/NT Write Value.vi"/>
				<Item Name="NT Write Variant.vi" Type="VI" URL="/&lt;vilib&gt;/Rock Robotics/Network Tables/NT Write Variant.vi"/>
				<Item Name="NT_Create Actual Table Name.vi" Type="VI" URL="/&lt;vilib&gt;/Rock Robotics/Network Tables/NT_Create Actual Table Name.vi"/>
				<Item Name="NT_CreateInstance.vi" Type="VI" URL="/&lt;vilib&gt;/Rock Robotics/Network Tables/NT_CreateInstance.vi"/>
				<Item Name="NT_LL_Read Boolean Array.vi" Type="VI" URL="/&lt;vilib&gt;/Rock Robotics/Network Tables/NT_LL_Read Boolean Array.vi"/>
				<Item Name="NT_LL_Read Boolean.vi" Type="VI" URL="/&lt;vilib&gt;/Rock Robotics/Network Tables/NT_LL_Read Boolean.vi"/>
				<Item Name="NT_LL_Read Float Array.vi" Type="VI" URL="/&lt;vilib&gt;/Rock Robotics/Network Tables/NT_LL_Read Float Array.vi"/>
				<Item Name="NT_LL_Read Float.vi" Type="VI" URL="/&lt;vilib&gt;/Rock Robotics/Network Tables/NT_LL_Read Float.vi"/>
				<Item Name="NT_LL_Read Int Array.vi" Type="VI" URL="/&lt;vilib&gt;/Rock Robotics/Network Tables/NT_LL_Read Int Array.vi"/>
				<Item Name="NT_LL_Read Integer.vi" Type="VI" URL="/&lt;vilib&gt;/Rock Robotics/Network Tables/NT_LL_Read Integer.vi"/>
				<Item Name="NT_LL_Read Number.vi" Type="VI" URL="/&lt;vilib&gt;/Rock Robotics/Network Tables/NT_LL_Read Number.vi"/>
				<Item Name="NT_LL_Read Numeric Array.vi" Type="VI" URL="/&lt;vilib&gt;/Rock Robotics/Network Tables/NT_LL_Read Numeric Array.vi"/>
				<Item Name="NT_LL_Read Raw.vi" Type="VI" URL="/&lt;vilib&gt;/Rock Robotics/Network Tables/NT_LL_Read Raw.vi"/>
				<Item Name="NT_LL_Read String Array.vi" Type="VI" URL="/&lt;vilib&gt;/Rock Robotics/Network Tables/NT_LL_Read String Array.vi"/>
				<Item Name="NT_LL_Read String.vi" Type="VI" URL="/&lt;vilib&gt;/Rock Robotics/Network Tables/NT_LL_Read String.vi"/>
				<Item Name="NT_LL_Write Boolean Array.vi" Type="VI" URL="/&lt;vilib&gt;/Rock Robotics/Network Tables/NT_LL_Write Boolean Array.vi"/>
				<Item Name="NT_LL_Write Boolean.vi" Type="VI" URL="/&lt;vilib&gt;/Rock Robotics/Network Tables/NT_LL_Write Boolean.vi"/>
				<Item Name="NT_LL_Write Float Array.vi" Type="VI" URL="/&lt;vilib&gt;/Rock Robotics/Network Tables/NT_LL_Write Float Array.vi"/>
				<Item Name="NT_LL_Write Float.vi" Type="VI" URL="/&lt;vilib&gt;/Rock Robotics/Network Tables/NT_LL_Write Float.vi"/>
				<Item Name="NT_LL_Write Integer.vi" Type="VI" URL="/&lt;vilib&gt;/Rock Robotics/Network Tables/NT_LL_Write Integer.vi"/>
				<Item Name="NT_LL_Write IntegerArray.vi" Type="VI" URL="/&lt;vilib&gt;/Rock Robotics/Network Tables/NT_LL_Write IntegerArray.vi"/>
				<Item Name="NT_LL_Write Number.vi" Type="VI" URL="/&lt;vilib&gt;/Rock Robotics/Network Tables/NT_LL_Write Number.vi"/>
				<Item Name="NT_LL_Write Numeric Array.vi" Type="VI" URL="/&lt;vilib&gt;/Rock Robotics/Network Tables/NT_LL_Write Numeric Array.vi"/>
				<Item Name="NT_LL_Write Raw.vi" Type="VI" URL="/&lt;vilib&gt;/Rock Robotics/Network Tables/NT_LL_Write Raw.vi"/>
				<Item Name="NT_LL_Write String Array.vi" Type="VI" URL="/&lt;vilib&gt;/Rock Robotics/Network Tables/NT_LL_Write String Array.vi"/>
				<Item Name="NT_LL_Write String.vi" Type="VI" URL="/&lt;vilib&gt;/Rock Robotics/Network Tables/NT_LL_Write String.vi"/>
				<Item Name="Obtain Semaphore Reference.vi" Type="VI" URL="/&lt;vilib&gt;/Utility/semaphor.llb/Obtain Semaphore Reference.vi"/>
				<Item Name="Release Semaphore.vi" Type="VI" URL="/&lt;vilib&gt;/Utility/semaphor.llb/Release Semaphore.vi"/>
				<Item Name="roboRIO_FPGA_2023_23.0.0.lvbitx" Type="Document" URL="/&lt;vilib&gt;/Rock Robotics/SystemInterfaces/roboRIO_FPGA_2023_23.0.0.lvbitx"/>
				<Item Name="Semaphore RefNum" Type="VI" URL="/&lt;vilib&gt;/Utility/semaphor.llb/Semaphore RefNum"/>
				<Item Name="Semaphore Refnum Core.ctl" Type="VI" URL="/&lt;vilib&gt;/Utility/semaphor.llb/Semaphore Refnum Core.ctl"/>
				<Item Name="System Exec.vi" Type="VI" URL="/&lt;vilib&gt;/Platform/system.llb/System Exec.vi"/>
				<Item Name="Trajectory_Library.lvlib" Type="Library" URL="/&lt;vilib&gt;/Rock Robotics/WPI/ThirdParty/JAS_Junk/TrajLib/Trajectory_Library.lvlib"/>
				<Item Name="Trim Whitespace One-Sided.vi" Type="VI" URL="/&lt;vilib&gt;/Utility/error.llb/Trim Whitespace One-Sided.vi"/>
				<Item Name="Trim Whitespace.vi" Type="VI" URL="/&lt;vilib&gt;/Utility/error.llb/Trim Whitespace.vi"/>
				<Item Name="Validate Semaphore Size.vi" Type="VI" URL="/&lt;vilib&gt;/Utility/semaphor.llb/Validate Semaphore Size.vi"/>
				<Item Name="whitespace.ctl" Type="VI" URL="/&lt;vilib&gt;/Utility/error.llb/whitespace.ctl"/>
				<Item Name="WPI_UtilitiesFRC Build Error.vi" Type="VI" URL="/&lt;vilib&gt;/Rock Robotics/WPI/Utilities/WPI_UtilitiesFRC Build Error.vi"/>
			</Item>
			<Item Name="ActDriveStraight.vi" Type="VI" URL="../../2025ReefBot/Actions/ActDriveStraight.vi"/>
			<Item Name="ActDriveStraightCalcError-ALT.vi" Type="VI" URL="../../2025ReefBot/Actions/ActDriveStraightCalcError-ALT.vi"/>
			<Item Name="ActTurn.vi" Type="VI" URL="../../2025ReefBot/Actions/ActTurn.vi"/>
			<Item Name="AutoDriveStraight.vi" Type="VI" URL="../../2025ReefBot/Autonomous/AutoDriveStraight.vi"/>
			<Item Name="AutoElevReadyWait.vi" Type="VI" URL="../../2025ReefBot/Autonomous/AutoElevReadyWait.vi"/>
			<Item Name="AutoFollowAbsoluteTrajectoryNEW.vi" Type="VI" URL="../../2025ReefBot/Autonomous/AutoFollowAbsoluteTrajectoryNEW.vi"/>
			<Item Name="AutoFollowAbsoluteTrajectoryNEWCommandInterrupt.vi" Type="VI" URL="../../2025ReefBot/Autonomous/AutoFollowAbsoluteTrajectoryNEWCommandInterrupt.vi"/>
			<Item Name="AutoFollowRelativeTrajectoryNEW.vi" Type="VI" URL="../../2025ReefBot/Autonomous/AutoFollowRelativeTrajectoryNEW.vi"/>
			<Item Name="AutoFollowRelativeTrajectoryNEWCommandInterupt.vi" Type="VI" URL="../../2025ReefBot/Autonomous/AutoFollowRelativeTrajectoryNEWCommandInterupt.vi"/>
			<Item Name="AutoIntakeReadyWait.vi" Type="VI" URL="../../2025ReefBot/Autonomous/AutoIntakeReadyWait.vi"/>
			<Item Name="AutoTurn.vi" Type="VI" URL="../../2025ReefBot/Autonomous/AutoTurn.vi"/>
			<Item Name="AutoWait.vi" Type="VI" URL="../../2025ReefBot/Autonomous/AutoWait.vi"/>
			<Item Name="DriveGetDist.vi" Type="VI" URL="../../2025ReefBot/Drive/DriveGetDist.vi"/>
			<Item Name="DriveGetModulePositions.vi" Type="VI" URL="../../2025ReefBot/Drive/DriveGetModulePositions.vi"/>
			<Item Name="DriveGetSwerveKinematics.vi" Type="VI" URL="../../2025ReefBot/Drive/DriveGetSwerveKinematics.vi"/>
			<Item Name="DriveGetYawRaw.vi" Type="VI" URL="../../2025ReefBot/Drive/DriveGetYawRaw.vi"/>
			<Item Name="DriveGlobal.vi" Type="VI" URL="../../2025ReefBot/Drive/DriveGlobal.vi"/>
			<Item Name="DriveSetSpeedDemand.vi" Type="VI" URL="../../2025ReefBot/Drive/DriveSetSpeedDemand.vi"/>
			<Item Name="ElevatorAlgae1SendCmd.vi" Type="VI" URL="../../2025ReefBot/Elevator/ElevatorAlgae1SendCmd.vi"/>
			<Item Name="ElevatorAlgae2SendCmd.vi" Type="VI" URL="../../2025ReefBot/Elevator/ElevatorAlgae2SendCmd.vi"/>
			<Item Name="ElevatorAtSetpoint.vi" Type="VI" URL="../../2025ReefBot/Elevator/ElevatorAtSetpoint.vi"/>
			<Item Name="ElevatorBargeSendCmd.vi" Type="VI" URL="../../2025ReefBot/Elevator/ElevatorBargeSendCmd.vi"/>
			<Item Name="ElevatorCancelCmd.vi" Type="VI" URL="../../2025ReefBot/Elevator/ElevatorCancelCmd.vi"/>
			<Item Name="ElevatorFloorSendCmd.vi" Type="VI" URL="../../2025ReefBot/Elevator/ElevatorFloorSendCmd.vi"/>
			<Item Name="ElevatorGetPosition.vi" Type="VI" URL="../../2025ReefBot/Elevator/ElevatorGetPosition.vi"/>
			<Item Name="ElevatorGlobal.vi" Type="VI" URL="../../2025ReefBot/Elevator/ElevatorGlobal.vi"/>
			<Item Name="ElevatorL1SendCmd.vi" Type="VI" URL="../../2025ReefBot/Elevator/ElevatorL1SendCmd.vi"/>
			<Item Name="ElevatorL2SendCmd.vi" Type="VI" URL="../../2025ReefBot/Elevator/ElevatorL2SendCmd.vi"/>
			<Item Name="ElevatorL3SendCmd.vi" Type="VI" URL="../../2025ReefBot/Elevator/ElevatorL3SendCmd.vi"/>
			<Item Name="ElevatorL4SendCmd.vi" Type="VI" URL="../../2025ReefBot/Elevator/ElevatorL4SendCmd.vi"/>
			<Item Name="ElevatorRestSendCmd.vi" Type="VI" URL="../../2025ReefBot/Elevator/ElevatorRestSendCmd.vi"/>
			<Item Name="ElevatorSetpointEnum.ctl" Type="VI" URL="../../2025ReefBot/Elevator/ElevatorSetpointEnum.ctl"/>
			<Item Name="ElevatorSetpoints.vi" Type="VI" URL="../../2025ReefBot/Elevator/ElevatorSetpoints.vi"/>
			<Item Name="ElevatorSourceSendCmd.vi" Type="VI" URL="../../2025ReefBot/Elevator/ElevatorSourceSendCmd.vi"/>
			<Item Name="FRC_NetworkTablesLV.dll" Type="Document" URL="FRC_NetworkTablesLV.dll">
				<Property Name="NI.PreserveRelativePath" Type="Bool">true</Property>
			</Item>
			<Item Name="IntakeAlgaeSendCmd.vi" Type="VI" URL="../../2025ReefBot/Intake/IntakeAlgaeSendCmd.vi"/>
			<Item Name="IntakeCancelSendCmd.vi" Type="VI" URL="../../2025ReefBot/Intake/IntakeCancelSendCmd.vi"/>
			<Item Name="IntakeCoralSendCmd.vi" Type="VI" URL="../../2025ReefBot/Intake/IntakeCoralSendCmd.vi"/>
			<Item Name="IntakeDepositAlgaeSendCmd.vi" Type="VI" URL="../../2025ReefBot/Intake/IntakeDepositAlgaeSendCmd.vi"/>
			<Item Name="IntakeDepositCoralSendCmd.vi" Type="VI" URL="../../2025ReefBot/Intake/IntakeDepositCoralSendCmd.vi"/>
			<Item Name="IntakeGlobal.vi" Type="VI" URL="../../2025ReefBot/Intake/IntakeGlobal.vi"/>
			<Item Name="IntakeReady.vi" Type="VI" URL="../../2025ReefBot/Intake/IntakeReady.vi"/>
			<Item Name="IntakeStates.ctl" Type="VI" URL="../../2025ReefBot/Intake/IntakeStates.ctl"/>
			<Item Name="lvanlys.dll" Type="Document" URL="/&lt;resource&gt;/lvanlys.dll"/>
			<Item Name="NiFpgaLv.dll" Type="Document" URL="NiFpgaLv.dll">
				<Property Name="NI.PreserveRelativePath" Type="Bool">true</Property>
			</Item>
			<Item Name="NT4150_Write_Boolean.vi" Type="VI" URL="../../2025ReefBot/NT_Helper/NT4150_Write_Boolean.vi"/>
			<Item Name="NT4150_Write_Integer.vi" Type="VI" URL="../../2025ReefBot/NT_Helper/NT4150_Write_Integer.vi"/>
			<Item Name="NT4150_Write_Number.vi" Type="VI" URL="../../2025ReefBot/NT_Helper/NT4150_Write_Number.vi"/>
			<Item Name="ntcoreffi.dll" Type="Document" URL="ntcoreffi.dll">
				<Property Name="NI.PreserveRelativePath" Type="Bool">true</Property>
			</Item>
			<Item Name="PositionControl.vi" Type="VI" URL="../../2025ReefBot/GeneralPurpose/PositionControl.vi"/>
			<Item Name="TrajectoryCalculateOrientation.vi" Type="VI" URL="../../2025ReefBot/Trajectory/TrajectoryCalculateOrientation.vi"/>
			<Item Name="TrajectoryCommonTuning.vi" Type="VI" URL="../../2025ReefBot/Trajectory/TrajectoryCommonTuning.vi"/>
			<Item Name="TrajectoryExecuteAbsolute.vi" Type="VI" URL="../../2025ReefBot/Trajectory/TrajectoryExecuteAbsolute.vi"/>
			<Item Name="TrajectoryExecuteAbsoluteInterrupt.vi" Type="VI" URL="../../2025ReefBot/Trajectory/TrajectoryExecuteAbsoluteInterrupt.vi"/>
			<Item Name="TrajectoryExecuteRelative.vi" Type="VI" URL="../../2025ReefBot/Trajectory/TrajectoryExecuteRelative.vi"/>
			<Item Name="TrajectoryExecuteRelativeInterrupt.vi" Type="VI" URL="../../2025ReefBot/Trajectory/TrajectoryExecuteRelativeInterrupt.vi"/>
			<Item Name="TrajectoryGetClosedLoop.vi" Type="VI" URL="../../2025ReefBot/Trajectory/TrajectoryGetClosedLoop.vi"/>
			<Item Name="TrajectoryGlobals.vi" Type="VI" URL="../../2025ReefBot/Trajectory/TrajectoryGlobals.vi"/>
			<Item Name="TrajectoryIssueCmd.vi" Type="VI" URL="../../2025ReefBot/Trajectory/TrajectoryIssueCmd.vi"/>
			<Item Name="VisionGetCurrentAbsPose.vi" Type="VI" URL="../../2025ReefBot/Vision/VisionGetCurrentAbsPose.vi"/>
			<Item Name="VisionGlobal.vi" Type="VI" URL="../../2025ReefBot/Vision/VisionGlobal.vi"/>
			<Item Name="WristAtSetpoint.vi" Type="VI" URL="../../2025ReefBot/Wrist/WristAtSetpoint.vi"/>
			<Item Name="WristCancelSendCmd.vi" Type="VI" URL="../../2025ReefBot/Wrist/WristCancelSendCmd.vi"/>
			<Item Name="WristGlobal.vi" Type="VI" URL="../../2025ReefBot/Wrist/WristGlobal.vi"/>
		</Item>
		<Item Name="Build Specifications" Type="Build">
			<Item Name="AutoEditor" Type="EXE">
				<Property Name="App_copyErrors" Type="Bool">true</Property>
				<Property Name="App_INI_aliasGUID" Type="Str">{50AE2B12-4EF7-49CF-8592-40D8B6F64F4D}</Property>
				<Property Name="App_INI_GUID" Type="Str">{FBFC01BF-25B1-4F2B-A8E0-5B5C6A0EE897}</Property>
				<Property Name="App_serverConfig.httpPort" Type="Int">8002</Property>
				<Property Name="App_serverType" Type="Int">0</Property>
				<Property Name="Bld_autoIncrement" Type="Bool">true</Property>
				<Property Name="Bld_buildCacheID" Type="Str">{91E451D4-21FD-4533-BAB1-6A43BA4D4712}</Property>
				<Property Name="Bld_buildSpecName" Type="Str">AutoEditor</Property>
				<Property Name="Bld_excludeInlineSubVIs" Type="Bool">true</Property>
				<Property Name="Bld_excludeLibraryItems" Type="Bool">true</Property>
				<Property Name="Bld_excludePolymorphicVIs" Type="Bool">true</Property>
				<Property Name="Bld_localDestDir" Type="Path">../builds/NI_AB_PROJECTNAME/AutoEditor</Property>
				<Property Name="Bld_localDestDirType" Type="Str">relativeToCommon</Property>
				<Property Name="Bld_modifyLibraryFile" Type="Bool">true</Property>
				<Property Name="Bld_previewCacheID" Type="Str">{3F907593-39FA-48D1-934B-B871C6E23A60}</Property>
				<Property Name="Bld_version.build" Type="Int">15</Property>
				<Property Name="Bld_version.major" Type="Int">1</Property>
				<Property Name="Destination[0].destName" Type="Str">AutoEditor.exe</Property>
				<Property Name="Destination[0].path" Type="Path">../builds/NI_AB_PROJECTNAME/AutoEditor/AutoEditor.exe</Property>
				<Property Name="Destination[0].preserveHierarchy" Type="Bool">true</Property>
				<Property Name="Destination[0].type" Type="Str">App</Property>
				<Property Name="Destination[1].destName" Type="Str">Support Directory</Property>
				<Property Name="Destination[1].path" Type="Path">../builds/NI_AB_PROJECTNAME/AutoEditor/data</Property>
				<Property Name="Destination[2].destName" Type="Str">BaseExecutableDir</Property>
				<Property Name="Destination[2].path" Type="Path">../builds/NI_AB_PROJECTNAME/AutoEditor</Property>
				<Property Name="DestinationCount" Type="Int">3</Property>
				<Property Name="Exe_iconItemID" Type="Ref">/My Computer/AutoEditor.ico</Property>
				<Property Name="Source[0].itemID" Type="Str">{134BFDD1-B518-424D-B97D-934BFFC19DCB}</Property>
				<Property Name="Source[0].type" Type="Str">Container</Property>
				<Property Name="Source[1].destinationIndex" Type="Int">0</Property>
				<Property Name="Source[1].itemID" Type="Ref">/My Computer/EditorInterface.vi</Property>
				<Property Name="Source[1].sourceInclusion" Type="Str">TopLevel</Property>
				<Property Name="Source[1].type" Type="Str">VI</Property>
				<Property Name="Source[2].destinationIndex" Type="Int">2</Property>
				<Property Name="Source[2].itemID" Type="Ref">/My Computer/NewAuto/New_Auto_Notes.txt</Property>
				<Property Name="Source[2].sourceInclusion" Type="Str">Include</Property>
				<Property Name="SourceCount" Type="Int">3</Property>
				<Property Name="TgtF_companyName" Type="Str">FRC4150</Property>
				<Property Name="TgtF_fileDescription" Type="Str">AutoEditor</Property>
				<Property Name="TgtF_internalName" Type="Str">AutoEditor</Property>
				<Property Name="TgtF_legalCopyright" Type="Str">Copyright © 2025 </Property>
				<Property Name="TgtF_productName" Type="Str">AutoEditor</Property>
				<Property Name="TgtF_targetfileGUID" Type="Str">{C37907A8-396A-4813-B650-CC30DA3202E4}</Property>
				<Property Name="TgtF_targetfileName" Type="Str">AutoEditor.exe</Property>
				<Property Name="TgtF_versionIndependent" Type="Bool">true</Property>
			</Item>
			<Item Name="AutoEditorSetup" Type="Installer">
				<Property Name="Destination[0].name" Type="Str">Frobotics4150</Property>
				<Property Name="Destination[0].parent" Type="Str">{3912416A-D2E5-411B-AFEE-B63654D690C0}</Property>
				<Property Name="Destination[0].tag" Type="Str">{295A43CA-500E-423A-9BD2-29AB3A5DA54F}</Property>
				<Property Name="Destination[0].type" Type="Str">userFolder</Property>
				<Property Name="DestinationCount" Type="Int">1</Property>
				<Property Name="DistPart[0].flavorID" Type="Str">DefaultFull</Property>
				<Property Name="DistPart[0].productID" Type="Str">{16386956-3E43-4680-9DDB-64A2AEAA501D}</Property>
				<Property Name="DistPart[0].productName" Type="Str">NI LabVIEW Runtime 2023 Q3 Patch 5</Property>
				<Property Name="DistPart[0].SoftDep[0].exclude" Type="Bool">false</Property>
				<Property Name="DistPart[0].SoftDep[0].productName" Type="Str">NI ActiveX Container</Property>
				<Property Name="DistPart[0].SoftDep[0].upgradeCode" Type="Str">{1038A887-23E1-4289-B0BD-0C4B83C6BA21}</Property>
				<Property Name="DistPart[0].SoftDep[1].exclude" Type="Bool">false</Property>
				<Property Name="DistPart[0].SoftDep[1].productName" Type="Str">NI Deployment Framework 2023</Property>
				<Property Name="DistPart[0].SoftDep[1].upgradeCode" Type="Str">{838942E4-B73C-492E-81A3-AA1E291FD0DC}</Property>
				<Property Name="DistPart[0].SoftDep[10].exclude" Type="Bool">false</Property>
				<Property Name="DistPart[0].SoftDep[10].productName" Type="Str">NI VC2015 Runtime</Property>
				<Property Name="DistPart[0].SoftDep[10].upgradeCode" Type="Str">{D42E7BAE-6589-4570-B6A3-3E28889392E7}</Property>
				<Property Name="DistPart[0].SoftDep[11].exclude" Type="Bool">false</Property>
				<Property Name="DistPart[0].SoftDep[11].productName" Type="Str">NI TDM Streaming 23.3</Property>
				<Property Name="DistPart[0].SoftDep[11].upgradeCode" Type="Str">{4CD11BE6-6BB7-4082-8A27-C13771BC309B}</Property>
				<Property Name="DistPart[0].SoftDep[2].exclude" Type="Bool">false</Property>
				<Property Name="DistPart[0].SoftDep[2].productName" Type="Str">NI Error Reporting 2020</Property>
				<Property Name="DistPart[0].SoftDep[2].upgradeCode" Type="Str">{42E818C6-2B08-4DE7-BD91-B0FD704C119A}</Property>
				<Property Name="DistPart[0].SoftDep[3].exclude" Type="Bool">false</Property>
				<Property Name="DistPart[0].SoftDep[3].productName" Type="Str">NI LabVIEW Real-Time NBFifo 2023</Property>
				<Property Name="DistPart[0].SoftDep[3].upgradeCode" Type="Str">{4916D413-AC43-3010-9B66-301D33EA43AC}</Property>
				<Property Name="DistPart[0].SoftDep[4].exclude" Type="Bool">false</Property>
				<Property Name="DistPart[0].SoftDep[4].productName" Type="Str">NI LabVIEW Runtime 2023 Q3 Non-English Support.</Property>
				<Property Name="DistPart[0].SoftDep[4].upgradeCode" Type="Str">{4C71F057-4B64-3691-A123-E064BF263A9B}</Property>
				<Property Name="DistPart[0].SoftDep[5].exclude" Type="Bool">false</Property>
				<Property Name="DistPart[0].SoftDep[5].productName" Type="Str">NI Logos 23.3</Property>
				<Property Name="DistPart[0].SoftDep[5].upgradeCode" Type="Str">{5E4A4CE3-4D06-11D4-8B22-006008C16337}</Property>
				<Property Name="DistPart[0].SoftDep[6].exclude" Type="Bool">false</Property>
				<Property Name="DistPart[0].SoftDep[6].productName" Type="Str">NI LabVIEW Web Server 2023</Property>
				<Property Name="DistPart[0].SoftDep[6].upgradeCode" Type="Str">{0960380B-EA86-4E0C-8B57-14CD8CCF2C15}</Property>
				<Property Name="DistPart[0].SoftDep[7].exclude" Type="Bool">false</Property>
				<Property Name="DistPart[0].SoftDep[7].productName" Type="Str">NI mDNS Responder 23.5</Property>
				<Property Name="DistPart[0].SoftDep[7].upgradeCode" Type="Str">{9607874B-4BB3-42CB-B450-A2F5EF60BA3B}</Property>
				<Property Name="DistPart[0].SoftDep[8].exclude" Type="Bool">false</Property>
				<Property Name="DistPart[0].SoftDep[8].productName" Type="Str">Math Kernel Libraries 2017</Property>
				<Property Name="DistPart[0].SoftDep[8].upgradeCode" Type="Str">{699C1AC5-2CF2-4745-9674-B19536EBA8A3}</Property>
				<Property Name="DistPart[0].SoftDep[9].exclude" Type="Bool">false</Property>
				<Property Name="DistPart[0].SoftDep[9].productName" Type="Str">Math Kernel Libraries 2020</Property>
				<Property Name="DistPart[0].SoftDep[9].upgradeCode" Type="Str">{9872BBBA-FB96-42A4-80A2-9605AC5CBCF1}</Property>
				<Property Name="DistPart[0].SoftDepCount" Type="Int">12</Property>
				<Property Name="DistPart[0].upgradeCode" Type="Str">{A3DD8CEA-07BB-3EB5-A026-4AB75BDFF807}</Property>
				<Property Name="DistPartCount" Type="Int">1</Property>
				<Property Name="INST_author" Type="Str">FRC 4150</Property>
				<Property Name="INST_autoIncrement" Type="Bool">true</Property>
				<Property Name="INST_buildLocation" Type="Path">../builds/ExternalAutoEditor/AutoEditorSetup</Property>
				<Property Name="INST_buildLocation.type" Type="Str">relativeToCommon</Property>
				<Property Name="INST_buildSpecName" Type="Str">AutoEditorSetup</Property>
				<Property Name="INST_defaultDir" Type="Str">{295A43CA-500E-423A-9BD2-29AB3A5DA54F}</Property>
				<Property Name="INST_installerName" Type="Str">AutoEditorSetup</Property>
				<Property Name="INST_productName" Type="Str">AutoEditor</Property>
				<Property Name="INST_productVersion" Type="Str">1.0.11</Property>
				<Property Name="InstSpecBitness" Type="Str">32-bit</Property>
				<Property Name="InstSpecVersion" Type="Str">23308000</Property>
				<Property Name="MSI_arpCompany" Type="Str">FRC 4150</Property>
				<Property Name="MSI_autoselectDrivers" Type="Bool">true</Property>
				<Property Name="MSI_distID" Type="Str">{481E3305-3429-44F0-8500-BE93ACAAB1D4}</Property>
				<Property Name="MSI_hideNonRuntimes" Type="Bool">true</Property>
				<Property Name="MSI_osCheck" Type="Int">0</Property>
				<Property Name="MSI_upgradeCode" Type="Str">{6019F0ED-711F-4BAD-8B88-38D165B0BD30}</Property>
				<Property Name="MSI_windowMessage" Type="Str">This will install the Frobotics 4150 Auto Editor to this computer.  The program can be launched from the Start Menu.  A short cut will be placed on the desktop.</Property>
				<Property Name="MSI_windowTitle" Type="Str">Frobotics 4150 Auto Editor Install</Property>
				<Property Name="RegDest[0].dirName" Type="Str">Software</Property>
				<Property Name="RegDest[0].dirTag" Type="Str">{DDFAFC8B-E728-4AC8-96DE-B920EBB97A86}</Property>
				<Property Name="RegDest[0].parentTag" Type="Str">2</Property>
				<Property Name="RegDestCount" Type="Int">1</Property>
				<Property Name="Source[0].dest" Type="Str">{295A43CA-500E-423A-9BD2-29AB3A5DA54F}</Property>
				<Property Name="Source[0].File[0].dest" Type="Str">{295A43CA-500E-423A-9BD2-29AB3A5DA54F}</Property>
				<Property Name="Source[0].File[0].name" Type="Str">AutoEditor.exe</Property>
				<Property Name="Source[0].File[0].Shortcut[0].destIndex" Type="Int">2</Property>
				<Property Name="Source[0].File[0].Shortcut[0].name" Type="Str">AutoEditor</Property>
				<Property Name="Source[0].File[0].Shortcut[0].subDir" Type="Str">Frobotics4150</Property>
				<Property Name="Source[0].File[0].Shortcut[1].destIndex" Type="Int">1</Property>
				<Property Name="Source[0].File[0].Shortcut[1].name" Type="Str">AutoEditor</Property>
				<Property Name="Source[0].File[0].Shortcut[1].subDir" Type="Str"></Property>
				<Property Name="Source[0].File[0].ShortcutCount" Type="Int">2</Property>
				<Property Name="Source[0].File[0].tag" Type="Str">{C37907A8-396A-4813-B650-CC30DA3202E4}</Property>
				<Property Name="Source[0].FileCount" Type="Int">1</Property>
				<Property Name="Source[0].name" Type="Str">AutoEditor</Property>
				<Property Name="Source[0].tag" Type="Ref">/My Computer/Build Specifications/AutoEditor</Property>
				<Property Name="Source[0].type" Type="Str">EXE</Property>
				<Property Name="SourceCount" Type="Int">1</Property>
			</Item>
		</Item>
	</Item>
</Project>
