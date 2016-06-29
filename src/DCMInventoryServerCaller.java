public interface DCMInventoryServerCaller
{
    public void log(String messageParam, boolean logToStatus, boolean logApplication, boolean logToFile);
    public void inventoryReady();
}
