package funFarm.core.workers.profiles;

import funFarm.core.workers.Worker;

public class HumanWorker implements Profile{
    @Override
    public int usePower(Worker worker) {
        int state = worker.getState();
        state++;
        worker.setState(state);
        if (state <= 3) {
            switch (state){
                case 1:
                    return 10;
                case 2:
                    return 5;
                case 3:
                    return 0;
            }
        } else {
            if (state == 8) {
                worker.setState(0);
            }
            return 0;
        }
        return 0;
    }

    @Override
    public void resetState(Worker worker) {
        worker.setState(0);
    }
}
