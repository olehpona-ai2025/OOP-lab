package funFarm.infrastructure.storage;

import funFarm.core.workers.WorkerDepotStore;
import funFarm.core.workers.WorkerDepotStoreTest;
import funFarm.core.workers.WorkerDepotException;
import funFarm.core.workers.Worker;
import funFarm.core.workers.profiles.WorkerProfileType;
import funFarm.core.model.events.FarmAreaDeletedEvent;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;

public class RamWorkerDepotStoreTest extends WorkerDepotStoreTest {
    @Override
    protected WorkerDepotStore getWorkerDepotStore() {
        return new RamWorkerDepotStore();
    }

    @Test
    void shouldThrowOnUpdateMissingWorker() {
        WorkerDepotStore store = getWorkerDepotStore();

        String missingId = UUID.randomUUID().toString();

        assertThatThrownBy(() -> store.updateWorker(missingId, new Worker(WorkerProfileType.HUMAN)))
                .isInstanceOf(WorkerDepotException.class);
    }

    @Test
    void shouldClearWorkerFarmAreaOnDeleteEvent() {
        WorkerDepotStore store = getWorkerDepotStore();

        store.addWorker(WorkerProfileType.HUMAN);
        String workerId = store.getWorkers().get(0).id();
        store.assignWorker(workerId, "area-1");

        store.pushEvent(new FarmAreaDeletedEvent("area-1"));

        assertThat(store.getWorker(workerId).worker().getFarmId()).isNull();
    }
}
