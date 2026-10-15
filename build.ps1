param(
 [ValidateSet('gui','smoke','test','build','capture')][string]$Mode='gui',
 [ValidateSet('','emiliano','diego','abraham')][string]$Member='',
 [string]$Board='123406758',[int]$Limit=181440,[string]$CapturePath='',
 [string]$JdkHome='',[switch]$Rma,[switch]$ValidateBoard
)
$ErrorActionPreference='Stop'
Set-Location -LiteralPath $PSScriptRoot
if (!$Member) {
 $members=@('emiliano','diego','abraham' | Where-Object {Test-Path (Join-Path $PSScriptRoot "$_\src\main\java")})
 if($members.Count -ne 1){throw 'Seleccione -Member emiliano, diego o abraham. Debe existir su modulo.'}
 $Member=$members[0]
}
if(!(Test-Path "$Member\src\main\java")){throw "Falta el modulo $Member en este checkout."}
$candidates=@($JdkHome,$env:JAVA_HOME)
$candidates+=@(Get-ChildItem 'C:\Program Files\Java','C:\Program Files\Eclipse Adoptium',(Join-Path $env:USERPROFILE '.jdks') -Directory -ErrorAction SilentlyContinue | Sort-Object Name -Descending | ForEach-Object FullName)
$jdk=$candidates | Where-Object {$_ -and (Test-Path (Join-Path $_ 'bin\javac.exe'))} | Select-Object -First 1
if($jdk){$javac=Join-Path $jdk 'bin\javac.exe';$java=Join-Path $jdk 'bin\java.exe'}
else {$javac=(Get-Command javac -ErrorAction Stop).Source;$java=(Get-Command java -ErrorAction Stop).Source}
Write-Host "AVANCE $Member | JDK: $java"
$classes=Join-Path $PSScriptRoot "target\$Member\classes"
$testClasses=Join-Path $PSScriptRoot "target\$Member\test-classes"
$jar=Join-Path $PSScriptRoot 'lib\jade-4.3.jar'
if(!(Test-Path $jar)){throw 'Falta lib/jade-4.3.jar; restaure la biblioteca del ZIP.'}
New-Item -ItemType Directory -Force $classes | Out-Null
$sources=@(Get-ChildItem 'common\src\main\java',"$Member\src\main\java" -Recurse -Filter '*.java' | ForEach-Object FullName)
& $javac -encoding UTF-8 --release 17 -cp $jar -d $classes $sources
if($LASTEXITCODE -ne 0){exit $LASTEXITCODE}
if($Mode -eq 'build'){Write-Host "BUILD PASS $Member";exit 0}
$cp="$classes;$jar"
if($Mode -eq 'test'){
 New-Item -ItemType Directory -Force $testClasses | Out-Null
 $tests=@(Get-ChildItem 'common\src\test\java',"$Member\src\test\java" -Recurse -Filter '*.java' | ForEach-Object FullName)
 & $javac -encoding UTF-8 --release 17 -cp $cp -d $testClasses $tests
 if($LASTEXITCODE -ne 0){exit $LASTEXITCODE}
 & $java -Xmx768m -cp "$testClasses;$cp" puzzle.shared.CommonVerification
 if($LASTEXITCODE -ne 0){exit $LASTEXITCODE}
 & $java -Xmx768m -cp "$testClasses;$cp" "puzzle.$Member.Verification"
} else {
 $appArgs=@("--board=$Board","--limit=$Limit")
 if($Mode -eq 'smoke'){$appArgs+='--smoke'}
 if($Rma){$appArgs+='--rma'}
 if($ValidateBoard){$appArgs+='--validate'}
 if($Mode -eq 'capture'){
  if(!$CapturePath){$CapturePath=Join-Path $PSScriptRoot "target\$Member\interfaz.png"}
  $appArgs+="--capture=$CapturePath"
 }
 & $java -Xmx768m -cp $cp "puzzle.$Member.Main" @appArgs
}
exit $LASTEXITCODE
