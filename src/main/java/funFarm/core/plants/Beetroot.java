package funFarm.core.plants;

import funFarm.core.model.PlantGrowState;

public class Beetroot extends Plant{
    public Beetroot(PlantGrowState state) {
        super("Beetroot", state);
    }

    @Override
    public int getBaseYield() {
        return 200;
    }

    @Override
    public int getPlantingCost() {
        return 5;
    }
}
