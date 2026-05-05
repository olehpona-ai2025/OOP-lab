package funFarm.core.workers;

import funFarm.core.model.WorkerData;
import funFarm.core.model.WorkerInfo;
import funFarm.core.workers.profiles.WorkerProfileType;
import funFarm.service.EventListener;

import java.util.List;

public interface WorkerDepotStore extends EventListener {
    void addWorker(WorkerProfileType type);
    WorkerData getWorker(String id);
    List<WorkerData> getWorkers();
    void updateWorker(String id, Worker worker);
    void removeWorker(String id);
    void assignWorker(String id, String farmArea);
    List<WorkerInfo> getWorkersInfo();
}
