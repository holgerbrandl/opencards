package info.opencards.core;

/**
 * The reversing policy of a flashcard. The front, the back or a randomly chosen side might be presented in order ti
 * test whether the user can remember the missing side.
 *
 * @author Holger Brandl
 */
public enum ReversePolicy {

    NORMAL, REVERSE, RANDOM_REVERSE;


    public static ReversePolicy getDefault() {
        return NORMAL;
    }


    public static int toInt(ReversePolicy reversePolicy) {
        switch (reversePolicy) {
            case NORMAL:
                return 0;
            case REVERSE:
                return 1;
            case RANDOM_REVERSE:
                return 2;
            default:
                throw new RuntimeException("unsupported reverse policy occured");
        }
    }


    public static ReversePolicy toPolicy(int polIndex) {
        ReversePolicy selectPolicy;
        switch (polIndex) {
            case 0:
                selectPolicy = ReversePolicy.NORMAL;
                break;
            case 1:
                selectPolicy = ReversePolicy.REVERSE;
                break;
            case 2:
                selectPolicy = ReversePolicy.RANDOM_REVERSE;
                break;
            default:
                throw new RuntimeException("invalid policy index");
        }
        return selectPolicy;
    }
}