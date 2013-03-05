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
 * (created at 2013-3-4)
 */
package qiushuo.treasurebox.disksync.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import qiushuo.treasurebox.disksync.common.FileSystemVisitor;
import qiushuo.treasurebox.disksync.common.FileVisitor;
import qiushuo.treasurebox.disksync.model.IndexFileUtil;

/**
 * @author <a href="mailto:shuo.qius@alibaba-inc.com">QIU Shuo</a>
 */
public class LastModifiedSync {

    public static void main(String[] args) throws Exception {
        BufferedReader sin = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("source dir path:");
        String fromDirPath = sin.readLine().trim();
        System.out.print("\r\ndest dir path:");
        String toDirPath = sin.readLine().trim();
        sync(new File(fromDirPath), new File(toDirPath));
    }

    /**
     * @param fromRoot e.g. <code>F:\Movie</code>
     * @param toRoot e.g. <code>E:\Movie</code>
     */
    public static void sync(final File fromRoot, final File toRoot) throws Exception {
        if (!fromRoot.isDirectory() || !toRoot.isDirectory())
            throw new IllegalArgumentException("fromRoot " + fromRoot.getAbsolutePath() + " or toRoot "
                    + toRoot.getAbsolutePath() + " is not dir ");
        FileSystemVisitor.visit(fromRoot, new FileVisitor() {
            @Override
            public void visitFile(File file) throws Exception {
                long time = file.lastModified();
                String rpath = IndexFileUtil.getRelevantPath(fromRoot, file);
                File to = IndexFileUtil.fromRelavant2File(toRoot, rpath);
                if (to.exists() && to.isFile()) {
                    to.setLastModified(time);
                } else {
                    System.err.println("file not found: " + to.getAbsolutePath());
                }
            }

            @Override
            public void visitDir(File dir) throws Exception {
            }
        });
    }

}
