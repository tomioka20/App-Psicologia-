package Controlador;

import Modelo.EntradaDiario;
import Modelo.Usuario; // Importar la entidad Usuario para obtener el nombre
import Repositorio.EntradaDiarioRepository;
import Servicio.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;


@Controller
@RequestMapping("/diario")
public class DiarioController {

    private final EntradaDiarioRepository entradaDiarioRepository;
    private final UsuarioService usuarioService;

    @Autowired
    public DiarioController(EntradaDiarioRepository entradaDiarioRepository, UsuarioService usuarioService) {
        this.entradaDiarioRepository = entradaDiarioRepository;
        this.usuarioService = usuarioService;
    }

    private Usuario getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        // 1. Verificar autenticación básica
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            throw new IllegalStateException("Acceso denegado: Usuario no autenticado.");
        }

        // 2. El nombre de la autenticación es el correo electrónico
        String correo = auth.getName(); 

        // 3. Buscar el usuario en la DB para obtener el objeto completo
        return usuarioService.findByCorreo(correo)
                .orElseThrow(() -> new IllegalStateException("Usuario autenticado no encontrado en DB."));
    }
    
    private Long getAuthenticatedUserId() {
        return getAuthenticatedUser().getId();
    }

    @GetMapping
    public String verDiario(Model model, @RequestParam(required = false) Long notaId) {
        try {
            // 1. Obtener el objeto Usuario autenticado para su ID y su nombre
            Usuario usuario = getAuthenticatedUser();
            Long userId = usuario.getId();
            
            // 2. Determinar el nombre a mostrar (usar nombre real o correo como fallback)
            String nombreMostrado = usuario.getNombre() != null && !usuario.getNombre().isEmpty() 
                                    ? usuario.getNombre() 
                                    : usuario.getCorreo();
            
            // Pasar el nombre del usuario al modelo (Para el saludo en Thymeleaf)
            model.addAttribute("usuarioNombre", nombreMostrado);
            
            // 3. Obtener solo las entradas del usuario autenticado, ordenadas por la más reciente
            List<EntradaDiario> entradas = entradaDiarioRepository.findByUsuarioIdOrderByFechaActualizacionDesc(userId);
            
            model.addAttribute("entradas", entradas);
            model.addAttribute("notaSeleccionadaId", notaId);
            
            // 4. Devolver la plantilla Thymeleaf
            return "diario";

        } catch (IllegalStateException e) {
            // Manejar usuarios no autenticados o no encontrados (redirigir a login)
            return "redirect:/login";
        }
    }

    
    @PostMapping("/crear")
    @ResponseBody
    public ResponseEntity<Map<String, Long>> crearEntrada(
            @RequestParam String emocion,
            @RequestParam String descripcion) {
        
        try {
            Long userId = getAuthenticatedUserId();
            
            EntradaDiario nuevaEntrada = new EntradaDiario();
            nuevaEntrada.setUsuarioId(userId); 
            nuevaEntrada.setEmocion(emocion);
            nuevaEntrada.setDescripcion(descripcion);

            EntradaDiario entradaGuardada = entradaDiarioRepository.save(nuevaEntrada);
            
            Map<String, Long> response = new HashMap<>();
            response.put("id", entradaGuardada.getId());
            
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (IllegalStateException e) {
            // Error de autenticación
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

   
    @PostMapping("/actualizar")
    @ResponseBody
    public ResponseEntity<Void> actualizarEntrada(
            @RequestParam Long id,
            @RequestParam String emocion,
            @RequestParam String descripcion) {

        try {
            Long userId = getAuthenticatedUserId();

            // Usamos findByIdAndUsuarioId para asegurar que solo el dueño pueda editar
            Optional<EntradaDiario> entradaOpt = entradaDiarioRepository.findByIdAndUsuarioId(id, userId);

            if (entradaOpt.isPresent()) {
                EntradaDiario entradaExistente = entradaOpt.get();
                entradaExistente.setEmocion(emocion);
                entradaExistente.setDescripcion(descripcion);
                
                entradaDiarioRepository.save(entradaExistente);
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND); 
            }
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

 
    @DeleteMapping("/eliminar/{id}")
    @ResponseBody
    public ResponseEntity<Void> eliminarEntrada(@PathVariable Long id) {
        try {
            Long userId = getAuthenticatedUserId();

           
            Optional<EntradaDiario> entradaOpt = entradaDiarioRepository.findByIdAndUsuarioId(id, userId);
            
            if (entradaOpt.isPresent()) {
                entradaDiarioRepository.deleteById(id);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT); 
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND); 
            }
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN); 
        }
    }
}
