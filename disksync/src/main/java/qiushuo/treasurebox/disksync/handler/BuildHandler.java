package qiushuo.treasurebox.disksync.handler;

import java.io.BufferedReader;
import java.io.File;

import qiushuo.treasurebox.disksync.Shell;
import qiushuo.treasurebox.disksync.common.Confirm;

/**
 * (created at 2011-11-7)
 * 
 * @author <a href="mailto:QiuShuo1985@gmail.com">QIU Shuo</a>
 */
public class BuildHandler extends PathArgumentsHandler {
    private static final String INDEX_FILE_NAME = ".fileContentIndex.qs";

    @Override
    public void handle(Shell shell, String cmdArg, BufferedReader sin) throws Exception {
        arguments = cmdArg;
        String rootPath = parserPath();
        rootPath = rootPath.trim();
        if (rootPath.length() == 0) {
            rootPath = shell.getWorkRoot().trim();
        }
        File root = new File(rootPath);
        rootPath = root.getCanonicalPath();
        System.out.println("build index for " + rootPath);
        if (!Confirm.confirm(sin)) return;
        root = new File(rootPath);
        //QS_TODO
    }

}
