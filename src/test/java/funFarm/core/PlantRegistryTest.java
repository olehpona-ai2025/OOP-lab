package funFarm.core;

import funFarm.core.model.PlantGrowState;
import funFarm.core.plants.Plant;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

public class PlantRegistryTest {

    @Test
    void shouldRegisterAndCreateGrowing() {
        Plant plantMock1 = mock(Plant.class);
        Plant plantMock2 = mock(Plant.class);

        when(plantMock1.getPlantName()).thenReturn("Test1");
        when(plantMock2.getPlantName()).thenReturn("Test2");

        PlantRegistry registry = new PlantRegistry();

        Function<PlantGrowState, Plant> fabricMock1 = mock(Function.class);
        when(fabricMock1.apply(any())).thenReturn(plantMock1);

        Function<PlantGrowState, Plant> fabricMock2 = mock(Function.class);
        when(fabricMock2.apply(any())).thenReturn(plantMock2);

        registry.register(fabricMock1);
        registry.register(fabricMock2);
        clearInvocations(fabricMock1);
        clearInvocations(fabricMock2);

        assertThat(registry.create("Test1")).isEqualTo(plantMock1);
        verify(fabricMock1).apply(PlantGrowState.GROWING);

        assertThat(registry.create("Test2")).isEqualTo(plantMock2);
        verify(fabricMock2).apply(PlantGrowState.GROWING);
    }

    @Test
    void shouldRegisterAndCreateCustom() {
        Plant plantMock1 = mock(Plant.class);
        Plant plantMock2 = mock(Plant.class);

        when(plantMock1.getPlantName()).thenReturn("Test1");
        when(plantMock2.getPlantName()).thenReturn("Test2");

        PlantRegistry registry = new PlantRegistry();

        Function<PlantGrowState, Plant> fabricMock1 = mock(Function.class);
        when(fabricMock1.apply(any())).thenReturn(plantMock1);

        Function<PlantGrowState, Plant> fabricMock2 = mock(Function.class);
        when(fabricMock2.apply(any())).thenReturn(plantMock2);

        registry.register(fabricMock1);
        registry.register(fabricMock2);
        clearInvocations(fabricMock1);
        clearInvocations(fabricMock2);

        assertThat(registry.createWithState("Test1", PlantGrowState.GREW)).isEqualTo(plantMock1);
        verify(fabricMock1).apply(PlantGrowState.GREW);

        assertThat(registry.createWithState("Test2", PlantGrowState.GROWING)).isEqualTo(plantMock2);
        verify(fabricMock2).apply(PlantGrowState.GROWING);
    }

    @Test
    void shouldThrowWhenPlantNotFound() {
        PlantRegistry registry = new PlantRegistry();

        assertThatThrownBy(() -> registry.create("")).isInstanceOf(FarmException.class);
        assertThatThrownBy(() -> registry.createWithState("", PlantGrowState.GREW)).isInstanceOf(FarmException.class);
    }

    @Test
    void shouldThrowWhenStateNull() {
        PlantRegistry registry = new PlantRegistry();

        assertThatThrownBy(() -> registry.createWithState("", null)).isInstanceOf(FarmException.class);
    }
}
