/**
 */
package treasurebox.guitar.model.music;

/**
 * @author shuo.qius
 * @version $Id: Scale.java, v 0.1 Apr 11, 2016 4:52:27 PM qiushuo Exp $
 */
public class KeySignature {
    public static final KeySignature SCALE_C_MAJOR = new KeySignature(Scale.MAJOR, Key.C);
    public static final KeySignature SCALE_A_MINOR = new KeySignature(Scale.MINOR, Key.A);
    
    private final Scale              scale;
    private final Key                tonic;

    /**
     * @param scale
     * @param tonic
     */
    public KeySignature(Scale scale, Key tonic) {
        super();
        this.scale = scale;
        this.tonic = tonic;
    }

    /**
     * Getter method for property <tt>scale</tt>.
     * 
     * @return property value of scale
     */
    public Scale getScale() {
        return scale;
    }

    /**
     * Getter method for property <tt>tonic</tt>.
     * 
     * @return property value of tonic
     */
    public Key getTonic() {
        return tonic;
    }

    /** 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return this.tonic.getDesc() + " " + this.scale.getName();
    }

}
