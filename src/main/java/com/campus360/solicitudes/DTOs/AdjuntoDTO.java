package com.campus360.solicitudes.DTOs;

import com.campus360.solicitudes.Dominio.Adjunto;

public class AdjuntoDTO {
    
    private Integer idAdjunto;
    private String nombreArchivo;
    private String ruta;
    private String tipoArchivo;

    public AdjuntoDTO(){}

    // Constructor que mapea desde la entidad
    public AdjuntoDTO(Adjunto entidad) {
        if (entidad != null) {
            this.idAdjunto = entidad.getIdAdjunto();
            this.nombreArchivo = entidad.getNombreArchivo();
            this.ruta = entidad.getRuta();
            this.tipoArchivo = entidad.getTipoArchivo();
        }
    }

    // Getters
    public Integer getIdAdjunto() { return idAdjunto; }
    public String getNombreArchivo() { return nombreArchivo; }
    public String getRuta() { return ruta; }
    public String getTipoArchivo() { return tipoArchivo; }

    

}
