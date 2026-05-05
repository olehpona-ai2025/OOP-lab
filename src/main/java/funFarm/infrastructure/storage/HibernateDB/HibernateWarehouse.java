package funFarm.infrastructure.storage.HibernateDB;

import funFarm.core.warehouse.Warehouse;
import funFarm.core.warehouse.WarehouseException;
import funFarm.core.model.WarehouseInfo;
import funFarm.infrastructure.storage.HibernateDB.Entity.WarehouseEntity;
import jakarta.data.exceptions.EmptyResultException;
import jakarta.persistence.OptimisticLockException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Primary
@ConditionalOnProperty(name = "storage.warehouse", havingValue = "hibernate")
public class HibernateWarehouse implements Warehouse {
    private final WarehouseRepository repository;

    @Autowired
    public HibernateWarehouse(WarehouseRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public int getPlantCount(String plant) {
        if (plant == null || plant.isEmpty()) {
            throw new WarehouseException("Incorrect key");
        }
        try {
            WarehouseEntity entity = this.repository.getCount(plant);
            return entity.count;
        } catch (EmptyResultException _) {
            return 0;
        }
    }

    @Override
    @Transactional
    public void updatePlantCount(String plant, int count) {
        if (plant == null || plant.isEmpty()) {
            throw new WarehouseException("Incorrect key");
        }
        int current = getPlantCount(plant);
        try {
            this.repository.setCount(new WarehouseEntity(plant, current + count));
        } catch (OptimisticLockException _) {
            throw new WarehouseException("Failed locking warehouse entry");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<WarehouseInfo> getInfo() {
        return this.repository.getAll().stream().map(entity -> new WarehouseInfo(entity.id, entity.count)).toList();
    }
}
