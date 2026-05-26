@echo off
setlocal

set "BASE_DIR=%~dp0"
if "%BASE_DIR:~-1%"=="\" set "BASE_DIR=%BASE_DIR:~0,-1%"
set "PROPS_FILE=%BASE_DIR%\.mvn\wrapper\maven-wrapper.properties"

if not exist "%PROPS_FILE%" (
    echo Error: Missing %PROPS_FILE%
    exit /b 1
)

for /f "usebackq tokens=1,* delims==" %%A in ("%PROPS_FILE%") do (
    if /I "%%A"=="distributionUrl" set "DISTRIBUTION_URL=%%B"
)

if not defined DISTRIBUTION_URL (
    echo Error: distributionUrl was not found in %PROPS_FILE%
    exit /b 1
)

for %%I in ("%DISTRIBUTION_URL%") do set "ZIP_NAME=%%~nxI"
set "VERSION_DIR=%ZIP_NAME:-bin.zip=%"
set "MVNW_REPO=%USERPROFILE%\.m2\wrapper\dists"
set "MAVEN_HOME=%MVNW_REPO%\%VERSION_DIR%"
set "MVN_CMD=%MAVEN_HOME%\bin\mvn.cmd"

if not exist "%MVN_CMD%" (
    powershell -NoProfile -ExecutionPolicy Bypass -Command ^
        "$ErrorActionPreference = 'Stop';" ^
        "$repo = $env:MVNW_REPO;" ^
        "$url = $env:DISTRIBUTION_URL;" ^
        "$zipName = [System.IO.Path]::GetFileName($url);" ^
        "$tmpZip = Join-Path $env:TEMP $zipName;" ^
        "New-Item -ItemType Directory -Force -Path $repo | Out-Null;" ^
        "Invoke-WebRequest -Uri $url -OutFile $tmpZip;" ^
        "Expand-Archive -Path $tmpZip -DestinationPath $repo -Force;"

    if errorlevel 1 (
        echo Error: Failed to download or extract Maven from %DISTRIBUTION_URL%
        exit /b 1
    )
)

if not exist "%MVN_CMD%" (
    echo Error: Maven executable was not created at %MVN_CMD%
    exit /b 1
)

call "%MVN_CMD%" %*
exit /b %ERRORLEVEL%