public interface DCMPollServerCaller
{
    public void log(String messageParam, boolean logToStatus, boolean logApplication, boolean logToFile);
    public void pollServerReady();
}
