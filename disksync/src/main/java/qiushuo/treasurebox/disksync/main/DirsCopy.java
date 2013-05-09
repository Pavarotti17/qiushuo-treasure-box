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
 * (created at 2013-5-7)
 */
package qiushuo.treasurebox.disksync.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:shuo.qius@alibaba-inc.com">QIU Shuo</a>
 */
public class DirsCopy {
    private static final int DEFAULT_BUFFER_SIZE_M = 96;
    public static final int DEFAULT_BUFFER_SIZE = DEFAULT_BUFFER_SIZE_M * 1024 * 1024;

    /**
     * -Xms240m -Xmx240m -Xmn24m -Xss512k
     */
    public static void main(String[] args) throws Exception {
        int size = DEFAULT_BUFFER_SIZE_M;
        try {
            size = Integer.parseInt(args[0].trim());
        } catch (Exception e) {
            System.out.println("args[0] is buffer size in MByte, default " + size);
        }
        System.out.println("bufferSize " + size + " MByte");
        DirCopy copier = new DirCopy(1024 * 1024 * size);
        BufferedReader sin = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("list of source dir or file path:");
        List<String> fromFilePaths = new ArrayList<String>(2);
        for (String line;;) {
            line = sin.readLine().trim();
            if (line.length() == 0)
                break;
            fromFilePaths.add(line);
        }
        System.out.print("\r\nto this dir:");
        String toRootPath = sin.readLine().trim();
        for (String fromFilePath : fromFilePaths) {
            copier.copyDirFile(new File(fromFilePath), new File(toRootPath));
        }
    }

}
