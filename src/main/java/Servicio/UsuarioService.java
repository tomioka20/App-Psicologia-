package Servicio;

import Modelo.Usuario;
import Repositorio.UsuarioRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    // La inyección se resuelve correctamente aquí
    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // --- MÉTODOS DE REGISTRO ---
    public Usuario registrarNuevoUsuario(Usuario usuario) {
        // 1. Validar unicidad
        if (usuarioRepository.findByCorreo(usuario.getCorreo()).isPresent()) {
            throw new IllegalStateException("El correo ya está registrado en la base de datos local.");
        }

        // 2. Hashear la contraseña
        String rawPassword = usuario.getContraseñaHash();
        String hashedPassword = passwordEncoder.encode(rawPassword);

        usuario.setContraseñaHash(hashedPassword);
        
        // 3. Asignar rol por defecto (IMPORTANTE para Spring Security)
        if (usuario.getRol() == null || usuario.getRol().isEmpty()) {
             usuario.setRol("ROLE_USER");
        }

        // 4. Guardar
        return usuarioRepository.save(usuario);
    }

    // --- MÉTODOS DEL PERFIL ---

    /**
     * Busca un usuario por su ID. Necesario para cargar el perfil completo en la vista.
     * @param id El ID del usuario.
     * @return Un Optional con el Usuario, o vacío si no se encuentra.
     */
    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id);
    }

    /**
     * Actualiza el nombre, la descripción y la URL de la foto del perfil.
     * @param id ID del usuario a modificar.
     * @param nombre Nuevo nombre.
     * @param descripcion Nueva biografía/descripción.
     * @param fotoUrl Nueva URL de la foto.
     */
    @Transactional
    public void actualizarPerfil(Long id, String nombre, String descripcion, String fotoUrl) {
        // Buscar el usuario por ID. Si no existe, lanza una excepción.
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con ID: " + id));

        // Actualizar los campos editables
        usuario.setNombre(nombre);
        usuario.setDescripcion(descripcion);
        usuario.setFotoUrl(fotoUrl);

        // Guardar la entidad actualizada.
        usuarioRepository.save(usuario);
    }

    /**
     * Elimina permanentemente la cuenta del usuario.
     * @param id ID del usuario a borrar.
     */
    @Transactional
    public void borrarUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new IllegalStateException("No se puede borrar el usuario: ID no encontrado.");
        }
        usuarioRepository.deleteById(id);
    }

    /**
     * Busca un usuario por su correo electrónico.
     * @param correo Correo electrónico del usuario.
     * @return Un Optional con el Usuario.
     */
    public Optional<Usuario> findByCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo);
    }
}
