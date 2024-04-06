
package org.example;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import org.example.service.KeyListenerService;
import org.example.service.KeyPressSimulationService;
import org.example.service.ScreenshotService;

import java.time.LocalDate;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class App {
    private static final String SOURCE_DIRECTORY_PATH = "D:\\Desktop\\ImagesBefore";
    private static final String TARGET_DIRECTORY_PATH = "D:\\Desktop\\ImagesAfter";

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

    public static boolean IS_TRIGGER_KEY_PRESSED = false;

    public static void main(String[] args) throws InterruptedException {
        KeyListenerService.initializeGlobalKeyListener(NativeKeyEvent.VC_ENTER);
        Thread.sleep(5000); // Wait for 5s at the start

        while (true) {
            KeyPressSimulationService.simulateKeyPressF12(23, 2000);
            System.out.println("Hit enter key to process the screenshot");
            while (!IS_TRIGGER_KEY_PRESSED) {
                try {
                    Thread.sleep(100); // Check every 100 milliseconds
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            System.out.println("Enter key pressed. Taking the screenshot...");
            ScreenshotService.takeScreenshot(SOURCE_DIRECTORY_PATH);
            System.out.println("Screenshot taken successfully. Processing the screenshot...");
            ScreenshotService.processScreenshot(SOURCE_DIRECTORY_PATH, TARGET_DIRECTORY_PATH);
            IS_TRIGGER_KEY_PRESSED = false;
        }
    }

    // just for documentation
    private static void oldMainWithoutKeyListener() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        Runnable runnableTask = () -> {
            KeyPressSimulationService.simulateKeyPressF12(23, 2000);
            ScreenshotService.processScreenshot(SOURCE_DIRECTORY_PATH, TARGET_DIRECTORY_PATH);
        };
        scheduler.scheduleAtFixedRate(runnableTask, 0, FOLDER_SCAN_INTERVAL, TimeUnit.SECONDS);
    }
}