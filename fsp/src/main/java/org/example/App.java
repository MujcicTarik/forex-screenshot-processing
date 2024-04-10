
package org.example;

import org.example.enums.ForexChartType;
import org.example.service.DateFileService;
import org.example.service.FocusedAppCheckerService;
import org.example.service.InstanceCounterService;
import org.example.service.KeyListenerService;
import org.example.service.KeyPressSimulationService;
import org.example.service.ScreenshotService;

import java.time.LocalDate;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class App {
    private static final String SOURCE_DIRECTORY_PATH = "C:\\US30\\Before";
    private static final String TARGET_DIRECTORY_PATH = "C:\\US30\\Hourly_1";

    public static LocalDate START_DATE;

    /**
     * Used to set how often (in seconds) will the folder be checked
     */
    private static final int FOLDER_SCAN_INTERVAL = 5;

    public static final boolean USE_CUSTOM_Y_COORDINATES = true;

    /**
     * VALUES USED FOR THE Y COORDINATE ARE 1,2 and 3 ! ! !
     */
    public static final int[] CUSTOM_Y_COORDINATES = {3, 3, 3, 3, 3};

    public static boolean IS_FULLY_AUTOMATED = true;
    public static boolean IS_TRIGGER_KEY_PRESSED = false;

    public static ForexChartType forexChartType = ForexChartType.HOURLY_1;

    public static void main(String[] args) throws InterruptedException {
        KeyListenerService.initializeGlobalKeyListener();
        InstanceCounterService.initializeInstanceCounters();
        Thread.sleep(5000); // Wait for 5s at the start
        START_DATE = DateFileService.getDateFromFile();
        if (START_DATE == null) {
            throw new RuntimeException("Start date is null - make sure that current-date.txt contains a valid date.");
        }
        while (true) {
            if (!IS_FULLY_AUTOMATED) {
                System.out.println("Hit B key to process the screenshot");
                while (!IS_TRIGGER_KEY_PRESSED) {
                    try {
                        Thread.sleep(100); // Check every 100 milliseconds
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            if (FocusedAppCheckerService.isTraderAppFocused()) {
                System.out.println("Enter key pressed. Taking the screenshot...");
                ScreenshotService.takeScreenshot(SOURCE_DIRECTORY_PATH);
                System.out.println("Screenshot taken successfully. Processing the screenshot...");
                ScreenshotService.processScreenshot(forexChartType, SOURCE_DIRECTORY_PATH, TARGET_DIRECTORY_PATH);
                IS_TRIGGER_KEY_PRESSED = false;

                int numberOfPresses = forexChartType == ForexChartType.HOURLY_23 ? DateFileService.getForexHoursForDate(START_DATE.plusDays(1)) : 1;
                while (!FocusedAppCheckerService.isTraderAppFocused()) {
                    Thread.sleep(500);
                }
                KeyPressSimulationService.simulateKeyPressF12(numberOfPresses);
            } else {
                System.out.println("Trader Application not focused.");
                Thread.sleep(1000);
            }
        }
    }

    // just for documentation
    private static void oldMainWithoutKeyListener() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        Runnable runnableTask = () -> {
            KeyPressSimulationService.simulateKeyPressF12(23);
            ScreenshotService.processScreenshot(ForexChartType.HOURLY_23, SOURCE_DIRECTORY_PATH, TARGET_DIRECTORY_PATH);
        };
        scheduler.scheduleAtFixedRate(runnableTask, 0, FOLDER_SCAN_INTERVAL, TimeUnit.SECONDS);
    }
}