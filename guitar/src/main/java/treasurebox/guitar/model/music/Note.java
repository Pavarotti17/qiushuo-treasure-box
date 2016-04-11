/**
 */
package treasurebox.guitar.model.music;

/**
 * 
 * @author shuo.qius
 * @version $Id: Note.java, v 0.1 Apr 11, 2016 4:34:35 PM qiushuo Exp $
 */
public class Note {
    private final KeySignature  scale;
    private final Pitch  pitch;
    private final Length length;

    /**
     * @param scale
     * @param pitch
     * @param length
     */
    public Note(KeySignature scale, Pitch pitch, Length length) {
        this.scale = scale;
        this.pitch = pitch;
        this.length = length;
    }

    /**
     * Getter method for property <tt>scale</tt>.
     * 
     * @return property value of scale
     */
    public KeySignature getScale() {
        return scale;
    }

    /**
     * Getter method for property <tt>pitch</tt>.
     * 
     * @return property value of pitch
     */
    public Pitch getPitch() {
        return pitch;
    }

    /**
     * Getter method for property <tt>length</tt>.
     * 
     * @return property value of length
     */
    public Length getLength() {
        return length;
    }

}
