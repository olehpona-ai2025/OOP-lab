package funFarm.core.plants;

import funFarm.core.model.PlantGrowState;
import funFarm.core.plants.strategies.GrowStrategy;

public abstract class Plant {
    public final String name;
    protected PlantGrowState state;
    private GrowStrategy strategy;

    Plant(String name, PlantGrowState state) {
        this.name = name;
        this.state = state;
    }

    protected void setStrategy(GrowStrategy strategy) {
        this.strategy = strategy;
    }

    public void grow() {
        this.state = this.strategy.grow(this.state);
    }

    public PlantGrowState getState() {
        return this.state;
    }
    public String getPlantName() { return this.name; }
    public abstract int getPlantingCost();
    protected float getModifier() {
        return state.getModifier();
    }
    public abstract int getBaseYield();
}
