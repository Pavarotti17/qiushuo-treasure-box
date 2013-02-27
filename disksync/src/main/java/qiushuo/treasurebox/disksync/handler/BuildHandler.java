package qiushuo.treasurebox.disksync.handler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import qiushuo.treasurebox.disksync.common.Config;
import qiushuo.treasurebox.disksync.common.Confirm;
import qiushuo.treasurebox.disksync.common.FileSystemVisitor;
import qiushuo.treasurebox.disksync.common.FileVisitor;
import qiushuo.treasurebox.disksync.common.StringUtils;
import qiushuo.treasurebox.disksync.main.DirSyncShell;
import qiushuo.treasurebox.disksync.model.FileContent;
import qiushuo.treasurebox.disksync.model.IndexFileUtil;

/**
 * (created at 2011-11-7)
 * 
 * @author <a href="mailto:shuo.qius@gmail.com">QIU Shuo</a>
 */
@SuppressWarnings("unchecked")
public class BuildHandler extends PathArgumentsHandler {
    private Map<String, FileContent> fileMap;
    private Set<String> emptyDir;

    @Override
    public synchronized void handle(DirSyncShell shell, String cmdArg, BufferedReader sin) throws Exception {
        arguments = cmdArg;
        String rootPath = parserPath();
        rootPath = rootPath.trim();
        if (rootPath.length() == 0) {
            rootPath = shell.getWorkRoot().trim();
        }
        File root = new File(rootPath);
        rootPath = root.getCanonicalPath();
        System.out.println("to build index (which will override existing index) for " + rootPath);
        if (!root.isDirectory()) {
            throw new IllegalArgumentException("build root must be dir");
        }
        if (!Confirm.confirm(sin))
            return;
        build(root);
    }

    /**
     * @return key: {@link FileContent#getPath()}
     */
    public Map<String, FileContent> getFileMap() {
        return (Map<String, FileContent>) (fileMap == null ? Collections.emptyMap() : fileMap);
    }

    public Set<String> getEmptyDir() {
        return (Set<String>) (emptyDir == null ? Collections.emptySet() : emptyDir);
    }

    public void build(final File root) throws Exception {
        File indexFile = new File(root, Config.INDEX_FILE_NAME);
        final Map<String, FileContent> oldMap = new TreeMap<String, FileContent>();
        //build oldMap
        if (indexFile.exists()) {
            FileInputStream fin = null;
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        fin = new FileInputStream(indexFile),
                        Config.INDEX_FILE_ENCODE));
                for (String line = null; (line = in.readLine()) != null;) {
                    FileContent fc = null;
                    try {
                        fc = IndexFileUtil.decodeAsFileContent(line);
                    } catch (Exception e) {
                    }
                    if (fc != null) {
                        oldMap.put(fc.getPath(), fc);
                    }
                }
            } finally {
                try {
                    fin.close();
                } catch (Exception e) {
                }
            }
        }

        //build newMap
        FileOutputStream fout = null;
        try {
            fout = new FileOutputStream(indexFile);
            final PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                    fout,
                    Config.INDEX_FILE_ENCODE)), true);
            FileSystemVisitor.visit(root, new FileVisitor() {
                private FileContent checkExists(File file) {
                    String path = StringUtils.getRelevantPath(root, file);
                    FileContent fc = oldMap.get(path);
                    if (fc == null)
                        return null;
                    if (fc.getSize() == file.length() && fc.getTimestamp() == file.lastModified()) {
                        return fc;
                    }
                    return null;
                }

                @Override
                public void visitFile(File file) throws Exception {
                    if (root.getAbsolutePath().equals(file.getParentFile().getAbsolutePath())
                            && Config.INDEX_FILE_NAME.equals(file.getName())) {
                        return;
                    }
                    FileContent fc = checkExists(file);
                    if (fc == null) {
                        fc = IndexFileUtil.buildIndexFileContent(root, file);
                    }
                    if (fileMap == null) {
                        fileMap = new TreeMap<String, FileContent>();
                    }
                    fileMap.put(fc.getPath(), fc);
                    out.print(fc.toString());
                    out.print(Config.INDEX_FILE_NEW_LINE);
                }

                @Override
                public void visitDir(File dir) throws Exception {
                    File[] list = dir.listFiles();
                    if (list == null || list.length == 0) {
                        if (emptyDir == null) {
                            emptyDir = new TreeSet<String>();
                        }
                        emptyDir.add(StringUtils.getRelevantPath(root, dir) + Config.INDEX_FILE_PATH_SEPERATOR);
                        String indexString = IndexFileUtil.getIndexString4EmptyDir(root, dir);
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
        return StringUtils.indent(indent) + "build ${absolutePath}" + StringUtils.NEW_LINE
                + StringUtils.indent(indent + 1) + "absolutePath is the dir under which index file locate";
    }

}
