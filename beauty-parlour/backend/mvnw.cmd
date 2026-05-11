@REM ----------------------------------------------------------------------------
@REM Maven Start Up Batch script
@REM ----------------------------------------------------------------------------
@IF "%__MVNW_ARG0_NAME__%"=="" (SET "BASE_DIR=%~dp0") ELSE (SET "BASE_DIR=%__MVNW_ARG0_NAME__%")
@SET WRAPPER_DIR=%BASE_DIR%.mvn\wrapper
@SET WRAPPER_JAR=%WRAPPER_DIR%\maven-wrapper.jar
@SET WRAPPER_LAUNCHER=org.apache.maven.wrapper.MavenWrapperMain

@REM Find JAVA_HOME
@IF NOT "%JAVA_HOME%"=="" GOTO findMvn
@FOR %%i IN (java.exe) DO @SET "JAVA_EXEC=%%~$PATH:i"
@IF NOT "%JAVA_EXEC%"=="" (
    @FOR %%i IN ("%JAVA_EXEC%\..\..\..") DO @SET "JAVA_HOME=%%~fi"
) ELSE (
    @ECHO Error: JAVA_HOME is not set and java could not be found on PATH.
    @EXIT /B 1
)

:findMvn
@SET JAVA_CMD="%JAVA_HOME%\bin\java.exe"

@REM Download wrapper jar if missing
@IF EXIST "%WRAPPER_JAR%" GOTO runMaven
@IF NOT EXIST "%WRAPPER_DIR%" MKDIR "%WRAPPER_DIR%"
@ECHO Downloading Maven Wrapper...
@powershell -Command "$uri='https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar'; Invoke-WebRequest -Uri $uri -OutFile '%WRAPPER_JAR%' -UseBasicParsing"
@IF NOT EXIST "%WRAPPER_JAR%" (
    @ECHO ERROR: Failed to download maven-wrapper.jar
    @EXIT /B 1
)

:runMaven
%JAVA_CMD% -classpath "%WRAPPER_JAR%" "-Dmaven.multiModuleProjectDirectory=%BASE_DIR%" %WRAPPER_LAUNCHER% %*
