package funFarm.infrastructure.storage.HibernateDB.Entity;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.jspecify.annotations.NonNull;

import java.util.UUID;

@Entity
public class WorkerEntity {
    @Id
    @GeneratedValue
    public UUID id;

    @NonNull
    public String profileName;

    @NonNull
    public int workProgress;

    @NonNull
    public int state;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farmAreaId")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    public FarmAreaEntity farmArea;

    public WorkerEntity() {

    }
}
