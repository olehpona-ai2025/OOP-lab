package lab1.service;

import lab1.core.model.FarmEvent;

@FunctionalInterface
public interface EventListener {
    void pushEvent(FarmEvent event);
}
