package qiushuo.treasurebox.disksync.common;

import java.io.BufferedReader;

import qiushuo.treasurebox.disksync.Shell;

/**
 * (created at 2011-11-4)
 * 
 * @author <a href="mailto:QiuShuo1985@gmail.com">QIU Shuo</a>
 */
public interface CommandHandler {
    /**
     * @param cmdArg sub-string after cmd. e.g. for "cd /abc/eft", param is
     *            " /abc/eft"
     */
    void handle(Shell shell, String cmdArg, BufferedReader sin) throws Exception;
}
