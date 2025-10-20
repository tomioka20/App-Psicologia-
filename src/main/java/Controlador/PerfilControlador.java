package Controlador;

import Modelo.Usuario;
import Servicio.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;


@Controller
public class PerfilControlador {

    private final UsuarioService usuarioService;

    @Autowired
    public PerfilControlador(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    
    private Optional<Usuario> getAuthenticatedUserFromDb() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated() || 
            authentication.getPrincipal().equals("anonymousUser")) {
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();
        String correo = null;

        // Si el principal es el objeto Usuario (ideal)
        if (principal instanceof Usuario) {
            correo = ((Usuario) principal).getCorreo();
        // Si el principal es el UserDetails de Spring Security (común)
        } else if (principal instanceof UserDetails) {
            correo = ((UserDetails) principal).getUsername();
        } else {
            // Último recurso: intentar como String (el username/correo)
            correo = principal.toString();
        }
        
        // Usamos el correo obtenido para buscar el objeto Usuario COMPLETO en la DB.
        if (correo != null) {
            return usuarioService.findByCorreo(correo);
        }
        
        return Optional.empty();
    }


    // 1. Mostrar el formulario de perfil
    @GetMapping("/perfil")
    public String mostrarPerfil(Model model) {
        
        Optional<Usuario> usuarioDb = getAuthenticatedUserFromDb();
        
        if (usuarioDb.isEmpty()) {
            // Si no hay usuario o la sesión ha expirado, redirige al login
            return "redirect:/login"; 
        }

        // Carga el objeto Usuario con la descripción y foto para que Thymeleaf lo use
        model.addAttribute("usuario", usuarioDb.get());
        return "perfil"; 
    }

    // 2. Procesar la edición del perfil (Nombre, Descripción, Foto URL)
    @PostMapping("/perfil/editar")
    public String editarPerfil(@ModelAttribute("usuario") Usuario usuarioForm, RedirectAttributes redirectAttributes) {
        
        // Obtener el ID del usuario actual de la sesión (más seguro que confiar en el formulario)
        Optional<Usuario> usuarioActualOpt = getAuthenticatedUserFromDb();
        
        if (usuarioActualOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("mensaje", "Error: Sesión expirada o no autorizada.");
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            return "redirect:/login";
        }

        Usuario usuarioActual = usuarioActualOpt.get();
        
      
        if (!usuarioActual.getId().equals(usuarioForm.getId())) {
             redirectAttributes.addFlashAttribute("mensaje", "Error: Intento de edición no autorizado.");
             redirectAttributes.addFlashAttribute("tipoMensaje", "error");
             return "redirect:/perfil";
        }
        
        try {
            
            usuarioService.actualizarPerfil(
                usuarioActual.getId(), 
                usuarioForm.getNombre(), 
                usuarioForm.getDescripcion(), 
                usuarioForm.getFotoUrl()
            );
            
            redirectAttributes.addFlashAttribute("mensaje", "¡Perfil interactivo actualizado con éxito!");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } catch (Exception e) {
            // Manejo de errores
            redirectAttributes.addFlashAttribute("mensaje", "Error al actualizar el perfil: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
        }
        
        return "redirect:/perfil";
    }


    @PostMapping("/perfil/borrar")
    public String borrarCuenta(@RequestParam("confirmacion") String confirmacion, RedirectAttributes redirectAttributes) {
        
        Optional<Usuario> usuarioActualOpt = getAuthenticatedUserFromDb();
        
        if (usuarioActualOpt.isEmpty()) {
            return "redirect:/login";
        }
        
        Usuario usuarioActual = usuarioActualOpt.get();
        
        if (!"BORRAR CUENTA".equals(confirmacion)) {
            redirectAttributes.addFlashAttribute("mensaje", "La confirmación no es correcta. No se borró la cuenta.");
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            return "redirect:/perfil";
        }

        try {
            usuarioService.borrarUsuario(usuarioActual.getId());
            // Invalida la sesión (el logout)
            return "redirect:/logout"; 
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Ocurrió un error al intentar borrar la cuenta.");
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            return "redirect:/perfil";
        }
    }
}
