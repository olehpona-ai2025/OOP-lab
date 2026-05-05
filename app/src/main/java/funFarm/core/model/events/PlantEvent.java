package funFarm.core.model.events;

public record PlantEvent(String plantName, int count) implements FarmEvent {
}
