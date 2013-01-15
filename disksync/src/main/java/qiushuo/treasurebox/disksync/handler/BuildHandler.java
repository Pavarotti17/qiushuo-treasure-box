package qiushuo.treasurebox.disksync.handler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import qiushuo.treasurebox.disksync.Shell;
import qiushuo.treasurebox.disksync.common.Config;
import qiushuo.treasurebox.disksync.common.Confirm;
import qiushuo.treasurebox.disksync.common.FileSystemVisitor;
import qiushuo.treasurebox.disksync.common.FileVisitor;
import qiushuo.treasurebox.disksync.common.StringUtils;
import qiushuo.treasurebox.disksync.model.IndexFile;

/**
 * (created at 2011-11-7)
 * 
 * @author <a href="mailto:QiuShuo1985@gmail.com">QIU Shuo</a>
 */
public class BuildHandler extends PathArgumentsHandler {
    private static final String INDEX_FILE_NAME = ".fileContentIndex.qs";

    @Override
    public synchronized void handle(Shell shell, String cmdArg, BufferedReader sin) throws Exception {
        arguments = cmdArg;
        String rootPath = parserPath();
        rootPath = rootPath.trim();
        if (rootPath.length() == 0) {
            rootPath = shell.getWorkRoot().trim();
        }
        File root = new File(rootPath);
        rootPath = root.getCanonicalPath();
        root = new File(rootPath);
        System.out.println("to build index (which will override existing index) for " + rootPath);
        if (!root.isDirectory()) {
            throw new IllegalArgumentException("build root must be dir");
        }
        if (!Confirm.confirm(sin)) return;
        build(root);
    }

    private void build(final File root) throws Exception {
        FileOutputStream fout = null;
        try {
            fout = new FileOutputStream(new File(root, INDEX_FILE_NAME));
            final PrintWriter out =
                    new PrintWriter(new BufferedWriter(new OutputStreamWriter(fout, Config.INDEX_FILE_ENCODE)), true);
            FileSystemVisitor.visit(root, new FileVisitor() {
                @Override
                public void visitFile(File file) throws Exception {
                    if (root.getAbsolutePath().equals(file.getParentFile().getAbsolutePath())
                        && INDEX_FILE_NAME.equals(file.getName())) {
                        return;
                    }
                    String indexString = IndexFile.getIndexString4File(root, file);
                    out.print(indexString);
                    out.print(Config.INDEX_FILE_NEW_LINE);
                }

                @Override
                public void visitDir(File dir) throws Exception {
                    File[] list = dir.listFiles();
                    if (list == null || list.length == 0) {
                        String indexString = IndexFile.getIndexString4EmptyDir(root, dir);
                        out.print(indexString);
                        out.print(Config.INDEX_FILE_NEW_LINE);
                    }
                }
            });
            out.flush();
            out.close();
        } finally {
            try {
                fout.close();
            } catch (Exception e) {
            }
        }
    }

    @Override
    public String help(int indent) {
        return StringUtils.indent(indent)
               + "build ${absolutePath}"
               + StringUtils.NEW_LINE
               + StringUtils.indent(indent + 1)
               + "absolutePath is the dir under which index file locate";
    }

}
