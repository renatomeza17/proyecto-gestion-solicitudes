package com.campus360.solicitudes.Dominio;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.ToString;






@Entity
@Table(name="solicitud")


public class Solicitud {
     // Atributos privados (-) según el diagrama

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idSolicitud;

    @Column(name = "fecha_creacion")
    @CreationTimestamp
    private Date fechaCreacion;

    @Column(name = "estado",length = 20, nullable = false)
    private String estado;

    @Column(name = "prioridad", length = 15)
    private String prioridad;

    @Column(name = "sla_objetivo", nullable = false)
    private int slaObjetivo;

    //ATRIBUTOS DEL TRAMITE
    @Column(name = "tipo", length = 50)
    private String tipo;

    @Column(name = "id_catalogo")
    private Integer catalogoId;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;




    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_usuario_solicitante", nullable = false)
    @JsonIgnoreProperties("solicitudes")
    private Usuario solicitante; 

    @ToString.Exclude
    @OneToMany(mappedBy = "solicitud", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Adjunto> adjuntos=new ArrayList<>();


    @ToString.Exclude
    @OneToMany(mappedBy = "solicitud",cascade = CascadeType.ALL)
    private List<HistorialEstado> historial=new ArrayList<>();

    
    // Constructor vacío (inicializa las listas para evitar NullPointerException)
    public Solicitud() {
        
    }

    // Constructor completo
    public Solicitud(int idSolicitud, Date fechaCreacion, String estado, String prioridad, int slaObjetivo, 
                     Usuario solicitante, List<Adjunto> adjuntos, List<HistorialEstado> historial) {
        this.idSolicitud = idSolicitud;
        this.fechaCreacion = fechaCreacion;
        this.estado = estado;
        this.prioridad = prioridad;
        this.slaObjetivo = slaObjetivo;
        this.solicitante = solicitante;
        this.adjuntos = adjuntos != null ? adjuntos : new ArrayList<>();
        this.historial = historial != null ? historial : new ArrayList<>();
    }

    // --- Métodos de Comportamiento (+) ---

    public void crear() {
        if (this.fechaCreacion == null) {
            this.fechaCreacion = new Date();
        }
        this.estado = "PENDIENTE";
        System.out.println("Solicitud creada con fecha: " + fechaCreacion);
    }

    public boolean validarRequisitos() {
        // Lógica: La solicitud debe tener un solicitante y prioridad definida
        return solicitante != null && prioridad != null && !prioridad.isEmpty();
    }

    public void cambiarEstado(String nuevoEstado) {
        this.estado = nuevoEstado;
        System.out.println("Estado actualizado a: " + nuevoEstado);
    }

    public void calcularSLA() {
        // Lógica de dominio para determinar el tiempo límite según la prioridad
        if ("ALTA".equals(this.prioridad)) {
            this.slaObjetivo = 24; // 24 horas
        } else {
            this.slaObjetivo = 72; // 72 horas
        }
    }

    public void agregarAdjunto(Adjunto a) {
        if (a != null && a.validarTipo()) {
            this.adjuntos.add(a);
        }
    }

    public String obtenerDetalle() {
        return "Solicitud [Estado: " + estado + ", Prioridad: " + prioridad + ", Solicitante: " + 
                (solicitante != null ? solicitante.getNombre() : "N/A") + "]";
    }

    // --- Getters y Setters ---

    public int getIdSolicitud(){
        return idSolicitud;
    }

    public void setIdSolicitud(int idSolicitud){
        this.idSolicitud = idSolicitud;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
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

    public int getSlaObjetivo() {
        return slaObjetivo;
    }

    public void setSlaObjetivo(int slaObjetivo) {
        this.slaObjetivo = slaObjetivo;
    }

    public Usuario getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(Usuario solicitante) {
        this.solicitante = solicitante;
    }

    public List<Adjunto> getAdjuntos() {
        return adjuntos;
    }

    public void setAdjuntos(List<Adjunto> adjuntos) {
        this.adjuntos = adjuntos;
    }

    public List<HistorialEstado> getHistorial() {
        return historial;
    }

    public void setHistorial(List<HistorialEstado> historial) {
        this.historial = historial;
    }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public Integer getCatalogoId() { return catalogoId; }
    public void setCatalogoId(Integer catalogoId) { this.catalogoId = catalogoId; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

}
