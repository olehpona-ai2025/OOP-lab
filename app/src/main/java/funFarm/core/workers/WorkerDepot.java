package funFarm.core.workers;

import funFarm.core.farm.FarmException;
import funFarm.core.farm.FarmStoreException;
import funFarm.core.model.WorkerData;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

public class WorkerDepot {
    private final WorkerDepotStore store;

    public WorkerDepot(WorkerDepotStore store) {
        this.store = store;
    }

    public List<String> workLoop() {
        List<String> res = new ArrayList<>();
        for (WorkerData workerData: store.getWorkers()) {
            if (workerData.worker().getFarmId() != null) {
                try {
                    if (workerData.worker().isDone()) {
                        res.add(workerData.worker().getFarmId());
                    }
                    store.updateWorker(workerData.id(), workerData.worker());
                } catch (FarmStoreException | FarmException fe) {
                    System.err.println("Farm error " + fe.getMessage());
                } catch (WorkerDepotException we) {
                    System.err.println("Worker depot error " + we.getMessage());
                }

            }
        }
        return res.stream().toList();
    }
}
