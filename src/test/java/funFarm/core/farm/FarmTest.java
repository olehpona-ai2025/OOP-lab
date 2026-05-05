package funFarm.core.farm;

import funFarm.core.model.FarmAreaInfo;
import funFarm.core.model.PlantGrowState;
import funFarm.core.model.HarvestResult;
import funFarm.core.model.PlantResult;
import funFarm.core.plants.Plant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;


import java.util.List;

class FarmTest {
    private Farm farm;
    private FarmStore store;

    @BeforeEach
    void setUp() {
        store = mock(FarmStore.class);
        farm = new Farm(store);
    }

    @Test
    void shouldAddNewArea() {
        String id = farm.addNewArea(100);
        ArgumentCaptor<FarmArea> areaCaptor = ArgumentCaptor.forClass(FarmArea.class);

        verify(store).saveFarmArea(areaCaptor.capture());
        assertThat(areaCaptor.getValue().area).isEqualTo(100);
        assertThat(areaCaptor.getValue().id).isEqualTo(id);
    }

    @Test
    void shouldRemoveArea() {
        farm.removeArea("Test");

        verify(store).removeFarmArea("Test");
    }

    @Test
    void shouldThrowWhenRemovingNonExistentArea() {
        doThrow(new FarmStoreException("not found")).when(store).removeFarmArea("null");

        assertThatThrownBy(() -> farm.removeArea("null"))
                .isInstanceOf(FarmStoreException.class);

        verify(store).removeFarmArea("null");
    }

    @Test
    void shouldGetNeededToPlant() {
        when(store.getFarmArea("Test1")).thenReturn(new FarmArea("Test1", 10));

        Plant plantMock = mock(Plant.class);
        when(plantMock.getPlantingCost()).thenReturn(2);

        int needed = farm.getNeededToPlant("Test1", plantMock);
        assertThat(needed).isEqualTo(20);

        verify(store).getFarmArea("Test1");
    }

    @Test
    void shouldThrowWhenGettingNeededToPlantForNonExistentArea() {
        Plant plantMock = mock(Plant.class);
        when(plantMock.getPlantingCost()).thenReturn(2);

        when(store.getFarmArea("null")).thenThrow(new FarmStoreException("not found"));

        assertThatThrownBy(() -> farm.getNeededToPlant("null", plantMock))
                .isInstanceOf(FarmStoreException.class);

        verify(store).getFarmArea("null");
    }

    @Test
    void shouldPlantArea() {
        FarmArea area = new FarmArea("test", 10);
        when(store.getFarmArea("test")).thenReturn(area);

        Plant plantMock = mock(Plant.class);

        when(plantMock.getPlantName()).thenReturn("TestPlant");
        when(plantMock.getState()).thenReturn(PlantGrowState.GREW);
        when(plantMock.getPlantingCost()).thenReturn(2);

        PlantResult result = farm.plantArea("test", plantMock);
        
        assertThat(result.success()).isTrue();
        assertThat(result.planted()).isEqualTo(20);

        assertThat(area.getInfo().plantName()).isEqualTo("TestPlant");
        assertThat(area.getInfo().plantState()).isEqualTo("GREW");

        verify(store).getFarmArea("test");
    }

    @Test
    void shouldUnsuccessPlantingNonExistentArea() {
        Plant plantMock = mock(Plant.class);

        when(store.getFarmArea("not-found")).thenThrow(new FarmStoreException("not found"));

        assertThat(farm.plantArea("not-found", plantMock).success())
                .isFalse();

        verify(store).getFarmArea("not-found");
    }

    @Test
    void shouldHarvestArea() {
        FarmArea area = new FarmArea("test", 10);
        when(store.getFarmArea("test")).thenReturn(area);

        Plant plantMock = mock(Plant.class);

        when(plantMock.getPlantName()).thenReturn("TestPlant");
        when(plantMock.getState()).thenReturn(PlantGrowState.GREW);
        when(plantMock.getYield()).thenReturn(2);

        farm.plantArea("test", plantMock);
        
        HarvestResult result = farm.harvestArea("test");
        assertThat(result.success()).isTrue();
        assertThat(result.harvested()).isEqualTo(20);
        assertThat(result.targetPlant()).isEqualTo("TestPlant");

        assertThat(area.getInfo().plantName()).isNull();
        assertThat(area.getInfo().plantState()).isNull();

        verify(store, times(2)).getFarmArea("test");
    }

    @Test
    void shouldUnsuccessHarvestingNonExistentArea() {
        when(store.getFarmArea("null")).thenThrow(new FarmStoreException("not found"));

        assertThat(farm.harvestArea("null").success())
                .isFalse();

        verify(store).getFarmArea("null");
    }

    @Test
    void shouldGrowPlants() {
        FarmArea area1 = new FarmArea("test1", 10);
        FarmArea area2 = new FarmArea("test2", 10);
        when(store.getFarmArea("test1")).thenReturn(area1);
        when(store.getFarmArea("test2")).thenReturn(area2);

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

        farm.areaLoop("test1");
        farm.areaLoop("test2");

        verify(plantMock1).grow();
        verify(plantMock2).grow();
        verify(store).saveFarmArea(area1);
        verify(store).saveFarmArea(area2);
    }

    @Test
    void shouldSetFarmAreaName() {
        FarmArea area = new FarmArea("test1", 10);
        when(store.getFarmArea("test1")).thenReturn(area);
        farm.setFarmAreaName("test1", "hello-world");

        assertThat(area.getName()).isEqualTo("hello-world");
        verify(store).saveFarmArea(area);
    }

    @Test
    void shouldThrowWhenSettingNameForNonExistentArea() {
        when(store.getFarmArea("null")).thenThrow(new FarmStoreException("not found"));

        assertThatThrownBy(() -> farm.setFarmAreaName("null", "hello-world"))
                .isInstanceOf(FarmStoreException.class);

        verify(store).getFarmArea("null");
    }

    @Test
    void shouldGetFarmAreaInfo() {
        List<FarmAreaInfo> infos = List.of(
            new FarmAreaInfo("test1", "Area test1", 10, null, null),
            new FarmAreaInfo("test2", "Area test2", 20, null, null)
        );
        when(store.getFarmAreaInfo()).thenReturn(infos);
        
        List<FarmAreaInfo> result = farm.getFarmAreaInfo();
        assertThat(infos).hasSize(2)
                .extracting(FarmAreaInfo::id, FarmAreaInfo::area)
                .containsExactlyInAnyOrder(tuple("test1", 10), tuple("test2", 20));

        assertThat(result).isEqualTo(infos);
        verify(store).getFarmAreaInfo();
    }
    @Test
    void shouldGetFarmAreaInfoById() {
        FarmArea area = new FarmArea("test1", 10);
        when(store.getFarmArea("test1")).thenReturn(area);
        
        FarmAreaInfo info = farm.getFarmAreaInfo("test1");
        assertThat(info.id()).isEqualTo("test1");
        assertThat(info.area()).isEqualTo(10);

        verify(store).getFarmArea("test1");
    }

    @Test
    void shouldThrowWhenGettingFarmAreaInfoForNonExistentArea() {
        when(store.getFarmArea("null")).thenThrow(new FarmStoreException("not found"));

        assertThatThrownBy(() -> farm.getFarmAreaInfo("null"))
                .isInstanceOf(FarmStoreException.class);
        verify(store).getFarmArea("null");
    }
}
