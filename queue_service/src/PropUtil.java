import java.io.InputStream;
import java.util.Properties;

public class PropUtil {

    private String fileName = "resources/config.properties";

    private Properties config = new Properties();

    public PropUtil() {
        // try() 블록 안에서 파일을 읽으면 자동으로 close()합니다.
        // Properties 파일 읽어서 Config 로딩합니다.
        try (InputStream inStream = getClass().getClassLoader().getResourceAsStream(fileName)) {
            this.config.load(inStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public PropUtil(String fileName) {
        // try() 블록 안에서 파일을 읽으면 자동으로 close()합니다.
        // Properties 파일 읽어서 Config 로딩합니다.
        try (InputStream inStream = getClass().getClassLoader().getResourceAsStream(fileName)) {
            this.config.load(inStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Properties getConfig() {
        return this.config;
    }

}
