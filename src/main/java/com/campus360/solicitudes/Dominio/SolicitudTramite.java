package com.campus360.solicitudes.Dominio;

import java.util.List;
import java.util.Date;


public class SolicitudTramite extends Solicitud {
    // Atributo privado (-) según el diagrama
    //private Tramite tramite;

    // Constructor vacío
    public SolicitudTramite() {
    }

    // Constructor con parámetros
    public SolicitudTramite(/*Tramite tramite,*/int idSolicitud, Date fechaCreacion, String estado, String prioridad, int slaObjetivo, 
                     Usuario solicitante, List<Adjunto> adjuntos, List<HistorialEstado> historial) {
        super(idSolicitud, fechaCreacion, estado, prioridad, slaObjetivo, solicitante, adjuntos, historial);
        //this.tramite = tramite;
    }

    // --- Métodos de Comportamiento (+) ---

    /**
     * Valida que los documentos adjuntos cumplan con lo requerido por el trámite.
     */
    public void validarDocumentosTramite() {
        // Lógica de dominio: verificar si el trámite tiene requisitos y si se cumplen
        System.out.println("Validando documentos para el trámite...");
    }

    /**
     * Obtiene la lista de requisitos necesarios para procesar este trámite.
     */
    public void obtenerRequisitosTramite() {
        // Lógica para listar las condiciones del trámite asociado
        /*if (this.tramite != null) {
            System.out.println("Obteniendo requisitos para: " + tramite.toString());
        }*/
    }

    // --- Getters y Setters ---

    /*public Tramite getTramite() {
        return tramite;
    }

    public void setTramite(Tramite tramite) {
        this.tramite = tramite;
    }*/

}
