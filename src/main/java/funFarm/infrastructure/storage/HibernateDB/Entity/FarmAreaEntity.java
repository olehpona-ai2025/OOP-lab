package funFarm.infrastructure.storage.HibernateDB.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.jspecify.annotations.NonNull;

import java.util.UUID;

@Entity
@Table(name = "FarmAreaEntity")
public class FarmAreaEntity {
    @Id
    @GeneratedValue
    public UUID id;
    @NonNull
    public String name;
    public int area;
    public String plantName;
    public String plantState;

    public FarmAreaEntity(String id, String name, int area, String plantName, String plantState) {
        this.id = UUID.fromString(id);
        this.name = name;
        this.area = area;
        this.plantName = plantName;
        this.plantState = plantState;
    }

    public FarmAreaEntity() {}
}
