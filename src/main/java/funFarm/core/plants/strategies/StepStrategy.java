package funFarm.core.plants.strategies;

import funFarm.core.model.PlantGrowState;

public class StepStrategy implements GrowStrategy{
    private int step = 0;
    private final int neededToChange;
    public StepStrategy(int needed) {
        neededToChange = needed;
    }

    @Override
    public PlantGrowState grow(PlantGrowState current) {
        if (current == PlantGrowState.OVERGREW) {
            return current;
        }
        
        step++;
        
        if (step >= neededToChange * 2) {
            return PlantGrowState.OVERGREW;
        } else if (step >= neededToChange) {
            return PlantGrowState.GREW;
        } else {
            return current;
        }
    }
}
