package Modelo;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Clase de Entidad que representa a un usuario en la base de datos.
 * Implementa UserDetails de Spring Security para facilitar la autenticación.
 */
@Entity
@Table(name = "usuarios")
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, unique = true)
    private String correo;

    @Column(name = "contraseña_hash", nullable = false)
    private String contraseñaHash;

    @Column(nullable = false)
    private String rol; // Mantenemos el rol para Spring Security, pero lo ocultamos en la vista

    // CORRECCIÓN CLAVE PARA POSTGRESQL: 
    // Para almacenar la cadena Base64 (texto largo), usamos columnDefinition = "TEXT".
    // Esto es el equivalente funcional de @Lob en el contexto de PostgreSQL, 
    // pero evita el error "Large Objects may not be used in auto-commit mode" 
    // que ocurre con el tipo CLOB/Large Object.
    @Column(name = "foto_url", columnDefinition = "TEXT") 
    private String fotoUrl; 

    @Column(columnDefinition = "TEXT") // Columna para la descripción (biografía)
    private String descripcion; 

    // Constructores, Getters y Setters
    public Usuario() {
    }

    // Getters y Setters (Añadidos para Foto y Descripción)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContraseñaHash() {
        return contraseñaHash;
    }

    public void setContraseñaHash(String contraseñaHash) {
        this.contraseñaHash = contraseñaHash;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    // --- Implementación de UserDetails (Necesaria para Spring Security) ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(rol));
    }

    @Override
    public String getPassword() {
        return this.contraseñaHash;
    }

    @Override
    public String getUsername() {
        return this.correo;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
