package funFarm.infrastructure.storage.HibernateDB.Entity;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.jspecify.annotations.NonNull;

import java.util.UUID;

@Entity
@Table(name = "worker_entity")
public class WorkerEntity {
    @Id
    @GeneratedValue
    public UUID id;

    @Column(name = "profile_name")
    @NonNull
    public String profileName;

    @Column(name = "work_progress")
    @NonNull
    public int workProgress;

    @NonNull
    public int state;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farm_area_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    public FarmAreaEntity farmArea;

    public WorkerEntity() {

    }
}
