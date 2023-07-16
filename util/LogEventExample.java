package util;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LogEventExample {
    private static final Logger logger = Logger.getLogger(LogEventExample.class.getName());

    public static void main(String[] args) {
        // 로그 파일 핸들러 설정
        try {
            FileHandler fileHandler = new FileHandler("logfile.log", true);
            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);
            logger.addHandler(fileHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 여러 프로세스로 이벤트 로깅
        Process[] processes = new Process[5];
        for (int i = 0; i < 5; i++) {
            final int processId = i;
            try {
                processes[i] = new ProcessBuilder("java", "-cp", "path/to/LogEventExample.class", String.valueOf(processId))
                        .redirectErrorStream(true)
                        .start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 모든 프로세스의 완료를 기다림
        for (Process process : processes) {
            try {
                process.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void logEvent(String message) {
        try (FileChannel fileChannel = FileChannel.open(Paths.get("logfile.log"), StandardOpenOption.APPEND);
             FileLock fileLock = fileChannel.lock()) {
            logger.log(Level.INFO, message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
