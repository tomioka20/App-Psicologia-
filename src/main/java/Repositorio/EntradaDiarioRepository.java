package Repositorio;

import Modelo.EntradaDiario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para la entidad EntradaDiario.
 * Proporciona métodos CRUD básicos y consultas personalizadas.
 */
@Repository
public interface EntradaDiarioRepository extends JpaRepository<EntradaDiario, Long> {

    /**
     * Busca todas las entradas de diario para un usuario específico (por su ID),
     * ordenadas descendentemente por la fecha de última actualización.
     * * CORRECCIÓN: El tipo del parámetro usuarioId debe ser Long.
     * @param usuarioId El ID del usuario.
     * @return Una lista de entradas del diario.
     */
    List<EntradaDiario> findByUsuarioIdOrderByFechaActualizacionDesc(Long usuarioId);
    
    /**
     * Busca una entrada de diario por su ID y el ID del usuario.
     * Útil para asegurar que el usuario solo pueda acceder a sus propias entradas.
     * * CORRECCIÓN: El tipo del parámetro usuarioId debe ser Long.
     * @param id El ID de la entrada.
     * @param usuarioId El ID del usuario.
     * @return Un Optional con la EntradaDiario si existe y pertenece al usuario.
     */
    Optional<EntradaDiario> findByIdAndUsuarioId(Long id, Long usuarioId);
}
