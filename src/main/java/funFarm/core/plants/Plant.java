package funFarm.core.plants;

import funFarm.core.model.PlantGrowState;
import funFarm.core.plants.strategies.GrowStrategy;

public class Plant {
    private final String name;
    private PlantGrowState state;
    private final GrowStrategy strategy;
    private final int plantingCost;
    private final int baseYield;
    private int age = 0;

    public Plant(String name, int plantingCost, int baseYield, GrowStrategy strategy) {
        this.name = name;
        this.state = PlantGrowState.GROWING;
        this.plantingCost = plantingCost;
        this.baseYield = baseYield;
        this.strategy = strategy;
    }

    public Plant(Plant oldPlant, PlantGrowState state, int age) {
        this.name = oldPlant.name;
        this.state = state;
        this.plantingCost = oldPlant.plantingCost;
        this.baseYield = oldPlant.baseYield;
        this.strategy = oldPlant.strategy;
        this.age = age;
    }

    public void grow() {
        this.state = this.strategy.grow(this.state, age);
        age++;
    }

    public PlantGrowState getState() {
        return this.state;
    }
    public int getAge() {return this.age;}
    public String getPlantName() { return this.name; }
    public int getPlantingCost() {
        return this.plantingCost;
    };
    public int getYield(){
        return (int)(this.baseYield * this.state.getModifier());
    };
    public int getBaseYield() { return this.baseYield; }

}
