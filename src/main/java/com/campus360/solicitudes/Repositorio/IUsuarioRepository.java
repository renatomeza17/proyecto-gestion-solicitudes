package com.campus360.solicitudes.Repositorio;



import org.springframework.data.jpa.repository.JpaRepository;


import com.campus360.solicitudes.Dominio.Usuario;

public interface IUsuarioRepository extends JpaRepository<Usuario, Integer> {

    // ArrayList<Solicitud> findBySolicitanteIdUsuario(Integer usuarioID);

}
