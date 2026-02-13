// package com.campus360.solicitudes.Repositorio;

// import java.util.ArrayList;

// import com.campus360.solicitudes.Dominio.Solicitud;

// public class RepositorioSolicitud implements ISolicitudRepository {
//     /*Constructor vacío*/
//     public RepositorioSolicitud(){

//     }

//     // --- Métodos de Comportamiento ---
//     public int saveSolicitud(Solicitud solicitud){
//          /*lógica de consulta a base de dato*/
//         return solicitud.getIdSolicitud();
//     }

//     public ArrayList<Solicitud> findAllByUsuario(int usarioID){
//         ArrayList<Solicitud> solicitudes = new ArrayList<Solicitud>();
//         /*lógica de consulta a base de datos*/
//         return solicitudes;
//     }

//     public Solicitud findById(int idSolicitud){
//         Solicitud solicitud = new Solicitud();
//         /*lógica de consulta a base de datos*/
//         return solicitud;
//     }

//     public String findEstadoActual(int idSolicitud){
//         String estado="";
//         /*lógica de consulta a base de datos*/
//         return estado;
//     }

//     public void updateEstado(int idSolicitud, String Estado){
//         /*lógica a base de dato*/
//     }

//     public boolean registrarEventoHistorial(String evento){
//         boolean registroExitoso=false;
//         /*lógica a base de dato*/
//         return registroExitoso;
//     }

// }
