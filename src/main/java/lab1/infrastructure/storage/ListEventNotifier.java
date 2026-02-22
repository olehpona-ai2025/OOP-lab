package lab1.infrastructure.storage;

import lab1.core.model.FarmEvent;
import lab1.service.EventListener;
import lab1.service.EventNotifier;

import java.util.ArrayList;
import java.util.List;

public class ListEventNotifier implements EventNotifier {
    private final List<EventListener> listeners = new ArrayList<>();

    public void registerListener(EventListener listener) {
        listeners.add(listener);
    }

    public void notifyListeners(FarmEvent event) {
        for (var listener: listeners) {
            listener.pushEvent(event);
        }
    }
}
