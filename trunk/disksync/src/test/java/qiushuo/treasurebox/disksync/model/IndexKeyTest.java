package qiushuo.treasurebox.disksync.model;

import java.security.MessageDigest;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * (created at 2011-11-7)
 * 
 * @author <a href="mailto:QiuShuo1985@gmail.com">QIU Shuo</a>
 */
public class IndexKeyTest extends TestCase {
    public void testMd5() throws Exception {
        byte[] fileContent = "afjaj;af;lkjldaj;ajaj;faj;fdj;klafj;fdjfjdk".getBytes();
        MessageDigest md = MessageDigest.getInstance("md5");
        md.update(fileContent);
        FileIndexKey key1 = new FileIndexKey(md.digest(), fileContent.length);

        String string = "9C62986273E1A28EFD5B03F8770DFBCD:43";
        FileIndexKey key2 = new FileIndexKey(string);

        Assert.assertEquals(key1.hashCode(), key2.hashCode());
        Assert.assertEquals(key1.toString(), key2.toString());
        Assert.assertEquals(key1, key2);
    }
}
