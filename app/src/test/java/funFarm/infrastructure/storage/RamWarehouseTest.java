package funFarm.infrastructure.storage;

import funFarm.core.warehouse.Warehouse;
import funFarm.core.warehouse.WarehouseTest;

public class RamWarehouseTest extends WarehouseTest {
    @Override
    protected Warehouse getWarehouse() {
        return new RamWarehouse();
    }
}
