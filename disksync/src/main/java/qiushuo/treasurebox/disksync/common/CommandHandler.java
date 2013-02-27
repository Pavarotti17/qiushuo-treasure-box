package qiushuo.treasurebox.disksync.common;

import java.io.BufferedReader;

import qiushuo.treasurebox.disksync.main.Shell;

/**
 * (created at 2011-11-4)<br/>
 * MUST contain a constructor without parameter<br/>
 * NOT thread-safe
 * 
 * @author <a href="mailto:shuo.qius@gmail.com">QIU Shuo</a>
 */
public interface CommandHandler {
    /**
     * @param cmdArg sub-string after cmd. e.g. for "cd /abc/eft", param is
     *            " /abc/eft"
     */
    void handle(Shell shell, String cmdArg, BufferedReader sin) throws Exception;

    String help(int indent);
}
