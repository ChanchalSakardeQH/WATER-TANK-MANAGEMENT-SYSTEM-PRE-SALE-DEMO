@echo off
rem Copy and adapt the Water Tanks Monitor build script for the demo app (run again after updating that script)
setlocal
cd /d "%~dp0.."
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0get-build-script.ps1" %*
echo.
pause
