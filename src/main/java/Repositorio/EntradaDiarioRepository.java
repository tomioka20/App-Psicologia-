package Repositorio;

import Modelo.EntradaDiario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface EntradaDiarioRepository extends JpaRepository<EntradaDiario, Long> {

  
    List<EntradaDiario> findByUsuarioIdOrderByFechaActualizacionDesc(Long usuarioId);
    
    Optional<EntradaDiario> findByIdAndUsuarioId(Long id, Long usuarioId);
}
