package twilight.bgfx;

/**
 * <p>BGFXException is used for situations
 * where the BGFX rendering API has encounter an error
 * of the user has supplied some type of invalid state.</p>
 * 
 * @author tmccrary
 *
 */
@SuppressWarnings("serial")
public class BGFXException extends RuntimeException {

    public BGFXException(String msg) {
        super(msg);
    }

    public BGFXException(String msg, Exception e) {
        super(msg, e);

    }

}
