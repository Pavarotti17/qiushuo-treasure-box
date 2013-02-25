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
package qiushuo.treasurebox.disksync.model;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import qiushuo.treasurebox.disksync.common.StringUtils;

/**
 * format: <code>md5:size:timestamp,path</code><br/>
 * example:
 * <code>1234567890ABCDEF1234567890ABCDEF:1234567:1357872482845,China/G 5/张艺谋/活着/huozhe.avi</code>
 * 
 * @author <a href="mailto:shuo.qius@alibaba-inc.com">QIU Shuo</a>
 */
public class FileContent {
    private final String path;
    private final String md5String;
    private final byte[] md5;
    private final long size;
    private final long timestamp;

    public FileContent(String path, String md5String, long size, long timestamp) throws NoSuchAlgorithmException {
        this.path = path;
        this.md5String = md5String.toUpperCase();
        this.md5 = StringUtils.fromString2Byte(md5String);
        if (this.md5.length != MessageDigest.getInstance("MD5").getDigestLength()) {
            throw new IllegalArgumentException("wrong md5 string: " + md5String);
        }
        this.size = size;
        this.timestamp = timestamp;
    }

    public FileContent(String path, byte[] md5, long size, long timestamp) throws NoSuchAlgorithmException {
        this.path = path;
        this.md5String = StringUtils.bytes2String(md5).toUpperCase();
        this.md5 = md5;
        if (this.md5.length != MessageDigest.getInstance("MD5").getDigestLength()) {
            throw new IllegalArgumentException("wrong md5 string: " + md5String);
        }
        this.size = size;
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(md5String.length() + 20 + path.length() + 3);
        sb.append(md5String).append(':').append(size).append(':').append(timestamp).append(',').append(path);
        return sb.toString();
    }

    public String getPath() {
        return path;
    }

    public String getMd5String() {
        return md5String;
    }

    /**
     * @return do not modify it!
     */
    public byte[] getMd5() {
        return md5;
    }

    public long getSize() {
        return size;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
