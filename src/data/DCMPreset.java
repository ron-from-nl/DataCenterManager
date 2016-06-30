package data;

import java.io.Serializable;
import java.util.Calendar;

public class DCMPreset implements Serializable, Cloneable
{
    private static final int     SECOND               = 1;
    private static final int     MINUTE               = SECOND * 60;
    private static final int     HOUR                 = MINUTE * 60;
    private static final int     DAY                  = HOUR   * 24;
    private static final int     MONTH                = DAY    * 31;
        
    private long        id;
    private long        userId;
    private String      presetName;
    private String      presetDescription;

    private boolean     startCalendarRelative;
    private Calendar    startCalendar;
    private int         startMonthSupplement;
    private int         startDaySupplement;
    private int         startHourSupplement;
    private int         startMinuteSupplement;

    private boolean     endCalendarRelative;
    private Calendar    endCalendar;
    private int         endMonthSupplement;
    private int         endDaySupplement;
    private int         endHourSupplement;
    private int         endMinuteSupplement;
    
    private boolean     enableSearch;
    private String      searchString;
    private boolean     searchExact;
    private String      selectedResources;
    private boolean     shared;

    public DCMPreset()
    {
        id =                        0;
        userId =                    0;
        presetName =                  "";
        presetDescription =           "";
        
        startCalendarRelative =     true;
        startCalendar =             Calendar.getInstance();
        startMonthSupplement =      0;
        startDaySupplement =        0;
        startHourSupplement =       0;
        startMinuteSupplement =     0;

        endCalendarRelative =       true;
        endCalendar =               Calendar.getInstance();
        endMonthSupplement =        0;
        endDaySupplement =          0;
        endHourSupplement =         0;
        endMinuteSupplement =       0;

        enableSearch =              true;
        searchString =              "";
        searchExact =               true;
        selectedResources =         "";
        shared =                    false;
    }

    public long     getId()                      { return id; }
    public long     getUserId()                  { return userId; }
    public String   getPresetName()              { return presetName; }
    public String   getPresetDescription()       { return presetDescription; }
    
    public boolean  getStartCalendarRelative()   { return startCalendarRelative; }
    public Calendar getStartCalendar()           { return startCalendar; }
    public int      getStartMonthSupplement()    { return startMonthSupplement; }
    public int      getStartDaySupplement()      { return startDaySupplement; }
    public int      getStartHourSupplement()     { return startHourSupplement; }
    public int      getStartMinuteSupplement()   { return startMinuteSupplement; }

    public boolean  getEndCalendarRelative()     { return endCalendarRelative; }
    public Calendar getEndCalendar()             { return endCalendar; }
    public int      getEndMonthSupplement()      { return endMonthSupplement; }
    public int      getEndDaySupplement()        { return endDaySupplement; }
    public int      getEndHourSupplement()       { return endHourSupplement; }
    public int      getEndMinuteSupplement()     { return endMinuteSupplement; } 

    public boolean  getEnableSearch()            { return enableSearch; }
    public String   getSearchString()            { return searchString; }
    public boolean  getSearchExact()             { return searchExact; }
    public String   getSelectedResources()       { return selectedResources; }
    public boolean  getShared()                  { return shared; }


    public void     setId(long idParam)                                             { id =                      idParam; }
    public void     setUserId(long userIdParam)                                     { userId =                  userIdParam; }
    public void     setPresetName(String presetNameParam)                           { presetName =              presetNameParam; }
    public void     setPresetDescription(String presetDescriptionParam)             { presetDescription =       presetDescriptionParam; }

    public void     setStartCalendarRelative(boolean startCalendarRelativeParam)    { startCalendarRelative =   startCalendarRelativeParam; }
    public void     setStartCalendar(Calendar startCalendarParam)                   { startCalendar = Calendar.getInstance(); startCalendar = startCalendarParam; }
    public void     setStartMonthSupplement(int     startMonthSupplementParam)      { startMonthSupplement =    startMonthSupplementParam; }
    public void     setStartDaySupplement(int     startDaySupplementParam)          { startDaySupplement =      startDaySupplementParam; }
    public void     setStartHourSupplement(int     startHourSupplementParam)        { startHourSupplement =     startHourSupplementParam; }
    public void     setStartMinuteSupplement(int     startMinuteSupplementParam)    { startMinuteSupplement =   startMinuteSupplementParam; }

    public void     setEndCalendarRelative(boolean endCalendarRelativeParam)        { endCalendarRelative =     endCalendarRelativeParam; }
    public void     setEndCalendar(Calendar endCalendarParam)                       { endCalendar = Calendar.getInstance(); endCalendar = endCalendarParam; }
    public void     setEndMonthSupplement(int     endMonthSupplementParam)          { endMonthSupplement =      endMonthSupplementParam; }
    public void     setEndDaySupplement(int     endDaySupplementParam)              { endDaySupplement =        endDaySupplementParam; }
    public void     setEndHourSupplement(int     endHourSupplementParam)            { endHourSupplement =       endHourSupplementParam; }
    public void     setEndMinuteSupplement(int     endMinuteSupplementParam)        { endMinuteSupplement =     endMinuteSupplementParam; }

    public void     setEnableSearch(boolean enableSearchParam)                      { enableSearch =            enableSearchParam; }
    public void     setSearchString(String searchStringParam)                       { searchString =            searchStringParam; }
    public void     setSearchExact(boolean searchExactParam)                        { searchExact =             searchExactParam; }
    public void     setSelectedResources(String selectedResourcesParam)             { selectedResources =       selectedResourcesParam; }
    public void     setShared(boolean sharedParam)                                  { shared =                  sharedParam; }

    @Override
    public String   toString()
    {
        String output = new String("");
        output += "Id: "                    + Long.toString(id) + "\n";
        output += "presetName: "            + presetName + "\n";
        output += "presetDescription: "     + presetDescription + "\n";
        
        output += "startCalendarRelative: " + startCalendarRelative + "\n";
        output += "startCalendar: "         + startCalendar + "\n";
        output += "startMonthSupplement: "  + startMonthSupplement + "\n";
        output += "startDaySupplement: "    + startDaySupplement + "\n";
        output += "startHourSupplement: "   + startHourSupplement + "\n";
        output += "startMinuteSupplement: " + startMinuteSupplement + "\n";
        
        output += "endCalendarRelative: "   + endCalendarRelative + "\n";
        output += "endCalendar: "           + endCalendar + "\n";
        output += "endMonthSupplement: "    + endMonthSupplement + "\n";
        output += "endDaySupplement: "      + endDaySupplement + "\n";
        output += "endHourSupplement: "     + endHourSupplement + "\n";
        output += "endMinuteSupplement: "   + endMinuteSupplement + "\n";

        output += "enableSearch: "          + enableSearch + "\n";
        output += "searchString: "          + searchString + "\n";
        output += "searchNonExact: "        + searchExact + "\n";
        output += "selectedResources: "     + selectedResources + "\n";
        output += "shared: "                + shared + "\n";

        return output;
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }
}
 ;