package funFarm.infrastructure.storage;

import funFarm.core.model.events.FarmEvent;
import funFarm.service.EventListener;
import funFarm.service.EventNotifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component("ListEventNotifier")
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
