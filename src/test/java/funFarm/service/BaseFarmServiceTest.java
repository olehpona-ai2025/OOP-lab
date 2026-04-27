package funFarm.service;

import funFarm.core.*;
import funFarm.core.model.*;
import funFarm.core.plants.Plant;
import funFarm.core.state.FarmState;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BaseFarmServiceTest {

    @Mock private PlantRegistry registry;
    @Mock private Warehouse warehouse;
    @Mock private EventNotifier notifier;
    @Mock private Farm farm;
    @Mock private FarmStore farmStore;
    @Mock private Reporter reporter;

    private AutoCloseable closeable;
    private BaseFarmService service;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        service = new BaseFarmService(registry, warehouse, notifier, farm, farmStore, reporter);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void getPlants() {
        Plant mockPlant = mock(Plant.class);
        when(registry.getPlants()).thenReturn(List.of(mockPlant));

        List<Plant> plants = service.getPlants();

        assertThat(plants).containsExactly(mockPlant);
        verify(registry).getPlants();
    }

    @Test
    void buyPlantsSuccess() {
        Plant mockPlant = mock(Plant.class);
        when(mockPlant.getPlantName()).thenReturn("Wheat");
        when(registry.create("Wheat")).thenReturn(mockPlant);

        boolean result = service.buyPlants("Wheat", 10);

        assertThat(result).isTrue();
        verify(warehouse).updatePlantCount("Wheat", 10);
    }

    @Test
    void buyPlantsNotFound() {
        when(registry.create("Unknown")).thenReturn(null);

        assertThatThrownBy(() -> service.buyPlants("Unknown", 10))
                .isInstanceOf(FarmException.class);
    }

    @Test
    void buyPlantsWarehouseException() {
        Plant mockPlant = mock(Plant.class);
        when(mockPlant.getPlantName()).thenReturn("Wheat");
        when(registry.create("Wheat")).thenReturn(mockPlant);
        doThrow(new WarehouseException("error")).when(warehouse).updatePlantCount("Wheat", 10);

        boolean result = service.buyPlants("Wheat", 10);

        assertThat(result).isFalse();
    }

    @Test
    void createFarmArea() {
        when(farm.addNewArea(100)).thenReturn("area1");
        FarmAreaInfo info = new FarmAreaInfo("area1", "Name", 100, "Wheat", "PLANTED");
        when(farm.getFarmAreaInfo("area1")).thenReturn(info);

        service.createFarmArea(100);

        verify(farm).addNewArea(100);
        verify(farmStore).updateFarmAreaState(any());
    }

    @Test
    void removeFarmArea() {
        service.removeFarmArea("area1");

        verify(farm).removeArea("area1");
        verify(farmStore).removeFarmArea("area1");
    }

    @Test
    void setFarmAreaName() {
        FarmAreaInfo info = new FarmAreaInfo("area1", "NewName", 100, "Wheat", "PLANTED");
        when(farm.getFarmAreaInfo("area1")).thenReturn(info);

        service.setFarmAreaName("area1", "NewName");

        verify(farm).setFarmAreaName("area1", "NewName");
        verify(farmStore).updateFarmAreaState(any());
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
        verify(notifier).notifyListeners(any());
        verify(farmStore).updateFarmAreaState(any());
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
        verify(notifier).notifyListeners(any());
        verify(farmStore).updateFarmAreaState(any());
    }

    @Test
    void growLoop() {
        when(farm.getFarmAreaInfo()).thenReturn(Collections.emptyList());

        service.growLoop();

        verify(farm).areaLoop();
        verify(farmStore).saveFarmState(any());
    }

    @Test
    void turboGrow() {
        when(farm.getFarmAreaInfo()).thenReturn(Collections.emptyList());

        service.turboGrow();

        verify(farm).customAction(any());
        verify(farmStore).saveFarmState(any());
    }

    @Test
    void getReport() {
        FarmReport report = new FarmReport("Wheat", 100);
        when(reporter.getReport()).thenReturn(List.of(report));

        List<FarmReport> result = service.getReport();

        assertThat(result).containsExactly(report);
    }

    @Test
    void getWarehouseInfo() {
        WarehouseInfo info = new WarehouseInfo("Wheat", 10);
        when(warehouse.getInfo()).thenReturn(List.of(info));

        List<WarehouseInfo> result = service.getWarehouseInfo();

        assertThat(result).containsExactly(info);
    }

    @Test
    void loadData() {
        FarmState state = new FarmState(Collections.emptyList());
        when(farmStore.loadFarmState()).thenReturn(state);

        service.loadData();

        verify(farmStore).loadFarmState();
    }
}
