package funFarm.core.workers.profiles;

import funFarm.core.workers.Worker;

public class RobotWorker implements Profile{
    @Override
    public int usePower(Worker worker) {
        return 5;
    }

    @Override
    public void resetState(Worker worker) {}
}
