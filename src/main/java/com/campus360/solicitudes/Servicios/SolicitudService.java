package com.campus360.solicitudes.Servicios;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.campus360.solicitudes.DTOs.ActualizarSolicitudDTO;
import com.campus360.solicitudes.DTOs.SolicitudCreateDTO;
import com.campus360.solicitudes.DTOs.SolicitudDTO;
import com.campus360.solicitudes.Dominio.Adjunto;
import com.campus360.solicitudes.Dominio.HistorialEstado;
import com.campus360.solicitudes.Dominio.Solicitud;
import com.campus360.solicitudes.Dominio.Usuario;
import com.campus360.solicitudes.Repositorio.IAlmacenamiento;

import com.campus360.solicitudes.Repositorio.ISolicitudRepository;
import com.campus360.solicitudes.Repositorio.IUsuarioRepository;

import jakarta.transaction.Transactional;

@Service
public class SolicitudService implements ISolicitudCommandService, ISolicitudQueryService {

    @Autowired
    private ISolicitudRepository repoSolicitud;
    @Autowired
    private IUsuarioRepository repoUsuario;
  

    
    @Autowired
    private IAlmacenamiento almacenamiento;

    @Transactional // Garantiza que todo se guarde o nada se guarde
    public boolean servRegistrarSolicitud(SolicitudCreateDTO dto,ArrayList <Adjunto> adjuntos){
         boolean registroExitoso = false;

        //CatalogoService.obetnerRequisitos(servicioID)
        registroExitoso = validarDatosYAdjuntos();
        //llamada a guardar adjuntos a sistema de almacenamiento
        almacenamiento.guardarAdjunto();

        Usuario solicitante = repoUsuario.findById(dto.getUsuarioId())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + dto.getUsuarioId()));

        Solicitud solicitud = new Solicitud();
        solicitud.setTipo(dto.getTipo());
        solicitud.setCatalogoId(dto.getCatalogoId());
        solicitud.setDescripcion(dto.getDescripcion());
        solicitud.setPrioridad(dto.getPrioridad());

         
         solicitud.setSolicitante(solicitante);
    
         //Logica para cacular sla
         solicitud.calcularSLA();
        
         solicitud.setEstado("PENDIENTE");
         solicitud.crear();


         // 5. LO QUE FALTA: Procesar y vincular adjuntos
        if (adjuntos != null && !adjuntos.isEmpty()) {
            for (Adjunto adj : adjuntos) {
                adj.setSolicitud(solicitud);
                // Usas tu método 'agregarAdjunto' que ya tienes
                solicitud.agregarAdjunto(adj); 
                
                // Si el almacenamiento es físico, podrías llamar aquí a:
                // almacenamiento.guardarAdjunto(adj); 
            }
        }




         repoSolicitud.save(solicitud);
         

         return registroExitoso;
   
    }


    private boolean validarDatosYAdjuntos(){
        //lógica de validación
         return true;
    }


    public List<SolicitudDTO> servObtenerHistorial(Integer usuarioID){ 
        List<Solicitud> solicitudes = repoSolicitud.findBySolicitanteIdUsuario(usuarioID);
         return solicitudes.stream()
                      .map(sol -> new SolicitudDTO(sol))
                      .collect(Collectors.toList());
     }


     public SolicitudDTO obtenerDetalleCompleto(int solicitudId, String rol){
        
         Solicitud solicitud = repoSolicitud.findById(solicitudId).orElse(null);
         if (solicitud != null) {
        // REGLA DE NEGOCIO: Si es APROBADOR y el estado es PENDIENTE
        // Usamos .equalsIgnoreCase por si el frontend manda "aprobador" en minúsculas
        if ("APROBADOR".equalsIgnoreCase(rol) && "PENDIENTE".equalsIgnoreCase(solicitud.getEstado())) {
            
            solicitud.setEstado("EN PROCESO");
            
            
            // Opcional: Aquí podrías registrar en tu tabla de historial
            HistorialEstado h = new HistorialEstado();
            h.setEstadoAnterior("PENDIENTE");
            h.setEstadoNuevo("EN PROCESO");
            h.setComentario("Visto por el aprobador");
            h.setFechaCambio(new Date());
            h.setSolicitud(solicitud);

            solicitud.getHistorial().add(h);

            solicitud.actualizarSeguimientoSLA("EN PROCESO");
            repoSolicitud.save(solicitud);

            System.out.println("Estado actualizado a EN PROCESO por acceso de Aprobador");
        }
         //lógica para calulcar estado de sla
         return new SolicitudDTO(solicitud);
    }                      

       return null; 
    }


     public boolean servAnularSolicitud(int solicitudId){
         
        Solicitud porEliminar=repoSolicitud.findById(solicitudId).orElse(null);

        String estado=porEliminar.getEstado();


         if("PENDIENTE".equals(estado)){
            repoSolicitud.deleteById(solicitudId);
            return true;
         }
         else{
            
            return false;
            
         }
        
    

         
     }


     public List<SolicitudDTO> servListarSolicitudes(){
        List<Solicitud> solicitudes = repoSolicitud.findAll();

        return solicitudes.stream()
                      .map(s -> new SolicitudDTO(s))
                      .collect(Collectors.toList());
        
     }





     public String guardarArchivoEnDisco(MultipartFile file) {
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


    public List<Adjunto> procesarArchivos(List<MultipartFile> archivos, Solicitud solicitud) {
    List<Adjunto> lista = new ArrayList<>();
    
    if (archivos != null && !archivos.isEmpty()) {
        for (MultipartFile archivo : archivos) {
            if (!archivo.isEmpty()) {
                // Llamamos a tu lógica de escritura física
                String rutaEnDisco = guardarArchivoEnDisco(archivo);
                
                // Creamos el objeto para la Base de Datos
                Adjunto adj = new Adjunto();
                adj.setNombreArchivo(archivo.getOriginalFilename());
                adj.setRuta(rutaEnDisco); // Guardamos el String de la ruta
                adj.setTipoArchivo(archivo.getContentType());
                adj.setSolicitud(solicitud); // Vinculamos a la solicitud actual
                
                lista.add(adj);
            }
        }
    }
    return lista;
}






     public boolean servActualizarSolicitud(Integer idSolicitud,ActualizarSolicitudDTO dto, String rol, List<MultipartFile> nuevosAdjuntos){
        Solicitud solicitud = repoSolicitud.findById(idSolicitud).orElse(null);
        
        String estadoAnterior = solicitud.getEstado();
        String nuevoEstado = dto.getEstado();
        
        // 1. VALIDACIÓN DE PERMISOS POR ROL
                if ("ESTUDIANTE".equalsIgnoreCase(rol)) {
                    // El estudiante SOLO puede corregir si la solicitud está OBSERVADA
                    if (!"OBSERVADO".equalsIgnoreCase(solicitud.getEstado())) {
                        return false; 
                    }
                    // Forzamos que el estado pase a PENDIENTE al subsanar
                    nuevoEstado = "PENDIENTE";
                } 
                else if (!"APROBADOR".equalsIgnoreCase(rol)) {
                    // Si no es ni estudiante ni aprobador, no tiene permiso
                    return false;
                }

                List<Adjunto> newAdjuntos = procesarArchivos(nuevosAdjuntos, solicitud);
                solicitud.getAdjuntos().addAll(newAdjuntos);

                // 2. ACTUALIZACIÓN INTELIGENTE DEL SLA
                // Aquí es donde el tiempo se reanuda y la fecha límite se "empuja" hacia adelante
                solicitud.actualizarSeguimientoSLA(nuevoEstado);


                // Agregamos nuevos archivos si existen
                if (nuevosAdjuntos != null) {
                    for (Adjunto adj : newAdjuntos) {
                        solicitud.agregarAdjunto(adj);
                    }
    }

                // 3. REGISTRO EN EL HISTORIAL
                HistorialEstado h = new HistorialEstado();
                h.setEstadoAnterior(estadoAnterior);
                h.setEstadoNuevo(nuevoEstado);
                h.setComentario(dto.getComentario());
                h.setFechaCambio(new Date());
                h.setSolicitud(solicitud);

                solicitud.getHistorial().add(h);

                // 4. PERSISTENCIA
                repoSolicitud.save(solicitud);
                return true;
     }




    //  public String obtenerRol(Authentication auth) {
    // // Esto extrae el primer rol que encuentre (ej. "ROLE_ESTUDIANTE")
    // return auth.getAuthorities().stream()
    //            .map(GrantedAuthority::getAuthority)
    //            .findFirst()
    //            .orElse("SIN_ROL");
    // }

    // public String obtenerUsername(Authentication auth) {
    // return auth.getName(); // Retorna el código de alumno o username
    // }



     public IUsuarioRepository getRepoUsuario() {
        return repoUsuario;
    }


    public void setRepoUsuario(IUsuarioRepository repoUsuario) {
        this.repoUsuario = repoUsuario;
    }









}


// public class SolicitudService implements ISolicitudCommandService, ISolicitudQueryService {
//     // Atributos privados 
//     private ISolicitudRepository repositorio;
//     private IAlmacenamiento almacenamiento;

//     /*Constructor*/
//     public SolicitudService(ISolicitudRepository repositorio,IAlmacenamiento almacenamiento){
//         this.repositorio = repositorio;
//         this.almacenamiento = almacenamiento;
//     }

//     // --- Métodos de Comportamiento ---
//     public boolean registrarSolicitud(String Datos,int servicioid,ArrayList <Adjunto> adjuntos){
//         boolean registroExitoso = false;
        
//         //CatalogoService.obetnerRequisitos(servicioID)
//         registroExitoso = validarDatosYAdjuntos();
//         //llamada a guardar adjuntos a sistema de almacenamiento
//         almacenamiento.guardarAdjunto();

//         Solicitud solicitud = new Solicitud();
//         //Logica para cacular sla
//         solicitud.setEstado("POR REVISAR");
//         repositorio.saveSolicitud(solicitud);

//         return registroExitoso;
   
//     }

//     public ArrayList<Solicitud> obtenerHistorial(int usuarioID){ 
//         return repositorio.findAllByUsuario(usuarioID);
//     }

//     public Solicitud obtenerDetalleCompleto(int solicitudId){
//         Solicitud solicitud = repositorio.findById(solicitudId);
//         //lógica para calulcar estado de sla
//         return solicitud;
//     }

//     public boolean anularSolicitud(int solicitudId){
//         boolean anulacionExitosa=false;
//         //logica de anulación
//         return anulacionExitosa;
//     }

//     private boolean validarDatosYAdjuntos(){
//         //lógica de validación
//         return true;
//     }


// }
