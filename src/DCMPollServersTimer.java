import java.net.UnknownHostException;
import java.util.TimerTask;

public class DCMPollServersTimer extends TimerTask // CLASS
{
    DCMPoller poller;

    DCMPollServersTimer(DCMPoller eCallCenterManagerParam) // CONSTRUCTOR
    {
        poller = eCallCenterManagerParam;
    }

    @Override
    public void run() // METHOD (THREADED)
    {
        try {try { poller.pollServers(); } catch (UnknownHostException ex) {  }
    }
        catch (CloneNotSupportedException ex) { }
    }
}
