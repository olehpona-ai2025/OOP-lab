package funFarm.infrastructure.storage.HibernateDB;

import funFarm.infrastructure.storage.HibernateDB.Entity.FarmAreaEntity;
import funFarm.infrastructure.storage.HibernateDB.Entity.WorkerEntity;
import jakarta.data.repository.*;

import java.util.List;
import java.util.UUID;


@Repository
public interface FarmStateRepository {
    @Find
    List<FarmAreaEntity> getAll();

    @Save
    void save(FarmAreaEntity info);

    @Delete
    int removeArea(UUID id);

    @Find
    FarmAreaEntity getArea(UUID id);
}
