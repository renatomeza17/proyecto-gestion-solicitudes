package com.campus360.solicitudes.Repositorio;

import java.util.List;

// import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.campus360.solicitudes.Dominio.Solicitud;

public interface ISolicitudRepository extends JpaRepository<Solicitud, Integer> {
    
    // Spring deduce el SQL solo por el nombre del método:
    // SELECT * FROM solicitud WHERE id_usuario_solicitante = ?
    List<Solicitud> findBySolicitanteIdUsuario(Integer idUsuario);

    @Query("SELECT s.estado FROM Solicitud s WHERE s.idSolicitud = :id")
    String findEstadoById(@Param("id") Integer id);

    
}

// public interface ISolicitudRepository {
//     public int saveSolicitud(Solicitud solicitud);
//     public ArrayList<Solicitud> findAllByUsuario(int usarioID);
//     public Solicitud findById(int idSolicitud);

// }
