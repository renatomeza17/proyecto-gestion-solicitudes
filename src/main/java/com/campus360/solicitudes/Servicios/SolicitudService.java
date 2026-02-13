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
         solicitud.setEstado("POR REVISAR");
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


     public Solicitud obtenerDetalleCompleto(int solicitudId){
         Solicitud solicitud = repoSolicitud.findById(solicitudId).orElse(null);
         //lógica para calulcar estado de sla
         return solicitud;
     }


     public boolean anularSolicitud(int solicitudId){
         boolean anulacionExitosa=false;
        repoSolicitud.deleteById(solicitudId);
         //logica de anulación

         anulacionExitosa=true;
         return anulacionExitosa;
     }




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
