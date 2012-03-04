package YodleeSrc;

public class RefreshHelper {

	/**
     * Indicates a high refresh_priority, which is normally used
     * while refreshing a single item on demand.
     */
    public static int REFRESH_PRIORITY_HIGH  = 1;

    /**
     * Indicates a low refresh_priority, which is normally used
     * while refreshing many items together.
     */
    public static int REFRESH_PRIORITY_LOW   = 2;


    /**
     * Indicates the stop_refresh reason as "refresh timedout".
     */
    public static int STOP_REFRESH_REASON_TIMEDOUT      = 100;

    /**
     * Indicates the stop_refresh reason as "refresh aborted by the user".
     */
    public static int STOP_REFRESH_REASON_USER_ABORTED   = 101;
    
    /**
     * Indicates the stop_refresh as "mfa user refresh timedout".
     */
    
    public static int STOP_REFRESH_REASON_MFA_TIMEDOUT	 = 102;
    
    /**
     * Indicates the stop_refresh as "mfa Gatherer timedout".
     */
    
    public static int STOP_REFRESH_REASON_MFA_GATHERER_TIMEDOUT	 = 103;
}
