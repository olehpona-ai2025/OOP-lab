package funFarm.service;

import funFarm.core.model.FarmEvent;

public interface EventNotifier {
     void registerListener(EventListener listener);
     void notifyListeners(FarmEvent event);
}
