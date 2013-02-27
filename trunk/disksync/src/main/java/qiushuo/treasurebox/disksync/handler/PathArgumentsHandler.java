package qiushuo.treasurebox.disksync.handler;

import qiushuo.treasurebox.disksync.common.CommandHandler;

/**
 * (created at 2011-11-7)
 * 
 * @author <a href="mailto:shuo.qius@gmail.com">QIU Shuo</a>
 */
public abstract class PathArgumentsHandler implements CommandHandler {
    protected String arguments;

    protected String parserPath() {
        String path;
        arguments = arguments.trim();
        if (arguments.charAt(0) == '"') {
            int last = arguments.indexOf('"', 1);
            if (last < 0)
                throw new IllegalArgumentException("path start with '\"' must end with '\"': " + arguments);
            path = arguments.substring(1, last);
            arguments = arguments.substring(last + 1);
            return path;
        }
        int last = arguments.indexOf(' ');
        if (last < 0)
            last = arguments.indexOf('\t');
        if (last < 0) {
            path = arguments;
            arguments = "";
            return path;
        }
        path = arguments.substring(0, last);
        arguments = arguments.substring(last + 1);
        return path;
    }
}
