package funFarm.core.plants.strategies;

import funFarm.core.model.PlantGrowState;

public class RandomStrategy implements GrowStrategy {
    private final int multiply;
    public RandomStrategy(int multiply) {
        this.multiply = multiply;
    }
    @Override
    public PlantGrowState grow(PlantGrowState curr) {
        if (curr == PlantGrowState.OVERGREW) {
            return curr;
        }
        int randomInt = (int) (Math.random() * this.multiply);
        if (randomInt == 0) {
            if (curr == PlantGrowState.GREW) {
                return PlantGrowState.OVERGREW;
            }
            return PlantGrowState.GREW;
        }
        return curr;
    }
}
