$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.Drawing

$projectRoot = Split-Path -Parent $PSScriptRoot
$sourceRoot = Join-Path $projectRoot 'assets-entrada\quadros'
$targetRoot = Join-Path $projectRoot 'src\main\resources\assets\guerra_ezuberante\textures\painting'

$paintings = @(
    @{ Prefix = '01'; Id = 'feliz_datas'; Width = 512; Height = 256 },
    @{ Prefix = '02'; Id = 'happy_nation'; Width = 512; Height = 256 },
    @{ Prefix = '03'; Id = 'me_respeita'; Width = 512; Height = 512 },
    @{ Prefix = '04'; Id = 'pergunta'; Width = 256; Height = 256 },
    @{ Prefix = '05'; Id = 'rachadinha_tres_porcento'; Width = 256; Height = 256 },
    @{ Prefix = '06'; Id = 'sacos_lula_flavio'; Width = 512; Height = 512 },
    @{ Prefix = '07'; Id = 'mestre_dos_bagos'; Width = 256; Height = 384 },
    @{ Prefix = '08'; Id = 'bolsonaro_joinha'; Width = 256; Height = 256 },
    @{ Prefix = '09'; Id = 'um_pau'; Width = 512; Height = 256 },
    @{ Prefix = '10'; Id = 'tweet_autodeclaracao'; Width = 512; Height = 384 }
)

New-Item -ItemType Directory -Force -Path $targetRoot | Out-Null

foreach ($painting in $paintings) {
    $sources = @(Get-ChildItem -LiteralPath $sourceRoot -File | Where-Object {
        $_.BaseName.StartsWith($painting.Prefix) -and $_.Extension.ToLowerInvariant() -in @('.png', '.jpg', '.jpeg')
    })
    if ($sources.Count -ne 1) {
        throw "Esperado exatamente um PNG/JPG começando com $($painting.Prefix) em $sourceRoot; encontrados: $($sources.Count)."
    }

    $source = [System.Drawing.Image]::FromFile($sources[0].FullName)
    try {
        $output = [System.Drawing.Bitmap]::new($painting.Width, $painting.Height, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
        try {
            $graphics = [System.Drawing.Graphics]::FromImage($output)
            try {
                $graphics.Clear([System.Drawing.Color]::Black)
                $graphics.CompositingMode = [System.Drawing.Drawing2D.CompositingMode]::SourceCopy
                $graphics.CompositingQuality = [System.Drawing.Drawing2D.CompositingQuality]::HighQuality
                $graphics.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
                $graphics.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::HighQuality
                $graphics.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::HighQuality

                $scale = [Math]::Min($painting.Width / $source.Width, $painting.Height / $source.Height)
                $drawWidth = [Math]::Max(1, [int][Math]::Round($source.Width * $scale))
                $drawHeight = [Math]::Max(1, [int][Math]::Round($source.Height * $scale))
                $x = [int][Math]::Floor(($painting.Width - $drawWidth) / 2)
                $y = [int][Math]::Floor(($painting.Height - $drawHeight) / 2)
                $graphics.DrawImage($source, $x, $y, $drawWidth, $drawHeight)
            }
            finally {
                $graphics.Dispose()
            }

            $destination = Join-Path $targetRoot ($painting.Id + '.png')
            $output.Save($destination, [System.Drawing.Imaging.ImageFormat]::Png)
            Write-Host "OK: $($sources[0].Name) -> $($painting.Id).png ($($painting.Width)x$($painting.Height))"
        }
        finally {
            $output.Dispose()
        }
    }
    finally {
        $source.Dispose()
    }
}

Write-Host 'OK: 10 quadros preparados sem recorte.'
