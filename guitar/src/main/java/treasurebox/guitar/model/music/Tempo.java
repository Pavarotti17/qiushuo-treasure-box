/**
 */
package treasurebox.guitar.model.music;

/**
 * 
 * @author shuo.qius
 * @version $Id: Tempo.java, v 0.1 Apr 11, 2016 9:31:48 PM qiushuo Exp $
 */
public class Tempo {
    public static final Tempo MARCIA = new Tempo(120, Length.QUARTER);

    /** beats per minute */
    private final int         bpm;
    /** beat unit */
    private final Length      unit;

    /**
     * @param bpm
     * @param unit
     */
    public Tempo(int bpm, Length unit) {
        super();
        this.bpm = bpm;
        this.unit = unit;
    }

    /**
     * Getter method for property <tt>bpm</tt>.
     * 
     * @return property value of bpm
     */
    public int getBpm() {
        return bpm;
    }

    /**
     * Getter method for property <tt>unit</tt>.
     * 
     * @return property value of unit
     */
    public Length getUnit() {
        return unit;
    }

    /** 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "1/" + unit.getCut() + " = " + bpm;
    }

}
