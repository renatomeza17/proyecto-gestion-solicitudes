package com.campus360.solicitudes.Dominio;

import java.util.ArrayList;
import java.util.Calendar;
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
import jakarta.persistence.OrderBy;
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

    // Añadir estos campos a tus atributos privados
    @Column(name = "fecha_limite")
    private Date fechaLimite;

    @Column(name = "tiempo_acumulado_ms")
    private Long tiempoAcumuladoMs = 0L; // Tiempo en milisegundos ya gastado por la administración

    @Column(name = "fecha_ultimo_cambio")
    private Date fechaUltimoCambio; // Cuándo entró al estado actual



    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_usuario_solicitante", nullable = false)
    @JsonIgnoreProperties("solicitudes")
    private Usuario solicitante; 

    @ToString.Exclude
    @OneToMany(mappedBy = "solicitud", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Adjunto> adjuntos=new ArrayList<>();


    @ToString.Exclude
    @OneToMany(mappedBy = "solicitud",cascade = CascadeType.ALL)
    @OrderBy("idHistorial ASC")
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
        HistorialEstado historia=new HistorialEstado();

        historia.setFechaCambio(fechaCreacion);
        historia.setEstadoAnterior(null);
        historia.setEstadoNuevo(estado);
        historia.setComentario("Creación de historial"+ historia.getIdHistorial());
        historia.setUsuarioResponsable(solicitante);
        historia.setSolicitud(this);

        historial.add(historia);

        System.out.println("Solicitud creada con fecha: " + fechaCreacion);
    }

    public boolean validarRequisitos() {
        // Lógica: La solicitud debe tener un solicitante y prioridad definida
        return solicitante != null && prioridad != null && !prioridad.isEmpty();
    }


    public void calcularSLA() {
            // 1. Definir el SLA Total según prioridad (en días para el cálculo)
            int diasPlazo = "ALTA".equalsIgnoreCase(this.prioridad) ? 1 : 3;
            this.slaObjetivo = diasPlazo * 24; // Guardamos las horas para el reporte

            // 2. Establecer la fecha límite inicial
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.add(Calendar.DAY_OF_YEAR, diasPlazo);
            
            this.fechaLimite = cal.getTime();
            this.fechaUltimoCambio = new Date(); // El reloj empieza ahora
    }


    public void actualizarSeguimientoSLA(String nuevoEstado) {
        Date ahora = new Date();
        
        // Si el estado anterior CONSUMÍA tiempo (ej. PENDIENTE, EN PROCESO)
        // calculamos cuánto tiempo se gastó hasta este momento.
        if ("PENDIENTE".equals(this.estado) || "EN PROCESO".equals(this.estado)) {
            long diferencia = ahora.getTime() - this.fechaUltimoCambio.getTime();
            this.tiempoAcumuladoMs += diferencia;
        }

        // Si el NUEVO estado es "activo", recalculamos la fecha límite 
        // para que el administrativo no pierda los días que el alumno se demoró.
        if ("PENDIENTE".equals(nuevoEstado) || "EN PROCESO".equals(nuevoEstado)) {
            long slaTotalMs = (long) this.slaObjetivo * 3600000;
            long tiempoRestanteMs = slaTotalMs - this.tiempoAcumuladoMs;
            
            this.fechaLimite = new Date(ahora.getTime() + tiempoRestanteMs);
        }

        this.estado = nuevoEstado;
        this.fechaUltimoCambio = ahora;
    }



    public String getTiempoRestante() {
        if ("APROBADO".equals(estado) || "RECHAZADO".equals(estado)) return "Completado";
        
        long diff = fechaLimite.getTime() - new Date().getTime();
        
        if (diff <= 0) return "VENCIDO";
        
        long horas = diff / 3600000;
        long dias = horas / 24;
        
        return (dias > 0) ? "Faltan " + dias + " días" : "Faltan " + horas + " horas";
    }


    public String calcularEstadoSlaAmigable() {
        if ("OBSERVADO".equalsIgnoreCase(this.estado)) return "PAUSADO";
        if ("APROBADO".equalsIgnoreCase(this.estado)) return "COMPLETADO";
        
        long diff = this.fechaLimite.getTime() - new Date().getTime();
        return (diff < 0) ? "VENCIDO" : "A TIEMPO";
    }
 

    
    public void agregarAdjunto(Adjunto a) {
        if (a != null && a.validarTipo()) {
            this.adjuntos.add(a);
            a.setSolicitud(this);
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

     public Date getFechaLimite() {
        return fechaLimite;
    }

    public void setFechaLimite(Date fechaLimite) {
        this.fechaLimite = fechaLimite;
    }

    public long getTiempoAcumuladoMs() {
        return tiempoAcumuladoMs;
    }

    public void setTiempoAcumuladoMs(long tiempoAcumuladoMs) {
        this.tiempoAcumuladoMs = tiempoAcumuladoMs;
    }

    public Date getFechaUltimoCambio() {
        return fechaUltimoCambio;
    }

    public void setFechaUltimoCambio(Date fechaUltimoCambio) {
        this.fechaUltimoCambio = fechaUltimoCambio;
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
