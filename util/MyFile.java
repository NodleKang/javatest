package util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class MyFile {

    /**
     * 현재 프로그램이 호출된 절대경로(path) 반환
     */
    public static String getCurrentDirectoryFullPath() {
        String currentPath = Paths.get("").toAbsolutePath().toString();
        return currentPath;
    }

    /**
     * 주어진 절대 경로의 상위 경로 반환
     */
    public static String getParentDirectoryFullPath(String directoryFullPath) {
        File directory = new File(directoryFullPath);
        return directory.getParent();
    }

    /**
     * 주어진 절대 경로에 있는 모든 파일 이름 반환
     */
    public static List<String> getFileNames(String directoryFullPath) {
        List<String> fileNames = new ArrayList<String>();
        File directory = new File(directoryFullPath);
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                fileNames.add(file.getName());
            }
        }
        return fileNames;
    }

    /**
     * 주어진 경로(path)에 해당하는 파일이 존재하는지 여부 반환
     */
    public static boolean checkFileExists(String fileFullPath) {
        File file = new File(fileFullPath);
        return file.exists() && file.isFile();
    }

    /**
     * 주어진 경로(path)에 해당하는 파일이 존재하는지 여부 반환
     */
    public static boolean checkDirectoryExists(String directoryFullPath) {
        File directory = new File(directoryFullPath);
        return directory.exists() && directory.isDirectory();
    }

    /**
     * 주어진 경로(path)에 해당하는 파일 또는 디렉토리가 존재하는지 여부 반환
     */
    public static boolean checkFileOrDirectoryExists(String fileOrDirectoryFullPath) {
        File fileOrDirectory = new File(fileOrDirectoryFullPath);
        return fileOrDirectory.exists();
    }

    /**
     * 주어진 경로(path)가 없으면 디렉토리 생성 (하위 디렉토리 포함 여부 선택 가능)
     */
    public static void createDirectory(String directoryFullPath, boolean includeSubdirectories) {
        // 디렉토리 객체 생성
        File directory = new File(directoryFullPath);
        // 디렉토리가 존재하지 않는 경우에만 디렉토리 생성
        if (!directory.exists()) {
            // 디렉토리 생성
            directory.mkdirs();
            // 하위 디렉토리 포함 여부가 true인 경우에만 하위 디렉토리 생성
            if (includeSubdirectories) {
                // 디렉토리의 상위 디렉토리 경로를 가져옴
                String parentDirectoryPath = directory.getParent();
                // 상위 디렉토리가 존재하지 않는 경우에만 상위 디렉토리 생성
                if (!checkDirectoryExists(parentDirectoryPath)) {
                    createDirectory(parentDirectoryPath, true);
                }
            }
        }
    }

    /**
     * 주어진 경로에 파일 생성
     */
    public static void createFile(String fileFullPath) {
        // 파일 객체 생성
        File file = new File(fileFullPath);
        // 파일이 존재하지 않는 경우에만 파일 생성
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 절대경로 파일 삭제
      */
    public static void deleteFile(String fileFullPath) {
        // 파일 객체 생성
        File file = new File(fileFullPath);
        // 파일이 존재하는 경우에만 파일 삭제
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * 새로운 경로로 파일 이동
     */
    public static void moveFile(String filename, String beforeFilePath, String afterFilePath) {

        String filePath = afterFilePath+"/"+filename;

        File dir = new File(afterFilePath);

        if (!dir.exists()) {
            dir.mkdirs();
        }

        try {
            File file = new File(beforeFilePath);
            file.renameTo(new File(afterFilePath));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 특정 경로에서 시작 키워드와 종료 키워드에 맞는 파일의 절대 경로 목록을 찾아서 String 배열로 반환 (하위 디렉토리 포함 여부 선택 가능)
     * @param path
     * @param startKeyword
     * @param endKeyword
     * @param includeSubdirectories
     * @return
     */
    public static String[] findFiles(String path, String startKeyword, String endKeyword, boolean includeSubdirectories) {
        // ArrayList 객체를 사용해서 파일 경로 저장
        List<String> fileList = new ArrayList<>();
        // 디렉토리 객체 생성
        File directory = new File(path);
        // 디렉토리가 존재하고 디렉토리인 경우에만 파일 목록 읽기
        if (directory.exists() && directory.isDirectory()) {
            // 디렉토리의 파일 목록을 읽어서 파일 경로 목록에 추가
            File[] files = directory.listFiles();
            // 파일 목록이 null이 아닌 경우에만 파일 경로 목록에 추가
            if (files != null) {
                // 파일 목록을 순회하면서 파일 경로 목록에 추가
                for (File file : files) {
                    // 파일이고 시작 키워드와 종료 키워드에 맞는 경우에만 파일 경로 목록에 추가
                    if (file.isFile() && file.getName().startsWith(startKeyword) && file.getName().endsWith(endKeyword)) {
                        fileList.add(file.getAbsolutePath());
                        // 하위 디렉토리 포함 여부가 true인 경우에만 하위 디렉토리의 파일 경로 목록을 추가
                    } else if (includeSubdirectories && file.isDirectory()) {
                        // 재귀 호출을 사용해서 하위 디렉토리의 파일 경로 목록을 추가
                        String subdirectoryPath = file.getAbsolutePath();
                        // 하위 디렉토리의 파일 경로 목록을 재귀 호출을 사용해서 추가
                        String[] subdirectoryFiles = findFiles(subdirectoryPath, startKeyword, endKeyword, true);
                        // 하위 디렉토리의 파일 경로 목록을 파일 경로 목록에 추가
                        fileList.addAll(Arrays.asList(subdirectoryFiles));
                    }
                }
            }
        }
        // ArrayList 객체를 String 배열로 변환해서 반환
        return fileList.toArray(new String[0]);
    }

    /**
     * 특정 경로에서 시작 키워드와 종료 키워드에 맞는 파일의 절대 경로 목록을 찾아서 파일들 삭제
     */
    public static void deleteFiles(String path, String startKeyword, String endKeyword) {
        // 파일 경로 목록을 찾아서 String 배열로 반환
        String[] files = findFiles(path, startKeyword, endKeyword, true);
        // 파일 경로 목록을 순회하면서 파일 삭제
        for (String file : files) {
            deleteFile(file);
        }
    }

    /**
     * 파일 내용을 읽어서 String 객체로 반환 (인코딩 적용)
     */
    public static String readFileToString(File file, String encoding) throws IOException {
        // StringBuilder 객체를 사용해서 파일 내용 저장
        StringBuilder contentBuilder = new StringBuilder();
        // 파일이 존재하고 파일인 경우에만 파일 내용 읽기
        if (file.exists() && file.isFile()) {
            // BufferedReader 객체를 사용해서 파일 내용 읽기
            // try-with-resources 구문 사용 (Java 7 이상)
            // try 블록이 끝나면 자동으로 close() 메소드가 호출됨
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding))) {
                String line;
                // 파일 내용을 모두 읽어서 StringBuilder 객체에 저장
                while ((line = reader.readLine()) != null) {
                    // StringBuilder 객체에 파일 내용 저장
                    contentBuilder.append(line).append(System.lineSeparator());
                }
            }
        }
        // StringBuilder 객체를 String 객체로 변환해서 반환
        return contentBuilder.toString();
    }

    /**
     * 파일 내용을 모두 읽어서 단일 String 객체로 반환 (UTF-8 인코딩)
     */
    public static String readFileToString(String fileFullPath) {
        String content = "";
        // 파일 객체 생성
        File file = new File(fileFullPath);

        try {
            // 파일 내용을 모두 읽어서 String 객체로 반환
            content = readFileToString(file, "UTF-8"); // euc-kr
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return content;
    }

    /**
     * 파일 내용을 모두 읽어서 String 배열로 반환 (UTF-8, euc-kr 등)
     */
    public static String[] readFileToArray(String fileFullPath, String encoding) {
        // ArrayList 객체를 사용해서 파일 내용 저장
        List<String> lines = new ArrayList<>();
        // 파일 객체 생성
        File file = new File(fileFullPath);
        // 파일이 존재하고 파일인 경우에만 파일 내용 읽기
        if (file.exists() && file.isFile()) {
            // try-with-resources 구문 사용 (Java 7 이상)
            // try 블록이 끝나면 자동으로 close() 메소드가 호출됨
            // BufferedReader 객체를 사용해서 파일 내용 읽기
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
            } catch (IOException e) {
                // 예외 발생 시 스택 트레이스 출력
                e.printStackTrace();
            }
        }
        // ArrayList 객체를 String 배열로 변환해서 반환
        return lines.toArray(new String[0]);
    }

    /**
     * 파일 내용중에 특정 키워드가 포함된 첫 번째 줄의 줄 번호 반환
     */
    public static int getLineNo(String fileFullPath, String keyword) {
        int[] lineNo = {0};
        try {
            Optional<String> matchingLine = Files.lines(Paths.get(fileFullPath)) // 스트림 생성
                    .peek(line -> lineNo[0]++) // 라인을 순회하면서 peek() 메소드를 사용해서 라인 번호 증가
                    .filter(line -> line.contains(keyword)) // 특정 키워드가 포함된 라인 필터링
                    .findFirst(); // 필터링된 라인 중에서 첫 번째 라인 반환

            if (matchingLine.isPresent()) {
                return lineNo[0]; // 특정 키워드가 포함된 첫 번째 라인의 줄 번호 반환
            } else {
                return -1; // 특정 키워드가 포함된 라인이 없는 경우 -1 반환
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lineNo[0];
    }

    /**
     * 파일 내용중에 특정 키워드가 포함된 모든 라인 반환
     */
    public static String[] getLinesWithKeyword(String fileFullPath, String keyword) {
        List<String> linesWithKeyword = new ArrayList<>();
        try {
            Files.lines(Paths.get(fileFullPath)) // 스트림 생성
                    .filter(line -> line.contains(keyword)) // 특정 키워드가 포함된 라인 필터링
                    .forEach(linesWithKeyword::add); // 필터링된 라인을 ArrayList 객체에 저장
        } catch (IOException e) {
            e.printStackTrace();
        }
        return linesWithKeyword.toArray(new String[0]); // ArrayList 객체를 String 배열로 변환해서 반환
    }

    /*
     * 파일의 특정 라인 읽기
     * : 3번째 라인이 필요하면 lineNo에 3을 입력
     */
    public static String readNthLine(String fileFullPath, int lineNo) {
        String line = "";
        try (Stream<String> lines = Files.lines(Paths.get(fileFullPath))) {
            line = lines.skip(lineNo-1).findFirst().get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return line;
    }

    /**
     * 파일의 라인 수 구하기
     */
    public static long getCountOfLines(String fileFullPath) {
        long lines = 0;
        try {
            lines = Files.lines(Paths.get(fileFullPath)).count();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    /*
     * 파일이 마지막으로 수정된 날짜 구하기
     */
    public static Date getLastModified(String fileFullPath) {
        File file = new File(fileFullPath);
        // file.lastModified() 메소드는 long 타입의 값을 반환
        return new Date(file.lastModified());
    }

    /**
     * 파일에 문자열 입력
     * position: "prepend" - 파일의 맨 앞에 입력
     *           "overwrite" - 파일의 내용을 모두 지우고 입력
     *           "append" - 파일의 맨 뒤에 입력
     */
    public static boolean writeToFile(String path, String content, String position) {

        // 파일의 맨 앞에 입력하는 경우 기존 내용을 읽어서 변수에 담기
        String existingContent = "";
        if (position.equals("prepend")) {
            try {
                existingContent = readFileToString(new File(path), "UTF-8");
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }

        // try-with-resources 구문 사용 (Java 7 이상)
        // try 블록이 끝나면 자동으로 close() 메소드가 호출됨
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path, position.equals("append")))) {
            if (position.equals("prepend")) {
                writer.write(content);
                writer.newLine();
                writer.write(existingContent);
            } else if (position.equals("overwrite")) {
                writer.write(content);
                writer.newLine();
            } else {
                writer.write(content);
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            System.out.println("파일에 문자열을 입력하는 도중 오류가 발생했습니다: " + e.getMessage());
            return false;
        }
    }

    /**
     * 파일에서 문자열을 다른 문자열로 바꾸기
     */
    public static void replaceStringInFile(String fileFullPath, String oldString, String newString) {
        try {
            File file = new File(fileFullPath);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(file), "UTF-8")
            );
            StringBuilder stringBuilder = new StringBuilder();
            String line;

            while ( (line = reader.readLine()) != null ) {
                line = line.replaceAll(oldString, newString);
                stringBuilder.append(line).append("\n");
            }

            reader.close();

            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(file), "UTF-8")
            );
            writer.write(stringBuilder.toString());
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 직렬화된 객체를 파일에 저장하기
     * 직렬화된 객체 = 내용을 바이트 단위로 변환하여 파일이나 네트워크를 통해 송수신 가능하게 한 것
     */
    public static void saveSerializeObjectToFile(Object o, String fileFullPath) {

        try {
            // FileOutputStream 생성
            FileOutputStream fileOutputStream = new FileOutputStream(fileFullPath);
            // ObjectOutputStream 생성
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            // 객체를 직렬화(바이트 단위로 변환)해서 파일에 쓰기
            objectOutputStream.write((byte[]) o);
            // ObjectOutputStream 닫기
            objectOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 파일에서 객체를 역직렬화해서 가져오기
     */
    public static Object deserializeObjectFromFile(String fileFullPath) {

        try {
            // FileInputStream 생성
            FileInputStream fileInputStream = new FileInputStream(fileFullPath);
            // ObjectInputStream 생성
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            // 객체를 역직렬화해서 가져오기
            Object one = objectInputStream.readObject();
            Object two = objectInputStream.readObject();
            // objectInputStream 닫기
            objectInputStream.close();

            return one;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

}
