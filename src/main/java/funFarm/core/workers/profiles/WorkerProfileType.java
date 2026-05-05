package funFarm.core.workers.profiles;

public enum WorkerProfileType {
    HUMAN,
    ROBOT;

    public Profile createProfile() {
        return switch (this) {
            case HUMAN -> new HumanWorker();
            case ROBOT -> new RobotWorker();
        };
    }
}
