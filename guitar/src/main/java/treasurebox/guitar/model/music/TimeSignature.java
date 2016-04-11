/**
 */
package treasurebox.guitar.model.music;

/**
 * 
 * @author shuo.qius
 * @version $Id: TimeSignature.java, v 0.1 Apr 11, 2016 9:16:17 PM qiushuo Exp $
 */
public class TimeSignature {
    /** 4/4 */
    public static final TimeSignature COMMON_TIME  = new TimeSignature(4, Length.QUARTER);
    /** 2/2 */
    public static final TimeSignature CUT_TIME     = new TimeSignature(2, Length.HALF);
    /** 3/4 */
    public static final TimeSignature PERFECT_TIME = new TimeSignature(3, Length.QUARTER);

    /** how many beats in each measure */
    private final int                 beats;
    /** length of each beat */
    private final Length              noteValue;

    /**
     * @param beats
     * @param noteValue
     */
    public TimeSignature(int beats, Length noteValue) {
        this.beats = beats;
        this.noteValue = noteValue;
    }

    /**
     * Getter method for property <tt>beats</tt>.
     * 
     * @return property value of beats
     */
    public int getBeats() {
        return beats;
    }

    /**
     * Getter method for property <tt>noteValue</tt>.
     * 
     * @return property value of noteValue
     */
    public Length getNoteValue() {
        return noteValue;
    }

    /** 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return beats + "/" + noteValue.getCut();
    }

}
