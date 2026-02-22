package lab1.core.model;

import lab1.infrastructure.ui.DisplayableResult;

public record PlantResult(boolean success, String msg, int planted) implements DisplayableResult {
    @Override
    public boolean isSuccess() {
        return success;
    }

    @Override
    public String getMsg() {
        return msg;
    }
}
