param([ValidateSet('','emiliano','diego','abraham')][string]$Member='', [string]$JdkHome='')
$ErrorActionPreference='Stop'
if(!$Member){
 $members=@('emiliano','diego','abraham'|Where-Object {Test-Path (Join-Path $PSScriptRoot "$_\src\main\java")})
 if($members.Count -ne 1){throw 'Indique -Member.'};$Member=$members[0]
}
$dest=Join-Path $PSScriptRoot "target\$Member\integracion"
New-Item -ItemType Directory -Force $dest|Out-Null
$cases=@(
 @{Name='normal';Board='724506831';Limit=181440;Code=0;Text='SMOKE PASS';Validate=$false},
 @{Name='meta';Board='123456780';Limit=181440;Code=0;Text='SMOKE PASS';Validate=$true},
 @{Name='insoluble';Board='123456870';Limit=181440;Code=2;Text='RECHAZADO';Validate=$true}
)
if($Member -ne 'emiliano'){$cases+=@{Name='limite';Board='724506831';Limit=1;Code=2;Text='LIMITE ALCANZADO';Validate=$false}}
foreach($case in $cases){
 $params=@('-NoProfile','-ExecutionPolicy','Bypass','-File',(Join-Path $PSScriptRoot 'build.ps1'),'-Mode','smoke','-Member',$Member,'-Board',$case.Board,'-Limit',$case.Limit)
 if($JdkHome){$params+=@('-JdkHome',$JdkHome)}
 if($case.Validate){$params+='-ValidateBoard'}
 $log=Join-Path $dest ($case.Name+'.txt')
 $ErrorActionPreference='Continue'
 & powershell.exe @params *> $log
 $code=$LASTEXITCODE
 $ErrorActionPreference='Stop'
 if($code -ne $case.Code -or !(Get-Content $log -Raw).Contains($case.Text)){throw "Fallo $Member/$($case.Name). Consulte $log"}
 Write-Host "PASS JADE $Member/$($case.Name) codigo=$code"
}
Write-Host "INTEGRACION PASS $Member"
