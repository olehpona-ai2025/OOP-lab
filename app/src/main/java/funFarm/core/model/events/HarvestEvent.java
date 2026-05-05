package funFarm.core.model.events;

public record HarvestEvent(String plantName, int count) implements FarmEvent{
}
