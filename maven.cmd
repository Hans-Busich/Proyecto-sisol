@echo off
setlocal
if defined SISOL_JAVA_HOME (
    set "JAVA_HOME=%SISOL_JAVA_HOME%"
) else (
    set "JAVA_HOME=C:\Program Files\Java\jdk-21"
)
if not exist "%JAVA_HOME%\bin\javac.exe" (
    echo No se encontro el JDK. Configura SISOL_JAVA_HOME con su carpeta.
    exit /b 1
)
if not defined SISOL_MAVEN_CMD set "SISOL_MAVEN_CMD=C:\Program Files\NetBeans-21\netbeans\java\maven\bin\mvn.cmd"
if not exist "%SISOL_MAVEN_CMD%" (
    echo No se encontro Maven. Configura SISOL_MAVEN_CMD con la ruta de mvn.cmd.
    exit /b 1
)
pushd "%~dp0"
if "%~1"=="" (
    call "%SISOL_MAVEN_CMD%" -B package
) else (
    call "%SISOL_MAVEN_CMD%" %*
)
set "SISOL_EXIT_CODE=%ERRORLEVEL%"
popd
exit /b %SISOL_EXIT_CODE%
