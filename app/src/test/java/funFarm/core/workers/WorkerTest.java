package funFarm.core.workers;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import funFarm.core.workers.profiles.Profile;
import funFarm.core.workers.profiles.WorkerProfileType;

class WorkerTest {

    @Test
    void shouldResetWorkProgressWhenDone() {
        Profile profile = mock(Profile.class);
        WorkerProfileType profileType = mock(WorkerProfileType.class);
        when(profileType.createProfile()).thenReturn(profile);
        when(profile.usePower(any())).thenReturn(15);

        Worker worker = new Worker(profileType, 10);

        boolean done = worker.isDone();

        assertThat(done).isTrue();
        assertThat(worker.getWorkProgress()).isEqualTo(10);
        verify(profile).usePower(worker);
    }

    @Test
    void shouldNotFinishUntilProgressReachesZero() {
        Profile profile = mock(Profile.class);
        WorkerProfileType profileType = mock(WorkerProfileType.class);
        when(profileType.createProfile()).thenReturn(profile);
        when(profile.usePower(any())).thenReturn(3);

        Worker worker = new Worker(profileType, 10);

        boolean done = worker.isDone();

        assertThat(done).isFalse();
        assertThat(worker.getWorkProgress()).isEqualTo(7);
        verify(profile).usePower(worker);
    }

    @Test
    void shouldAssignFarmAreaAndResetProfileState() {
        Profile profile = mock(Profile.class);
        WorkerProfileType profileType = mock(WorkerProfileType.class);
        when(profileType.createProfile()).thenReturn(profile);

        Worker worker = new Worker(profileType, 30);

        worker.assignFarmArea("area-1");

        assertThat(worker.getFarmId()).isEqualTo("area-1");
        verify(profile).resetState(worker);
    }

    @Test
    void shouldReturnProfileType() {
        Profile profile = mock(Profile.class);
        WorkerProfileType profileType = mock(WorkerProfileType.class);
        when(profileType.createProfile()).thenReturn(profile);

        Worker worker = new Worker(profileType);

        assertThat(worker.getProfileType()).isEqualTo(profileType);
    }
}
