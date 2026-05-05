package funFarm.service;

import funFarm.core.farm.Farm;
import funFarm.core.farm.FarmException;
import funFarm.core.farm.FarmStore;
import funFarm.core.model.*;
import funFarm.core.model.events.HarvestEvent;
import funFarm.core.model.events.PlantEvent;
import funFarm.core.plants.Plant;
import funFarm.core.plants.PlantRegistry;
import funFarm.core.warehouse.Warehouse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BaseFarmServiceTest {
    @Mock private PlantRegistry registry;
    @Mock private Warehouse warehouse;
    @Mock private EventNotifier notifier;
    @Mock private Farm farm;
    @Mock private FarmStore farmStore;

    private AutoCloseable closeable;
    private BaseFarmService service;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        service = new BaseFarmService(registry, warehouse, notifier, farm, farmStore);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void createFarmArea() {
        when(farm.addNewArea(100)).thenReturn("area1");
        FarmAreaInfo info = new FarmAreaInfo("area1", "Name", 100, "Wheat", "PLANTED");
        when(farm.getFarmAreaInfo("area1")).thenReturn(info);

        service.createFarmArea(100);

        verify(farm).addNewArea(100);
    }

    @Test
    void removeFarmArea() {
        service.removeFarmArea("area1");

        verify(farm).removeArea("area1");
        verify(farmStore).removeFarmArea("area1");
        verify(notifier).notifyListeners(any());

    }

    @Test
    void setFarmAreaName() {
        FarmAreaInfo info = new FarmAreaInfo("area1", "NewName", 100, "Wheat", "PLANTED");
        when(farm.getFarmAreaInfo("area1")).thenReturn(info);

        service.setFarmAreaName("area1", "NewName");

        verify(farm).setFarmAreaName("area1", "NewName");
    }

    @Test
    void getFarmAreas() {
        FarmAreaInfo info = new FarmAreaInfo("area1", "NewName", 100, "Wheat", "PLANTED");
        when(farm.getFarmAreaInfo()).thenReturn(List.of(info));

        List<FarmAreaInfo> areas = service.getFarmAreas();

        assertThat(areas).containsExactly(info);
    }

    @Test
    void plantFarmAreaSuccess() {
        Plant mockPlant = mock(Plant.class);
        when(mockPlant.getPlantName()).thenReturn("Wheat");
        when(registry.create("Wheat")).thenReturn(mockPlant);
        when(farm.getNeededToPlant("area1", mockPlant)).thenReturn(5);
        when(warehouse.getPlantCount("Wheat")).thenReturn(10);
        PlantResult successResult = new PlantResult(true, "Ok", 5);
        when(farm.plantArea("area1", mockPlant)).thenReturn(successResult);
        FarmAreaInfo info = new FarmAreaInfo("area1", "Name", 100, "Wheat", "PLANTED");
        when(farm.getFarmAreaInfo("area1")).thenReturn(info);

        PlantResult result = service.plantFarmArea("area1", "Wheat");

        assertThat(result.success()).isTrue();
        verify(warehouse).updatePlantCount("Wheat", -5);
        verify(notifier).notifyListeners(any(PlantEvent.class));
    }

    @Test
    void plantFarmAreaNotFound() {
        when(registry.create("Unknown")).thenReturn(null);

        PlantResult result = service.plantFarmArea("area1", "Unknown");

        assertThat(result.success()).isFalse();
    }

    @Test
    void plantFarmAreaFarmException() {
        Plant mockPlant = mock(Plant.class);
        when(registry.create("Wheat")).thenReturn(mockPlant);
        when(farm.getNeededToPlant("area1", mockPlant)).thenThrow(new FarmException("Cannot plant"));

        PlantResult result = service.plantFarmArea("area1", "Wheat");

        assertThat(result.success()).isFalse();
        assertThat(result.msg()).isEqualTo("Cannot plant");
    }

    @Test
    void plantFarmAreaNotEnoughInWarehouse() {
        Plant mockPlant = mock(Plant.class);
        when(registry.create("Wheat")).thenReturn(mockPlant);
        when(farm.getNeededToPlant("area1", mockPlant)).thenReturn(5);
        when(warehouse.getPlantCount("Wheat")).thenReturn(2);

        PlantResult result = service.plantFarmArea("area1", "Wheat");

        assertThat(result.success()).isFalse();
    }

    @Test
    void harvestFarmAreaSuccess() {
        HarvestResult harvestResult = new HarvestResult(true, "Ok", 10, "Wheat");
        when(farm.harvestArea("area1")).thenReturn(harvestResult);
        FarmAreaInfo info = new FarmAreaInfo("area1", "Name", 100, "Wheat", "PLANTED");
        when(farm.getFarmAreaInfo("area1")).thenReturn(info);

        HarvestResult result = service.harvestFarmArea("area1");

        assertThat(result.success()).isTrue();
        verify(warehouse).updatePlantCount("Wheat", 10);
        verify(notifier).notifyListeners(any(HarvestEvent.class));
    }
}
