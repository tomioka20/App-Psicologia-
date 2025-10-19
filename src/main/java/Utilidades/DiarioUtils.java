package Utilidades;

import org.springframework.stereotype.Component;

/**
 * Clase de utilidad para el módulo Diario.
 * Contiene métodos estáticos que pueden ser accedidos directamente desde Thymeleaf
 * para realizar transformaciones de datos o lógica de presentación simple (como mapeo de emojis).
 */
@Component("DiarioUtils") // Nombre del bean para referenciar en Thymeleaf: T(DiarioUtils)
public class DiarioUtils {

    /**
     * Mapea el nombre de una emoción a su correspondiente emoji Unicode.
     * @param emocion El nombre de la emoción (String).
     * @return El emoji correspondiente o un icono de nota por defecto.
     */
    public static String getEmoji(String emocion) {
        if (emocion == null) {
            return "📝"; // Icono de nota por defecto
        }
        
        // Mapeo de emojis basado en las constantes definidas en el frontend
        switch (emocion) {
            case "Felicidad":
                return "😊";
            case "Tristeza":
                return "😔";
            case "Ansiedad":
                return "😰";
            case "Enojo":
                return "😡";
            case "Calma":
                return "🧘";
            case "Confusión":
                return "🤔";
            default:
                return "📝"; 
        }
    }
}
