package lab1.core.model;

import lab1.infrastructure.ui.DisplayableResult;

public record HarvestResult(boolean success, String msg, int harvested, String targetPlant) implements DisplayableResult {
    @Override
    public boolean isSuccess() {
        return success;
    }

    @Override
    public String getMsg() {
        return msg;
    }
}
