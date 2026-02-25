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

    @Column(name = "estado")
    private String estado = "ACTIVO"; // Para marcar eliminación lógica

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_solicitud", nullable = false)
    private Solicitud solicitud; // Relación necesaria para el mappedBy de la clase Solicitud

    // Constructor vacío
    public Adjunto() {
    }

    // Constructor completo
    public Adjunto(int idAdjunto, String nombreArchivo, String tipoArchivo, 
                   double tamañoKB, String ruta, Date fechaCarga, String estado; Solicitud solicitud) {
        this.idAdjunto = idAdjunto;
        this.nombreArchivo = nombreArchivo;
        this.tipoArchivo = tipoArchivo;
        this.tamañoKB = tamañoKB;
        this.ruta = ruta;
        this.fechaCarga = fechaCarga;
        this.estado = "ACTIVO";
        this.solicitud=solicitud;
    }

  // --- Métodos de Comportamiento ---

    /**
     * Valida que el tipo de archivo sea permitido
     * @return true si el archivo es válido, false si no
     * @throws IllegalArgumentException si el tipo no es permitido
     */
    public boolean validarTipo() {
        // Lógica de dominio: ejemplo para permitir solo imágenes y documentos
        if (tipoArchivo == null || tipoArchivo.trim().isEmpty()) {
            throw new IllegalArgumentException("El tipo de archivo no puede estar vacío");
        }
        
        String ext = tipoArchivo.toLowerCase().trim();
        boolean esValido = ext.equals("pdf") || ext.equals("jpg") || 
                          ext.equals("png") || ext.equals("docx") || 
                          ext.equals("xlsx");
        
        if (!esValido) {
            throw new IllegalArgumentException(
                "Tipo de archivo no permitido: " + tipoArchivo + 
                ". Permitidos: PDF, JPG, PNG, DOCX, XLSX"
            );
        }
        
        return true;
    }
        /**
     * Valida que el tamaño del archivo sea dentro de los límites
     * @return true si el tamaño es válido
     * @throws IllegalArgumentException si el tamaño excede el límite
     */
    public boolean validarTamaño() {
        final double LIMITE_MB = 10;
        final double LIMITE_KB = LIMITE_MB * 1024;
        
        if (tamañoKB <= 0) {
            throw new IllegalArgumentException("El tamaño debe ser mayor a 0 KB");
        }
        
        if (tamañoKB > LIMITE_KB) {
            throw new IllegalArgumentException(
                String.format("El archivo excede el límite de %d MB. " +
                "Tamaño actual: %.2f MB", 
                (int)LIMITE_MB, tamañoKB / 1024)
            );
        }
        
        return true;
    }

    /**
     * Valida todo el adjunto antes de ser guardado
     * @throws IllegalArgumentException si alguna validación falla
     */
        public void validarAdjunto() {
        validarTipo();
        validarTamaño();
        
        if (nombreArchivo == null || nombreArchivo.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del archivo no puede estar vacío");
        }
        
        if (ruta == null || ruta.trim().isEmpty()) {
            throw new IllegalArgumentException("La ruta del archivo no puede estar vacía");
        }
    }

    /**
     * Prepara el adjunto para ser guardado (solo inicializa la fecha)
     * La persistencia la maneja la capa Service/Repository
     */
    public void guardar() {
        validarAdjunto(); // Valida antes de guardar
        
        if (this.fechaCarga == null) {
            this.fechaCarga = new Date();
        }
        
        this.estado = "ACTIVO";
        
        System.out.println("✓ Adjunto '" + nombreArchivo + "' preparado para guardar");
    }

    /**
     * Marca el adjunto como eliminado (eliminación lógica)
     * No elimina de la BD, solo marca como inactivo
     */
    public void eliminar() {
        if ("INACTIVO".equals(this.estado)) {
            throw new IllegalStateException(
                "El adjunto ya fue eliminado anteriormente"
            );
        }
        
        this.estado = "INACTIVO";
        System.out.println("✓ Adjunto '" + nombreArchivo + "' marcado como eliminado");
    }

    /**
     * Verifica si el adjunto está activo
     */
    public boolean estaActivo() {
        return "ACTIVO".equals(this.estado);
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

    
    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    public Solicitud getSolicitud(){
        return solicitud;
    }

    public void setSolicitud(Solicitud solicitud){
        this.solicitud=solicitud;
    }

}
