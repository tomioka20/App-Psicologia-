package Configuracion; 

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    // Configura el bean del codificador de contraseñas.
    // Necesario para que Spring Security y tu servicio de registro comparen hashes.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Deshabilita CSRF (recomendado para desarrollo, pero considera habilitarlo en producción)
            .csrf(csrf -> csrf.disable()) 
            
            // 1. Configuración de Autorizaciones (PermitAll)
            .authorizeHttpRequests(authorize -> authorize
                // Rutas PÚBLICAS: Acceso sin autenticar
                .requestMatchers(
                    AntPathRequestMatcher.antMatcher("/"),
                    AntPathRequestMatcher.antMatcher("/registro"), 
                    AntPathRequestMatcher.antMatcher("/login"),   
                    AntPathRequestMatcher.antMatcher("/css/**"),  
                    AntPathRequestMatcher.antMatcher("/js/**")    
                ).permitAll()
                
                // Otras rutas requieren autenticación
                .anyRequest().authenticated()
            )
            
            // 2. Configuración del Formulario de Login
            .formLogin(form -> form
                // Indica la URL de tu página de login personalizada
                .loginPage("/login") 
                // Redirige después de un login exitoso
                .defaultSuccessUrl("/dashboard", true) 
                // Redirige a la misma página de login con el parámetro 'error' si falla
                .failureUrl("/login?error") 
                .permitAll()
            )
            
            // 3. Configuración del Logout
            .logout(logout -> logout
                // Define el endpoint para cerrar sesión
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                // Redirige después de un cierre de sesión exitoso
                .logoutSuccessUrl("/login?logout") 
                .permitAll()
            );

        return http.build();
    }
}