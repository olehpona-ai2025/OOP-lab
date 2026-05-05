package funFarm.service;

import funFarm.core.farm.Farm;
import funFarm.core.workers.WorkerDepot;
import funFarm.core.workers.WorkerDepotStore;
import funFarm.core.workers.profiles.WorkerProfileType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;

public class BaseWorkerServiceTest {
    @Mock private Farm farm;
    @Mock private WorkerDepot workerDepot;
    @Mock private WorkerDepotStore workerDepotStore;

    private AutoCloseable closeable;
    private BaseWorkerService service;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        service = new BaseWorkerService(farm, workerDepot, workerDepotStore);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }
    @Test
    void createWorker() {
        WorkerProfileType type = WorkerProfileType.HUMAN;
        service.createWorker(type);

        verify(workerDepotStore).addWorker(type);
    }

    @Test
    void assignWorker() {
        service.assignWorker("test", "test");

        verify(workerDepotStore).assignWorker("test", "test");
    }

    @Test
    void deleteWorker() {
        service.deleteWorker("test");

        verify(workerDepotStore).removeWorker("test");
    }

    @Test
    void workerLoop() {
        service.workerLoop();

        verify(workerDepot).workLoop();
    }
}
