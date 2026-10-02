$env:JAVA_HOME = "C:\Users\SAPUSER\AppData\Local\Programs\Eclipse Adoptium\jdk-25.0.4.101-hotspot"
$env:Path = "$env:JAVA_HOME\bin;" + $env:Path

Write-Host "========================================================" -ForegroundColor Cyan
Write-Host " Launching Creator Platform Backend (Spring Boot)" -ForegroundColor Cyan
Write-Host " Active Java: Java 25 (Eclipse Adoptium)" -ForegroundColor Cyan
Write-Host "========================================================" -ForegroundColor Cyan

Set-Location -Path "$PSScriptRoot\backend"
& .\mvnw.cmd spring-boot:run
