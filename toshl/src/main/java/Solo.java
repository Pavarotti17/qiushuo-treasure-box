import java.io.File;
import java.text.SimpleDateFormat;

public class Solo {
    public static void main(String[] args) throws Throwable {
        File root = new File("/Users/shuo.qius/Pictures/2015-07-19 copy.mp4");
        System.out.println(root.getAbsolutePath());
        root.setLastModified(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2015-09-23 17:12:09").getTime());
    }

    static void print(File file) {
        if (file.isDirectory()) {
            System.out.println(file.getAbsolutePath());
            for (File f : file.listFiles()) {
                print(f);
            }
        }

    }
}
