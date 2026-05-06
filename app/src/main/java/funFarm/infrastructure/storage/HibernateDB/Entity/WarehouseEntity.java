package funFarm.infrastructure.storage.HibernateDB.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "warehouse_entity")
public class WarehouseEntity {
    @Id
    public String id;

    public int count;

    @Version
    private Long version;

    public WarehouseEntity(String id, int count) {
        this.id = id;
        this.count = count;
    }

    public WarehouseEntity() {

    }
}
