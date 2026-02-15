@echo off

REM -------- copy to final location for driver station to use

xcopy  /S ..\builds\FRC_DASHBOARD\*.* "%HOMEDRIVE%%HOMEPATH%\Documents\LabVIEW Data\builds\FRC_Dashboard\*.*"


pause

