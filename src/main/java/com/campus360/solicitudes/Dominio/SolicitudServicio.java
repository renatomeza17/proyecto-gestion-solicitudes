package com.campus360.solicitudes.Dominio;

import java.util.List;
import java.util.Date;

public class SolicitudServicio extends Solicitud{
     // Atributo privado (-)
    //private Servicio servicio;

    public SolicitudServicio() {
        super();
    }

    public SolicitudServicio(/*Servicio servicio,*/ int idSolicitud, Date fechaCreacion, String estado, String prioridad, int slaObjetivo, 
                     Usuario solicitante, List<Adjunto> adjuntos, List<HistorialEstado> historial) {
        super(idSolicitud, fechaCreacion, estado, prioridad, slaObjetivo,solicitante, adjuntos, historial);
        //this.servicio = servicio;
    }

    // --- Métodos de Comportamiento (+) ---

    public void validarCondicionesServicio() {
        // Lógica: Verificar si el servicio asociado está disponible
        System.out.println("Validando condiciones para el servicio...");
    }

    public void obtenerReglasServicio() {
        // Lógica: Consultar las reglas de negocio del servicio
        /*if (this.servicio != null) {
            System.out.println("Obteniendo reglas para el servicio.");
        }*/
    }

    // --- Getters y Setters ---

    /*public Servicio getServicio() {
        return servicio;
    }

    public void setServicio(Servicio servicio) {
        this.servicio = servicio;
    }*/

}
