package funFarm.service;

import funFarm.core.model.events.FarmEvent;

@FunctionalInterface
public interface EventListener {
    void pushEvent(FarmEvent event);
}
