package qiushuo.treasurebox.disksync.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import qiushuo.treasurebox.disksync.common.CommandHandler;
import qiushuo.treasurebox.disksync.common.StringUtils;
import qiushuo.treasurebox.disksync.handler.BuildHandler;
import qiushuo.treasurebox.disksync.handler.CDHandler;
import qiushuo.treasurebox.disksync.handler.LLHandler;
import qiushuo.treasurebox.disksync.handler.SyncHandler;

/**
 * (created at 2011-11-4)
 * 
 * @author <a href="mailto:shuo.qius@gmail.com">QIU Shuo</a>
 */
public class DirSyncShell {
    public static final DirCopy DIR_COPIER = new DirCopy(DirCopy.DEFAULT_BUFFER_SIZE);
    private static Map<String, CommandHandler> handlers = new TreeMap<String, CommandHandler>();
    static {
        handlers.put("cd", new CDHandler());
        handlers.put("ll", new LLHandler());
        handlers.put("build", new BuildHandler());
        handlers.put("sync", new SyncHandler());
        //QS_TODO add handler
    }

    public static void main(String[] args) throws Exception {
        new DirSyncShell().run();
    }

    private void run() throws Exception {
        System.out.println("WorkRoot:");
        System.out.println(getWorkRoot());
        BufferedReader sin = new BufferedReader(new InputStreamReader(System.in));
        for (String line = null;;) {
            System.out.println();
            System.out.print(getWorkRoot() + " >> ");
            line = sin.readLine();
            if (line == null)
                return;
            line = line.trim();
            if (line.toLowerCase().equals("exit") || line.toLowerCase().equals("quit"))
                return;
            int i = line.indexOf(' ');
            if (i < 0)
                i = line.indexOf('\t');
            if (i < 0)
                i = line.length();
            String cmd = line.substring(0, i).toLowerCase();
            CommandHandler handler = handlers.get(cmd);
            if (handler == null) {
                help();
            } else {
                try {
                    handler = handler.getClass().getConstructor().newInstance();
                    handler.handle(this, line.substring(cmd.length()), sin);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String workRoot;

    private DirSyncShell() throws Exception {
        workRoot = new File(".").getCanonicalPath();
    }

    public DirSyncShell setWorkRoot(String workRoot) {
        this.workRoot = workRoot;
        return this;
    }

    public String getWorkRoot() {
        return workRoot;
    }

    private void help() {
        for (Entry<String, CommandHandler> en : handlers.entrySet()) {
            System.out.println(StringUtils.indent(1) + en.getKey());
            System.out.println(en.getValue().help(2));
        }
    }
}
