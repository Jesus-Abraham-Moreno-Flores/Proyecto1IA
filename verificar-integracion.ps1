param([string]$JdkHome = '')
$ErrorActionPreference = 'Stop'
$destination = Join-Path $PSScriptRoot 'target\integracion'
New-Item -ItemType Directory -Force $destination | Out-Null

$cases = @(
    @{Name='normal'; Board='123406758'; Limit=181440; Code=0; Text='AGENTE PASS'},
    @{Name='meta'; Board='123456780'; Limit=181440; Code=0; Text='AGENTE PASS'},
    @{Name='insoluble'; Board='123456870'; Limit=181440; Code=2; Text='Tablero insoluble'},
    @{Name='limite'; Board='123406758'; Limit=1; Code=2; Text='LIMITE ALCANZADO'}
)

foreach ($case in $cases) {
    $arguments = @('-NoProfile','-ExecutionPolicy','Bypass','-File',
        (Join-Path $PSScriptRoot 'build.ps1'),'-Mode','agente',
        '-Board',$case.Board,'-Limit',$case.Limit)
    if ($JdkHome) { $arguments += @('-JdkHome', $JdkHome) }

    $log = Join-Path $destination ($case.Name + '.txt')
    $ErrorActionPreference = 'Continue'
    & powershell.exe @arguments *> $log
    $code = $LASTEXITCODE
    $ErrorActionPreference = 'Stop'

    if ($code -ne $case.Code -or !(Get-Content $log -Raw).Contains($case.Text)) {
        throw "Fallo $($case.Name). Consulte $log"
    }
    Write-Host "PASS JADE $($case.Name) codigo=$code"
}
Write-Host 'INTEGRACION CONSOLA PASS'
