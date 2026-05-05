package funFarm.service;

import funFarm.core.model.WorkerInfo;
import funFarm.core.workers.profiles.WorkerProfileType;

import java.util.List;

public interface WorkerService {
    void assignWorker(String workerId, String farmAreaId);
    void createWorker(WorkerProfileType type);
    void deleteWorker(String workerId);
    List<WorkerInfo> getWorkers();
    void workerLoop();
}
