package funFarm.core.plants.strategies;

import funFarm.core.model.PlantGrowState;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StepStrategyTest {

    @Test
    void shouldGrowStepByStep() {
        StepStrategy strategy = new StepStrategy(3);

        PlantGrowState state1 = strategy.grow(PlantGrowState.GROWING);
        assertThat(state1).isEqualTo(PlantGrowState.GROWING);

        PlantGrowState state2 = strategy.grow(state1);
        assertThat(state2).isEqualTo(PlantGrowState.GROWING);

        PlantGrowState state3 = strategy.grow(state2);
        assertThat(state3).isEqualTo(PlantGrowState.GREW);

        PlantGrowState state4 = strategy.grow(state3);
        assertThat(state4).isEqualTo(PlantGrowState.GREW);

        PlantGrowState state5 = strategy.grow(state4);
        assertThat(state5).isEqualTo(PlantGrowState.GREW);

        PlantGrowState state6 = strategy.grow(state5);
        assertThat(state6).isEqualTo(PlantGrowState.OVERGREW);

        PlantGrowState state7 = strategy.grow(state6);
        assertThat(state7).isEqualTo(PlantGrowState.OVERGREW);
    }
    
    @Test
    void shouldInstantlyReturnOvergrew_whenStateIsAlreadyOvergrew() {
        StepStrategy strategy = new StepStrategy(10);
        PlantGrowState result = strategy.grow(PlantGrowState.OVERGREW);
        
        assertThat(result).isEqualTo(PlantGrowState.OVERGREW);
    }
}
