@rem
@rem Copyright 2015 the original author or authors.
@rem
@rem Licensed under the Apache License, Version 2.0 (the "License");
@rem you may not use this file except in compliance with the License.
@rem You may obtain a copy of the License at
@rem
@rem      https://www.apache.org/licenses/LICENSE-2.0
@rem
@rem Unless required by applicable law or agreed to in writing, software
@rem distributed under the License is distributed on an "AS IS" BASIS,
@rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@rem See the License for the specific language governing permissions and
@rem limitations under the License.
@rem

@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  Practica-4 startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Resolve any "." and ".." in APP_HOME to make it shorter.
for %%i in ("%APP_HOME%") do set APP_HOME=%%~fi

@rem Add default JVM options here. You can also use JAVA_OPTS and PRACTICA_4_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto execute

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto execute

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\Practica-4-1.0-SNAPSHOT.jar;%APP_HOME%\lib\thymeleaf-3.0.11.RELEASE.jar;%APP_HOME%\lib\h2-1.4.200.jar;%APP_HOME%\lib\postgresql-42.2.1.jar;%APP_HOME%\lib\hibernate-entitymanager-5.4.17.Final.jar;%APP_HOME%\lib\hibernate-core-5.4.17.Final.jar;%APP_HOME%\lib\jjwt-impl-0.11.2.jar;%APP_HOME%\lib\jjwt-jackson-0.11.2.jar;%APP_HOME%\lib\jjwt-api-0.11.2.jar;%APP_HOME%\lib\javalin-3.13.7.jar;%APP_HOME%\lib\slf4j-simple-1.7.30.jar;%APP_HOME%\lib\hotswap-agent-core-1.4.1.jar;%APP_HOME%\lib\jasypt-1.9.2.jar;%APP_HOME%\lib\ognl-3.1.12.jar;%APP_HOME%\lib\attoparser-2.0.5.RELEASE.jar;%APP_HOME%\lib\unbescape-1.1.6.RELEASE.jar;%APP_HOME%\lib\slf4j-api-1.7.30.jar;%APP_HOME%\lib\hibernate-commons-annotations-5.1.0.Final.jar;%APP_HOME%\lib\jboss-logging-3.3.2.Final.jar;%APP_HOME%\lib\javax.persistence-api-2.2.jar;%APP_HOME%\lib\javassist-3.24.0-GA.jar;%APP_HOME%\lib\byte-buddy-1.10.10.jar;%APP_HOME%\lib\antlr-2.7.7.jar;%APP_HOME%\lib\jboss-transaction-api_1.2_spec-1.1.1.Final.jar;%APP_HOME%\lib\jandex-2.1.3.Final.jar;%APP_HOME%\lib\classmate-1.5.1.jar;%APP_HOME%\lib\jaxb-runtime-2.3.1.jar;%APP_HOME%\lib\jaxb-api-2.3.1.jar;%APP_HOME%\lib\javax.activation-api-1.2.0.jar;%APP_HOME%\lib\dom4j-2.1.3.jar;%APP_HOME%\lib\jetty-webapp-9.4.40.v20210413.jar;%APP_HOME%\lib\websocket-server-9.4.40.v20210413.jar;%APP_HOME%\lib\jetty-servlet-9.4.40.v20210413.jar;%APP_HOME%\lib\jetty-security-9.4.40.v20210413.jar;%APP_HOME%\lib\jetty-server-9.4.40.v20210413.jar;%APP_HOME%\lib\kotlin-stdlib-jdk8-1.3.71.jar;%APP_HOME%\lib\jackson-databind-2.9.10.4.jar;%APP_HOME%\lib\txw2-2.3.1.jar;%APP_HOME%\lib\istack-commons-runtime-3.0.7.jar;%APP_HOME%\lib\stax-ex-1.8.jar;%APP_HOME%\lib\FastInfoset-1.2.15.jar;%APP_HOME%\lib\websocket-servlet-9.4.40.v20210413.jar;%APP_HOME%\lib\javax.servlet-api-3.1.0.jar;%APP_HOME%\lib\websocket-client-9.4.40.v20210413.jar;%APP_HOME%\lib\jetty-client-9.4.40.v20210413.jar;%APP_HOME%\lib\jetty-http-9.4.40.v20210413.jar;%APP_HOME%\lib\websocket-common-9.4.40.v20210413.jar;%APP_HOME%\lib\jetty-io-9.4.40.v20210413.jar;%APP_HOME%\lib\jetty-xml-9.4.40.v20210413.jar;%APP_HOME%\lib\kotlin-stdlib-jdk7-1.3.71.jar;%APP_HOME%\lib\kotlin-stdlib-1.3.71.jar;%APP_HOME%\lib\jackson-annotations-2.9.10.jar;%APP_HOME%\lib\jackson-core-2.9.10.jar;%APP_HOME%\lib\jetty-util-ajax-9.4.40.v20210413.jar;%APP_HOME%\lib\jetty-util-9.4.40.v20210413.jar;%APP_HOME%\lib\websocket-api-9.4.40.v20210413.jar;%APP_HOME%\lib\kotlin-stdlib-common-1.3.71.jar;%APP_HOME%\lib\annotations-13.0.jar


@rem Execute Practica-4
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %PRACTICA_4_OPTS%  -classpath "%CLASSPATH%" org.practica.Main %*

:end
@rem End local scope for the variables with windows NT shell
if "%ERRORLEVEL%"=="0" goto mainEnd

:fail
rem Set variable PRACTICA_4_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
if  not "" == "%PRACTICA_4_EXIT_CONSOLE%" exit 1
exit /b 1

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
