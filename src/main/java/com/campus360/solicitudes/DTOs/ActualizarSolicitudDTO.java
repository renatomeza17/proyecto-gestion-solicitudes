package com.campus360.solicitudes.DTOs;

public class ActualizarSolicitudDTO {
    
    private String estado;
    private String comentario;

    
   
    public ActualizarSolicitudDTO() {
    }


    public ActualizarSolicitudDTO(String estado, String comentario) {
        
        this.estado = estado;
        this.comentario = comentario;
    }


     public String getEstado() {
        return estado;
    }


    public void setEstado(String estado) {
        this.estado = estado;
    }


    public String getComentario() {
        return comentario;
    }


    public void setComentario(String comentario) {
        this.comentario = comentario;
    }







}
