package funFarm.infrastructure.storage.HibernateDB;

import funFarm.core.state.FarmAreaState;
import funFarm.core.state.FarmState;
import funFarm.infrastructure.storage.HibernateDB.Entity.FarmAreaEntity;
import funFarm.service.FarmStore;
import funFarm.service.FarmStoreException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@Primary
public class HibernateStateStore implements FarmStore {
    private final FarmStateRepository repository;

    @Autowired
    public HibernateStateStore(FarmStateRepository repository){
        this.repository = repository;
    }

    @Override
    @Transactional
    public void saveFarmState(FarmState state) {
        if (state == null) {
            throw new FarmStoreException("State should not be null");
        }
        state.farmAreaStateList().forEach(farmAreaInfo -> {
            repository.save(new FarmAreaEntity(farmAreaInfo.id(), farmAreaInfo.name(), farmAreaInfo.area(), farmAreaInfo.plantName(), farmAreaInfo.plantState()));
        });
    }

    @Override
    @Transactional(readOnly = true)
    public FarmState loadFarmState() {
        return new FarmState(repository.getAll().stream().map(
                farmAreaEntity -> new FarmAreaState(farmAreaEntity.id.toString(), farmAreaEntity.name, farmAreaEntity.area, farmAreaEntity.plantName, farmAreaEntity.plantState)
        ).toList());
    }

    @Override
    @Transactional
    public void updateFarmAreaState(FarmAreaState state) {
        if (state == null) {
            throw new FarmStoreException("State should not be null");
        }
        repository.save(new FarmAreaEntity(state.id(), state.name(), state.area(), state.plantName(), state.plantState()));
    }

    @Override
    @Transactional
    public void removeFarmArea(String id) {
        if (id == null) {
            throw new FarmStoreException("ID should not be null");
        }
        repository.removeArea(UUID.fromString(id));
    }
}
