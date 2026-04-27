package funFarm.infrastructure.ui;

import funFarm.core.model.FarmAreaInfo;
import funFarm.core.model.FarmReport;
import funFarm.core.model.HarvestResult;
import funFarm.core.model.PlantResult;
import funFarm.core.model.WarehouseInfo;
import funFarm.core.plants.Plant;
import funFarm.service.FarmService;
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
    private ConsoleView consoleView;
    private final InputStream originalIn = System.in;
    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outContent;

    @BeforeEach
    void setUp() {
        farmService = Mockito.mock(FarmService.class);
        consoleView = new ConsoleView(farmService);
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

    @Test
    void testExit() {
        provideInput("14\n");
        consoleView.run();
        verifyNoInteractions(farmService);
    }

    @Test
    void testAddFarmArea() {
        provideInput("1\n100\n14\n");
        consoleView.run();
        verify(farmService).createFarmArea(100);
    }

    @Test
    void testPlantList() {
        provideInput("2\n14\n");
        
        Plant plantMock = mock(Plant.class);
        when(plantMock.getPlantName()).thenReturn("Wheat");
        when(plantMock.getPlantingCost()).thenReturn(10);
        when(plantMock.getBaseYield()).thenReturn(20);
        
        when(farmService.getPlants()).thenReturn(List.of(plantMock));
        
        consoleView.run();
        
        verify(farmService).getPlants();
        assertTrue(outContent.toString().contains("Wheat 10 20"));
    }

    @Test
    void testBuyPlant() {
        provideInput("3\nWheat\n5\n14\n");
        when(farmService.buyPlants("Wheat", 5)).thenReturn(true);
        
        consoleView.run();
        
        verify(farmService).buyPlants("Wheat", 5);
    }

    @Test
    void testBuyPlantWithError() {
        provideInput("3\nWheat\n5\n14\n");
        when(farmService.buyPlants("Wheat", 5)).thenReturn(false);

        consoleView.run();

        assertTrue(outContent.toString().contains("Failed"));

    }

    @Test
    void testFarmAreasList() {
        provideInput("4\n14\n");
        FarmAreaInfo info = new FarmAreaInfo("1", "Area1",100, "Wheat", "Planted");
        when(farmService.getFarmAreas()).thenReturn(List.of(info));
        
        consoleView.run();
        
        verify(farmService).getFarmAreas();
        assertTrue(outContent.toString().contains("1, 100, Area1, Wheat, Planted"));
    }

    @Test
    void testPlantFarmArea() {
        provideInput("5\narea1\nWheat\n14\n");
        when(farmService.plantFarmArea("area1", "Wheat")).thenReturn(new PlantResult(true, "", 0));
        
        consoleView.run();
        
        verify(farmService).plantFarmArea("area1", "Wheat");
    }

    @Test
    void testPlantFarmAreaWithError() {
        provideInput("5\narea1\nWheat\n14\n");
        when(farmService.plantFarmArea("area1", "Wheat")).thenThrow(new RuntimeException("test msg"));

        consoleView.run();

        assertTrue(outContent.toString().contains("test msg"));
    }

    @Test
    void testHarvestFarmArea() {
        provideInput("6\narea1\n14\n");
        when(farmService.harvestFarmArea("area1")).thenReturn(new HarvestResult(true, "", 10, ""));
        
        consoleView.run();
        
        verify(farmService).harvestFarmArea("area1");
    }

    @Test
    void testGetReport() {
        provideInput("7\n14\n");
        when(farmService.getReport()).thenReturn(List.of(new FarmReport("Wheat", 15)));
        
        consoleView.run();
        
        verify(farmService).getReport();
        assertTrue(outContent.toString().contains("Wheat 15%"));
    }

    @Test
    void testGrowLoop() {
        provideInput("8\n14\n");
        consoleView.run();
        verify(farmService).growLoop();
    }

    @Test
    void testTurboGrow() {
        provideInput("9\n14\n");
        consoleView.run();
        verify(farmService).turboGrow();
    }

    @Test
    void testRemoveFarmArea() {
        provideInput("10\narea1\n14\n");
        consoleView.run();
        verify(farmService).removeFarmArea("area1");
    }

    @Test
    void testSetFarmAreaName() {
        provideInput("11\narea1\nNewName\n14\n");
        consoleView.run();
        verify(farmService).setFarmAreaName("area1", "NewName");
    }

    @Test
    void testLoadFarmState() {
        provideInput("12\n14\n");
        consoleView.run();
        verify(farmService).loadData();
    }

    @Test
    void testWarehouseInfo() {
        provideInput("13\n14\n");
        when(farmService.getWarehouseInfo()).thenReturn(List.of(new WarehouseInfo("Wheat", 100)));
        consoleView.run();
        verify(farmService).getWarehouseInfo();
        assertTrue(outContent.toString().contains("Wheat, 100"));
    }

    @Test
    void testInvalidMenuOption() {
        provideInput("99\n14\n");
        consoleView.run();
        assertTrue(outContent.toString().contains("Unknown option."));
    }

    @Test
    void testFarmExceptionHandling() {
        provideInput("8\n14\n");
        doThrow(new funFarm.core.FarmException("Test exception")).when(farmService).growLoop();
        consoleView.run();
        assertTrue(outContent.toString().contains("Farm logic error: Test exception"));
    }

    @Test
    void testReadIntWithInvalidInput() {
        provideInput("1\ninvalid\n100\n14\n");
        consoleView.run();
        assertTrue(outContent.toString().contains("Error: Please enter a valid number."));
        verify(farmService).createFarmArea(100);
    }

    @Test
    void testReadStringWithEmptyInput() {
        provideInput("3\n\nWheat\n5\n14\n");
        when(farmService.buyPlants("Wheat", 5)).thenReturn(true);
        consoleView.run();
        assertTrue(outContent.toString().contains("Error: String cannot be empty. Try again."));
        verify(farmService).buyPlants("Wheat", 5);
    }
}
