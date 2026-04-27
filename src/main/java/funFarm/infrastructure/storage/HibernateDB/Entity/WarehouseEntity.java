package funFarm.infrastructure.storage.HibernateDB.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class WarehouseEntity {
    @Id
    public String id;

    public int count;

    public WarehouseEntity(String id, int count) {
        this.id = id;
        this.count = count;
    }

    public WarehouseEntity() {

    }
}
