package lab1.service;

public interface Warehouse {
    int getPlantCount(String plant);
    void updatePlantCount(String plant, int count);
}
