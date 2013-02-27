package qiushuo.treasurebox.disksync.common;

/**
 * (created at 2013-1-11)
 * 
 * @author <a href="mailto:shuo.qius@gmail.com">QIU Shuo</a>
 */
public interface Config {
    String INDEX_FILE_NAME = ".fileContentIndex.dirsync";
    String INDEX_FILE_PATH_SEPERATOR = "/";
    String INDEX_FILE_NEW_LINE = "\r\n";
    String INDEX_FILE_ENCODE = "utf8";
    int READ_FILE_BUFFER_LEN = 16 * 1024 * 1024;
}
