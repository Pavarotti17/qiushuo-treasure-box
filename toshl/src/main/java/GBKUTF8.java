import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;

public class GBKUTF8 {
    public static void main(String[] args) throws Throwable {
        convertDir(new File("/Users/shuo.qius/work/inspectsys/inspectcore/app"));
    }

    private static void convertDir(File dir) throws Throwable {
        File[] subs = dir.listFiles();
        if (subs == null) {
            return;
        }
        for (File f : subs) {
            if (f.isDirectory()) {
                convertDir(f);
            } else {
                convertFile(f);
            }
        }
    }

    private static void convertFile(File file) throws Throwable {
        if (!file.getName().endsWith(".java")) {
            return;
        }
        File temp = new File(file.getParentFile(), file.getName() + ".temp");
        PrintWriter fout = new PrintWriter(new OutputStreamWriter(new FileOutputStream(temp), Charset.forName("GBK")));
        try (BufferedReader sin = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), Charset.forName("utf8")))) {
            for (String line = null; (line = sin.readLine()) != null;) {
                fout.println(line);
            }
        }
        fout.flush();
        fout.close();
        String name = file.getAbsolutePath();
        file.delete();
        temp.renameTo(new File(name));

    }
}
