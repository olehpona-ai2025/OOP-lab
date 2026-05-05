package funFarm.infrastructure.storage.HibernateDB;

import funFarm.infrastructure.storage.HibernateDB.Entity.WarehouseEntity;
import jakarta.data.repository.Find;
import jakarta.data.repository.Repository;
import jakarta.data.repository.Save;

import java.util.List;

@Repository
public interface WarehouseRepository {
    @Find
    WarehouseEntity getCount(String id);

    @Save
    void setCount(WarehouseEntity e);

    @Find
    List<WarehouseEntity> getAll();
}
