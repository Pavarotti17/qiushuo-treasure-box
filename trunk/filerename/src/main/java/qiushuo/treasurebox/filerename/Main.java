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
 * (created at 2012-10-30)
 */
package qiushuo.treasurebox.filerename;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

/**
 * @author <a href="mailto:shuo.qius@alibaba-inc.com">QIU Shuo</a>
 */
public class Main {
    public static void main(String[] args) throws Exception {
        new Main().run();
    }

    private Set<String> prep = new HashSet<String>();

    private Main() {
        prep.add("a");
        prep.add("abaft");
        prep.add("aboard");
        prep.add("about");
        prep.add("above");
        prep.add("absent");
        prep.add("across");
        prep.add("afore");
        prep.add("after");
        prep.add("against");
        prep.add("along");
        prep.add("alongside");
        prep.add("amid");
        prep.add("amidst");
        prep.add("among");
        prep.add("amongst");
        prep.add("an");
        prep.add("apropos");
        prep.add("around");
        prep.add("as");
        prep.add("aside");
        prep.add("astride");
        prep.add("at");
        prep.add("athwart");
        prep.add("atop");
        prep.add("barring");
        prep.add("before");
        prep.add("behind");
        prep.add("below");
        prep.add("beneath");
        prep.add("beside");
        prep.add("besides");
        prep.add("between");
        prep.add("beyond");
        prep.add("but");
        prep.add("by");
        prep.add("circa");
        prep.add("c.");
        prep.add("ca.");
        prep.add("concerning");
        prep.add("despite");
        prep.add("down");
        prep.add("during");
        prep.add("except");
        prep.add("excluding");
        prep.add("failing");
        prep.add("following");
        prep.add("for");
        prep.add("from");
        prep.add("given");
        prep.add("in");
        prep.add("including");
        prep.add("inside");
        prep.add("into");
        prep.add("lest");
        prep.add("like");
        prep.add("mid");
        prep.add("midst");
        prep.add("minus");
        prep.add("modulo");
        prep.add("near");
        prep.add("next");
        prep.add("notwithstanding");
        prep.add("of");
        prep.add("off");
        prep.add("on");
        prep.add("onto");
        prep.add("opposite");
        prep.add("out");
        prep.add("outside");
        prep.add("over");
        prep.add("pace");
        prep.add("past");
        prep.add("per");
        prep.add("plus");
        prep.add("pro");
        prep.add("qua");
        prep.add("regarding");
        prep.add("round");
        prep.add("sans");
        prep.add("save");
        prep.add("since");
        prep.add("than");
        prep.add("through");
        prep.add("thru");
        prep.add("throughout");
        prep.add("thruout");
        prep.add("till");
        prep.add("times");
        prep.add("to");
        prep.add("toward");
        prep.add("towards");
        prep.add("under");
        prep.add("underneath");
        prep.add("unlike");
        prep.add("until");
        prep.add("unto");
        prep.add("up");
        prep.add("upon");
        prep.add("versus");
        prep.add("vs.");
        prep.add("v.");
        prep.add("via");
        prep.add("vice");
        prep.add("with");
        prep.add("within");
        prep.add("without");
    }

    public void run() throws Exception {
        BufferedReader sin = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("root dir:");
        String rootPath = sin.readLine().trim();
        File root = new File(rootPath);
        renameDir(root);
    }

    private String newName(String name) {
        StringBuilder sb = new StringBuilder();
        String[] tokens = StringUtils.split(name);
        for (int i = 0; i < tokens.length; ++i) {
            String t = tokens[i];
            if (t.length() == 0) {
                continue;
            } else if (prep.contains(t.toLowerCase())) {
                if (i > 0) sb.append(' ');
                sb.append(t.toLowerCase());
            } else {
                char fst = t.toUpperCase().charAt(0);
                String suff = t.substring(1);
                if (i > 0) sb.append(' ');
                sb.append(fst).append(suff.toLowerCase());
            }
        }
        return sb.toString();
    }

    private void renameFile(File f) throws Exception {
        String orgName = f.getName();
        String name = orgName.toLowerCase();
        if (name.endsWith(".mp3") || name.endsWith(".aac")) {
            name = newName(name.substring(0, name.length() - 4)) + name.substring(name.length() - 4);
            File parent = f.getParentFile();
            f.renameTo(new File(parent, name));
            System.out.println("File " + orgName + " renamed to " + f.getName());
        } else {
            System.out.println("Skip: " + f.getAbsolutePath());
        }
    }

    private void renameDir(File dir) throws Exception {
        if (!dir.isDirectory()) {
            System.err.println(dir.getAbsolutePath() + " is not a dir");
        }
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) return;
        for (File f : files) {
            if (f.isDirectory()) {
                renameDir(f);
            } else {
                renameFile(f);
            }
        }
    }

}
