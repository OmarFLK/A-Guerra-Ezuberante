param([string]$WorldSource = '.tools/skins-8-gametest/world')
$ErrorActionPreference = 'Stop'
$root = Split-Path -Parent $PSScriptRoot
$profile = Join-Path $root '.tools/run-skins-eight'
New-Item -ItemType Directory -Force $profile | Out-Null
$options = Join-Path $profile 'options.txt'
$text = if (Test-Path -LiteralPath $options) { Get-Content -LiteralPath $options -Raw } else { '' }
$text = $text -replace '(?m)^(renderDistance|simulationDistance|maxFps|enableVsync|soundCategory_master):[^\r\n]*\r?\n?', ''
[IO.File]::WriteAllText($options, $text.TrimEnd() + "`nrenderDistance:5`nsimulationDistance:5`nmaxFps:60`nenableVsync:false`nsoundCategory_master:0.0`n", (New-Object Text.UTF8Encoding($false)))
$result = Join-Path $profile 'skins-eight-result.json'
if (Test-Path -LiteralPath $result) { Remove-Item -LiteralPath $result }
$world = 'SkinsEight-' + (Get-Date -Format 'yyyyMMdd-HHmmss')
$destination = Join-Path $profile "saves/$world"
New-Item -ItemType Directory -Force (Split-Path -Parent $destination) | Out-Null
Copy-Item -LiteralPath (Join-Path $root $WorldSource) -Destination $destination -Recurse
& (Join-Path $PSScriptRoot 'gradle.ps1') runClient "-PclientWorkDir=$profile" '-PskinsEightTest' '-PsilentClient' "-PquickPlayWorld=$world" --console=plain --max-workers=4
if ($LASTEXITCODE -ne 0) { throw "Minecraft test exited $LASTEXITCODE" }
if (-not (Test-Path -LiteralPath $result)) { throw 'Missing eight-skin graphical report.' }
$report = Get-Content -LiteralPath $result -Raw | ConvertFrom-Json
if (-not $report.passed) { throw ($report.errors -join '; ') }
Write-Output 'Eight egg families, all 24 textures and 97 captures passed.'
