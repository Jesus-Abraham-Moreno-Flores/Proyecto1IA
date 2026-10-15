@echo off
powershell.exe -NoProfile -ExecutionPolicy Bypass -File "%~dp0build.ps1" -Mode test %*
if errorlevel 1 goto fin
powershell.exe -NoProfile -ExecutionPolicy Bypass -File "%~dp0verificar-integracion.ps1" %*
:fin
pause
