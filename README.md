# Sistema de laboratorio

## Compatibilidad

Base de compilacion: Java 21, con `maven.compiler.release=21`. Se limitan tanto
las API disponibles como el bytecode, incluso al compilar con un JDK posterior.
No se requieren funciones experimentales de Java 22.

- JDK de trabajo: Java 21; mantenerlo actualizado dentro de esa version.
- Maven: 3.9.x; en este equipo se utiliza el incluido en NetBeans 21.
- Maven Compiler Plugin: 3.14.1.
- MySQL Connector/J: 8.4.0, conservado del proyecto original.
- Fuentes: `src/main/java`; pruebas futuras: `src/test/java`.

La version del servidor MySQL y los procedimientos almacenados deben verificarse
por separado. Compilar no comprueba la conexion ni las operaciones SQL.
La prueba de conexion de este equipo confirmo MySQL 8.0.44 y Connector/J 8.4.0.

La conexion usa `sslMode=REQUIRED`: exige TLS para autenticar con MySQL sin
desactivar la proteccion del intercambio de claves. Para otro entorno se puede
configurar `SISOL_DB_URL`, `SISOL_DB_USER` y `SISOL_DB_PASSWORD`. Para un servidor
remoto, configurar certificados y `sslMode=VERIFY_IDENTITY`. Los valores locales
de usuario y contrasena originales se conservan como valores predeterminados;
se deben reemplazar antes de desplegar el backend web.

## Compilar y ejecutar en Windows

Desde la raiz del proyecto, en PowerShell:

```powershell
.\maven.cmd -o package
.\maven.cmd -o compile exec:java
.\maven.cmd -o compile exec:java "-Dexec.mainClass=TestConexion"
```

El modo offline usa las dependencias ya descargadas. En un equipo nuevo, omitir
`-o` para que Maven las descargue. El script selecciona el JDK para ese
proceso y restaura `JAVA_HOME` al terminar, sin cambiar Windows globalmente.
Para otras instalaciones:

```powershell
$env:SISOL_JAVA_HOME = 'C:/Java/jdk-21'
$env:SISOL_MAVEN_CMD = 'C:/Tools/apache-maven/bin/mvn.cmd'
.\maven.cmd package
```

Si Maven esta en PATH y `JAVA_HOME` apunta al JDK apropiado:

```powershell
mvn package
mvn compile exec:java
mvn compile exec:java -Dexec.mainClass=TestConexion
```

El JAR se genera en `target/Poryecto_SISOL-1.0-SNAPSHOT.jar`. Usar `exec:java`
para ejecutar con las dependencias; el JAR no incorpora el driver MySQL ni esta
configurado para `java -jar` en la modalidad Swing. Spring Boot genera un JAR
ejecutable para `app.SisolApplication`. Las pruebas del DAO de pacientes se
ejecutan con `.\maven.cmd test` y no modifican la base de datos.

## VS Code

`.vscode/settings.json` selecciona el JDK 21 y el Maven de NetBeans de este equipo.
En otra computadora, ajustar esas rutas. Si el IDE conserva las carpetas antiguas,
ejecutar **Java: Clean Java Language Server Workspace** desde la paleta de comandos
y volver a abrir el proyecto.

## Migracion futura a web

```text
ui (Swing) -> controller -> service -> dao -> MySQL
                              |
                            model
```

Los paquetes `controller`, `service`, `dao` y `model` no importan Swing ni AWT.
`LaboratorioController` acepta una instancia de `LaboratorioService` en su
constructor, conservando el constructor sin argumentos para la interfaz actual.

Para migrar, agregar un backend HTTP que invoque el servicio y un frontend web
que consuma esa API. El navegador no debe conectarse directamente a MySQL.
Java 21 es la base del backend; el frontend tendra sus propias herramientas.

Antes de exponer una API hay que implementar autenticacion y permisos reales,
configuracion externa de credenciales, conexiones JDBC por operacion o un pool,
validacion de entradas y tratamiento de errores. La conexion singleton actual
no esta preparada para solicitudes web concurrentes. Los reportes y las
notificaciones siguen siendo simulaciones del proyecto original.

El DAO de pacientes ya usa `JdbcTemplate` y es un bean `@Repository`.
Spring inyecta su conexion administrada y `LaboratorioService` recibe el DAO
por constructor. `listarPacientes()` y `obtenerPacientePorId(int)` permiten
consultar pacientes; el registro conserva `sp_registrar_paciente` y exige un
ID valido devuelto por el procedimiento. Si el registro lanza una excepcion,
no se debe reintentar automaticamente: el procedimiento pudo haber guardado
el paciente antes de fallar al devolver el ID.

Swing conserva los constructores sin argumentos: el DAO de pacientes utiliza
una conexion independiente por operacion con la configuracion de `DBConnection`.
Los demas DAO aun usan el singleton anterior.

## Primera pantalla web

Este equipo dispone de `config/database-local.properties`, importado por Spring
Boot y excluido de Git y del JAR. Contiene la configuracion local de MySQL para
que ejecutar desde VS Code o una terminal nueva no dependa de variables definidas
en otra terminal. Las variables `SISOL_DB_USER` y `SISOL_DB_PASSWORD`, si existen,
sustituyen los valores locales. No compartir este archivo. En otro equipo,
configurar las variables o crear un archivo local equivalente. Ejecutar siempre
desde la raiz del proyecto para que se encuentre el archivo externo.

Ejecutar `.\maven.cmd spring-boot:run` con las variables de conexion configuradas
y abrir `http://localhost:8080/pacientes`. La ruta `/` redirige a esa pantalla.
El controlador `PacienteWebController` presenta el listado y recibe el formulario
de registro. Thymeleaf muestra errores de validacion y conserva los datos del
formulario cuando hay un error. El registro exitoso redirige al listado para
evitar repetir el envio al actualizar la pagina.

**Agregar paciente** y **Editar** abren un modal. El formulario usa JavaScript
y la API JSON `/api/pacientes` para guardar y actualizar la fila sin recargar
la pagina. Los errores de validacion se muestran en el modal y mantienen los
datos escritos. El ID se toma de la ruta, no de los campos del formulario.
**Actualizar** vuelve a consultar el listado sin navegar a otra pagina.

**Eliminar**, junto a **Editar**, abre una confirmacion antes de enviar `DELETE`.
Los pacientes con ordenes o facturas asociadas no se pueden eliminar. No se
realizan eliminaciones en cascada. La API responde 400 para datos invalidos,
404 para pacientes inexistentes y 409 para restricciones de integridad.
Mientras se guarda o elimina se bloquean los botones para impedir doble envio.
Se requiere JavaScript para estas acciones.

Se verificaron las columnas de `Paciente` y `sp_registrar_paciente` con MySQL,
el listado por HTTP y el rechazo de formularios invalidos. No se insertaron
pacientes de prueba. El registro valido contra MySQL queda pendiente de una
prueba con datos reales autorizados. Las pantallas restantes y la autenticacion
todavia no estan implementadas.
