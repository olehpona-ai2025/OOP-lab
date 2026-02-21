package lab1;

import java.util.UUID;

public class FarmArea {
    public final String id;
    public final int area;

    private String name;

    private Plant currentPlant = null;

    public FarmArea(int area) {
        this(UUID.randomUUID().toString(), area);
        name = "Area " + this.id.substring(0,4);
    }

    public String getName() {
        return name;
    }

    public void setName(String newName) {
        if (newName == null || newName.isEmpty()) {
            throw new FarmException("New name incorrect");
        }
        name = newName;
    }

    public FarmArea(String id, int area) {
        this.id = id;
        this.area = area;
    }

    public FarmOpResult plant(Plant toPlant) {
        if (currentPlant != null && currentPlant.getState() == PlantGrowState.GROWING) {
          return new FarmOpResult(false, "Already planted", 0);
        }

        this.currentPlant = toPlant;
        return new FarmOpResult(true, "", this.getNeededToPlant(toPlant));
    }

    public int getNeededToPlant(Plant plant) {
        return plant.getPlantingCost() * area;
    }

    public Plant getCurrentPlant() {
        return currentPlant;
    }

    public void plantGrowLoop() {
        this.currentPlant.grow();
    }

    public FarmOpResult harvest() {
        if (this.currentPlant == null || this.currentPlant.getState() == PlantGrowState.GROWING) return new FarmOpResult(false, "Not grew yet", 0);

        int harvested = this.currentPlant.getBaseYield() * area;

        this.currentPlant = null;
        return new FarmOpResult(true, "", harvested);
    }
}
