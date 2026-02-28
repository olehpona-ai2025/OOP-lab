package funFarm.service;

import funFarm.core.model.FarmEvent;

@FunctionalInterface
public interface EventListener {
    void pushEvent(FarmEvent event);
}
