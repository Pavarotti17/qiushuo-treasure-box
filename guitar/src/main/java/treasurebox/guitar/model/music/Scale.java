/**
 */
package treasurebox.guitar.model.music;

import treasurebox.guitar.mrotondo.transcription.Interval;

/**
 * 
 * @author shuo.qius
 * @version $Id: Scale.java, v 0.1 Apr 11, 2016 4:30:51 PM qiushuo Exp $
 */
public enum Scale {
    /** major scale */
    MAJOR("Major", "Ionian", Interval.MAJOR_SECOND, Interval.MAJOR_SECOND, Interval.MINOR_SECOND,
          Interval.MAJOR_SECOND, Interval.MAJOR_SECOND, Interval.MAJOR_SECOND,
          Interval.MINOR_SECOND),
    /** natural minor scale */
    MINOR("Minor", "Aeolian", Interval.MAJOR_SECOND, Interval.MINOR_SECOND, Interval.MAJOR_SECOND,
          Interval.MAJOR_SECOND, Interval.MINOR_SECOND, Interval.MAJOR_SECOND,
          Interval.MAJOR_SECOND);
    /***/
    private final String     name;
    private final String     mode;
    /**describe notes in an octave*/
    private final Interval[] intervals;

    /**
     * @param name
     */
    private Scale(String name, String mode, Interval... intervals) {
        this.name = name;
        this.mode = mode;
        this.intervals = intervals;
    }

    /**
     * Getter method for property <tt>intervals</tt>.
     * 
     * @return property value of intervals
     */
    public Interval[] getIntervals() {
        return intervals;
    }

    /**
     * Getter method for property <tt>mode</tt>.
     * 
     * @return property value of mode
     */
    public String getMode() {
        return mode;
    }

    /**
     * Getter method for property <tt>name</tt>.
     * 
     * @return property value of name
     */
    public String getName() {
        return name;
    }
}
