package lab1.service;

import lab1.core.model.FarmEvent;

public interface EventNotifier {
     void registerListener(EventListener listener);
     void notifyListeners(FarmEvent event);
}
