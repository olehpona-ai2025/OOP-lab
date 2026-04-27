package funFarm.core.plants.strategies;

import funFarm.core.model.PlantGrowState;

public class FastStrategy implements GrowStrategy {
    @Override
    public PlantGrowState grow(PlantGrowState current, int plantAge) {
        if (current == PlantGrowState.GREW) {
            return PlantGrowState.OVERGREW;
        }
        if (current == PlantGrowState.OVERGREW) {
            return current;
        }
        return PlantGrowState.GREW;
    }
}
