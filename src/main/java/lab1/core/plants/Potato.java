package lab1.core.plants;

import lab1.core.model.PlantGrowState;

public class Potato extends Plant {
    public Potato(PlantGrowState state) {
        super("Potato", state);
    }

    @Override
    public int getBaseYield() {
        return 10;
    }

    @Override
    public int getPlantingCost() {
        return 2;
    }
}
