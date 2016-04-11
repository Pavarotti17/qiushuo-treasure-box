/**
 */
package treasurebox.guitar.model.music;

import java.util.ArrayList;
import java.util.List;

/**
 * @author shuo.qius
 * @version $Id: Tone.java, v 0.1 Apr 11, 2016 3:33:30 PM qiushuo Exp $
 */
public class Pitch {
    public static final Pitch       PITCH_NONE          = null;
    public static final Pitch       PITCH_MIDDLE_C      = new Pitch(0);
    public static final List<Pitch> PITCHS_GUITAR_RANGE = generateTones(
                                                            PITCH_MIDDLE_C.getPitch() - 24 + 4,
                                                            PITCH_MIDDLE_C.getPitch() + 24 + 2);

    /**
     * @param from {@link Pitch#getPitch()}, include
     * @param to {@link Pitch#getPitch()}, include
     * @return
     */
    private static List<Pitch> generateTones(int from, int to) {
        List<Pitch> list = new ArrayList<Pitch>(to - from + 1);
        for (int i = from; i <= to; ++i) {
            list.add(new Pitch(i));
        }
        return list;
    }

    /** 440.0 HZ */
    private static final double A4 = 440;
    private final Key           key;
    /** C4=0, {@link #A4}=9, 1 per semitone */
    private final int           pitch;
    /**in HZ*/
    private final double        frequency;

    /**
     * @param pitch {@link #pitch}
     */
    public Pitch(int pitch) {
        this.pitch = pitch;
        this.key = Key.getBySemitoneDiffByA(pitch);
        this.frequency = A4 * Math.pow(2, (pitch - 9) / 12.0d);
    }

    /** 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        int octave = (pitch + 48) / 12;
        return this.key.getDesc() + octave;
    }

    /** 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + pitch;
        return result;
    }

    /** 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Pitch other = (Pitch) obj;
        if (pitch != other.pitch)
            return false;
        return true;
    }

    /**
     * Getter method for property <tt>frequency</tt>.
     * 
     * @return property value of frequency
     */
    public double getFrequency() {
        return frequency;
    }

    /**
    * Getter method for property <tt>pitch</tt>.
    * 
    * @return property value of pitch
    */
    public int getPitch() {
        return pitch;
    }

}
