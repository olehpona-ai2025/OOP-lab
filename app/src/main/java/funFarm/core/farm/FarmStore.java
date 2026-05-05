package funFarm.core.farm;

import funFarm.core.model.FarmAreaInfo;

import java.util.List;

public interface FarmStore {
    FarmArea getFarmArea(String id);
    List<FarmAreaInfo> getFarmAreaInfo();

    void removeFarmArea(String id);
    void saveFarmArea(FarmArea area);
}
