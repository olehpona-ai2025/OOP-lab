package funFarm.core.plants;

import funFarm.core.model.PlantGrowState;
import funFarm.core.plants.strategies.FastStrategy;

public class Potato extends Plant {
    public Potato(PlantGrowState state) {
        super("Potato", state);
        this.setStrategy(new FastStrategy());
    }

    @Override
    public int getBaseYield() {
        return (int) (10 * super.getModifier());
    }

    @Override
    public int getPlantingCost() {
        return 2;
    }
}
