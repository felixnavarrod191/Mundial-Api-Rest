# Mundial de Clubes — API REST + interfaz (para subir a Render)

Un solo proyecto con las dos cosas, igual que `agenda-apirest`:

- **Interfaz web** (`*_web`): páginas con formulario y lista para guardar datos (Thymeleaf + Bootstrap 5, tema oscuro).
- **API REST** (`*_apirest`): los mismos datos en JSON bajo `/api/...`.
- **MongoDB Atlas**: base `mundial`.

Es **un solo programa** que cubre los dos puntos del ejercicio:

1. **Cliente-Servidor:** las páginas web (`/`, `/clubes`, `/clubes/nuevo`, …).
2. **API REST:** los servicios JSON (`/api/...`), que se publican **en línea con Render** junto con la interfaz.

---

## Qué se sube y qué NO

Se sube a **GitHub** (y Render lo toma de ahí) la carpeta del proyecto con:

| Sí se sube | NO se sube |
|---|---|
| `src/`, `pom.xml`, `Dockerfile`, `.dockerignore`, `.gitignore`, `README.md`, `Mundial_API.postman_collection.json` | **`config/`** (tiene tu clave de Mongo) y `target/` |

> Si subes los archivos arrastrándolos en la web de GitHub, NO arrastres `config/` ni `target/`. Con Git (comandos o GitHub Desktop) el `.gitignore` ya los excluye. Recomendado: repositorio **Private**.

La clave de Mongo no va dentro del código: en Render se escribe como variable de entorno `MONGODB_URI`.

---

## 1. Probar en tu PC (Eclipse)

1. Descomprime e importa: **File → Import → Maven → Existing Maven Projects**.
2. Clic derecho en `MundialApirestApplication.java` → **Run As → Spring Boot App**.
   La conexión sale de `config/application.properties` (ya está lista).
3. Abre **http://localhost:8081/** → debe verse la página de inicio con el tema oscuro. Prueba también `/asociaciones` y `/api/asociaciones`.

## 2. Permitir a Render en MongoDB Atlas

Atlas → **Network Access → Add IP Address → Allow access from anywhere** (`0.0.0.0/0`) → Confirm.
(Las IP de Render cambian, por eso se permite todo.)

## 3. Subir el proyecto a GitHub

**Opción A — con Git (consola, dentro de la carpeta del proyecto):**

```bash
git init
git add .
git commit -m "Mundial API REST"
git branch -M main
git remote add origin https://github.com/TU_USUARIO/mundial-apirest.git
git push -u origin main
```

(Antes crea el repositorio vacío en github.com → **New repository**, nombre `mundial-apirest`, **Private**.)

**Opción B — GitHub Desktop:** *Add local repository* → elige la carpeta → *Commit* → *Publish repository* (marca *Keep this code private*).

Revisa en github.com que **no aparezca** la carpeta `config/`.

## 4. Crear el servicio en Render

1. Entra a **render.com** e inicia sesión con tu cuenta de GitHub.
2. **New + → Web Service → Build and deploy from a Git repository** → elige `mundial-apirest` (si no aparece, *Configure account* y dale acceso al repositorio).
3. Configura:
   - **Name:** `mundial-apirest` (será parte de tu URL).
   - **Language / Runtime:** **Docker** (deja vacíos Build Command y Start Command).
   - **Branch:** `main`.
   - **Instance Type:** **Free**.
4. En **Environment Variables** agrega:
   - **Key:** `MONGODB_URI`
   - **Value:** copia la URL de la línea `spring.data.mongodb.uri=` de tu archivo `config/application.properties` (solo lo que va después del `=`).
5. **Create Web Service**. Render construye la imagen con el `Dockerfile` (tarda unos 3–6 minutos). Cuando el estado diga **Live**, ya está en línea.

## 5. Qué debes ver en línea

Tu URL será `https://mundial-apirest-xxxx.onrender.com`:

| Dirección | Qué muestra |
|---|---|
| `/` | Página de inicio con el título y accesos (Nuevo / Listar) a cada sección |
| `/asociaciones/nuevo`, `/competiciones/nuevo`, `/entrenadores/nuevo`, `/jugadores/nuevo`, `/clubes/nuevo` | Formularios para guardar valores |
| `/asociaciones`, `/competiciones`, … `/clubes` | Listas con lo guardado |
| `/api/asociaciones`, … `/api/clubes` | Los mismos datos en JSON (API REST) |

Sigue el **orden lógico**: Asociación → Competición → Entrenador → Jugador → Club.

> Plan gratis: si nadie lo usa ~15 minutos, el servicio se duerme y la primera visita tarda cerca de un minuto en despertar. Ábrelo antes de mostrárselo al profesor.

## 6. Probar la API con Postman

1. Importa `Mundial_API.postman_collection.json`.
2. En la colección → pestaña **Variables** → cambia `base` por tu URL de Render (sin `/` al final).
3. Ejecuta las carpetas en orden con la base vacía: *1. Crear* debe dar **201**; *4. Pruebas que deben FALLAR* dan **400** y **409**.

## 7. Actualizar después de cambios

Haz `git add .`, `git commit -m "cambios"` y `git push`. Render vuelve a desplegar solo.

## 8. Errores comunes

| Síntoma | Solución |
|---|---|
| Build falla en Render | Abre *Logs*; revisa que `Dockerfile` y `pom.xml` estén en la raíz del repositorio. |
| Servicio "Live" pero la página da error 500 o tarda | Revisa `MONGODB_URI` en *Environment* (sin espacios) y el `0.0.0.0/0` en Atlas. |
| `Authentication failed` | Usuario o clave mal escritos en `MONGODB_URI`. |
| La web carga sin colores | Espera unos segundos y recarga (Ctrl+F5); revisa que exista `src/main/resources/static/css/estilo.css` en el repositorio. |
| Los datos no coinciden con los de tu PC | Cada entorno usa la base que indique su URL; ambos usan `mundial` si pegaste la misma. |

## 9. Después de la entrega

Cambia la contraseña del usuario en Atlas (*Database Access*) y actualiza `MONGODB_URI` en Render y `config/application.properties`.
