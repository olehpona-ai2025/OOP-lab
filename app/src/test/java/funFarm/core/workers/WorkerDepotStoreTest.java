package funFarm.core.workers;

import funFarm.core.model.WorkerData;
import funFarm.core.model.WorkerInfo;
import funFarm.core.workers.profiles.WorkerProfileType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public abstract class WorkerDepotStoreTest {
    protected abstract WorkerDepotStore getWorkerDepotStore();

    protected void seedFarmArea(String farmAreaId) {
    }

    @Test
    void shouldAddAndGetWorker() {
        WorkerDepotStore store = getWorkerDepotStore();

        store.addWorker(WorkerProfileType.HUMAN);

        List<WorkerData> workers = store.getWorkers();
        assertThat(workers).hasSize(1);

        WorkerData data = workers.getFirst();
        WorkerData loaded = store.getWorker(data.id());

        assertThat(loaded.id()).isEqualTo(data.id());
        assertThat(loaded.worker().getProfileType()).isEqualTo(WorkerProfileType.HUMAN);
        assertThat(loaded.worker().getFarmId()).isNull();
    }

    @Test
    void shouldUpdateWorker() {
        WorkerDepotStore store = getWorkerDepotStore();

        store.addWorker(WorkerProfileType.HUMAN);
        WorkerData data = store.getWorkers().getFirst();

        Worker updated = new Worker(WorkerProfileType.HUMAN, 7);
        store.updateWorker(data.id(), updated);

        WorkerData loaded = store.getWorker(data.id());
        assertThat(loaded.worker().getProfileType()).isEqualTo(WorkerProfileType.HUMAN);
        assertThat(loaded.worker().getWorkProgress()).isEqualTo(7);
    }

    @Test
    void shouldRemoveWorker() {
        WorkerDepotStore store = getWorkerDepotStore();

        store.addWorker(WorkerProfileType.HUMAN);
        WorkerData data = store.getWorkers().getFirst();

        store.removeWorker(data.id());

        assertThat(store.getWorkers()).isEmpty();
        assertThatThrownBy(() -> store.getWorker(data.id()))
                .isInstanceOf(WorkerDepotException.class);
    }

    @Test
    void shouldAssignWorkerToFarmArea() {
        WorkerDepotStore store = getWorkerDepotStore();

        store.addWorker(WorkerProfileType.HUMAN);
        WorkerData data = store.getWorkers().getFirst();

        String areaId = UUID.randomUUID().toString();
        seedFarmArea(areaId);

        store.assignWorker(data.id(), areaId);

        WorkerData loaded = store.getWorker(data.id());
        assertThat(loaded.worker().getFarmId()).isEqualTo(areaId);
    }

    @Test
    void shouldReturnWorkersInfo() {
        WorkerDepotStore store = getWorkerDepotStore();

        store.addWorker(WorkerProfileType.HUMAN);
        store.addWorker(WorkerProfileType.HUMAN);

        Set<String> workerIds = store.getWorkers().stream()
                .map(WorkerData::id)
                .collect(Collectors.toSet());

        List<WorkerInfo> infos = store.getWorkersInfo();

        assertThat(infos).hasSize(2);
        assertThat(infos).allSatisfy(info -> {
            assertThat(workerIds).contains(info.id());
            assertThat(info.profile()).isEqualTo(WorkerProfileType.HUMAN.name());
        });
    }

    @Test
    void shouldThrowWhenWorkerMissing() {
        WorkerDepotStore store = getWorkerDepotStore();

        String missingId = UUID.randomUUID().toString();

        assertThatThrownBy(() -> store.getWorker(missingId))
                .isInstanceOf(WorkerDepotException.class);
        assertThatThrownBy(() -> store.removeWorker(missingId))
                .isInstanceOf(WorkerDepotException.class);
        assertThatThrownBy(() -> store.assignWorker(missingId, UUID.randomUUID().toString()))
                .isInstanceOf(WorkerDepotException.class);
    }
}
