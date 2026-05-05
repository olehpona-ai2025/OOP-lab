package funFarm.core.farm;

import funFarm.core.model.HarvestResult;
import funFarm.core.model.PlantGrowState;
import funFarm.core.model.PlantResult;
import funFarm.core.plants.Plant;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

public class FarmAreaTest {
    @Test
    void shouldPlantAndReturnCorrectValue() {
        Plant plantMock = mock(Plant.class);
        when(plantMock.getState()).thenReturn(PlantGrowState.GROWING);
        when(plantMock.getPlantingCost()).thenReturn(2);

        FarmArea farmArea = new FarmArea(100);
        PlantResult res = farmArea.plant(plantMock);

        assertThat(res.success()).isTrue();
        assertThat(res.planted()).isEqualTo(100*2);
        assertThat(farmArea.getCurrentPlant()).isEqualTo(plantMock);
    }

    @Test
    void shouldThrowWhenPlantingNull() {
        FarmArea farmArea = new FarmArea(100);

        assertThatThrownBy(() -> farmArea.plant(null)).isInstanceOf(FarmException.class);
    }

    @Test
    void shouldNotPlantWhenAlreadyPlantedGrowing() {
        Plant plantMock = mock(Plant.class);
        when(plantMock.getState()).thenReturn(PlantGrowState.GROWING);
        when(plantMock.getPlantingCost()).thenReturn(2);

        Plant plantMock2 = mock(Plant.class);
        when(plantMock2.getState()).thenReturn(PlantGrowState.GROWING);
        when(plantMock2.getPlantingCost()).thenReturn(4);

        FarmArea farmArea = new FarmArea(100);
        farmArea.plant(plantMock);
        PlantResult res = farmArea.plant(plantMock2);

        assertThat(res.success()).isFalse();
        assertThat(res.planted()).isEqualTo(0);

        assertThat(farmArea.getCurrentPlant()).isEqualTo(plantMock);
    }

    @Test
    void shouldPlantWhenAlreadyGrew() {
        Plant plantMock = mock(Plant.class);
        when(plantMock.getState()).thenReturn(PlantGrowState.GREW);
        when(plantMock.getPlantingCost()).thenReturn(2);

        Plant plantMock2 = mock(Plant.class);
        when(plantMock2.getState()).thenReturn(PlantGrowState.GROWING);
        when(plantMock2.getPlantingCost()).thenReturn(4);

        FarmArea farmArea = new FarmArea(100);
        farmArea.plant(plantMock);
        PlantResult res = farmArea.plant(plantMock2);

        assertThat(res.success()).isTrue();
        assertThat(res.planted()).isEqualTo(100*4);
        assertThat(farmArea.getCurrentPlant()).isEqualTo(plantMock2);
    }

    @Test
    void shouldHarvestAndReturnCorrectValue() {
        Plant plantMock = mock(Plant.class);
        when(plantMock.getState()).thenReturn(PlantGrowState.GREW);
        when(plantMock.getPlantName()).thenReturn("TEST");
        when(plantMock.getYield()).thenReturn(2);

        FarmArea farmArea = new FarmArea(100);
        farmArea.plant(plantMock);

        HarvestResult res = farmArea.harvest();

        assertThat(res.success()).isTrue();
        assertThat(res.harvested()).isEqualTo(100*2);
        assertThat(res.targetPlant()).isEqualTo("TEST");
        assertThat(farmArea.getCurrentPlant()).isNull();
    }

    @Test
    void shouldNotHarvestWhenNotPlanted() {
        FarmArea farmArea = new FarmArea(100);

        HarvestResult res = farmArea.harvest();

        assertThat(res.success()).isFalse();
        assertThat(res.harvested()).isEqualTo(0);
        assertThat(res.targetPlant()).isEqualTo(null);
    }

    @Test
    void shouldNotHarvestWhenNotGrew() {
        Plant plantMock = mock(Plant.class);
        when(plantMock.getState()).thenReturn(PlantGrowState.GROWING);
        when(plantMock.getPlantName()).thenReturn("TEST");
        when(plantMock.getYield()).thenReturn(2);

        FarmArea farmArea = new FarmArea(100);
        farmArea.plant(plantMock);

        HarvestResult res = farmArea.harvest();

        assertThat(res.success()).isFalse();
        assertThat(res.harvested()).isEqualTo(0);
        assertThat(res.targetPlant()).isEqualTo("TEST");
        assertThat(farmArea.getCurrentPlant()).isNotNull();
    }

    @Test
    void shouldNotSetNameWhenStringEmpty(){
        FarmArea farmArea = new FarmArea(100);
        assertThatThrownBy(() -> farmArea.setName("")).isInstanceOf(FarmException.class);
    }

    @Test
    void shouldNotSetNameWhenStringNull(){
        FarmArea farmArea = new FarmArea(100);
        assertThatThrownBy(() -> farmArea.setName(null)).isInstanceOf(FarmException.class);
    }
}
