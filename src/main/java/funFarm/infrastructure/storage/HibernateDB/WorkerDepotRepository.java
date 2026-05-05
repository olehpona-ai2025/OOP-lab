package funFarm.infrastructure.storage.HibernateDB;

import funFarm.infrastructure.storage.HibernateDB.Entity.WorkerEntity;
import jakarta.data.repository.*;

import java.util.List;
import java.util.UUID;

@Repository
public interface WorkerDepotRepository {
    @Query("select w from WorkerEntity w left join fetch w.farmArea f")
    List<WorkerEntity> getAll();

    @Query("select w from WorkerEntity w left join fetch w.farmArea f where w.id = :id")
    WorkerEntity getById(UUID id);

    @Save
    void save(WorkerEntity entity);

    @Delete
    int delete(UUID id);

    @Find
    List<WorkerEntity> getByFarmAreaId(@By("farmArea.id") UUID farmAreaId);

}
