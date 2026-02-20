 package com.campus360.solicitudes.Servicios;


import java.util.List;

import com.campus360.solicitudes.DTOs.SolicitudDTO;


public interface ISolicitudQueryService {
     public List<SolicitudDTO> servObtenerHistorial(Integer usuarioID); 
     public SolicitudDTO obtenerDetalleCompleto(int solicitudId, String rol); 

 }
