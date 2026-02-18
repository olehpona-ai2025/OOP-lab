package lab1;

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
}
