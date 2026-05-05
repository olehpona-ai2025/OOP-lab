package funFarm.infrastructure.storage.HibernateDB.Entity;

import jakarta.persistence.*;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.UUID;

@Entity
public class FarmAreaEntity {
    @Id
    @GeneratedValue
    public UUID id;
    @NonNull
    public String name;
    public int area;
    public String plantName;
    public String plantState;
    public int plantAge;

    @Version
    private Long version;

    @OneToMany(mappedBy = "farmArea", fetch = FetchType.LAZY)
    public List<WorkerEntity> workers;

    public FarmAreaEntity(String id, @NonNull String name, int area, String plantName, String plantState, int plantAge) {
        this.id = UUID.fromString(id);
        this.name = name;
        this.area = area;
        this.plantName = plantName;
        this.plantState = plantState;
        this.plantAge = plantAge;
    }

    public FarmAreaEntity() {}
}
