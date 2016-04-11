/**
 */
package treasurebox.guitar.model.music;

import junit.framework.Assert;

import org.junit.Test;

/**
 * 
 * @author shuo.qius
 * @version $Id: PitchTest.java, v 0.1 Apr 11, 2016 8:16:57 PM qiushuo Exp $
 */
public class PitchTest {
    @Test
    public void pitch() {
        Assert.assertEquals(27.5d, new Pitch(-39).getFrequency());
        Assert.assertEquals(440d, new Pitch(9).getFrequency());
        Assert.assertEquals(55d, new Pitch(-27).getFrequency());

        Assert.assertEquals("C0", new Pitch(-48).toString());
        Assert.assertEquals("B0", new Pitch(-37).toString());
        Assert.assertEquals("C4", new Pitch(0).toString());
        Assert.assertEquals("A4", new Pitch(9).toString());
        Assert.assertEquals("B4", new Pitch(11).toString());
    }
}
