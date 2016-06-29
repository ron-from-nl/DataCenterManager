public interface DCMInventoryServerServerCaller
{
    public void log(String messageParam, boolean logToStatus, boolean logApplication, boolean logToFile);
    public void inventoryReady();
}
