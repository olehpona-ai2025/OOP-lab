package funFarm.core.plants.strategies;

import funFarm.core.model.PlantGrowState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FastStrategyTest {
    private FastStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new FastStrategy();
    }

    @Test
    void shouldGrowImmediatelyToGrewWhenGrowing() {
        PlantGrowState result = strategy.grow(PlantGrowState.GROWING);
        assertThat(result).isEqualTo(PlantGrowState.GREW);
    }

    @Test
    void shouldGrowImmediatelyToOvergrewWhenGrew() {
        PlantGrowState result = strategy.grow(PlantGrowState.GREW);
        assertThat(result).isEqualTo(PlantGrowState.OVERGREW);
    }

    @Test
    void shouldRemainOvergrewWhenAlreadyOvergrew() {
        PlantGrowState result = strategy.grow(PlantGrowState.OVERGREW);
        assertThat(result).isEqualTo(PlantGrowState.OVERGREW);
    }
}
