package lab1;

import java.util.UUID;

public class FarmArea {
    final String id;
    final int area;

    private Plant currentPlant = null;

    private final Warehouse warehouse;

    public FarmArea(int area, Warehouse warehouse) {
        this(UUID.randomUUID().toString(), area, warehouse);
    }

    public FarmArea(String id, int area, Warehouse warehouse) {
        this.id = id;
        this.area = area;
        this.warehouse = warehouse;
    }

    public FarmOpResult plant(Plant toPlant) {
        int available = warehouse.getPlantCount(toPlant.getPlantName());

        int neededForPlant = toPlant.getPlantingCost() * area;

        if (available < neededForPlant){
            return new FarmOpResult(false, "Not enough to plant, needed " + neededForPlant + " but have " + available, neededForPlant);
        }

        this.currentPlant = toPlant;

        warehouse.updatePlantCount(this.currentPlant.getPlantName(), -neededForPlant);
        return new FarmOpResult(true, "", neededForPlant);
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

        warehouse.updatePlantCount(this.currentPlant.getPlantName(), harvested);
        this.currentPlant = null;
        return new FarmOpResult(true, "", harvested);
    }
}
