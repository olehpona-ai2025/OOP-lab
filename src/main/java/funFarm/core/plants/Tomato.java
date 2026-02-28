package funFarm.core.plants;

import funFarm.core.model.PlantGrowState;

public class Tomato extends Plant{
    public Tomato(PlantGrowState state) {
        super("Tomato", state);
    }

    @Override
    public int getBaseYield() {
        return 5;
    }

    @Override
    public int getPlantingCost() {
        return 1;
    }
}
