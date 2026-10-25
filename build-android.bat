@echo off
rem Build the WTMS Demo Android app on Windows - woodyouloveit.com
rem Double-click for a menu, or run with options, e.g.:  build-android.bat -Task Debug -Install -Launch
setlocal
cd /d "%~dp0"
if not exist "%~dp0tools\build-android.ps1" (
    echo The build script is not in tools\ yet. Copying it from the Water Tanks Monitor project...
    powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0tools\get-build-script.ps1"
    if errorlevel 1 (
        echo.
        pause
        exit /b 1
    )
)
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0tools\build-android.ps1" %*
set "EXITCODE=%ERRORLEVEL%"
if "%~1"=="" (
    echo.
    pause
)
exit /b %EXITCODE%
