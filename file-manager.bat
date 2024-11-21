@echo off
setlocal EnableDelayedExpansion

rem file-manager.bat

rem Get script directory
set "SCRIPT_DIR=%~dp0"
set "JAR_PATH=%SCRIPT_DIR%target\file-manager-1.0-SNAPSHOT.jar"
set "LAST_DIR_FILE=%USERPROFILE%\.file_navigator_last_dir"

rem Check if jar exists
if not exist "%JAR_PATH%" (
    echo Error: Jar file not found at %JAR_PATH%
    echo Please run 'mvn clean package' first
    exit /b 1
)

rem Function to change directory
:change_directory
if exist "%LAST_DIR_FILE%" (
    set /p NEW_DIR=<"%LAST_DIR_FILE%"
    cd /d "!NEW_DIR!"
    del "%LAST_DIR_FILE%"
)
goto :eof

rem Main execution
if "%~1"=="" (
    java -jar "%JAR_PATH%" navigate
    call :change_directory
) else if "%~1"=="navigate" (
    java -jar "%JAR_PATH%" %*
    call :change_directory
) else (
    java -jar "%JAR_PATH%" %*
)

endlocal