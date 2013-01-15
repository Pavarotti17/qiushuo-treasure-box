package qiushuo.treasurebox.disksync.common;

import java.io.File;

/**
 * (created at 2011-11-7)
 * 
 * @author <a href="mailto:QiuShuo1985@gmail.com">QIU Shuo</a>
 */
public interface FileVisitor {
    void visitFile(File file) throws Exception;

    void visitDir(File dir) throws Exception;
}
