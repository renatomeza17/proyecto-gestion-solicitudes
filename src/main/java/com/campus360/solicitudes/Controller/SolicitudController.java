package com.campus360.solicitudes.Controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import java.nio.file.Path;


import org.springframework.http.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.campus360.solicitudes.DTOs.SolicitudCreateDTO;
import com.campus360.solicitudes.Dominio.Adjunto;
import com.campus360.solicitudes.Dominio.Solicitud;
// import com.campus360.solicitudes.Dominio.Usuario;
import com.campus360.solicitudes.Servicios.ISolicitudCommandService;
import com.campus360.solicitudes.Servicios.ISolicitudQueryService;
import com.campus360.solicitudes.Servicios.SolicitudService;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;




@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/solicitudes")
public class SolicitudController {

    @Autowired
    private SolicitudService servSolicitud;

    @Autowired
    private ISolicitudCommandService command;
    @Autowired
    private ISolicitudQueryService query;


    //Constructor
    public SolicitudController(ISolicitudCommandService solicitudCommandService,ISolicitudQueryService solicitudQueryService){
        this.command = solicitudCommandService;
        this.query = solicitudQueryService;
    }


    //GETTERS Y SETTERS


      public ISolicitudCommandService getCommand() {
        return command;
    }


    public void setCommand(ISolicitudCommandService command) {
        this.command = command;
    }

    public ISolicitudQueryService getQuery() {
        return query;
    }

    public void setQuery(ISolicitudQueryService query) {
        this.query = query;
    }



    //APIS


    @GetMapping
    public List<Solicitud> listarSolicitudes(@RequestParam Integer usuarioId){
        return servSolicitud.servObtenerHistorial(usuarioId);
    }


    @GetMapping("/{id}")
    public Solicitud consultarSolicitud(@PathVariable Integer id){
        return servSolicitud.obtenerDetalleCompleto(id);
    }


    @PostMapping(value = "/registrar", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
public ResponseEntity<?> crearSolicitud(
        @RequestPart("solicitud") SolicitudCreateDTO dto,
        @RequestPart(value = "archivos", required = false) List<MultipartFile> archivos
) {
    try {
        ArrayList<Adjunto> listaAdjuntosParaBD = new ArrayList<>();

        // 1. Procesar los archivos físicos si es que el usuario envió alguno
        if (archivos != null && !archivos.isEmpty()) {
            for (MultipartFile file : archivos) {
                // Guardamos en el disco duro y obtenemos la ruta -  LLAMO AL OTRO METODO DE ABAJO 
                String rutaFisica = guardarArchivoEnDisco(file);

                // Creamos el objeto Adjunto que se guardará en MySQL
                Adjunto adj = new Adjunto();
                adj.setNombreArchivo(file.getOriginalFilename());
                adj.setRuta(rutaFisica);
                adj.setTipoArchivo(file.getContentType());

                listaAdjuntosParaBD.add(adj);
            }
        }

        // 2. Llamar al servicio con el DTO y la lista de objetos Adjunto
        boolean exito = servSolicitud.servRegistrarSolicitud(dto, listaAdjuntosParaBD);

        if (exito) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("{\"mensaje\": \"Solicitud creada con éxito y archivos guardados\"}");
        } else {
            return ResponseEntity.badRequest().body("{\"error\": \"No se pudo procesar la solicitud\"}");
        }

    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("{\"error\": \"" + e.getMessage() + "\"}");
    }
}



private String guardarArchivoEnDisco(MultipartFile file) {
    try {
        // 1. Definimos la carpeta "uploads" dentro de la raíz del proyecto
        String rootPath = System.getProperty("user.dir");
        String nombreCarpeta = "uploads";

        // 2. Creamos el objeto File para la subcarpeta
        File directory = new File(rootPath, nombreCarpeta);

        // 3. Si la subcarpeta no existe, la creamos
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // 4. Generar nombre único (Timestamp + nombre original)
        String nombreUnico = System.currentTimeMillis() + "_" + file.getOriginalFilename();

        // 5. Usamos Paths.get con dos argumentos para que Java ponga el "/" o "\" correcto
        Path rutaDestino = Paths.get(directory.getAbsolutePath(), nombreUnico);

        // 6. Escribimos los bytes del archivo
        Files.write(rutaDestino, file.getBytes());

        // 7. Retornamos la ruta absoluta para guardarla en la base de datos
        return rutaDestino.toString();

    } catch (IOException e) {
        throw new RuntimeException("Error al escribir el archivo en el servidor: " + e.getMessage());
    }
}



    //RESTO DE PETICIONES (JeanFranco)

    // @PostMapping(value = "/{id}/adjuntos", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    // public ResponseEntity<?> adjuntarArchivos(
    //         @PathVariable Integer id,
    //         @RequestPart(value = "archivos", required = false) List<MultipartFile> archivos
    // ) {
    //     try {
    //         ArrayList<Adjunto> listaAdjuntosParaBD = new ArrayList<>();

    //         if (archivos != null && !archivos.isEmpty()) {
    //             for (MultipartFile file : archivos) {
    //                 String rutaFisica = guardarArchivoEnDisco(file);

    //                 Adjunto adj = new Adjunto();
    //                 adj.setNombreArchivo(file.getOriginalFilename());
    //                 adj.setRuta(rutaFisica);
    //                 adj.setTipoArchivo(file.getContentType());

    //                 listaAdjuntosParaBD.add(adj);
    //             }
    //         }

    //         boolean exito = servSolicitud.servAdjuntarArchivos(id, listaAdjuntosParaBD);

    //         if (exito) {
    //             return ResponseEntity.status(HttpStatus.CREATED)
    //                     .body("{\"mensaje\": \"Adjuntos registrados con éxito\"}");
    //         } else {
    //             return ResponseEntity.badRequest().body("{\"error\": \"No se pudo registrar los adjuntos\"}");
    //         }

    //     } catch (Exception e) {
    //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
    //                 .body("{\"error\": \"" + e.getMessage() + "\"}");
    //     }
    // }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> anularSolicitud(@PathVariable Integer id){

        boolean eliminado=servSolicitud.servAnularSolicitud(id);

        if(eliminado){
            return ResponseEntity.ok().body(" {\"mensaje\": \"La solicitud fue eliminada correctamente\"}");
        }
        else{
            return ResponseEntity.status(HttpStatus.CONFLICT).body(" {\"mensaje\": \"No se pudo eliminar la solicitud\"}");
        }
        

    }












    // @DeleteMapping("/{id}")
    // public ResponseEntity<?> anularSolicitud(@PathVariable Integer id) {
    //     try {
    //         boolean exito = servSolicitud.servAnularSolicitud(id);

    //         if (exito) {
    //             return ResponseEntity.ok().body("{\"mensaje\": \"Solicitud anulada correctamente\"}");
    //         } else {
    //             return ResponseEntity.status(HttpStatus.CONFLICT)
    //                     .body("{\"error\": \"No se pudo anular la solicitud\"}");
    //         }

    //     } catch (Exception e) {
    //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
    //                 .body("{\"error\": \"" + e.getMessage() + "\"}");
    //     }
    // }

    // @PostMapping("/{id}/incidencia")
    // public ResponseEntity<?> registrarIncidencia(
    //         @PathVariable Integer id,
    //         @RequestBody String incidenciaJson
    // ) {
    //     try {
    //         boolean exito = servSolicitud.servRegistrarIncidencia(id, incidenciaJson);

    //         if (exito) {
    //             return ResponseEntity.status(HttpStatus.CREATED)
    //                     .body("{\"mensaje\": \"Incidencia registrada correctamente\"}");
    //         } else {
    //             return ResponseEntity.badRequest().body("{\"error\": \"No se pudo registrar la incidencia\"}");
    //         }

    //     } catch (Exception e) {
    //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
    //                 .body("{\"error\": \"" + e.getMessage() + "\"}");
    //     }
    // }

    // @GetMapping("/reportes")
    // public ResponseEntity<?> generarReportes(
    //         @RequestParam(required = false) String estado,
    //         @RequestParam(required = false) String fechaInicio,
    //         @RequestParam(required = false) String fechaFin
    // ) {
    //     try {
    //         Object reporte = servSolicitud.servGenerarReporte(estado, fechaInicio, fechaFin);
    //         return ResponseEntity.ok(reporte);

    //     } catch (Exception e) {
    //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
    //                 .body("{\"error\": \"" + e.getMessage() + "\"}");
    //     }
    // }

    // @GetMapping("/{id}/sla")
    // public ResponseEntity<?> consultarSla(@PathVariable Integer id) {
    //     try {
    //         Object sla = servSolicitud.servConsultarSla(id);

    //         if (sla == null) {
    //             return ResponseEntity.status(HttpStatus.NOT_FOUND)
    //                     .body("{\"error\": \"No se encontró SLA para la solicitud\"}");
    //         }

    //         return ResponseEntity.ok(sla);

    //     } catch (Exception e) {
    //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
    //                 .body("{\"error\": \"" + e.getMessage() + "\"}");
    //     }
    // }

    // private String guardarArchivoEnDisco(MultipartFile file) {
    //     try {
    //         // 1. Definimos la carpeta "uploads" dentro de la raíz del proyecto
    //         String rootPath = System.getProperty("user.dir");
    //         String nombreCarpeta = "uploads";

    //         // 2. Creamos el objeto File para la subcarpeta
    //         File directory = new File(rootPath, nombreCarpeta);

    //         // 3. Si la subcarpeta no existe, la creamos
    //         if (!directory.exists()) {
    //             directory.mkdirs();
    //         }

    //         // 4. Generar nombre único (Timestamp + nombre original)
    //         String nombreUnico = System.currentTimeMillis() + "_" + file.getOriginalFilename();

    //         // 5. Usamos Paths.get con dos argumentos para que Java ponga el "/" o "\" correcto
    //         Path rutaDestino = Paths.get(directory.getAbsolutePath(), nombreUnico);

    //         // 6. Escribimos los bytes del archivo
    //         Files.write(rutaDestino, file.getBytes());

    //         // 7. Retornamos la ruta absoluta para guardarla en la base de datos
    //         return rutaDestino.toString();

    //     } catch (IOException e) {
    //         throw new RuntimeException("Error al escribir el archivo en el servidor: " + e.getMessage());
    //     }
    // }



}
