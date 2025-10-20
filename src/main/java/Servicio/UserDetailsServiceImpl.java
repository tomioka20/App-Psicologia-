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
        
        Usuario usuario = usuarioRepository.findByCorreo(correo)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con correo: " + correo));
        
      
        return new org.springframework.security.core.userdetails.User(
            usuario.getCorreo(),               
            usuario.getContraseñaHash(),       
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
}
