<#-- template to update the package -->
<#-- assumed to be unzipped into a folder named "update" of the per-user installation -->
<#-- invoked with the PID of the running client and the path of the executable as arguments -->
<#if osName?upper_case?contains("WIN")>
@echo off
set script_file=%~nx0
pushd %~dp0
set script_dir=%CD%
popd
cd %script_dir%
cd ..\..
if "%1" neq "" (
  :loop
  tasklist | find " %1 " >nul
  if not errorlevel 1 (
    timeout /t 1 >nul
    goto :loop
  )
)
if exist update\bin (if exist update\app (if exist update\runtime (
  if exist update\bin\%script_file% (
    for %%d in (app, runtime) do (
      if exist %%d rmdir %%d /s /q
      move /y update\%%d .
    )
    rmdir update /s /q
    %2
  ) else (
    echo this is not the correct update script
    exit /b 2
  )
))) else (
  echo no update found
  exit /b 1
)
<#elseif osName?upper_case?contains("MAC")>
abs_path() {
  echo "$(cd "$(dirname "$1")" && pwd)/$(basename "$1")"
}
<#-- necessary only if running on NFS -->
if [ "$1" != "" ]; then
  while kill -0 $1 2>/dev/null
  do
	  sleep 1
  done
fi
cd "$(dirname "$(dirname "$(dirname "$(abs_path "$0")")")")"
if [ -d update/bin ] && [ -d update/Contents ]; then
  if [ -f update/bin/$(basename "$0") ]; then
    rm -fr Contents
    mv -f update/Contents .
    rm -fr update
    chmod +x Contents/MacOS/* Contents/runtime/Contents/Home/lib/runtime/lib/jspawnhelper
    $2
  else
    echo this is not the correct update script
    exit 2
  fi
else
  echo no update found
  exit 1
fi
<#else>
<#-- necessary only if running on NFS -->
if [ "$1" != "" ]; then
  while kill -0 $1 2>/dev/null
  do
	  sleep 1
  done
fi
cd "$(dirname "$(dirname "$(dirname "$(readlink -f "$0")")")")"
if [ -d update/bin ] && [ -d update/lib ]; then
  if [ -f update/bin/$(basename "$0") ]; then
    rm -fr bin lib
    mv -f update/* .
    rm -fr update
    rm bin/$(basename "$0")
    chmod +x bin/* lib/runtime/lib/jexec lib/runtime/lib/jspawnhelper
    $2
  else
    echo this is not the correct update script
    exit 2
  fi
else
  echo no update found
  exit 1
fi
</#if>