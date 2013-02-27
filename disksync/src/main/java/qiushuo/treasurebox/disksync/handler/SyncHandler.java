/*
 * Copyright 1999-2012 Alibaba Group.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * (created at 2013-2-25)
 */
package qiushuo.treasurebox.disksync.handler;

import java.io.BufferedReader;
import java.io.File;

import qiushuo.treasurebox.disksync.common.CommandHandler;
import qiushuo.treasurebox.disksync.common.Confirm;
import qiushuo.treasurebox.disksync.common.StringUtils;
import qiushuo.treasurebox.disksync.main.DirSyncShell;

/**
 * @author <a href="mailto:shuo.qius@alibaba-inc.com">QIU Shuo</a>
 */
public class SyncHandler implements CommandHandler {

    @Override
    public void handle(DirSyncShell shell, String cmdArg, BufferedReader sin) throws Exception {
        System.out.println("src dir path(e.g. F:\\Movie):");
        String line = sin.readLine().trim();
        File srcDir = new File(line);
        System.out.println("dest dir path(e.g. F:\\Movie):");
        line = sin.readLine().trim();
        File destDir = new File(line);
        if (!srcDir.isDirectory() || !destDir.isDirectory()) {
            throw new IllegalArgumentException("src or dest path is not dir! src: " + srcDir.getAbsolutePath()
                    + ", dest: " + destDir.getAbsolutePath());
        }
        System.out.println("sync from " + srcDir.getAbsolutePath());
        System.out.println("   --to-> " + destDir.getAbsolutePath());
        Confirm.confirm(sin);
        sync(srcDir, destDir);
    }

    /**
     * @param fromDir content never changed
     * @param toDir content changed according to fromDir
     */
    private void sync(File fromDir, File toDir) throws Exception {
        BuildHandler fromBuilder = new BuildHandler();
        BuildHandler toBuilder = new BuildHandler();

        //QS_TODO
    }

    @Override
    public String help(int indent) {
        return StringUtils.indent(indent) + "sync" + StringUtils.NEW_LINE + StringUtils.indent(indent + 1)
                + "sync src dir to dest dir, arguments will follow the hint";
    }

}
