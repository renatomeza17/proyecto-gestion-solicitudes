package com.campus360.solicitudes.Dominio;

import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "adjuntos")
public class Adjunto {

    
    // Atributos privados (-)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_adjunto")
    private int idAdjunto;

    @Column(name = "nombre_archivo", nullable = false, length = 150)
    private String nombreArchivo;

    @Column(name = "tipo_archivo", length = 50)
    private String tipoArchivo;

    @Column(name = "tamano_kb")
    private double tamañoKB;

    @Column(name = "ruta", nullable = false, length = 255)
    private String ruta;

    @CreationTimestamp
    @Column(name = "fecha_carga", updatable = false)
    private Date fechaCarga;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_solicitud", nullable = false)
    private Solicitud solicitud; // Relación necesaria para el mappedBy de la clase Solicitud

    // Constructor vacío
    public Adjunto() {
    }

    // Constructor completo
    public Adjunto(int idAdjunto, String nombreArchivo, String tipoArchivo, 
                   double tamañoKB, String ruta, Date fechaCarga) {
        this.idAdjunto = idAdjunto;
        this.nombreArchivo = nombreArchivo;
        this.tipoArchivo = tipoArchivo;
        this.tamañoKB = tamañoKB;
        this.ruta = ruta;
        this.fechaCarga = fechaCarga;
    }

  // --- Métodos de Comportamiento ---

    public boolean validarTipo() {
        // Lógica de dominio: ejemplo para permitir solo imágenes y documentos
        if (tipoArchivo == null) return false;
        String ext = tipoArchivo.toLowerCase();
        return ext.equals("pdf") || ext.equals("jpg") || ext.equals("png");
    }

    public boolean validarTamaño() {
        // Lógica de dominio: ejemplo límite de 10MB
        return tamañoKB > 0 && tamañoKB <= 10240;
    }

    public void guardar() {
        // Prepara el objeto para ser persistido por la capa de Service
        if (this.fechaCarga == null) {
            this.fechaCarga = new Date();
        }
    }

    public void eliminar() {
        // Lógica para marcar el objeto como inactivo o limpiar referencias
        System.out.println("Preparando eliminación del adjunto: " + nombreArchivo);
    }

    // --- Getters y Setters ---

    public int getIdAdjunto() {
        return idAdjunto;
    }

    public void setIdAdjunto(int idAdjunto) {
        this.idAdjunto = idAdjunto;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public String getTipoArchivo() {
        return tipoArchivo;
    }

    public void setTipoArchivo(String tipoArchivo) {
        this.tipoArchivo = tipoArchivo;
    }

    public double getTamañoKB() {
        return tamañoKB;
    }

    public void setTamañoKB(double tamañoKB) {
        this.tamañoKB = tamañoKB;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public Date getFechaCarga() {
        return fechaCarga;
    }

    public void setFechaCarga(Date fechaCarga) {
        this.fechaCarga = fechaCarga;
    }

}
