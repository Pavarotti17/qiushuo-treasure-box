/**
 */
package treasurebox.guitar.model.music;

/**
 * 
 * @author shuo.qius
 * @version $Id: Length.java, v 0.1 Apr 11, 2016 5:13:06 PM qiushuo Exp $
 */
public enum Length {
    SIXYFOURTH(64), THIRTYSECOND(32), SIXTEENTH(16), EIGHTH(8), QUARTER(4), HALF(2), WHOLE(1);

    private final int cut;

    private Length(int cut) {
        this.cut = cut;
    }

    /**
     * Getter method for property <tt>cut</tt>.
     * 
     * @return property value of cut
     */
    public int getCut() {
        return cut;
    }
    
}
