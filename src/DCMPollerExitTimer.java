import java.util.TimerTask;

public class DCMPollerExitTimer extends TimerTask // CLASS
{
    DCMPoller poller;

    DCMPollerExitTimer(DCMPoller eCallCenterManagerParam) // CONSTRUCTOR
    {
        poller = eCallCenterManagerParam;
    }

    @Override
    public void run() // METHOD (THREADED)
    {
        poller.exitPoller();
    }
}
