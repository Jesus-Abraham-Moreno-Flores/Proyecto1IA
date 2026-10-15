# Ramas independientes para GitHub

Se entrega `8puzzle-avances.bundle` con historial Git local y estas ramas:

| Rama | Contenido sobre la base común |
|---|---|
| `avance/emiliano-gui-generador` | Solo la carpeta de Emiliano |
| `avance/diego-jugadores-h1-h2` | Solo la carpeta de Diego |
| `avance/abraham-greedy-h3` | Solo la carpeta de Abraham |
| `base-comun` | Contratos, soporte, biblioteca y scripts compartidos |

Las tres ramas nacen del mismo commit base. Los archivos añadidos por cada integrante están en su carpeta y no chocan con los de los otros. Los commits son una preparación técnica de estas entregas; cada integrante puede añadir sus propios cambios y commits antes de subir su rama.

## Opción A: usar las ramas preparadas en un repositorio GitHub nuevo

Requiere Git instalado. Colocar el bundle en la carpeta actual y elegir **solo el comando de su integrante**:

```powershell
git clone --branch avance/emiliano-gui-generador .\8puzzle-avances.bundle avance-emiliano
git clone --branch avance/diego-jugadores-h1-h2 .\8puzzle-avances.bundle avance-diego
git clone --branch avance/abraham-greedy-h3 .\8puzzle-avances.bundle avance-abraham
```

Entrar a la carpeta que se creó, ejecutar `ejecutar.cmd` y `probar.cmd`. Al clonar, `origin` apunta al bundle local; reemplazarlo por la URL de **su repositorio GitHub**:

```powershell
git remote set-url origin https://github.com/USUARIO/REPOSITORIO.git
git branch --show-current
git push -u origin HEAD
```

Reemplazar `USUARIO/REPOSITORIO`; no es una URL configurada. El usuario debe autenticarse con su propia cuenta. Los tres pueden apuntar al mismo repositorio y subir sus ramas diferentes. Preferir un repositorio nuevo vacío para mantener el historial compartido del bundle.

Después de modificar su avance:

```powershell
git status
git add emiliano
git commit -m "Avance individual: GUI y generador soluble"
git push
```

Diego sustituye `emiliano` por `diego` y ajusta el mensaje; Abraham hace lo mismo con `abraham`.

## Opción B: usar un repositorio ya existente y su historial actual

No reemplace su `.git` por otro ni copie un bundle encima del checkout. Una persona del equipo incorpora primero el contenido de `base-comun.zip` a la raíz del repositorio existente, revisando los archivos que ya haya. Hace commit y comparte ese commit base con los compañeros.

Cada integrante actualiza su checkout hasta ese mismo commit base y crea su rama:

```powershell
git switch -c avance/emiliano-gui-generador
```

Luego copia **solo la carpeta `emiliano/`** desde su ZIP a la raíz de ese checkout y ejecuta las pruebas. Los archivos comunes ya deben estar en el commit base. Así conserva el historial de su repositorio y evita sobrescribir archivos compartidos con tres versiones diferentes.

```powershell
git add emiliano
git commit -m "Avance individual: GUI y generador soluble"
git push -u origin avance/emiliano-gui-generador
```

Para Diego y Abraham, usar los nombres de carpeta y rama de la tabla. No hace falta `--force` ni unir historiales que no comparten base. Si `origin` todavía no existe, agregarlo con `git remote add origin URL-DE-SU-REPOSITORIO` antes del push.

## Integración posterior

Desde la base común se pueden reunir las tres ramas en una rama nueva, tras guardar cualquier trabajo local:

```powershell
git switch base-comun
git switch -c integracion/avances
git merge avance/emiliano-gui-generador
git merge avance/diego-jugadores-h1-h2
git merge avance/abraham-greedy-h3
.\build.ps1 -Member emiliano -Mode build
.\build.ps1 -Member diego -Mode build
.\build.ps1 -Member abraham -Mode build
```

En un clon donde las otras ramas solo estén como referencias remotas, usar `origin/avance/...` en los merge. En el repositorio existente de la opción B, partir de su commit base compartido en lugar de una rama `base-comun` que no se haya creado.

Estos merge reúnen los fuentes de las demostraciones individuales. La conexión de todos los agentes en una sola demostración será otro commit de integración. Los tres programas actuales seguirán siendo presentables por separado usando `-Member`.
