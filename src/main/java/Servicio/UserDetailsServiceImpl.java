package Servicio; 

import Modelo.Usuario;
import Repositorio.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UserDetailsServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

  
    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        // 1. Buscar el usuario en la base de datos por correo
        Usuario usuario = usuarioRepository.findByCorreo(correo)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con correo: " + correo));
        
        // 2. Crear y devolver el objeto UserDetails de Spring Security.
        // Spring Security toma el username (correo), la contraseña (hash) y las autoridades.
        // Luego utiliza el PasswordEncoder para comparar el hash.
        return new org.springframework.security.core.userdetails.User(
            usuario.getCorreo(),               // Username (correo)
            usuario.getContraseñaHash(),       // Contraseña (hash encriptado)
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")) // Roles/Autoridades
        );
    }
}
