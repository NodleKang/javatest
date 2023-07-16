package util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MultipleThreadFileWriter {

    // Lock ��ü�� �̿��ؼ� ���ÿ� ���Ͽ� ���� ���� ���� �� �ֽ��ϴ�.
    private static Lock lock = new ReentrantLock();

    public static void main(String[] args) {
        // ��Ƽ �����忡�� ���ÿ� ȣ���ϴ� ����
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
