package funFarm.infrastructure.ui;

import funFarm.core.model.FarmAreaInfo;
import funFarm.core.model.FarmReport;
import funFarm.core.model.HarvestResult;
import funFarm.core.model.PlantResult;
import funFarm.core.model.WorkerInfo;
import funFarm.core.model.WarehouseInfo;
import funFarm.core.plants.Plant;
import funFarm.core.workers.profiles.WorkerProfileType;
import funFarm.service.FarmService;
import funFarm.service.PlantService;
import funFarm.service.WarehouseService;
import funFarm.service.WorkerService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class ConsoleViewTest {

    private FarmService farmService;
    private PlantService plantService;
    private WarehouseService warehouseService;
    private WorkerService workerService;
    private ConsoleView consoleView;
    private final InputStream originalIn = System.in;
    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outContent;

    @BeforeEach
    void setUp() {
        farmService = Mockito.mock(FarmService.class);
        plantService = Mockito.mock(PlantService.class);
        warehouseService = Mockito.mock(WarehouseService.class);
        workerService = Mockito.mock(WorkerService.class);
        consoleView = new ConsoleView(farmService, plantService, warehouseService, workerService);
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    private void provideInput(String data) {
        System.setIn(new ByteArrayInputStream(data.getBytes()));
    }

    private String cmd(ConsoleCommand command) {
        return command.input() + "\n";
    }

    @Test
    void testExit() {
        provideInput(cmd(ConsoleCommand.EXIT));
        consoleView.run();
        verifyNoInteractions(farmService, plantService, warehouseService, workerService);
    }

    @Test
    void testAddFarmArea() {
        provideInput(cmd(ConsoleCommand.ADD_FARM_AREA) + "100\n" + cmd(ConsoleCommand.EXIT));
        consoleView.run();
        verify(farmService).createFarmArea(100);
    }

    @Test
    void testPlantList() {
        provideInput(cmd(ConsoleCommand.PLANT_LIST) + cmd(ConsoleCommand.EXIT));
        
        Plant plantMock = mock(Plant.class);
        when(plantMock.getPlantName()).thenReturn("Wheat");
        when(plantMock.getPlantingCost()).thenReturn(10);
        when(plantMock.getBaseYield()).thenReturn(20);
        
        when(plantService.getPlants()).thenReturn(List.of(plantMock));
        
        consoleView.run();
        
        verify(plantService).getPlants();
        assertTrue(outContent.toString().contains("Wheat 10 20"));
    }

    @Test
    void testBuyPlant() {
        provideInput(cmd(ConsoleCommand.BUY_PLANT) + "Wheat\n5\n" + cmd(ConsoleCommand.EXIT));
        when(warehouseService.buyPlants("Wheat", 5)).thenReturn(true);
        
        consoleView.run();
        
        verify(warehouseService).buyPlants("Wheat", 5);
    }

    @Test
    void testBuyPlantWithError() {
        provideInput(cmd(ConsoleCommand.BUY_PLANT) + "Wheat\n5\n" + cmd(ConsoleCommand.EXIT));
        when(warehouseService.buyPlants("Wheat", 5)).thenReturn(false);

        consoleView.run();

        assertTrue(outContent.toString().contains("Failed"));

    }

    @Test
    void testFarmAreasList() {
        provideInput(cmd(ConsoleCommand.FARM_AREAS_LIST) + cmd(ConsoleCommand.EXIT));
        FarmAreaInfo info = new FarmAreaInfo("1", "Area1",100, "Wheat", "Planted");
        when(farmService.getFarmAreas()).thenReturn(List.of(info));
        
        consoleView.run();
        
        verify(farmService).getFarmAreas();
        assertTrue(outContent.toString().contains("1, 100, Area1, Wheat, Planted"));
    }

    @Test
    void testPlantFarmArea() {
        provideInput(cmd(ConsoleCommand.PLANT_FARM_AREA) + "area1\nWheat\n" + cmd(ConsoleCommand.EXIT));
        when(farmService.plantFarmArea("area1", "Wheat")).thenReturn(new PlantResult(true, "", 0));
        
        consoleView.run();
        
        verify(farmService).plantFarmArea("area1", "Wheat");
    }

    @Test
    void testPlantFarmAreaWithError() {
        provideInput(cmd(ConsoleCommand.PLANT_FARM_AREA) + "area1\nWheat\n" + cmd(ConsoleCommand.EXIT));
        when(farmService.plantFarmArea("area1", "Wheat")).thenThrow(new RuntimeException("test msg"));

        consoleView.run();

        assertTrue(outContent.toString().contains("test msg"));
    }

    @Test
    void testHarvestFarmArea() {
        provideInput(cmd(ConsoleCommand.HARVEST_FARM_AREA) + "area1\n" + cmd(ConsoleCommand.EXIT));
        when(farmService.harvestFarmArea("area1")).thenReturn(new HarvestResult(true, "", 10, ""));
        
        consoleView.run();
        
        verify(farmService).harvestFarmArea("area1");
    }

    @Test
    void testGetReport() {
        provideInput(cmd(ConsoleCommand.GET_REPORT) + cmd(ConsoleCommand.EXIT));
        when(warehouseService.getReport()).thenReturn(List.of(new FarmReport("Wheat", 15)));
        
        consoleView.run();
        
        verify(warehouseService).getReport();
        assertTrue(outContent.toString().contains("Wheat 15%"));
    }

    @Test
    void testWorkerLoop() {
        provideInput(cmd(ConsoleCommand.WORKER_LOOP) + cmd(ConsoleCommand.EXIT));
        consoleView.run();
        verify(workerService).workerLoop();
    }

    @Test
    void testRemoveFarmArea() {
        provideInput(cmd(ConsoleCommand.REMOVE_FARM_AREA) + "area1\n" + cmd(ConsoleCommand.EXIT));
        consoleView.run();
        verify(farmService).removeFarmArea("area1");
    }

    @Test
    void testSetFarmAreaName() {
        provideInput(cmd(ConsoleCommand.SET_FARM_AREA_NAME) + "area1\nNewName\n" + cmd(ConsoleCommand.EXIT));
        consoleView.run();
        verify(farmService).setFarmAreaName("area1", "NewName");
    }

    @Test
    void testWarehouseInfo() {
        provideInput(cmd(ConsoleCommand.WAREHOUSE_INFO) + cmd(ConsoleCommand.EXIT));
        when(warehouseService.getWarehouseInfo()).thenReturn(List.of(new WarehouseInfo("Wheat", 100)));
        consoleView.run();
        verify(warehouseService).getWarehouseInfo();
        assertTrue(outContent.toString().contains("Wheat, 100"));
    }

    @Test
    void testWorkersList() {
        provideInput(cmd(ConsoleCommand.WORKERS_LIST) + cmd(ConsoleCommand.EXIT));
        when(workerService.getWorkers()).thenReturn(List.of(new WorkerInfo("w1", "HUMAN", "a1")));

        consoleView.run();

        verify(workerService).getWorkers();
        assertTrue(outContent.toString().contains("w1, HUMAN, a1"));
    }

    @Test
    void testCreateWorker() {
        provideInput(cmd(ConsoleCommand.CREATE_WORKER) + "HUMAN\n" + cmd(ConsoleCommand.EXIT));

        consoleView.run();

        verify(workerService).createWorker(WorkerProfileType.HUMAN);
    }

    @Test
    void testAssignWorker() {
        provideInput(cmd(ConsoleCommand.ASSIGN_WORKER) + "worker1\narea1\n" + cmd(ConsoleCommand.EXIT));

        consoleView.run();

        verify(workerService).assignWorker("worker1", "area1");
    }

    @Test
    void testDeleteWorker() {
        provideInput(cmd(ConsoleCommand.DELETE_WORKER) + "worker1\n" + cmd(ConsoleCommand.EXIT));

        consoleView.run();

        verify(workerService).deleteWorker("worker1");
    }

    @Test
    void testInvalidMenuOption() {
        provideInput("99\n" + cmd(ConsoleCommand.EXIT));
        consoleView.run();
        assertTrue(outContent.toString().contains("Unknown option."));
    }

    @Test
    void testReadIntWithInvalidInput() {
        provideInput(cmd(ConsoleCommand.ADD_FARM_AREA) + "invalid\n100\n" + cmd(ConsoleCommand.EXIT));
        consoleView.run();
        assertTrue(outContent.toString().contains("Error: Please enter a valid number."));
        verify(farmService).createFarmArea(100);
    }

    @Test
    void testReadStringWithEmptyInput() {
        provideInput(cmd(ConsoleCommand.BUY_PLANT) + "\nWheat\n5\n" + cmd(ConsoleCommand.EXIT));
        when(warehouseService.buyPlants("Wheat", 5)).thenReturn(true);
        consoleView.run();
        assertTrue(outContent.toString().contains("Error: String cannot be empty. Try again."));
        verify(warehouseService).buyPlants("Wheat", 5);
    }
}
