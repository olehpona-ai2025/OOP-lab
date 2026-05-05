package funFarm.service;

import funFarm.core.plants.Plant;
import funFarm.core.plants.PlantRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class BasePlantServiceTest {
    @Mock private PlantRegistry registry;

    private AutoCloseable closeable;
    private BasePlantService service;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        service = new BasePlantService(registry);
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
}
