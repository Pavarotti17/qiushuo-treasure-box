package qiushuo.treasurebox.disksync.handler;

import java.io.BufferedReader;
import java.io.File;

import qiushuo.treasurebox.disksync.Shell;
import qiushuo.treasurebox.disksync.common.CommandHandler;
import qiushuo.treasurebox.disksync.common.StringUtils;

/**
 * (created at 2011-11-4)
 * 
 * @author <a href="mailto:shuo.qius@gmail.com">QIU Shuo</a>
 */
public class CDHandler implements CommandHandler {

    @Override
    public void handle(Shell shell, String cmdArg, BufferedReader sin) throws Exception {
        String path = cmdArg.trim();
        if (!path.endsWith(File.separator))
            path = path + File.separator;
        File f = new File(path);
        path = f.getCanonicalPath();
        if (f.isDirectory()) {
            shell.setWorkRoot(path);
        } else {
            System.err.println(path + " is not a dir");
        }
    }

    @Override
    public String help(int indent) {
        return StringUtils.indent(indent) + "cd ${absolutePath}";
    }
}
