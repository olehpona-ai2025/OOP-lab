package funFarm.infrastructure.ui;

public enum ConsoleCommand {
    ADD_FARM_AREA(1),
    PLANT_LIST(2),
    BUY_PLANT(3),
    FARM_AREAS_LIST(4),
    PLANT_FARM_AREA(5),
    HARVEST_FARM_AREA(6),
    GET_REPORT(7),
    WORKER_LOOP(8),
    REMOVE_FARM_AREA(9),
    SET_FARM_AREA_NAME(10),
    WAREHOUSE_INFO(11),
    WORKERS_LIST(12),
    CREATE_WORKER(13),
    ASSIGN_WORKER(14),
    DELETE_WORKER(15),
    EXIT(16);

    private final int code;

    ConsoleCommand(int code) {
        this.code = code;
    }

    public int code() {
        return code;
    }

    public String input() {
        return String.valueOf(code);
    }
}
