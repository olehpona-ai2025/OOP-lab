package funFarm.infrastructure.storage.HibernateDB;

import funFarm.core.workers.WorkerDepotException;
import funFarm.core.workers.WorkerDepotStore;
import funFarm.core.model.WorkerData;
import funFarm.core.model.WorkerInfo;
import funFarm.core.model.events.FarmEvent;
import funFarm.core.workers.Worker;
import funFarm.core.workers.profiles.WorkerProfileType;
import funFarm.infrastructure.storage.HibernateDB.Entity.WorkerEntity;
import jakarta.data.exceptions.EmptyResultException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Component("HibernateWorkerDepotStore")
@ConditionalOnProperty(name = "storage.workerDepot", havingValue = "hibernate")
public class HibernateWorkerDepotStore implements WorkerDepotStore {
    private final WorkerDepotRepository repository;
    private final FarmStateRepository farmStateRepository;

    public HibernateWorkerDepotStore(WorkerDepotRepository repository, FarmStateRepository farmStateRepository) {
        this.repository = repository;
        this.farmStateRepository = farmStateRepository;
    }

    @Override
    public void addWorker(WorkerProfileType type) {
        repository.save(workerToWorkerEntity(new Worker(type)));
    }

    @Override
    public WorkerData getWorker(String id) {
        try {
            WorkerEntity entity = repository.getById(UUID.fromString(id));
            return new WorkerData(id, workerEntityToWorker(entity));
        } catch (EmptyResultException _) {
            throw new WorkerDepotException("Worker " + id + " do not exists");
        } catch (IllegalArgumentException _) {
            throw new WorkerDepotException("Incorrect uuid");
        }

    }

    @Override
    public List<WorkerData> getWorkers() {
        List<WorkerEntity> entities = repository.getAll();
        return entities.stream().map(entity -> new WorkerData(entity.id.toString(), workerEntityToWorker(entity))).toList();
    }

    @Override
    public void updateWorker(String id, Worker worker) {
        try {
            WorkerEntity entity = workerToWorkerEntity(worker);
            entity.id = UUID.fromString(id);
            repository.save(entity);
        } catch (EntityNotFoundException _) {
            throw new WorkerDepotException("Worker " + id + " do not exists");
        } catch (IllegalArgumentException _) {
            throw new WorkerDepotException("Incorrect uuid");
        }
    }

    @Override
    public void removeWorker(String id) {
        try {
            if (repository.delete(UUID.fromString(id)) == 0) {
                throw new WorkerDepotException("Worker " + id + " do not exists");
            }
        } catch (IllegalArgumentException _) {
            throw new WorkerDepotException("Incorrect uuid");
        }
    }

    @Override
    @Transactional
    public void assignWorker(String id, String farmArea) {
        WorkerEntity entity;
        try {
            entity = repository.getById(UUID.fromString(id));
        } catch (EmptyResultException _) {
            throw new WorkerDepotException("Worker " + id + " do not exists");
        } catch (IllegalArgumentException _) {
            throw new WorkerDepotException("Incorrect uuid");
        }

        try {
            entity.farmArea = farmStateRepository.getArea(UUID.fromString(farmArea));
        } catch (EntityNotFoundException _) {
            throw new WorkerDepotException("Worker " + id + " do not exists");
        } catch (IllegalArgumentException _) {
            throw new WorkerDepotException("Incorrect uuid");
        }

        repository.save(entity);
    }

    @Override
    public List<WorkerInfo> getWorkersInfo() {
        return repository.getAll().stream().map(entity -> new WorkerInfo(entity.id.toString(), entity.profileName, entity.farmArea != null? entity.farmArea.id.toString(): "-")).toList();
    }

    @Override
    public void pushEvent(FarmEvent event) {

    }

    private Worker workerEntityToWorker(WorkerEntity entity) {
        Worker worker = new Worker(WorkerProfileType.valueOf(entity.profileName), entity.workProgress);
        worker.setState(entity.state);

        if (entity.farmArea != null) {
            worker.assignFarmArea(entity.farmArea.id.toString());
        }
        return worker;
    }

    private WorkerEntity workerToWorkerEntity(Worker worker) {
        WorkerEntity entity = new WorkerEntity();

        entity.profileName = worker.getProfileType().name();
        entity.workProgress = worker.getWorkProgress();
        entity.state = worker.getState();

        if (worker.getFarmId() != null) {
            entity.farmArea = farmStateRepository.getArea(UUID.fromString(worker.getFarmId()));
        }
        return entity;
    }
}
