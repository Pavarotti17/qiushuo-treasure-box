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

import qiushuo.treasurebox.disksync.Shell;
import qiushuo.treasurebox.disksync.common.CommandHandler;
import qiushuo.treasurebox.disksync.common.StringUtils;

/**
 * @author <a href="mailto:shuo.qius@alibaba-inc.com">QIU Shuo</a>
 */
public class SyncHandler implements CommandHandler {

    @Override
    public void handle(Shell shell, String cmdArg, BufferedReader sin) throws Exception {
        // QS_TODO

    }

    @Override
    public String help(int indent) {
        return StringUtils.indent(indent) + "sync" + StringUtils.NEW_LINE + StringUtils.indent(indent + 1)
                + "sync src dir to dest dir, arguments will follow the hint";
    }

}
