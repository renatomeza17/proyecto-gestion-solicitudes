package com.campus360.solicitudes.Dominio;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.ToString;

@Entity
@Table(name="usuario")
public class Usuario {
    // Atributos privados según el diagrama UML
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_usuario")
    private int idUsuario;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "rol", length = 30)
    private String rol; 

    @OneToMany(mappedBy = "solicitante")
    @ToString.Exclude
    private List<Solicitud> solicitudes = new ArrayList<>();

    

    // Constructor vacío 
    public Usuario() {
    }

    // Constructor con parámetros
    public Usuario(int idUsuario, String nombre, String email, String rol) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.email = email;
        this.rol = rol;
    }

    // --- Métodos de Comportamiento ---

    public void crearSolicitud() {
        // En una arquitectura de capas, aquí solo iría lógica de estado interna
        // o validaciones de dominio propias del usuario.
        System.out.println("El usuario " + nombre + " está intentando crear una solicitud.");
    }

    public void consultarSolicitudes() {
        // Nota: En capas, la consulta real la haría el Service llamando a Persistence.
        // Este método en el dominio suele representar la capacidad del objeto.
    }

    public void anularSolicitud(int idSolicitud) {
        // Lógica de negocio pura: ej. verificar si el usuario tiene permisos según su Rol
        System.out.println("Solicitando anulación de la solicitud: " + idSolicitud);
    }

    public void reportarIncidencia() {
        // Lógica de dominio para marcar un comportamiento de incidencia
        System.out.println("Incidencia reportada por el usuario: " + idUsuario);
    }

    // --- Getters y Setters ---

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public List<Solicitud> getSolicitudes() {
        return solicitudes;
    }

    public void setSolicitudes(List<Solicitud> solicitudes) {
        this.solicitudes = solicitudes;
    }

}
