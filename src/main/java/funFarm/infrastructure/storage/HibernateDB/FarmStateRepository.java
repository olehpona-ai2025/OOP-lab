package funFarm.infrastructure.storage.HibernateDB;

import funFarm.infrastructure.storage.HibernateDB.Entity.FarmAreaEntity;
import jakarta.data.repository.Delete;
import jakarta.data.repository.Find;
import jakarta.data.repository.Repository;
import jakarta.data.repository.Save;

import java.util.List;
import java.util.UUID;


@Repository
public interface FarmStateRepository {
    @Find
    List<FarmAreaEntity> getAll();

    @Save
    void save(FarmAreaEntity info);

    @Delete
    void removeArea(UUID id);
}
