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


    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }


    public Usuario registrarNuevoUsuario(Usuario usuario) {
    
        if (usuarioRepository.findByCorreo(usuario.getCorreo()).isPresent()) {
            throw new IllegalStateException("El correo ya está registrado en la base de datos local.");
        }

        
        String rawPassword = usuario.getContraseñaHash();
        String hashedPassword = passwordEncoder.encode(rawPassword);

        usuario.setContraseñaHash(hashedPassword);
        
    
        if (usuario.getRol() == null || usuario.getRol().isEmpty()) {
             usuario.setRol("ROLE_USER");
        }

       
        return usuarioRepository.save(usuario);
    }

  
    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id);
    }

  
    @Transactional
    public void actualizarPerfil(Long id, String nombre, String descripcion, String fotoUrl) {
        // Buscar el usuario por ID. Si no existe, lanza una excepción.
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con ID: " + id));

      
        usuario.setNombre(nombre);
        usuario.setDescripcion(descripcion);
        usuario.setFotoUrl(fotoUrl);
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void borrarUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new IllegalStateException("No se puede borrar el usuario: ID no encontrado.");
        }
        usuarioRepository.deleteById(id);
    }

    public Optional<Usuario> findByCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo);
    }
}
