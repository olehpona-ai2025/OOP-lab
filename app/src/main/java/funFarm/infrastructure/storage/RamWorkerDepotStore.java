package funFarm.infrastructure.storage;

import funFarm.core.workers.WorkerDepotStore;
import funFarm.core.workers.WorkerDepotException;
import funFarm.core.model.WorkerData;
import funFarm.core.model.WorkerInfo;
import funFarm.core.model.events.FarmAreaDeletedEvent;
import funFarm.core.model.events.FarmEvent;
import funFarm.core.workers.Worker;
import funFarm.core.workers.profiles.WorkerProfileType;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.*;

@Component("RamWorkerDepotStore")
@ConditionalOnProperty(name = "storage.workerDepot", havingValue = "ram")
public class RamWorkerDepotStore implements WorkerDepotStore {
    private final Map<String, Worker> workers = new HashMap<>();

    @Override
    public void addWorker(WorkerProfileType type) {
        String id = UUID.randomUUID().toString();
        workers.put(id, new Worker(type));
    }

    @Override
    public WorkerData getWorker(String id) {
        if (!workers.containsKey(id)){
            throw new WorkerDepotException("Worker with id " + id + "do not exist");
        }
        return new WorkerData(id, workers.get(id));
    }

    @Override
    public List<WorkerData> getWorkers() {
        return workers.entrySet().stream().map((entry) -> new WorkerData(entry.getKey(), entry.getValue())).toList();
    }

    @Override
    public void updateWorker(String id, Worker worker) {
        if (!workers.containsKey(id)){
            throw new WorkerDepotException("Worker with id " + id + "do not exist");
        }
        workers.put(id, worker);
    }

    @Override
    public void removeWorker(String id) {
        if (!workers.containsKey(id)){
            throw new WorkerDepotException("Worker with id " + id + "do not exist");
        }
        workers.remove(id);
    }

    @Override
    public void assignWorker(String id, String farmArea) {
        if (!workers.containsKey(id)){
            throw new WorkerDepotException("Worker with id " + id + "do not exist");
        }
        workers.get(id).assignFarmArea(farmArea);
    }

    @Override
    public List<WorkerInfo> getWorkersInfo() {
        List<WorkerInfo> res = new ArrayList<>();

        workers.forEach((key, worker) -> {
            res.add(new WorkerInfo(key, worker.getProfileType().name(), worker.getFarmId()));
        });

        return res.stream().toList();
    }

    @Override
    public void pushEvent(FarmEvent event) {
        if (event instanceof FarmAreaDeletedEvent(String areaId)) {
            workers.forEach((_key, val) -> {
                if (Objects.equals(val.getFarmId(), areaId)) {
                    val.assignFarmArea(null);
                }
            });
        }
    }
}
