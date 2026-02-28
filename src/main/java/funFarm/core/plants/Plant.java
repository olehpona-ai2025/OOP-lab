package funFarm.core.plants;

import funFarm.core.model.PlantGrowState;

public abstract class Plant {
    public final String name;
    protected PlantGrowState state = PlantGrowState.GROWING;

    Plant(String name, PlantGrowState state) {
        this.name = name;
        this.state = state;
    }

    public void grow() {
        int randomInt = (int) (Math.random() * 3) + 1;
        if (randomInt == 2) {
            state = PlantGrowState.GREW;
        }
    }

    public PlantGrowState getState() {
        return this.state;
    };
    public String getPlantName() { return this.name; }
    public abstract int getPlantingCost();
    public abstract int getBaseYield();
}
