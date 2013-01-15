package qiushuo.treasurebox.disksync.handler;

import java.io.BufferedReader;
import java.io.File;

import qiushuo.treasurebox.disksync.Shell;
import qiushuo.treasurebox.disksync.common.CommandHandler;
import qiushuo.treasurebox.disksync.common.StringUtils;

/**
 * (created at 2011-11-4)
 * 
 * @author <a href="mailto:QiuShuo1985@gmail.com">QIU Shuo</a>
 */
public class LLHandler implements CommandHandler {

    @Override
    public void handle(Shell shell, String cmdArg, BufferedReader sin) throws Exception {
        File dir = new File(shell.getWorkRoot());
        if (dir.isDirectory()) {
            File[] fs = dir.listFiles();
            for (File f : fs) {
                String fn = f.getName();
                if (f.isDirectory() && !fn.endsWith(File.separator)) fn += File.separator;
                System.out.println(fn);
            }
        } else {
            System.err.println(shell.getWorkRoot() + " is not a dir");
        }
    }

    @Override
    public String help(int indent) {
        return StringUtils.indent(indent) + "ll";
    }
}
