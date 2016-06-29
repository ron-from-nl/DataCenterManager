package data;

public class TimeWindow
{
    private String              description;

    public static final int     SECOND                  = 1;
    public static final int     MINUTE                  = SECOND * 60;
    public static final int     HOUR                    = MINUTE * 60;
    public static final int     DAY                     = HOUR   * 24;

    private final int[]         DAYSTARTDAYSTART        = { 0, 0 };  // HR,MIN

    private int[]               start; //                 { hour, minute }
    private int[]               end; //                   { hour, minute }

    private int                 startSeconds;
    private int                 endSeconds;
    private int                 seconds;
    private float               factor;

    public TimeWindow(String descriptionParam, int startHourParam, int startMinuteParam, int endHourParam, int endMinuteParam )
    {
        description             = descriptionParam;

        start = new int[2];

        start[0]                = startHourParam;
        start[1]                = startMinuteParam;

        end   = new int[2];

        end[0]                  = endHourParam;
        end[1]                  = endMinuteParam;

        startSeconds            = (HOUR * start[0]) + (MINUTE *  start[1]);
        endSeconds              = (HOUR * end[0]) + (MINUTE * end[1]);
        seconds                 = (endSeconds - startSeconds);
        factor                  = (DAY / seconds);
    }

    public TimeWindow() {
        
    }

    public int              getSECOND()             { return SECOND; }
    public int              getMINUTE()             { return MINUTE; }
    public int              getHOUR()               { return HOUR; }
    public int              getDAY()                { return DAY; }

    public String           getDescription()        { return description; }
    public int              getStartHour()          { return start[0]; }
    public int              getStartMinute()        { return start[1]; }
    public int              getEndHour()            { return end[0]; }
    public int              getEndMinute()          { return end[1]; }
    public int              getStartSeconds()       { return startSeconds; }
    public int              getEndSeconds()         { return endSeconds; }
    public int              getSeconds()            { return seconds; }
    public float            getTimewindowFACTOR()   { return factor; }
}
