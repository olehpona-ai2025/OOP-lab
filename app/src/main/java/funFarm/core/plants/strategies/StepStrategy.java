package funFarm.core.plants.strategies;

import funFarm.core.model.PlantGrowState;

public class StepStrategy implements GrowStrategy{
    private final int neededToChange;
    public StepStrategy(int needed) {
        neededToChange = needed;
    }

    @Override
    public PlantGrowState grow(PlantGrowState current, int plantAge) {
        if (current == PlantGrowState.OVERGREW) {
            return current;
        }
        
        if (plantAge >= neededToChange * 2) {
            return PlantGrowState.OVERGREW;
        } else if (plantAge >= neededToChange) {
            return PlantGrowState.GREW;
        } else {
            return current;
        }
    }
}
