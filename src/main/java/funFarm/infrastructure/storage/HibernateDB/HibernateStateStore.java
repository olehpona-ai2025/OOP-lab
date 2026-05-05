package funFarm.infrastructure.storage.HibernateDB;

import funFarm.core.farm.FarmArea;
import funFarm.core.farm.FarmStoreException;
import funFarm.core.plants.PlantRegistry;
import funFarm.core.model.FarmAreaInfo;
import funFarm.core.model.PlantGrowState;
import funFarm.core.plants.Plant;
import funFarm.infrastructure.storage.HibernateDB.Entity.FarmAreaEntity;
import funFarm.core.farm.FarmStore;
import jakarta.data.exceptions.EmptyResultException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.OptimisticLockException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@Primary
@ConditionalOnProperty(name = "storage.state", havingValue = "hibernate")
public class HibernateStateStore implements FarmStore {
    private final FarmStateRepository repository;
    private final PlantRegistry registry;

    @Autowired
    public HibernateStateStore(FarmStateRepository repository, PlantRegistry registry){
        this.repository = repository;
        this.registry = registry;
    }

    @Override
    public FarmArea getFarmArea(String id) {
        if (id == null) {
            throw new FarmStoreException("Farm area id should not be null");
        }
        try {
            FarmAreaEntity entity = repository.getArea(UUID.fromString(id));
            return entityToFarmArea(entity);
        } catch (IllegalArgumentException  _) {
            throw new FarmStoreException("Incorrect id");
        } catch (EntityNotFoundException | EmptyResultException _) {
            throw new FarmStoreException("Entity not found");
        } catch (OptimisticLockException _) {
            throw new FarmStoreException("Failed locking farm area");
        }
    }

    @Override
    public List<FarmAreaInfo> getFarmAreaInfo() {
        List<FarmAreaEntity> entities = repository.getAll();
        return entities.stream().map(entity -> entityToFarmArea(entity).getInfo()).toList();
    }

    @Override
    public void removeFarmArea(String id) {
        if (id == null) {
            throw new FarmStoreException("Farm area id should not be null");
        }
        try {
            if (repository.removeArea(UUID.fromString(id)) == 0) {
                throw new FarmStoreException("Farm area " + id + " not found");
            }
        } catch (IllegalArgumentException  _) {
            throw new FarmStoreException("Incorrect id");
        } catch (EntityNotFoundException | EmptyResultException _) {
            throw new FarmStoreException("Entity not found");
        } catch (OptimisticLockException _) {
            throw new FarmStoreException("Failed locking farm area");
        }
    }

    @Override
    public void saveFarmArea(FarmArea area) {
        try {
            repository.save(farmAreaToEntity(area));
        } catch (OptimisticLockException _) {
            throw new FarmStoreException("Failed locking farm area");
        }
    }

    private FarmArea entityToFarmArea(FarmAreaEntity entity) {
        FarmArea area = new FarmArea(entity.id.toString(), entity.area);
        area.setName(entity.name);

        if (entity.plantName == null || entity.plantState == null) {
            return area;
        }
        Plant plant = registry.createWithState(entity.plantName, PlantGrowState.valueOf(entity.plantState), entity.plantAge);
        if (plant != null) {
            area.plant(plant);
        }
        return area;
    }

    private FarmAreaEntity farmAreaToEntity(FarmArea area) {
        Plant currentPlant = area.getCurrentPlant();
        return new FarmAreaEntity(area.id,
                area.getName(),
                area.area ,
                currentPlant != null ? currentPlant.getPlantName() : null,
                currentPlant != null ? currentPlant.getState().toString() : null,
                currentPlant != null ? currentPlant.getAge() : 0);
    }
}
