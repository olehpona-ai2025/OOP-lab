package funFarm.infrastructure.storage;

import funFarm.core.Warehouse;
import funFarm.core.WarehouseTest;

public class RamWarehouseTest extends WarehouseTest {
    @Override
    protected Warehouse getWarehouse() {
        return new RamWarehouse();
    }
}
