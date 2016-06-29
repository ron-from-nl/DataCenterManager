import data.Server;

public interface DCMRemoteCommandCaller
{
    public void log(String messageParam, boolean logToStatus, boolean logApplication, boolean logToFile);
    public void remoteFinalCommandSuccessResponse(int stageParam, String stdOutParam, String stdErrParam);
    public void remoteCommandSuccessResponse(int stageParam, String stdOutParam, String stdErrParam);
    public void remoteCommandFailureResponse(int stageParam, String messageParam);
    public void inventoryReady(Server serverParam);
}
