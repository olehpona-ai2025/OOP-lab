package lab1.core;

import lab1.core.model.HarvestResult;
import lab1.core.model.PlantGrowState;
import lab1.core.model.PlantResult;
import lab1.core.plants.Plant;

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
        name = "Area " + this.id.substring(0,4);
    }

    public PlantResult plant(Plant toPlant) {
        if (currentPlant != null && currentPlant.getState() == PlantGrowState.GROWING) {
          return new PlantResult(false, "Already planted", 0);
        }

        this.currentPlant = toPlant;
        return new PlantResult(true, "", this.getNeededToPlant(toPlant));
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

    public HarvestResult harvest() {
        if (this.currentPlant == null || this.currentPlant.getState() == PlantGrowState.GROWING) return new HarvestResult(false, "Not grew yet", 0, currentPlant != null?currentPlant.getPlantName(): null);

        int harvested = this.currentPlant.getBaseYield() * area;
        HarvestResult result = new HarvestResult(true, "", harvested, currentPlant.getPlantName());
        this.currentPlant = null;
        return result;
    }
}
