package funFarm.core.workers;

import funFarm.core.workers.profiles.Profile;
import funFarm.core.workers.profiles.WorkerProfileType;

public class Worker {
    private final WorkerProfileType profileType;
    private final Profile profile;
    private String farmAreaId;
    private int workProgress = 10;
    private int state = 0;

    public Worker(WorkerProfileType profileType) {
        this.profileType = profileType;
        this.profile = profileType.createProfile();
    }
    public Worker(WorkerProfileType profileType, int workProgress) {
        this.profileType = profileType;
        this.profile = profileType.createProfile();
        this.workProgress = workProgress;
    }

    public void assignFarmArea(String id) {
        farmAreaId = id;
        profile.resetState(this);
    }

    public int getWorkProgress(){
        return workProgress;
    }

    public boolean isDone() {
        workProgress -= profile.usePower(this);
        boolean result = false;
        if (workProgress <= 0) {
            result = true;
            workProgress = 10;
        }
        return result;
    }

    public String getFarmId() {
        return farmAreaId;
    }
    public WorkerProfileType getProfileType() {
        return this.profileType;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
