package Repositorio;

import Modelo.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Método JpaRepository para buscar un usuario por su correo.
    // Usado por UserDetailsServiceImpl.
    Optional<Usuario> findByCorreo(String correo);
}
