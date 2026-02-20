package com.campus360.solicitudes.DTOs;

import java.util.Date;

import com.campus360.solicitudes.Dominio.HistorialEstado;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"idHistorial", "estadoAnterior", "estadoNuevo", "comentario", "fecha", "nombreResponsable", "idSolicitud"})
public class HistorialDTO {
    private Integer idHistorial;
    private String estadoAnterior;
    private String estadoNuevo;
    private String comentario;
    private Date fecha;
    private String nombreResponsable;
    private Integer idSolicitud;

     public HistorialDTO(){}
    
    public HistorialDTO(Integer idHistorial, String estadoAnterior, String estadoNuevo, String comentario, Date fecha,
            String nombreResponsable, Integer idSolicitud) {
        this.idHistorial = idHistorial;
        this.estadoAnterior = estadoAnterior;
        this.estadoNuevo = estadoNuevo;
        this.comentario = comentario;
        this.fecha = fecha;
        this.nombreResponsable = nombreResponsable;
        this.idSolicitud = idSolicitud;
    }

    public HistorialDTO(HistorialEstado entidad) {
        this.idHistorial = entidad.getIdHistorial();
        this.estadoAnterior = entidad.getEstadoAnterior();
        this.estadoNuevo = entidad.getEstadoNuevo();
        this.comentario = entidad.getComentario();
        this.fecha = entidad.getFechaCambio();
        
        // Aquí extraes solo lo que necesitas (el nombre)
        if (entidad.getUsuarioResponsable() != null) {
            this.nombreResponsable = entidad.getUsuarioResponsable().getNombre();
        }
        
        // Y aquí el ID de la solicitud 
        if (entidad.getSolicitud() != null) {
            this.idSolicitud = entidad.getSolicitud().getIdSolicitud();
        }
    }
    
    
    public Integer getIdHistorial() {
        return idHistorial;
    }


    public void setIdHistorial(Integer idHistorial) {
        this.idHistorial = idHistorial;
    }


    public String getEstadoAnterior() {
        return estadoAnterior;
    }


    public void setEstadoAnterior(String estadoAnterior) {
        this.estadoAnterior = estadoAnterior;
    }


    public String getEstadoNuevo() {
        return estadoNuevo;
    }


    public void setEstadoNuevo(String estadoNuevo) {
        this.estadoNuevo = estadoNuevo;
    }


    public String getComentario() {
        return comentario;
    }


    public void setComentario(String comentario) {
        this.comentario = comentario;
    }


    public Date getFecha() {
        return fecha;
    }


    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }


    public String getNombreResponsable() {
        return nombreResponsable;
    }


    public void setNombreResponsable(String nombreResponsable) {
        this.nombreResponsable = nombreResponsable;
    }


    public Integer getIdSolicitud() {
        return idSolicitud;
    }


    public void setIdSolicitud(Integer idSolicitud) {
        this.idSolicitud = idSolicitud;
    }


    
   
    

}
