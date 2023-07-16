package util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;

public class MultipleProcessFileWriter {

    public static void main(String[] args) {
        // ���� ���� ���μ������� ����Ǵ� ���, �� ���μ������� ������ FILE_PATH�� ����ؾ� �մϴ�.
        String data = "Hello, World!";
        writeToFile("tmp.log", data);
    }

    private static void writeToFile(String filePath, String data) {
        FileOutputStream fileOutputStream = null;
        FileChannel fileChannel = null;
        FileLock fileLock = null;

        try {
            fileOutputStream = new FileOutputStream(filePath, true); // true: append ���
            fileChannel = fileOutputStream.getChannel();

            // ���� ��� ȹ��
            fileLock = fileChannel.lock();

            // �����͸� ���Ͽ� �߰�
            byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
            fileOutputStream.write(bytes);
            fileOutputStream.write(System.lineSeparator().getBytes(StandardCharsets.UTF_8)); // ���� �߰�

            fileOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // ���� ��� ����
                if (fileLock != null) {
                    fileLock.release();
                }

                // ���ҽ� ����
                if (fileChannel != null) {
                    fileChannel.close();
                }

                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
