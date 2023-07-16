package util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;

public class MultipleProcessFileWriter {

    public static void main(String[] args) {
        // 여러 개의 프로세스에서 실행되는 경우, 각 프로세스에서 동일한 FILE_PATH를 사용해야 합니다.
        String data = "Hello, World!";
        writeToFile("tmp.log", data);
    }

    private static void writeToFile(String filePath, String data) {
        FileOutputStream fileOutputStream = null;
        FileChannel fileChannel = null;
        FileLock fileLock = null;

        try {
            fileOutputStream = new FileOutputStream(filePath, true); // true: append 모드
            fileChannel = fileOutputStream.getChannel();

            // 파일 잠금 획득
            fileLock = fileChannel.lock();

            // 데이터를 파일에 추가
            byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
            fileOutputStream.write(bytes);
            fileOutputStream.write(System.lineSeparator().getBytes(StandardCharsets.UTF_8)); // 개행 추가

            fileOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // 파일 잠금 해제
                if (fileLock != null) {
                    fileLock.release();
                }

                // 리소스 해제
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
