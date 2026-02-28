package funFarm.infrastructure.storage;

import funFarm.service.AnalyticStore;
import funFarm.service.AnalyticStoreTest;

public class RamAnalyticStoreTest extends AnalyticStoreTest {
    @Override
    protected AnalyticStore getAnalyticStore() {
        return new RamAnalyticStore();
    }
}
