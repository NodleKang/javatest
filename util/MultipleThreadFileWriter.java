package util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MultipleThreadFileWriter {

    // Lock 객체를 이용해서 동시에 파일에 쓰는 것을 막을 수 있습니다.
    private static Lock lock = new ReentrantLock();

    public static void main(String[] args) {
        // 멀티 스레드에서 동시에 호출하는 예시
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                writeToLog("tmp.log", "Hello from thread " + Thread.currentThread().getId());
            }).start();
        }
    }

    public static void writeToLog(String filePath, String message) {
        lock.lock();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(message);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}
