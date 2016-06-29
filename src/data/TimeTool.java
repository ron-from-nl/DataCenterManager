package data;

import java.util.Calendar;

public class TimeTool
{
    private TimeWindow[]        timewindowArray;

    public TimeTool()
    {
        timewindowArray = new TimeWindow[3];
        timewindowArray[0] = new TimeWindow("Morning", 0, 0, 8, 59);
        timewindowArray[1] = new TimeWindow("Daytime", 9, 0, 17, 59);
        timewindowArray[2] = new TimeWindow("Evening", 18, 0, 23, 59);
    }

    public String displayCalendar(Calendar calendarParam)
    {
        String calendarString = 
                                String.format("%04d", calendarParam.get(Calendar.YEAR)) + "-" +
                                String.format("%02d", calendarParam.get(Calendar.MONTH + 1)) + "-" +
                                String.format("%02d", calendarParam.get(Calendar.DAY_OF_MONTH)) + " " +
                                String.format("%02d", calendarParam.get(Calendar.HOUR_OF_DAY)) + ":" +
                                String.format("%02d", calendarParam.get(Calendar.MINUTE)) + ":" +
                                String.format("%02d", calendarParam.get(Calendar.SECOND));
        return calendarString;
    }

    public int getCurrentTimeWindowIndex()
    {
        int timeWindow = 0;
        Calendar currentTimeCalendar = Calendar.getInstance();
        int currentTimeHour   = currentTimeCalendar.get(Calendar.HOUR_OF_DAY);

//        for (TimeWindow timewindow: timewindowArray) { if ( (currentTimeHour >= timewindow.getStartHour()) && (currentTimeHour <= timewindow.getEndHour()) )   { timeWindow = timewindow.getDescription(); } }
        for (int counter = 0; counter <= (timewindowArray.length - 1); counter++) { if ( (currentTimeHour >= timewindowArray[counter].getStartHour()) && (currentTimeHour <= timewindowArray[counter].getEndHour()) )   { timeWindow = counter; } }
        
        return timeWindow;
    }

    public TimeWindow getCurrentTimeWindow()
    {
        return timewindowArray[getCurrentTimeWindowIndex()];
    }

    public TimeWindow getTimeWindow(int timewindowIndexParam)
    {
        return timewindowArray[timewindowIndexParam];
    }
}
