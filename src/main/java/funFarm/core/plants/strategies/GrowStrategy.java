package funFarm.core.plants.strategies;

import funFarm.core.model.PlantGrowState;

public interface GrowStrategy {
    PlantGrowState grow(PlantGrowState current, int plantAge);
}
