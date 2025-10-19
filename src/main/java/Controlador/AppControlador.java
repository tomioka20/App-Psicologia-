package Controlador;

import Modelo.Usuario;
import Servicio.UsuarioService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@SpringBootApplication
@ComponentScan(basePackages = {"Controlador", "Servicio", "Configuracion", "Repositorio"})
@EnableJpaRepositories(basePackages = "Repositorio")
@EntityScan(basePackages = "Modelo")
@Controller
public class AppControlador {

    private final UsuarioService usuarioService;
    
    // Inyección de dependencias
    public AppControlador(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }
    
    public static void main(String[] args) {
        SpringApplication.run(AppControlador.class, args);
    }
    
    // --- Mapeos de Vistas PÚBLICAS ---
    
    @GetMapping("/")
    public String verPaginaDeInicio() { return "index"; }
    
    @GetMapping("/login")
    public String verFormularioDeLogin() { return "login"; }
    
    @GetMapping("/registro")
    public String verFormularioDeRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registro";
    }

    // --- Mapeo de Acción (Registro POST) ---
    
    @PostMapping("/registro")
    public String registrarUsuario(@ModelAttribute("usuario") Usuario usuario, Model model) {
        try {
            usuarioService.registrarNuevoUsuario(usuario);
            // Redirección a login después del registro exitoso
            return "redirect:/login";
        } catch (IllegalStateException e) {
            // Si el correo ya existe, recarga la página de registro con el mensaje de error
            model.addAttribute("error", e.getMessage());
            model.addAttribute("usuario", usuario);
            return "registro";
        }
    }
    
    // --- Mapeos PROTEGIDOS (Requieren autenticación) ---

    /**
     * El destino después de un login exitoso.
     */
    @GetMapping("/dashboard")
    public String showDashboard() { 
        return "dashboard"; 
    }
    
    /**
     * Mapeo para la navegación al Chatbot.
     */
    @GetMapping("/chatbox")
    public String verChatBot() { 
        return "chatbox"; 
    }
    
    /**
     * Mapeo para la navegación al Diario.
     * NOTA: El método original fue ELIMINADO para evitar el conflicto con DiarioController.
     * DiarioController ahora maneja la ruta base "/diario".
     */
    
    // NOTA: La ruta /perfil se manejará en el PerfilControlador.java
    
    /**
     * Mapeo de ejemplo para Recursos Adicionales.
     */
    @GetMapping("/recursos")
    public String verRecursos() { 
        return "recursos"; 
    }
}
