import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.tree.TreePath;


public class DCMPopupMenus // FrontEnd
{
    private JPopupMenu      datacenterMenu;
    
    private final JMenuItem datacenterMenuItemRefresh;
    private MouseListener   datacenterMenuItemRefreshMouseListener;
    
    private final JMenuItem datacenterMenuItemAddServer;
    private MouseListener   datacenterMenuItemAddServerMouseListener;
    
    private final JMenuItem serverMenuItemUpdateServers;
    private MouseListener   serverMenuItemUpdateServersMouseListener;
    
    private final JMenuItem datacenterMenuItemDeleteAllServers;
    private MouseListener   datacenterMenuItemDeleteAllServersMouseListener;
    

    private JPopupMenu      serverMenu;
    
    private final JMenuItem serverMenuItemDisablePolling;
    private MouseListener   serverMenuItemDisablePollingMouseListener;
    
    private final JMenuItem serverMenuItemEnablePolling;
    private MouseListener   serverMenuItemEnablePollingMouseListener;
    
    private final JMenuItem serverMenuItemUpdateServer;
    private MouseListener   serverMenuItemUpdateServerMouseListener;
    
    private final JMenuItem serverMenuItemDeleteServer;
    private MouseListener   serverMenuItemDeleteServerMouseListener;
    
    
    private JPopupMenu      graphMenu;
    private final JMenuItem graphMenuItemRefresh;
    private MouseListener   graphMenuItemRefreshMouseListener;
    
    private final JMenuItem graphMenuItemAutoScale;
    private MouseListener   graphMenuItemAutoScaleMouseListener;
    
    private final JMenuItem graphMenuItemCenterTime;
    private MouseListener   graphMenuItemCenterTimeMouseListener;
    
    private final JMenuItem graphMenuItemMaxTo100;
    private MouseListener   graphMenuItemMaxTo100MouseListener;
    
    private final JMenuItem graphMenuItemMinTo0;
    private MouseListener   graphMenuItemMinTo0MouseListener;
    
    private final JMenuItem graphMenuItemLineBold;
    private MouseListener   graphMenuItemLineBoldMouseListener;
    
    private final JMenuItem graphMenuItemLineNormal;
    private MouseListener   graphMenuItemLineNormalMouseListener;
    
    private final JMenuItem graphMenuItemLineFine;
    private MouseListener   graphMenuItemLineFineMouseListener;
    
    private final JMenuItem graphMenuItemLineMicro;
    private MouseListener   graphMenuItemLineMicroMouseListener;
    
    private final JMenuItem graphMenuItemShuffleColors;
    private MouseListener   graphMenuItemShuffleColorsMouseListener;
    
    private final JMenuItem graphMenuItemSaveImage;
    private MouseListener   graphMenuItemSaveImageMouseListener;
    
    private MouseEvent      evt;
    
    protected void enableAdministrator(boolean enableParam)
    {
        serverMenuItemEnablePolling.setEnabled(enableParam);
        serverMenuItemDisablePolling.setEnabled(enableParam);
        datacenterMenuItemAddServer.setEnabled(enableParam);
        serverMenuItemUpdateServer.setEnabled(enableParam);
        serverMenuItemUpdateServers.setEnabled(enableParam);
        serverMenuItemDeleteServer.setEnabled(enableParam);
        datacenterMenuItemDeleteAllServers.setEnabled(enableParam);
    }
    
    protected void enableServersMenuItemAddServer(boolean enableParam)
    {
        datacenterMenuItemAddServer.setEnabled(enableParam);
    }
    
    public DCMPopupMenus(final DCMDesktop dcmDesktop)
    {
                
// Datacenter Menu
        
        datacenterMenu = new javax.swing.JPopupMenu();

        datacenterMenuItemRefresh = new JMenuItem("Refresh");
        datacenterMenuItemRefreshMouseListener = new MouseListener()
        {

            @Override public void mouseClicked(MouseEvent me) {  }
            @Override public void mousePressed(MouseEvent me) { datacenterMenu.setVisible(false); try { dcmDesktop.buildServerTree(); } catch (CloneNotSupportedException ex) {   }; }
            @Override public void mouseReleased(MouseEvent me) {  }
            @Override public void mouseEntered(MouseEvent me) {  }
            @Override public void mouseExited(MouseEvent me) { }
        };
        datacenterMenuItemRefresh.addMouseListener(datacenterMenuItemRefreshMouseListener);        
        datacenterMenu.add(datacenterMenuItemRefresh);

        datacenterMenuItemAddServer = new JMenuItem("Add Server");
        datacenterMenuItemAddServerMouseListener = new MouseListener()
        {

            @Override public void mouseClicked(MouseEvent me) { }
            @Override public void mousePressed(MouseEvent me) { datacenterMenu.setVisible(false); if (datacenterMenuItemAddServer.isEnabled()) { dcmDesktop.addServerForm.setVisible(true);} }
            @Override public void mouseReleased(MouseEvent me) {  }
            @Override public void mouseEntered(MouseEvent me) {  }
            @Override public void mouseExited(MouseEvent me) { }
        };
        datacenterMenuItemAddServer.addMouseListener(datacenterMenuItemAddServerMouseListener);
        datacenterMenu.add(datacenterMenuItemAddServer);

        serverMenuItemUpdateServers = new JMenuItem("Update All Servers");
        serverMenuItemUpdateServersMouseListener = new MouseListener()
        {

            @Override public void mouseClicked(MouseEvent me) {  }
            @Override public void mousePressed(MouseEvent me) { datacenterMenu.setVisible(false); if (serverMenuItemUpdateServers.isEnabled()) { try { dcmDesktop.updateServers(); } catch (CloneNotSupportedException ex) {   }; }}
            @Override public void mouseReleased(MouseEvent me) {  }
            @Override public void mouseEntered(MouseEvent me) {  }
            @Override public void mouseExited(MouseEvent me) { }
        };
        serverMenuItemUpdateServers.addMouseListener(serverMenuItemUpdateServersMouseListener);        
        datacenterMenu.add(serverMenuItemUpdateServers);

        datacenterMenuItemDeleteAllServers = new JMenuItem("Delete All Servers");
        datacenterMenuItemDeleteAllServersMouseListener = new MouseListener()
        {

            @Override public void mouseClicked(MouseEvent me) { }
            @Override public void mousePressed(MouseEvent me) { datacenterMenu.setVisible(false);  if (datacenterMenuItemDeleteAllServers.isEnabled()) { dcmDesktop.openDeleteAllServerForm(); } }
            @Override public void mouseReleased(MouseEvent me) {  }
            @Override public void mouseEntered(MouseEvent me) {  }
            @Override public void mouseExited(MouseEvent me) { }
        };
        datacenterMenuItemDeleteAllServers.addMouseListener(datacenterMenuItemDeleteAllServersMouseListener);
        datacenterMenu.add(datacenterMenuItemDeleteAllServers);

// Server Menu        
        serverMenu = new javax.swing.JPopupMenu();

        serverMenuItemDisablePolling = new JMenuItem("Disable Polling");
        serverMenuItemDisablePollingMouseListener = new MouseListener()
        {

            @Override public void mouseClicked(MouseEvent me) { }
            @Override public void mousePressed(MouseEvent me) { serverMenu.setVisible(false); if (serverMenuItemDisablePolling.isEnabled()) { try { dcmDesktop.enablePollingSelectedServers(false); } catch (CloneNotSupportedException ex) {   }; } }
            @Override public void mouseReleased(MouseEvent me) {  }
            @Override public void mouseEntered(MouseEvent me) {  }
            @Override public void mouseExited(MouseEvent me) { }
        };
        serverMenuItemDisablePolling.addMouseListener(serverMenuItemDisablePollingMouseListener);
        serverMenu.add(serverMenuItemDisablePolling);

        serverMenuItemEnablePolling = new JMenuItem("Enable Polling");
        serverMenuItemEnablePollingMouseListener = new MouseListener()
        {

            @Override public void mouseClicked(MouseEvent me) { }
            @Override public void mousePressed(MouseEvent me) { serverMenu.setVisible(false); if (serverMenuItemEnablePolling.isEnabled()) { try { dcmDesktop.enablePollingSelectedServers(true); } catch (CloneNotSupportedException ex) {   }; } }
            @Override public void mouseReleased(MouseEvent me) {  }
            @Override public void mouseEntered(MouseEvent me) {  }
            @Override public void mouseExited(MouseEvent me) { }
        };
        serverMenuItemEnablePolling.addMouseListener(serverMenuItemEnablePollingMouseListener);
        serverMenu.add(serverMenuItemEnablePolling);

        serverMenuItemUpdateServer = new JMenuItem("Update Server");
        serverMenuItemUpdateServerMouseListener = new MouseListener()
        {

            @Override public void mouseClicked(MouseEvent me) {  }
            @Override public void mousePressed(MouseEvent me) { serverMenu.setVisible(false); if (serverMenuItemUpdateServer.isEnabled()) { try { dcmDesktop.updateServer(); } catch (CloneNotSupportedException ex) {   }; }}
            @Override public void mouseReleased(MouseEvent me) {  }
            @Override public void mouseEntered(MouseEvent me) {  }
            @Override public void mouseExited(MouseEvent me) { }
        };
        serverMenuItemUpdateServer.addMouseListener(serverMenuItemUpdateServerMouseListener);        
        serverMenu.add(serverMenuItemUpdateServer);

        serverMenuItemDeleteServer = new JMenuItem("Delete Server");
        serverMenuItemDeleteServerMouseListener = new MouseListener()
        {

            @Override public void mouseClicked(MouseEvent me) {  }
            @Override public void mousePressed(MouseEvent me)
            {
                serverMenu.setVisible(false);
                if (serverMenuItemDeleteServer.isEnabled())
                {
//                    dcManager.deleteServerForm.setHostname(dcManager.serverTree.getSelectionPath().getLastPathComponent().toString());
                    ArrayList<String> serverList = new ArrayList<String>();
                    for (TreePath tp:dcmDesktop.serverTree.getSelectionPaths()) { serverList.add(tp.getLastPathComponent().toString()); }
//                    dcmDesktop.deleteServerForm.setServerList(serverList);
                    dcmDesktop.openDeleteServerForm(serverList);
                }
            }
            @Override public void mouseReleased(MouseEvent me) {  }
            @Override public void mouseEntered(MouseEvent me) {  }
            @Override public void mouseExited(MouseEvent me) {  }
        };
        serverMenuItemDeleteServer.addMouseListener(serverMenuItemDeleteServerMouseListener);        
        serverMenu.add(serverMenuItemDeleteServer);
        
// Graph Menu
        
        graphMenu = new javax.swing.JPopupMenu();

        graphMenuItemRefresh = new JMenuItem("Refresh");
        graphMenuItemRefreshMouseListener = new MouseListener()
        {

            @Override public void mouseClicked(MouseEvent me) { }
            @Override public void mousePressed(MouseEvent me)
            {
                graphMenu.setVisible(false);
                dcmDesktop.setCursor(Cursor.WAIT_CURSOR);
                Thread RefreshThread = new Thread(new Runnable()
                {
                    @Override
                    @SuppressWarnings({"static-access"})
                    public void run()
                    {
//                        dcManager.setGraph(dcManager.getTrend(false,dcManager.getImageWidth(),dcManager.getImageHeight(),dcManager.manualValueMode,dcManager.startCalendar,dcManager.endCalendar,dcManager.valueLimits)); // MiddleWare Invocation
                        try { dcmDesktop.setGraph(dcmDesktop.getTrend(false,dcmDesktop.getImageWidth(),dcmDesktop.getImageHeight(),dcmDesktop.manualValueMode,dcmDesktop.startCalendar,dcmDesktop.endCalendar,dcmDesktop.valueLimits, dcmDesktop.selectionArea.getText(), dcmDesktop.lineType)); } catch (CloneNotSupportedException ex) {   } // MiddleWare Invocation
                    }
                });
                RefreshThread.setName("RefreshThread");
                RefreshThread.setDaemon(true);
                RefreshThread.setPriority(Thread.NORM_PRIORITY);
                RefreshThread.start();        
            }
            @Override public void mouseReleased(MouseEvent me) {  }
            @Override public void mouseEntered(MouseEvent me) {  }
            @Override public void mouseExited(MouseEvent me) { }
        };
        graphMenuItemRefresh.addMouseListener(graphMenuItemRefreshMouseListener);
        graphMenu.add(graphMenuItemRefresh);

        graphMenuItemAutoScale = new JMenuItem("AutoScale");
        graphMenuItemAutoScaleMouseListener = new MouseListener()
        {

            @Override public void mouseClicked(MouseEvent me) { }
            @Override public void mousePressed(MouseEvent me)
            {
                graphMenu.setVisible(false);
                dcmDesktop.setCursor(Cursor.WAIT_CURSOR);
                Thread AutoScaleThread = new Thread(new Runnable()
                {
                    @Override
                    @SuppressWarnings({"static-access"})
                    public void run()
                    {
//                        dcManager.setGraph(dcManager.getTrend(false,dcManager.getImageWidth(),dcManager.getImageHeight(),dcManager.manualValueMode,dcManager.startCalendar,dcManager.endCalendar,dcManager.valueLimits)); // MiddleWare Invocation
                        dcmDesktop.manualValueMode = false; // New graph = auto value ax
                        try { dcmDesktop.setGraph(dcmDesktop.getTrend(false,dcmDesktop.getImageWidth(),dcmDesktop.getImageHeight(),dcmDesktop.manualValueMode,dcmDesktop.startCalendar,dcmDesktop.endCalendar,dcmDesktop.valueLimits, dcmDesktop.selectionArea.getText(), dcmDesktop.lineType)); } catch (CloneNotSupportedException ex) {   } // MiddleWare Invocation
                    }
                });
                AutoScaleThread.setName("AutoScaleThread");
                AutoScaleThread.setDaemon(true);
                AutoScaleThread.setPriority(Thread.NORM_PRIORITY);
                AutoScaleThread.start();        
            }
            @Override public void mouseReleased(MouseEvent me) {  }
            @Override public void mouseEntered(MouseEvent me) {  }
            @Override public void mouseExited(MouseEvent me) { }
        };
        graphMenuItemAutoScale.addMouseListener(graphMenuItemAutoScaleMouseListener);
        graphMenu.add(graphMenuItemAutoScale);

        graphMenuItemCenterTime = new JMenuItem("Center Time");
        graphMenuItemCenterTimeMouseListener = new MouseListener()
        {

            @Override public void mouseClicked(MouseEvent me) { }
            @Override public void mousePressed(MouseEvent me) { graphMenu.setVisible(false); dcmDesktop.centerTime(evt); }
            @Override public void mouseReleased(MouseEvent me) {  }
            @Override public void mouseEntered(MouseEvent me) {  }
            @Override public void mouseExited(MouseEvent me) { }
        };
        graphMenuItemCenterTime.addMouseListener(graphMenuItemCenterTimeMouseListener);
        graphMenu.add(graphMenuItemCenterTime);

        graphMenuItemMaxTo100 = new JMenuItem("Maximum to 100");
        graphMenuItemMaxTo100MouseListener = new MouseListener()
        {

            @Override public void mouseClicked(MouseEvent me) { }
            @Override public void mousePressed(MouseEvent me) { graphMenu.setVisible(false); dcmDesktop.setMaxValueTo100(); }
            @Override public void mouseReleased(MouseEvent me) {  }
            @Override public void mouseEntered(MouseEvent me) {  }
            @Override public void mouseExited(MouseEvent me) { }
        };
        graphMenuItemMaxTo100.addMouseListener(graphMenuItemMaxTo100MouseListener);
        graphMenu.add(graphMenuItemMaxTo100);

        graphMenuItemMinTo0 = new JMenuItem("Minimum to 0");
        graphMenuItemMinTo0MouseListener = new MouseListener()
        {

            @Override public void mouseClicked(MouseEvent me) { }
            @Override public void mousePressed(MouseEvent me) { graphMenu.setVisible(false); dcmDesktop.setMinValueTo0(); }
            @Override public void mouseReleased(MouseEvent me) {  }
            @Override public void mouseEntered(MouseEvent me) {  }
            @Override public void mouseExited(MouseEvent me) { }
        };
        graphMenuItemMinTo0.addMouseListener(graphMenuItemMinTo0MouseListener);
        graphMenu.add(graphMenuItemMinTo0);

        graphMenuItemLineBold = new JMenuItem("Bold Line");
        graphMenuItemLineBoldMouseListener = new MouseListener()
        {

            @Override public void mouseClicked(MouseEvent me) { }
            @Override public void mousePressed(MouseEvent me)
            {
                graphMenu.setVisible(false); dcmDesktop.lineType = 2.0F;
                dcmDesktop.setCursor(Cursor.WAIT_CURSOR);
                Thread LineBoldThread = new Thread(new Runnable()
                {
                    @Override
                    @SuppressWarnings({"static-access"})
                    public void run()
                    {
                        try { dcmDesktop.setGraph(dcmDesktop.getTrend(false,dcmDesktop.getImageWidth(),dcmDesktop.getImageHeight(),dcmDesktop.manualValueMode,dcmDesktop.startCalendar,dcmDesktop.endCalendar,dcmDesktop.valueLimits, dcmDesktop.selectionArea.getText(), dcmDesktop.lineType)); } catch (CloneNotSupportedException ex) {   } // MiddleWare Invocation
                    }
                });
                LineBoldThread.setName("LineBoldThread");
                LineBoldThread.setDaemon(true);
                LineBoldThread.setPriority(Thread.NORM_PRIORITY);
                LineBoldThread.start();        
            }
            @Override public void mouseReleased(MouseEvent me) {  }
            @Override public void mouseEntered(MouseEvent me) {  }
            @Override public void mouseExited(MouseEvent me) { }
        };
        graphMenuItemLineBold.addMouseListener(graphMenuItemLineBoldMouseListener);
        graphMenu.add(graphMenuItemLineBold);

        graphMenuItemLineNormal = new JMenuItem("Normal Line");
        graphMenuItemLineNormalMouseListener = new MouseListener()
        {

            @Override public void mouseClicked(MouseEvent me) { }
            @Override public void mousePressed(MouseEvent me)
            {
                graphMenu.setVisible(false); dcmDesktop.lineType = 1.5F; 
                dcmDesktop.setCursor(Cursor.WAIT_CURSOR);
                Thread LineNormalThread = new Thread(new Runnable()
                {
                    @Override
                    @SuppressWarnings({"static-access"})
                    public void run()
                    {
//                        dcManager.setGraph(dcManager.getTrend(false,dcManager.getImageWidth(),dcManager.getImageHeight(),dcManager.manualValueMode,dcManager.startCalendar,dcManager.endCalendar,dcManager.valueLimits)); // MiddleWare Invocation
                        try { dcmDesktop.setGraph(dcmDesktop.getTrend(false,dcmDesktop.getImageWidth(),dcmDesktop.getImageHeight(),dcmDesktop.manualValueMode,dcmDesktop.startCalendar,dcmDesktop.endCalendar,dcmDesktop.valueLimits, dcmDesktop.selectionArea.getText(), dcmDesktop.lineType)); } catch (CloneNotSupportedException ex) {   } // MiddleWare Invocation
                    }
                });
                LineNormalThread.setName("LineNormalThread");
                LineNormalThread.setDaemon(true);
                LineNormalThread.setPriority(Thread.NORM_PRIORITY);
                LineNormalThread.start();        
            }
            @Override public void mouseReleased(MouseEvent me) {  }
            @Override public void mouseEntered(MouseEvent me) {  }
            @Override public void mouseExited(MouseEvent me) { }
        };
        graphMenuItemLineNormal.addMouseListener(graphMenuItemLineNormalMouseListener);
        graphMenu.add(graphMenuItemLineNormal);

        graphMenuItemLineFine = new JMenuItem("Fine Line");
        graphMenuItemLineFineMouseListener = new MouseListener()
        {

            @Override public void mouseClicked(MouseEvent me) { }
            @Override public void mousePressed(MouseEvent me)
            {
                graphMenu.setVisible(false); dcmDesktop.lineType = 1.0F;
                dcmDesktop.setCursor(Cursor.WAIT_CURSOR);
                Thread LineFineThread = new Thread(new Runnable()
                {
                    @Override
                    @SuppressWarnings({"static-access"})
                    public void run()
                    {
//                        dcManager.setGraph(dcManager.getTrend(false,dcManager.getImageWidth(),dcManager.getImageHeight(),dcManager.manualValueMode,dcManager.startCalendar,dcManager.endCalendar,dcManager.valueLimits)); // MiddleWare Invocation
                        try { dcmDesktop.setGraph(dcmDesktop.getTrend(false,dcmDesktop.getImageWidth(),dcmDesktop.getImageHeight(),dcmDesktop.manualValueMode,dcmDesktop.startCalendar,dcmDesktop.endCalendar,dcmDesktop.valueLimits, dcmDesktop.selectionArea.getText(), dcmDesktop.lineType)); } catch (CloneNotSupportedException ex) {   } // MiddleWare Invocation
                    }
                });
                LineFineThread.setName("LineFineThread");
                LineFineThread.setDaemon(true);
                LineFineThread.setPriority(Thread.NORM_PRIORITY);
                LineFineThread.start();        
            }
            @Override public void mouseReleased(MouseEvent me) {  }
            @Override public void mouseEntered(MouseEvent me) {  }
            @Override public void mouseExited(MouseEvent me) { }
        };
        graphMenuItemLineFine.addMouseListener(graphMenuItemLineFineMouseListener);
        graphMenu.add(graphMenuItemLineFine);

        graphMenuItemLineMicro = new JMenuItem("Micro Line");
        graphMenuItemLineMicroMouseListener = new MouseListener()
        {

            @Override public void mouseClicked(MouseEvent me) { }
            @Override public void mousePressed(MouseEvent me)
            {
                graphMenu.setVisible(false); dcmDesktop.lineType = 0.5F;
                dcmDesktop.setCursor(Cursor.WAIT_CURSOR);
                Thread LineMicroThread = new Thread(new Runnable()
                {
                    @Override
                    @SuppressWarnings({"static-access"})
                    public void run()
                    {
//                        dcManager.setGraph(dcManager.getTrend(false,dcManager.getImageWidth(),dcManager.getImageHeight(),dcManager.manualValueMode,dcManager.startCalendar,dcManager.endCalendar,dcManager.valueLimits)); // MiddleWare Invocation
                        try { dcmDesktop.setGraph(dcmDesktop.getTrend(false,dcmDesktop.getImageWidth(),dcmDesktop.getImageHeight(),dcmDesktop.manualValueMode,dcmDesktop.startCalendar,dcmDesktop.endCalendar,dcmDesktop.valueLimits, dcmDesktop.selectionArea.getText(), dcmDesktop.lineType)); } catch (CloneNotSupportedException ex) {   } // MiddleWare Invocation
                    }
                });
                LineMicroThread.setName("LineMicroThread");
                LineMicroThread.setDaemon(true);
                LineMicroThread.setPriority(Thread.NORM_PRIORITY);
                LineMicroThread.start();        
            }
            @Override public void mouseReleased(MouseEvent me) {  }
            @Override public void mouseEntered(MouseEvent me) {  }
            @Override public void mouseExited(MouseEvent me) { }
        };
        graphMenuItemLineMicro.addMouseListener(graphMenuItemLineMicroMouseListener);
        graphMenu.add(graphMenuItemLineMicro);

        graphMenuItemShuffleColors = new JMenuItem("Shuffle Colors");
        graphMenuItemShuffleColorsMouseListener = new MouseListener()
        {

            @Override public void mouseClicked(MouseEvent me) { }
            @Override public void mousePressed(MouseEvent me)
            {
                graphMenu.setVisible(false);
                dcmDesktop.setCursor(Cursor.WAIT_CURSOR);
                Thread ShuffleColorsThread = new Thread(new Runnable()
                {
                    @Override
                    @SuppressWarnings({"static-access"})
                    public void run()
                    {
                        try { dcmDesktop.setGraph(dcmDesktop.getTrend(true,dcmDesktop.getImageWidth(),dcmDesktop.getImageHeight(),dcmDesktop.manualValueMode,dcmDesktop.startCalendar,dcmDesktop.endCalendar,dcmDesktop.valueLimits, dcmDesktop.selectionArea.getText(), dcmDesktop.lineType)); } catch (CloneNotSupportedException ex) {   } // MiddleWare Invocation
                    }
                });
                ShuffleColorsThread.setName("ShuffleColorsThread");
                ShuffleColorsThread.setDaemon(true);
                ShuffleColorsThread.setPriority(Thread.NORM_PRIORITY);
                ShuffleColorsThread.start();        
            }
            @Override public void mouseReleased(MouseEvent me) {  }
            @Override public void mouseEntered(MouseEvent me) {  }
            @Override public void mouseExited(MouseEvent me) { }
        };
        graphMenuItemShuffleColors.addMouseListener(graphMenuItemShuffleColorsMouseListener);
        graphMenu.add(graphMenuItemShuffleColors);

        graphMenuItemSaveImage = new JMenuItem("Save Image As");
        graphMenuItemSaveImageMouseListener = new MouseListener()
        {

            @Override public void mouseClicked(MouseEvent me) { }
            @Override public void mousePressed(MouseEvent me)
            {
                graphMenu.setVisible(false);
                dcmDesktop.setCursor(Cursor.WAIT_CURSOR);
                Thread ShuffleColorsThread = new Thread(new Runnable()
                {
                    @Override
                    @SuppressWarnings({"static-access"})
                    public void run()
                    {
                        try { dcmDesktop.saveGraph(dcmDesktop.getTrend(false,dcmDesktop.getImageWidth(),dcmDesktop.getImageHeight(),dcmDesktop.manualValueMode,dcmDesktop.startCalendar,dcmDesktop.endCalendar,dcmDesktop.valueLimits, dcmDesktop.selectionArea.getText(), dcmDesktop.lineType)); } catch (CloneNotSupportedException ex) {   } // MiddleWare Invocation
                    }
                });
                ShuffleColorsThread.setName("ShuffleColorsThread");
                ShuffleColorsThread.setDaemon(true);
                ShuffleColorsThread.setPriority(Thread.NORM_PRIORITY);
                ShuffleColorsThread.start();        
            }
            @Override public void mouseReleased(MouseEvent me) {  }
            @Override public void mouseEntered(MouseEvent me) {  }
            @Override public void mouseExited(MouseEvent me) { }
        };
        graphMenuItemSaveImage.addMouseListener(graphMenuItemSaveImageMouseListener);
        graphMenu.add(graphMenuItemSaveImage);
    }
    
    protected void showServerMenu(MouseEvent evt) { serverMenu.show(evt.getComponent(), evt.getX(), evt.getY()); }
    protected void showServersMenu(MouseEvent evt) { datacenterMenu.show(evt.getComponent(), evt.getX(), evt.getY()); }
    protected void showGraphMenu(MouseEvent evtParam) { evt = evtParam; graphMenu.show(evt.getComponent(), evtParam.getX(), evtParam.getY()); }
     
}
