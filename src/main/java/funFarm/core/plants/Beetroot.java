package funFarm.core.plants;

import funFarm.core.model.PlantGrowState;
import funFarm.core.plants.strategies.StepStrategy;

public class Beetroot extends Plant{
    public Beetroot(PlantGrowState state) {
        super("Beetroot", state);
        this.setStrategy(new StepStrategy(10));
    }

    @Override
    public int getBaseYield() {
        return (int) (200*super.getModifier());
    }

    @Override
    public int getPlantingCost() {
        return 5;
    }
}
