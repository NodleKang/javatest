/*
 * 클래스 사용법
 *
 * File file = new file("파일 경로");
 * MyFileWatcher watcher = new MyFileWatcher(file);
 * Thread thread = new Thread(watcher);
 * thread.setDeamon(true);
 * thread.start();
 *
 * 사용 종료
 * watcher.stop();
 */
package test.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class MyFileWatcher implements Runnable {

    private static final int DELAY_MILLIS = 100; // 0.1초

    private boolean isRun;

    private final File file;

    /**
     * 생성자
     * : filePath 경로를 받아서 file 객체를 생성
     */
    public MyFileWatcher(String filePath) {
        this.file = new File(filePath);
    }

    /**
     * 생성자
     * : file 객체를 받음
     */
    public MyFileWatcher(File file) {
        this.file = file;
    }

    /**
     * tail 기능을 하는 쓰레드
     * : 파일의 내용을 0.01초 간격으로 읽어서 콘솔에 출력
     */
    @Override
    public void run() {
        System.out.println("Start to tail a file -" + file.getName());

        isRun = true;
        if (!file.exists()) {
            System.out.println("Failed to find a file - " + file.getAbsolutePath());
        }

        // try 문에서 stream을 열면 블록이 끝날 때 자동으로 close 해 줌
        try (BufferedReader br =
                     new BufferedReader(
                             new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)
                     )
        ) {
            while (isRun) { // 무한반복
                final String line = br.readLine(); // 파일에서 한 라인 읽어오기
                if (line != null) { // 파일에서 읽어온 라인이 null이 아니면 출력
                    System.out.println("New line added - " + line);
                } else {
                    Thread.sleep(DELAY_MILLIS);
                }
            }
        } catch (Exception e) {
            System.out.println("Failed to tail a file - " + file.getAbsolutePath());
        }

        System.out.println("Stop to tail a file - " + file.getAbsolutePath());
    }

    /**
     * 스레드가 중지될 수 있게, isRun을 false로 변경
     */
    public void stop() {
        isRun = false;
    }

}
