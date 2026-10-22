param(
    [ValidateSet('agente','heuristica','greedy','motor','test','build','smoke')]
    [string]$Mode = 'agente',
    [string]$Board = '123406758',
    [int]$Limit = 181440,
    [string]$JdkHome = ''
)

$ErrorActionPreference = 'Stop'
Set-Location -LiteralPath $PSScriptRoot

$candidates = @($JdkHome, $env:JAVA_HOME)
$candidates += @(Get-ChildItem 'C:\Program Files\Java',
    (Join-Path $env:USERPROFILE '.jdks') -Directory -ErrorAction SilentlyContinue |
    Sort-Object Name -Descending | ForEach-Object FullName)
$jdk = $candidates | Where-Object {
    $_ -and (Test-Path (Join-Path $_ 'bin\javac.exe'))
} | Select-Object -First 1

if ($jdk) {
    $javac = Join-Path $jdk 'bin\javac.exe'
    $java = Join-Path $jdk 'bin\java.exe'
} else {
    $javac = (Get-Command javac -ErrorAction Stop).Source
    $java = (Get-Command java -ErrorAction Stop).Source
}

$pureJava = $Mode -in @('heuristica','greedy','motor')
$outputFolder = if ($pureJava) { 'target\vanilla\classes' } else { 'target\consola\classes' }
$classes = Join-Path $PSScriptRoot $outputFolder
$tests = Join-Path $PSScriptRoot 'target\consola\test-classes'
New-Item -ItemType Directory -Force $classes | Out-Null

if ($pureJava) {
    $sources = @(Get-ChildItem 'src\main\java' -Recurse -Filter '*.java' |
        Where-Object Name -notin @('Main.java','Player3Agent.java','AgenteConsola.java') |
        ForEach-Object FullName)
    & $javac -encoding UTF-8 --release 17 -d $classes $sources
    $classpath = $classes
} else {
    $jar = Join-Path $PSScriptRoot 'lib\jade-4.3.jar'
    $sources = @(Get-ChildItem 'src\main\java' -Recurse -Filter '*.java' | ForEach-Object FullName)
    & $javac -encoding UTF-8 --release 17 -cp $jar -d $classes $sources
    $classpath = "$classes;$jar"
}
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
if ($Mode -eq 'build') { Write-Host 'COMPILACION PASS'; exit 0 }

switch ($Mode) {
    'heuristica' {
        if ($PSBoundParameters.ContainsKey('Board')) {
            & $java -cp $classpath uabc.computacion.proyectomedioia.DemoHeuristica "--board=$Board"
        } else {
            & $java -cp $classpath uabc.computacion.proyectomedioia.DemoHeuristica
        }
    }
    'greedy' {
        & $java -cp $classpath uabc.computacion.proyectomedioia.DemoGreedy "--board=$Board"
    }
    { $_ -in @('motor','test') } {
        New-Item -ItemType Directory -Force $tests | Out-Null
        $testSources = @(Get-ChildItem 'src\test\java' -Recurse -Filter '*.java' | ForEach-Object FullName)
        & $javac -encoding UTF-8 --release 17 -cp $classpath -d $tests $testSources
        if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
        $testMain = if ($Mode -eq 'motor') { 'puzzle.abraham.PruebaMotorVanilla' } else { 'puzzle.abraham.Verification' }
        & $java -Xmx768m -cp "$tests;$classpath" $testMain
    }
    default {
        & $java -cp $classpath puzzle.abraham.Main "--board=$Board" "--limit=$Limit"
    }
}
exit $LASTEXITCODE
