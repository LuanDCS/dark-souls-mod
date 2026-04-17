$ErrorActionPreference = "Stop"

$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$srcJava = Join-Path $projectRoot "src\\main\\java"
$srcRes = Join-Path $projectRoot "src\\main\\resources"
$buildDir = Join-Path $projectRoot "build"
$classesDir = Join-Path $buildDir "classes"
$stagingDir = Join-Path $buildDir "staging"
$distDir = Join-Path $projectRoot "dist"
$jarName = "dbc-hair-color-fix-1.0.0.jar"
$jarPath = Join-Path $distDir $jarName

$modpackRoot = "C:\\Users\\luand\\AppData\\Roaming\\.technic\\modpacks\\dbcdragonwarriors"
$cp = @(
  (Join-Path $modpackRoot "bin\\modpack.jar"),
  (Join-Path $modpackRoot "mods\\jinryuudragonblockc-1.7.10.jar"),
  (Join-Path $modpackRoot "mods\\jinryuujrmcore-1.7.10-UNKNOWN.jar")
) -join ";"

if (Test-Path $buildDir) { Remove-Item -Recurse -Force $buildDir }
if (Test-Path $distDir) { Remove-Item -Recurse -Force $distDir }
New-Item -ItemType Directory -Path $classesDir | Out-Null
New-Item -ItemType Directory -Path $stagingDir | Out-Null
New-Item -ItemType Directory -Path $distDir | Out-Null

$javaFiles = Get-ChildItem -Path $srcJava -Recurse -Filter *.java | ForEach-Object { $_.FullName }
if ($javaFiles.Count -eq 0) {
  throw "Nenhum arquivo Java encontrado em $srcJava"
}

javac --release 8 -cp $cp -d $classesDir $javaFiles
if ($LASTEXITCODE -ne 0) {
  throw "Falha na compilação Java"
}

Copy-Item -Path (Join-Path $classesDir "*") -Destination $stagingDir -Recurse -Force
Copy-Item -Path (Join-Path $srcRes "*") -Destination $stagingDir -Recurse -Force

Add-Type -AssemblyName System.IO.Compression
Add-Type -AssemblyName System.IO.Compression.FileSystem
if (Test-Path $jarPath) { Remove-Item -Force $jarPath }
$zip = [System.IO.Compression.ZipFile]::Open($jarPath, [System.IO.Compression.ZipArchiveMode]::Create)
try {
  Get-ChildItem -Path $stagingDir -Recurse -File | ForEach-Object {
    $relative = $_.FullName.Substring($stagingDir.Length + 1).Replace("\", "/")
    [System.IO.Compression.ZipFileExtensions]::CreateEntryFromFile($zip, $_.FullName, $relative, [System.IO.Compression.CompressionLevel]::Optimal) | Out-Null
  }
}
finally {
  $zip.Dispose()
}

Write-Host "Build concluído:"
Write-Host $jarPath
