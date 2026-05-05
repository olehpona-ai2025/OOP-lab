package funFarm.service;

import funFarm.core.model.events.FarmEvent;

public interface EventNotifier {
     void registerListener(EventListener listener);
     void notifyListeners(FarmEvent event);
}
