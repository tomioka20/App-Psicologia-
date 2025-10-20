package Utilidades;

import org.springframework.stereotype.Component;


@Component("DiarioUtils") 
public class DiarioUtils {

    
    public static String getEmoji(String emocion) {
        if (emocion == null) {
            return "📝"; 
        }
        
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
