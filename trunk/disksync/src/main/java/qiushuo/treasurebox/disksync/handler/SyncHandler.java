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
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import qiushuo.treasurebox.disksync.common.CommandHandler;
import qiushuo.treasurebox.disksync.common.Confirm;
import qiushuo.treasurebox.disksync.common.StringUtils;
import qiushuo.treasurebox.disksync.main.DirSyncShell;
import qiushuo.treasurebox.disksync.model.FileContent;
import qiushuo.treasurebox.disksync.model.IndexKey;

/**
 * @author <a href="mailto:shuo.qius@alibaba-inc.com">QIU Shuo</a>
 */
public class SyncHandler implements CommandHandler {

    public static void main(String[] args) throws Exception {
        SyncHandler sync = new SyncHandler();
        sync.handle(null, null, new BufferedReader(new InputStreamReader(System.in)));
    }

    @Override
    public void handle(DirSyncShell shell, String cmdArg, BufferedReader sin) throws Exception {
        System.out.println("src dir path(e.g. F:\\Movie)(new content)(synchonizer will never change its content):");
        String line = sin.readLine().trim();
        File srcDir = new File(line);
        System.out.println("dest dir path(e.g. E:\\Movie)(old content)(synchonizer will change its content to apply src's delta):");
        line = sin.readLine().trim();
        File destDir = new File(line);
        if (!srcDir.isDirectory() || !destDir.isDirectory()) {
            throw new IllegalArgumentException("src or dest path is not dir! src: " + srcDir.getAbsolutePath()
                    + ", dest: " + destDir.getAbsolutePath());
        }
        System.out.println("sync from " + srcDir.getAbsolutePath());
        System.out.println("   --to-> " + destDir.getAbsolutePath());
        if (Confirm.confirm(sin)) {
            sync(srcDir, destDir);
        } else {
            System.out.println("sync exited, never started.");
        }

    }

    /**
     * @param fromDir content never changed
     * @param toDir content changed according to fromDir
     */
    private void sync(final File fromDir, final File toDir) throws Exception {
        System.out.println("building fromDir index: " + fromDir.getAbsolutePath());
        BuildHandler fromBuilder = new BuildHandler();
        fromBuilder.build(fromDir);
        final Set<String> emptyDirs = fromBuilder.getEmptyDir();
        final Map<IndexKey, FileContent> fromIndexMap = buildIndexMap(fromBuilder.getFileMap());
        fromBuilder = null;

        System.out.println("building toDir index: " + toDir.getAbsolutePath());
        BuildHandler toBuilder = new BuildHandler();
        toBuilder.build(toDir);
        final Map<IndexKey, FileContent> toIndexMap = buildIndexMap(toBuilder.getFileMap());
        toBuilder = null;

        System.out.println("index built. synchronizing...");
        Map<IndexKey, FileContent> added = mapSubtract(fromIndexMap, toIndexMap);
        for (Entry<IndexKey, FileContent> en : added.entrySet()) {
            FileContent fc = en.getValue();
            //QS_TODO
        }
        added = null;

        //QS_TODO
    }

    private Map<IndexKey, FileContent> buildIndexMap(Map<String, FileContent> input) {
        Map<IndexKey, FileContent> map = new TreeMap<IndexKey, FileContent>();
        for (Entry<String, FileContent> en : input.entrySet()) {
            FileContent fc = en.getValue();
            IndexKey index = new IndexKey(fc.getMd5(), fc.getSize());
            if (map.containsKey(index)) {
                String msg = "file md5 duplicated: " + fc + ": " + map.get(index);
                System.err.println(msg);
                throw new IllegalArgumentException(msg);
            }
            map.put(index, fc);
        }
        return map;
    }

    @Override
    public String help(int indent) {
        return StringUtils.indent(indent) + "sync" + StringUtils.NEW_LINE + StringUtils.indent(indent + 1)
                + "sync src dir to dest dir, arguments will follow the hint";
    }

    /**
     * might be the same reference with parameter
     * 
     * @return map1 - map2. never null
     */
    private static Map<IndexKey, FileContent> mapSubtract(Map<IndexKey, FileContent> map1,
                                                          Map<IndexKey, FileContent> map2) {
        if (map1 == null || map1.isEmpty())
            return Collections.emptyMap();
        if (map2 == null || map2.isEmpty())
            return map1;
        Map<IndexKey, FileContent> map = new TreeMap<IndexKey, FileContent>();
        for (Entry<IndexKey, FileContent> en : map1.entrySet()) {
            if (!map2.containsKey(en.getKey())) {
                map.put(en.getKey(), en.getValue());
            }
        }
        return map;
    }

}
