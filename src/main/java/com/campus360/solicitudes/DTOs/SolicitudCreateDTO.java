package com.campus360.solicitudes.DTOs;

public class SolicitudCreateDTO {
    private String tipo;
    private Integer catalogoId; 
    private String prioridad;   
    private String descripcion; 
    private Integer usuarioId; 

    public SolicitudCreateDTO() {}
    
    public SolicitudCreateDTO(String tipo, Integer catalogoId, String prioridad, String descripcion,
            Integer usuarioId) {
        this.tipo = tipo;
        this.catalogoId = catalogoId;
        this.prioridad = prioridad;
        this.descripcion = descripcion;
        this.usuarioId = usuarioId;
    }

     public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Integer getCatalogoId() {
        return catalogoId;
    }

    public void setCatalogoId(Integer catalogoId) {
        this.catalogoId = catalogoId;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }

    

     

   

    


}
