package funFarm.service;

import funFarm.core.state.FarmAreaState;
import funFarm.core.state.FarmState;

public interface FarmStore {
    void saveFarmState(FarmState state);
    FarmState loadFarmState();

    void updateFarmAreaState(FarmAreaState state);
    void removeFarmArea(String id);
}
