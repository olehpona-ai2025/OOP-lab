package funFarm.core.model;

import java.util.Objects;

public class FarmEvent {
    public final FarmEventType eventType;
    public final String targetPlant;
    public final int count;

    private FarmEvent(FarmEventType eventType, String plant, int count) {
        this.eventType = eventType;
        this.count = count;
        this.targetPlant = plant;
    }

    public static FarmEvent planted(String plant, int count) {
        return new FarmEvent(FarmEventType.Planted, plant, count);
    }

    public static FarmEvent harvested(String plant, int count) {
        return new FarmEvent(FarmEventType.Harvested, plant, count);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FarmEvent that = (FarmEvent) o;
        return count == that.count && eventType == that.eventType
                && Objects.equals(targetPlant, that.targetPlant);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventType, count, targetPlant);
    }
}
