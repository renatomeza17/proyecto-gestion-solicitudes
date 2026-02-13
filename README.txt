---------Módulo de Solicitudes - Sistema de Interoperabilidad-----------

Este módulo permite el registro de solicitudes técnicas, integrando información del Catálogo de Trámites y gestionando archivos adjuntos (PDF/Imágenes) de forma eficiente.

-------------------------------------------------------------------------------------------------------
Requisitos Previos

Java: JDK 17 o superior instalado.

Base de Datos: MySQL Server 8.0+.

IDE: VS Code, IntelliJ IDEA o Eclipse.

Internet: Necesario para la primera ejecución (descarga de dependencias de Maven).

-------------------------------------------------------------------------------------------------------------

-----Configuración y Ejecución-----
1. Preparación de la Base de Datos
Antes de correr el proyecto, debes crear la base de datos en tu MySQL:

Abre tu terminal de MySQL o MySQL Workbench.

Ejecuta el siguiente comando:

CREATE DATABASE db_solicitudes;
 
y luego usarlo con 

USE db_solicitudes;

(Nota: No necesitas crear las tablas manualmente, JPA las creará por ti al iniciar).




2. Ajustes en el Código (Obligatorio) ----MUY IMPORTANTEEEE-------
Para que el proyecto conecte con tu base de datos local, abre el archivo:
src/main/resources/application.properties

Y cambia las siguientes líneas con tus credenciales de MySQL:



spring.datasource.username: Poner su usuario de MySQL (casi siempre es root).

spring.datasource.password: Poner su propia clave de MySQL.

spring.datasource.url: Si su puerto de MySQL no es el estándar (3306), deberán cambiarlo (ej. 3307).





3. Ejecución
Abre el proyecto en tu IDE.

Espera a que el IDE reconozca el archivo pom.xml y descargue las dependencias (puede tardar un par de minutos la primera vez).

Busca la clase SolicitudesApplication.java y dale a Run.





--------------------------------------------------------------------------------------------------------------
Gestión de Archivos (Uploads)
El sistema está configurado para ser portátil.

Al subir un archivo, se creará automáticamente una carpeta llamada /uploads dentro de la carpeta raíz del proyecto.

No necesitas configurar rutas de disco C:, el sistema detecta tu ubicación automáticamente.


--------------------------------------------------------------------------------------------------------------
Pruebas con Postman
Para registrar una solicitud con adjuntos, sigue estos pasos en Postman:

Método: POST

URL: http://localhost:8080/api/solicitudes/registrar

Body: Selecciona form-data.

Parámetros (Keys):

solicitud: Escribe tu JSON de prueba. Importante: En la columna Content-Type de esta fila, selecciona application/json.

archivos: Cambia el tipo de Text a File y selecciona uno o varios archivos PDF/Imagen de tu PC.

------------------------------------------------------------------------------------------------------------

Ejemplo de JSON para la Key solicitud:


Solución de problemas comunes:
Error de versión de Java: Si te dice que la versión no coincide, verifica que tu IDE esté usando el JDK 17 en la configuración del proyecto.

Puerto ocupado: Si el puerto 8080 ya está en uso, cámbialo en application.properties agregando: server.port=8081.

Error de Maven: Si no descargan las librerías, dale clic derecho al proyecto -> Maven -> Reload Project.



------------------------------------------------------------------------------------------------------------


Como recomendación crear un Usuario en la base de datos antes de probar el módulo de solicitudes.