package com.campus360.solicitudes.Repositorio;

import org.springframework.data.jpa.repository.JpaRepository;

import com.campus360.solicitudes.Dominio.HistorialEstado;


public interface IHistorialRepository extends JpaRepository<HistorialEstado, Integer>{

}
