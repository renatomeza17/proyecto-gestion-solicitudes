package com.campus360.solicitudes.Servicios;

import java.util.ArrayList;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.campus360.solicitudes.DTOs.SolicitudCreateDTO;
import com.campus360.solicitudes.Dominio.Adjunto;
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


    public List<Solicitud> servObtenerHistorial(Integer usuarioID){ 
         return repoSolicitud.findBySolicitanteIdUsuario(usuarioID);
     }


     public Solicitud obtenerDetalleCompleto(int solicitudId, String rol){
        
         Solicitud solicitud = repoSolicitud.findById(solicitudId).orElse(null);
         if (solicitud != null) {
        // REGLA DE NEGOCIO: Si es APROBADOR y el estado es PENDIENTE
        // Usamos .equalsIgnoreCase por si el frontend manda "aprobador" en minúsculas
        if ("APROBADOR".equalsIgnoreCase(rol) && "PENDIENTE".equalsIgnoreCase(solicitud.getEstado())) {
            
            solicitud.setEstado("EN PROCESO");
            repoSolicitud.save(solicitud);
            
            // Opcional: Aquí podrías registrar en tu tabla de historial
            
            System.out.println("Estado actualizado a EN PROCESO por acceso de Aprobador");
        }
    }                      

         //lógica para calulcar estado de sla
         return solicitud;
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


     public List<Solicitud> servListarSolicitudes(){
        return repoSolicitud.findAll();
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
