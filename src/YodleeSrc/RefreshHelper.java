package YodleeSrc;

public class RefreshHelper {

	/**
     * Indicates a high refresh_priority, which is normally used
     * while refreshing a single item on demand.
     */
    public static Integer REFRESH_PRIORITY_HIGH  = new Integer(1);

    /**
     * Indicates a low refresh_priority, which is normally used
     * while refreshing many items together.
     */
    public static Integer REFRESH_PRIORITY_LOW   = new Integer(2);


    /**
     * Indicates the stop_refresh reason as "refresh timedout".
     */
    public static Integer STOP_REFRESH_REASON_TIMEDOUT  = new Integer(100);

    /**
     * Indicates the stop_refresh reason as "refresh aborted by the user".
     */
    public static Integer STOP_REFRESH_REASON_USER_ABORTED   = new Integer(101);
    
    /**
     * Indicates the stop_refresh as "mfa user refresh timedout".
     */
    
    public static Integer STOP_REFRESH_REASON_MFA_TIMEDOUT	 = new Integer(102);
    
    /**
     * Indicates the stop_refresh as "mfa Gatherer timedout".
     */
    
    public static Integer STOP_REFRESH_REASON_MFA_GATHERER_TIMEDOUT	 = new Integer(103);
    
}
