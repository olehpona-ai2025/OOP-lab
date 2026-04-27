package funFarm.core;

import funFarm.core.model.PlantGrowState;
import funFarm.core.plants.Plant;
import funFarm.core.plants.strategies.FastStrategy;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

public class PlantRegistryTest {

    @Test
    void shouldRegisterAndCreateGrowing() {
        Plant mockPlant1 = new Plant("test1", 10, 10, new FastStrategy());
        Plant mockPlant2 = new Plant("test2", 5, 5, new FastStrategy());

        PlantRegistry registry = new PlantRegistry();

        registry.register(mockPlant1);
        registry.register(mockPlant2);

        Plant created1 = registry.create("test1");
        Plant created2 = registry.create("test2");

        assertThat(created1).isNotSameAs(mockPlant1);
        assertThat(created1.getPlantName()).isEqualTo(mockPlant1.getPlantName());
        assertThat(created1.getState()).isEqualTo(PlantGrowState.GROWING);

        assertThat(created2).isNotSameAs(mockPlant2);
        assertThat(created2.getPlantName()).isEqualTo(mockPlant2.getPlantName());
        assertThat(created2.getState()).isEqualTo(PlantGrowState.GROWING);
    }

    @Test
    void shouldRegisterAndCreateCustom() {
        Plant mockPlant1 = new Plant("test1", 10, 10, new FastStrategy());
        Plant mockPlant2 = new Plant("test2", 5, 5, new FastStrategy());

        PlantRegistry registry = new PlantRegistry();

        registry.register(mockPlant1);
        registry.register(mockPlant2);

        Plant created1 = registry.createWithState("test1", PlantGrowState.GROWING);
        Plant created2 = registry.createWithState("test2", PlantGrowState.GREW);

        assertThat(created1).isNotSameAs(mockPlant1);
        assertThat(created1.getPlantName()).isEqualTo(mockPlant1.getPlantName());
        assertThat(created1.getState()).isEqualTo(PlantGrowState.GROWING);

        assertThat(created2).isNotSameAs(mockPlant2);
        assertThat(created2.getPlantName()).isEqualTo(mockPlant2.getPlantName());
        assertThat(created2.getState()).isEqualTo(PlantGrowState.GREW);
    }

    @Test
    void shouldReturnNullWhenPlantNotFound() {
        PlantRegistry registry = new PlantRegistry();

        assertThat(registry.create("")).isNull();
        assertThat(registry.createWithState("", PlantGrowState.GREW)).isNull();
    }

    @Test
    void shouldThrowWhenStateNull() {
        PlantRegistry registry = new PlantRegistry();

        assertThatThrownBy(() -> registry.createWithState("", null)).isInstanceOf(FarmException.class);
    }
}
