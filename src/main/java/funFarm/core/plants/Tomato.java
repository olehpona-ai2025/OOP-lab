package funFarm.core.plants;

import funFarm.core.model.PlantGrowState;
import funFarm.core.plants.strategies.RandomStrategy;

public class Tomato extends Plant{
    public Tomato(PlantGrowState state) {
        super("Tomato", state);
        this.setStrategy(new RandomStrategy(5));
    }

    @Override
    public int getBaseYield() {
        return (int) (5*super.getModifier());
    }

    @Override
    public int getPlantingCost() {
        return 1;
    }
}
