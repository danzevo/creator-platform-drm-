@echo off
setlocal
echo ========================================================
echo  Launching Creator Platform Backend (Spring Boot)
echo  Active Java: Java 25 (Eclipse Adoptium)
echo ========================================================

set "JAVA_HOME=C:\Users\SAPUSER\AppData\Local\Programs\Eclipse Adoptium\jdk-25.0.4.101-hotspot"
set "PATH=%JAVA_HOME%\bin;%PATH%"

cd /d "%~dp0"

set "SPRINGDOTENV_DIRECTORY=.."
call mvnw.cmd spring-boot:run

echo.
echo ========================================================
echo  Process ended.
echo ========================================================
pause
