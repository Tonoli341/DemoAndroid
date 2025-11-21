@ECHO OFF
SET DIR=%~dp0
SET WRAPPER_JAR=%DIR%gradle\wrapper\gradle-wrapper.jar
SET PROPERTIES_FILE=%DIR%gradle\wrapper\gradle-wrapper.properties

IF EXIST "%WRAPPER_JAR%" GOTO execute
ECHO gradle-wrapper.jar non trovato. Scarica la distribuzione indicata in %PROPERTIES_FILE% e posiziona il file nella cartella gradle\wrapper.
EXIT /B 1

:execute
SET JAVA_EXE=java
"%JAVA_EXE%" -Dorg.gradle.appname="gradlew" -classpath "%WRAPPER_JAR%" org.gradle.wrapper.GradleWrapperMain %*
