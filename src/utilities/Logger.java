package utilities;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Logger{
    /**
     * This utility can log messages and is created as a general utility but implemented to record login activity only.
     * @param message string to be written into log file.
     * @throws IOException
     */
    public static void log(String message) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("login_activity.txt", true));
        writer.append(message + "\n");
        writer.close();
    }
}
