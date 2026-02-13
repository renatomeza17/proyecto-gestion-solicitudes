 package com.campus360.solicitudes.Servicios;
 import java.util.ArrayList;

import com.campus360.solicitudes.DTOs.SolicitudCreateDTO;
import com.campus360.solicitudes.Dominio.Adjunto;

 public interface ISolicitudCommandService {
     public boolean servRegistrarSolicitud(SolicitudCreateDTO dto,ArrayList <Adjunto> adjuntos);
     public boolean anularSolicitud(int solicitudId);

 }
