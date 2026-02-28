package funFarm.infrastructure.storage;

import funFarm.service.EventNotifier;
import funFarm.service.EventNotifierTest;

public class ListEventNotifierTest extends EventNotifierTest {
    @Override
    protected EventNotifier getNotifier() {
        return new ListEventNotifier();
    }
}
