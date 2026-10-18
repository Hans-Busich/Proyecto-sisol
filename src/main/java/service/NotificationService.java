package service;

import java.util.ArrayList;
import java.util.List;

public class NotificationService {

    // Lista de correos electrónicos que desean recibir notificaciones
    private List<String> subscribers = new ArrayList<>();

    public void subscribe(String email) {
        if (email != null && !email.isBlank() && !subscribers.contains(email)) {
            subscribers.add(email);
            System.out.println("[Observer] Suscrito: " + email);
        }
    }

    public void unsubscribe(String email) {
        subscribers.remove(email);
        System.out.println("[Observer] Eliminado: " + email);
    }

    public void notifyAllClients(String message) {
        System.out.println("=== Notificaciones Enviadas ===");
        for (String email : subscribers) {
            // Simulación real de envío
            System.out.println("Enviando a: " + email + " → " + message);
        }
    }

    public List<String> getSubscribers() {
        return subscribers;
    }
}

