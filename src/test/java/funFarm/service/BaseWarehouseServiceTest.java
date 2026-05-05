package funFarm.service;

import funFarm.core.farm.FarmException;
import funFarm.core.model.FarmReport;
import funFarm.core.model.WarehouseInfo;
import funFarm.core.plants.Plant;
import funFarm.core.plants.PlantRegistry;
import funFarm.core.warehouse.Warehouse;
import funFarm.core.warehouse.WarehouseException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class BaseWarehouseServiceTest {
    @Mock private PlantRegistry registry;
    @Mock private Warehouse warehouse;
    @Mock private Reporter reporter;

    private AutoCloseable closeable;
    private BaseWarehouseService service;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        service = new BaseWarehouseService(warehouse, reporter, registry);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
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
}
