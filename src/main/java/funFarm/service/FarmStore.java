package funFarm.service;

import funFarm.core.state.FarmState;

public interface FarmStore {
    void saveFarmState(FarmState state);
    FarmState loadFarmState();
}
