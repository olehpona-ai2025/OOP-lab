package funFarm.core.workers;

import funFarm.core.model.WorkerData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class WorkerDepotTest {
    @Mock private WorkerDepotStore store;

    private AutoCloseable closeable;
    private WorkerDepot depot;

    @BeforeEach
    void setup() {
        closeable = MockitoAnnotations.openMocks(this);
        depot = new WorkerDepot(store);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void shouldIterateOverWorkersWithFarmId() {
        Worker worker1 = mock(Worker.class);
        Worker worker2 = mock(Worker.class);
        Worker worker3 = mock(Worker.class);
        when(store.getWorkers()).thenReturn(List.of(new WorkerData("Test1", worker1), new WorkerData("Test2", worker2), new WorkerData("Test3", worker3)));

        when(worker1.getFarmId()).thenReturn(null);
        when(worker2.getFarmId()).thenReturn("TestId");
        when(worker2.isDone()).thenReturn(true);
        when(worker3.getFarmId()).thenReturn("TestId");

        List<String> res = depot.workLoop();

        verify(store).updateWorker("Test2", worker2);
        verify(store).updateWorker("Test3", worker3);

        verify(store).getWorkers();
        verifyNoMoreInteractions(store);

        assertThat(res).isNotEmpty().containsExactly("TestId");
    }
}
