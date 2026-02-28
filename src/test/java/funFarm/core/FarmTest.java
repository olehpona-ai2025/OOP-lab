package funFarm.core;

import funFarm.core.model.FarmAreaInfo;
import funFarm.core.model.PlantGrowState;
import funFarm.core.model.HarvestResult;
import funFarm.core.model.PlantResult;
import funFarm.core.plants.Plant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;


import java.util.List;

class FarmTest {
    private Farm farm;

    @BeforeEach
    void setUp() {
        farm = new Farm();
    }

    @Test
    void shouldAddNewArea() {
        farm.addNewArea(100);
        List<FarmAreaInfo> infos = farm.getFarmAreaInfo();

        assertThat(infos).hasSize(1);
        assertThat(infos.getFirst().area()).isEqualTo(100);
    }

    @Test
    void shouldLoadNewArea() {
        farm.loadNewArea(50, "Test");
        List<FarmAreaInfo> infos = farm.getFarmAreaInfo();

        assertThat(infos).hasSize(1);
        assertThat(infos.getFirst().id()).isEqualTo("Test");
        assertThat(infos.getFirst().area()).isEqualTo(50);
    }

    @Test
    void shouldRemoveArea() throws FarmException {
        farm.loadNewArea(50, "Test");
        farm.removeArea("Test");

        assertThat(farm.getFarmAreaInfo()).isEmpty();
    }

    @Test
    void shouldThrowWhenRemovingNonExistentArea() {
        assertThatThrownBy(() -> farm.removeArea("null"))
                .isInstanceOf(FarmException.class);
    }

    @Test
    void shouldGetNeededToPlant() throws FarmException {
        farm.loadNewArea(10, "Test1");

        Plant plantMock = mock(Plant.class);
        when(plantMock.getPlantingCost()).thenReturn(2);

        int needed = farm.getNeededToPlant("Test1", plantMock);
        assertThat(needed).isEqualTo(20);
    }

    @Test
    void shouldThrowWhenGettingNeededToPlantForNonExistentArea() {
        Plant plantMock = mock(Plant.class);
        when(plantMock.getPlantingCost()).thenReturn(2);

        assertThatThrownBy(() -> farm.getNeededToPlant("null", plantMock))
                .isInstanceOf(FarmException.class);
    }

    @Test
    void shouldPlantArea() throws FarmException {
        farm.loadNewArea(10, "test");

        Plant plantMock = mock(Plant.class);

        when(plantMock.getPlantName()).thenReturn("TestPlant");
        when(plantMock.getState()).thenReturn(PlantGrowState.GREW);
        when(plantMock.getPlantingCost()).thenReturn(2);

        PlantResult result = farm.plantArea("test", plantMock);
        
        assertThat(result.success()).isTrue();
        assertThat(result.planted()).isEqualTo(20);

        List<FarmAreaInfo> infos = farm.getFarmAreaInfo();

        assertThat(infos.getFirst().plantName()).isEqualTo("TestPlant");
        assertThat(infos.getFirst().plantState()).isEqualTo("GREW");
    }

    @Test
    void shouldThrowWhenPlantingNonExistentArea() {
        Plant plantMock = mock(Plant.class);

        assertThatThrownBy(() -> farm.plantArea("not-found", plantMock))
                .isInstanceOf(FarmException.class);
    }

    @Test
    void shouldHarvestArea() throws FarmException {
        farm.loadNewArea(10, "test");

        Plant plantMock = mock(Plant.class);

        when(plantMock.getPlantName()).thenReturn("TestPlant");
        when(plantMock.getState()).thenReturn(PlantGrowState.GREW);
        when(plantMock.getBaseYield()).thenReturn(2);

        farm.plantArea("test", plantMock);
        
        HarvestResult result = farm.harvestArea("test");
        assertThat(result.success()).isTrue();
        assertThat(result.harvested()).isEqualTo(20);
        assertThat(result.targetPlant()).isEqualTo("TestPlant");

        List<FarmAreaInfo> infos = farm.getFarmAreaInfo();
        assertThat(infos.getFirst().plantName()).isEqualTo("null");
        assertThat(infos.getFirst().plantState()).isEqualTo("null");
    }

    @Test
    void shouldThrowWhenHarvestingNonExistentArea() {
        assertThatThrownBy(() -> farm.harvestArea("null"))
                .isInstanceOf(FarmException.class);
    }

    @Test
    void shouldLoopThroughAreasAndGrowPlants() throws FarmException {
        farm.loadNewArea(10, "test1");
        farm.loadNewArea(10, "test2");

        Plant plantMock1 = mock(Plant.class);
        Plant plantMock2 = mock(Plant.class);

        when(plantMock1.getPlantName()).thenReturn("TestPlant1");
        when(plantMock1.getState()).thenReturn(PlantGrowState.GROWING);
        when(plantMock1.getBaseYield()).thenReturn(2);

        when(plantMock2.getPlantName()).thenReturn("TestPlant1");
        when(plantMock2.getState()).thenReturn(PlantGrowState.GROWING);
        when(plantMock2.getBaseYield()).thenReturn(2);


        farm.plantArea("test1", plantMock1);
        farm.plantArea("test2", plantMock2);

        farm.areaLoop();
        
        verify(plantMock1).grow();
        verify(plantMock2).grow();
    }

    @Test
    void shouldSetFarmAreaName() throws FarmException {
        farm.loadNewArea(10, "test1");
        farm.setFarmAreaName("test1", "hello-world");
        
        List<FarmAreaInfo> infos = farm.getFarmAreaInfo();
        assertThat(infos.getFirst().name()).isEqualTo("hello-world");
    }

    @Test
    void shouldThrowWhenSettingNameForNonExistentArea() {
        assertThatThrownBy(() -> farm.setFarmAreaName("null", "hello-world"))
                .isInstanceOf(FarmException.class);
    }

    @Test
    void shouldGetFarmAreaInfo() {
        farm.loadNewArea(10, "test1");
        farm.loadNewArea(20, "test2");
        
        List<FarmAreaInfo> infos = farm.getFarmAreaInfo();
        assertThat(infos).hasSize(2)
                .extracting(FarmAreaInfo::id, FarmAreaInfo::area)
                .containsExactlyInAnyOrder(tuple("test1", 10), tuple("test2", 20));
    }

    @Test
    void shouldPerformCustomAction() throws FarmException {
        farm.loadNewArea(10, "test");
        
        final boolean[] flag = {false};
        farm.customAction(area -> {
            if ("test".equals(area.id)) {
                flag[0] = true;
            }
        });
        
        assertThat(flag[0]).isTrue();
    }

    @Test
    void shouldThrowOnNullCustomAction() {
        assertThatThrownBy(() -> farm.customAction(null))
                .isInstanceOf(FarmException.class);
    }
}
