package funFarm.infrastructure.ui.web;

import funFarm.core.model.WorkerInfo;
import funFarm.core.workers.profiles.WorkerProfileType;
import funFarm.service.WorkerService;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Profile("web")
public class WorkerController {
    private final WorkerService service;

    public WorkerController(WorkerService service) {
        this.service = service;
    }

    private record CreateWorkerBody(String profile) {}

    @PostMapping("/workers")
    public void a(@RequestBody CreateWorkerBody body) {
        service.createWorker(WorkerProfileType.valueOf(body.profile()));
    }

    @GetMapping("/workers")
    public List<WorkerInfo> getWorkers() {
        return service.getWorkers();
    }

    private record AssignWorkerBody(String farmAreaId) {}

    @PatchMapping("/workers/assign/{id}")
    public void assignWorker(@PathVariable String id, @RequestBody AssignWorkerBody body) {
        service.assignWorker(id, body.farmAreaId());
    }

    @DeleteMapping("/workers/{id}")
    public void deleteWorker(@PathVariable String id) {
        service.deleteWorker(id);
    }

    @PostMapping("/workers/loop")
    public void workerLoop() {
        service.workerLoop();
    }
}
