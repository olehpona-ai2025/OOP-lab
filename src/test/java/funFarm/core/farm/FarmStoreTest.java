package funFarm.core.farm;

import funFarm.core.model.FarmAreaInfo;
import funFarm.core.model.PlantGrowState;
import funFarm.core.plants.Plant;
import funFarm.core.plants.strategies.FastStrategy;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.UUID;

public abstract class FarmStoreTest {
    protected abstract FarmStore getFarmStore();

    private static final String ID_1 = UUID.randomUUID().toString();
    private static final String ID_2 = UUID.randomUUID().toString();

    @Test
    void shouldPersistAreaInfoAfterSave() {
        FarmStore store = getFarmStore();

        FarmArea area = createArea(ID_1, 100, "TestPlant", PlantGrowState.GROWING, 0);

        store.saveFarmArea(area);
        List<FarmAreaInfo> infos = store.getFarmAreaInfo();

        assertThat(infos).isNotNull().hasSize(1);
        assertThat(infos.getFirst().id()).isEqualTo(ID_1);
        assertThat(infos.getFirst().area()).isEqualTo(100);
        assertThat(infos.getFirst().plantName()).isEqualTo("TestPlant");
        assertThat(infos.getFirst().plantState()).isEqualTo(PlantGrowState.GROWING.name());
    }

    @Test
    void shouldLoadSavedArea() {
        FarmStore store = getFarmStore();

        FarmArea area = createArea(ID_1, 100, "TestPlant", PlantGrowState.GREW, 2);
        store.saveFarmArea(area);

        FarmArea loaded = store.getFarmArea(ID_1);

        assertThat(loaded.getInfo().id()).isEqualTo(ID_1);
        assertThat(loaded.getInfo().area()).isEqualTo(100);
        assertThat(loaded.getCurrentPlant().getPlantName()).isEqualTo("TestPlant");
        assertThat(loaded.getCurrentPlant().getState()).isEqualTo(PlantGrowState.GREW);
        assertThat(loaded.getCurrentPlant().getAge()).isEqualTo(2);
    }

    @Test
    void shouldThrowErrorWhenAreaMissing() {
        FarmStore store = getFarmStore();

        String missingId = UUID.randomUUID().toString();
        assertThatThrownBy(() -> store.getFarmArea(missingId))
                .isInstanceOf(FarmStoreException.class);
    }

    @Test
    void shouldRemoveFarmArea() {
        FarmStore store = getFarmStore();

        FarmArea area1 = createArea(ID_1, 100, "TestPlant", PlantGrowState.GROWING, 0);
        FarmArea area2 = createArea(ID_2, 200, "TestPlant", PlantGrowState.GREW, 1);
        store.saveFarmArea(area1);
        store.saveFarmArea(area2);

        store.removeFarmArea(ID_1);

        List<FarmAreaInfo> infos = store.getFarmAreaInfo();
        assertThat(infos)
                .hasSize(1)
                .extracting(FarmAreaInfo::id)
                .containsExactly(ID_2);
    }

    @Test
    void shouldThrowErrorWhenRemoveIdNull() {
        FarmStore store = getFarmStore();

        assertThatThrownBy(() -> store.removeFarmArea(null))
                .isInstanceOf(FarmStoreException.class);
    }

    private FarmArea createArea(String id, int area, String plantName, PlantGrowState state, int age) {
        Plant base = new Plant(plantName, 2, 5, new FastStrategy());
        Plant plant = new Plant(base, state, age);
        FarmArea farmArea = new FarmArea(id, area);
        farmArea.plant(plant);
        return farmArea;
    }
}
