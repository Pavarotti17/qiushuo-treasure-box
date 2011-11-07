package qiushuo.treasurebox.disksync.common;

import java.io.File;

/**
 * (created at 2011-11-7)
 * 
 * @author <a href="mailto:QiuShuo1985@gmail.com">QIU Shuo</a>
 */
public class FileSystemVisitor {
    public void visit(File file, FileVisitor visitor) {
        if (file == null) return;
        if (file.isDirectory()) {
            visitor.visitDir(file);
            File[] fs = file.listFiles();
            if (fs == null) return;
            for (File f : fs) {
                visit(f, visitor);
            }
        } else if (file.isFile()) {
            visitor.visitFile(file);
        }
    }
}
