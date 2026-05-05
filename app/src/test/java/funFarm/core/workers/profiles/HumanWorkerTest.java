package funFarm.core.workers.profiles;

import funFarm.core.workers.Worker;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class HumanWorkerTest {

    @Test
    void shouldFollowPowerCycle() {
        List<Integer> states = new ArrayList<>();
        states.add(0);

        Worker testWorker = mock(Worker.class);

        when(testWorker.getState()).thenAnswer(invocation -> states.getLast());

        doAnswer(invocationOnMock -> {
            states.add(invocationOnMock.getArgument(0));
            return null;
        }).when(testWorker).setState(anyInt());

        HumanWorker worker = new HumanWorker();

        List<Integer> outputs = List.of(
                worker.usePower(testWorker),
                worker.usePower(testWorker),
                worker.usePower(testWorker),
                worker.usePower(testWorker),
                worker.usePower(testWorker),
                worker.usePower(testWorker),
                worker.usePower(testWorker)
        );

        assertThat(outputs).containsExactly(10, 5, 0, 0, 0, 0, 0);
        assertThat(states).containsExactly(0, 1, 2, 3, 4, 5, 6, 7);
    }

    @Test
    void resetShouldRestoreInitialState() {
        Worker testWorker = mock(Worker.class);
        int[] captured = new int[1];
        captured[0] = 0;

        when(testWorker.getState()).thenAnswer(invocation -> captured[0]);

        doAnswer(invocationOnMock -> {
            captured[0] = invocationOnMock.getArgument(0);
            return null;
        }).when(testWorker).setState(anyInt());

        HumanWorker worker = new HumanWorker();

        worker.usePower(testWorker);
        worker.usePower(testWorker);
        worker.usePower(testWorker);

        worker.resetState(testWorker);

        assertThat(worker.usePower(testWorker)).isEqualTo(10);
    }
}