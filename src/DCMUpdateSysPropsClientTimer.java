import java.util.TimerTask;

public class DCMUpdateSysPropsClientTimer extends TimerTask // CLASS
{
    DCMClient dcManagerClient;

    DCMUpdateSysPropsClientTimer(DCMClient dcManagerClientParam) // CONSTRUCTOR
    {
        dcManagerClient = dcManagerClientParam;
    }

    @Override
    public void run() // METHOD (THREADED)
    {
        dcManagerClient.showJVMDynamicPropertiesList();
    }
}
