package com.campus360.solicitudes.Servicios;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    
         //Logica para cacular sla
         solicitud.calcularSLA();
         
         solicitud.setSolicitante(solicitante);
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


     public boolean servActualizarSolicitud(Integer idSolicitud,ActualizarSolicitudDTO dto, String rol){
        Solicitud solicitud = repoSolicitud.findById(idSolicitud).orElse(null);
        if ("APROBADOR".equalsIgnoreCase(rol)){
            solicitud.setEstado(dto.getEstado());

            //Se guarda en el historial
            HistorialEstado h = new HistorialEstado();
            h.setEstadoAnterior(solicitud.getEstado());
            h.setEstadoNuevo(dto.getEstado());
            h.setComentario(dto.getComentario());
            h.setFechaCambio(new Date());
            h.setSolicitud(solicitud);

            solicitud.getHistorial().add(h);
            
            repoSolicitud.save(solicitud);
            return true;
        }
        else{
            return false;
        }

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
