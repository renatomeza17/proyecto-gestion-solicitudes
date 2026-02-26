package com.campus360.solicitudes.DTOs;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


import com.campus360.solicitudes.Dominio.Solicitud;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"idSolicitud", "estado", "prioridad", "fechaCreacion", "nombreSolicitante", "historial"})
public class SolicitudDTO {
    private Integer idSolicitud;
    private String estado;
    private String prioridad;
    private Date fechaCreacion;
    private String nombreSolicitante;
    private Date fechaLimite; // Para que vea el calendario
    private String tiempoRestante; // Ejemplo: "Faltan 2 días" o "Pausado"
    private String indicadorSla; // Ejemplo: "A TIEMPO", "VENCIDO" o "PAUSADO"

    private List<HistorialDTO> historial;
    private List<AdjuntoDTO> adjuntos;

    
    public SolicitudDTO(){}
   

    public SolicitudDTO(Solicitud sol) {
        this.idSolicitud = sol.getIdSolicitud();
        this.estado = sol.getEstado();
        this.prioridad = sol.getPrioridad();
        this.fechaCreacion = sol.getFechaCreacion();
        this.fechaLimite = sol.getFechaLimite();
        this.nombreSolicitante = sol.getSolicitante().getNombre();

        // El DTO solo formatea el resultado que el DOMINIO ya procesó
        this.indicadorSla = sol.calcularEstadoSlaAmigable(); 
        this.tiempoRestante = (this.indicadorSla.equals("PAUSADO")) 
                            ? "Reloj detenido" 
                            : "Revisar fecha límite";
        
        // Convertimos la lista de entidades a lista de DTOs
        this.historial = sol.getHistorial().stream()
                            .map(h -> new HistorialDTO(h)) 
                            .collect(Collectors.toList());

        this.adjuntos = sol.getAdjuntos().stream()
                           .map(AdjuntoDTO::new)
                           .collect(Collectors.toList());

        
    }

    public SolicitudDTO(int idSolicitud, String estado, String prioridad, Date fechaCreacion, String nombreSolicitante,
            List<HistorialDTO> historial) {
        this.idSolicitud = idSolicitud;
        this.estado = estado;
        this.prioridad = prioridad;
        this.fechaCreacion = fechaCreacion;
        this.nombreSolicitante = nombreSolicitante;
        this.historial = historial;
    }


     public int getIdSolicitud() {
        return idSolicitud;
    }

    public void setIdSolicitud(int idSolicitud) {
        this.idSolicitud = idSolicitud;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getNombreSolicitante() {
        return nombreSolicitante;
    }


    public void setNombreSolicitante(String nombreSolicitante) {
        this.nombreSolicitante = nombreSolicitante;
    }

       public Date getFechaLimite() {
        return fechaLimite;
    }


    public void setFechaLimite(Date fechaLimite) {
        this.fechaLimite = fechaLimite;
    }


    public String getTiempoRestante() {
        return tiempoRestante;
    }


    public void setTiempoRestante(String tiempoRestante) {
        this.tiempoRestante = tiempoRestante;
    }


    public String getIndicadorSla() {
        return indicadorSla;
    }


    public void setIndicadorSla(String indicadorSla) {
        this.indicadorSla = indicadorSla;
    }

    public List<HistorialDTO> getHistorial() {
        return historial;
    }

    public void setHistorial(List<HistorialDTO> historial) {
        this.historial = historial;
    }

     public List<AdjuntoDTO> getAdjuntos() {
        return adjuntos;
    }

    public void setAdjuntos(List<AdjuntoDTO> adjuntos) {
        this.adjuntos = adjuntos;
    }

    

}
