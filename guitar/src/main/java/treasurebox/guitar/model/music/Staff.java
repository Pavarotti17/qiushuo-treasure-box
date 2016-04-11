/**
 */
package treasurebox.guitar.model.music;

/**
 * same info with {@link Score}
 * 
 * @author shuo.qius
 * @version $Id: Staff.java, v 0.1 Apr 11, 2016 9:50:55 PM qiushuo Exp $
 */
public class Staff {
    private final Score score;

    // TODO add clef and line discription
    /**
     * @param score
     */
    public Staff(Score score) {
        super();
        this.score = score;
    }

}
