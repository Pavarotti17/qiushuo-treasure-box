/**
 */
package treasurebox.guitar.model.music;

/**
 * 
 * @author shuo.qius
 * @version $Id: PitchName.java, v 0.1 Apr 11, 2016 4:57:44 PM qiushuo Exp $
 */
public enum Key {
    C("C", 0),
    C_SHARP("C#/Db", 1),
    D("D", 2),
    E_FLAT("Eb/D#", 3),
    E("E", 4),
    F("F", 5),
    F_SHARP("F#/Gb", 6),
    G("G", 7),
    A_FLAT("Ab/G#", 8),
    A("A", 9),
    B_FLAT("Bb/A#", 10),
    B("B", 11);

    private int    offset;
    private String desc;

    /**
     * @param desc
     * @param offset semitones from {@link #C}
     */
    private Key(String desc, int offset) {
        this.desc = desc;
        this.offset = offset;
    }

    /**
     * Getter method for property <tt>offset</tt>.
     * 
     * @return property value of offset
     */
    public int getOffset() {
        return offset;
    }

    /**
     * Setter method for property <tt>offset</tt>.
     * 
     * @param offset value to be assigned to property offset
     */
    public void setOffset(int offset) {
        this.offset = offset;
    }

    /**
     * Getter method for property <tt>desc</tt>.
     * 
     * @return property value of desc
     */
    public String getDesc() {
        return desc;
    }

    /**
     * Setter method for property <tt>desc</tt>.
     * 
     * @param desc value to be assigned to property desc
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }

    /**
     * @param semiToneDiff
     * @return
     */
    public static Key getBySemitoneDiffByA(int semiToneDiff) {
        int diff = semiToneDiff % 12;
        if(diff<0){
            diff+=12;
        }
        for (Key p : Key.values()) {
            if (diff == p.offset) {
                return p;
            }
        }
        return null;
    }
}
