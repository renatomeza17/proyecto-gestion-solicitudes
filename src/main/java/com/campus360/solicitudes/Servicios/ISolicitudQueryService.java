 package com.campus360.solicitudes.Servicios;


import java.util.List;

import com.campus360.solicitudes.Dominio.Solicitud;

public interface ISolicitudQueryService {
     public List<Solicitud> servObtenerHistorial(Integer usuarioID); 
     public Solicitud obtenerDetalleCompleto(int solicitudId, String rol); 

 }
