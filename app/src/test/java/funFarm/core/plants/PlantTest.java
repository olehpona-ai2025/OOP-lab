package funFarm.core.plants;

import funFarm.core.model.PlantGrowState;
import funFarm.core.plants.strategies.GrowStrategy;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

public class PlantTest {

    @Test
    void shouldReturnUnmodifiedYieldAsBase() {
        GrowStrategy strategy = mock(GrowStrategy.class);
        Plant plant = new Plant("Name", 10, 10, strategy);

        assertThat(plant.getBaseYield()).isEqualTo(10);
    }

    @Test
    void shouldSetStateFromStrategy() {
        GrowStrategy strategy = mock(GrowStrategy.class);
        PlantGrowState state = mock(PlantGrowState.class);

        when(state.getModifier()).thenReturn(20F);

        when(strategy.grow(any(), anyInt())).thenReturn(state);
        Plant plant = new Plant("Name", 10, 10, strategy);
        plant.grow();

        assertThat(plant.getYield()).isEqualTo(10*20);
    }

    @Test
    void shouldProvideStrategyWithCorrectState() {
        GrowStrategy strategy = mock(GrowStrategy.class);

        when(strategy.grow(any(), anyInt())).thenReturn(PlantGrowState.GREW);

        Plant plant = new Plant("Name", 10, 10, strategy);

        plant.grow();
        plant.grow();
        plant.grow();

        verify(strategy).grow(PlantGrowState.GROWING, 0);
        verify(strategy).grow(PlantGrowState.GREW, 1);
        verify(strategy).grow(PlantGrowState.GREW, 2);
    }
}
