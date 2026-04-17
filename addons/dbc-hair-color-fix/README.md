# DBC Hair Color Fix (1.7.10)

Addon client-side para DBC/JRMCore que mantém a cor de cabelo do DNS mesmo com `state` Saiyajin ativo.

## O que ele corrige

- Intercepta `JinRyuu.JRMCore.JRMCoreHDBC#getPlayerColor(...)`.
- Quando o `part` é cabelo (`part == 1`) e a raça é Saiyajin (`1`) ou Half-Saiyajin (`2`), o addon usa a cor base do DNS (`def`) em vez da cor fixa amarela do estado.
- Mantém variação de sombreamento simples para os tipos internos do render (`type == 1` e `type == 3`).

## Build local

1. Abra PowerShell nesta pasta.
2. Rode:

```powershell
.\build.ps1
```

O JAR gerado ficará em:

- `dist/dbc-hair-color-fix-1.0.0.jar`

## Instalação no modpack

1. Copie o JAR para:
   - `C:\Users\luand\AppData\Roaming\.technic\modpacks\dbcdragonwarriors\mods`
2. Inicie o jogo.

## Configuração

No primeiro boot o addon cria:

- `config/dbc_hair_color_fix.properties`

Opções:

- `enabled=true` ativa/desativa tudo.
- `only_state=-1` aplica em todos os states Saiyajin (`-1`) ou em um state específico (`1`, `2`, etc).
- `include_ssj4=true` controla se aplica também no state 14.
- `shade_darken=0.35` intensidade de escurecimento para sombra.
- `shade_lighten=0.15` intensidade de clareamento para brilho.

## Observação

É um patch de render no cliente. Para todos verem a mesma cor, o ideal é instalar no cliente de todos.

