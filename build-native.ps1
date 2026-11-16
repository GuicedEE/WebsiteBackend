param([switch]$PrepareOnly, [string]$Image = 'website-backend-native:local')
$ErrorActionPreference = 'Stop'
Set-Location $PSScriptRoot
function Run([string]$program, [string[]]$arguments) {
    & $program @arguments
    if ($LASTEXITCODE -ne 0) { throw "$program failed with exit code $LASTEXITCODE" }
}
$target = Join-Path $PSScriptRoot 'target'
New-Item -ItemType Directory -Force $target | Out-Null
$work = Join-Path $target ('native-stage-' + [guid]::NewGuid().ToString('N'))
New-Item -ItemType Directory $work | Out-Null
try {
    $context = Join-Path $work 'context'
    $libs = Join-Path $context 'native-libs'
    New-Item -ItemType Directory -Force $libs | Out-Null
    $urls = @{
        'health' = 'https://github.com/GuicedEE/Health.git'
        'service-registry' = 'https://github.com/GuicedEE/ServiceRegistry.git'
        'rest-client' = 'https://github.com/GuicedEE/Rest-Client.git'
    }
    $sources = @{}
    foreach ($line in Get-Content (Join-Path $PSScriptRoot 'native-patches/sources.lock')) {
        $parts = $line -split '\s+'
        if ($parts.Count -ne 2 -or -not $urls.ContainsKey($parts[0]) -or $parts[1] -notmatch '^[0-9a-f]{40}$') { throw "Invalid source lock: $line" }
        $name = $parts[0]; $revision = $parts[1]; $dir = Join-Path $work $name
        Run git @('clone','--quiet','--no-checkout',$urls[$name],$dir)
        Run git @('-C',$dir,'checkout','--quiet','--detach',$revision)
        Run git @('-C',$dir,'apply','--check',(Join-Path $PSScriptRoot "native-patches/$name.patch"))
        Run git @('-C',$dir,'apply',(Join-Path $PSScriptRoot "native-patches/$name.patch"))
        Push-Location $dir
        try { Run mvn @('-B','-ntp','package','-DskipTests') } finally { Pop-Location }
        $sources[$name] = Join-Path $dir "target/$name-2.6.0.jar"
    }
    if ($sources.Count -ne 3) { throw 'Source lock must contain all three native compatibility patches' }
    Run mvn @('-B','-ntp','package','dependency:copy-dependencies','-DskipTests','-DincludeScope=runtime',"-DoutputDirectory=$libs")
    foreach ($name in $sources.Keys) { Copy-Item $sources[$name] (Join-Path $libs "$name-2.6.0.jar") -Force }
    $expected = @{}
    foreach ($line in Get-Content (Join-Path $PSScriptRoot 'native-patches/dependencies.sha256')) {
        if ($line -notmatch '^([0-9a-f]{64})  (.+)$') { throw "Invalid dependency lock: $line" }
        $expected[$Matches[2]] = $Matches[1]
    }
    $patched = @('health-2.6.0.jar','service-registry-2.6.0.jar','rest-client-2.6.0.jar')
    foreach ($file in Get-ChildItem $libs -File) {
        if ($file.Name -in $patched) { continue }
        if (-not $expected.ContainsKey($file.Name) -or (Get-FileHash $file.FullName -Algorithm SHA256).Hash.ToLower() -ne $expected[$file.Name]) { throw "Dependency lock mismatch: $($file.Name)" }
        $expected.Remove($file.Name)
    }
    if ($expected.Count) { throw "Missing locked dependencies: $($expected.Keys -join ', ')" }
    Copy-Item (Join-Path $PSScriptRoot 'NativeBuild.java') $context
    Copy-Item (Join-Path $PSScriptRoot 'Dockerfile') $context
    Copy-Item (Join-Path $target 'website-backend-2.5.0.jar') (Join-Path $context 'application.jar')
    if ($PrepareOnly) {
        Write-Output "Prepared native build context: $context"
        Write-Output 'This unique context is retained for the caller.'
    } else {
        Run docker @('build','--progress','plain','-t',$Image,$context)
    }
} finally {
    if (-not $PrepareOnly -and (Test-Path -LiteralPath $work)) {
        $resolved = (Resolve-Path -LiteralPath $work).Path
        if (-not $resolved.StartsWith((Resolve-Path -LiteralPath $target).Path + [IO.Path]::DirectorySeparatorChar)) { throw 'Unsafe cleanup path' }
        Remove-Item -LiteralPath $resolved -Recurse -Force
    }
}
