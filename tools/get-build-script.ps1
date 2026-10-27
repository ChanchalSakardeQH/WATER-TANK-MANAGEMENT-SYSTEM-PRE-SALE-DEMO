<#
.SYNOPSIS
    Copies the self-setting-up Android build script from the Water Tanks Monitor project
    and adapts it for the WTMS Demo app.

.DESCRIPTION
    The demo app is built with the same tools/build-android.ps1 as the real Water Tanks Monitor app.
    This copies that script into this project's tools\ folder and changes only what must differ:
      - the app ID       com.woodyouloveit.wtms  ->  com.woodyouloveit.wtms.demo
      - the file names   woodyouloveit-wtms-...  ->  woodyouloveit-wtms-demo-...
      - the menu and log titles
    The release signing key (wtms-release.jks) and the shared tools in D:\Android stay the same.

.PARAMETER From
    Path of the source build-android.ps1. If omitted, the newest one found in
    D:\Android\Projects\AI-WATER-TANK-MANAGEMENT-SYSTEM* (or next to this project) is used.

.EXAMPLE
    .\tools\get-build-script.ps1
    .\tools\get-build-script.ps1 -From D:\Android\Projects\AI-WATER-TANK-MANAGEMENT-SYSTEM-V1\tools\build-android.ps1

.NOTES
    woodyouloveit.com
#>
param([string]$From)

$ErrorActionPreference = 'Stop'
$ToolsDir = $PSScriptRoot
$Target   = Join-Path $ToolsDir 'build-android.ps1'

if (-not $From) {
    $roots = @('D:\Android\Projects', (Split-Path (Split-Path $ToolsDir -Parent) -Parent)) | Select-Object -Unique
    $found = @()
    foreach ($root in $roots) {
        if (-not (Test-Path $root)) { continue }
        $found += Get-ChildItem -Path $root -Directory -ErrorAction SilentlyContinue |
            Where-Object { $_.Name -like 'AI-WATER-TANK-MANAGEMENT-SYSTEM*' -or $_.Name -like '*WATERTANK*' } |
            ForEach-Object { Join-Path $_.FullName 'tools\build-android.ps1' } |
            Where-Object { Test-Path $_ }
    }
    if ($found.Count -gt 0) {
        $From = $found | Sort-Object { (Get-Item $_).LastWriteTime } -Descending | Select-Object -First 1
    }
}

if (-not $From -or -not (Test-Path $From)) {
    Write-Host ''
    Write-Host '  [!] Could not find the Water Tanks Monitor build script.' -ForegroundColor Yellow
    Write-Host '      Run this again with its path, for example:'
    Write-Host '      .\tools\get-build-script.ps1 -From D:\Android\Projects\AI-WATER-TANK-MANAGEMENT-SYSTEM\tools\build-android.ps1'
    exit 1
}

$From = (Resolve-Path $From).Path
Write-Host "  Source: $From"
$bytes  = [IO.File]::ReadAllBytes($From)
$hasBom = $bytes.Length -ge 3 -and $bytes[0] -eq 0xEF -and $bytes[1] -eq 0xBB -and $bytes[2] -eq 0xBF
$text   = [Text.Encoding]::UTF8.GetString($bytes)
if ($hasBom) { $text = $text.Substring(1) }

if ($text.Contains('com.woodyouloveit.wtms.demo')) {
    Write-Host '  [!] That script is already the demo version. Point -From at the Water Tanks Monitor project.' -ForegroundColor Yellow
    exit 1
}

# Old text, new text, required?
$changes = @(
    @("'com.woodyouloveit.wtms'",              "'com.woodyouloveit.wtms.demo'",              $true,  'app ID'),
    @("= 'woodyouloveit-wtms'",                "= 'woodyouloveit-wtms-demo'",                $false, 'output file names'),
    @('woodyouloveit-wtms-$',                  'woodyouloveit-wtms-demo-$',                  $false, 'output file names (older script)'),
    @("= 'Water Tanks Monitor'",               "= 'WTMS Demo'",                              $false, 'app name'),
    @('Water Tanks Monitor - Android build',   'Water Tanks Monitor DEMO - Android build',   $false, 'menu title'),
    @('Water Tanks Monitor Android build',     'Water Tanks Monitor DEMO Android build',     $false, 'log title')
)
$missing = @()
foreach ($c in $changes) {
    $n = ([regex]::Matches($text, [regex]::Escape($c[0]))).Count
    if ($n -eq 0) {
        if ($c[2]) { $missing += $c[3] }
        Write-Host ("  -  {0}: not found" -f $c[3]) -ForegroundColor DarkGray
        continue
    }
    $text = $text.Replace($c[0], $c[1])
    Write-Host ("  OK {0}: {1} change(s)" -f $c[3], $n) -ForegroundColor Green
}
if (-not $text.Contains("woodyouloveit-wtms-demo")) { $missing += 'output file names' }
if ($missing.Count -gt 0) {
    Write-Host ''
    Write-Host ("  [!] Could not adapt: {0}. The script was not copied." -f ($missing -join ', ')) -ForegroundColor Yellow
    Write-Host '      Send tools\build-android.ps1 from the Water Tanks Monitor project and it will be adapted by hand.'
    exit 1
}

$enc = New-Object System.Text.UTF8Encoding($hasBom)
[IO.File]::WriteAllText($Target, $text, $enc)
Write-Host ''
Write-Host "  Saved $Target" -ForegroundColor Green
Write-Host '  Build with build-android.bat (menu) or: build-android.bat -Task Debug -Install -Launch'
exit 0
