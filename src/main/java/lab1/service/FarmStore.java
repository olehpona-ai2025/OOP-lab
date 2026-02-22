package lab1.service;

import lab1.core.state.FarmState;

public interface FarmStore {
    void saveFarmState(FarmState state);
    FarmState loadFarmState();
}
