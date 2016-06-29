public interface DCMDBClientCaller
{

//    public void logToApplication(String messageParam);
//    public void logToFile(String messageParam);
//    public void logToStatus(String messageParam);
    public void log(String messageParam, boolean logToStatusParam, boolean logToApplicationParam, boolean logToFileParam);
    public void dbClientReady();
    public void updatedHost();
}
