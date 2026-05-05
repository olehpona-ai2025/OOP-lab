package funFarm.core.workers.profiles;

import funFarm.core.workers.Worker;

public interface Profile {
    int usePower(Worker worker);
    void resetState(Worker worker);
}
