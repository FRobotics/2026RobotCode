<?xml version='1.0' encoding='UTF-8'?>
<Project Type="Project" LVVersion="25008000">
	<Item Name="My Computer" Type="My Computer">
		<Property Name="NI.SortType" Type="Int">3</Property>
		<Property Name="server.app.propertiesEnabled" Type="Bool">true</Property>
		<Property Name="server.control.propertiesEnabled" Type="Bool">true</Property>
		<Property Name="server.tcp.enabled" Type="Bool">false</Property>
		<Property Name="server.tcp.port" Type="Int">0</Property>
		<Property Name="server.tcp.serviceName" Type="Str">My Computer/VI Server</Property>
		<Property Name="server.tcp.serviceName.default" Type="Str">My Computer/VI Server</Property>
		<Property Name="server.vi.callsEnabled" Type="Bool">true</Property>
		<Property Name="server.vi.propertiesEnabled" Type="Bool">true</Property>
		<Property Name="specify.custom.address" Type="Bool">false</Property>
		<Item Name="Support" Type="Folder">
			<Item Name="App EXE.ico" Type="Document" URL="../App EXE.ico"/>
			<Item Name="Panel Resized.vi" Type="VI" URL="../Panel Resized.vi"/>
			<Item Name="DashBoardGlobals.vi" Type="VI" URL="../DashBoardGlobals.vi"/>
		</Item>
		<Item Name="MissingSupportVI" Type="Folder">
			<Item Name="Prepare Joystick Data for Displays.vi" Type="VI" URL="../Prepare Joystick Data for Displays.vi"/>
			<Item Name="Interpolate RGB Color.vi" Type="VI" URL="../Interpolate RGB Color.vi"/>
			<Item Name="Playback Controls.vi" Type="VI" URL="../Playback Controls.vi"/>
			<Item Name="Open Playback Panel.vi" Type="VI" URL="../Open Playback Panel.vi"/>
			<Item Name="Initialize CheckList.vi" Type="VI" URL="../Initialize CheckList.vi"/>
			<Item Name="Handle Camera Configuration.vi" Type="VI" URL="../Handle Camera Configuration.vi"/>
		</Item>
		<Item Name="FixedSizeVI" Type="Folder">
			<Item Name="Adjust Dashboard Window_NO_RESIZE.vi" Type="VI" URL="../Adjust Dashboard Window_NO_RESIZE.vi"/>
		</Item>
		<Item Name="SystemPanels" Type="Folder">
			<Property Name="NI.SortType" Type="Int">0</Property>
			<Item Name="AgitatorSystem.vi" Type="VI" URL="../AgitatorSystem.vi"/>
			<Item Name="AutoSystem.vi" Type="VI" URL="../AutoSystem.vi"/>
			<Item Name="ClimbSystem.vi" Type="VI" URL="../ClimbSystem.vi"/>
			<Item Name="DriveSystem.vi" Type="VI" URL="../DriveSystem.vi"/>
			<Item Name="FeederSystem.vi" Type="VI" URL="../FeederSystem.vi"/>
			<Item Name="IntakeSystem.vi" Type="VI" URL="../IntakeSystem.vi"/>
			<Item Name="LauncherSystem.vi" Type="VI" URL="../LauncherSystem.vi"/>
			<Item Name="OdometrySystem.vi" Type="VI" URL="../OdometrySystem.vi"/>
			<Item Name="SupervisorySystem.vi" Type="VI" URL="../SupervisorySystem.vi"/>
			<Item Name="TeleopSystem.vi" Type="VI" URL="../TeleopSystem.vi"/>
			<Item Name="TrajectorySystem.vi" Type="VI" URL="../TrajectorySystem.vi"/>
			<Item Name="TurretSystem.vi" Type="VI" URL="../TurretSystem.vi"/>
			<Item Name="VisionSystem.vi" Type="VI" URL="../VisionSystem.vi"/>
		</Item>
		<Item Name="FieldElements" Type="Folder">
			<Item Name="SmallDot.png" Type="Document" URL="../SmallDot.png"/>
			<Item Name="Robot_kitbot_5.png" Type="Document" URL="../Robot_kitbot_5.png"/>
			<Item Name="Robot_kitbot_4.png" Type="Document" URL="../Robot_kitbot_4.png"/>
		</Item>
		<Item Name="FieldInfo" Type="Folder" URL="../FieldInfo">
			<Property Name="NI.DISK" Type="Bool">true</Property>
		</Item>
		<Item Name="VIsualControls" Type="Folder">
			<Item Name="IntakeControl.xctl" Type="XControl" URL="../VisualControls/Intake/IntakeControl.xctl"/>
		</Item>
		<Item Name="Windows" Type="Folder">
			<Item Name="DriveModuleTrend.vi" Type="VI" URL="../DriveModuleTrend.vi"/>
			<Item Name="FieldDisplaySmall.vi" Type="VI" URL="../FieldDisplaySmall.vi"/>
			<Item Name="TrajectoryTrend.vi" Type="VI" URL="../TrajectoryTrend.vi"/>
			<Item Name="LauncherTrend.vi" Type="VI" URL="../LauncherTrend.vi"/>
			<Item Name="FeederTrend.vi" Type="VI" URL="../FeederTrend.vi"/>
		</Item>
		<Item Name="EnhancedWPI" Type="Folder" URL="../EnhancedWPI">
			<Property Name="NI.DISK" Type="Bool">true</Property>
		</Item>
		<Item Name="Checklist" Type="Folder" URL="../Checklist">
			<Property Name="NI.DISK" Type="Bool">true</Property>
		</Item>
		<Item Name="Dashboard Main1920x1080fixed.vi" Type="VI" URL="../Dashboard Main1920x1080fixed.vi"/>
		<Item Name="subpanel_sizes.txt" Type="Document" URL="../subpanel_sizes.txt"/>
		<Item Name="CopyExeToFinalLocation.bat" Type="Document" URL="../CopyExeToFinalLocation.bat"/>
		<Item Name="Dependencies" Type="Dependencies">
			<Property Name="NI.SortType" Type="Int">0</Property>
		</Item>
		<Item Name="Build Specifications" Type="Build">
			<Item Name="FRC_Dashboard1920x1080" Type="EXE">
				<Property Name="App_INI_aliasGUID" Type="Str">{016D75AA-063D-4595-8743-2CBE0BD28D71}</Property>
				<Property Name="App_INI_GUID" Type="Str">{4D5C88B0-9693-4065-9758-4FCE7439AB43}</Property>
				<Property Name="App_serverConfig.httpPort" Type="Int">8002</Property>
				<Property Name="App_serverType" Type="Int">1</Property>
				<Property Name="Bld_autoIncrement" Type="Bool">true</Property>
				<Property Name="Bld_buildCacheID" Type="Str">{1B338B96-4300-47E3-9CD6-444DBB44C2CA}</Property>
				<Property Name="Bld_buildSpecDescription" Type="Str">Build Dashboard Main.vi into an EXE that will respond to the driver station and display robot information on a PC.</Property>
				<Property Name="Bld_buildSpecName" Type="Str">FRC_Dashboard1920x1080</Property>
				<Property Name="Bld_excludeLibraryItems" Type="Bool">true</Property>
				<Property Name="Bld_excludePolymorphicVIs" Type="Bool">true</Property>
				<Property Name="Bld_localDestDir" Type="Path">../builds/FRC_Dashboard</Property>
				<Property Name="Bld_localDestDirType" Type="Str">relativeToCommon</Property>
				<Property Name="Bld_modifyLibraryFile" Type="Bool">true</Property>
				<Property Name="Bld_previewCacheID" Type="Str">{97A92A64-867E-4C0B-9013-521F62B9881E}</Property>
				<Property Name="Bld_version.build" Type="Int">64</Property>
				<Property Name="Bld_version.major" Type="Int">17</Property>
				<Property Name="Bld_version.patch" Type="Int">1</Property>
				<Property Name="Destination[0].destName" Type="Str">Dashboard.exe</Property>
				<Property Name="Destination[0].path" Type="Path">../builds/FRC_Dashboard/Dashboard.exe</Property>
				<Property Name="Destination[0].preserveHierarchy" Type="Bool">true</Property>
				<Property Name="Destination[0].type" Type="Str">App</Property>
				<Property Name="Destination[1].destName" Type="Str">Support Directory</Property>
				<Property Name="Destination[1].path" Type="Path">../builds/FRC_Dashboard/data</Property>
				<Property Name="Destination[2].destName" Type="Str">FieldInfo</Property>
				<Property Name="Destination[2].path" Type="Path">../builds/FRC_Dashboard/FieldInfo</Property>
				<Property Name="Destination[2].preserveHierarchy" Type="Bool">true</Property>
				<Property Name="Destination[3].destName" Type="Str">Base</Property>
				<Property Name="Destination[3].path" Type="Path">../builds/FRC_Dashboard</Property>
				<Property Name="Destination[3].preserveHierarchy" Type="Bool">true</Property>
				<Property Name="DestinationCount" Type="Int">4</Property>
				<Property Name="Exe_iconItemID" Type="Ref">/My Computer/Support/App EXE.ico</Property>
				<Property Name="Source[0].itemID" Type="Str">{C06A13B7-5EB8-4C72-8AB4-1FF8BDD2D78F}</Property>
				<Property Name="Source[0].type" Type="Str">Container</Property>
				<Property Name="Source[1].destinationIndex" Type="Int">0</Property>
				<Property Name="Source[1].itemID" Type="Ref"></Property>
				<Property Name="Source[1].type" Type="Str">VI</Property>
				<Property Name="Source[2].destinationIndex" Type="Int">0</Property>
				<Property Name="Source[2].itemID" Type="Ref">/My Computer/Support/Panel Resized.vi</Property>
				<Property Name="Source[2].sourceInclusion" Type="Str">Include</Property>
				<Property Name="Source[2].type" Type="Str">VI</Property>
				<Property Name="Source[3].destinationIndex" Type="Int">0</Property>
				<Property Name="Source[3].itemID" Type="Ref"></Property>
				<Property Name="Source[3].sourceInclusion" Type="Str">Include</Property>
				<Property Name="Source[3].type" Type="Str">VI</Property>
				<Property Name="Source[4].destinationIndex" Type="Int">0</Property>
				<Property Name="Source[4].itemID" Type="Ref">/My Computer/Dashboard Main1920x1080fixed.vi</Property>
				<Property Name="Source[4].sourceInclusion" Type="Str">TopLevel</Property>
				<Property Name="Source[4].type" Type="Str">VI</Property>
				<Property Name="Source[5].Container.applyDestination" Type="Bool">true</Property>
				<Property Name="Source[5].Container.applyInclusion" Type="Bool">true</Property>
				<Property Name="Source[5].Container.depDestIndex" Type="Int">0</Property>
				<Property Name="Source[5].destinationIndex" Type="Int">2</Property>
				<Property Name="Source[5].itemID" Type="Ref">/My Computer/FieldInfo</Property>
				<Property Name="Source[5].sourceInclusion" Type="Str">Include</Property>
				<Property Name="Source[5].type" Type="Str">Container</Property>
				<Property Name="Source[6].destinationIndex" Type="Int">3</Property>
				<Property Name="Source[6].itemID" Type="Ref">/My Computer/FieldElements/SmallDot.png</Property>
				<Property Name="Source[6].sourceInclusion" Type="Str">Include</Property>
				<Property Name="Source[7].destinationIndex" Type="Int">3</Property>
				<Property Name="Source[7].itemID" Type="Ref">/My Computer/FieldElements/Robot_kitbot_5.png</Property>
				<Property Name="Source[7].sourceInclusion" Type="Str">Include</Property>
				<Property Name="Source[8].destinationIndex" Type="Int">3</Property>
				<Property Name="Source[8].itemID" Type="Ref">/My Computer/FieldElements/Robot_kitbot_4.png</Property>
				<Property Name="Source[8].sourceInclusion" Type="Str">Include</Property>
				<Property Name="SourceCount" Type="Int">9</Property>
				<Property Name="TgtF_fileDescription" Type="Str">FRC_Dashboard</Property>
				<Property Name="TgtF_internalName" Type="Str">FRC_Dashboard</Property>
				<Property Name="TgtF_targetfileGUID" Type="Str">{5E4E6760-0A78-428A-ABCD-98D797FDE190}</Property>
				<Property Name="TgtF_targetfileName" Type="Str">Dashboard.exe</Property>
			</Item>
		</Item>
	</Item>
</Project>
