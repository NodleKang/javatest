import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.nio.file.*;
import java.nio.file.Path; // java.io.File 클래스의 업그레이드 버전

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class FileQueueService implements QueueService {

    private final String queueDirectory;

    private final String delimiter;

    private final int visibilityTimeout;

    /**
     * 생성자
     * : properties 파일을 읽어서 환경 구성을 함
     */
    public FileQueueService() {

        // Properties 파일 읽기
        PropUtil propUtil = new PropUtil("resources/config.properties");

        // Config 설정

        // 큐 디렉토리
        this.queueDirectory = propUtil.getConfig().getProperty("queueDirectory", "qs");
        // 구분자 문자
        this.delimiter = propUtil.getConfig().getProperty("fieldDelimiter", ":");
        // 볼 수 있는 시간제한(초)
        this.visibilityTimeout = Integer.parseInt(propUtil.getConfig().getProperty("visibilityTimeout", "30"));
    }

    private void lock(File lockFile) throws InterruptedException {
        while (!lockFile.mkdirs()) {
            Thread.sleep(50); // 50 밀리초 (0.05초)
        }
    }

    private void unlock(File lockFile) {
        lockFile.delete();
    }

    /**
     * 큐에 메시지 밀어넣기
     * @param queueName
     * @param msgBody
     */
    @Override
    public void push(String queueName, String msgBody) {

        File msgFile = getMessagesFile(queueName);
        File lockFile = getLockFile(queueName);

        try {
            lock(lockFile);
        } catch (InterruptedException e) {
            e.printStackTrace();
            unlock(lockFile);
            return;
        }

        if (Files.notExists(msgFile.toPath())) {
            try {
                Files.createFile(msgFile.toPath());
            } catch (IOException e) {
                System.err.format("createFile error: %s%n", e);
                unlock(lockFile);
                return;
            }
        }

        try (PrintWriter pw = new PrintWriter(new FileWriter(msgFile, true))) {
            pw.println(createRecord(0, msgBody));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            unlock(lockFile);
        }
    }

    /**
     * 큐에서 메시지 하나 꺼내오기
     * @param queueName
     * @return
     */
    @Override
    public Message pull(String queueName) {
        Message msg = null;
        File msgFile = getMessagesFile(queueName);
        File lockFile = getLockFile(queueName);

        try {
            lock(lockFile);
        } catch (InterruptedException e) {
            e.printStackTrace();
            unlock(lockFile);
            return null;
        }

        // 임시 파일 생성
        Path queuePath = Paths.get(queueDirectory);
        Path tempFile;
        try {
            tempFile = Files.createTempFile(queuePath, null, ".msg");
        } catch (IOException e) {
            e.printStackTrace();
            unlock(lockFile);
            return null;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(msgFile));
             PrintWriter pwTemp = new PrintWriter(new FileWriter(tempFile.toFile(), true))
        ) {
            String msgLine = null;

            while ((msgLine = reader.readLine()) != null) {
                if (msg == null) {
                    msg = getVisibleMessage(msgLine);

                    if (msg == null) {
                        pwTemp.println(msgLine);
                    } else {
                        pwTemp.println(getDeliveredRecord(msgLine, msg.getMsgId()));
                    }
                } else {
                    pwTemp.println(msgLine);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (msg != null) {
                    Files.move(tempFile, msgFile.toPath(), REPLACE_EXISTING);
                } else {
                    Files.delete(tempFile);
                }
            } catch (IOException e) {

            }
        }

        return msg;
    }

    /**
     * pull()로 받은 메시지 하나를 큐에서 지우기
     * @param queueName
     * @param msgId
     */
    @Override
    public void delete(String queueName, String msgId) {
        File msgFile = getMessagesFile(queueName);
        File lockFile = getLockFile(queueName);
        Path tempFile;

        try {
            lock(lockFile);
            Path queuePath = Paths.get(queueDirectory);
            tempFile = Files.createTempFile(queuePath, null, ".msg");
        } catch (InterruptedException | IOException e) {
            unlock(lockFile);
            return;
        }

        boolean processed = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(msgFile));
             PrintWriter writer = new PrintWriter(new FileWriter(tempFile.toFile(), true))) {

            String msgLine = null;

            while ((msgLine = reader.readLine()) != null) {
                if ( isToDelete(msgLine, msgId)) {
                    processed = true;
                } else {
                    writer.println(msgLine);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (processed) {
                    Files.move(tempFile, msgFile.toPath(), REPLACE_EXISTING);
                } else {
                    Files.delete(tempFile);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            unlock(lockFile);
        }
    }

    /**
     *
     * @param queueName
     */
    protected void purgeQueue(String queueName) {
        File msgFile = getMessagesFile(queueName);
        File lockFile = getLockFile(queueName);

        try {
            lock(lockFile);
        } catch (InterruptedException e) {
            e.printStackTrace();
            unlock(lockFile);
            return ;
        }

        if (Files.exists(msgFile.toPath())) {
            try {
                Files.delete(msgFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            Files.createFile(msgFile.toPath());
        } catch (IOException e) {

        }

        unlock(lockFile);
    }

    /**
     * 큐 메시지를 담을 파일을 반환합니다.
     */
    private File getMessagesFile(String queueName) {
        Path path = Paths.get(queueDirectory, queueName, "messages");
        return path.toFile();
    }

    /**
     * 큐를 위한 lock file 얻기
     */
    private File getLockFile(String queueName) {
        Path queueFolder = Paths.get(queueDirectory, queueName);

        // 큐를 저장할 디렉토리가 없으면 디렉토리 생성하기
        if (Files.notExists(queueFolder)) {
            try {
                Files.createDirectories(queueFolder);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // lock file 반환
        Path path = Paths.get(queueDirectory, queueName, ".lock");
        return path.toFile();
    }

    /**
     * 메시지 레코드 반환
     * (레코드 포멧: {prior attempts}delimiter{visible From}delimiter{receipt id}delimiter{message body})
     */
    private String createRecord(long visibleFrom, String message) {
        return "0"+ delimiter + visibleFrom + delimiter + delimiter + message;
    }

    /**
     * 레코드에 visible 메시지가 있으면, 레코드에서 visible 메시지를 구성해서 반환
     */
    private Message getVisibleMessage(String record) {
        if (record == null || record == "") {
            return null;
        }

        String[] fieldArr = record.split(delimiter, 4);

        if (Long.parseLong(fieldArr[1]) < now()) {
            Message msg = new Message(fieldArr[3], UUID.randomUUID().toString());
            return msg;
        } else {
            return null;
        }
    }

    /**
     * deliver 된 후에, 갱신된 레코드를 반환합니다.
     * 작업 시도 횟수가 1회 증가하고,
     * visibleFrom은 현재 시각부터 타임아웃 이후로 설정되며,
     * 아이디가 포함한 레코드를 반환합니다.
     */
    private String getDeliveredRecord(String record, String msgId) {
        String[] fieldArr = record.split(delimiter, 4); // 문자열을 분리하는데, 최대 4개 요소 까지만 추출
        if (fieldArr.length < 4) {
            return record;
        }

        int attempts = Integer.parseInt(fieldArr[0] + 1);
        long visibleFrom = now() + TimeUnit.SECONDS.toMillis(visibilityTimeout);

        return attempts
                + delimiter
                + visibleFrom
                + delimiter
                + msgId
                + delimiter
                + fieldArr[3];
    }

    /**
     * 삭제해야 할 레코드인지 판단합니다.
     */
    private boolean isToDelete(String record, String msgId) {

        String[] fieldArr = record.split(delimiter, 4); // 문자열을 분리하는데, 최대 4개 요소 까지만 추출

        // 레코드 구성 요소가 4개 미만이면 삭제할 대상으로 판단합니다.
        if (fieldArr.length < 4) {
            return true;
        }

        // visibleFrom이 현재시각 이후이고, 아이디가 같으면 삭제할 대상으로 판단합니다.
        if (Long.parseLong(fieldArr[1]) >= now() && fieldArr[2].equals(msgId)) {
            return true;
        }

        // 아니면 삭제할 대상이 아닌 것으로 판단합니다.
        return false;
    }

    /**
     * epoch_mills 현재 시각
     */
    long now() {
        return System.currentTimeMillis();
    }

}
