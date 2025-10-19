package Modelo;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad JPA para almacenar entradas del diario.
 * Se han añadido campos de auditoría (fechas) y la referencia a un usuario.
 */
@Entity
@Table(name = "entradas_diario")
public class EntradaDiario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FIX 1: Cambiado a Long para que coincida con el ID de la entidad Usuario.
    // NOTA: Para una práctica JPA óptima, este campo debería ser un @ManyToOne a la entidad Usuario.
    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId; 

    // Emoción asociada a la entrada
    @Column(nullable = false)
    private String emocion;

    // FIX 2: Se ELIMINÓ la anotación @Lob para evitar que Hibernate active el
    // manejo de Large Objects (CLOBs) de PostgreSQL, lo cual requiere desactivar
    // el modo auto-commit y provoca el error.
    // Solo se mantiene columnDefinition="TEXT" para usar el tipo nativo de PostgreSQL.
    @Column(columnDefinition = "TEXT", nullable = false)
    private String descripcion;
    
    // Campo de fecha para la creación (no se actualiza)
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    // Campo de fecha para la última actualización (se actualiza en cada cambio)
    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;

    // Constructor por defecto (necesario para JPA)
    public EntradaDiario() {
    }

    // Métodos de ciclo de vida de JPA para automatizar las fechas
    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }

    // --- Getters y Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // Modificado a Long
    public Long getUsuarioId() {
        return usuarioId;
    }

    // Modificado a Long
    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getEmocion() {
        return emocion;
    }

    public void setEmocion(String emocion) {
        this.emocion = emocion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }
}
