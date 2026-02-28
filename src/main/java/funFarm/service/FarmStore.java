package funFarm.service;

import funFarm.core.state.FarmState;

public interface FarmStore {
    void saveFarmState(FarmState state) throws FarmStoreException;
    FarmState loadFarmState() throws FarmStoreException;
}
