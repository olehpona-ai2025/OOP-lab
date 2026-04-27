package funFarm.core;

import funFarm.core.model.WarehouseInfo;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

public abstract class WarehouseTest {
    protected abstract Warehouse getWarehouse();

    @Test
    void shouldPersistDataAfterSave() {
        Warehouse warehouse = getWarehouse();

        warehouse.updatePlantCount("Test", 2);

        assertThat(warehouse.getPlantCount("Test")).isEqualTo(2);
    }

    @Test
    void shouldUpdateData() {
        Warehouse warehouse = getWarehouse();

        warehouse.updatePlantCount("Test", 2);
        warehouse.updatePlantCount("Test", -2);
        warehouse.updatePlantCount("Test", 5);
        warehouse.updatePlantCount("Test", 12);
        warehouse.updatePlantCount("Test", -8);

        assertThat(warehouse.getPlantCount("Test")).isEqualTo(9);
    }

    @Test
    void shouldThrowOnIncorrectKey() {
        Warehouse warehouse = getWarehouse();

        assertThatThrownBy(() -> warehouse.updatePlantCount("", 0)).isInstanceOf(WarehouseException.class);
        assertThatThrownBy(() -> warehouse.updatePlantCount(null, 0)).isInstanceOf(WarehouseException.class);
        assertThatThrownBy(() -> warehouse.getPlantCount("")).isInstanceOf(WarehouseException.class);
        assertThatThrownBy(() -> warehouse.getPlantCount(null)).isInstanceOf(WarehouseException.class);
    }

    @Test
    void shouldReturnZeroOnFirstInteraction() {
        Warehouse warehouse = getWarehouse();
        assertThat(warehouse.getPlantCount("Test")).isZero();
        warehouse.updatePlantCount("Test", 2);
        assertThat(warehouse.getPlantCount("Test")).isNotZero();
    }

    @Test
    void returnsCorrectInfo() {
        Warehouse warehouse = getWarehouse();

        assertThat(warehouse.getInfo()).isNotNull().isEmpty();

        warehouse.updatePlantCount("Test1", 2);
        warehouse.updatePlantCount("Test2", 3);

        assertThat(warehouse.getInfo())
                .isNotNull()
                .isNotEmpty()
                .hasSize(2)
                .containsExactlyInAnyOrder(
                        new WarehouseInfo("Test1",2),
                        new WarehouseInfo("Test2", 3)
                );

        warehouse.updatePlantCount("Test1", 2);

        assertThat(warehouse.getInfo())
                .isNotNull()
                .isNotEmpty()
                .hasSize(2)
                .containsExactlyInAnyOrder(
                        new WarehouseInfo("Test1",4),
                        new WarehouseInfo("Test2", 3)
                );
    }
}
