import java.util.TimerTask;

public class DCMUpdateSysPropsTimer extends TimerTask // CLASS
{
    DCMDesktop dcManager;

    DCMUpdateSysPropsTimer(DCMDesktop dcManagerParam) // CONSTRUCTOR
    {
        dcManager = dcManagerParam;
    }

    @Override
    public void run() // METHOD (THREADED)
    {
        dcManager.showJVMDynamicPropertiesList();
    }
}
