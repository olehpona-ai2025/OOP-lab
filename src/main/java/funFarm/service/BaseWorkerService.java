package funFarm.service;

import funFarm.core.farm.Farm;
import funFarm.core.workers.WorkerDepot;
import funFarm.core.workers.WorkerDepotStore;
import funFarm.core.model.WorkerInfo;
import funFarm.core.workers.profiles.WorkerProfileType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("BaseWorkerService")
public class BaseWorkerService implements WorkerService{
    private final Farm farm;
    private final WorkerDepot depot;
    private final WorkerDepotStore depotStore;

    public BaseWorkerService(Farm farm, WorkerDepot depot, WorkerDepotStore store) {
        this.farm = farm;
        this.depot = depot;
        this.depotStore = store;
    }

    @Override
    public void assignWorker(String workerId, String farmAreaId) {
        this.depotStore.assignWorker(workerId, farmAreaId);
    }

    @Override
    public void createWorker(WorkerProfileType type) {
        this.depotStore.addWorker(type);
    }

    @Override
    public void deleteWorker(String workerId) {
        this.depotStore.removeWorker(workerId);
    }

    @Override
    public void workerLoop() {
        for (String id: this.depot.workLoop()) {
            farm.areaLoop(id);
        }
    }

    @Override
    public List<WorkerInfo> getWorkers() {
        return this.depotStore.getWorkersInfo();
    }
}
