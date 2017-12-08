import data.Configuration;
import data.ValueLimits;
import data.ConfigurationCaller;
import data.DCMPreset;
import data.MD5Converter;
import data.Resource;
import data.Server;
import data.Host;
import data.DCMUser;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.tree.*;
import net.java.balloontip.BalloonTip;
import net.java.balloontip.utils.FadingUtils;
import org.jrobin.core.FetchData;
import org.jrobin.core.FetchRequest;
import org.jrobin.core.RrdDb;
import org.jrobin.core.RrdException;
import org.jrobin.graph.RrdGraph;
import org.jrobin.graph.RrdGraphDef;

public class DCMDesktop extends javax.swing.JFrame implements DCMInventoryServerCaller, DCMDBClientCaller, ConfigurationCaller, DCMPollServerCaller, DCMPushPollScriptCaller
{
    private File firsttimepassedFile;
    private String firsttimepassedFileString;
    private String country;
    private String language;
    private Locale currentLocale;
//  STDIN & STDOUT Redirection
    public class FilteredStream extends FilterOutputStream
    {
        public FilteredStream(OutputStream aStream) { super(aStream); }
        @Override public void write(byte b[]) throws IOException {}
        @Override public void write(byte b[], int off, int len) throws IOException { }
    }
//  End Redirection

    String host;
    String user;
    String userPassword;
    String superuserPassword = null;
    String serversListArray;

    Server inventoryServer;
    Server pollerServer;
    String command;
    String tmpOutput;
    String output;
    String data;
        
    int port = 22;
    int retentionTime = 1;
    int sessionTimeout = 5;
    int retryMax = 2;

    int serverInstance = 0;
    int serverInstanceCounter = 0;

//    Server server;
    DCMInventoryServer dcmInventoryServer;
    DCMPollServer dcmPollServer;
    DCMInventoryServerCaller inventoryCaller;
    ConfigurationCaller configurationCaller;
    private final Configuration configuration;
    private DCMDBClient dcmDBClient;
    private Thread dcmDBClientThread;
    private boolean dcmDBClientIsReady = false;
    private boolean trendIsViewable = true;
    private DCMDesktop dcmDesktopReference;
    private ArrayList<Server> serverList;
//    private ResourcesTableCellRenderer resourcesTableCellRenderer;
    private static final String VERGUNNINGTOEKENNER  = "IsNwtNp4L";
    private static final String TIJDRESETTER         = "IsNwtNp4T";
    public static final long    SECOND               = 1000;
    public static final long    MINUTE               = SECOND * 60;
    public static final long    HOUR                 = MINUTE * 60;
    public static final long    DAY                  = HOUR   * 24;
    public static final long    MONTH                = DAY    * 31;
    private boolean             runThreadsAsDaemons  = true;
    private TableColumn resourcesTableColumn;
    protected DCMAddServerForm addServerForm;
    protected DCMAddDCMUserForm addDCMUserForm;
    protected DCMDeleteUserForm deleteDCMUserForm;
    protected DCMDeleteServerForm deleteServerForm;
    protected DCMDeleteServersForm deleteServersForm;
    protected DCMLoginForm dcmLoginForm;
    private DCMShell shell;
    protected Calendar startCalendar;
    protected Calendar endCalendar;
    private Long    navigationXFactor = 6L;
    private Double  navigationYFactor = 4D;
    protected boolean manualValueMode = false;
    protected ValueLimits valueLimits;

    private DCMVergunning       dcmVergunning;
    private Calendar            vergunningStartCalendar;
    private Calendar            vergunningEndCalendar;
    protected float lineType = 1.5F;
    private  DCMPopupMenus popupMenus;
    protected int imageWidth;
    protected int imageHeight;
    private DCMTableCellListener hostTableCellListener;
    private Action hostTableCellAction;
    private boolean hostnameUpdated = false;
    private DCMTableCellListener resourcesTableCellListener;
    private Action resourcesTableCellAction;
    private DCMTableCellListener userTableCellListener;
    private Action userTableCellAction;
    private ArrayList<Color> lineColorArray;
    private final int viewEdgeX;
    private final int imageEdgeX;
    private final int imageEdgeY;
    private final int viewEdgeY;
    private ImageIcon imageIcon; // Leave for MiddleWare
    protected boolean loggedIn = false;
    private DCMUser dcmUser;
    private DCMSysMonitor sysmon;
    private java.util.Timer     jvmDynamicPropertiesTimer;
    private long                jvmDynamicPropertiesTimerInterval = 1000; // mS

    private String              logDateString;
    private Calendar            currentTimeCalendar;
    private static final String THISPRODUCT = "DCMDesktop";
    private FileWriter          logFileWriter;
    private String              logFileString = "logFileString_made_a_booboo.checkme";
    private String              logBuffer                                       = "";
    
    private TimerTask blinkStartNowButtonAction;
    private java.util.Timer blinkStartNowButtonTimer;
    
    private TimerTask blinkEndNowButtonAction;
    private java.util.Timer blinkEndNowButtonTimer;
    
    private TimerTask blinkGraphButtonAction;
    private java.util.Timer blinkGraphButtonActionTimer;

    private TimerTask generateGraphAction;
    private java.util.Timer generateGraphActionTimer;
    
    private TimerTask secureViewAction;
    private java.util.Timer secureViewActionTimer;
    
    private TimerTask secureAction;
    private java.util.Timer secureActionTimer;
    
    private ImageIcon graphIconOn;
    private ImageIcon graphIconOff;
    DefaultMutableTreeNode rootNode;
    DefaultMutableTreeNode serverNode;
//    private javax.swing.JScrollPane logScroller3;

//    private ImageIcon aixIcon;
//    private ImageIcon sunIcon;
//    private ImageIcon hpuxIcon;
//    private ImageIcon osxIcon;
//    private ImageIcon linuxIcon;
    private ImageIcon serverIcon;
    private ImageIcon datacenterIcon;
        
    private boolean graphButtonOn;
    private String licenseInstruction = "Server Limit Reached! Click [Admin] Tab to increase number of Servers.";

    private boolean daemon = false;
    private boolean debug = false;
//    private StyleContext StyleContext;
//    private AttributeSet attributeSet;
    private DCMColorPane colorPane;
    private String platform;
    private boolean dropOnSearchField = true;
    private boolean searchExactWasUsed;
    private boolean animated;
    long selectedResources = 0;

/** Creates new form InventoryGUI
     * @param daemonParam
     * @param debugParam
     * @throws javax.swing.UnsupportedLookAndFeelException */
    public DCMDesktop(boolean daemonParam, boolean debugParam) throws UnsupportedLookAndFeelException // FrontEnd
    {
        daemon = daemonParam;
        debug = debugParam;
        platform = System.getProperty("os.name").toLowerCase();
        if (platform.contains("mac os"))
        {
            viewEdgeX = 30;  viewEdgeY = 45;            
            imageEdgeX = 90; imageEdgeY = 30;
        }
        else
        {
            viewEdgeX = 10;  viewEdgeY = 45;                        
            imageEdgeX = 90; imageEdgeY = 30; // imageEdgeX Larger means wider  black gap
        }

        //      STDIN & STDOUT Redirection
//        if ((!Configuration.DEBUG) && (!debug))
        if (!debug)
        {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            FilteredStream filteredStream = new FilteredStream(byteArrayOutputStream);
            PrintStream aPrintStream  = new PrintStream(filteredStream);
            System.setOut(aPrintStream); System.setErr(aPrintStream);
        } else { System.out.println(THISPRODUCT + " debugging enabled..."); }
                
//        setMinimumSize(new Dimension(1085,680));
        
        initComponents();
        
        setSize(new Dimension(Toolkit.getDefaultToolkit().getScreenSize().width,Toolkit.getDefaultToolkit().getScreenSize().height));
        setPreferredSize(new Dimension(Toolkit.getDefaultToolkit().getScreenSize().width,Toolkit.getDefaultToolkit().getScreenSize().height));

        // Make sure you keep this colorPane before the new UIManager.setLookAndFeel
        colorPane = new DCMColorPane();
        colorPane.setBackground(new java.awt.Color(0,0,0)); // 4th zero sets alpha transparancy , but smudges screen (setOpaque(true) doesn't work with StyleContext)
        colorPane.setContentType("text/html");
        colorPane.setFont(new java.awt.Font("Courier New", 1, 12)); // NOI18N
        colorPane.setOpaque(true);
        colorPane.setForeground(new java.awt.Color(255, 255, 255));        
        logScroller.setViewportView(colorPane);

//        colorArray = Tools.getColorArray();
        
        try
        { UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel"); }
        catch (ClassNotFoundException ex)           { System.out.println(THISPRODUCT + " ClassNotFoundException: " + ex.getMessage()); }
        catch (InstantiationException ex)           { System.out.println(THISPRODUCT + " InstantiationException: " + ex.getMessage()); }
        catch (IllegalAccessException ex)           { System.out.println(THISPRODUCT + " IllegalAccessException: " + ex.getMessage()); }
        catch (UnsupportedLookAndFeelException ex)  { System.out.println(THISPRODUCT + " UnsupportedLookAndFeelException: " + ex.getMessage()); }
        
        
//      setVisible(false); setVisible(true);
//        logScroller3 = new javax.swing.JScrollPane();
        
        treeButtonBar.setLayout(new FlowLayout(FlowLayout.CENTER));
        navigateXButtonBar.setLayout(new FlowLayout(FlowLayout.CENTER));

        updateSearchStatsTable("0","0","0","0","0","0");
                
        dcmDesktopReference = this;
        configurationCaller = this;
        configuration = new Configuration(configurationCaller);
        
        
        graphIconOn = new ImageIcon(getClass().getResource("images/graph-on.png"));
        graphIconOff = new ImageIcon(getClass().getResource("images/graph-off.png"));

        serverIcon = new ImageIcon(getClass().getResource("images/server-icon.png"));
        datacenterIcon = new ImageIcon(getClass().getResource("images/datacenter.png"));

        graphButtonOn = true;
        
        // Just for the logToFile
        currentTimeCalendar = Calendar.getInstance();
        logDateString = "" +
        String.format("%04d", currentTimeCalendar.get(Calendar.YEAR)) +
        String.format("%02d", currentTimeCalendar.get(Calendar.MONTH) + 1) +
        String.format("%02d", currentTimeCalendar.get(Calendar.DAY_OF_MONTH)) + "_" +
        String.format("%02d", currentTimeCalendar.get(Calendar.HOUR_OF_DAY)) +
        String.format("%02d", currentTimeCalendar.get(Calendar.MINUTE)) +
        String.format("%02d", currentTimeCalendar.get(Calendar.SECOND));
        logFileString = configuration.getLogDir() + logDateString + "_" + THISPRODUCT + ".log";
        
        lineColorArray = new ArrayList<Color>();

        // Begin MiddleWare        
        try { dcmDBClientThread = new DCMDBClient(dcmDesktopReference, configuration.getJavaDB(), daemon, debug); }
        catch (SQLException ex)                 { inventoryCaller.log("InventoryGUI: SQLException", true, true, true); }
        catch (ClassNotFoundException ex)       { inventoryCaller.log("InventoryGUI: ClassNotFoundException: " + ex.getMessage(), true, true, true); }
        catch (InstantiationException ex)       { inventoryCaller.log("InventoryGUI: InstantiationException: " + ex.getMessage(), true, true, true); }
        catch (IllegalAccessException ex)       { inventoryCaller.log("InventoryGUI: IllegalAccessException: " + ex.getMessage(), true, true, true); }
        catch (NoSuchMethodException ex)        { inventoryCaller.log("InventoryGUI: NoSuchMethodException: " + ex.getMessage(), true, true, true); }
        catch (InvocationTargetException ex)    { inventoryCaller.log("InventoryGUI: InvocationTargetException: " + ex.getMessage(), true, true, true); }
        catch (Exception ex)                    { inventoryCaller.log("InventoryGUI: Exception: " + ex.getMessage(), true, true, true); }

        dcmDBClientThread.setName("dcmMetaDBClientThread");
        dcmDBClientThread.setDaemon(false);
        dcmDBClientThread.setPriority(Thread.NORM_PRIORITY);
        dcmDBClientThread.start();
        
        dcmDBClient = (DCMDBClient) dcmDBClientThread;

        if ( System.getenv().get("DISPLAY") == null )
        {
            animated = true;
            log("Info:    DCMDesktop: Animations Enabled",false,true,true);            
        }
        else
        {
            if (( System.getenv().get("DISPLAY") == null ) || ( System.getenv().get("DISPLAY").isEmpty()) || (System.getenv().get("DISPLAY").startsWith("/")) || (System.getenv().get("DISPLAY").startsWith(":")) )
            {
                animated = true;
                log("Info:    DCMDesktop: Animations Enabled: DISPLAY=" + System.getenv().get("DISPLAY"),false,true,true);
            }
            else
            {
                animated = false;
                log("Info:    DCMDesktop: Animations Disabled: DISPLAY=" + System.getenv().get("DISPLAY"),false,true,true);
            }            
        }
        
        while (!dcmDBClientIsReady) { try { Thread.sleep(100); } catch (InterruptedException ex) {  }}
        // End MiddleWare        

        // Begin MiddleWare        
        shell = new DCMShell(debug); // For starting the poller
        // End MiddleWare        
                
        startCalendar = Calendar.getInstance();
        startCalendar.setTimeInMillis(Calendar.getInstance().getTimeInMillis()-((DAY+HOUR)));        
        endCalendar = Calendar.getInstance();

        deleteServerForm = new DCMDeleteServerForm(this);
        deleteServersForm = new DCMDeleteServersForm(this);
        addDCMUserForm = new DCMAddDCMUserForm(this);
        deleteDCMUserForm = new DCMDeleteUserForm(this);

        popupMenus = new DCMPopupMenus(this);
                
        valueLimits = new ValueLimits();
        
        serverTree.setEnabled(false);
        
        vergunningStartCalendar = Calendar.getInstance();
        vergunningEndCalendar   = Calendar.getInstance();
        vergunningStartCalendar.set(Calendar.HOUR_OF_DAY, (int)0);
        vergunningStartCalendar.set(Calendar.MINUTE, (int)0);
        vergunningStartCalendar.set(Calendar.SECOND, (int)0);

        setTitle(DCMLicense.getProcuct() + " " + DCMLicense.getVersion());
        staticLabel.setText(DCMLicense.getCopyright() + " Author: " +DCMLicense.getAuthor());
        
        try { addServerForm = new DCMAddServerForm(this); } catch (UnsupportedLookAndFeelException ex) { }
        
        sysmon = new DCMSysMonitor();
//        vergunning = new DCMVergunning();
        dcmLoginForm = new DCMLoginForm(this);
//        if (!loggedIn) { setState(JFrame.ICONIFIED); dcmLoginForm.setVisible(true); }        
        if (!loggedIn) { setVisible(false); dcmLoginForm.setVisible(true); }
    }

    protected void continueAfterLogin(String usernameParam, String passwordParam)
    {
        country = System.getProperty("user.country");
        language = System.getProperty("user.language");
        currentLocale = new Locale(language, country);
        startDateChooserPanel.setLocale(currentLocale);
        endDateChooserPanel.setLocale(currentLocale);
        vergunningDateChooserPanel.setLocale(currentLocale);
        
        if (!dcmDBClient.authenticateDCMUser(usernameParam, passwordParam))
        {
            dcmLoginForm.status("Login as user: " + usernameParam + " failed"); dcmLoginForm.setVisible(true);
        }
        else
        {
            searchField.addActionListener(hostTableCellAction);
            setVisible(true);
            dcmLoginForm.setVisible(false);
            dcmUser = dcmDBClient.getDCMUserByUsername(usernameParam);
            dcmVergunning = new DCMVergunning(dcmDesktopReference,debug); vergunningDoorvoeren(); // Middleware

            // Make sure it keeps happening
            secureAction = new TimerTask() { @Override public void run() { vergunningDoorvoeren(); } };
            secureActionTimer = new java.util.Timer();
            secureActionTimer.schedule(secureAction, 3600000L, 3600000L);
            // End MiddleWare        

            loggedIn = true;

            try { buildServerTree(); } catch (CloneNotSupportedException ex) {} // might fail
            try { fillinUserTable(); } catch (CloneNotSupportedException ex) {}
    
            updateSearchStatsTable("0/"+dcmDBClient.getResourceCount(),"0","0","0","0","0");
            
            controleerVergunningLimiet();
            
            Thread splitterThread = new Thread(new Runnable()
            {
                private ActionListener finishedAction;
                
                @Override
                public void run()
                {
    //                try { Thread.sleep(3000); } catch (InterruptedException ex) {  }
                    updateTimeWidgets(startCalendar,endCalendar);

                    showJVMStaticPropertiesList();
                    jvmDynamicPropertiesTimer = new java.util.Timer(); jvmDynamicPropertiesTimer.scheduleAtFixedRate(new DCMUpdateSysPropsTimer(dcmDesktopReference),     (long)(0), jvmDynamicPropertiesTimerInterval);
                    log("Status:  DCMDesktop: SysPropsTimer Scheduled immediate at " + Math.round(jvmDynamicPropertiesTimerInterval / 1000) + " Sec Interval", true,true,true);
                    
                    startHourField.setText("-24");
                    enableStartTimeRelativeWidgets(true);
                    enableEndTimeRelativeWidgets(true);                    

                    // This is to send runtime environment info to gmail only for free license and only the first time
//                    firsttimepassedFileString = ".dcmfirsttimepassed"; firsttimepassedFile = new File(firsttimepassedFileString);
//                    if ( (!firsttimepassedFile.exists()) && (!dcmVergunning.isValid())) { new DCMSendMessage().setVisible(true); }

                    try { Thread.sleep(1000); } catch (InterruptedException ex) {  }
                    viewTrendPane(false);
                    try { if ( getServerList().isEmpty() ) { firstTimeBalloonTip(); } } catch (CloneNotSupportedException ex) {  }

                    if (dcmUser.getAdministrator()) { log("Success: DCMDesktop: Welcome " + dcmUser.getUsername() + ", you have Administrator privileges", true, true, true); } else { log("Welcome " + dcmUser.getUsername() + ", you have User privileges", true, true, true); } 
                }
            });
            splitterThread.setName("splitterThread");
            splitterThread.setDaemon(false);
            splitterThread.setPriority(Thread.NORM_PRIORITY);
            splitterThread.start();            
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {
        java.awt.GridBagConstraints gridBagConstraints;

        managerTab = new javax.swing.JTabbedPane();
        managerPanel = new javax.swing.JPanel();
        navigatorPanel = new javax.swing.JPanel();
        startDateSelectorPanel = new javax.swing.JPanel();
        startDateChooserPanel = new datechooser.beans.DateChooserPanel();
        startTimeSelectorPanel = new javax.swing.JPanel();
        startTimeField = new javax.swing.JTextField();
        startMinuteSlider = new javax.swing.JSlider();
        startHourSlider = new javax.swing.JSlider();
        endTimeSelectorPanel = new javax.swing.JPanel();
        endTimeField = new javax.swing.JTextField();
        endHourSlider = new javax.swing.JSlider();
        endMinuteSlider = new javax.swing.JSlider();
        endDateSelectorPanel = new javax.swing.JPanel();
        endDateChooserPanel = new datechooser.beans.DateChooserPanel();
        searchPanel = new javax.swing.JPanel();
        deletePresetButton = new javax.swing.JButton();
        selectPresetBox = new javax.swing.JComboBox();
        savePresetButton = new javax.swing.JButton();
        searchField = new javax.swing.JTextField();
        wordSearchButton = new javax.swing.JButton();
        matchSearchButton = new javax.swing.JButton();
        selectionScroller = new javax.swing.JScrollPane();
        selectionArea = new javax.swing.JTextArea();
        searchStatsScroller = new javax.swing.JScrollPane();
        searchStatsTable = new javax.swing.JTable();
        navigateXButtonBar = new javax.swing.JToolBar();
        leftButton = new javax.swing.JButton();
        zoomInXButton = new javax.swing.JButton();
        zoomOutXButton = new javax.swing.JButton();
        rightButton = new javax.swing.JButton();
        swapViewButton = new javax.swing.JButton();
        graphButton = new javax.swing.JButton();
        upButton = new javax.swing.JButton();
        zoomInYButton = new javax.swing.JButton();
        zoomOutYButton = new javax.swing.JButton();
        downButton = new javax.swing.JButton();
        loginButton = new javax.swing.JButton();
        startTimeRelativePanel = new javax.swing.JPanel();
        startTimeIsRelativeButton = new javax.swing.JToggleButton();
        startMonthLabel = new javax.swing.JLabel();
        startMonthPlusButton = new javax.swing.JButton();
        startDayPlusButton = new javax.swing.JButton();
        startMonthField = new javax.swing.JTextField();
        startDayField = new javax.swing.JTextField();
        startMonthMinusButton = new javax.swing.JButton();
        startDayMinusButton = new javax.swing.JButton();
        startHourLabel = new javax.swing.JLabel();
        startHourPlusButton = new javax.swing.JButton();
        startMinutePlusButton = new javax.swing.JButton();
        startHourField = new javax.swing.JTextField();
        startMinuteField = new javax.swing.JTextField();
        startHourMinusButton = new javax.swing.JButton();
        startMinuteMinusButton = new javax.swing.JButton();
        startDayLabel = new javax.swing.JLabel();
        startMinuteLabel = new javax.swing.JLabel();
        endTimeRelativePanel = new javax.swing.JPanel();
        endTimeIsRelativeButton = new javax.swing.JToggleButton();
        endMonthLabel = new javax.swing.JLabel();
        endMonthPlusButton = new javax.swing.JButton();
        endDayPlusButton = new javax.swing.JButton();
        endMonthField = new javax.swing.JTextField();
        endDayField = new javax.swing.JTextField();
        endMonthMinusButton = new javax.swing.JButton();
        endDayMinusButton = new javax.swing.JButton();
        endHourLabel = new javax.swing.JLabel();
        endHourPlusButton = new javax.swing.JButton();
        endMinutePlusButton = new javax.swing.JButton();
        endHourField = new javax.swing.JTextField();
        endMinuteField = new javax.swing.JTextField();
        endHourMinusButton = new javax.swing.JButton();
        endMinuteMinusButton = new javax.swing.JButton();
        endMinuteLabel = new javax.swing.JLabel();
        endDayLabel = new javax.swing.JLabel();
        viewSplitter = new javax.swing.JSplitPane();
        treeInventorySplitter = new javax.swing.JSplitPane();
        treeSplitter = new javax.swing.JSplitPane();
        treeButtonBar = new javax.swing.JToolBar();
        addServerButton = new javax.swing.JButton();
        deleteServerButton = new javax.swing.JButton();
        startPollerButton = new javax.swing.JButton();
        startCommanderButton = new javax.swing.JButton();
        serverTreeScoller = new javax.swing.JScrollPane();
        serverTree = new javax.swing.JTree();
        tableSplitter = new javax.swing.JSplitPane();
        hostScroller = new javax.swing.JScrollPane();
        hostTable = new javax.swing.JTable();
        resourcesScroller = new javax.swing.JScrollPane();
        resourcesTable = new javax.swing.JTable();
        graphLabel = new javax.swing.JLabel();
        adminPanel = new javax.swing.JPanel();
        licenseInnerPanel = new javax.swing.JPanel();
        applyVergunningButton = new javax.swing.JButton();
        licenseDatePanel = new javax.swing.JPanel();
        vergunningDateChooserPanel = new datechooser.beans.DateChooserPanel();
        licenseCodePanel = new javax.swing.JPanel();
        vergunningCodeField = new javax.swing.JTextField();
        licenseTypePanel = new javax.swing.JPanel();
        serversInLicenseField = new javax.swing.JTextField();
        plusButton = new javax.swing.JButton();
        minusButton = new javax.swing.JButton();
        licenseDetailsPanel = new javax.swing.JPanel();
        licenseDetailsScrollPane = new javax.swing.JScrollPane();
        vergunningDetailsTable = new javax.swing.JTable();
        orderLicenseButton = new javax.swing.JButton();
        licensePeriodPanel = new javax.swing.JPanel();
        licensePeriodScrollPane = new javax.swing.JScrollPane();
        vergunningPeriodList = new javax.swing.JList();
        activationCodePanel = new javax.swing.JPanel();
        activationCodeField = new javax.swing.JTextField();
        systemPropertiesPanel = new javax.swing.JPanel();
        sysPropsScroller = new javax.swing.JScrollPane();
        sysPropsTable = new javax.swing.JTable();
        userPanel = new javax.swing.JPanel();
        userScroller = new javax.swing.JScrollPane();
        userTable = new javax.swing.JTable();
        dcmUserButtonBar = new javax.swing.JToolBar();
        addDCMUserButton = new javax.swing.JButton();
        deleteDCMUserButton = new javax.swing.JButton();
        backupPanel = new javax.swing.JPanel();
        exportButton = new javax.swing.JButton();
        importButton = new javax.swing.JButton();
        helpdeskPanel = new javax.swing.JPanel();
        dcmHelpdeskButtonBar = new javax.swing.JToolBar();
        sendEmailButton = new javax.swing.JButton();
        clearHelpdeskButton = new javax.swing.JButton();
        subjectField = new javax.swing.JTextField();
        messageScroller = new javax.swing.JScrollPane();
        messagePane = new javax.swing.JEditorPane();
        logPanel = new javax.swing.JPanel();
        logScroller = new javax.swing.JScrollPane();
        logPane = new javax.swing.JTextPane();
        statusPanel = new javax.swing.JPanel();
        statusLabel = new javax.swing.JLabel();
        staticLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("DCMDesktop"); // NOI18N
        setTitle(bundle.getString("DCMDesktop.title")); // NOI18N
        setFocusable(false);
        setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        setMinimumSize(new java.awt.Dimension(1155, 760));
        setName("mainFrame"); // NOI18N
        addComponentListener(new java.awt.event.ComponentAdapter()
        {
            public void componentResized(java.awt.event.ComponentEvent evt)
            {
                formComponentResized(evt);
            }
        });

        managerTab.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        managerTab.setFocusable(false);
        managerTab.setPreferredSize(new java.awt.Dimension(1024, 768));
        managerTab.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                managerTabMouseClicked(evt);
            }
        });

        managerPanel.setFont(managerPanel.getFont());
        managerPanel.setPreferredSize(new java.awt.Dimension(1024, 742));

        navigatorPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, bundle.getString("DCMDesktop.navigatorPanel.border.title"), javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Arial", 0, 14), new java.awt.Color(102, 102, 102))); // NOI18N
        navigatorPanel.setFont(navigatorPanel.getFont());

        startDateSelectorPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), bundle.getString("DCMDesktop.startDateSelectorPanel.border.title"), javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Lucida Grande", 0, 10), new java.awt.Color(102, 102, 102))); // NOI18N
        startDateSelectorPanel.setFont(startDateSelectorPanel.getFont());
        startDateSelectorPanel.setOpaque(false);
        startDateSelectorPanel.setPreferredSize(new java.awt.Dimension(210, 210));

        startDateChooserPanel.setCurrentView(new datechooser.view.appearance.AppearancesList("Light",
            new datechooser.view.appearance.ViewAppearance("custom",
                new datechooser.view.appearance.swing.SwingCellAppearance(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 12),
                    new java.awt.Color(77, 77, 77),
                    new java.awt.Color(0, 0, 255),
                    false,
                    true,
                    new datechooser.view.appearance.swing.ButtonPainter()),
                new datechooser.view.appearance.swing.SwingCellAppearance(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 12),
                    new java.awt.Color(77, 77, 77),
                    new java.awt.Color(0, 0, 255),
                    true,
                    true,
                    new datechooser.view.appearance.swing.ButtonPainter()),
                new datechooser.view.appearance.swing.SwingCellAppearance(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 12),
                    new java.awt.Color(0, 0, 255),
                    new java.awt.Color(0, 0, 255),
                    false,
                    true,
                    new datechooser.view.appearance.swing.ButtonPainter()),
                new datechooser.view.appearance.swing.SwingCellAppearance(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 12),
                    new java.awt.Color(128, 128, 128),
                    new java.awt.Color(0, 0, 255),
                    false,
                    true,
                    new datechooser.view.appearance.swing.LabelPainter()),
                new datechooser.view.appearance.swing.SwingCellAppearance(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 12),
                    new java.awt.Color(77, 77, 77),
                    new java.awt.Color(0, 0, 255),
                    false,
                    true,
                    new datechooser.view.appearance.swing.LabelPainter()),
                new datechooser.view.appearance.swing.SwingCellAppearance(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 12),
                    new java.awt.Color(77, 77, 77),
                    new java.awt.Color(255, 0, 0),
                    false,
                    false,
                    new datechooser.view.appearance.swing.ButtonPainter()),
                (datechooser.view.BackRenderer)null,
                false,
                true)));
    startDateChooserPanel.setEnabled(false);
    startDateChooserPanel.setLocale(new java.util.Locale("en", "", ""));
    startDateChooserPanel.setNavigateFont(new java.awt.Font("Verdana", java.awt.Font.PLAIN, 7));
    startDateChooserPanel.setBehavior(datechooser.model.multiple.MultyModelBehavior.SELECT_SINGLE);
    startDateChooserPanel.addSelectionChangedListener(new datechooser.events.SelectionChangedListener()
    {
        public void onSelectionChange(datechooser.events.SelectionChangedEvent evt)
        {
            startDateChooserPanelOnSelectionChange(evt);
        }
    });

    org.jdesktop.layout.GroupLayout startDateSelectorPanelLayout = new org.jdesktop.layout.GroupLayout(startDateSelectorPanel);
    startDateSelectorPanel.setLayout(startDateSelectorPanelLayout);
    startDateSelectorPanelLayout.setHorizontalGroup(
        startDateSelectorPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(startDateChooserPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 198, Short.MAX_VALUE)
    );
    startDateSelectorPanelLayout.setVerticalGroup(
        startDateSelectorPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(org.jdesktop.layout.GroupLayout.TRAILING, startDateChooserPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 199, Short.MAX_VALUE)
    );

    startTimeSelectorPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), bundle.getString("DCMDesktop.startTimeSelectorPanel.border.title"), javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Lucida Grande", 0, 10), new java.awt.Color(102, 102, 102))); // NOI18N
    startTimeSelectorPanel.setFont(startTimeSelectorPanel.getFont());
    startTimeSelectorPanel.setOpaque(false);
    startTimeSelectorPanel.setPreferredSize(new java.awt.Dimension(94, 200));
    java.awt.GridBagLayout startTimeSelectorPanelLayout = new java.awt.GridBagLayout();
    startTimeSelectorPanelLayout.columnWidths = new int[] {0, 5, 0};
    startTimeSelectorPanelLayout.rowHeights = new int[] {0, 7, 0};
    startTimeSelectorPanel.setLayout(startTimeSelectorPanelLayout);

    startTimeField.setBackground(new java.awt.Color(204, 204, 204));
    startTimeField.setFont(new java.awt.Font("Synchro LET", 0, 24)); // NOI18N
    startTimeField.setForeground(new java.awt.Color(153, 153, 153));
    startTimeField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
    startTimeField.setText(bundle.getString("DCMDesktop.startTimeField.text")); // NOI18N
    startTimeField.setFocusable(false);
    startTimeField.setHorizontalAlignment(SwingConstants.CENTER);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.gridwidth = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 0.2;
    startTimeSelectorPanel.add(startTimeField, gridBagConstraints);

    startMinuteSlider.setFont(startMinuteSlider.getFont().deriveFont(startMinuteSlider.getFont().getSize()-6f));
    startMinuteSlider.setMajorTickSpacing(5);
    startMinuteSlider.setMaximum(59);
    startMinuteSlider.setMinorTickSpacing(1);
    startMinuteSlider.setOrientation(javax.swing.JSlider.VERTICAL);
    startMinuteSlider.setPaintLabels(true);
    startMinuteSlider.setPaintTicks(true);
    startMinuteSlider.setSnapToTicks(true);
    startMinuteSlider.setValue(30);
    startMinuteSlider.addChangeListener(new javax.swing.event.ChangeListener()
    {
        public void stateChanged(javax.swing.event.ChangeEvent evt)
        {
            startMinuteSliderStateChanged(evt);
        }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 0.1;
    gridBagConstraints.weighty = 0.3;
    startTimeSelectorPanel.add(startMinuteSlider, gridBagConstraints);

    startHourSlider.setFont(startHourSlider.getFont().deriveFont(startHourSlider.getFont().getSize()-6f));
    startHourSlider.setMajorTickSpacing(1);
    startHourSlider.setMaximum(23);
    startHourSlider.setMinorTickSpacing(1);
    startHourSlider.setOrientation(javax.swing.JSlider.VERTICAL);
    startHourSlider.setPaintLabels(true);
    startHourSlider.setPaintTicks(true);
    startHourSlider.setSnapToTicks(true);
    startHourSlider.setValue(12);
    startHourSlider.addChangeListener(new javax.swing.event.ChangeListener()
    {
        public void stateChanged(javax.swing.event.ChangeEvent evt)
        {
            startHourSliderStateChanged(evt);
        }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 0.1;
    gridBagConstraints.weighty = 0.3;
    startTimeSelectorPanel.add(startHourSlider, gridBagConstraints);

    endTimeSelectorPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), bundle.getString("DCMDesktop.endTimeSelectorPanel.border.title"), javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Lucida Grande", 0, 10), new java.awt.Color(102, 102, 102))); // NOI18N
    endTimeSelectorPanel.setFont(endTimeSelectorPanel.getFont());
    endTimeSelectorPanel.setOpaque(false);
    endTimeSelectorPanel.setPreferredSize(new java.awt.Dimension(150, 170));
    java.awt.GridBagLayout endTimeSelectorPanelLayout = new java.awt.GridBagLayout();
    endTimeSelectorPanelLayout.columnWidths = new int[] {0, 5, 0};
    endTimeSelectorPanelLayout.rowHeights = new int[] {0, 7, 0};
    endTimeSelectorPanel.setLayout(endTimeSelectorPanelLayout);

    endTimeField.setBackground(new java.awt.Color(204, 204, 204));
    endTimeField.setFont(new java.awt.Font("Synchro LET", 0, 24)); // NOI18N
    endTimeField.setForeground(new java.awt.Color(153, 153, 153));
    endTimeField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
    endTimeField.setText(bundle.getString("DCMDesktop.endTimeField.text")); // NOI18N
    endTimeField.setFocusable(false);
    endTimeField.setMaximumSize(new java.awt.Dimension(82, 2147483647));
    startTimeField.setHorizontalAlignment(SwingConstants.CENTER);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.gridwidth = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 0.2;
    endTimeSelectorPanel.add(endTimeField, gridBagConstraints);

    endHourSlider.setFont(endHourSlider.getFont().deriveFont(endHourSlider.getFont().getSize()-6f));
    endHourSlider.setMajorTickSpacing(1);
    endHourSlider.setMaximum(23);
    endHourSlider.setMinorTickSpacing(1);
    endHourSlider.setOrientation(javax.swing.JSlider.VERTICAL);
    endHourSlider.setPaintLabels(true);
    endHourSlider.setPaintTicks(true);
    endHourSlider.setSnapToTicks(true);
    endHourSlider.setValue(12);
    endHourSlider.setPreferredSize(new java.awt.Dimension(29, 42));
    endHourSlider.addChangeListener(new javax.swing.event.ChangeListener()
    {
        public void stateChanged(javax.swing.event.ChangeEvent evt)
        {
            endHourSliderStateChanged(evt);
        }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 0.1;
    gridBagConstraints.weighty = 0.3;
    endTimeSelectorPanel.add(endHourSlider, gridBagConstraints);

    endMinuteSlider.setFont(endMinuteSlider.getFont().deriveFont(endMinuteSlider.getFont().getSize()-6f));
    endMinuteSlider.setMajorTickSpacing(5);
    endMinuteSlider.setMaximum(59);
    endMinuteSlider.setMinorTickSpacing(1);
    endMinuteSlider.setOrientation(javax.swing.JSlider.VERTICAL);
    endMinuteSlider.setPaintLabels(true);
    endMinuteSlider.setPaintTicks(true);
    endMinuteSlider.setSnapToTicks(true);
    endMinuteSlider.setValue(30);
    endMinuteSlider.setPreferredSize(new java.awt.Dimension(44, 124));
    endMinuteSlider.addChangeListener(new javax.swing.event.ChangeListener()
    {
        public void stateChanged(javax.swing.event.ChangeEvent evt)
        {
            endMinuteSliderStateChanged(evt);
        }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 0.1;
    gridBagConstraints.weighty = 0.3;
    endTimeSelectorPanel.add(endMinuteSlider, gridBagConstraints);

    endDateSelectorPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), bundle.getString("DCMDesktop.endDateSelectorPanel.border.title"), javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Lucida Grande", 0, 10), new java.awt.Color(102, 102, 102))); // NOI18N
    endDateSelectorPanel.setForeground(new java.awt.Color(51, 51, 51));
    endDateSelectorPanel.setFont(endDateSelectorPanel.getFont());
    endDateSelectorPanel.setOpaque(false);
    endDateSelectorPanel.setPreferredSize(new java.awt.Dimension(210, 170));

    endDateChooserPanel.setCurrentView(new datechooser.view.appearance.AppearancesList("Light",
        new datechooser.view.appearance.ViewAppearance("custom",
            new datechooser.view.appearance.swing.SwingCellAppearance(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 12),
                new java.awt.Color(77, 77, 77),
                new java.awt.Color(0, 0, 255),
                false,
                true,
                new datechooser.view.appearance.swing.ButtonPainter()),
            new datechooser.view.appearance.swing.SwingCellAppearance(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 12),
                new java.awt.Color(77, 77, 77),
                new java.awt.Color(0, 0, 255),
                true,
                true,
                new datechooser.view.appearance.swing.ButtonPainter()),
            new datechooser.view.appearance.swing.SwingCellAppearance(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 12),
                new java.awt.Color(0, 0, 255),
                new java.awt.Color(0, 0, 255),
                false,
                true,
                new datechooser.view.appearance.swing.ButtonPainter()),
            new datechooser.view.appearance.swing.SwingCellAppearance(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 12),
                new java.awt.Color(128, 128, 128),
                new java.awt.Color(0, 0, 255),
                false,
                true,
                new datechooser.view.appearance.swing.LabelPainter()),
            new datechooser.view.appearance.swing.SwingCellAppearance(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 12),
                new java.awt.Color(77, 77, 77),
                new java.awt.Color(0, 0, 255),
                false,
                true,
                new datechooser.view.appearance.swing.LabelPainter()),
            new datechooser.view.appearance.swing.SwingCellAppearance(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 12),
                new java.awt.Color(77, 77, 77),
                new java.awt.Color(255, 0, 0),
                false,
                false,
                new datechooser.view.appearance.swing.ButtonPainter()),
            (datechooser.view.BackRenderer)null,
            false,
            true)));
endDateChooserPanel.setEnabled(false);
endDateChooserPanel.setLocale(new java.util.Locale("en", "", ""));
endDateChooserPanel.setNavigateFont(new java.awt.Font("Verdana", java.awt.Font.PLAIN, 7));
endDateChooserPanel.setBehavior(datechooser.model.multiple.MultyModelBehavior.SELECT_SINGLE);
endDateChooserPanel.addSelectionChangedListener(new datechooser.events.SelectionChangedListener()
{
    public void onSelectionChange(datechooser.events.SelectionChangedEvent evt)
    {
        endDateChooserPanelOnSelectionChange(evt);
    }
    });

    org.jdesktop.layout.GroupLayout endDateSelectorPanelLayout = new org.jdesktop.layout.GroupLayout(endDateSelectorPanel);
    endDateSelectorPanel.setLayout(endDateSelectorPanelLayout);
    endDateSelectorPanelLayout.setHorizontalGroup(
        endDateSelectorPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(endDateChooserPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 198, Short.MAX_VALUE)
    );
    endDateSelectorPanelLayout.setVerticalGroup(
        endDateSelectorPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(endDateChooserPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 199, Short.MAX_VALUE)
    );

    searchPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), bundle.getString("DCMDesktop.searchPanel.border.title"), javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Lucida Grande", 0, 10), new java.awt.Color(102, 102, 102))); // NOI18N
    searchPanel.setFont(searchPanel.getFont());
    searchPanel.setOpaque(false);
    searchPanel.setPreferredSize(new java.awt.Dimension(190, 314));

    deletePresetButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/delete_icon.png"))); // NOI18N
    deletePresetButton.setToolTipText(bundle.getString("DCMDesktop.deletePresetButton.toolTipText")); // NOI18N
    deletePresetButton.setEnabled(false);
    deletePresetButton.setFocusable(false);
    deletePresetButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    deletePresetButton.setMaximumSize(new java.awt.Dimension(80, 10));
    deletePresetButton.setMinimumSize(new java.awt.Dimension(80, 10));
    deletePresetButton.setPreferredSize(new java.awt.Dimension(80, 10));
    deletePresetButton.addActionListener(new java.awt.event.ActionListener()
    {
        public void actionPerformed(java.awt.event.ActionEvent evt)
        {
            deletePresetButtonActionPerformed(evt);
        }
    });

    selectPresetBox.setEditable(true);
    selectPresetBox.setFont(selectPresetBox.getFont().deriveFont(selectPresetBox.getFont().getSize()-3f));
    selectPresetBox.setForeground(new java.awt.Color(102, 102, 102));
    selectPresetBox.setMaximumRowCount(25);
    selectPresetBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Presets" }));
    selectPresetBox.setToolTipText(bundle.getString("DCMDesktop.selectPresetBox.toolTipText")); // NOI18N
    selectPresetBox.setEnabled(false);
    selectPresetBox.setMinimumSize(new java.awt.Dimension(68, 20));
    selectPresetBox.setPreferredSize(new java.awt.Dimension(68, 20));
    selectPresetBox.addPopupMenuListener(new javax.swing.event.PopupMenuListener()
    {
        public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt)
        {
            selectPresetBoxPopupMenuWillBecomeVisible(evt);
        }
        public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt)
        {
        }
        public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt)
        {
        }
    });
    selectPresetBox.addActionListener(new java.awt.event.ActionListener()
    {
        public void actionPerformed(java.awt.event.ActionEvent evt)
        {
            selectPresetBoxActionPerformed(evt);
        }
    });
    selectPresetBox.addFocusListener(new java.awt.event.FocusAdapter()
    {
        public void focusGained(java.awt.event.FocusEvent evt)
        {
            selectPresetBoxFocusGained(evt);
        }
    });

    savePresetButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/save_icon.png"))); // NOI18N
    savePresetButton.setToolTipText(bundle.getString("DCMDesktop.savePresetButton.toolTipText")); // NOI18N
    savePresetButton.setEnabled(false);
    savePresetButton.setFocusable(false);
    savePresetButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    savePresetButton.setMaximumSize(new java.awt.Dimension(80, 10));
    savePresetButton.setMinimumSize(new java.awt.Dimension(80, 10));
    savePresetButton.setPreferredSize(new java.awt.Dimension(80, 10));
    savePresetButton.addActionListener(new java.awt.event.ActionListener()
    {
        public void actionPerformed(java.awt.event.ActionEvent evt)
        {
            savePresetButtonActionPerformed(evt);
        }
    });

    searchField.setFont(searchField.getFont().deriveFont(searchField.getFont().getSize()-3f));
    searchField.setForeground(new java.awt.Color(102, 102, 102));
    searchField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
    searchField.setText(bundle.getString("DCMDesktop.searchField.text")); // NOI18N
    searchField.setToolTipText(bundle.getString("DCMDesktop.searchField.toolTipText")); // NOI18N
    searchField.setEnabled(false);
    searchField.setMaximumSize(new java.awt.Dimension(900, 20));
    searchField.setPreferredSize(new java.awt.Dimension(48, 20));
    searchField.addFocusListener(new java.awt.event.FocusAdapter()
    {
        public void focusGained(java.awt.event.FocusEvent evt)
        {
            searchFieldFocusGained(evt);
        }
    });
    searchField.addKeyListener(new java.awt.event.KeyAdapter()
    {
        public void keyReleased(java.awt.event.KeyEvent evt)
        {
            searchFieldKeyReleased(evt);
        }
    });

    wordSearchButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/search_exact.png"))); // NOI18N
    wordSearchButton.setToolTipText(bundle.getString("DCMDesktop.wordSearchButton.toolTipText")); // NOI18N
    wordSearchButton.setEnabled(false);
    wordSearchButton.setFocusable(false);
    wordSearchButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    wordSearchButton.setMaximumSize(new java.awt.Dimension(80, 10));
    wordSearchButton.setMinimumSize(new java.awt.Dimension(80, 10));
    wordSearchButton.setPreferredSize(new java.awt.Dimension(80, 10));
    wordSearchButton.addActionListener(new java.awt.event.ActionListener()
    {
        public void actionPerformed(java.awt.event.ActionEvent evt)
        {
            wordSearchButtonActionPerformed(evt);
        }
    });

    matchSearchButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/search_match.png"))); // NOI18N
    matchSearchButton.setToolTipText(bundle.getString("DCMDesktop.matchSearchButton.toolTipText")); // NOI18N
    matchSearchButton.setEnabled(false);
    matchSearchButton.setFocusable(false);
    matchSearchButton.setMaximumSize(new java.awt.Dimension(80, 20));
    matchSearchButton.setMinimumSize(new java.awt.Dimension(80, 20));
    matchSearchButton.setPreferredSize(new java.awt.Dimension(112, 20));
    matchSearchButton.addActionListener(new java.awt.event.ActionListener()
    {
        public void actionPerformed(java.awt.event.ActionEvent evt)
        {
            matchSearchButtonActionPerformed(evt);
        }
    });

    selectionArea.setBackground(new java.awt.Color(204, 204, 204));
    selectionArea.setColumns(20);
    selectionArea.setFont(new java.awt.Font("Courier New", 1, 8)); // NOI18N
    selectionArea.setForeground(new java.awt.Color(51, 51, 51));
    selectionArea.setRows(5);
    selectionArea.setToolTipText(bundle.getString("DCMDesktop.selectionArea.toolTipText")); // NOI18N
    selectionArea.setDoubleBuffered(true);
    selectionArea.addCaretListener(new javax.swing.event.CaretListener()
    {
        public void caretUpdate(javax.swing.event.CaretEvent evt)
        {
            selectionAreaCaretUpdate(evt);
        }
    });
    selectionScroller.setViewportView(selectionArea);

    searchStatsScroller.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    searchStatsScroller.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

    searchStatsTable.setBackground(new java.awt.Color(204, 204, 204));
    searchStatsTable.setFont(searchStatsTable.getFont().deriveFont(searchStatsTable.getFont().getSize()-2f));
    searchStatsTable.setForeground(new java.awt.Color(102, 102, 102));
    searchStatsTable.setModel(new javax.swing.table.DefaultTableModel(
        new Object [][]
        {

        },
        new String []
        {
            "Tot", "Servers", "Cat's", "R.Types", "V.Types", "Res."
        }
    )
    {
        Class[] types = new Class []
        {
            java.lang.Long.class, java.lang.Long.class, java.lang.Long.class, java.lang.Long.class, java.lang.Long.class, java.lang.Long.class
        };
        boolean[] canEdit = new boolean []
        {
            false, false, false, false, false, false
        };

        public Class getColumnClass(int columnIndex)
        {
            return types [columnIndex];
        }

        public boolean isCellEditable(int rowIndex, int columnIndex)
        {
            return canEdit [columnIndex];
        }
    });
    searchStatsTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
    searchStatsTable.setAutoscrolls(false);
    searchStatsTable.setEnabled(false);
    searchStatsTable.setFocusable(false);
    searchStatsTable.setGridColor(new java.awt.Color(51, 51, 51));
    searchStatsTable.setOpaque(false);
    searchStatsTable.setRowSelectionAllowed(false);
    searchStatsScroller.setViewportView(searchStatsTable);

    navigateXButtonBar.setBorder(javax.swing.BorderFactory.createEtchedBorder());
    navigateXButtonBar.setFloatable(false);
    navigateXButtonBar.setRollover(true);
    navigateXButtonBar.setFont(navigateXButtonBar.getFont());

    leftButton.setFont(leftButton.getFont().deriveFont(leftButton.getFont().getSize()-2f));
    leftButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/left.png"))); // NOI18N
    leftButton.setToolTipText(bundle.getString("DCMDesktop.leftButton.toolTipText")); // NOI18N
    leftButton.setEnabled(false);
    leftButton.setFocusable(false);
    leftButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    leftButton.setMaximumSize(new java.awt.Dimension(23, 18));
    leftButton.setMinimumSize(new java.awt.Dimension(23, 18));
    leftButton.setPreferredSize(new java.awt.Dimension(23, 18));
    leftButton.addActionListener(new java.awt.event.ActionListener()
    {
        public void actionPerformed(java.awt.event.ActionEvent evt)
        {
            leftButtonActionPerformed(evt);
        }
    });
    navigateXButtonBar.add(leftButton);

    zoomInXButton.setFont(zoomInXButton.getFont().deriveFont(zoomInXButton.getFont().getSize()-2f));
    zoomInXButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/plus.png"))); // NOI18N
    zoomInXButton.setToolTipText(bundle.getString("DCMDesktop.zoomInXButton.toolTipText")); // NOI18N
    zoomInXButton.setEnabled(false);
    zoomInXButton.setFocusable(false);
    zoomInXButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    zoomInXButton.setMaximumSize(new java.awt.Dimension(23, 18));
    zoomInXButton.setMinimumSize(new java.awt.Dimension(23, 18));
    zoomInXButton.setPreferredSize(new java.awt.Dimension(23, 18));
    zoomInXButton.addActionListener(new java.awt.event.ActionListener()
    {
        public void actionPerformed(java.awt.event.ActionEvent evt)
        {
            zoomInXButtonActionPerformed(evt);
        }
    });
    navigateXButtonBar.add(zoomInXButton);

    zoomOutXButton.setFont(zoomOutXButton.getFont().deriveFont(zoomOutXButton.getFont().getSize()-2f));
    zoomOutXButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/minus.png"))); // NOI18N
    zoomOutXButton.setToolTipText(bundle.getString("DCMDesktop.zoomOutXButton.toolTipText")); // NOI18N
    zoomOutXButton.setEnabled(false);
    zoomOutXButton.setFocusable(false);
    zoomOutXButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    zoomOutXButton.setMaximumSize(new java.awt.Dimension(23, 18));
    zoomOutXButton.setMinimumSize(new java.awt.Dimension(23, 18));
    zoomOutXButton.setPreferredSize(new java.awt.Dimension(23, 18));
    zoomOutXButton.addActionListener(new java.awt.event.ActionListener()
    {
        public void actionPerformed(java.awt.event.ActionEvent evt)
        {
            zoomOutXButtonActionPerformed(evt);
        }
    });
    navigateXButtonBar.add(zoomOutXButton);

    rightButton.setFont(rightButton.getFont().deriveFont(rightButton.getFont().getSize()-2f));
    rightButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/right.png"))); // NOI18N
    rightButton.setToolTipText(bundle.getString("DCMDesktop.rightButton.toolTipText")); // NOI18N
    rightButton.setEnabled(false);
    rightButton.setFocusable(false);
    rightButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    rightButton.setMaximumSize(new java.awt.Dimension(23, 18));
    rightButton.setMinimumSize(new java.awt.Dimension(23, 18));
    rightButton.setPreferredSize(new java.awt.Dimension(23, 18));
    rightButton.addActionListener(new java.awt.event.ActionListener()
    {
        public void actionPerformed(java.awt.event.ActionEvent evt)
        {
            rightButtonActionPerformed(evt);
        }
    });
    navigateXButtonBar.add(rightButton);

    swapViewButton.setFont(swapViewButton.getFont().deriveFont(swapViewButton.getFont().getSize()-2f));
    swapViewButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/up-down.png"))); // NOI18N
    swapViewButton.setToolTipText(bundle.getString("DCMDesktop.swapViewButton.toolTipText")); // NOI18N
    swapViewButton.setEnabled(false);
    swapViewButton.setFocusable(false);
    swapViewButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    swapViewButton.setMaximumSize(new java.awt.Dimension(23, 18));
    swapViewButton.setMinimumSize(new java.awt.Dimension(23, 18));
    swapViewButton.setPreferredSize(new java.awt.Dimension(23, 18));
    swapViewButton.addActionListener(new java.awt.event.ActionListener()
    {
        public void actionPerformed(java.awt.event.ActionEvent evt)
        {
            swapViewButtonActionPerformed(evt);
        }
    });
    navigateXButtonBar.add(swapViewButton);

    graphButton.setFont(graphButton.getFont().deriveFont(graphButton.getFont().getSize()-2f));
    graphButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/graph-on.png"))); // NOI18N
    graphButton.setToolTipText(bundle.getString("DCMDesktop.graphButton.toolTipText")); // NOI18N
    graphButton.setEnabled(false);
    graphButton.setFocusable(false);
    graphButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    graphButton.setMaximumSize(new java.awt.Dimension(23, 18));
    graphButton.setMinimumSize(new java.awt.Dimension(23, 18));
    graphButton.setPreferredSize(new java.awt.Dimension(23, 18));
    graphButton.addActionListener(new java.awt.event.ActionListener()
    {
        public void actionPerformed(java.awt.event.ActionEvent evt)
        {
            graphButtonActionPerformed(evt);
        }
    });
    navigateXButtonBar.add(graphButton);

    upButton.setFont(upButton.getFont().deriveFont(upButton.getFont().getSize()-2f));
    upButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/up.png"))); // NOI18N
    upButton.setToolTipText(bundle.getString("DCMDesktop.upButton.toolTipText")); // NOI18N
    upButton.setEnabled(false);
    upButton.setFocusable(false);
    upButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    upButton.setMaximumSize(new java.awt.Dimension(23, 18));
    upButton.setMinimumSize(new java.awt.Dimension(23, 18));
    upButton.setPreferredSize(new java.awt.Dimension(23, 18));
    upButton.addActionListener(new java.awt.event.ActionListener()
    {
        public void actionPerformed(java.awt.event.ActionEvent evt)
        {
            upButtonActionPerformed(evt);
        }
    });
    navigateXButtonBar.add(upButton);

    zoomInYButton.setFont(zoomInYButton.getFont().deriveFont(zoomInYButton.getFont().getSize()-2f));
    zoomInYButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/plus.png"))); // NOI18N
    zoomInYButton.setToolTipText(bundle.getString("DCMDesktop.zoomInYButton.toolTipText")); // NOI18N
    zoomInYButton.setEnabled(false);
    zoomInYButton.setFocusable(false);
    zoomInYButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    zoomInYButton.setMaximumSize(new java.awt.Dimension(23, 18));
    zoomInYButton.setMinimumSize(new java.awt.Dimension(23, 18));
    zoomInYButton.setPreferredSize(new java.awt.Dimension(23, 18));
    zoomInYButton.addActionListener(new java.awt.event.ActionListener()
    {
        public void actionPerformed(java.awt.event.ActionEvent evt)
        {
            zoomInYButtonActionPerformed(evt);
        }
    });
    navigateXButtonBar.add(zoomInYButton);

    zoomOutYButton.setFont(zoomOutYButton.getFont().deriveFont(zoomOutYButton.getFont().getSize()-2f));
    zoomOutYButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/minus.png"))); // NOI18N
    zoomOutYButton.setToolTipText(bundle.getString("DCMDesktop.zoomOutYButton.toolTipText")); // NOI18N
    zoomOutYButton.setEnabled(false);
    zoomOutYButton.setFocusable(false);
    zoomOutYButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    zoomOutYButton.setMaximumSize(new java.awt.Dimension(23, 18));
    zoomOutYButton.setMinimumSize(new java.awt.Dimension(23, 18));
    zoomOutYButton.setPreferredSize(new java.awt.Dimension(23, 18));
    zoomOutYButton.addActionListener(new java.awt.event.ActionListener()
    {
        public void actionPerformed(java.awt.event.ActionEvent evt)
        {
            zoomOutYButtonActionPerformed(evt);
        }
    });
    navigateXButtonBar.add(zoomOutYButton);

    downButton.setFont(downButton.getFont().deriveFont(downButton.getFont().getSize()-2f));
    downButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/down.png"))); // NOI18N
    downButton.setToolTipText(bundle.getString("DCMDesktop.downButton.toolTipText")); // NOI18N
    downButton.setEnabled(false);
    downButton.setFocusable(false);
    downButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    downButton.setMaximumSize(new java.awt.Dimension(23, 18));
    downButton.setMinimumSize(new java.awt.Dimension(23, 18));
    downButton.setPreferredSize(new java.awt.Dimension(23, 18));
    downButton.addActionListener(new java.awt.event.ActionListener()
    {
        public void actionPerformed(java.awt.event.ActionEvent evt)
        {
            downButtonActionPerformed(evt);
        }
    });
    navigateXButtonBar.add(downButton);

    loginButton.setFont(loginButton.getFont().deriveFont(loginButton.getFont().getSize()-2f));
    loginButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lock.png"))); // NOI18N
    loginButton.setToolTipText(bundle.getString("DCMDesktop.loginButton.toolTipText")); // NOI18N
    loginButton.setFocusable(false);
    loginButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    loginButton.setMaximumSize(new java.awt.Dimension(23, 18));
    loginButton.setMinimumSize(new java.awt.Dimension(23, 18));
    loginButton.setPreferredSize(new java.awt.Dimension(23, 18));
    loginButton.addActionListener(new java.awt.event.ActionListener()
    {
        public void actionPerformed(java.awt.event.ActionEvent evt)
        {
            loginButtonActionPerformed(evt);
        }
    });
    navigateXButtonBar.add(loginButton);

    org.jdesktop.layout.GroupLayout searchPanelLayout = new org.jdesktop.layout.GroupLayout(searchPanel);
    searchPanel.setLayout(searchPanelLayout);
    searchPanelLayout.setHorizontalGroup(
        searchPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(searchPanelLayout.createSequentialGroup()
            .add(3, 3, 3)
            .add(deletePresetButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 27, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(selectPresetBox, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(savePresetButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 27, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(searchField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(wordSearchButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 27, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(matchSearchButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 27, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        .add(searchStatsScroller, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        .add(navigateXButtonBar, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 367, Short.MAX_VALUE)
        .add(selectionScroller)
    );
    searchPanelLayout.setVerticalGroup(
        searchPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(searchPanelLayout.createSequentialGroup()
            .add(searchPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                .add(deletePresetButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 27, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(selectPresetBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(savePresetButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 27, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(searchField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(wordSearchButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 27, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(matchSearchButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 27, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(0, 11, Short.MAX_VALUE)
            .add(selectionScroller, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 108, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(searchStatsScroller, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(navigateXButtonBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
    );

    searchPanelLayout.linkSize(new java.awt.Component[] {deletePresetButton, matchSearchButton, savePresetButton, wordSearchButton}, org.jdesktop.layout.GroupLayout.VERTICAL);

    startTimeRelativePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), bundle.getString("DCMDesktop.startTimeRelativePanel.border.title"), javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Lucida Grande", 0, 10), new java.awt.Color(102, 102, 102))); // NOI18N
    startTimeRelativePanel.setFont(startTimeRelativePanel.getFont());
    startTimeRelativePanel.setOpaque(false);
    startTimeRelativePanel.setPreferredSize(new java.awt.Dimension(150, 170));
    java.awt.GridBagLayout startTimeRelativePanelLayout = new java.awt.GridBagLayout();
    startTimeRelativePanelLayout.columnWidths = new int[] {0, 5, 0};
    startTimeRelativePanelLayout.rowHeights = new int[] {0, 7, 0, 7, 0, 7, 0, 7, 0, 7, 0, 7, 0, 7, 0, 7, 0};
    startTimeRelativePanel.setLayout(startTimeRelativePanelLayout);

    startTimeIsRelativeButton.setFont(startTimeIsRelativeButton.getFont().deriveFont((float)9));
    startTimeIsRelativeButton.setText(bundle.getString("DCMDesktop.startTimeIsRelativeButton.text")); // NOI18N
    startTimeIsRelativeButton.setToolTipText(bundle.getString("DCMDesktop.startTimeIsRelativeButton.toolTipText")); // NOI18N
    startTimeIsRelativeButton.setFocusable(false);
    startTimeIsRelativeButton.addActionListener(new java.awt.event.ActionListener()
    {
        public void actionPerformed(java.awt.event.ActionEvent evt)
        {
            startTimeIsRelativeButtonActionPerformed(evt);
        }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.gridwidth = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 2.0;
    gridBagConstraints.weighty = 1.0;
    startTimeRelativePanel.add(startTimeIsRelativeButton, gridBagConstraints);

    startMonthLabel.setFont(startMonthLabel.getFont().deriveFont(startMonthLabel.getFont().getSize()-6f));
    startMonthLabel.setForeground(new java.awt.Color(102, 102, 102));
    startMonthLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    startMonthLabel.setText(bundle.getString("DCMDesktop.startMonthLabel.text")); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 2.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
    startTimeRelativePanel.add(startMonthLabel, gridBagConstraints);

    startMonthPlusButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/plus.png"))); // NOI18N
    startMonthPlusButton.setToolTipText(bundle.getString("DCMDesktop.startMonthPlusButton.toolTipText")); // NOI18N
    startMonthPlusButton.setEnabled(false);
    startMonthPlusButton.setFocusable(false);
    startMonthPlusButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    startMonthPlusButton.setMaximumSize(new java.awt.Dimension(22, 15));
    startMonthPlusButton.setMinimumSize(new java.awt.Dimension(22, 15));
    startMonthPlusButton.setPreferredSize(new java.awt.Dimension(22, 15));
    startMonthPlusButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    startMonthPlusButton.addActionListener(new java.awt.event.ActionListener()
    {
        public void actionPerformed(java.awt.event.ActionEvent evt)
        {
            startMonthPlusButtonActionPerformed(evt);
        }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 2.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
    startTimeRelativePanel.add(startMonthPlusButton, gridBagConstraints);

    startDayPlusButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/plus.png"))); // NOI18N
    startDayPlusButton.setToolTipText(bundle.getString("DCMDesktop.startDayPlusButton.toolTipText")); // NOI18N
    startDayPlusButton.setEnabled(false);
    startDayPlusButton.setFocusable(false);
    startDayPlusButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    startDayPlusButton.setMaximumSize(new java.awt.Dimension(22, 15));
    startDayPlusButton.setMinimumSize(new java.awt.Dimension(22, 15));
    startDayPlusButton.setPreferredSize(new java.awt.Dimension(22, 15));
    startDayPlusButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    startDayPlusButton.addActionListener(new java.awt.event.ActionListener()
    {
        public void actionPerformed(java.awt.event.ActionEvent evt)
        {
            startDayPlusButtonActionPerformed(evt);
        }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 2.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
    startTimeRelativePanel.add(startDayPlusButton, gridBagConstraints);

    startMonthField.setFont(startMonthField.getFont().deriveFont(startMonthField.getFont().getSize()-5f));
    startMonthField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
    startMonthField.setText(bundle.getString("DCMDesktop.startMonthField.text")); // NOI18N
    startMonthField.setToolTipText(bundle.getString("DCMDesktop.startMonthField.toolTipText")); // NOI18N
    startMonthField.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
    startMonthField.setEnabled(false);
    startMonthField.addKeyListener(new java.awt.event.KeyAdapter()
    {
        public void keyReleased(java.awt.event.KeyEvent evt)
        {
            startMonthFieldKeyReleased(evt);
        }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 6;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 2.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
    startTimeRelativePanel.add(startMonthField, gridBagConstraints);

    startDayField.setFont(startDayField.getFont().deriveFont(startDayField.getFont().getSize()-5f));
    startDayField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
    startDayField.setText(bundle.getString("DCMDesktop.startDayField.text")); // NOI18N
    startDayField.setToolTipText(bundle.getString("DCMDesktop.startDayField.toolTipText")); // NOI18N
    startDayField.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
    startDayField.setEnabled(false);
    startDayField.addKeyListener(new java.awt.event.KeyAdapter()
    {
        public void keyReleased(java.awt.event.KeyEvent evt)
        {
            startDayFieldKeyReleased(evt);
        }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 6;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 2.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
    startTimeRelativePanel.add(startDayField, gridBagConstraints);

    startMonthMinusButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/minus.png"))); // NOI18N
    startMonthMinusButton.setToolTipText(bundle.getString("DCMDesktop.startMonthMinusButton.toolTipText")); // NOI18N
    startMonthMinusButton.setEnabled(false);
    startMonthMinusButton.setFocusable(false);
    startMonthMinusButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    startMonthMinusButton.setMaximumSize(new java.awt.Dimension(22, 15));
    startMonthMinusButton.setMinimumSize(new java.awt.Dimension(22, 15));
    startMonthMinusButton.setPreferredSize(new java.awt.Dimension(22, 15));
    startMonthMinusButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    startMonthMinusButton.addActionListener(new java.awt.event.ActionListener()
    {
        public void actionPerformed(java.awt.event.ActionEvent evt)
        {
            startMonthMinusButtonActionPerformed(evt);
        }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 8;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 2.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
    startTimeRelativePanel.add(startMonthMinusButton, gridBagConstraints);

    startDayMinusButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/minus.png"))); // NOI18N
    startDayMinusButton.setToolTipText(bundle.getString("DCMDesktop.startDayMinusButton.toolTipText")); // NOI18N
    startDayMinusButton.setEnabled(false);
    startDayMinusButton.setFocusable(false);
    startDayMinusButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    startDayMinusButton.setMaximumSize(new java.awt.Dimension(22, 15));
    startDayMinusButton.setMinimumSize(new java.awt.Dimension(22, 15));
    startDayMinusButton.setPreferredSize(new java.awt.Dimension(22, 15));
    startDayMinusButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    startDayMinusButton.addActionListener(new java.awt.event.ActionListener()
    {
        public void actionPerformed(java.awt.event.ActionEvent evt)
        {
            startDayMinusButtonActionPerformed(evt);
        }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 8;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 2.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
    startTimeRelativePanel.add(startDayMinusButton, gridBagConstraints);

    startHourLabel.setFont(startHourLabel.getFont().deriveFont(startHourLabel.getFont().getSize()-6f));
    startHourLabel.setForeground(new java.awt.Color(102, 102, 102));
    startHourLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    startHourLabel.setText(bundle.getString("DCMDesktop.startHourLabel.text")); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 10;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 2.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
    startTimeRelativePanel.add(startHourLabel, gridBagConstraints);

    startHourPlusButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/plus.png"))); // NOI18N
    startHourPlusButton.setToolTipText(bundle.getString("DCMDesktop.startHourPlusButton.toolTipText")); // NOI18N
    startHourPlusButton.setEnabled(false);
    startHourPlusButton.setFocusable(false);
    startHourPlusButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    startHourPlusButton.setMaximumSize(new java.awt.Dimension(22, 15));
    startHourPlusButton.setMinimumSize(new java.awt.Dimension(22, 15));
    startHourPlusButton.setPreferredSize(new java.awt.Dimension(22, 15));
    startHourPlusButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    startHourPlusButton.addActionListener(new java.awt.event.ActionListener()
    {
        public void actionPerformed(java.awt.event.ActionEvent evt)
        {
            startHourPlusButtonActionPerformed(evt);
        }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 12;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 2.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
    startTimeRelativePanel.add(startHourPlusButton, gridBagConstraints);

    startMinutePlusButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/plus.png"))); // NOI18N
    startMinutePlusButton.setToolTipText(bundle.getString("DCMDesktop.startMinutePlusButton.toolTipText")); // NOI18N
    startMinutePlusButton.setEnabled(false);
    startMinutePlusButton.setFocusable(false);
    startMinutePlusButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    startMinutePlusButton.setMaximumSize(new java.awt.Dimension(22, 15));
    startMinutePlusButton.setMinimumSize(new java.awt.Dimension(22, 15));
    startMinutePlusButton.setPreferredSize(new java.awt.Dimension(22, 15));
    startMinutePlusButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    startMinutePlusButton.addActionListener(new java.awt.event.ActionListener()
    {
        public void actionPerformed(java.awt.event.ActionEvent evt)
        {
            startMinutePlusButtonActionPerformed(evt);
        }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 12;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 2.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
    startTimeRelativePanel.add(startMinutePlusButton, gridBagConstraints);

    startHourField.setFont(startHourField.getFont().deriveFont(startHourField.getFont().getSize()-5f));
    startHourField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
    startHourField.setText(bundle.getString("DCMDesktop.startHourField.text")); // NOI18N
    startHourField.setToolTipText(bundle.getString("DCMDesktop.startHourField.toolTipText")); // NOI18N
    startHourField.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
    startHourField.setEnabled(false);
    startHourField.addKeyListener(new java.awt.event.KeyAdapter()
    {
        public void keyReleased(java.awt.event.KeyEvent evt)
        {
            startHourFieldKeyReleased(evt);
        }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 14;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 2.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
    startTimeRelativePanel.add(startHourField, gridBagConstraints);

    startMinuteField.setFont(startMinuteField.getFont().deriveFont(startMinuteField.getFont().getSize()-5f));
    startMinuteField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
    startMinuteField.setText(bundle.getString("DCMDesktop.startMinuteField.text")); // NOI18N
    startMinuteField.setToolTipText(bundle.getString("DCMDesktop.startMinuteField.toolTipText")); // NOI18N
    startMinuteField.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
    startMinuteField.setEnabled(false);
    startMinuteField.addKeyListener(new java.awt.event.KeyAdapter()
    {
        public void keyReleased(java.awt.event.KeyEvent evt)
        {
            startMinuteFieldKeyReleased(evt);
        }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 14;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 2.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
    startTimeRelativePanel.add(startMinuteField, gridBagConstraints);

    startHourMinusButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/minus.png"))); // NOI18N
    startHourMinusButton.setToolTipText(bundle.getString("DCMDesktop.startHourMinusButton.toolTipText")); // NOI18N
    startHourMinusButton.setEnabled(false);
    startHourMinusButton.setFocusable(false);
    startHourMinusButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    startHourMinusButton.setMaximumSize(new java.awt.Dimension(22, 15));
    startHourMinusButton.setMinimumSize(new java.awt.Dimension(22, 15));
    startHourMinusButton.setPreferredSize(new java.awt.Dimension(22, 15));
    startHourMinusButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    startHourMinusButton.addActionListener(new java.awt.event.ActionListener()
    {
        public void actionPerformed(java.awt.event.ActionEvent evt)
        {
            startHourMinusButtonActionPerformed(evt);
        }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 16;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 2.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
    startTimeRelativePanel.add(startHourMinusButton, gridBagConstraints);

    startMinuteMinusButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/minus.png"))); // NOI18N
    startMinuteMinusButton.setToolTipText(bundle.getString("DCMDesktop.startMinuteMinusButton.toolTipText")); // NOI18N
    startMinuteMinusButton.setEnabled(false);
    startMinuteMinusButton.setFocusable(false);
    startMinuteMinusButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    startMinuteMinusButton.setMaximumSize(new java.awt.Dimension(22, 15));
    startMinuteMinusButton.setMinimumSize(new java.awt.Dimension(22, 15));
    startMinuteMinusButton.setPreferredSize(new java.awt.Dimension(22, 15));
    startMinuteMinusButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    startMinuteMinusButton.addActionListener(new java.awt.event.ActionListener()
    {
        public void actionPerformed(java.awt.event.ActionEvent evt)
        {
            startMinuteMinusButtonActionPerformed(evt);
        }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 16;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 2.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
    startTimeRelativePanel.add(startMinuteMinusButton, gridBagConstraints);

    startDayLabel.setFont(startDayLabel.getFont().deriveFont(startDayLabel.getFont().getSize()-6f));
    startDayLabel.setForeground(new java.awt.Color(102, 102, 102));
    startDayLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    startDayLabel.setText(bundle.getString("DCMDesktop.startDayLabel.text")); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 2.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
    startTimeRelativePanel.add(startDayLabel, gridBagConstraints);

    startMinuteLabel.setFont(startMinuteLabel.getFont().deriveFont(startMinuteLabel.getFont().getSize()-6f));
    startMinuteLabel.setForeground(new java.awt.Color(102, 102, 102));
    startMinuteLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    startMinuteLabel.setText(bundle.getString("DCMDesktop.startMinuteLabel.text")); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 10;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 2.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
    startTimeRelativePanel.add(startMinuteLabel, gridBagConstraints);

    endTimeRelativePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), bundle.getString("DCMDesktop.endTimeRelativePanel.border.title"), javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Lucida Grande", 0, 10), new java.awt.Color(102, 102, 102))); // NOI18N
    endTimeRelativePanel.setFont(endTimeRelativePanel.getFont());
    endTimeRelativePanel.setOpaque(false);
    endTimeRelativePanel.setPreferredSize(new java.awt.Dimension(150, 170));
    java.awt.GridBagLayout endTimeRelativePanelLayout = new java.awt.GridBagLayout();
    endTimeRelativePanelLayout.columnWidths = new int[] {0, 5, 0};
    endTimeRelativePanelLayout.rowHeights = new int[] {0, 7, 0, 7, 0, 7, 0, 7, 0, 7, 0, 7, 0, 7, 0, 7, 0};
    endTimeRelativePanel.setLayout(endTimeRelativePanelLayout);

    endTimeIsRelativeButton.setFont(endTimeIsRelativeButton.getFont().deriveFont((float)9));
    endTimeIsRelativeButton.setText(bundle.getString("DCMDesktop.endTimeIsRelativeButton.text")); // NOI18N
    endTimeIsRelativeButton.setToolTipText(bundle.getString("DCMDesktop.endTimeIsRelativeButton.toolTipText")); // NOI18N
    endTimeIsRelativeButton.setFocusable(false);
    endTimeIsRelativeButton.addActionListener(new java.awt.event.ActionListener()
    {
        public void actionPerformed(java.awt.event.ActionEvent evt)
        {
            endTimeIsRelativeButtonActionPerformed(evt);
        }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.gridwidth = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 2.0;
    gridBagConstraints.weighty = 1.0;
    endTimeRelativePanel.add(endTimeIsRelativeButton, gridBagConstraints);

    endMonthLabel.setFont(endMonthLabel.getFont().deriveFont(endMonthLabel.getFont().getSize()-6f));
    endMonthLabel.setForeground(new java.awt.Color(102, 102, 102));
    endMonthLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    endMonthLabel.setText(bundle.getString("DCMDesktop.endMonthLabel.text")); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 2.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
    endTimeRelativePanel.add(endMonthLabel, gridBagConstraints);

    endMonthPlusButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/plus.png"))); // NOI18N
    endMonthPlusButton.setToolTipText(bundle.getString("DCMDesktop.endMonthPlusButton.toolTipText")); // NOI18N
    endMonthPlusButton.setEnabled(false);
    endMonthPlusButton.setFocusable(false);
    endMonthPlusButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    endMonthPlusButton.setMaximumSize(new java.awt.Dimension(22, 15));
    endMonthPlusButton.setMinimumSize(new java.awt.Dimension(22, 15));
    endMonthPlusButton.setPreferredSize(new java.awt.Dimension(22, 15));
    endMonthPlusButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    endMonthPlusButton.addActionListener(new java.awt.event.ActionListener()
    {
        public void actionPerformed(java.awt.event.ActionEvent evt)
        {
            endMonthPlusButtonActionPerformed(evt);
        }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 2.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
    endTimeRelativePanel.add(endMonthPlusButton, gridBagConstraints);

    endDayPlusButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/plus.png"))); // NOI18N
    endDayPlusButton.setToolTipText(bundle.getString("DCMDesktop.endDayPlusButton.toolTipText")); // NOI18N
    endDayPlusButton.setEnabled(false);
    endDayPlusButton.setFocusable(false);
    endDayPlusButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    endDayPlusButton.setMaximumSize(new java.awt.Dimension(22, 15));
    endDayPlusButton.setMinimumSize(new java.awt.Dimension(22, 15));
    endDayPlusButton.setPreferredSize(new java.awt.Dimension(22, 15));
    endDayPlusButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    endDayPlusButton.addActionListener(new java.awt.event.ActionListener()
    {
        public void actionPerformed(java.awt.event.ActionEvent evt)
        {
            endDayPlusButtonActionPerformed(evt);
        }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 2.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
    endTimeRelativePanel.add(endDayPlusButton, gridBagConstraints);

    endMonthField.setFont(endMonthField.getFont().deriveFont(endMonthField.getFont().getSize()-5f));
    endMonthField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
    endMonthField.setText(bundle.getString("DCMDesktop.endMonthField.text")); // NOI18N
    endMonthField.setToolTipText(bundle.getString("DCMDesktop.endMonthField.toolTipText")); // NOI18N
    endMonthField.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
    endMonthField.setEnabled(false);
    endMonthField.addKeyListener(new java.awt.event.KeyAdapter()
    {
        public void keyReleased(java.awt.event.KeyEvent evt)
        {
            endMonthFieldKeyReleased(evt);
        }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 6;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 2.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
    endTimeRelativePanel.add(endMonthField, gridBagConstraints);

    endDayField.setFont(endDayField.getFont().deriveFont(endDayField.getFont().getSize()-5f));
    endDayField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
    endDayField.setText(bundle.getString("DCMDesktop.endDayField.text")); // NOI18N
    endDayField.setToolTipText(bundle.getString("DCMDesktop.endDayField.toolTipText")); // NOI18N
    endDayField.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
    endDayField.setEnabled(false);
    endDayField.addKeyListener(new java.awt.event.KeyAdapter()
    {
        public void keyReleased(java.awt.event.KeyEvent evt)
        {
            endDayFieldKeyReleased(evt);
        }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 6;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 2.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
    endTimeRelativePanel.add(endDayField, gridBagConstraints);

    endMonthMinusButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/minus.png"))); // NOI18N
    endMonthMinusButton.setToolTipText(bundle.getString("DCMDesktop.endMonthMinusButton.toolTipText")); // NOI18N
    endMonthMinusButton.setEnabled(false);
    endMonthMinusButton.setFocusable(false);
    endMonthMinusButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    endMonthMinusButton.setMaximumSize(new java.awt.Dimension(22, 15));
    endMonthMinusButton.setMinimumSize(new java.awt.Dimension(22, 15));
    endMonthMinusButton.setPreferredSize(new java.awt.Dimension(22, 15));
    endMonthMinusButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    endMonthMinusButton.addActionListener(new java.awt.event.ActionListener()
    {
        public void actionPerformed(java.awt.event.ActionEvent evt)
        {
            endMonthMinusButtonActionPerformed(evt);
        }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 8;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 2.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
    endTimeRelativePanel.add(endMonthMinusButton, gridBagConstraints);

    endDayMinusButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/minus.png"))); // NOI18N
    endDayMinusButton.setToolTipText(bundle.getString("DCMDesktop.endDayMinusButton.toolTipText")); // NOI18N
    endDayMinusButton.setEnabled(false);
    endDayMinusButton.setFocusable(false);
    endDayMinusButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    endDayMinusButton.setMaximumSize(new java.awt.Dimension(22, 15));
    endDayMinusButton.setMinimumSize(new java.awt.Dimension(22, 15));
    endDayMinusButton.setPreferredSize(new java.awt.Dimension(22, 15));
    endDayMinusButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    endDayMinusButton.addActionListener(new java.awt.event.ActionListener()
    {
        public void actionPerformed(java.awt.event.ActionEvent evt)
        {
            endDayMinusButtonActionPerformed(evt);
        }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 8;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 2.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
    endTimeRelativePanel.add(endDayMinusButton, gridBagConstraints);

    endHourLabel.setFont(endHourLabel.getFont().deriveFont(endHourLabel.getFont().getSize()-6f));
    endHourLabel.setForeground(new java.awt.Color(102, 102, 102));
    endHourLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    endHourLabel.setText(bundle.getString("DCMDesktop.endHourLabel.text")); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 10;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 2.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
    endTimeRelativePanel.add(endHourLabel, gridBagConstraints);

    endHourPlusButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/plus.png"))); // NOI18N
    endHourPlusButton.setToolTipText(bundle.getString("DCMDesktop.endHourPlusButton.toolTipText")); // NOI18N
    endHourPlusButton.setEnabled(false);
    endHourPlusButton.setFocusable(false);
    endHourPlusButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    endHourPlusButton.setMaximumSize(new java.awt.Dimension(22, 15));
    endHourPlusButton.setMinimumSize(new java.awt.Dimension(22, 15));
    endHourPlusButton.setPreferredSize(new java.awt.Dimension(22, 15));
    endHourPlusButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    endHourPlusButton.addActionListener(new java.awt.event.ActionListener()
    {
        public void actionPerformed(java.awt.event.ActionEvent evt)
        {
            endHourPlusButtonActionPerformed(evt);
        }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 12;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 2.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
    endTimeRelativePanel.add(endHourPlusButton, gridBagConstraints);

    endMinutePlusButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/plus.png"))); // NOI18N
    endMinutePlusButton.setToolTipText(bundle.getString("DCMDesktop.endMinutePlusButton.toolTipText")); // NOI18N
    endMinutePlusButton.setEnabled(false);
    endMinutePlusButton.setFocusable(false);
    endMinutePlusButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    endMinutePlusButton.setMaximumSize(new java.awt.Dimension(22, 15));
    endMinutePlusButton.setMinimumSize(new java.awt.Dimension(22, 15));
    endMinutePlusButton.setPreferredSize(new java.awt.Dimension(22, 15));
    endMinutePlusButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    endMinutePlusButton.addActionListener(new java.awt.event.ActionListener()
    {
        public void actionPerformed(java.awt.event.ActionEvent evt)
        {
            endMinutePlusButtonActionPerformed(evt);
        }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 12;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 2.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
    endTimeRelativePanel.add(endMinutePlusButton, gridBagConstraints);

    endHourField.setFont(endHourField.getFont().deriveFont(endHourField.getFont().getSize()-5f));
    endHourField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
    endHourField.setText(bundle.getString("DCMDesktop.endHourField.text")); // NOI18N
    endHourField.setToolTipText(bundle.getString("DCMDesktop.endHourField.toolTipText")); // NOI18N
    endHourField.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
    endHourField.setEnabled(false);
    endHourField.addKeyListener(new java.awt.event.KeyAdapter()
    {
        public void keyReleased(java.awt.event.KeyEvent evt)
        {
            endHourFieldKeyReleased(evt);
        }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 14;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 2.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
    endTimeRelativePanel.add(endHourField, gridBagConstraints);

    endMinuteField.setFont(endMinuteField.getFont().deriveFont(endMinuteField.getFont().getSize()-5f));
    endMinuteField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
    endMinuteField.setText(bundle.getString("DCMDesktop.endMinuteField.text")); // NOI18N
    endMinuteField.setToolTipText(bundle.getString("DCMDesktop.endMinuteField.toolTipText")); // NOI18N
    endMinuteField.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
    endMinuteField.setEnabled(false);
    endMinuteField.addKeyListener(new java.awt.event.KeyAdapter()
    {
        public void keyReleased(java.awt.event.KeyEvent evt)
        {
            endMinuteFieldKeyReleased(evt);
        }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 14;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 2.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
    endTimeRelativePanel.add(endMinuteField, gridBagConstraints);

    endHourMinusButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/minus.png"))); // NOI18N
    endHourMinusButton.setToolTipText(bundle.getString("DCMDesktop.endHourMinusButton.toolTipText")); // NOI18N
    endHourMinusButton.setEnabled(false);
    endHourMinusButton.setFocusable(false);
    endHourMinusButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    endHourMinusButton.setMaximumSize(new java.awt.Dimension(22, 15));
    endHourMinusButton.setMinimumSize(new java.awt.Dimension(22, 15));
    endHourMinusButton.setPreferredSize(new java.awt.Dimension(22, 15));
    endHourMinusButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    endHourMinusButton.addActionListener(new java.awt.event.ActionListener()
    {
        public void actionPerformed(java.awt.event.ActionEvent evt)
        {
            endHourMinusButtonActionPerformed(evt);
        }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 16;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 2.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
    endTimeRelativePanel.add(endHourMinusButton, gridBagConstraints);

    endMinuteMinusButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/minus.png"))); // NOI18N
    endMinuteMinusButton.setToolTipText(bundle.getString("DCMDesktop.endMinuteMinusButton.toolTipText")); // NOI18N
    endMinuteMinusButton.setEnabled(false);
    endMinuteMinusButton.setFocusable(false);
    endMinuteMinusButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    endMinuteMinusButton.setMaximumSize(new java.awt.Dimension(22, 15));
    endMinuteMinusButton.setMinimumSize(new java.awt.Dimension(22, 15));
    endMinuteMinusButton.setPreferredSize(new java.awt.Dimension(22, 15));
    endMinuteMinusButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    endMinuteMinusButton.addActionListener(new java.awt.event.ActionListener()
    {
        public void actionPerformed(java.awt.event.ActionEvent evt)
        {
            endMinuteMinusButtonActionPerformed(evt);
        }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 16;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 2.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
    endTimeRelativePanel.add(endMinuteMinusButton, gridBagConstraints);

    endMinuteLabel.setFont(endMinuteLabel.getFont().deriveFont(endMinuteLabel.getFont().getSize()-6f));
    endMinuteLabel.setForeground(new java.awt.Color(102, 102, 102));
    endMinuteLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    endMinuteLabel.setText(bundle.getString("DCMDesktop.endMinuteLabel.text")); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 10;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 2.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
    endTimeRelativePanel.add(endMinuteLabel, gridBagConstraints);

    endDayLabel.setFont(endDayLabel.getFont().deriveFont(endDayLabel.getFont().getSize()-6f));
    endDayLabel.setForeground(new java.awt.Color(102, 102, 102));
    endDayLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    endDayLabel.setText(bundle.getString("DCMDesktop.endDayLabel.text")); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 2.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
    endTimeRelativePanel.add(endDayLabel, gridBagConstraints);

    org.jdesktop.layout.GroupLayout navigatorPanelLayout = new org.jdesktop.layout.GroupLayout(navigatorPanel);
    navigatorPanel.setLayout(navigatorPanelLayout);
    navigatorPanelLayout.setHorizontalGroup(
        navigatorPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(navigatorPanelLayout.createSequentialGroup()
            .add(startDateSelectorPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(startTimeSelectorPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(startTimeRelativePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 79, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(searchPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 375, Short.MAX_VALUE)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(endTimeRelativePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 79, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(endTimeSelectorPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 94, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(endDateSelectorPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
    );

    navigatorPanelLayout.linkSize(new java.awt.Component[] {endTimeSelectorPanel, startTimeSelectorPanel}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

    navigatorPanelLayout.linkSize(new java.awt.Component[] {endDateSelectorPanel, startDateSelectorPanel}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

    navigatorPanelLayout.setVerticalGroup(
        navigatorPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(navigatorPanelLayout.createSequentialGroup()
            .add(navigatorPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                .add(org.jdesktop.layout.GroupLayout.LEADING, startDateSelectorPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 220, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(org.jdesktop.layout.GroupLayout.LEADING, endDateSelectorPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 220, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(org.jdesktop.layout.GroupLayout.LEADING, endTimeSelectorPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                .add(org.jdesktop.layout.GroupLayout.LEADING, startTimeSelectorPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(org.jdesktop.layout.GroupLayout.LEADING, startTimeRelativePanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(org.jdesktop.layout.GroupLayout.LEADING, endTimeRelativePanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(org.jdesktop.layout.GroupLayout.LEADING, searchPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE))
            .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    navigatorPanelLayout.linkSize(new java.awt.Component[] {endDateSelectorPanel, endTimeRelativePanel, endTimeSelectorPanel, searchPanel, startDateSelectorPanel, startTimeRelativePanel, startTimeSelectorPanel}, org.jdesktop.layout.GroupLayout.VERTICAL);

    viewSplitter.setBorder(javax.swing.BorderFactory.createTitledBorder(null, bundle.getString("DCMDesktop.viewSplitter.border.title"), javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Lucida Grande", 0, 14), new java.awt.Color(102, 102, 102))); // NOI18N
    viewSplitter.setDividerLocation(0);
    viewSplitter.setDividerSize(1);
    viewSplitter.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
    viewSplitter.setDoubleBuffered(true);
    viewSplitter.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
    viewSplitter.setMinimumSize(new java.awt.Dimension(500, 0));
    viewSplitter.setPreferredSize(new java.awt.Dimension(600, 600));

    treeInventorySplitter.setBorder(null);
    treeInventorySplitter.setDividerLocation(212);
    treeInventorySplitter.setDividerSize(4);
    treeInventorySplitter.setMinimumSize(new java.awt.Dimension(133, 0));

    treeSplitter.setDividerLocation(32);
    treeSplitter.setDividerSize(1);
    treeSplitter.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

    treeButtonBar.setBorder(javax.swing.BorderFactory.createEtchedBorder());
    treeButtonBar.setFloatable(false);
    treeButtonBar.setRollover(true);
    treeButtonBar.setFont(treeButtonBar.getFont());

    addServerButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/plus.png"))); // NOI18N
    addServerButton.setToolTipText(bundle.getString("DCMDesktop.addServerButton.toolTipText")); // NOI18N
    addServerButton.setEnabled(false);
    addServerButton.setMaximumSize(new java.awt.Dimension(23, 18));
    addServerButton.setMinimumSize(new java.awt.Dimension(23, 18));
    addServerButton.setPreferredSize(new java.awt.Dimension(23, 18));
    addServerButton.addActionListener(new java.awt.event.ActionListener()
    {
        public void actionPerformed(java.awt.event.ActionEvent evt)
        {
            addServerButtonActionPerformed(evt);
        }
    });
    treeButtonBar.add(addServerButton);

    deleteServerButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/minus.png"))); // NOI18N
    deleteServerButton.setToolTipText(bundle.getString("DCMDesktop.deleteServerButton.toolTipText")); // NOI18N
    deleteServerButton.setEnabled(false);
    deleteServerButton.setFocusable(false);
    deleteServerButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    deleteServerButton.setMaximumSize(new java.awt.Dimension(23, 18));
    deleteServerButton.setMinimumSize(new java.awt.Dimension(23, 18));
    deleteServerButton.setPreferredSize(new java.awt.Dimension(23, 18));
    deleteServerButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    deleteServerButton.addActionListener(new java.awt.event.ActionListener()
    {
        public void actionPerformed(java.awt.event.ActionEvent evt)
        {
            deleteServerButtonActionPerformed(evt);
        }
    });
    treeButtonBar.add(deleteServerButton);

    startPollerButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/poller_icon.png"))); // NOI18N
    startPollerButton.setToolTipText(bundle.getString("DCMDesktop.startPollerButton.toolTipText")); // NOI18N
    startPollerButton.setEnabled(false);
    startPollerButton.setFocusable(false);
    startPollerButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    startPollerButton.setMaximumSize(new java.awt.Dimension(23, 18));
    startPollerButton.setMinimumSize(new java.awt.Dimension(23, 18));
    startPollerButton.setPreferredSize(new java.awt.Dimension(23, 18));
    startPollerButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    startPollerButton.addActionListener(new java.awt.event.ActionListener()
    {
        public void actionPerformed(java.awt.event.ActionEvent evt)
        {
            startPollerButtonActionPerformed(evt);
        }
    });
    treeButtonBar.add(startPollerButton);

    startCommanderButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/terminal_icon.png"))); // NOI18N
    startCommanderButton.setToolTipText(bundle.getString("DCMDesktop.startCommanderButton.toolTipText")); // NOI18N
    startCommanderButton.setEnabled(false);
    startCommanderButton.setFocusable(false);
    startCommanderButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    startCommanderButton.setMaximumSize(new java.awt.Dimension(23, 18));
    startCommanderButton.setMinimumSize(new java.awt.Dimension(23, 18));
    startCommanderButton.setPreferredSize(new java.awt.Dimension(23, 18));
    startCommanderButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    startCommanderButton.addActionListener(new java.awt.event.ActionListener()
    {
        public void actionPerformed(java.awt.event.ActionEvent evt)
        {
            startCommanderButtonActionPerformed(evt);
        }
    });
    treeButtonBar.add(startCommanderButton);

    treeSplitter.setLeftComponent(treeButtonBar);

    serverTreeScoller.setFont(serverTreeScoller.getFont());

    serverTree.setFont(serverTree.getFont());
    javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("Servers");
    serverTree.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
    serverTree.setEnabled(false);
    serverTreeScoller.setViewportView(serverTree);

    treeSplitter.setBottomComponent(serverTreeScoller);

    treeInventorySplitter.setLeftComponent(treeSplitter);

    tableSplitter.setDividerLocation(40);
    tableSplitter.setDividerSize(1);
    tableSplitter.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

    hostScroller.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
    hostScroller.setFont(hostScroller.getFont());

    hostTable.setAutoCreateRowSorter(true);
    hostTable.setBackground(new java.awt.Color(204, 204, 204));
    hostTable.setFont(hostTable.getFont().deriveFont((float)11));
    hostTable.setForeground(new java.awt.Color(51, 51, 51));
    hostTable.setModel(new javax.swing.table.DefaultTableModel(
        new Object [][]
        {
            {null, null, null, null, null, null, null, null, null, null}
        },
        new String []
        {
            "Id", "Host", "Port", "Username", "Password", "SUPassword", "Enabled", "Command", "Comments", "Sysinfo"
        }
    )
    {
        Class[] types = new Class []
        {
            java.lang.Integer.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Boolean.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
        };
        boolean[] canEdit = new boolean []
        {
            false, true, true, true, true, true, true, true, true, true
        };

        public Class getColumnClass(int columnIndex)
        {
            return types [columnIndex];
        }

        public boolean isCellEditable(int rowIndex, int columnIndex)
        {
            return canEdit [columnIndex];
        }
    });
    hostTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
    hostTable.setColumnSelectionAllowed(true);
    hostTable.setDoubleBuffered(true);
    hostTable.setDragEnabled(true);
    hostScroller.setViewportView(hostTable);

    tableSplitter.setTopComponent(hostScroller);

    resourcesScroller.setFont(resourcesScroller.getFont());

    resourcesTable.setAutoCreateRowSorter(true);
    resourcesTable.setBackground(new java.awt.Color(204, 204, 204));
    resourcesTable.setFont(resourcesTable.getFont().deriveFont((float)11));
    resourcesTable.setForeground(new java.awt.Color(51, 51, 51));
    resourcesTable.setModel(new javax.swing.table.DefaultTableModel(
        new Object [][]
        {
            {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null}
        },
        new String []
        {
            "Id", "HostId", "Category", "ResourceType", "ValueType", "CounterType", "Resource", "PollCommand", "LastValue", "WarningLimit", "CriticalLimit", "AlertPolls", "Updated", "RRDFile", "Retention", "Enabled"
        }
    )
    {
        Class[] types = new Class []
        {
            java.lang.Long.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Integer.class, java.lang.Long.class, java.lang.String.class, java.lang.Integer.class, java.lang.Boolean.class
        };
        boolean[] canEdit = new boolean []
        {
            false, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true
        };

        public Class getColumnClass(int columnIndex)
        {
            return types [columnIndex];
        }

        public boolean isCellEditable(int rowIndex, int columnIndex)
        {
            return canEdit [columnIndex];
        }
    });
    resourcesTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
    resourcesTable.setColumnSelectionAllowed(true);
    resourcesTable.setDoubleBuffered(true);
    resourcesTable.setDragEnabled(true);
    resourcesScroller.setViewportView(resourcesTable);

    tableSplitter.setRightComponent(resourcesScroller);

    treeInventorySplitter.setRightComponent(tableSplitter);

    viewSplitter.setLeftComponent(treeInventorySplitter);

    graphLabel.setBackground(new java.awt.Color(255, 255, 255));
    graphLabel.setFont(graphLabel.getFont());
    graphLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    graphLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/dcmheader.jpg"))); // NOI18N
    graphLabel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
    graphLabel.setMaximumSize(new java.awt.Dimension(10000, 5000));
    graphLabel.setMinimumSize(new java.awt.Dimension(0, 0));
    graphLabel.setOpaque(true);
    graphLabel.addMouseListener(new java.awt.event.MouseAdapter()
    {
        public void mouseClicked(java.awt.event.MouseEvent evt)
        {
            graphLabelMouseClicked(evt);
        }
    });
    viewSplitter.setBottomComponent(graphLabel);

    org.jdesktop.layout.GroupLayout managerPanelLayout = new org.jdesktop.layout.GroupLayout(managerPanel);
    managerPanel.setLayout(managerPanelLayout);
    managerPanelLayout.setHorizontalGroup(
        managerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(navigatorPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .add(viewSplitter, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
    );
    managerPanelLayout.setVerticalGroup(
        managerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(managerPanelLayout.createSequentialGroup()
            .add(navigatorPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(viewSplitter, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 511, Short.MAX_VALUE))
    );

    managerTab.addTab(bundle.getString("DCMDesktop.managerPanel.TabConstraints.tabTitle"), managerPanel); // NOI18N

    adminPanel.setFont(adminPanel.getFont());
    adminPanel.setName("adminPanel"); // NOI18N
    adminPanel.setPreferredSize(new java.awt.Dimension(1164, 700));
    adminPanel.addMouseListener(new java.awt.event.MouseAdapter()
    {
        public void mouseClicked(java.awt.event.MouseEvent evt)
        {
            adminPanelMouseClicked(evt);
        }
    });

    licenseInnerPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, bundle.getString("DCMDesktop.licenseInnerPanel.border.title"), javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Lucida Grande", 0, 14), new java.awt.Color(51, 51, 51))); // NOI18N
    licenseInnerPanel.setFont(licenseInnerPanel.getFont());
    licenseInnerPanel.setPreferredSize(new java.awt.Dimension(830, 370));

    applyVergunningButton.setFont(applyVergunningButton.getFont().deriveFont(applyVergunningButton.getFont().getSize()+1f));
    applyVergunningButton.setText(bundle.getString("DCMDesktop.applyVergunningButton.text")); // NOI18N
    applyVergunningButton.setToolTipText(bundle.getString("DCMDesktop.applyVergunningButton.toolTipText")); // NOI18N
    applyVergunningButton.setEnabled(false);
    applyVergunningButton.setFocusPainted(false);
    applyVergunningButton.addActionListener(new java.awt.event.ActionListener()
    {
        public void actionPerformed(java.awt.event.ActionEvent evt)
        {
            applyVergunningButtonActionPerformed(evt);
        }
    });

    licenseDatePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, bundle.getString("DCMDesktop.licenseDatePanel.border.title"), javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Lucida Grande", 0, 10))); // NOI18N
    licenseDatePanel.setToolTipText(bundle.getString("DCMDesktop.licenseDatePanel.toolTipText")); // NOI18N
    licenseDatePanel.setFont(licenseDatePanel.getFont());
    licenseDatePanel.setMaximumSize(new java.awt.Dimension(200, 239));
    licenseDatePanel.setMinimumSize(new java.awt.Dimension(200, 239));
    licenseDatePanel.setLayout(new java.awt.GridBagLayout());

    vergunningDateChooserPanel.setCurrentView(new datechooser.view.appearance.AppearancesList("Light",
        new datechooser.view.appearance.ViewAppearance("custom",
            new datechooser.view.appearance.swing.SwingCellAppearance(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 12),
                new java.awt.Color(77, 77, 77),
                new java.awt.Color(0, 0, 255),
                false,
                true,
                new datechooser.view.appearance.swing.ButtonPainter()),
            new datechooser.view.appearance.swing.SwingCellAppearance(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 12),
                new java.awt.Color(77, 77, 77),
                new java.awt.Color(0, 0, 255),
                true,
                true,
                new datechooser.view.appearance.swing.ButtonPainter()),
            new datechooser.view.appearance.swing.SwingCellAppearance(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 12),
                new java.awt.Color(0, 0, 255),
                new java.awt.Color(0, 0, 255),
                false,
                true,
                new datechooser.view.appearance.swing.ButtonPainter()),
            new datechooser.view.appearance.swing.SwingCellAppearance(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 12),
                new java.awt.Color(128, 128, 128),
                new java.awt.Color(0, 0, 255),
                false,
                true,
                new datechooser.view.appearance.swing.LabelPainter()),
            new datechooser.view.appearance.swing.SwingCellAppearance(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 12),
                new java.awt.Color(77, 77, 77),
                new java.awt.Color(0, 0, 255),
                false,
                true,
                new datechooser.view.appearance.swing.LabelPainter()),
            new datechooser.view.appearance.swing.SwingCellAppearance(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 12),
                new java.awt.Color(77, 77, 77),
                new java.awt.Color(255, 0, 0),
                false,
                false,
                new datechooser.view.appearance.swing.ButtonPainter()),
            (datechooser.view.BackRenderer)null,
            false,
            true)));
vergunningDateChooserPanel.setLocale(new java.util.Locale("en", "", ""));
vergunningDateChooserPanel.setNavigateFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 9));
vergunningDateChooserPanel.setBehavior(datechooser.model.multiple.MultyModelBehavior.SELECT_SINGLE);
vergunningDateChooserPanel.addSelectionChangedListener(new datechooser.events.SelectionChangedListener()
{
    public void onSelectionChange(datechooser.events.SelectionChangedEvent evt)
    {
        vergunningDateChooserPanelOnSelectionChange(evt);
    }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 0.1;
    gridBagConstraints.weighty = 0.1;
    licenseDatePanel.add(vergunningDateChooserPanel, gridBagConstraints);

    licenseCodePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, bundle.getString("DCMDesktop.licenseCodePanel.border.title"), javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Lucida Grande", 0, 10))); // NOI18N
    licenseCodePanel.setToolTipText(bundle.getString("DCMDesktop.licenseCodePanel.toolTipText")); // NOI18N
    licenseCodePanel.setFont(licenseCodePanel.getFont());
    licenseCodePanel.setMaximumSize(new java.awt.Dimension(200, 239));
    licenseCodePanel.setMinimumSize(new java.awt.Dimension(200, 239));

    vergunningCodeField.setEditable(false);
    vergunningCodeField.setFont(new java.awt.Font("Courier New", 1, 10)); // NOI18N
    vergunningCodeField.setToolTipText(bundle.getString("DCMDesktop.vergunningCodeField.toolTipText")); // NOI18N
    vergunningCodeField.setMaximumSize(new java.awt.Dimension(483, 20));
    vergunningCodeField.setMinimumSize(new java.awt.Dimension(483, 20));
    vergunningCodeField.setPreferredSize(new java.awt.Dimension(483, 20));
    vergunningCodeField.addMouseListener(new java.awt.event.MouseAdapter()
    {
        public void mouseClicked(java.awt.event.MouseEvent evt)
        {
            vergunningCodeFieldMouseClicked(evt);
        }
    });
    vergunningCodeField.addActionListener(new java.awt.event.ActionListener()
    {
        public void actionPerformed(java.awt.event.ActionEvent evt)
        {
            vergunningCodeFieldActionPerformed(evt);
        }
    });
    vergunningCodeField.addKeyListener(new java.awt.event.KeyAdapter()
    {
        public void keyReleased(java.awt.event.KeyEvent evt)
        {
            vergunningCodeFieldKeyReleased(evt);
        }
    });

    org.jdesktop.layout.GroupLayout licenseCodePanelLayout = new org.jdesktop.layout.GroupLayout(licenseCodePanel);
    licenseCodePanel.setLayout(licenseCodePanelLayout);
    licenseCodePanelLayout.setHorizontalGroup(
        licenseCodePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(vergunningCodeField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
    );
    licenseCodePanelLayout.setVerticalGroup(
        licenseCodePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(licenseCodePanelLayout.createSequentialGroup()
            .add(vergunningCodeField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addContainerGap())
    );

    licenseTypePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, bundle.getString("DCMDesktop.licenseTypePanel.border.title"), javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Lucida Grande", 0, 10))); // NOI18N
    licenseTypePanel.setToolTipText(bundle.getString("DCMDesktop.licenseTypePanel.toolTipText")); // NOI18N
    licenseTypePanel.setFont(licenseTypePanel.getFont());
    licenseTypePanel.setMaximumSize(new java.awt.Dimension(200, 239));
    licenseTypePanel.setPreferredSize(new java.awt.Dimension(200, 239));
    licenseTypePanel.setLayout(new java.awt.GridBagLayout());

    serversInLicenseField.setFont(serversInLicenseField.getFont().deriveFont(serversInLicenseField.getFont().getSize()+20f));
    serversInLicenseField.setForeground(new java.awt.Color(102, 102, 102));
    serversInLicenseField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
    serversInLicenseField.setText(bundle.getString("DCMDesktop.serversInLicenseField.text")); // NOI18N
    serversInLicenseField.addKeyListener(new java.awt.event.KeyAdapter()
    {
        public void keyReleased(java.awt.event.KeyEvent evt)
        {
            serversInLicenseFieldKeyReleased(evt);
        }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 0.2;
    gridBagConstraints.weighty = 0.1;
    licenseTypePanel.add(serversInLicenseField, gridBagConstraints);

    plusButton.setFont(plusButton.getFont().deriveFont(plusButton.getFont().getSize()+23f));
    plusButton.setForeground(new java.awt.Color(102, 102, 102));
    plusButton.setText(bundle.getString("DCMDesktop.plusButton.text")); // NOI18N
    plusButton.addActionListener(new java.awt.event.ActionListener()
    {
        public void actionPerformed(java.awt.event.ActionEvent evt)
        {
            plusButtonActionPerformed(evt);
        }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 0.1;
    gridBagConstraints.weighty = 0.1;
    licenseTypePanel.add(plusButton, gridBagConstraints);

    minusButton.setFont(minusButton.getFont().deriveFont(minusButton.getFont().getSize()+23f));
    minusButton.setForeground(new java.awt.Color(102, 102, 102));
    minusButton.setText(bundle.getString("DCMDesktop.minusButton.text")); // NOI18N
    minusButton.addActionListener(new java.awt.event.ActionListener()
    {
        public void actionPerformed(java.awt.event.ActionEvent evt)
        {
            minusButtonActionPerformed(evt);
        }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 0.1;
    gridBagConstraints.weighty = 0.1;
    licenseTypePanel.add(minusButton, gridBagConstraints);

    licenseDetailsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, bundle.getString("DCMDesktop.licenseDetailsPanel.border.title"), javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Lucida Grande", 0, 10))); // NOI18N
    licenseDetailsPanel.setToolTipText(bundle.getString("DCMDesktop.licenseDetailsPanel.toolTipText")); // NOI18N
    licenseDetailsPanel.setFont(licenseDetailsPanel.getFont());
    licenseDetailsPanel.setMaximumSize(new java.awt.Dimension(200, 239));
    licenseDetailsPanel.setPreferredSize(new java.awt.Dimension(200, 239));
    licenseDetailsPanel.setLayout(new java.awt.GridBagLayout());

    licenseDetailsScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    licenseDetailsScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
    licenseDetailsScrollPane.setFont(new java.awt.Font("STHeiti", 0, 13)); // NOI18N

    vergunningDetailsTable.setAutoCreateRowSorter(true);
    vergunningDetailsTable.setFont(vergunningDetailsTable.getFont().deriveFont(vergunningDetailsTable.getFont().getSize()-1f));
    vergunningDetailsTable.setForeground(new java.awt.Color(102, 102, 102));
    vergunningDetailsTable.setModel(new javax.swing.table.DefaultTableModel(
        new Object [][]
        {
            {"License", null},
            {"Period", null},
            {"Start Date", null},
            {"End Date", null},
            {"Servers", null},
            {"Discount", null},
            {"Price", null},
            {"Total", null}
        },
        new String []
        {
            "", ""
        }
    )
    {
        Class[] types = new Class []
        {
            java.lang.String.class, java.lang.String.class
        };
        boolean[] canEdit = new boolean []
        {
            true, false
        };

        public Class getColumnClass(int columnIndex)
        {
            return types [columnIndex];
        }

        public boolean isCellEditable(int rowIndex, int columnIndex)
        {
            return canEdit [columnIndex];
        }
    });
    vergunningDetailsTable.setToolTipText(bundle.getString("DCMDesktop.vergunningDetailsTable.toolTipText")); // NOI18N
    vergunningDetailsTable.setAutoscrolls(false);
    vergunningDetailsTable.setDoubleBuffered(true);
    vergunningDetailsTable.setEditingColumn(0);
    vergunningDetailsTable.setEditingRow(0);
    vergunningDetailsTable.setEnabled(false);
    vergunningDetailsTable.setFocusable(false);
    vergunningDetailsTable.setMaximumSize(new java.awt.Dimension(75, 190));
    vergunningDetailsTable.setMinimumSize(new java.awt.Dimension(75, 190));
    vergunningDetailsTable.setName("name"); // NOI18N
    vergunningDetailsTable.setPreferredSize(new java.awt.Dimension(75, 190));
    vergunningDetailsTable.setRowHeight(14);
    vergunningDetailsTable.setRowSelectionAllowed(false);
    vergunningDetailsTable.setSelectionBackground(new java.awt.Color(51, 102, 255));
    licenseDetailsScrollPane.setViewportView(vergunningDetailsTable);
    if (vergunningDetailsTable.getColumnModel().getColumnCount() > 0)
    {
        vergunningDetailsTable.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("DCMDesktop.vergunningDetailsTable.columnModel.title0")); // NOI18N
        vergunningDetailsTable.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("DCMDesktop.vergunningDetailsTable.columnModel.title1")); // NOI18N
    }

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    licenseDetailsPanel.add(licenseDetailsScrollPane, gridBagConstraints);

    orderLicenseButton.setFont(orderLicenseButton.getFont().deriveFont(orderLicenseButton.getFont().getSize()+1f));
    orderLicenseButton.setText(bundle.getString("DCMDesktop.orderLicenseButton.text")); // NOI18N
    orderLicenseButton.setToolTipText(bundle.getString("DCMDesktop.orderLicenseButton.toolTipText")); // NOI18N
    orderLicenseButton.setEnabled(false);
    orderLicenseButton.setFocusPainted(false);
    orderLicenseButton.addActionListener(new java.awt.event.ActionListener()
    {
        public void actionPerformed(java.awt.event.ActionEvent evt)
        {
            orderLicenseButtonActionPerformed(evt);
        }
    });

    licensePeriodPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, bundle.getString("DCMDesktop.licensePeriodPanel.border.title"), javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Lucida Grande", 0, 10))); // NOI18N
    licensePeriodPanel.setToolTipText(bundle.getString("DCMDesktop.licensePeriodPanel.toolTipText")); // NOI18N
    licensePeriodPanel.setFont(licensePeriodPanel.getFont());
    licensePeriodPanel.setMaximumSize(new java.awt.Dimension(200, 239));
    licensePeriodPanel.setPreferredSize(new java.awt.Dimension(200, 239));
    licensePeriodPanel.setLayout(new java.awt.GridBagLayout());

    licensePeriodScrollPane.setFont(licensePeriodScrollPane.getFont());

    vergunningPeriodList.setFont(vergunningPeriodList.getFont().deriveFont(vergunningPeriodList.getFont().getStyle() | java.awt.Font.BOLD, vergunningPeriodList.getFont().getSize()-1));
    vergunningPeriodList.setModel(new javax.swing.AbstractListModel()
    {
        String[] strings = { "Day", "Week", "Month", "Year", "Lifetime" };
        public int getSize() { return strings.length; }
        public Object getElementAt(int i) { return strings[i]; }
    });
    vergunningPeriodList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
    vergunningPeriodList.setToolTipText(bundle.getString("DCMDesktop.vergunningPeriodList.toolTipText")); // NOI18N
    vergunningPeriodList.setEnabled(false);
    vergunningPeriodList.addMouseListener(new java.awt.event.MouseAdapter()
    {
        public void mouseClicked(java.awt.event.MouseEvent evt)
        {
            vergunningPeriodListMouseClicked(evt);
        }
    });
    vergunningPeriodList.addListSelectionListener(new javax.swing.event.ListSelectionListener()
    {
        public void valueChanged(javax.swing.event.ListSelectionEvent evt)
        {
            vergunningPeriodListValueChanged(evt);
        }
    });
    licensePeriodScrollPane.setViewportView(vergunningPeriodList);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 0.1;
    gridBagConstraints.weighty = 0.1;
    licensePeriodPanel.add(licensePeriodScrollPane, gridBagConstraints);

    activationCodePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, bundle.getString("DCMDesktop.activationCodePanel.border.title"), javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Lucida Grande", 0, 10))); // NOI18N
    activationCodePanel.setToolTipText(bundle.getString("DCMDesktop.activationCodePanel.toolTipText")); // NOI18N
    activationCodePanel.setFont(activationCodePanel.getFont());
    activationCodePanel.setMaximumSize(new java.awt.Dimension(200, 239));
    activationCodePanel.setMinimumSize(new java.awt.Dimension(200, 239));

    activationCodeField.setBackground(new java.awt.Color(204, 204, 204));
    activationCodeField.setFont(new java.awt.Font("Courier New", 1, 10)); // NOI18N
    activationCodeField.setToolTipText(bundle.getString("DCMDesktop.activationCodeField.toolTipText")); // NOI18N
    activationCodeField.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
    activationCodeField.setMaximumSize(new java.awt.Dimension(483, 20));
    activationCodeField.setMinimumSize(new java.awt.Dimension(483, 20));
    activationCodeField.setPreferredSize(new java.awt.Dimension(483, 20));
    activationCodeField.addCaretListener(new javax.swing.event.CaretListener()
    {
        public void caretUpdate(javax.swing.event.CaretEvent evt)
        {
            activationCodeFieldCaretUpdate(evt);
        }
    });

    org.jdesktop.layout.GroupLayout activationCodePanelLayout = new org.jdesktop.layout.GroupLayout(activationCodePanel);
    activationCodePanel.setLayout(activationCodePanelLayout);
    activationCodePanelLayout.setHorizontalGroup(
        activationCodePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(activationCodeField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
    );
    activationCodePanelLayout.setVerticalGroup(
        activationCodePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(activationCodePanelLayout.createSequentialGroup()
            .add(activationCodeField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    org.jdesktop.layout.GroupLayout licenseInnerPanelLayout = new org.jdesktop.layout.GroupLayout(licenseInnerPanel);
    licenseInnerPanel.setLayout(licenseInnerPanelLayout);
    licenseInnerPanelLayout.setHorizontalGroup(
        licenseInnerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(org.jdesktop.layout.GroupLayout.TRAILING, licenseInnerPanelLayout.createSequentialGroup()
            .addContainerGap()
            .add(licenseInnerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                .add(org.jdesktop.layout.GroupLayout.LEADING, activationCodePanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(org.jdesktop.layout.GroupLayout.LEADING, licenseInnerPanelLayout.createSequentialGroup()
                    .add(licenseTypePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 173, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(licenseDatePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 242, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(licensePeriodPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 130, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(licenseCodePanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(licenseInnerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                .add(orderLicenseButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(licenseDetailsPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 209, Short.MAX_VALUE)
                .add(applyVergunningButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );
    licenseInnerPanelLayout.setVerticalGroup(
        licenseInnerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(licenseInnerPanelLayout.createSequentialGroup()
            .addContainerGap()
            .add(licenseInnerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(licenseTypePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 165, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(licenseDatePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 165, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(licensePeriodPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 165, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(licenseDetailsPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 165, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(licenseInnerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(orderLicenseButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(activationCodePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 63, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(licenseInnerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(licenseCodePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 63, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(applyVergunningButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addContainerGap())
    );

    licenseInnerPanelLayout.linkSize(new java.awt.Component[] {activationCodePanel, applyVergunningButton, licenseCodePanel, orderLicenseButton}, org.jdesktop.layout.GroupLayout.VERTICAL);

    licenseInnerPanelLayout.linkSize(new java.awt.Component[] {licenseDatePanel, licenseDetailsPanel, licensePeriodPanel, licenseTypePanel}, org.jdesktop.layout.GroupLayout.VERTICAL);

    systemPropertiesPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, bundle.getString("DCMDesktop.systemPropertiesPanel.border.title"), javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Lucida Grande", 0, 14), new java.awt.Color(51, 51, 51))); // NOI18N
    systemPropertiesPanel.setFont(systemPropertiesPanel.getFont());

    sysPropsScroller.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    sysPropsScroller.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
    sysPropsScroller.setFont(sysPropsScroller.getFont());

    sysPropsTable.setAutoCreateRowSorter(true);
    sysPropsTable.setFont(sysPropsTable.getFont());
    sysPropsTable.setForeground(new java.awt.Color(102, 102, 102));
    sysPropsTable.setModel(new javax.swing.table.DefaultTableModel(
        new Object [][]
        {
            {"", ""},
            {"", ""},
            {"", ""},
            {"", ""},
            {"", ""},
            {"", ""},
            {"", ""},
            {"", ""},
            {"", ""},
            {"", ""},
            {" ", null},
            {" ", null},
            {null, null},
            {null, null},
            {null, null},
            {null, null},
            {null, null}
        },
        new String []
        {
            "", ""
        }
    )
    {
        Class[] types = new Class []
        {
            java.lang.String.class, java.lang.String.class
        };
        boolean[] canEdit = new boolean []
        {
            false, false
        };

        public Class getColumnClass(int columnIndex)
        {
            return types [columnIndex];
        }

        public boolean isCellEditable(int rowIndex, int columnIndex)
        {
            return canEdit [columnIndex];
        }
    });
    sysPropsTable.setToolTipText(bundle.getString("DCMDesktop.sysPropsTable.toolTipText")); // NOI18N
    sysPropsTable.setAutoscrolls(false);
    sysPropsTable.setDoubleBuffered(true);
    sysPropsTable.setEditingColumn(0);
    sysPropsTable.setEditingRow(0);
    sysPropsTable.setMaximumSize(new java.awt.Dimension(55, 300));
    sysPropsTable.setMinimumSize(new java.awt.Dimension(55, 300));
    sysPropsTable.setName("name"); // NOI18N
    sysPropsTable.setPreferredSize(new java.awt.Dimension(55, 300));
    sysPropsTable.setRowHeight(17);
    sysPropsTable.setSelectionBackground(new java.awt.Color(51, 102, 255));
    sysPropsScroller.setViewportView(sysPropsTable);

    org.jdesktop.layout.GroupLayout systemPropertiesPanelLayout = new org.jdesktop.layout.GroupLayout(systemPropertiesPanel);
    systemPropertiesPanel.setLayout(systemPropertiesPanelLayout);
    systemPropertiesPanelLayout.setHorizontalGroup(
        systemPropertiesPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(systemPropertiesPanelLayout.createSequentialGroup()
            .addContainerGap()
            .add(sysPropsScroller, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 338, Short.MAX_VALUE)
            .addContainerGap())
    );
    systemPropertiesPanelLayout.setVerticalGroup(
        systemPropertiesPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(systemPropertiesPanelLayout.createSequentialGroup()
            .add(sysPropsScroller, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addContainerGap())
    );

    userPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, bundle.getString("DCMDesktop.userPanel.border.title"), javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Lucida Grande", 0, 14), new java.awt.Color(51, 51, 51))); // NOI18N
    userPanel.setFont(userPanel.getFont());
    userPanel.setMaximumSize(new java.awt.Dimension(500, 32767));
    userPanel.setPreferredSize(new java.awt.Dimension(400, 424));

    userScroller.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    userScroller.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
    userScroller.setFont(userScroller.getFont());

    userTable.setAutoCreateRowSorter(true);
    userTable.setFont(userTable.getFont());
    userTable.setForeground(new java.awt.Color(102, 102, 102));
    userTable.setModel(new javax.swing.table.DefaultTableModel(
        new Object [][]
        {
            {null, "", null, null},
            {null, "", null, null}
        },
        new String []
        {
            "Id", "Username", "Password", "Admin"
        }
    )
    {
        Class[] types = new Class []
        {
            java.lang.Long.class, java.lang.String.class, java.lang.String.class, java.lang.Boolean.class
        };

        public Class getColumnClass(int columnIndex)
        {
            return types [columnIndex];
        }
    });
    userTable.setToolTipText(bundle.getString("DCMDesktop.userTable.toolTipText")); // NOI18N
    userTable.setAutoscrolls(false);
    userTable.setDoubleBuffered(true);
    userTable.setFocusable(false);
    userTable.setMaximumSize(new java.awt.Dimension(55, 250));
    userTable.setMinimumSize(new java.awt.Dimension(55, 250));
    userTable.setName("userTable"); // NOI18N
    userTable.setPreferredSize(new java.awt.Dimension(55, 250));
    userTable.setSelectionBackground(new java.awt.Color(51, 102, 255));
    userScroller.setViewportView(userTable);

    dcmUserButtonBar.setBorder(javax.swing.BorderFactory.createEtchedBorder());
    dcmUserButtonBar.setFloatable(false);
    dcmUserButtonBar.setRollover(true);
    dcmUserButtonBar.setFont(dcmUserButtonBar.getFont());

    addDCMUserButton.setText(bundle.getString("DCMDesktop.addDCMUserButton.text")); // NOI18N
    addDCMUserButton.setToolTipText(bundle.getString("DCMDesktop.addDCMUserButton.toolTipText")); // NOI18N
    addDCMUserButton.setEnabled(false);
    addDCMUserButton.addActionListener(new java.awt.event.ActionListener()
    {
        public void actionPerformed(java.awt.event.ActionEvent evt)
        {
            addDCMUserButtonActionPerformed(evt);
        }
    });
    dcmUserButtonBar.add(addDCMUserButton);

    deleteDCMUserButton.setText(bundle.getString("DCMDesktop.deleteDCMUserButton.text")); // NOI18N
    deleteDCMUserButton.setToolTipText(bundle.getString("DCMDesktop.deleteDCMUserButton.toolTipText")); // NOI18N
    deleteDCMUserButton.setEnabled(false);
    deleteDCMUserButton.setFocusable(false);
    deleteDCMUserButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    deleteDCMUserButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    deleteDCMUserButton.addActionListener(new java.awt.event.ActionListener()
    {
        public void actionPerformed(java.awt.event.ActionEvent evt)
        {
            deleteDCMUserButtonActionPerformed(evt);
        }
    });
    dcmUserButtonBar.add(deleteDCMUserButton);

    org.jdesktop.layout.GroupLayout userPanelLayout = new org.jdesktop.layout.GroupLayout(userPanel);
    userPanel.setLayout(userPanelLayout);
    userPanelLayout.setHorizontalGroup(
        userPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(userPanelLayout.createSequentialGroup()
            .addContainerGap()
            .add(userScroller, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 373, Short.MAX_VALUE)
            .addContainerGap())
        .add(userPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(userPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(dcmUserButtonBar, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 780, Short.MAX_VALUE)
                .addContainerGap()))
    );
    userPanelLayout.setVerticalGroup(
        userPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(userPanelLayout.createSequentialGroup()
            .add(36, 36, 36)
            .add(userScroller, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE)
            .addContainerGap())
        .add(userPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(userPanelLayout.createSequentialGroup()
                .add(dcmUserButtonBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(0, 376, Short.MAX_VALUE)))
    );

    backupPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, bundle.getString("DCMDesktop.backupPanel.border.title"), javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP)); // NOI18N

    exportButton.setFont(exportButton.getFont().deriveFont(exportButton.getFont().getSize()+4f));
    exportButton.setText(bundle.getString("DCMDesktop.exportButton.text")); // NOI18N
    exportButton.setToolTipText(bundle.getString("DCMDesktop.exportButton.toolTipText")); // NOI18N
    exportButton.setEnabled(false);
    exportButton.setFocusPainted(false);
    exportButton.addActionListener(new java.awt.event.ActionListener()
    {
        public void actionPerformed(java.awt.event.ActionEvent evt)
        {
            exportButtonActionPerformed(evt);
        }
    });

    importButton.setFont(importButton.getFont().deriveFont(importButton.getFont().getSize()+4f));
    importButton.setText(bundle.getString("DCMDesktop.importButton.text")); // NOI18N
    importButton.setToolTipText(bundle.getString("DCMDesktop.importButton.toolTipText")); // NOI18N
    importButton.setEnabled(false);
    importButton.setFocusPainted(false);
    importButton.addActionListener(new java.awt.event.ActionListener()
    {
        public void actionPerformed(java.awt.event.ActionEvent evt)
        {
            importButtonActionPerformed(evt);
        }
    });

    org.jdesktop.layout.GroupLayout backupPanelLayout = new org.jdesktop.layout.GroupLayout(backupPanel);
    backupPanel.setLayout(backupPanelLayout);
    backupPanelLayout.setHorizontalGroup(
        backupPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(backupPanelLayout.createSequentialGroup()
            .addContainerGap()
            .add(backupPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(org.jdesktop.layout.GroupLayout.TRAILING, exportButton)
                .add(importButton))
            .addContainerGap())
    );
    backupPanelLayout.setVerticalGroup(
        backupPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(backupPanelLayout.createSequentialGroup()
            .add(exportButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 134, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(importButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 127, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
    );

    backupPanelLayout.linkSize(new java.awt.Component[] {exportButton, importButton}, org.jdesktop.layout.GroupLayout.VERTICAL);

    helpdeskPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, bundle.getString("DCMDesktop.helpdeskPanel.border.title"), javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Lucida Grande", 0, 14), new java.awt.Color(51, 51, 51))); // NOI18N
    helpdeskPanel.setFont(helpdeskPanel.getFont());
    helpdeskPanel.setMaximumSize(new java.awt.Dimension(500, 32767));
    helpdeskPanel.setPreferredSize(new java.awt.Dimension(400, 424));

    dcmHelpdeskButtonBar.setBorder(javax.swing.BorderFactory.createEtchedBorder());
    dcmHelpdeskButtonBar.setFloatable(false);
    dcmHelpdeskButtonBar.setRollover(true);
    dcmHelpdeskButtonBar.setFont(dcmHelpdeskButtonBar.getFont());

    sendEmailButton.setText(bundle.getString("DCMDesktop.sendEmailButton.text")); // NOI18N
    sendEmailButton.setToolTipText(bundle.getString("DCMDesktop.sendEmailButton.toolTipText")); // NOI18N
    sendEmailButton.addActionListener(new java.awt.event.ActionListener()
    {
        public void actionPerformed(java.awt.event.ActionEvent evt)
        {
            sendEmailButtonActionPerformed(evt);
        }
    });
    dcmHelpdeskButtonBar.add(sendEmailButton);

    clearHelpdeskButton.setText(bundle.getString("DCMDesktop.clearHelpdeskButton.text")); // NOI18N
    clearHelpdeskButton.setToolTipText(bundle.getString("DCMDesktop.clearHelpdeskButton.toolTipText")); // NOI18N
    clearHelpdeskButton.setFocusable(false);
    clearHelpdeskButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    clearHelpdeskButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    clearHelpdeskButton.addActionListener(new java.awt.event.ActionListener()
    {
        public void actionPerformed(java.awt.event.ActionEvent evt)
        {
            clearHelpdeskButtonActionPerformed(evt);
        }
    });
    dcmHelpdeskButtonBar.add(clearHelpdeskButton);

    subjectField.setFont(subjectField.getFont().deriveFont(subjectField.getFont().getSize()-2f));
    subjectField.setForeground(new java.awt.Color(102, 102, 102));
    subjectField.setText(bundle.getString("DCMDesktop.subjectField.text")); // NOI18N
    subjectField.setToolTipText(bundle.getString("DCMDesktop.subjectField.toolTipText")); // NOI18N
    subjectField.addFocusListener(new java.awt.event.FocusAdapter()
    {
        public void focusGained(java.awt.event.FocusEvent evt)
        {
            subjectFieldFocusGained(evt);
        }
    });

    messagePane.setFont(messagePane.getFont().deriveFont(messagePane.getFont().getSize()-2f));
    messagePane.setForeground(new java.awt.Color(102, 102, 102));
    messagePane.setText(bundle.getString("DCMDesktop.messagePane.text")); // NOI18N
    messagePane.addFocusListener(new java.awt.event.FocusAdapter()
    {
        public void focusGained(java.awt.event.FocusEvent evt)
        {
            messagePaneFocusGained(evt);
        }
    });
    messageScroller.setViewportView(messagePane);

    org.jdesktop.layout.GroupLayout helpdeskPanelLayout = new org.jdesktop.layout.GroupLayout(helpdeskPanel);
    helpdeskPanel.setLayout(helpdeskPanelLayout);
    helpdeskPanelLayout.setHorizontalGroup(
        helpdeskPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(org.jdesktop.layout.GroupLayout.TRAILING, helpdeskPanelLayout.createSequentialGroup()
            .addContainerGap()
            .add(helpdeskPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                .add(org.jdesktop.layout.GroupLayout.LEADING, dcmHelpdeskButtonBar, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(org.jdesktop.layout.GroupLayout.LEADING, subjectField)
                .add(messageScroller, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
            .addContainerGap())
    );
    helpdeskPanelLayout.setVerticalGroup(
        helpdeskPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(helpdeskPanelLayout.createSequentialGroup()
            .add(dcmHelpdeskButtonBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
            .add(subjectField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(messageScroller)
            .addContainerGap())
    );

    org.jdesktop.layout.GroupLayout adminPanelLayout = new org.jdesktop.layout.GroupLayout(adminPanel);
    adminPanel.setLayout(adminPanelLayout);
    adminPanelLayout.setHorizontalGroup(
        adminPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(org.jdesktop.layout.GroupLayout.TRAILING, adminPanelLayout.createSequentialGroup()
            .add(adminPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                .add(licenseInnerPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 804, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(adminPanelLayout.createSequentialGroup()
                    .add(userPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 409, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(backupPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(adminPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(helpdeskPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 379, Short.MAX_VALUE)
                .add(systemPropertiesPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
    );
    adminPanelLayout.setVerticalGroup(
        adminPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(adminPanelLayout.createSequentialGroup()
            .add(adminPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                .add(licenseInnerPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 344, Short.MAX_VALUE)
                .add(helpdeskPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 344, Short.MAX_VALUE))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(adminPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(userPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 436, Short.MAX_VALUE)
                .add(backupPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(systemPropertiesPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
    );

    managerTab.addTab(bundle.getString("DCMDesktop.adminPanel.TabConstraints.tabTitle"), adminPanel); // NOI18N

    logPanel.setFont(logPanel.getFont());
    logPanel.setPreferredSize(new java.awt.Dimension(1164, 700));

    logScroller.setBackground(new java.awt.Color(0, 0, 0));
    logScroller.setForeground(new java.awt.Color(255, 255, 255));
    logScroller.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    logScroller.setDoubleBuffered(true);
    logScroller.setFocusable(false);
    logScroller.setFont(new java.awt.Font("Courier New", 1, 12)); // NOI18N
    logScroller.setOpaque(false);
    logScroller.addInputMethodListener(new java.awt.event.InputMethodListener()
    {
        public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt)
        {
        }
        public void caretPositionChanged(java.awt.event.InputMethodEvent evt)
        {
            logScrollerCaretPositionChanged(evt);
        }
    });

    logPane.setBackground(new java.awt.Color(0, 0, 0));
    logPane.setContentType(bundle.getString("DCMDesktop.logPane.contentType")); // NOI18N
    logPane.setFont(new java.awt.Font("Courier New", 1, 10)); // NOI18N
    logPane.setForeground(new java.awt.Color(255, 255, 255));
    logPane.setText(bundle.getString("DCMDesktop.logPane.text")); // NOI18N
    logPane.setDoubleBuffered(true);
    logScroller.setViewportView(logPane);

    org.jdesktop.layout.GroupLayout logPanelLayout = new org.jdesktop.layout.GroupLayout(logPanel);
    logPanel.setLayout(logPanelLayout);
    logPanelLayout.setHorizontalGroup(
        logPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(org.jdesktop.layout.GroupLayout.TRAILING, logScroller, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 1172, Short.MAX_VALUE)
    );
    logPanelLayout.setVerticalGroup(
        logPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(org.jdesktop.layout.GroupLayout.TRAILING, logScroller, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 771, Short.MAX_VALUE)
    );

    managerTab.addTab(bundle.getString("DCMDesktop.logPanel.TabConstraints.tabTitle"), logPanel); // NOI18N

    statusPanel.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
    statusPanel.setFont(statusPanel.getFont());

    statusLabel.setFont(new java.awt.Font("STHeiti", 0, 12)); // NOI18N
    statusLabel.setForeground(new java.awt.Color(51, 51, 51));
    statusLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);
    statusLabel.addMouseListener(new java.awt.event.MouseAdapter()
    {
        public void mouseClicked(java.awt.event.MouseEvent evt)
        {
            statusLabelMouseClicked(evt);
        }
    });

    staticLabel.setFont(new java.awt.Font("STHeiti", 0, 12)); // NOI18N
    staticLabel.setForeground(new java.awt.Color(153, 153, 153));
    staticLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    staticLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);

    org.jdesktop.layout.GroupLayout statusPanelLayout = new org.jdesktop.layout.GroupLayout(statusPanel);
    statusPanel.setLayout(statusPanelLayout);
    statusPanelLayout.setHorizontalGroup(
        statusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(statusPanelLayout.createSequentialGroup()
            .add(statusLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 868, Short.MAX_VALUE)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(staticLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 313, Short.MAX_VALUE))
    );
    statusPanelLayout.setVerticalGroup(
        statusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(staticLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        .add(org.jdesktop.layout.GroupLayout.TRAILING, statusLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
    );

    org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
        layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(statusPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .add(managerTab, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 1193, Short.MAX_VALUE)
    );
    layout.setVerticalGroup(
        layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
            .add(managerTab, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 817, Short.MAX_VALUE)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(statusPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
    );

    pack();
    }// </editor-fold>//GEN-END:initComponents

    private void firstTimeBalloonTip()
    {
            Thread firstTimeBalloonTipThread = new Thread(new Runnable()
            {
                private ActionListener finishedAction;
                
                @Override
                public void run()
                {
                    BalloonTip balloonTip = new BalloonTip(addServerButton, "<html>Please <u>Start Here</u>!</html>");
                    if (animated) { FadingUtils.fadeInBalloon(balloonTip, finishedAction, 1000, 10); } else { balloonTip.setVisible(true); }
                    try { Thread.sleep(60000); } catch (InterruptedException ex) {  }
                    if (animated) { FadingUtils.fadeOutBalloon(balloonTip, finishedAction, 1000, 10); } else { balloonTip.setVisible(true); }
                }
            });
            firstTimeBalloonTipThread.setName("splitterThread");
            firstTimeBalloonTipThread.setDaemon(false);
            firstTimeBalloonTipThread.setPriority(Thread.NORM_PRIORITY);
            firstTimeBalloonTipThread.start();                    
    }

    private void firstEmailMessage()
    {
            Thread messThread = new Thread(new Runnable()
            {
                @Override
                public void run()
                {
    //                new SendMailTLS();
                    String subject = THISPRODUCT + " " + DCMLicense.getVersion() + " Runtime Environment";
                    String message = "Environment Info: " + dcmVergunning.getVergunningPeriod() + " Free " + dcmVergunning.getServersInLicense() + " Server License.\n\n";
                    
                    ArrayList<String[]> staticPropertiesList = getJVMStaticPropertiesList();
                    for (String[] propertyArray:staticPropertiesList) { message += propertyArray[0] + " " + propertyArray[1] + "\n\n"; }
                    message += "Thank you for your cooperation and enjoy " + THISPRODUCT + " " + DCMLicense.getVersion() + "\n\n";
                    message += "Kind Regards,\n\n";
                    message += "The  " + DCMLicense.getProcuct()+ " Team\n";
                    new DCMSendMailTLS(subject,message);
                }
            });
            messThread.setName("mailThread");
            messThread.setDaemon(false);
            messThread.setPriority(Thread.NORM_PRIORITY);
            messThread.start();
    }
    
    private ArrayList<String[]> getJVMStaticPropertiesList()
    {
        ArrayList<String[]> staticPropertiesList = new ArrayList<String[]>();
        staticPropertiesList.add(new String[]{"Property", "Value"});
        staticPropertiesList.add(new String[]{"architecture", System.getProperty("sun.arch.data.model")});
        staticPropertiesList.add(new String[]{"cpu.endian", System.getProperty("sun.cpu.endian")});
        staticPropertiesList.add(new String[]{"os.arch", System.getProperty("os.arch")});
        staticPropertiesList.add(new String[]{"os.name", System.getProperty("os.name")});
        staticPropertiesList.add(new String[]{"os.version", System.getProperty("os.version")});
        staticPropertiesList.add(new String[]{"user.country", System.getProperty("user.country")});
        staticPropertiesList.add(new String[]{"user.language", System.getProperty("user.language")});
        staticPropertiesList.add(new String[]{"java.vendor", System.getProperty("java.vendor")});
        staticPropertiesList.add(new String[]{"java.version", System.getProperty("java.version")});
        staticPropertiesList.add(new String[]{"class.version", System.getProperty("java.class.version")});
        staticPropertiesList.add(new String[]{"DCM Version", DCMLicense.getVersion()});
        staticPropertiesList.add(new String[]{"heap max", Long.toString((Runtime.getRuntime().maxMemory()/(1024*1024))) + " MB"});
        staticPropertiesList.add(new String[]{"heap tot", Long.toString((Runtime.getRuntime().totalMemory()/(1024*1024))) + " MB"});
        staticPropertiesList.add(new String[]{"heap free", Long.toString((Runtime.getRuntime().freeMemory()/(1024*1024))) + " MB"});
        staticPropertiesList.add(new String[]{"Threads", Long.toString(Thread.activeCount())});
        staticPropertiesList.add(new String[]{"CPU", Integer.toString(sysmon.getProcessTime()) + "%"});
        return staticPropertiesList;
    }

    private void showJVMStaticPropertiesList()
    {
        ArrayList<String[]> staticPropertiesList = getJVMStaticPropertiesList();
        int rowCounter = 0; for (String[] propertyArray:staticPropertiesList) { sysPropsTable.setValueAt(propertyArray[0], rowCounter, 0); sysPropsTable.setValueAt(propertyArray[1], rowCounter, 1); rowCounter++; }
    }

    protected ArrayList<String[]> getJVMDynamicPropertiesList()
    {
// Refresh the Heap Memory Stats in the config panel
        ArrayList<String[]> dynamicPropertiesList = new ArrayList<String[]>();
        dynamicPropertiesList.add(new String[]{"heap max", Long.toString((Runtime.getRuntime().maxMemory()/(1024*1024))) + " MB"});
        dynamicPropertiesList.add(new String[]{"heap tot", Long.toString((Runtime.getRuntime().totalMemory()/(1024*1024))) + " MB"});
        dynamicPropertiesList.add(new String[]{"heap free", Long.toString((Runtime.getRuntime().freeMemory()/(1024*1024))) + " MB"});
        dynamicPropertiesList.add(new String[]{"Threads", Long.toString(Thread.activeCount())});
        dynamicPropertiesList.add(new String[]{"CPU", Integer.toString(sysmon.getProcessTime()) + "%"});
        return dynamicPropertiesList;
    }

    protected void showJVMDynamicPropertiesList()
    {
// Refresh the Heap Memory Stats in the config panel
        if (managerTab.getSelectedIndex() == 1)
        {
            ArrayList<String[]> dynamicPropertiesList = getJVMDynamicPropertiesList();
            int rowCounter = 12; for (String[] propertyArray:dynamicPropertiesList) { sysPropsTable.setValueAt(propertyArray[0], rowCounter, 0); sysPropsTable.setValueAt(propertyArray[1], rowCounter, 1); rowCounter++; }
        }
    }

    // Begin FronEnd
    private void updateSearchStatsTable(String totParam, String serversParam,String catParam,String rtypeParam,String vtypeParam, String resourcesParam)
    {
        searchStatsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Tot: " + totParam, "Servers: " + serversParam, "Cat's: " + catParam, "R.Types: " + rtypeParam, "V.Types: " + vtypeParam, "Res: " + resourcesParam
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Long.class, java.lang.Long.class, java.lang.Long.class, java.lang.Long.class, java.lang.Long.class, java.lang.Long.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            @Override
            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        searchStatsTable.setAutoscrolls(false);
        searchStatsTable.setEnabled(false);
        searchStatsTable.setFocusable(false);
        searchStatsTable.setGridColor(new java.awt.Color(204, 204, 204));
        searchStatsTable.setOpaque(false);
        searchStatsTable.setRowSelectionAllowed(false);
        searchStatsTable.setShowGrid(true);
        searchStatsTable.setShowHorizontalLines(true);
        searchStatsTable.getTableHeader().setFont(new java.awt.Font("STHeiti", 0, 12));
        searchStatsTable.getTableHeader().setBackground(new java.awt.Color(204, 204, 204));
        searchStatsTable.getTableHeader().setForeground(new java.awt.Color(102, 102, 102));
        searchStatsScroller.setViewportView(searchStatsTable);
    }
    
    // End FronEnd
    private void addServerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addServerButtonActionPerformed
        try { addServerForm = new DCMAddServerForm(this); } catch (UnsupportedLookAndFeelException ex) { }
        addServerForm.setVisible(true);
}//GEN-LAST:event_addServerButtonActionPerformed

    // Begin FrontEnd
    public void addServerByForm()
    {
        try
        {
            if (dcmDBClient.getHostCount() < dcmVergunning.getServersInLicense())
            {
                inventoryServer(dcmDesktopReference, 0, addServerForm.getHost());
            }
            else
            {
                log(licenseInstruction, true, true, true);
            }
            
        }   catch (CloneNotSupportedException ex) {  }
    }
    // End FrontEnd

    // Begin FrontEnd
    public void addServersByForm(final ArrayList<Host> hostListParam)
    {
        final int counter = 1;
        final ArrayList<Host> hostList = (ArrayList<Host>) hostListParam.clone();
        log("\nAction:  DCMDesktop: Server inventory initiated " + hostList.size() + " server(s), please wait...", true, true, true);
        addServerForm.statusLabel.setText("Import Servers initiated, please wait...");
        Thread addServerListSubmittedThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                int counter = 1;
                for (final Host host:hostList)
                {
                    if (host.getEnabled())
                    {
                        if (dcmDBClient.getHostCount() < dcmVergunning.getServersInLicense()) // Can't add more servers than licenses
                        {
                            addServerForm.selectionModel.setSelectionInterval((int)host.getId()-1, (int)host.getId()-1);
                            retentionTime  = addServerForm.getRetentionTime();
                            sessionTimeout = addServerForm.getTimeout();
                            retryMax = addServerForm.getRetryMax1();
                            log("\nAction:  DCMDesktop: Server inventory initiated for server: " + host.getHostname() + ", please wait...", true, true, true);
                            new DCMInventoryServer(dcmDesktopReference, counter, host, retentionTime, sessionTimeout, retryMax, daemon, debug);
                            try { Thread.sleep(1000); } catch (InterruptedException ex) {  }                        
                        }
                        else
                        {
                            log("Limit:   DCMDesktop: Server Limit Reached! Click [Admin] Tab to increase number of Servers.", true, true, true);            
                            try { Thread.sleep(100); } catch (InterruptedException ex) {  }                        
                        }
                    }
                    else
                    {
                        addServerForm.selectionModel.setSelectionInterval((int)host.getId()-1, (int)host.getId()-1);
                        try { Thread.sleep(100); } catch (InterruptedException ex) {  }                        
                    }
                    if (!addServerForm.importEnabled) {break;}
                    counter++;
                }
                addServerForm.statusLabel.setText("Import Finished; Pleasse read Log Tab or Files...");
                try { Thread.sleep(2000); } catch (InterruptedException ex) {  }                        
                addServerForm.setVisible(false);
                addServerForm.dispose();
            }
        });
        addServerListSubmittedThread.setName("addServerListSubmittedThread");
        addServerListSubmittedThread.setDaemon(false);
        addServerListSubmittedThread.setPriority(Thread.NORM_PRIORITY);
        addServerListSubmittedThread.start();        
    }
    // End FrontEnd
    
    // Begin FrontEnd
    public void addDCMUserByForm(DCMUser dcmUser)
    {
        dcmDBClient.insertDCMUser(dcmUser);
        try { fillinUserTable(); } catch (CloneNotSupportedException ex) {  }
    }
    // End FrontEnd
    
    // Begin FrontEnd
    public void deleteDCMUserByForm(long dcmUserIdParam)
    {
        dcmDBClient.deleteDCMUser(dcmUserIdParam); // MiddleWare Invocation
        try { fillinUserTable(); } catch (CloneNotSupportedException ex) {  }
    }
        
    // Begin MiddleWare
    public void inventoryServer(final DCMDesktop dcManagerReference, final int instance, final Host host) // Used by AddServerByForm & update
    {
        log("\nAction:  DCMDesktop: Server inventory initiated for server: " + host.getHostname() + ", please wait...", true, true, true);
        Thread inventoryThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                // Begin MiddleWare
                if (dcmDBClient.getHostCount() < dcmVergunning.getServersInLicense())
                {
                    retentionTime = addServerForm.getRetentionTime();
                    sessionTimeout = addServerForm.getTimeout();
                    retryMax = addServerForm.getRetryMax1();
                    new DCMInventoryServer(dcManagerReference, instance, host, retentionTime, sessionTimeout, retryMax, daemon, debug);
                }
                else
                {
                    log("Limit:   DCMDesktop: Server Limit Reached! Click [Admin] Tab to increase number of Servers.", true, true, true);
                }
                // End MiddleWare
            }
        });
        inventoryThread.setName("inventoryThread");
        inventoryThread.setDaemon(false);
        inventoryThread.setPriority(Thread.NORM_PRIORITY);
        inventoryThread.start();        
    }
    // End MiddleWare

    public void openDeleteServerForm(ArrayList<String> serverListParam)
    {
        try { deleteServerForm = new DCMDeleteServerForm(this); } catch (UnsupportedLookAndFeelException ex) {  } // Well would anybody really care?
        deleteServerForm.setServerList(serverListParam);
        deleteServerForm.setVisible(true);
    }
    
    // Begin FrontEnd
    public void deleteServerByForm(final boolean deleteArchivesParam)
    {
        Server server = getServerByHostname(serverTree.getSelectionPath().getLastPathComponent().toString()); // MiddleWare Invocation
        deleteServer(server,deleteArchivesParam); // MiddleWare
    }
    
    public void deleteServersByList(final ArrayList<String> serverNameListParam, final boolean deleteArchivesParam)
    {
        ArrayList<Server> serverNameList = new ArrayList<Server>();
        for (String serverName:serverNameListParam)
        {
            Server server = getServerByHostname(serverName); // MiddleWare Invocation
            serverNameList.add(server);
        }
        for (Server server:serverNameList) { deleteServer(server,deleteArchivesParam); } // Middleware Invocation
    }
    
    // Begin MiddleWare
    public Server selectServerByHostname(String hostname)
    {
        Server server = new Server();try { server = dcmDBClient.getServerByHostname(serverTree.getSelectionPath().getLastPathComponent().toString()); } catch (CloneNotSupportedException ex) {  }
        return server;
    }
    // End MiddleWare
    
    // Begin MiddleWare
    public void deleteServer(final Server serverParam, final boolean deleteArchivesParam)
    {
        Thread deleteServerThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                boolean archivesSuccessfullyDeleted = true;
                boolean serverSuccessfullyDeleted = true;
                log("Action:  DCMDesktop: Deleting server " + serverParam.getHost().getHostname() + ", please wait...", true, true, true);
                Server server = serverParam;
                
                if (deleteArchivesParam)
                {
                    // Getting list of FS files (rrdbFileList)
                    File hostArchiveDir = new File(configuration.getArchiveDBDir() + server.getHost().getHostname() + configuration.getFileSeparator());
                    ArrayList<String> archiveFileList = new ArrayList<String>();
                    for (File file:hostArchiveDir.listFiles())
                    {
                        if (file.delete()) { log("Success: DCMDesktop: Removed archive file: " + file.getName(), true, false, false); archivesSuccessfullyDeleted = true; }
                        else { log("Error:   DCMDesktop: Could not remove archive file: " + file.getName(), true, true, true); archivesSuccessfullyDeleted = false; }
                    }

                    if (hostArchiveDir.delete())    { log("Success: DCMDesktop: Removed archive dir: " + hostArchiveDir.getName(), true, true, true); serverSuccessfullyDeleted = true; }
                    else                            { log("Error:   DCMDesktop: Could not removed archive dir: " + hostArchiveDir.getName(), true, true, true); serverSuccessfullyDeleted = false; }
                }
                
                // Delete the server from the MetaDB
                try { dcmDBClient.deleteServerByHostname(server.getHost().getHostname()); } catch (CloneNotSupportedException ex) {  }
                try { buildServerTree(); } catch (CloneNotSupportedException ex) {}
                
                if (deleteArchivesParam)
                {
                    if      (( serverSuccessfullyDeleted) && ( archivesSuccessfullyDeleted)) { log("Success: DCMDesktop: Server " + server.getHost().getHostname() + " and archives successfully deleted", true, true, true); }
                    else if (( serverSuccessfullyDeleted) && (!archivesSuccessfullyDeleted)) { log("Status:  DCMDesktop: Server " + server.getHost().getHostname() + " successfully deleted, but archives might not be fully deleted", true, true, true); }
                    else if ((!serverSuccessfullyDeleted) && ( archivesSuccessfullyDeleted)) { log("Status:  DCMDesktop: Server " + server.getHost().getHostname() + " NOT successfully deleted, but archives were successfully deleted", true, true, true); }
                    else if ((!serverSuccessfullyDeleted) && (!archivesSuccessfullyDeleted)) { log("Status:  DCMDesktop: Server " + server.getHost().getHostname() + " and archives NOT successfully deleted", true, true, true); }                    
                }
                else
                {
                    if      ( serverSuccessfullyDeleted) { log("Success: DCMDesktop: Server " + server.getHost().getHostname() + " successfully deleted", true, true, true); }
                    else if (!serverSuccessfullyDeleted) { log("Status:  DCMDesktop: Server " + server.getHost().getHostname() + " NOT successfully deleted", true, true, true); }
                }
            }
        });
        deleteServerThread.setName("deleteServerThread");
        deleteServerThread.setDaemon(false);
        deleteServerThread.setPriority(Thread.NORM_PRIORITY);
        deleteServerThread.start();        
    }
    // End MiddleWare
    
    public void openDeleteAllServerForm()
    {
        try { deleteServersForm = new DCMDeleteServersForm(this); } catch (UnsupportedLookAndFeelException ex) {  } // Well would anybody really care?
        deleteServersForm.setVisible(true);
    }

    // Begin FrontEnd
    public void deleteServersByForm(final boolean deleteArchivesParam) throws CloneNotSupportedException
    {
        deleteServers(deleteArchivesParam); // MiddleWare Invocation
    }
    // End FrontEnd
    
    // Begin MiddleWare
    public void deleteServers(final boolean deleteArchivesParam) throws CloneNotSupportedException
    {
        ArrayList<Server> thisServerList = new ArrayList<Server>();
        thisServerList = dcmDBClient.selectServers();
        for (Server server:thisServerList) { deleteServer(server, deleteArchivesParam); }
    }    
    // End MiddleWare
        
    public void addServerCanceled() // FrontEnd
    {
        log("Add Server canceled by user", true, true, true);
    }
    
    public void deleteServerCanceled() // FrontEnd
    {
        log("Delete Server canceled by user", true, true, true);
    }
    
    public void addDCMUserCanceled() // FrontEnd
    {
        log("Add DCMUser canceled by user", true, true, true);
    }
    
    public void deleteRCMUserCanceled() // FrontEnd
    {
        log("Delete DCMUser canceled by user", true, true, true);
    }
    
    private void startPollerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startPollerButtonActionPerformed
        startPoller(); // 
    }//GEN-LAST:event_startPollerButtonActionPerformed

    private void startPoller()
    {
        String javaParam = ""; if (daemon) { javaParam = " --daemon "; } else { javaParam = ""; }
        shell.startPoller(128," -Duser.country=US -Duser.language=en" + " ", javaParam);
    } // MiddleWare Invocation
    private void startCommander()
    {
        String javaParam = ""; if (daemon) { javaParam = " --daemon "; } else { javaParam = ""; }
        shell.startCommander(128, "", javaParam);
    } // MiddleWare Invocation
    
    // FrontEnd
    private void startHourSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_startHourSliderStateChanged
        Thread startHourSliderStateChangedThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                startCalendar = startDateChooserPanel.getSelectedDate();
                startCalendar.set(Calendar.HOUR_OF_DAY, startHourSlider.getValue());
                startCalendar.set(Calendar.MINUTE, startMinuteSlider.getValue());
                startCalendar.set(Calendar.SECOND, (int)0);
                startTimeField.setText(""); startTimeField.setText(String.format("%02d:%02d", startCalendar.get(Calendar.HOUR_OF_DAY), startCalendar.get(Calendar.MINUTE)));                                
            }
        });
        startHourSliderStateChangedThread.setName("startHourSliderStateChangedThread");
        startHourSliderStateChangedThread.setDaemon(false);
        startHourSliderStateChangedThread.setPriority(Thread.NORM_PRIORITY);
        startHourSliderStateChangedThread.start();        
}//GEN-LAST:event_startHourSliderStateChanged

    // FrontEnd
    private void startMinuteSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_startMinuteSliderStateChanged
        Thread startMinuteSliderStateChangedThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                startCalendar = startDateChooserPanel.getSelectedDate();
                startCalendar.set(Calendar.HOUR_OF_DAY, startHourSlider.getValue());
                startCalendar.set(Calendar.MINUTE, startMinuteSlider.getValue());
                startCalendar.set(Calendar.SECOND, (int)0);
                startTimeField.setText(""); startTimeField.setText(String.format("%02d:%02d", startCalendar.get(Calendar.HOUR_OF_DAY), startCalendar.get(Calendar.MINUTE)));                                
            }
        });
        startMinuteSliderStateChangedThread.setName("startMinuteSliderStateChangedThread");
        startMinuteSliderStateChangedThread.setDaemon(false);
        startMinuteSliderStateChangedThread.setPriority(Thread.NORM_PRIORITY);
        startMinuteSliderStateChangedThread.start();        
}//GEN-LAST:event_startMinuteSliderStateChanged

    // FrontEnd
    private void endDateChooserPanelOnSelectionChange(datechooser.events.SelectionChangedEvent evt) {//GEN-FIRST:event_endDateChooserPanelOnSelectionChange
        endCalendar = endDateChooserPanel.getSelectedDate();
        endCalendar.set(Calendar.HOUR_OF_DAY, endHourSlider.getValue());
        endCalendar.set(Calendar.MINUTE, endMinuteSlider.getValue());
        endCalendar.set(Calendar.SECOND, (int)0);
    }//GEN-LAST:event_endDateChooserPanelOnSelectionChange

    // FrontEnd
    private void endHourSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_endHourSliderStateChanged
        Thread endHourSliderStateChangedThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                endCalendar = endDateChooserPanel.getSelectedDate();
                endCalendar.set(Calendar.HOUR_OF_DAY, endHourSlider.getValue());
                endCalendar.set(Calendar.MINUTE, endMinuteSlider.getValue());
                endCalendar.set(Calendar.SECOND, (int)0);
                endTimeField.setText(""); endTimeField.setText(String.format("%02d:%02d", endCalendar.get(Calendar.HOUR_OF_DAY), endCalendar.get(Calendar.MINUTE)));                                
            }
        });
        endHourSliderStateChangedThread.setName("endHourSliderStateChangedThread");
        endHourSliderStateChangedThread.setDaemon(false);
        endHourSliderStateChangedThread.setPriority(Thread.NORM_PRIORITY);
        endHourSliderStateChangedThread.start();        
    }//GEN-LAST:event_endHourSliderStateChanged

    // FrontEnd
    private void endMinuteSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_endMinuteSliderStateChanged
        Thread endMinuteSliderStateChangedThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                endCalendar = endDateChooserPanel.getSelectedDate();
                endCalendar.set(Calendar.HOUR_OF_DAY, endHourSlider.getValue());
                endCalendar.set(Calendar.MINUTE, endMinuteSlider.getValue());
                endCalendar.set(Calendar.SECOND, (int)0);
                endTimeField.setText(""); endTimeField.setText(String.format("%02d:%02d", endCalendar.get(Calendar.HOUR_OF_DAY), endCalendar.get(Calendar.MINUTE)));                                
            }
        });
        endMinuteSliderStateChangedThread.setName("endMinuteSliderStateChangedThread");
        endMinuteSliderStateChangedThread.setDaemon(false);
        endMinuteSliderStateChangedThread.setPriority(Thread.NORM_PRIORITY);
        endMinuteSliderStateChangedThread.start();        
    }//GEN-LAST:event_endMinuteSliderStateChanged

    // FrontEnd
    private void wordSearchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_wordSearchButtonActionPerformed
//        if (searchField.getText().length() > 0)
//        {
            searchExactWasUsed = true;
            setSelectionArea(searchExactResources(searchField.getText()));
//        }
    }//GEN-LAST:event_wordSearchButtonActionPerformed

    // Begin MiddleWare
    private String searchExactResources(String searchString)
    {
        String thisOutput = "";
        ArrayList<Server> serverListLimited = new ArrayList<Server>();
        if ( dcmVergunning.getServersInLicense() < serverList.size()) // We can not get a sublist from the serverList that is larger than serverList (outofbounds error)
        {
            for (Server server:serverList.subList(0, dcmVergunning.getServersInLicense())) {serverListLimited.add(server);}
        }
        else
        {
            serverListLimited = serverList;
        }        
        
        if (searchString.length() > 0) { thisOutput = dcmDBClient.convertSearchStringToResourcesText(searchField.getText(), serverListLimited, true); }
        return thisOutput;
    }    
    // End MiddleWare
    
    // Begin MiddleWare
    private String searchNonExactResources(String searchString)
    {
        String thisOutput = "";
        ArrayList<Server> serverListLimited = new ArrayList<Server>();
        if ( dcmVergunning.getServersInLicense() < serverList.size()) // We can not get a sublist from the serverList that is larger than serverList (outofbounds error)
        {
            for (Server server:serverList.subList(0, dcmVergunning.getServersInLicense())) {serverListLimited.add(server);}
        }
        else
        {
            serverListLimited = serverList;
        }        
        if (searchString.length() > 0) { thisOutput = dcmDBClient.convertSearchStringToResourcesText(searchField.getText(), serverListLimited, false); }
        return thisOutput;
    }    
    // End MiddleWare

    // FrontEnd
    private void matchSearchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_matchSearchButtonActionPerformed
//        if (searchField.getText().length() > 0)
//        {
            searchExactWasUsed = false;
            setSelectionArea(searchNonExactResources(searchField.getText()));
//        }
    }//GEN-LAST:event_matchSearchButtonActionPerformed

    // Begin FrontEnd
    private void selectionAreaCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_selectionAreaCaretUpdate
        Thread selectionAreaCaretUpdateThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                // The part below takes the lines in the selectionArea and turns every column into a uniq hash for stats
                selectedResources = 0;
                long totalResources = dcmDBClient.getResourceCount();
                long uniqueServers;
                long uniqueCategories;
                long uniqueRTypes;
                long uniqueVTypes;
                long uniqueResources;

                // Breaking down stats unique values with hashes (which can only contain unique values)
                Set<String> totHash = new HashSet<String>();
                Set<String> serverHash = new HashSet<String>();
                Set<String> categoryHash = new HashSet<String>();
                Set<String> resourceTypeHash = new HashSet<String>();
                Set<String> valueTypeHash = new HashSet<String>();
                Set<String> resourceHash = new HashSet<String>();

                String[] lineArray = new String[4];
                BufferedReader reader = new BufferedReader(new StringReader(selectionArea.getText()));
                String line = "";
                output = "";

                try
                {
                    while ((line = reader.readLine()) != null)
                    {
                        if (line.length()>4)
                        {
                            selectedResources++;
                            lineArray = line.split("\\s+");
                            if ((lineArray[0] != null) && (lineArray[0].length()>0)) {totHash.add(lineArray[0]);}
                            if ((lineArray[1] != null) && (lineArray[1].length()>0)) {serverHash.add(lineArray[1]);}
                            if ((lineArray[2] != null) && (lineArray[2].length()>0)) {categoryHash.add(lineArray[2]);}
                            if ((lineArray[3] != null) && (lineArray[3].length()>0)) {resourceTypeHash.add(lineArray[3]);}
                            if ((lineArray[4] != null) && (lineArray[3].length()>0)) {valueTypeHash.add(lineArray[4]);}
                            if ((lineArray[5] != null) && (lineArray[4].length()>0)) {resourceHash.add(lineArray[5]);}                    
                        }
                    }

                } catch(IOException ex) { log("Error:   DCMDesktop: selectionAreaCaretUpdate: IOException: while ((line = reader.readLine())" + ex.getMessage(),true,true,true); }

        //        totalResources =    totHash.size();
        //        totalResources =    selectionArea.getLineCount();
                uniqueServers =     serverHash.size();
                uniqueCategories =  categoryHash.size();
                uniqueRTypes =      resourceTypeHash.size();
                uniqueVTypes =      valueTypeHash.size();
                uniqueResources =   resourceHash.size();

                updateSearchStatsTable(Long.toString(selectedResources) + "/" + Long.toString(totalResources),Long.toString(uniqueServers),Long.toString(uniqueCategories),Long.toString(uniqueRTypes),Long.toString(uniqueVTypes),Long.toString(uniqueResources));

                if (selectedResources > 0)
                {
                    enableNavigationButtons(true);
                }
                else
                {
                    enableNavigationButtons(false);
                }
            }
        });
        selectionAreaCaretUpdateThread.setName("selectionAreaCaretUpdateThread");
        selectionAreaCaretUpdateThread.setDaemon(false);
        selectionAreaCaretUpdateThread.setPriority(Thread.NORM_PRIORITY);
        selectionAreaCaretUpdateThread.start();
    }//GEN-LAST:event_selectionAreaCaretUpdate
    // End FrontEnd

    // FrontEnd
    private void enableNavigationButtons(final boolean enable)
    {
        Thread enableNavigationButtonsThread = new Thread(new Runnable()
        {
            @Override
            @SuppressWarnings({"static-access"})
            public void run()
            {
                leftButton.setEnabled(enable);
                zoomInXButton.setEnabled(enable);
                zoomOutXButton.setEnabled(enable);
                rightButton.setEnabled(enable);
                graphButton.setEnabled(enable);
                upButton.setEnabled(enable);
                zoomInYButton.setEnabled(enable);
                zoomOutYButton.setEnabled(enable);
                downButton.setEnabled(enable);        
            }
        });
        enableNavigationButtonsThread.setName("enableNavigationButtonsThread");
        enableNavigationButtonsThread.setDaemon(true);
        enableNavigationButtonsThread.setPriority(Thread.NORM_PRIORITY);
        enableNavigationButtonsThread.start();
    }
    
    // FrontEnd
    private void leftButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_leftButtonActionPerformed
        enableGraphButton(false);
        enableStartTimeRelativeWidgets(false);
        enableEndTimeRelativeWidgets(false);
        leftButton();
    }//GEN-LAST:event_leftButtonActionPerformed

    private void leftButton()
    {                                           
//        enableNavigationButtons(false);
        long start = startCalendar.getTimeInMillis();
        long end   = endCalendar.getTimeInMillis();
        long diff  = (endCalendar.getTimeInMillis() - startCalendar.getTimeInMillis()) / navigationXFactor;
        long newstart = start - diff;
        long newend   = end - diff;
        startCalendar = Calendar.getInstance(); startCalendar.setTimeInMillis(newstart);
        endCalendar =   Calendar.getInstance(); endCalendar.setTimeInMillis(newend);
        updateTimeWidgets(startCalendar,endCalendar);
        setCursor(Cursor.WAIT_CURSOR);
        Thread leftButtonThread = new Thread(new Runnable()
        {
            @Override
            @SuppressWarnings({"static-access"})
            public void run()
            {
                 // MiddleWare Invocation
                try { setGraph(getTrend(false,getImageWidth(),getImageHeight(),manualValueMode,startCalendar,endCalendar, valueLimits, selectionArea.getText(), lineType)); } catch (CloneNotSupportedException ex) { }
            }
        });
        leftButtonThread.setName("leftButtonThread");
        leftButtonThread.setDaemon(true);
        leftButtonThread.setPriority(Thread.NORM_PRIORITY);
        leftButtonThread.start();        
    }                                          

    // FrontEnd
    private void rightButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rightButtonActionPerformed
        enableGraphButton(false);
        enableStartTimeRelativeWidgets(false);
        enableEndTimeRelativeWidgets(false);
        rightButton();
    }//GEN-LAST:event_rightButtonActionPerformed

    private void rightButton()
    {                                            
//        enableNavigationButtons(false);
        long start = startCalendar.getTimeInMillis();
        long end   = endCalendar.getTimeInMillis();
        long diff  = (endCalendar.getTimeInMillis() - startCalendar.getTimeInMillis()) / navigationXFactor;
        long newstart = start + diff;
        long newend   = end + diff;
        startCalendar = Calendar.getInstance(); startCalendar.setTimeInMillis(newstart);
        endCalendar =   Calendar.getInstance(); endCalendar.setTimeInMillis(newend);
        updateTimeWidgets(startCalendar,endCalendar);
        setCursor(Cursor.WAIT_CURSOR);
        Thread rightButtonThread = new Thread(new Runnable()
        {
            @Override
            @SuppressWarnings({"static-access"})
            public void run()
            {
//                setGraph(getTrend(false,getImageWidth(),getImageHeight(),manualValueMode,startCalendar,endCalendar, valueLimits)); // MiddleWare Invocation
                try { setGraph(getTrend(false,getImageWidth(),getImageHeight(),manualValueMode,startCalendar,endCalendar, valueLimits, selectionArea.getText(), lineType)); } catch (CloneNotSupportedException ex) { } // MiddleWare Invocation
            }
        });
        rightButtonThread.setName("rightButtonThread");
        rightButtonThread.setDaemon(true);
        rightButtonThread.setPriority(Thread.NORM_PRIORITY);
        rightButtonThread.start();        
    }                                           

    // FrontEnd
    private void zoomInXButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zoomInXButtonActionPerformed
        enableGraphButton(false);
        enableStartTimeRelativeWidgets(false);
        enableEndTimeRelativeWidgets(false);
        zoomInXButton();
    }//GEN-LAST:event_zoomInXButtonActionPerformed
    private void zoomInXButton()
    {                                              
//        enableNavigationButtons(false);
        long start = startCalendar.getTimeInMillis();
        long end   = endCalendar.getTimeInMillis();
        long diff  = (endCalendar.getTimeInMillis() - startCalendar.getTimeInMillis()) / navigationXFactor;
        long newstart = start + diff;
        long newend   = end - diff;
        startCalendar = Calendar.getInstance(); startCalendar.setTimeInMillis(newstart);
        endCalendar =   Calendar.getInstance(); endCalendar.setTimeInMillis(newend);
        updateTimeWidgets(startCalendar,endCalendar);
        setCursor(Cursor.WAIT_CURSOR);
        Thread zoomInXButtonThread = new Thread(new Runnable()
        {
            @Override
            @SuppressWarnings({"static-access"})
            public void run()
            {
//                setGraph(getTrend(false,getImageWidth(),getImageHeight(),manualValueMode,startCalendar,endCalendar, valueLimits)); // MiddleWare Invocation
                try { setGraph(getTrend(false,getImageWidth(),getImageHeight(),manualValueMode,startCalendar,endCalendar, valueLimits, selectionArea.getText(), lineType)); } catch (CloneNotSupportedException ex) { } // MiddleWare Invocation
            }
        });
        zoomInXButtonThread.setName("zoomInXButtonThread");
        zoomInXButtonThread.setDaemon(true);
        zoomInXButtonThread.setPriority(Thread.NORM_PRIORITY);
        zoomInXButtonThread.start();        
    }                                             

    private void convertStartRelativeToAbsoluteCalendar()
    {
        long now = Calendar.getInstance().getTimeInMillis();
        long diff  =    ((Long.parseLong(startMonthField.getText()) * MONTH) +
                        (Long.parseLong(startDayField.getText()) * DAY) +
                        (Long.parseLong(startHourField.getText()) * HOUR) +
                        (Long.parseLong(startMinuteField.getText()) * MINUTE));
        long newstart = now + diff;
        startCalendar = Calendar.getInstance(); startCalendar.setTimeInMillis(newstart);
        updateTimeWidgets(startCalendar,endCalendar);
    }

    private void convertEndRelativeToAbsoluteCalendar()
    {
        long now = Calendar.getInstance().getTimeInMillis();
        long diff  =    ((Long.parseLong(endMonthField.getText()) * MONTH) +
                        (Long.parseLong(endDayField.getText()) * DAY) +
                        (Long.parseLong(endHourField.getText()) * HOUR) +
                        (Long.parseLong(endMinuteField.getText()) * MINUTE));
        long newend = now + diff;
        endCalendar = Calendar.getInstance(); endCalendar.setTimeInMillis(newend);
        updateTimeWidgets(startCalendar,endCalendar);
    }

    protected int getImageWidth()     { return viewSplitter.getWidth() - viewEdgeX; } // 30
    protected int getImageHeight()    { return imageHeight = viewSplitter.getHeight() - viewEdgeY; } // 30
    
//    protected void showTrend(final boolean resetColors)
    
    // Begin MiddleWare (Be aware that this method is being called also from the PopupMenu)
    protected ImageIcon getTrend(final boolean resetColors, final int imageWidth, final int imageHeight, final boolean manualValueMode, final Calendar startCalendar, final Calendar endCalendar, final ValueLimits valueLimits, final String selectedResources, final Float lineType) throws CloneNotSupportedException
    {
        ArrayList<String> resourceIdList = new ArrayList<String>();
//        ArrayList<Resource> resourceList = new ArrayList<Resource>();
        String[] lineArray = new String[5];
//        BufferedReader reader = new BufferedReader(new StringReader(selectionArea.getText()));
        BufferedReader reader = new BufferedReader(new StringReader(selectedResources));
        String line = "";
        output = "";
        final Object ref = this;
        
//        StringBuffer pidResources = new StringBuffer();
//        String pidResources = "";

        try
        {
            while ((line = reader.readLine()) != null)
            {
                if (line.length()>1)
                {
                    lineArray = line.split("\\s+");
                    if ((lineArray[0] != null) && (lineArray[0].length()>0)) { resourceIdList.add(lineArray[0]); }
//                    if ((lineArray[4] != null) && (lineArray[4].equals("PID"))) { pidResources += line + "\n"; } // This generates selectedResources with a PID number for getData()
                }
            }

        } catch(IOException ex) { log("Error:   DCMDesktop: getTrend: IOException: while ((line = reader.readLine())" + ex.getMessage(),true,true,true); }

        final ArrayList<Resource> resourceList = dcmDBClient.convertResourcesTextToResources(resourceIdList);

        String rrdFile = "";
        BufferedImage bufferedImage = new BufferedImage(imageWidth,imageHeight,BufferedImage.TYPE_INT_RGB);

        RrdGraphDef rrdGraphDef = new RrdGraphDef();
        RrdGraph rrdGraph = null;
        //rrdGraphDef.setTitle("Graph"); // Caused null pointer in Swing

        int colorCounter;

        if ((lineColorArray.size() != resourceList.size()) || (resetColors))
        {
            lineColorArray.clear();
            for (Resource resource:resourceList) { lineColorArray.add(Color.getHSBColor( new Random().nextFloat(), new Random().nextFloat()/2+0.5F, new Random().nextFloat()/2+0.5F )); }            
        }


        colorCounter = 0;
//        String pidComments = "";
        for (Resource resource:resourceList)
        {
            //System.out.println("File: " + configuration.getRRDBDir() + javaDBClient.selectHost(resource.getHostId()).getHostname() + configuration.getFileSeparator() + resource.getRRDFile());
            String resourceHost = dcmDBClient.selectHost(resource.getHostId()).getHostname();
            rrdFile = configuration.getArchiveDBDir() + resourceHost + configuration.getFileSeparator() + resource.getRRDFile();
            rrdGraphDef.datasource(resource.getId()+resource.getResource(), rrdFile, resource.getResource(), "AVERAGE");
            rrdGraphDef.line(resource.getId()+resource.getResource(), lineColorArray.get(colorCounter), resourceHost + " " + resource.getResourceType() + " " + resource.getResource(), lineType); // last=line
            colorCounter++;
        }
//        if ((pidResources.length() > 0) && (endCalendar.getTimeInMillis() - startCalendar.getTimeInMillis() <= (MINUTE * 5))) { pidComments = getData(startCalendar, endCalendar, pidResources ).toString(); }
        
        rrdGraphDef.setStartTime((Long)(startCalendar.getTimeInMillis()/1000L));
        rrdGraphDef.setEndTime((Long)(endCalendar.getTimeInMillis()/1000L));
//        rrdGraphDef.setVerticalLabel(resourceList.get(0).getCategory() + " " +resourceList.get(0).getValueType());
        rrdGraphDef.setVerticalLabel(resourceList.get(0).getValueType());
        rrdGraphDef.setAltAutoscale(true);
        rrdGraphDef.setWidth(imageWidth - imageEdgeX); // 90
        rrdGraphDef.setHeight(imageHeight - imageEdgeY); // 30 netto (zonder legenda) imageheight 439
        rrdGraphDef.setImageFormat("png");
        rrdGraphDef.setImageInfo(DCMLicense.getProcuct() + " " + DCMLicense.getVersion() + " Trend Analysis");
        rrdGraphDef.setTitle(DCMLicense.getProcuct() + " " + resourceList.get(0).getCategory() + " Trend. Period: [" + DCMTools.getHumanDateLong(startCalendar) + "] - [" + DCMTools.getHumanDateLong(endCalendar)+ "]");
        rrdGraphDef.setAntiAliasing(true);

//        rrdGraphDef.comment(pidComments);
//        log(pidComments,false,true,false);

        rrdGraphDef.setRigid(true);

        rrdGraphDef.setSmallFont(new java.awt.Font("STHeiti", 0, 9));
        rrdGraphDef.setLargeFont(new java.awt.Font("STHeiti", 0, 11));
        rrdGraphDef.setShowSignature(false);

        // Generate the graph to find out how big it becomes including legenda
        rrdGraphDef.setImageQuality(0F); // 0F worst 1F best 
        try { rrdGraph = new RrdGraph(rrdGraphDef); }
        catch (IOException ex)  { log("Error:  DCMDesktop: IOException: RrdGraph(graphDef)" + ex.getMessage(),true,true,true); }
        catch (RrdException ex) { log("Error:  DCMDesktop: RrdException: RrdGraph(graphDef)" + ex.getMessage(),true,true,true); }
        int additionalHeight = rrdGraph.getRrdGraphInfo().getHeight() - (imageHeight - imageEdgeY);

        rrdGraphDef.setHeight((imageHeight - (additionalHeight))); // 30 netto (zonder legenda) imageheight 439

//                if (manualValueMode) { rrdGraphDef.setMinValue(minValue); rrdGraphDef.setMaxValue(maxValue); }
        if (manualValueMode) { rrdGraphDef.setMinValue(valueLimits.getMin()); rrdGraphDef.setMaxValue(valueLimits.getMax()); }

        // Generate the graph again, but now shrunken, so the total height incl legenda becomes as big as without legenda
        rrdGraphDef.setImageQuality(0.5F); // 0F worst 1F best 
        try { rrdGraph = new RrdGraph(rrdGraphDef); }
        catch (IOException ex)  { log("Error:  DCMDesktop: IOException: RrdGraph(graphDef)" + ex.getMessage(),true,true,true); }
        catch (RrdException ex) { log("Error:  DCMDesktop: RrdException: RrdGraph(graphDef)" + ex.getMessage(),true,true,true); }

        rrdGraph.render(bufferedImage.getGraphics());
        imageIcon = new ImageIcon(bufferedImage);
//        System.out.println("imagestatus: " + imageIcon.getImageLoadStatus());

        return imageIcon;
    }
    
    protected StringBuffer getData(final Calendar startCalendar, final Calendar endCalendar, final String selectedResources) throws CloneNotSupportedException
    {
        StringBuffer returnData = new StringBuffer();
        ArrayList<String> resourceIdList = new ArrayList<String>();
        String[] lineArray = new String[5];
        BufferedReader reader = new BufferedReader(new StringReader(selectedResources));
        String line = "";
        output = "";

        try
        {
            while ((line = reader.readLine()) != null)
            {
                if (line.length()>1)
                {
                    lineArray = line.split("\\s+");
                    if ((lineArray[0] != null) && (lineArray[0].length()>0)) { resourceIdList.add(lineArray[0]); }
                }
            }

        } catch(IOException ex) { log("Error:   DCMDesktop: getData: IOException: while ((line = reader.readLine())" + ex.getMessage(),true,true,true); }

        final ArrayList<Resource> resourceList = dcmDBClient.convertResourcesTextToResources(resourceIdList);
        
//      RRDFetch being used print RRD values to STDOUT for debugging purposes
        String rrdFetchFile;
        RrdDb rrdDb = null;
        FetchRequest fetchRequest = null;
        FetchData fetchData = null;
        ArrayList<Double> valueList = new ArrayList<Double>();
        Host thisHost = new Host();

        for (Resource resource:resourceList)
        {
            thisHost = new Host(); try { thisHost = (Host) dcmDBClient.selectHost(resource.getHostId()).clone(); } catch (CloneNotSupportedException ex) { log("Error: CloneNotSupportedException: selectHost(resource.getHostId())" + ex.getMessage(),true,true,true); }
            returnData.append("Hostname: ").append(thisHost.getHostname()).append(" Category: ").append(resource.getCategory()).append(" Resource: ").append(resource.getResource()).append(" Archive: ").append(resource.getRRDFile()).append("\n\n");

            rrdFetchFile = configuration.getArchiveDBDir() + thisHost.getHostname() + configuration.getFileSeparator() + resource.getRRDFile();
            try { rrdDb = new RrdDb(rrdFetchFile); }
            catch (IOException ex) { log("Error:  DCMDesktop: IOException: rrdDb = new RrdDb(rrdFile)" + ex.getMessage(),true,true,true); }
            catch (RrdException ex) { log("Error:  DCMDesktop: RrdException: rrdDb = new RrdDb(rrdFile)" + ex.getMessage(),true,true,true); }

            try { fetchRequest = rrdDb.createFetchRequest("AVERAGE", (Long)startCalendar.getTimeInMillis()/1000, (Long)endCalendar.getTimeInMillis()/1000); }
            catch (RrdException ex) { log("Error:  DCMDesktop: RrdException: rrdDb.createFetchRequest(\"AVERAGE\"" + ex.getMessage(),true,true,true); }

            try { fetchData = fetchRequest.fetchData(); }
            catch (RrdException ex) { log("Error:  DCMDesktop: RrdException: fetchRequest.fetchData()" + ex.getMessage(),true,true,true); }
            catch (IOException ex) { log("Error:  DCMDesktop: IOException: fetchRequest.fetchData()" + ex.getMessage(),true,true,true); }

            returnData.append(fetchData.dump());

            try { rrdDb.close(); } catch (IOException ex) { log("Error:  DCMDesktop: IOException: rrdDb.close()" + ex.getMessage(),true,true,true); }
        }
        
        return returnData;
    }
    
    // FrontEnd
    class MyFileFilter extends javax.swing.filechooser.FileFilter { public boolean accept(File file) { String filename = file.getName(); return filename.endsWith(".png"); }  public String getDescription() { return "*.png"; } }

    protected void saveGraph(final ImageIcon imageIcon)
    {
        Thread saveGraphThread = new Thread(new Runnable()
        {
            @Override
            @SuppressWarnings({"static-access"})
            public void run()
            {
                Image image = imageIcon.getImage();  
                RenderedImage rendered = null;  
                if (image instanceof RenderedImage)  
                {  
                    rendered = (RenderedImage)image;  
                }  
                else  
                {  
                    BufferedImage buffered = new BufferedImage(  
                        imageIcon.getIconWidth(),  
                        imageIcon.getIconHeight(),  
                        BufferedImage.TYPE_INT_RGB  
                    );  
                    Graphics2D g = buffered.createGraphics();  
                    g.drawImage(image, 0, 0, null);  
                    g.dispose();  
                    rendered = buffered;  
                }

                setCursor(Cursor.DEFAULT_CURSOR);
                String filename = configuration.getImagesDir();

                JFileChooser fc = new JFileChooser(new File(filename)); fc.addChoosableFileFilter(new MyFileFilter()); fc.setAcceptAllFileFilterUsed(false);
                JFrame frame = new JFrame(); fc.showSaveDialog(frame); File fileToSave = fc.getSelectedFile();
                
                String extension = "";
                if (! fileToSave.getAbsoluteFile().toString().matches(".+\\.png$")) { extension = ".png"; }
                setCursor(Cursor.WAIT_CURSOR);
                boolean writeWasSuccessful = true;  try { writeWasSuccessful = ImageIO.write(rendered, "png", new File(fileToSave.getAbsoluteFile() + extension)); } catch (IOException ex) { log("Error:  DCMDesktop: IOException: DCManager.saveGraph(ImageIcon): " + ex.getMessage(),true,true,true); }
                
                if (writeWasSuccessful) { log("Successfully saved: " + fileToSave.getAbsoluteFile() + extension,true,true,true); } else { log("Failed to save: " + fileToSave.getAbsoluteFile() + extension,true,true,true); }
                
                viewTrendPane(true);
                setCursor(Cursor.DEFAULT_CURSOR);
                enableNavigationButtons(true);        
            }
        });
        saveGraphThread.setName("saveGraphThread");
        saveGraphThread.setDaemon(true);
        saveGraphThread.setPriority(Thread.NORM_PRIORITY);
        saveGraphThread.start();
    }    
    
    // FrontEnd
    protected void setGraph(final ImageIcon imageIcon)
    {
        Thread setGraphThread = new Thread(new Runnable()
        {
            @Override
            @SuppressWarnings({"static-access"})
            public void run()
            {
                graphLabel.setIcon(imageIcon);
                viewTrendPane(true);
                setCursor(Cursor.DEFAULT_CURSOR);
                enableNavigationButtons(true);        
            }
        });
        setGraphThread.setName("setGraphThread");
        setGraphThread.setDaemon(true);
        setGraphThread.setPriority(Thread.NORM_PRIORITY);
        setGraphThread.start();
    }

    // FrontEnd
    private void zoomOutXButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zoomOutXButtonActionPerformed
        enableGraphButton(false);
        enableStartTimeRelativeWidgets(false);
        enableEndTimeRelativeWidgets(false);
        zoomOutXButton();
    }//GEN-LAST:event_zoomOutXButtonActionPerformed
    private void zoomOutXButton()
    {                                               
//        enableNavigationButtons(false);
        long start = startCalendar.getTimeInMillis();
        long end   = endCalendar.getTimeInMillis();
        long diff  = (endCalendar.getTimeInMillis() - startCalendar.getTimeInMillis()) / navigationXFactor;
        long newstart = start - diff;
        long newend   = end + diff;
        startCalendar = Calendar.getInstance(); startCalendar.setTimeInMillis(newstart);
        endCalendar =   Calendar.getInstance(); endCalendar.setTimeInMillis(newend);
        updateTimeWidgets(startCalendar,endCalendar);
        setCursor(Cursor.WAIT_CURSOR);
        Thread trendAnalysisButtonThread = new Thread(new Runnable()
        {
            @Override
            @SuppressWarnings({"static-access"})
            public void run()
            {
//                setGraph(getTrend(false,getImageWidth(),getImageHeight(),manualValueMode,startCalendar,endCalendar, valueLimits)); // MiddleWare Invocation
                try { setGraph(getTrend(false,getImageWidth(),getImageHeight(),manualValueMode,startCalendar,endCalendar, valueLimits, selectionArea.getText(), lineType)); } catch (CloneNotSupportedException ex) { } // MiddleWare Invocation
            }
        });
        trendAnalysisButtonThread.setName("trendAnalysisButtonThread");
        trendAnalysisButtonThread.setDaemon(true);
        trendAnalysisButtonThread.setPriority(Thread.NORM_PRIORITY);
        trendAnalysisButtonThread.start();
    }                                              


    private void swapViewButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_swapViewButtonActionPerformed
//        setMinimumSize(new Dimension(1100,640));
//        setPreferredSize(new Dimension(Toolkit.getDefaultToolkit().getScreenSize().width,Toolkit.getDefaultToolkit().getScreenSize().height));
        if (trendIsViewable) { viewTrendPane(false); } else { viewTrendPane(true); }
    }//GEN-LAST:event_swapViewButtonActionPerformed

    // FronEnd
    private void searchFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchFieldKeyReleased
//        entre = 10)
        if ( evt.getKeyCode() == 10 )
        {
            searchExactWasUsed = false;
            if (searchField.getText().length() >= 0)
            {
                setSelectionArea(searchExactResources(searchField.getText())); // MiddleWare Invocation

                Thread searchFieldKeyReleasedThread = new Thread(new Runnable()
                {
                    @Override
                    @SuppressWarnings({"static-access"})
                    public void run()
                    {
                        matchSearchButton.setSelected(true);
                        try { Thread.sleep(250); } catch (InterruptedException ex) { }
                        matchSearchButton.setSelected(false);
                        setSelectionArea(searchNonExactResources(searchField.getText())); // MiddleWare Invocation
                    }
                });
                searchFieldKeyReleasedThread.setName("searchFieldKeyReleasedThread");
                searchFieldKeyReleasedThread.setDaemon(true);
                searchFieldKeyReleasedThread.setPriority(Thread.NORM_PRIORITY);
                searchFieldKeyReleasedThread.start();
            }
//            else
//            {
//                Thread searchFieldKeyReleasedThread = new Thread(new Runnable()
//                {
//                    @Override
//                    @SuppressWarnings({"static-access"})
//                    public void run()
//                    {
//                        wordSearchButton.setSelected(true);
//                        try { Thread.sleep(250); } catch (InterruptedException ex) { }
//                        wordSearchButton.setSelected(false);
//                        setSelectionArea(searchExactResources(searchField.getText())); // MiddleWare Invocation
//                    }
//                });
//                searchFieldKeyReleasedThread.setName("searchFieldKeyReleasedThread");
//                searchFieldKeyReleasedThread.setDaemon(true);
//                searchFieldKeyReleasedThread.setPriority(Thread.NORM_PRIORITY);
//                searchFieldKeyReleasedThread.start();
//
////                setSelectionArea("");
//            }            
        }
        else
        {
            
        }
    }//GEN-LAST:event_searchFieldKeyReleased

    // FrontEnd
    private void deleteServerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteServerButtonActionPerformed
        deleteServer();
    }//GEN-LAST:event_deleteServerButtonActionPerformed
    
    // FrontEnd
    protected void deleteServer()
    {                                                   
        if (serverTree.getSelectionPath().getPathCount() == 1)
        {
            try { deleteServersForm = new DCMDeleteServersForm(this); } catch (UnsupportedLookAndFeelException ex) {  } // Anybody care? ok!
            deleteServersForm.setVisible(true);            
        }
        else if(serverTree.getSelectionPath().getPathCount() >= 1)
        {
            ArrayList<String> serverNameList = new ArrayList<String>();
            for (TreePath treePath:serverTree.getSelectionPaths()) { serverNameList.add(treePath.getLastPathComponent().toString()); }
            openDeleteServerForm(serverNameList);
//            deleteServerForm.setServerList(serverNameList);
//            deleteServerForm.setVisible(true);            
        }
    }                                                  
    
    // Begin MiddleWare
//    private ValueLimits manualValueMode(String valueButton, String selectedResources) // Wrapper method for showTrend
    private ValueLimits manualValueMode(final Calendar startCalendar, final Calendar endCalendar, String selectedResources) // Calculates the selected Archive min and max values (SelectedResources = TextArea as result from from Search button)
    {
        ValueLimits valueLimits = new ValueLimits();
        ArrayList<String> resourceIdList = new ArrayList<String>();
        ArrayList<Resource> resourceList = new ArrayList<Resource>();
        String[] lineArray = new String[5];
        BufferedReader reader = new BufferedReader(new StringReader(selectedResources));
        String line = "";
        output = "";

        try
        {
            while ((line = reader.readLine()) != null)
            {
                if (line.length()>1)
                {
                    lineArray = line.split("\\s+");
                    if ((lineArray[0] != null) && (lineArray[0].length()>0)) { resourceIdList.add(lineArray[0]); }
                }
            }

        } catch(IOException ex) { log("Error:   DCMDesktop: manualValueMode: IOException: while ((line = reader.readLine())" + ex.getMessage(),true,true,true); }

        resourceList = dcmDBClient.convertResourcesTextToResources(resourceIdList);

////      RRDFetch being used to get min and max value from all selected archives within selected period
        String rrdFetchFile;
        RrdDb rrdDb = null;
        FetchRequest fetchRequest = null;
        FetchData fetchData = null;
        ArrayList<Double> valueList = new ArrayList<Double>();
        Host thisHost = new Host();
        for (Resource resource:resourceList)
        {
            thisHost = new Host(); try { thisHost = (Host) dcmDBClient.selectHost(resource.getHostId()).clone(); } catch (CloneNotSupportedException ex) { log("Error: CloneNotSupportedException: selectHost(resource.getHostId())" + ex.getMessage(),true,true,true); }
            
            rrdFetchFile = configuration.getArchiveDBDir() + thisHost.getHostname() + configuration.getFileSeparator() + resource.getRRDFile();
            try { rrdDb = new RrdDb(rrdFetchFile); }
            catch (IOException ex) { log("Error:   DCMDesktop: IOException: rrdDb = new RrdDb(rrdFile)" + ex.getMessage(),true,true,true); }
            catch (RrdException ex) { log("Error:   DCMDesktop: RrdException: rrdDb = new RrdDb(rrdFile)" + ex.getMessage(),true,true,true); }

            try { fetchRequest = rrdDb.createFetchRequest("AVERAGE", (Long)startCalendar.getTimeInMillis()/1000, (Long)endCalendar.getTimeInMillis()/1000); }
            catch (RrdException ex) { log("Error:   DCMDesktop: RrdException: rrdDb.createFetchRequest(\"AVERAGE\"" + ex.getMessage(),true,true,true); }

            try { fetchData = fetchRequest.fetchData(); }
            catch (RrdException ex) { log("Error:   DCMDesktop: RrdException: fetchRequest.fetchData()" + ex.getMessage(),true,true,true); }
            catch (IOException ex) { log("Error:   DCMDesktop: IOException: fetchRequest.fetchData()" + ex.getMessage(),true,true,true); }

//            // This also prints RRD Values from archive to STDOUT (this is already used in getTrend(..))
//            if (debug) { System.out.println("Dumping: " + rrdFetchFile); System.out.println(fetchData.dump()); }

            for (Double value:fetchData.getValues(0)) { valueList.add(value); }
            try { rrdDb.close(); } catch (IOException ex) { log("Error:   DCMDesktop: IOException: rrdDb.close()" + ex.getMessage(),true,true,true); }
        }
        valueLimits.setMin(Double.MAX_VALUE); for (Double value :valueList) { if (valueLimits.getMin() > value) {valueLimits.setMin(value);} }
        valueLimits.setMax(Double.MIN_VALUE); for (Double value :valueList) { if (valueLimits.getMax() < value) {valueLimits.setMax(value);} }
//        manualValueMode = true;
        
        return valueLimits;
    }
    
    // FrontEnd
    private void downButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_downButtonActionPerformed
        enableGraphButton(false);
        enableStartTimeRelativeWidgets(false);
        enableEndTimeRelativeWidgets(false);
        downButton();
}//GEN-LAST:event_downButtonActionPerformed

    private void downButton()
    {                                           
        if (!manualValueMode)
        {
            manualValueMode = true;
            try { valueLimits = (ValueLimits) manualValueMode(startCalendar, endCalendar, selectionArea.getText()).clone(); } catch (CloneNotSupportedException ex) {}
        }
        
        Double range = valueLimits.getMax() - valueLimits.getMin();
        Double portion = (range / navigationYFactor);
        valueLimits.subMin(portion); valueLimits.subMax(portion);
        setCursor(Cursor.WAIT_CURSOR);
        Thread downButtonThread = new Thread(new Runnable()
        {
            @Override
            @SuppressWarnings({"static-access"})
            public void run()
            {
//                setGraph(getTrend(false,getImageWidth(),getImageHeight(),manualValueMode,startCalendar,endCalendar, valueLimits)); // MiddleWare Invocation
                try { setGraph(getTrend(false,getImageWidth(),getImageHeight(),manualValueMode,startCalendar,endCalendar, valueLimits, selectionArea.getText(), lineType)); } catch (CloneNotSupportedException ex) { } // MiddleWare Invocation
            }
        });
        downButtonThread.setName("downButtonThread");
        downButtonThread.setDaemon(true);
        downButtonThread.setPriority(Thread.NORM_PRIORITY);
        downButtonThread.start();        
    }                                          

    protected void setMaxValueTo100()
    {                                           
        if (!manualValueMode)
        {
            manualValueMode = true;
            try { valueLimits = (ValueLimits) manualValueMode(startCalendar, endCalendar, selectionArea.getText()).clone(); } catch (CloneNotSupportedException ex) {}
        }
        
//        Double range = valueLimits.getMax() - valueLimits.getMin();
//        Double portion = (range / navigationYFactor);
        valueLimits.setMax(100D);
        setCursor(Cursor.WAIT_CURSOR);
        Thread zeroValueAtBottomThread = new Thread(new Runnable()
        {
            @Override
            @SuppressWarnings({"static-access"})
            public void run()
            {
//                setGraph(getTrend(false,getImageWidth(),getImageHeight(),manualValueMode,startCalendar,endCalendar, valueLimits)); // MiddleWare Invocation
                try { setGraph(getTrend(false,getImageWidth(),getImageHeight(),manualValueMode,startCalendar,endCalendar, valueLimits, selectionArea.getText(), lineType)); } catch (CloneNotSupportedException ex) { } // MiddleWare Invocation
            }
        });
        zeroValueAtBottomThread.setName("zeroValueAtBottomThread");
        zeroValueAtBottomThread.setDaemon(true);
        zeroValueAtBottomThread.setPriority(Thread.NORM_PRIORITY);
        zeroValueAtBottomThread.start();        
    }                                          

    protected void setMinValueTo0()
    {                                           
        if (!manualValueMode)
        {
            manualValueMode = true;
            try { valueLimits = (ValueLimits) manualValueMode(startCalendar, endCalendar, selectionArea.getText()).clone(); } catch (CloneNotSupportedException ex) {}
        }
        
//        Double range = valueLimits.getMax() - valueLimits.getMin();
//        Double portion = (range / navigationYFactor);
        valueLimits.setMin(0D);
        setCursor(Cursor.WAIT_CURSOR);
        Thread zeroValueAtBottomThread = new Thread(new Runnable()
        {
            @Override
            @SuppressWarnings({"static-access"})
            public void run()
            {
//                setGraph(getTrend(false,getImageWidth(),getImageHeight(),manualValueMode,startCalendar,endCalendar, valueLimits)); // MiddleWare Invocation
                try { setGraph(getTrend(false,getImageWidth(),getImageHeight(),manualValueMode,startCalendar,endCalendar, valueLimits, selectionArea.getText(), lineType)); } catch (CloneNotSupportedException ex) { } // MiddleWare Invocation
            }
        });
        zeroValueAtBottomThread.setName("zeroValueAtBottomThread");
        zeroValueAtBottomThread.setDaemon(true);
        zeroValueAtBottomThread.setPriority(Thread.NORM_PRIORITY);
        zeroValueAtBottomThread.start();        
    }                                          

    // FrontEnd
    private void zoomOutYButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zoomOutYButtonActionPerformed
        enableGraphButton(false);
        enableStartTimeRelativeWidgets(false);
        enableEndTimeRelativeWidgets(false);
        zoomOutYButton();
}//GEN-LAST:event_zoomOutYButtonActionPerformed

    private void zoomOutYButton()
    {                                               
        if (!manualValueMode)
        {
            manualValueMode = true;
            //valueLimits = manualValueMode(selectionArea.getText());
            try { valueLimits = (ValueLimits) manualValueMode(startCalendar, endCalendar, selectionArea.getText()).clone(); } catch (CloneNotSupportedException ex) {}
        }
        Double range = valueLimits.getMax() - valueLimits.getMin();
        Double portion = (range / navigationYFactor);
        valueLimits.subMin(portion*2D); valueLimits.addMax(portion*2D);
        setCursor(Cursor.WAIT_CURSOR);
        Thread zoomOutYButtonThread = new Thread(new Runnable()
        {
            @Override
            @SuppressWarnings({"static-access"})
            public void run()
            {
//                setGraph(getTrend(false,getImageWidth(),getImageHeight(),manualValueMode,startCalendar,endCalendar, valueLimits)); // MiddleWare Invocation
                try { setGraph(getTrend(false,getImageWidth(),getImageHeight(),manualValueMode,startCalendar,endCalendar, valueLimits, selectionArea.getText(), lineType)); } catch (CloneNotSupportedException ex) { } // MiddleWare Invocation
            }
        });
        zoomOutYButtonThread.setName("zoomOutYButtonThread");
        zoomOutYButtonThread.setDaemon(true);
        zoomOutYButtonThread.setPriority(Thread.NORM_PRIORITY);
        zoomOutYButtonThread.start();        
}                                              

    // FrontEnd
    private void zoomInYButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zoomInYButtonActionPerformed
        enableGraphButton(false);
        enableStartTimeRelativeWidgets(false);
        enableEndTimeRelativeWidgets(false);
        zoomInYButton();
}//GEN-LAST:event_zoomInYButtonActionPerformed

    private void zoomInYButton()
    {                                              
        if (!manualValueMode)
        {
            manualValueMode = true;
//            valueLimits = manualValueMode(selectionArea.getText());
            try { valueLimits = (ValueLimits) manualValueMode(startCalendar, endCalendar, selectionArea.getText()).clone(); } catch (CloneNotSupportedException ex) {}
        }
        Double range = valueLimits.getMax() - valueLimits.getMin();
        Double portion = (range / navigationYFactor);
        valueLimits.addMin(portion); valueLimits.subMax(portion);
        setCursor(Cursor.WAIT_CURSOR);
        Thread zoomInYButtonThread = new Thread(new Runnable()
        {
            @Override
            @SuppressWarnings({"static-access"})
            public void run()
            {
//                setGraph(getTrend(false,getImageWidth(),getImageHeight(),manualValueMode,startCalendar,endCalendar, valueLimits)); // MiddleWare Invocation
                try { setGraph(getTrend(false,getImageWidth(),getImageHeight(),manualValueMode,startCalendar,endCalendar, valueLimits, selectionArea.getText(), lineType)); } catch (CloneNotSupportedException ex) { } // MiddleWare Invocation
            }
        });
        zoomInYButtonThread.setName("zoomInYButtonThread");
        zoomInYButtonThread.setDaemon(true);
        zoomInYButtonThread.setPriority(Thread.NORM_PRIORITY);
        zoomInYButtonThread.start();        
}                                             

    // FrontEnd
    private void upButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_upButtonActionPerformed
        enableGraphButton(false);
        enableStartTimeRelativeWidgets(false);
        enableEndTimeRelativeWidgets(false);
        upButton();
}//GEN-LAST:event_upButtonActionPerformed

    private void upButton()
    {                                         
        if (!manualValueMode)
        {
            manualValueMode = true;
//            valueLimits = manualValueMode(selectionArea.getText());
            try { valueLimits = (ValueLimits) manualValueMode(startCalendar, endCalendar, selectionArea.getText()).clone(); } catch (CloneNotSupportedException ex) {}
        }
        Double range = valueLimits.getMax() - valueLimits.getMin();
        Double portion = (range / navigationYFactor);
        valueLimits.addMin(portion); valueLimits.addMax(portion);
        setCursor(Cursor.WAIT_CURSOR);
        Thread upButtonThread = new Thread(new Runnable()
        {
            @Override
            @SuppressWarnings({"static-access"})
            public void run()
            {
//                setGraph(getTrend(false,getImageWidth(),getImageHeight(),manualValueMode,startCalendar,endCalendar, valueLimits)); // MiddleWare Invocation
                try { setGraph(getTrend(false,getImageWidth(),getImageHeight(),manualValueMode,startCalendar,endCalendar, valueLimits, selectionArea.getText(), lineType)); } catch (CloneNotSupportedException ex) { } // MiddleWare Invocation
            }
        });
        upButtonThread.setName("upButtonThread");
        upButtonThread.setDaemon(true);
        upButtonThread.setPriority(Thread.NORM_PRIORITY);
        upButtonThread.start();        
}                                        

    // FrontEnd
    private void vergunningDateChooserPanelOnSelectionChange(datechooser.events.SelectionChangedEvent evt) {//GEN-FIRST:event_vergunningDateChooserPanelOnSelectionChange
        vergunningPeriodList.setSelectedValue("Year", false);
        updateVergunningWidgets(Integer.parseInt(serversInLicenseField.getText()), vergunningDateChooserPanel.getSelectedDate(), vergunningPeriodList.getSelectedValue().toString());
}//GEN-LAST:event_vergunningDateChooserPanelOnSelectionChange

    // FrontEnd
    private void vergunningPeriodListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_vergunningPeriodListMouseClicked
        if (evt.getClickCount() == 2)
        {
            vergunningPeriodList.setEnabled(true);
        }
        else
        {
            if (vergunningPeriodList.isEnabled())
            {
                updateVergunningWidgets(Integer.parseInt(serversInLicenseField.getText()), vergunningDateChooserPanel.getSelectedDate(), vergunningPeriodList.getSelectedValue().toString());
            }
        }
}//GEN-LAST:event_vergunningPeriodListMouseClicked

    // MiddleWare
    private void vergunningCodeFieldMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_vergunningCodeFieldMouseClicked
        if ( evt.getClickCount() == 2 )
        {
            licenseCodePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "License Authorisation Code", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("STHeiti", 0, 10))); // NOI18N
            vergunningCodeField.setForeground(Color.WHITE);
        }
}//GEN-LAST:event_vergunningCodeFieldMouseClicked

    // MiddleWare
    private void vergunningCodeFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_vergunningCodeFieldActionPerformed
        if (vergunningCodeField.getText().equals(TIJDRESETTER))
        {
            Calendar cal = Calendar.getInstance();
            dcmDBClient.updateDCMTijd(cal);
            log("License Manually Validated and Timelock Reset",false,true,true);
            vergunningCodeField.setForeground(Color.BLACK);
            licenseCodePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "License Code", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("STHeiti", 0, 10))); // NOI18N
            applyVergunningButton.setEnabled(false);
            vergunningDoorvoeren();
            try { buildServerTree(); } catch (CloneNotSupportedException ex) {   } // who cares
        }
        else if (vergunningCodeField.getText().equals(VERGUNNINGTOEKENNER))
        {
            vergunningCodeField.setText(MD5Converter.getMD5SumFromString(activationCodeField.getText() + VERGUNNINGTOEKENNER));
            vergunningCodeField.setForeground(Color.BLACK);
            licenseCodePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "License Code", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("STHeiti", 0, 10))); // NOI18N
            applyVergunningButton.setEnabled(false);
            applySendVergunning();
            try { buildServerTree(); } catch (CloneNotSupportedException ex) {   } // who cares
        }
        else
        {
            licenseCodePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "License Code", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("STHeiti", 0, 10))); // NOI18N
            applyVergunningButton.setEnabled(false);
            applySendVergunning();
            try { buildServerTree(); } catch (CloneNotSupportedException ex) {   } // who cares
        }
}//GEN-LAST:event_vergunningCodeFieldActionPerformed

    // MiddleWare
    private void vergunningCodeFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_vergunningCodeFieldKeyReleased
        if ((vergunningCodeField.getText().length() > 0 ) && (evt.getKeyCode() != 10)) // Turns on buttons while editing field
        {
            applyVergunningButton.setEnabled(true); 
        }
        else
        {
            applyVergunningButton.setEnabled(false);
        }
}//GEN-LAST:event_vergunningCodeFieldKeyReleased

    // MiddleWare
    private void applyVergunningButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_applyVergunningButtonActionPerformed
        applySendVergunning();
}//GEN-LAST:event_applyVergunningButtonActionPerformed

    // MiddleWare
    private void orderLicenseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_orderLicenseButtonActionPerformed
        NumberFormat currencyFormatter = new DecimalFormat("#0.00"); NumberFormat discountFormatter = new DecimalFormat("#0");
        try { java.awt.Desktop.getDesktop().mail(java.net.URI.create("mailto:ronuitzaandam@gmail.com?subject=" + URLEncoder.encode( "DatacenterManager License Order", "UTF-8" ).replaceAll("\\+", "%20") + "&body=" + URLEncoder.encode( "\nDear customer service,\n\nWe would like to order the following license: " + activationCodeField.getText() + "\n\nDiscount: " + discountFormatter.format(DCMLicense.getDiscount(Integer.parseInt(serversInLicenseField.getText()))) + "%\nPrice: € " + currencyFormatter.format(DCMLicense.getDiscountPrice(Integer.parseInt(serversInLicenseField.getText()))) + "\nTotal: € " + currencyFormatter.format(DCMLicense.getTotalPrice(Integer.parseInt(serversInLicenseField.getText()), vergunningPeriodList.getSelectedValue().toString())) + "\n\nKind regards,\n\nYour Name\nYour Address\nYour Number", "UTF-8" ).replaceAll("\\+", "%20") )); } catch (IOException ex) { }
}//GEN-LAST:event_orderLicenseButtonActionPerformed

    // MiddleWare
    private void adminPanelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_adminPanelMouseClicked
        
}//GEN-LAST:event_adminPanelMouseClicked

    // MiddleWare
    private void graphLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_graphLabelMouseClicked
        if (evt.getButton() == 3) { popupMenus.showGraphMenu(evt); }
    }//GEN-LAST:event_graphLabelMouseClicked

    // FrontEnd
    private void statusLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_statusLabelMouseClicked
        statusLabel.setText("");
    }//GEN-LAST:event_statusLabelMouseClicked

    // FrontEnd
    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
        if ((trendIsViewable) && (loggedIn) && (selectionArea.getLineCount() > 0))
        {
            viewTrendPane(true);
            setCursor(Cursor.WAIT_CURSOR);
            Thread formComponentResizedThread = new Thread(new Runnable()
            {
                @Override
                @SuppressWarnings({"static-access"})
                public void run()
                {
//                    setGraph(getTrend(false,getImageWidth(),getImageHeight(),manualValueMode,startCalendar,endCalendar, valueLimits)); // MiddleWare Invocation
                    try { setGraph(getTrend(false,getImageWidth(),getImageHeight(),manualValueMode,startCalendar,endCalendar, valueLimits, selectionArea.getText(), lineType)); } catch (CloneNotSupportedException ex) { } // MiddleWare Invocation
                }
            });
            formComponentResizedThread.setName("formComponentResizedThread");
            formComponentResizedThread.setDaemon(true);
            formComponentResizedThread.setPriority(Thread.NORM_PRIORITY);
            formComponentResizedThread.start();        
        }
        else if (loggedIn)
        {
            viewTrendPane(false);
        }
    }//GEN-LAST:event_formComponentResized

    private void loginButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loginButtonActionPerformed
        enableGraphButton(false);
        enableStartTimeRelativeWidgets(false);
        enableEndTimeRelativeWidgets(false);
//        try { dcmLoginForm = new DCMLoginForm(this); } catch (UnsupportedLookAndFeelException ex) {    }
//        setState(JFrame.ICONIFIED);        
        dcmLoginForm.setVisible(true);
        dcmLoginForm.resetPassword();
        setVisible(false);
}//GEN-LAST:event_loginButtonActionPerformed

    private void addDCMUserButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addDCMUserButtonActionPerformed
        addDCMUserForm.setVisible(true);        
    }//GEN-LAST:event_addDCMUserButtonActionPerformed

    private void deleteDCMUserButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteDCMUserButtonActionPerformed
        deleteDCMUserForm.setVisible(true);
        DCMUser thisDCMUser = getDCMUser(Long.parseLong(userTable.getValueAt(userTable.getSelectedRow(), 0).toString()));
        deleteDCMUserForm.setDCMUser(thisDCMUser);
    }//GEN-LAST:event_deleteDCMUserButtonActionPerformed

    private void managerTabMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_managerTabMouseClicked
        if (managerTab.getSelectedIndex() == 1) // Admin
        {
            try { fillinUserTable(); } catch (CloneNotSupportedException ex) {  }
        }
    }//GEN-LAST:event_managerTabMouseClicked

    private void startCommanderButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startCommanderButtonActionPerformed
        new DCMCommander(debug).setVisible(true);
//        startCommander();
    }//GEN-LAST:event_startCommanderButtonActionPerformed

    
    private void searchFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_searchFieldFocusGained
        if (searchField.getText().isEmpty())
        {
            dropOnSearchField = true;
        }
        else if (searchField.getText().equals("Search"))
        {
            searchField.setText("");
        }
        else
        {
            if ((searchField.getSelectedText() != null) && (searchField.getSelectedText().length() == searchField.getText().length()) && ( dropOnSearchField ))
            {
                searchField.setText(searchField.getSelectedText().replace(" ", "|"));
                setSelectionArea(searchExactResources(searchField.getText()));
            }
            dropOnSearchField = false;            
        }
    }//GEN-LAST:event_searchFieldFocusGained

    private void startMonthPlusButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startMonthPlusButtonActionPerformed
        startMonthField.setText(Long.toString(Long.parseLong(startMonthField.getText())+1)); convertStartRelativeToAbsoluteCalendar();
    }//GEN-LAST:event_startMonthPlusButtonActionPerformed

    private void startMonthMinusButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startMonthMinusButtonActionPerformed
        startMonthField.setText(Long.toString(Long.parseLong(startMonthField.getText())-1)); convertStartRelativeToAbsoluteCalendar();
    }//GEN-LAST:event_startMonthMinusButtonActionPerformed

    private void startDayMinusButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startDayMinusButtonActionPerformed
        startDayField.setText(Long.toString(Long.parseLong(startDayField.getText())-1)); convertStartRelativeToAbsoluteCalendar();
    }//GEN-LAST:event_startDayMinusButtonActionPerformed

    private void startDayPlusButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startDayPlusButtonActionPerformed
        startDayField.setText(Long.toString(Long.parseLong(startDayField.getText())+1)); convertStartRelativeToAbsoluteCalendar();
    }//GEN-LAST:event_startDayPlusButtonActionPerformed

    private void startHourMinusButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startHourMinusButtonActionPerformed
        startHourField.setText(Long.toString(Long.parseLong(startHourField.getText())-1)); convertStartRelativeToAbsoluteCalendar();
    }//GEN-LAST:event_startHourMinusButtonActionPerformed

    private void startHourPlusButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startHourPlusButtonActionPerformed
        startHourField.setText(Long.toString(Long.parseLong(startHourField.getText())+1)); convertStartRelativeToAbsoluteCalendar();
    }//GEN-LAST:event_startHourPlusButtonActionPerformed

    private void startMinutePlusButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startMinutePlusButtonActionPerformed
        startMinuteField.setText(Long.toString(Long.parseLong(startMinuteField.getText())+1)); convertStartRelativeToAbsoluteCalendar();
    }//GEN-LAST:event_startMinutePlusButtonActionPerformed

    private void startMinuteMinusButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startMinuteMinusButtonActionPerformed
        startMinuteField.setText(Long.toString(Long.parseLong(startMinuteField.getText())-1)); convertStartRelativeToAbsoluteCalendar();
    }//GEN-LAST:event_startMinuteMinusButtonActionPerformed

    private void endMonthPlusButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_endMonthPlusButtonActionPerformed
        endMonthField.setText(Long.toString(Long.parseLong(endMonthField.getText())+1)); convertEndRelativeToAbsoluteCalendar();
    }//GEN-LAST:event_endMonthPlusButtonActionPerformed

    private void endDayPlusButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_endDayPlusButtonActionPerformed
        endDayField.setText(Long.toString(Long.parseLong(endDayField.getText())+1)); convertEndRelativeToAbsoluteCalendar();
    }//GEN-LAST:event_endDayPlusButtonActionPerformed

    private void endMonthMinusButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_endMonthMinusButtonActionPerformed
        endMonthField.setText(Long.toString(Long.parseLong(endMonthField.getText())-1)); convertEndRelativeToAbsoluteCalendar();
    }//GEN-LAST:event_endMonthMinusButtonActionPerformed

    private void endDayMinusButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_endDayMinusButtonActionPerformed
        endDayField.setText(Long.toString(Long.parseLong(endDayField.getText())-1)); convertEndRelativeToAbsoluteCalendar();
    }//GEN-LAST:event_endDayMinusButtonActionPerformed

    private void endHourPlusButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_endHourPlusButtonActionPerformed
        endHourField.setText(Long.toString(Long.parseLong(endHourField.getText())+1)); convertEndRelativeToAbsoluteCalendar();
    }//GEN-LAST:event_endHourPlusButtonActionPerformed

    private void endMinutePlusButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_endMinutePlusButtonActionPerformed
        endMinuteField.setText(Long.toString(Long.parseLong(endMinuteField.getText())+1)); convertEndRelativeToAbsoluteCalendar();
    }//GEN-LAST:event_endMinutePlusButtonActionPerformed

    private void startTimeIsRelativeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startTimeIsRelativeButtonActionPerformed
        resetStartTimeRelativeWidgets(); if (startTimeIsRelativeButton.isSelected()) { enableStartTimeRelativeWidgets(true); convertStartRelativeToAbsoluteCalendar(); } else { enableStartTimeRelativeWidgets(false); }
    }//GEN-LAST:event_startTimeIsRelativeButtonActionPerformed

    private void endTimeIsRelativeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_endTimeIsRelativeButtonActionPerformed
        resetEndTimeRelativeWidgets(); if (endTimeIsRelativeButton.isSelected()) { enableEndTimeRelativeWidgets(true); convertEndRelativeToAbsoluteCalendar(); } else { enableEndTimeRelativeWidgets(false); }
    }//GEN-LAST:event_endTimeIsRelativeButtonActionPerformed

    private void startMonthFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_startMonthFieldKeyReleased
        try { Long.parseLong(startMonthField.getText()); } catch (NumberFormatException ex) { startMonthField.setText("0"); }
    }//GEN-LAST:event_startMonthFieldKeyReleased

    private void startDayFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_startDayFieldKeyReleased
        try { Long.parseLong(startDayField.getText()); } catch (NumberFormatException ex) { startDayField.setText("0"); }
    }//GEN-LAST:event_startDayFieldKeyReleased

    private void startHourFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_startHourFieldKeyReleased
        try { Long.parseLong(startHourField.getText()); } catch (NumberFormatException ex) { startHourField.setText("0"); }
    }//GEN-LAST:event_startHourFieldKeyReleased

    private void startMinuteFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_startMinuteFieldKeyReleased
        try { Long.parseLong(startMinuteField.getText()); } catch (NumberFormatException ex) { startMinuteField.setText("0"); }
    }//GEN-LAST:event_startMinuteFieldKeyReleased

    private void endMonthFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_endMonthFieldKeyReleased
        try { Long.parseLong(endMonthField.getText()); } catch (NumberFormatException ex) { endMonthField.setText("0"); }
    }//GEN-LAST:event_endMonthFieldKeyReleased

    private void endDayFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_endDayFieldKeyReleased
        try { Long.parseLong(endDayField.getText()); } catch (NumberFormatException ex) { endDayField.setText("0"); }
    }//GEN-LAST:event_endDayFieldKeyReleased

    private void endHourFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_endHourFieldKeyReleased
        try { Long.parseLong(endHourField.getText()); } catch (NumberFormatException ex) { endHourField.setText("0"); }
    }//GEN-LAST:event_endHourFieldKeyReleased

    private void endMinuteFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_endMinuteFieldKeyReleased
        try { Long.parseLong(endMinuteField.getText()); } catch (NumberFormatException ex) { endMinuteField.setText("0"); }
    }//GEN-LAST:event_endMinuteFieldKeyReleased

    private void graphButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_graphButtonActionPerformed
        if (!graphButton.isSelected())
        {
            enableGraphButton(true);
        }
        else
        {
            enableGraphButton(false);
        }
    }//GEN-LAST:event_graphButtonActionPerformed

    private void endMinuteMinusButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_endMinuteMinusButtonActionPerformed

        endMinuteField.setText(Long.toString(Long.parseLong(endMinuteField.getText()) - 1));         convertEndRelativeToAbsoluteCalendar();     }//GEN-LAST:event_endMinuteMinusButtonActionPerformed

    private void endHourMinusButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_endHourMinusButtonActionPerformed

        endHourField.setText(Long.toString(Long.parseLong(endHourField.getText()) - 1));         convertEndRelativeToAbsoluteCalendar();     }//GEN-LAST:event_endHourMinusButtonActionPerformed

    private void importButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importButtonActionPerformed
        dcmDBClient.importInventory();
        try { buildServerTree(); } catch (CloneNotSupportedException ex) {   } // who cares
    }//GEN-LAST:event_importButtonActionPerformed

    private void exportButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportButtonActionPerformed
        dcmDBClient.exportInventory();
    }//GEN-LAST:event_exportButtonActionPerformed

    private void minusButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_minusButtonActionPerformed
        vergunningPeriodList.setSelectedValue("Year", false);
        if (serversInLicenseField.getText().length()==0) { serversInLicenseField.setText("1"); }
        int servers = 0;
        if (Integer.parseInt(serversInLicenseField.getText()) > 1  )
        {
            servers = Integer.parseInt(serversInLicenseField.getText()); servers--; serversInLicenseField.setText(Integer.toString(servers));
        }
        updateVergunningWidgets(Integer.parseInt(serversInLicenseField.getText()), vergunningDateChooserPanel.getSelectedDate(), vergunningPeriodList.getSelectedValue().toString());
    }//GEN-LAST:event_minusButtonActionPerformed

    private void plusButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_plusButtonActionPerformed
        vergunningPeriodList.setSelectedValue("Year", false);
        if (serversInLicenseField.getText().length()==0) { serversInLicenseField.setText("1"); }
        int servers = 0;
        if (Integer.parseInt(serversInLicenseField.getText()) < 1000  )
        {
            servers = Integer.parseInt(serversInLicenseField.getText()); servers++; serversInLicenseField.setText(Integer.toString(servers));
        }
        updateVergunningWidgets(Integer.parseInt(serversInLicenseField.getText()), vergunningDateChooserPanel.getSelectedDate(), vergunningPeriodList.getSelectedValue().toString());
    }//GEN-LAST:event_plusButtonActionPerformed

    private void serversInLicenseFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_serversInLicenseFieldKeyReleased
        if (serversInLicenseField.getText().length()>0)
        {
            updateVergunningWidgets(Integer.parseInt(serversInLicenseField.getText()), vergunningDateChooserPanel.getSelectedDate(), vergunningPeriodList.getSelectedValue().toString());
        }
    }//GEN-LAST:event_serversInLicenseFieldKeyReleased

    private void vergunningPeriodListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_vergunningPeriodListValueChanged
        vergunningPeriodList.setEnabled(false);
        updateVergunningWidgets(Integer.parseInt(serversInLicenseField.getText()), vergunningDateChooserPanel.getSelectedDate(), vergunningPeriodList.getSelectedValue().toString());
    }//GEN-LAST:event_vergunningPeriodListValueChanged

    private void logScrollerCaretPositionChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_logScrollerCaretPositionChanged
    }//GEN-LAST:event_logScrollerCaretPositionChanged

    private void sendEmailButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendEmailButtonActionPerformed
        try { java.awt.Desktop.getDesktop().mail(java.net.URI.create("mailto:ronuitzaandam@gmail.com?subject=" + URLEncoder.encode( subjectField.getText(), "UTF-8" ).replaceAll("\\+", "%20") + "&body=" + URLEncoder.encode( messagePane.getText(), "UTF-8" ).replaceAll("\\+", "%20"))); } catch (IOException ex) { }
    }//GEN-LAST:event_sendEmailButtonActionPerformed

    private void clearHelpdeskButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearHelpdeskButtonActionPerformed
        subjectField.setText("Subject");
        messagePane.setText("Your Message");
    }//GEN-LAST:event_clearHelpdeskButtonActionPerformed

    private void subjectFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_subjectFieldFocusGained
        if (subjectField.getText().equals("Subject")) { subjectField.setText(""); }
    }//GEN-LAST:event_subjectFieldFocusGained

    private void messagePaneFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_messagePaneFocusGained
        if (messagePane.getText().equals("Your Message")) { messagePane.setText(""); }
    }//GEN-LAST:event_messagePaneFocusGained

    private void deletePresetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deletePresetButtonActionPerformed
        Boolean startsWithANumber = true;
        String selectViewBoxText = "";
        selectViewBoxText = String.valueOf(selectPresetBox.getSelectedItem());
        if ( selectViewBoxText == null ) { selectViewBoxText = ""; }
        String[] lineArray = new String[2];
        lineArray = selectViewBoxText.split("\\s+");
        if (( lineArray[0] != null ) && ( ! lineArray[0].isEmpty() ))
        {
            try { Long.parseLong(lineArray[0].toString()); } catch (NumberFormatException ex) {startsWithANumber = false;}

            if (startsWithANumber)
            {
                DCMPreset dcmPreset = dcmDBClient.getDCMPreset(Long.parseLong(lineArray[0].toString()));
                dcmDBClient.deleteDCMPreset(dcmPreset.getId());
                selectPresetBox.setSelectedItem("");
                deletePresetButton.setEnabled(false);
                savePresetButton.setEnabled(false);
            }
        }
    }//GEN-LAST:event_deletePresetButtonActionPerformed

    private void savePresetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_savePresetButtonActionPerformed
        Boolean startsWithANumber = true;
        String selectViewBoxText = "";
        selectViewBoxText = String.valueOf(selectPresetBox.getSelectedItem());
        if ( selectViewBoxText == null ) { selectViewBoxText = ""; }
        String[] lineArray = new String[2];
        lineArray = selectViewBoxText.split("\\s+", 2);
        if (( lineArray[0] != null ) && ( ! lineArray[0].isEmpty() ))
        {
            try { Long.parseLong(lineArray[0].toString()); } catch (NumberFormatException ex) {startsWithANumber = false;}

            if (startsWithANumber)
            {
                DCMPreset dcmPreset = dcmDBClient.getDCMPreset(Long.parseLong(lineArray[0].toString()));

                dcmPreset.setPresetName(lineArray[1]);

                if (startDateChooserPanel.isEnabled()) { dcmPreset.setStartCalendar(startDateChooserPanel.getSelectedDate()); } else { dcmPreset.setStartCalendar(startCalendar); } 
                dcmPreset.setStartCalendarRelative(startTimeIsRelativeButton.isSelected());
                dcmPreset.setStartMonthSupplement(Integer.parseInt(startMonthField.getText()));
                dcmPreset.setStartDaySupplement(Integer.parseInt(startDayField.getText()));
                dcmPreset.setStartHourSupplement(Integer.parseInt(startHourField.getText()));
                dcmPreset.setStartMinuteSupplement(Integer.parseInt(startMinuteField.getText()));

                if (endDateChooserPanel.isEnabled()) { dcmPreset.setEndCalendar(endDateChooserPanel.getSelectedDate()); } else { dcmPreset.setEndCalendar(endCalendar); } 
                dcmPreset.setEndCalendarRelative(endTimeIsRelativeButton.isSelected());
                dcmPreset.setEndMonthSupplement(Integer.parseInt(endMonthField.getText()));
                dcmPreset.setEndDaySupplement(Integer.parseInt(endDayField.getText()));
                dcmPreset.setEndHourSupplement(Integer.parseInt(endHourField.getText()));
                dcmPreset.setEndMinuteSupplement(Integer.parseInt(endMinuteField.getText()));

                dcmPreset.setEnableSearch(true);
                dcmPreset.setSearchString(searchField.getText());
                dcmPreset.setSearchExact(searchExactWasUsed);
        //        thisView.setSelectedResources(selectionArea.getText()); // Perhaps later (this is a risk on inconsistency)
                dcmPreset.setSelectedResources("");
                dcmPreset.setShared(false);

                dcmDBClient.updateDCMPreset(dcmPreset);
            }
            else
            {
                DCMPreset dcmPreset = new DCMPreset();

                dcmPreset.setUserId(dcmUser.getId());
                dcmPreset.setPresetName(String.valueOf(selectPresetBox.getSelectedItem()));
                dcmPreset.setPresetDescription("");

                if (startDateChooserPanel.isEnabled()) { dcmPreset.setStartCalendar(startDateChooserPanel.getSelectedDate()); } else { dcmPreset.setStartCalendar(startCalendar); } 
                dcmPreset.setStartCalendarRelative(startTimeIsRelativeButton.isSelected());
                dcmPreset.setStartMonthSupplement(Integer.parseInt(startMonthField.getText()));
                dcmPreset.setStartDaySupplement(Integer.parseInt(startDayField.getText()));
                dcmPreset.setStartHourSupplement(Integer.parseInt(startHourField.getText()));
                dcmPreset.setStartMinuteSupplement(Integer.parseInt(startMinuteField.getText()));

                if (endDateChooserPanel.isEnabled()) { dcmPreset.setEndCalendar(endDateChooserPanel.getSelectedDate()); } else { dcmPreset.setEndCalendar(endCalendar); } 
                dcmPreset.setEndCalendarRelative(endTimeIsRelativeButton.isSelected());
                dcmPreset.setEndMonthSupplement(Integer.parseInt(endMonthField.getText()));
                dcmPreset.setEndDaySupplement(Integer.parseInt(endDayField.getText()));
                dcmPreset.setEndHourSupplement(Integer.parseInt(endHourField.getText()));
                dcmPreset.setEndMinuteSupplement(Integer.parseInt(endMinuteField.getText()));

                dcmPreset.setEnableSearch(true);
                dcmPreset.setSearchString(searchField.getText());
                dcmPreset.setSearchExact(searchExactWasUsed);
        //        thisView.setSelectedResources(selectionArea.getText()); // Perhaps later (this is a risk on inconsistency)
                dcmPreset.setSelectedResources("");
                dcmPreset.setShared(false);

                dcmDBClient.insertDCMPreset(dcmPreset);
            }
        }
        else
        {
                deletePresetButton.setEnabled(false);
                savePresetButton.setEnabled(false);            
        }
    }//GEN-LAST:event_savePresetButtonActionPerformed

    private void selectPresetBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectPresetBoxActionPerformed
        Boolean startsWithANumber = true;
        String selectViewBoxText = "";
        selectViewBoxText = String.valueOf(selectPresetBox.getSelectedItem());
        if ( selectViewBoxText == null ) { selectViewBoxText = ""; }
        String[] lineArray = new String[2];
        lineArray = selectViewBoxText.split("\\s+");
        if (( lineArray[0] != null ) && ( ! lineArray[0].isEmpty() ))
        {
            try { Long.parseLong(lineArray[0].toString()); } catch (NumberFormatException ex) {startsWithANumber = false;}

            if (startsWithANumber)
            {
                deletePresetButton.setEnabled(true);
                savePresetButton.setEnabled(true);
                
                DCMPreset dcmPreset = dcmDBClient.getDCMPreset(Long.parseLong(lineArray[0].toString()));
                
                startCalendar = Calendar.getInstance(); startCalendar = dcmPreset.getStartCalendar();
                endCalendar = Calendar.getInstance(); endCalendar = dcmPreset.getEndCalendar();

                startTimeIsRelativeButton.setSelected(dcmPreset.getStartCalendarRelative()); // Just set the RelativeTimeButton
                enableStartTimeRelativeWidgets(dcmPreset.getStartCalendarRelative()); // Either Enable the Left Time or the Right (Relative) Time Widgets
                startMonthField.setText(Integer.toString(dcmPreset.getStartMonthSupplement()));
                startDayField.setText(Integer.toString(dcmPreset.getStartDaySupplement()));
                startHourField.setText(Integer.toString(dcmPreset.getStartHourSupplement()));
                startMinuteField.setText(Integer.toString(dcmPreset.getStartMinuteSupplement()));
                if ( dcmPreset.getStartCalendarRelative() ) { convertStartRelativeToAbsoluteCalendar(); } else { updateTimeWidgets(startCalendar,endCalendar); }
                
                endTimeIsRelativeButton.setSelected(dcmPreset.getEndCalendarRelative()); // Just set the RelativeTimeButton
                enableEndTimeRelativeWidgets(dcmPreset.getEndCalendarRelative()); // Either Enable the Left Time or the Right (Relative) Time Widgets
                endMonthField.setText(Integer.toString(dcmPreset.getEndMonthSupplement()));
                endDayField.setText(Integer.toString(dcmPreset.getEndDaySupplement()));
                endHourField.setText(Integer.toString(dcmPreset.getEndHourSupplement()));
                endMinuteField.setText(Integer.toString(dcmPreset.getEndMinuteSupplement()));
                if ( dcmPreset.getEndCalendarRelative() ) { convertEndRelativeToAbsoluteCalendar(); } else { updateTimeWidgets(startCalendar,endCalendar); }
                
//                updateTimeWidgets(startCalendar,endCalendar);

                searchField.setText(dcmPreset.getSearchString());
                
                if (dcmPreset.getSearchExact())   { searchExactWasUsed = true; setSelectionArea(searchExactResources(searchField.getText())); }
                else                            { searchExactWasUsed = false; setSelectionArea(searchNonExactResources(searchField.getText())); }
                enableGraphButton(false); enableGraphButton(true);
            }
            else
            {
                deletePresetButton.setEnabled(false);
                if (selectedResources > 0) { savePresetButton.setEnabled(true); } else { savePresetButton.setEnabled(false); }
            }
        }
        else
        {
                deletePresetButton.setEnabled(false);
                savePresetButton.setEnabled(false);            
        }
    }//GEN-LAST:event_selectPresetBoxActionPerformed

    private void selectPresetBoxPopupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_selectPresetBoxPopupMenuWillBecomeVisible
        ArrayList<DCMPreset> dcmPresetList = getDCMViewList();
        selectPresetBox.removeAllItems(); selectPresetBox.addItem("");
        for (DCMPreset dcmPreset:dcmPresetList) { selectPresetBox.addItem(dcmPreset.getId() + " " + dcmPreset.getPresetName()); }
    }//GEN-LAST:event_selectPresetBoxPopupMenuWillBecomeVisible

    private void selectPresetBoxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_selectPresetBoxFocusGained
        if (String.valueOf(selectPresetBox.getSelectedItem()).equals("Presets"))
        {
            selectPresetBox.setSelectedItem("");
        }
    }//GEN-LAST:event_selectPresetBoxFocusGained

    private void activationCodeFieldCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_activationCodeFieldCaretUpdate
        if (activationCodeField.getText().length() > 2) { orderLicenseButton.setEnabled(true); vergunningCodeField.setEditable(true); } else { orderLicenseButton.setEnabled(false); vergunningCodeField.setEditable(false); }
    }//GEN-LAST:event_activationCodeFieldCaretUpdate

    // FrontEnd
    private void startDateChooserPanelOnSelectionChange(datechooser.events.SelectionChangedEvent evt)//GEN-FIRST:event_startDateChooserPanelOnSelectionChange
    {//GEN-HEADEREND:event_startDateChooserPanelOnSelectionChange
        startCalendar = startDateChooserPanel.getSelectedDate();
        startCalendar.set(Calendar.HOUR_OF_DAY, startHourSlider.getValue());
        startCalendar.set(Calendar.MINUTE, startMinuteSlider.getValue());
        startCalendar.set(Calendar.SECOND, (int)0);
    }//GEN-LAST:event_startDateChooserPanelOnSelectionChange

//    private void updateLicenseWidgets()
//    {
//        Calendar thisVergunningStartCalendar; thisVergunningStartCalendar = Calendar.getInstance();
//        Calendar thisVergunningEndCalendar; thisVergunningEndCalendar = Calendar.getInstance();
//        thisVergunningStartCalendar.set(Calendar.HOUR_OF_DAY, (int)0);
//        thisVergunningStartCalendar.set(Calendar.MINUTE, (int)0);
//        thisVergunningStartCalendar.set(Calendar.SECOND, (int)0);
//
//        //Compile the activation string (Do before setting details)
//        String activationCodeString = updateVergunningWidgets(Integer.parseInt(serversInLicenseField.getText()),vergunningDateChooserPanel.getSelectedDate(),vergunningPeriodList.getSelectedValue().toString());
//
//        // set the license details table
//        vergunningDetailsTable.setValueAt(dcmVergunning.getVergunningPeriod(), 1, 1);
//        vergunningDetailsTable.setValueAt(DCMTools.getHumanDate(dcmVergunning.getVergunningStartDate()), 2, 1 );
//        vergunningDetailsTable.setValueAt(DCMTools.getHumanDate(dcmVergunning.getVergunningEndDate()), 3, 1 );
//        vergunningDetailsTable.setValueAt(dcmVergunning.getServersInLicense(), 4, 1 );
//
//        activationCodeField.setText(activationCodeString);
//        
//        if (activationCodeField.getText().length() > 2) { requestVergunningButton.setEnabled(true); } else { requestVergunningButton.setEnabled(false); }
//        vergunningCodeField.setText("");
//        vergunningCodeField.setEditable(true);        
//    }
//    
    private void enableGraphButton(boolean enableParam)
    {
        if (enableParam)
        {
            graphButton.setSelected(true);
            
            // Timer for blinking button
            blinkGraphButtonAction = new TimerTask() { @Override public void run() { if (graphButtonOn) { graphButton.setIcon(graphIconOff); graphButtonOn = false; } else { graphButton.setIcon(graphIconOn); graphButtonOn = true; } } };
            blinkGraphButtonActionTimer = new java.util.Timer();
            blinkGraphButtonActionTimer.schedule(blinkGraphButtonAction, 0L, 500L);

            // Timer for generating graph repeatedly
            generateGraphAction = new TimerTask() { @Override public void run() { generateGraph(); } };
            generateGraphActionTimer = new java.util.Timer();
            generateGraphActionTimer.schedule(generateGraphAction, 0L, 60000L);
        }
        else
        {
            graphButton.setSelected(false); graphButton.setIcon(graphIconOn); graphButtonOn = true; 
            
            // Timer for blinking button
            if (blinkGraphButtonActionTimer != null)
            {
                blinkGraphButtonActionTimer.cancel();
                blinkGraphButtonActionTimer.purge();                
            }

            // Timer for generating graph repeatedly
            if (generateGraphActionTimer != null)
            {
                generateGraphActionTimer.cancel();
                generateGraphActionTimer.purge();            
            }
        }
    }

    protected void generateGraph()
    {
        setCursor(Cursor.WAIT_CURSOR);
        Thread trendAnalysisButtonThread = new Thread(new Runnable()
        {
            @Override
            @SuppressWarnings({"static-access"})
            public void run()
            {
                if (startTimeIsRelativeButton.isSelected()) { convertStartRelativeToAbsoluteCalendar(); }
                if (endTimeIsRelativeButton.isSelected()) { convertEndRelativeToAbsoluteCalendar(); }
//                setGraph(getTrend(false,getImageWidth(),getImageHeight(),manualValueMode,startCalendar,endCalendar, valueLimits)); // MiddleWare Invocation
                try { setGraph(getTrend(false,getImageWidth(),getImageHeight(),manualValueMode,startCalendar,endCalendar, valueLimits, selectionArea.getText(), lineType)); } catch (CloneNotSupportedException ex) { } // MiddleWare Invocation
                if (debug) { try { log(getData(startCalendar,endCalendar, selectionArea.getText()).toString(),false,true,true); } catch (CloneNotSupportedException ex) { } } // MiddleWare Invocation
            }
        });
        trendAnalysisButtonThread.setName("trendAnalysisButtonThread");
        trendAnalysisButtonThread.setDaemon(true);
        trendAnalysisButtonThread.setPriority(Thread.NORM_PRIORITY);
        trendAnalysisButtonThread.start();                    
    }
    
    private void resetStartTimeRelativeWidgets()
    {
        startMonthField.setText("0");
        startDayField.setText("0");
        startHourField.setText("0");
        startMinuteField.setText("0");
    }
    
    private void enableStartTimeRelativeWidgets(boolean enableParam)
    {
        if (enableParam)
        {
            startDateChooserPanel.setEnabled(false);
            startDateChooserPanel.setLocked(true);
            startTimeIsRelativeButton.setForeground(Color.WHITE);
//            blinkStartNowButtonAction = new TimerTask() { @Override public void run() { if (startTimeIsRelativeButton.getForeground() == Color.BLACK) { startTimeIsRelativeButton.setForeground(Color.WHITE); } else { startTimeIsRelativeButton.setForeground(Color.BLACK); } } };
//            blinkStartNowButtonTimer = new java.util.Timer();
//            blinkStartNowButtonTimer.schedule(blinkStartNowButtonAction, 0L, 500L);
            if (!startTimeIsRelativeButton.isSelected()) {startTimeIsRelativeButton.setSelected(true);}
        }
        else
        {
            startDateChooserPanel.setEnabled(true);
            startDateChooserPanel.setLocked(false);
//            if ( blinkStartNowButtonTimer != null )
//            {
//                blinkStartNowButtonTimer.cancel();
//                blinkStartNowButtonTimer.purge();
//            }
            startTimeIsRelativeButton.setForeground(Color.BLACK);
            if (startTimeIsRelativeButton.isSelected()) {startTimeIsRelativeButton.setSelected(false);}
        }
//        startDateChooserPanel.setEnabled(!enableParam);
        startHourSlider.setEnabled(!enableParam);
        startMinuteSlider.setEnabled(!enableParam);
        
        startMonthPlusButton.setEnabled(enableParam);
        startDayPlusButton.setEnabled(enableParam);
        startHourPlusButton.setEnabled(enableParam);
        startMinutePlusButton.setEnabled(enableParam);
        startMonthMinusButton.setEnabled(enableParam);
        startDayMinusButton.setEnabled(enableParam);
        startHourMinusButton.setEnabled(enableParam);
        startMinuteMinusButton.setEnabled(enableParam);
        startMonthField.setEnabled(enableParam);
        startDayField.setEnabled(enableParam);
        startHourField.setEnabled(enableParam);
        startMinuteField.setEnabled(enableParam);
    }
    
    private void resetEndTimeRelativeWidgets()
    {
        endMonthField.setText("0");
        endDayField.setText("0");
        endHourField.setText("0");
        endMinuteField.setText("0");
    }
    
    private void enableEndTimeRelativeWidgets(boolean enableParam)
    {
        if (enableParam)
        {
            endDateChooserPanel.setEnabled(false);
            endDateChooserPanel.setLocked(true);
            endTimeIsRelativeButton.setForeground(Color.WHITE);
//            blinkEndNowButtonAction = new TimerTask() { @Override public void run() { if (endTimeIsRelativeButton.getForeground() == Color.BLACK) { endTimeIsRelativeButton.setForeground(Color.WHITE); } else { endTimeIsRelativeButton.setForeground(Color.BLACK); } } };
//            blinkEndNowButtonTimer = new java.util.Timer();
//            blinkEndNowButtonTimer.schedule(blinkEndNowButtonAction, 0L, 500L);
            if (!endTimeIsRelativeButton.isSelected()) {endTimeIsRelativeButton.setSelected(true);}
        }
        else
        {
            endDateChooserPanel.setEnabled(true);
            endDateChooserPanel.setLocked(false);
//            if ( blinkEndNowButtonTimer != null )
//            {
//                blinkEndNowButtonTimer.cancel();
//                blinkEndNowButtonTimer.purge();
//            }
//            blinkEndNowButtonTimer.cancel();
//            blinkEndNowButtonTimer.purge();
            endTimeIsRelativeButton.setForeground(Color.BLACK);
            if (endTimeIsRelativeButton.isSelected()) {endTimeIsRelativeButton.setSelected(false);}
        }
//        endDateChooserPanel.setEnabled(!enableParam);
        endHourSlider.setEnabled(!enableParam);
        endMinuteSlider.setEnabled(!enableParam);
        
        endMonthPlusButton.setEnabled(enableParam);
        endDayPlusButton.setEnabled(enableParam);
        endHourPlusButton.setEnabled(enableParam);
        endMinutePlusButton.setEnabled(enableParam);
        endMonthMinusButton.setEnabled(enableParam);
        endDayMinusButton.setEnabled(enableParam);
        endHourMinusButton.setEnabled(enableParam);
        endMinuteMinusButton.setEnabled(enableParam);
        endMonthField.setEnabled(enableParam);
        endDayField.setEnabled(enableParam);
        endHourField.setEnabled(enableParam);
        endMinuteField.setEnabled(enableParam);
    }
    
    private void fillinUserTable() throws CloneNotSupportedException
    {
        ArrayList<DCMUser> dcmUserList = getDCMUserList();
        int userTableRowsNeeded = dcmUserList.size();
        
        Object[][]  thisData = new Object[userTableRowsNeeded][4];
        String[]    header = new String [] {"Id", "Username", "Password", "Administrator"};
        Class[]     types = new Class [] {java.lang.Long.class, java.lang.String.class, java.lang.String.class, java.lang.Boolean.class};
        
        userTable.setModel(new javax.swing.table.DefaultTableModel(thisData,header)
        {
            Class[] types = new Class [] {java.lang.Long.class, java.lang.String.class, java.lang.String.class, java.lang.Boolean.class};
            boolean[] canEdit = new boolean [] { false, true, true, true };

            @Override
            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        userTable.setToolTipText("");
        userTable.setAutoCreateRowSorter(true);
        userTable.setAutoscrolls(false);
        userTable.setDoubleBuffered(true);
        userTable.setFocusable(false);
        userTable.setMaximumSize(new java.awt.Dimension(55, 250));
        userTable.setMinimumSize(new java.awt.Dimension(55, 250));
        userTable.setName("userTable"); // NOI18N
        userTable.setPreferredSize(new java.awt.Dimension(55, 250));
        userTable.setRowSelectionAllowed(true);
        userTable.setSelectionBackground(new java.awt.Color(51, 102, 255));
        userTable.setShowGrid(false);
        userTable.setSize(new java.awt.Dimension(55, 250));
        userScroller.setViewportView(userTable);
        
        if (dcmUser.getAdministrator()) { userTable.setEnabled(true); } else { userTable.setEnabled(false); }
        
        int rowCounter = 0;
        for (DCMUser thisDCMUser:dcmUserList)
        {
            userTable.setValueAt(thisDCMUser.getId(), rowCounter, 0);
            userTable.setValueAt(thisDCMUser.getUsername(), rowCounter, 1);
            userTable.setValueAt(thisDCMUser.getPassword().replaceAll(".", "*"), rowCounter, 2); // don't go with \\. as suggested
            userTable.setValueAt(thisDCMUser.getAdministrator(), rowCounter, 3);
            rowCounter++;
        }

//      Implement the userTable ActionListener
        
        userTableCellAction = new AbstractAction()
        {
            @Override public void actionPerformed(ActionEvent e)
            {
                DCMTableCellListener userTableCellListener = (DCMTableCellListener)e.getSource();
                if (userTableCellListener.getOldValue() != userTableCellListener.getNewValue())
                {
//                    log("Id    : " + userTableCellListener.getTable().getValueAt(userTableCellListener.getRow(), 0) + " Row   : " + userTableCellListener.getRow() + " Column: " + userTableCellListener.getColumn() + " Old   : " + userTableCellListener.getOldValue() + " New   : " + userTableCellListener.getNewValue() ,true,true,true );
                    
                    int column = userTableCellListener.getColumn();
                    switch (column) // Beware these table column numbers belong to the table, not to the object Host or Host Table in the metaDB (columnnumbers might differ)
                    {
                        case 1:     {
                                        DCMUser dcmUser = getDCMUser(Long.parseLong( userTableCellListener.getTable().getValueAt(userTableCellListener.getRow(), 0).toString()));
                                        dcmUser.setUsername((String)userTableCellListener.getNewValue());
                                        try { updateDCMUser(dcmUser); } catch (CloneNotSupportedException ex) {  };
                                        log("Info:    DCMDesktop: DCMUser updated",true,true,true);
                                        break;
                                    }
                        case 2:     {
                                        DCMUser dcmUser = getDCMUser(Long.parseLong( userTableCellListener.getTable().getValueAt(userTableCellListener.getRow(), 0).toString()));
                                        dcmUser.setPassword(MD5Converter.getMD5SumFromString((String)userTableCellListener.getNewValue()));
                                        try { updateDCMUser(dcmUser); } catch (CloneNotSupportedException ex) {  };
                                        log("Info:    DCMDesktop: DCMPassword updated",true,true,true);
                                        break;
                                    }
                    }
//                    dcmDBClient.updateResource(resource); Server server = new Server();
//                    try { updateDCMUser(dcmUser); } catch (CloneNotSupportedException ex) {  }
                }
            }
        };
        userTableCellListener = new DCMTableCellListener(userTable, userTableCellAction);
        
        // And another listener, just to overcome the boolean field in this user table
        userTable.getModel().addTableModelListener(new TableModelListener()
        {
            @Override public void tableChanged(TableModelEvent e)
            {
                if (e.getColumn() == 3) // NEVER forget this one if the columns in this table changes
                {
                    TableModel model = (TableModel)e.getSource();
                    Object data = model.getValueAt(e.getLastRow(), e.getColumn());
                    long id = Long.parseLong(model.getValueAt(e.getLastRow(), 0).toString());
//                    log("id: " + model.getValueAt(e.getLastRow(), 0) + " data: " + data,true,true,true);
                    DCMUser dcmUser = new DCMUser(); try { dcmUser = (DCMUser) getDCMUser(Long.parseLong(model.getValueAt(e.getLastRow(), 0).toString() )).clone(); } catch (CloneNotSupportedException ex) {}
//                    log("dcmUser: " + dcmUser.toString(),true,true,true);
                    if (data.toString().equals("true")) { dcmUser.setAdministrator(true); } else { dcmUser.setAdministrator(false); }
                    try { updateDCMUser(dcmUser); } catch (CloneNotSupportedException ex) {   }
                }
            }
        });    
    }
    
    // MiddleWare
    public void updateDCMUser(DCMUser userParam) throws CloneNotSupportedException
    {
        dcmDBClient.updateDCMUser(userParam);
    }
    
    // Begin MiddleWare
    public DCMUser getDCMUser(long idParam)
    {
        DCMUser thisDCMUser = (DCMUser) dcmDBClient.getDCMUser(idParam);
        return thisDCMUser;
    }
    // End MiddleWare
    
    // Begin MiddleWare
    public ArrayList<DCMUser> getDCMUserList()
    {
        ArrayList<DCMUser> dcmUserList = dcmDBClient.getDCMUserList();
        return dcmUserList;
    }
    // End MiddleWare

    // Begin MiddleWare
    public DCMPreset getDCMView(long idParam)
    {
        DCMPreset thisDCMView = (DCMPreset) dcmDBClient.getDCMPreset(idParam);
        return thisDCMView;
    }
    // End MiddleWare
    
    // Begin MiddleWare
    public ArrayList<DCMPreset> getDCMViewList()
    {
        ArrayList<DCMPreset> dcmPresetList = dcmDBClient.getDCMPresetList();
        return dcmPresetList;
    }
    // End MiddleWare

    // FrontEnd
    protected void centerTime(MouseEvent evt)
    {
        // Graph Calculation

        // Get location on graph and turn it into a percentage offset from 50% of the graphwidth (can be negative of positive offsetpercentage)
//        long offsetPercentage = ( evt.getX() / (((rrdGraph.getRrdGraphInfo().getWidth()+90) / 2) / 100) - 100);
        long offsetPercentage = ( evt.getX() / (((graphLabel.getIcon().getIconWidth()+90) / 2) / 100) - 100);

        // Time Preservation

        // Preserve calendars (calendar bug)
        long start = startCalendar.getTimeInMillis();
        long end   = endCalendar.getTimeInMillis();
        long diff  = (endCalendar.getTimeInMillis() - startCalendar.getTimeInMillis()) / navigationXFactor;
        long newstart = start - diff;
        long newend   = end - diff;

        // Get the shiftscale (half the timescale in milisecs shown within graph)
        long halfPeriod = ((endCalendar.getTimeInMillis() - startCalendar.getTimeInMillis()) / 2 );
        long shiftMiliseconds = offsetPercentage * (halfPeriod/100L); // if we clicked on -87% or 87% (left or right to center of graph, then we also move that percentage on the visible period to)

        startCalendar = Calendar.getInstance(); startCalendar.setTimeInMillis(start+shiftMiliseconds);
        endCalendar =   Calendar.getInstance(); endCalendar.setTimeInMillis(end+shiftMiliseconds);

//        log(" offsetPercentage: " + offsetPercentage + " secscale: " + (halfPeriod/1000) + " partsec: " + (shiftMiliseconds /1000),true, true, true);
        updateTimeWidgets(startCalendar,endCalendar);
        setCursor(Cursor.WAIT_CURSOR);
        Thread centerTimeThread = new Thread(new Runnable()
        {
            @Override
            @SuppressWarnings({"static-access"})
            public void run()
            {
//                setGraph(getTrend(false,getImageWidth(),getImageHeight(),manualValueMode,startCalendar,endCalendar, valueLimits)); // MiddleWare Invocation
                try { setGraph(getTrend(false,getImageWidth(),getImageHeight(),manualValueMode,startCalendar,endCalendar, valueLimits, selectionArea.getText(), lineType)); } catch (CloneNotSupportedException ex) { } // MiddleWare Invocation
            }
        });
        centerTimeThread.setName("centerTimeThread");
        centerTimeThread.setDaemon(true);
        centerTimeThread.setPriority(Thread.NORM_PRIORITY);
        centerTimeThread.start();        
    }
    
    // MiddleWare
    private String updateVergunningWidgets(int serversInLicenseParam, Calendar selectedDateParam, String vergunningPeriodParam) // Basically sets the userpreferences and returns an AK
    {
        String[] status = new String[2];
        
        // Gathering all data
        int serversInLicense = serversInLicenseParam;
        String activationCodeKeyString = "";
        String activationCodeString = "";
        
        Calendar thisVergunningStartCalendar = Calendar.getInstance(); thisVergunningStartCalendar = selectedDateParam;
        thisVergunningStartCalendar.set(Calendar.HOUR_OF_DAY, (int)0);
        thisVergunningStartCalendar.set(Calendar.MINUTE, (int)0);
        thisVergunningStartCalendar.set(Calendar.SECOND, (int)0);
        
        Calendar vergunningEndCalendar = Calendar.getInstance();

        status = dcmVergunning.getAK(); // MiddleWare Invocation
        if (status[0].equals("0"))
        {
            // Get the activation string
            activationCodeKeyString = status[1];
            activationCodeString =  serversInLicenseParam + "-" +
                                    String.format("%04d", thisVergunningStartCalendar.get(Calendar.YEAR)) + "-" +
                                    String.format("%02d", (thisVergunningStartCalendar.get(Calendar.MONTH)) + 1 ) + "-" +
                                    String.format("%02d", thisVergunningStartCalendar.get(Calendar.DAY_OF_MONTH)) + "-" +
                                    vergunningPeriodParam + "-" +
                                    activationCodeKeyString; // Hash
        }
        
        setVergunningOrderInProgress(true); // Middleware Invocation

        if (selectedDateParam != null)
        {
            vergunningEndCalendar = Calendar.getInstance(); vergunningEndCalendar.setTimeInMillis(thisVergunningStartCalendar.getTimeInMillis());
            if      ( vergunningPeriodParam.equals("Day") )         { vergunningEndCalendar.add(Calendar.DAY_OF_YEAR, 1); }
            else if ( vergunningPeriodParam.equals("Week") )        { vergunningEndCalendar.add(Calendar.WEEK_OF_YEAR, 1); }
            else if ( vergunningPeriodParam.equals("Month") )       { vergunningEndCalendar.add(Calendar.MONTH, 1); }
            else if ( vergunningPeriodParam.equals("Year") )        { vergunningEndCalendar.add(Calendar.YEAR, 1); }
            else if ( vergunningPeriodParam.equals("Lifetime") )    { vergunningEndCalendar.add(Calendar.YEAR, 20); }
        }

        // Displaying all date
        NumberFormat currencyFormatter = new DecimalFormat("#0.00"); NumberFormat discountFormatter = new DecimalFormat("#0");
        vergunningDetailsTable.setValueAt(vergunningPeriodParam, 1, 1); // MiddleWare Invocation
        vergunningDetailsTable.setValueAt(DCMTools.getHumanDate(thisVergunningStartCalendar), 2, 1);  // MiddleWare Invocation
        vergunningDetailsTable.setValueAt(DCMTools.getHumanDate(vergunningEndCalendar), 3, 1);   // MiddleWare Invocation
        vergunningDetailsTable.setValueAt(serversInLicense, 4, 1); // MiddleWare Invocation
        vergunningDetailsTable.setValueAt(discountFormatter.format(DCMLicense.getDiscount(serversInLicense)) + "%", 5, 1); // MiddleWare Invocation
        vergunningDetailsTable.setValueAt("€ " + currencyFormatter.format(DCMLicense.getDiscountPrice(serversInLicense)), 6, 1); // MiddleWare Invocation
        vergunningDetailsTable.setValueAt("€ " + currencyFormatter.format(DCMLicense.getTotalPrice(serversInLicense,vergunningPeriodParam)), 7, 1); // MiddleWare Invocation

        activationCodeField.setText(activationCodeString); // MiddleWare Invocation
        vergunningCodeField.setText(""); // MiddleWare Invocation
        
        return activationCodeString;
    }

    // MiddleWare
    public void applyReceiveVergunning(String activationCodeParam, String vergunningCodeParam)
    {
        dcmVergunning.setActivationCodeFromFile(activationCodeParam);
        dcmVergunning.setVergunningCodeFromFile(vergunningCodeParam);
        dcmVergunning.saveVergunning();
        dcmVergunning.controleerVergunning();
        dcmVergunning.setVergunningOrderInProgress(false);
    }
    
    // FrontEnd
    private void applySendVergunning()
    {
        applyVergunningButton.setEnabled(false);
        applyReceiveVergunning(activationCodeField.getText(),vergunningCodeField.getText());
        vergunningDoorvoeren();
        if (dcmVergunning.isValid())
        {
//            inboundCallCenterButton.setEnabled(true);
//            outboundCallCenterButton.setEnabled(true);
//            performanceMeter.setCallPerHourScale(0, (vergunning.getCallsPerHour() / 100), (vergunning.getCallsPerHour() / 1000));
//            movePerformanceMeter(0, true);

            Thread validLicenseAppliedThread = new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    applyVergunningButton.setEnabled(false);
                    orderLicenseButton.setEnabled(false);
                    try { Thread.sleep(1000); } catch (InterruptedException ex) { }
                }
            });
            validLicenseAppliedThread.setName("validLicenseAppliedThread");
            validLicenseAppliedThread.setDaemon(runThreadsAsDaemons);
            validLicenseAppliedThread.start();
        }
        else
        {
            Thread invalidLicenseAppliedThread = new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    applyVergunningButton.setEnabled(false);
                    orderLicenseButton.setEnabled(false);
                    //licenseCodeField.setEnabled(false);
                    vergunningCodeField.setEditable(false);
                    vergunningCodeField.setForeground(Color.red);
                    if ( dcmVergunning.getVergunningInvalidReason().length() == 0 )
                    {
                        log("License Code is Invalid due to Internet Connectivity", true, true, true);
                    }
                    else
                    {
                        log("License Code is Invalid due to " + getVergunningInvalidReason() + ". " + getVergunningInvalidAdvice(), true, true, true);
                    }
                    try { Thread.sleep(5000); } catch (InterruptedException ex) { }
                    //licenseCodeField.setEnabled(true);
                    vergunningCodeField.setEditable(true);
                    vergunningCodeField.setForeground(Color.black);
                    licenseCodePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "License Code", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("STHeiti", 0, 10))); // NOI18N
                }
            });
            invalidLicenseAppliedThread.setName("invalidLicenseAppliedThread");
            invalidLicenseAppliedThread.setDaemon(runThreadsAsDaemons);
            invalidLicenseAppliedThread.start();
        }
    }

    // Begin MiddleWare
    public boolean  controleerVergunning()                                  { dcmVergunning.controleerVergunning(); return dcmVergunning.isValid(); }
    public String   getVergunningPeriod()                                   { return dcmVergunning.getVergunningPeriod(); }
    public String   getVergunningStartDateString()                          { String startDate = String.format("%04d", dcmVergunning.getVergunningStartDate().get(Calendar.YEAR)) + "-" + String.format("%02d", (dcmVergunning.getVergunningStartDate().get(Calendar.MONTH)) + 1) + "-" + String.format("%02d", dcmVergunning.getVergunningStartDate().get(Calendar.DAY_OF_MONTH)); return startDate; }
    public String   getVergunningEndDateString()                            { String endDate =   String.format("%04d", dcmVergunning.getVergunningEndDate().get(Calendar.YEAR)) + "-" +   String.format("%02d", (dcmVergunning.getVergunningEndDate().get(Calendar.MONTH)) + 1) + "-" +   String.format("%02d", dcmVergunning.getVergunningEndDate().get(Calendar.DAY_OF_MONTH));   return endDate;   }
    public Calendar getVergunningStartDate()                                { return dcmVergunning.getVergunningStartDate(); }
    public Calendar getVergunningEndDate()                                  { return dcmVergunning.getVergunningEndDate();   }
    public int      getServersInLicense()                                   { return dcmVergunning.getServersInLicense(); }
    public String   getActivationCode()                                     { return dcmVergunning.getActivationCodeFromFile(); }
    public String   getVergunningCode()                                     { return dcmVergunning.getVergunningCodeFromFile(); }
    public String   getVergunningInvalidAdvice()                            { return dcmVergunning.getVergunningInvalidAdvice(); }
    public String   getVergunningInvalidReason()                            { return dcmVergunning.getVergunningInvalidReason(); }
    public void     setVergunningOrderInProgress(boolean inProgress)        { dcmVergunning.setVergunningOrderInProgress(inProgress); }
    public void     setVergunningPeriod(String vergunningPeriodParam)       { dcmVergunning.setVergunningPeriod(vergunningPeriodParam); }
    public void     setVergunningStartDate(Calendar startDateParam)         { dcmVergunning.setVergunningStartDate(startDateParam); }
    public void     setVergunningEndDate(Calendar endDateParam)             { dcmVergunning.setVergunningEndDate(endDateParam); }
    public void     setServersInLicense(int serversInLicenseParam)          { dcmVergunning.setServersInLicense(serversInLicenseParam); }
    public void     setActivationCode(String activationCodeParam)           { dcmVergunning.setActivationCodeFromFile(activationCodeParam); }
    public void     setVergunningCode(String vergunningCodeParam)           { dcmVergunning.setVergunningCodeFromFile(vergunningCodeParam); }
    // End MiddleWare
    
    // FrontEnd
    private void vergunningDoorvoeren() // Makes sure you also see Valid LicenseDetails in the LicenseTab
    {
        dcmVergunning = new DCMVergunning(dcmDesktopReference,debug);
        log("Action:  DCMDesktop: License being Validated", false, true, true);
        dcmVergunning.controleerVergunning();
        
        // In case people turn back the time
        Calendar now = Calendar.getInstance();
        Calendar last = Calendar.getInstance(); last = dcmDBClient.getDCMTijd(); last.add(Calendar.DAY_OF_MONTH, -1);
        if (   now.compareTo(last) < 0 )
        {
            dcmVergunning.setVergunningValid(false);
            dcmVergunning.setDefaultServersInLicense();
            log("Failure: DCMDesktop: Time issue detected...", true, true, true);
            if (debug) { log("Last: " + DCMTools.getHumanDateLong(last), false, true, true); log("Now:  " + DCMTools.getHumanDateLong(now), false, true, true); }
        }
        else
        {
            dcmDBClient.updateDCMTijd(now);
        }

        // Set the Admin License Fields
        serversInLicenseField.setText(Integer.toString(getServersInLicense())); // MiddleWare Invocation
        
//        vergunningDateChooserPanel.setSelectedDate(getVergunningStartDate()); // Don't set causes license update
        
        vergunningPeriodList.setSelectedValue(getVergunningPeriod(), false); // MiddleWare Invocation

        // Set the Admin License Details Table
        if (dcmVergunning.isValid()) { vergunningDetailsTable.setValueAt("Yes", 0, 1); } else { vergunningDetailsTable.setValueAt("Free", 0, 1); } // MiddleWare Invocation
//        if (dcmVergunning.isValid()) { vergunningDetailsTable.setValueAt(dcmVergunning.getVergunningPeriod(), 1, 1); } else { vergunningDetailsTable.setValueAt("Lifetime", 1, 1); } // MiddleWare Invocation
        vergunningDetailsTable.setValueAt(dcmVergunning.getVergunningPeriod(), 1, 1); // MiddleWare Invocation
        vergunningDetailsTable.setValueAt(DCMTools.getHumanDate(dcmVergunning.getVergunningStartDate()), 2, 1);  // MiddleWare Invocation
        vergunningDetailsTable.setValueAt(DCMTools.getHumanDate(dcmVergunning.getVergunningEndDate()), 3, 1);   // MiddleWare Invocation
        vergunningDetailsTable.setValueAt(dcmVergunning.getServersInLicense(), 4, 1); // MiddleWare Invocation
        dcmVergunning.setVergunningOrderInProgress(false);

        activationCodeField.setText(getActivationCode()); // MiddleWare Invocation
        vergunningCodeField.setText(getVergunningCode()); // MiddleWare Invocation
        vergunningCodeField.setEditable(false);

        if (dcmVergunning.isValid()) // MiddleWare Invocation
        {
            log("Success: DCMDesktop: License Valid", true, true, true);
        }
        else
        {
            log("Advise:  DCMDesktop: Thank you for using this Free Lifetime 5 Server License", true, true, true);
            log("Advise:  DCMDesktop: Please click the Admin Tab if you want more Servers Licensed", true, true, true);
            
        }
            managerTab.setSelectedIndex(0);
            startPollerButton.setEnabled(true);
            startCommanderButton.setEnabled(true);
            serverTree.setEnabled(true);
            if (dcmUser.getAdministrator())
            {
                addServerButton.setEnabled(true);
                deleteServerButton.setEnabled(true);
                startPollerButton.setEnabled(true);
                startCommanderButton.setEnabled(true);
                addDCMUserButton.setEnabled(true);
                deleteDCMUserButton.setEnabled(true);
                popupMenus.enableAdministrator(true);
                exportButton.setEnabled(true);
                importButton.setEnabled(true);
            }
            else
            {
                addServerButton.setEnabled(false);
                deleteServerButton.setEnabled(false);
                startPollerButton.setEnabled(false);
                startCommanderButton.setEnabled(false);
                addDCMUserButton.setEnabled(false);
                deleteDCMUserButton.setEnabled(false);
                popupMenus.enableAdministrator(false);
                exportButton.setEnabled(false);
                importButton.setEnabled(false);
            }
//            deleteViewButton.setEnabled(true);
            selectPresetBox.setEnabled(true);
//            saveViewButton.setEnabled(true);
            wordSearchButton.setEnabled(true);
            matchSearchButton.setEnabled(true);
            searchField.setEnabled(true);
//            searchField.setText("");
            swapViewButton.setEnabled(true);
            
            controleerVergunningLimiet();
            try { buildServerTree(); } catch (CloneNotSupportedException ex) { } // might fail
            viewTrendPane(false);
    }

    // FrontEnd
    private synchronized void viewTrendPane(final boolean viewTrendParam)
    {   
        setCursor(Cursor.DEFAULT_CURSOR);
        Thread viewTrendPaneThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                int stepSize = 50;
                int roof = 0;
                int devider = viewSplitter.getDividerLocation();
                int floor = viewSplitter.getHeight();
                int step = 0;
                
                if (viewTrendParam) // going up
                {
                    if (devider > roof) { step = -stepSize; } else { step = stepSize; }
                    if ( animated )
                    {
                        for (int counter = devider;counter>roof;counter+=step) { viewSplitter.setDividerLocation(counter); try { Thread.sleep(20); } catch (InterruptedException ex) {  } }
                    }
                    viewSplitter.setDividerLocation(roof);
                    viewSplitter.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Trend View", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("STHeiti", 0, 11), new java.awt.Color(102, 102, 102))); // NOI18N
                    trendIsViewable = true;
//                    log("devider: " + inventoryTrendSplitter.getDividerLocation() + " floor: "+inventoryTrendSplitter.getHeight(),true,true,true);
                }
                else // going down
                {
                    if (devider < floor) { step = stepSize; } else { step = -stepSize; }
                    if (animated)
                    {
                        for (int counter = devider;counter<floor;counter+=step) { viewSplitter.setDividerLocation(counter); try { Thread.sleep(20); } catch (InterruptedException ex) {  } }                    
                    }
                    viewSplitter.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Inventory View", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("STHeiti", 0, 11), new java.awt.Color(102, 102, 102))); // NOI18N
                    viewSplitter.setDividerLocation(1.0D);
                    trendIsViewable = false;
                    tableSplitter.setDividerLocation(40);
//                    log("devider: " + inventoryTrendSplitter.getDividerLocation() + " floor: "+inventoryTrendSplitter.getHeight(),true,true,true);
                }
                if (secureViewActionTimer != null) { secureViewActionTimer.cancel(); secureViewActionTimer.purge(); }
                secureViewAction = new TimerTask() { @Override public void run() { if (trendIsViewable) { viewSplitter.setDividerLocation(0D); } else { viewSplitter.setDividerLocation(1.0D); tableSplitter.setDividerLocation(40); } } };
                secureViewActionTimer = new java.util.Timer();
                secureViewActionTimer.schedule(secureViewAction, 500L);
            }
        });
        viewTrendPaneThread.setName("viewTrendPaneThread");
        viewTrendPaneThread.setDaemon(false);
        viewTrendPaneThread.setPriority(Thread.MAX_PRIORITY);
        viewTrendPaneThread.start();
    }
    
    // FrontEnd
    private void setSelectionArea(String dataParam)
    {
        manualValueMode = false; // New graph = auto value ax
        selectionArea.setText(dataParam);
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField activationCodeField;
    private javax.swing.JPanel activationCodePanel;
    private javax.swing.JButton addDCMUserButton;
    private javax.swing.JButton addServerButton;
    private javax.swing.JPanel adminPanel;
    private javax.swing.JButton applyVergunningButton;
    private javax.swing.JPanel backupPanel;
    private javax.swing.JButton clearHelpdeskButton;
    private javax.swing.JToolBar dcmHelpdeskButtonBar;
    private javax.swing.JToolBar dcmUserButtonBar;
    private javax.swing.JButton deleteDCMUserButton;
    private javax.swing.JButton deletePresetButton;
    private javax.swing.JButton deleteServerButton;
    private javax.swing.JButton downButton;
    public datechooser.beans.DateChooserPanel endDateChooserPanel;
    private javax.swing.JPanel endDateSelectorPanel;
    private javax.swing.JTextField endDayField;
    private javax.swing.JLabel endDayLabel;
    private javax.swing.JButton endDayMinusButton;
    private javax.swing.JButton endDayPlusButton;
    private javax.swing.JTextField endHourField;
    private javax.swing.JLabel endHourLabel;
    private javax.swing.JButton endHourMinusButton;
    private javax.swing.JButton endHourPlusButton;
    javax.swing.JSlider endHourSlider;
    private javax.swing.JTextField endMinuteField;
    private javax.swing.JLabel endMinuteLabel;
    private javax.swing.JButton endMinuteMinusButton;
    private javax.swing.JButton endMinutePlusButton;
    javax.swing.JSlider endMinuteSlider;
    private javax.swing.JTextField endMonthField;
    private javax.swing.JLabel endMonthLabel;
    private javax.swing.JButton endMonthMinusButton;
    private javax.swing.JButton endMonthPlusButton;
    private javax.swing.JTextField endTimeField;
    private javax.swing.JToggleButton endTimeIsRelativeButton;
    private javax.swing.JPanel endTimeRelativePanel;
    private javax.swing.JPanel endTimeSelectorPanel;
    private javax.swing.JButton exportButton;
    private javax.swing.JButton graphButton;
    private javax.swing.JLabel graphLabel;
    private javax.swing.JPanel helpdeskPanel;
    private javax.swing.JScrollPane hostScroller;
    private javax.swing.JTable hostTable;
    private javax.swing.JButton importButton;
    private javax.swing.JButton leftButton;
    private javax.swing.JPanel licenseCodePanel;
    private javax.swing.JPanel licenseDatePanel;
    private javax.swing.JPanel licenseDetailsPanel;
    private javax.swing.JScrollPane licenseDetailsScrollPane;
    private javax.swing.JPanel licenseInnerPanel;
    private javax.swing.JPanel licensePeriodPanel;
    private javax.swing.JScrollPane licensePeriodScrollPane;
    private javax.swing.JPanel licenseTypePanel;
    private javax.swing.JTextPane logPane;
    private javax.swing.JPanel logPanel;
    private javax.swing.JScrollPane logScroller;
    private javax.swing.JButton loginButton;
    private javax.swing.JPanel managerPanel;
    private javax.swing.JTabbedPane managerTab;
    private javax.swing.JButton matchSearchButton;
    private javax.swing.JEditorPane messagePane;
    private javax.swing.JScrollPane messageScroller;
    private javax.swing.JButton minusButton;
    private javax.swing.JToolBar navigateXButtonBar;
    private javax.swing.JPanel navigatorPanel;
    private javax.swing.JButton orderLicenseButton;
    private javax.swing.JButton plusButton;
    private javax.swing.JScrollPane resourcesScroller;
    private javax.swing.JTable resourcesTable;
    private javax.swing.JButton rightButton;
    private javax.swing.JButton savePresetButton;
    private javax.swing.JTextField searchField;
    private javax.swing.JPanel searchPanel;
    private javax.swing.JScrollPane searchStatsScroller;
    private javax.swing.JTable searchStatsTable;
    private javax.swing.JComboBox selectPresetBox;
    protected javax.swing.JTextArea selectionArea;
    private javax.swing.JScrollPane selectionScroller;
    private javax.swing.JButton sendEmailButton;
    protected javax.swing.JTree serverTree;
    private javax.swing.JScrollPane serverTreeScoller;
    private javax.swing.JTextField serversInLicenseField;
    private javax.swing.JButton startCommanderButton;
    public datechooser.beans.DateChooserPanel startDateChooserPanel;
    private javax.swing.JPanel startDateSelectorPanel;
    private javax.swing.JTextField startDayField;
    private javax.swing.JLabel startDayLabel;
    private javax.swing.JButton startDayMinusButton;
    private javax.swing.JButton startDayPlusButton;
    private javax.swing.JTextField startHourField;
    private javax.swing.JLabel startHourLabel;
    private javax.swing.JButton startHourMinusButton;
    private javax.swing.JButton startHourPlusButton;
    javax.swing.JSlider startHourSlider;
    private javax.swing.JTextField startMinuteField;
    private javax.swing.JLabel startMinuteLabel;
    private javax.swing.JButton startMinuteMinusButton;
    private javax.swing.JButton startMinutePlusButton;
    javax.swing.JSlider startMinuteSlider;
    private javax.swing.JTextField startMonthField;
    private javax.swing.JLabel startMonthLabel;
    private javax.swing.JButton startMonthMinusButton;
    private javax.swing.JButton startMonthPlusButton;
    private javax.swing.JButton startPollerButton;
    private javax.swing.JTextField startTimeField;
    private javax.swing.JToggleButton startTimeIsRelativeButton;
    private javax.swing.JPanel startTimeRelativePanel;
    private javax.swing.JPanel startTimeSelectorPanel;
    private javax.swing.JLabel staticLabel;
    private javax.swing.JLabel statusLabel;
    private javax.swing.JPanel statusPanel;
    private javax.swing.JTextField subjectField;
    private javax.swing.JButton swapViewButton;
    private javax.swing.JScrollPane sysPropsScroller;
    private javax.swing.JTable sysPropsTable;
    private javax.swing.JPanel systemPropertiesPanel;
    private javax.swing.JSplitPane tableSplitter;
    private javax.swing.JToolBar treeButtonBar;
    private javax.swing.JSplitPane treeInventorySplitter;
    private javax.swing.JSplitPane treeSplitter;
    private javax.swing.JButton upButton;
    private javax.swing.JPanel userPanel;
    private javax.swing.JScrollPane userScroller;
    private javax.swing.JTable userTable;
    private javax.swing.JTextField vergunningCodeField;
    public datechooser.beans.DateChooserPanel vergunningDateChooserPanel;
    private javax.swing.JTable vergunningDetailsTable;
    private javax.swing.JList vergunningPeriodList;
    private javax.swing.JSplitPane viewSplitter;
    private javax.swing.JButton wordSearchButton;
    private javax.swing.JButton zoomInXButton;
    private javax.swing.JButton zoomInYButton;
    private javax.swing.JButton zoomOutXButton;
    private javax.swing.JButton zoomOutYButton;
    // End of variables declaration//GEN-END:variables

    // FrontEnd
    @Override public void inventoryReady()
    {
        // JavaDB & RRDB creation finished, now update serversTree
        try { buildServerTree(); } catch (CloneNotSupportedException ex) { }
    }
    
    // MiddleWare
    public ArrayList<Server> getServerList() throws CloneNotSupportedException
    {
        // JavaDB & RRDB creation finished, now update serversTree
        ArrayList<Server> thisServerList = dcmDBClient.selectServers();
        return thisServerList;
    }
    
    // FrontEnd
    public void buildServerTree() throws CloneNotSupportedException
    {
        dcmVergunning = new DCMVergunning(dcmDesktopReference,debug); // Middleware
        controleerVergunningLimiet();
        
        serverList = getServerList();
        
        //Create the rootNode including all its leafs (servers)
        if ( dcmVergunning.isValid() ) { rootNode = new DefaultMutableTreeNode("Datacenter"); } else { rootNode = new DefaultMutableTreeNode("Datacenter Free"); }
        if ( dcmVergunning.getServersInLicense() < serverList.size()) // We can not get a sublist from the serverList that is larger than serverList (outofbounds error) 
        {
            for (Server server:serverList.subList(0, dcmVergunning.getServersInLicense()))
            {
                serverNode = new DefaultMutableTreeNode(server.getHost().getHostname());
                rootNode.add(new DefaultMutableTreeNode(serverNode));
            }            
        }
        else
        {
            for (Server server:serverList)
            {
                serverNode = new DefaultMutableTreeNode(server.getHost().getHostname());
                rootNode.add(new DefaultMutableTreeNode(serverNode));
            }                        
        }
        
        // Create a treemodel and set the rootnode
        DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);
        
        // Create a new *EMPTY* JTree
        serverTree = new JTree();
        
        // Get the fresh renderer from the tree and set a leaf-server icon
        DefaultTreeCellRenderer treeRenderer = (DefaultTreeCellRenderer)serverTree.getCellRenderer();
        treeRenderer.setLeafIcon(serverIcon);
        treeRenderer.setClosedIcon(datacenterIcon);
        treeRenderer.setOpenIcon(datacenterIcon);
        
        // Set the prebuild treemodel on the jtree
        serverTree.setModel(treeModel);
        serverTree.setCellRenderer(treeRenderer);
        
        // Refresh the bitch!
        serverTree.revalidate();
        serverTreeScoller.setViewportView(serverTree);
        serverTree.addMouseListener(new java.awt.event.MouseAdapter() {@Override public void mouseClicked(java.awt.event.MouseEvent evt) {try { serverTreeMouseClicked(evt); } catch (CloneNotSupportedException ex) { } } });
        serverTree.addKeyListener(new java.awt.event.KeyAdapter() {@Override public void keyReleased(java.awt.event.KeyEvent evt) {try { serverTreeKeyed(evt); } catch (CloneNotSupportedException ex) { } } });
        resetServerPane();
        if (searchField.getText().length() > 0)
        {
            setSelectionArea(dcmDBClient.convertSearchStringToResourcesText(searchField.getText(), serverList, false));
        }
        else
        {
            updateSearchStatsTable("0/"+dcmDBClient.getResourceCount(),"0","0","0","0","0");
        }
        if (serverList.size()>0) {serverTree.setSelectionRow(1); fillinServerPane(serverTree.getSelectionPath().getLastPathComponent().toString());}
    }
    
    // FrontEnd
    protected void controleerVergunningLimiet() // Disables adding of to many servers
    {
        if ((dcmDBClientIsReady)&&(dcmVergunning != null))
        {
            if (vergunningReachedLimiet()) // MiddleWare Invocation
            {
                addServerButton.setEnabled(false);
                popupMenus.enableServersMenuItemAddServer(false);
            }
            else 
            {
                if (dcmUser.getAdministrator()) { addServerButton.setEnabled(true); }
                if (dcmUser.getAdministrator()) { popupMenus.enableServersMenuItemAddServer(true); }
            }                        
        }
    }
    
    // MiddleWare
    protected boolean vergunningReachedLimiet() // Disables adding of to many servers
    {
        boolean limitExceeded = true;
        if ((dcmDBClientIsReady)&&(dcmVergunning != null))
        {
            if ( dcmDBClient.getHostCount() >= dcmVergunning.getServersInLicense() )
            {
                limitExceeded = true;
            }
            else 
            {
                limitExceeded = false;
            }            
        }
        return limitExceeded;
    }
    
    // FrontEnd
    private void serverTreeMouseClicked(MouseEvent evt) throws CloneNotSupportedException
    {
        TreePath selPath = null;
        int selRow = serverTree.getRowForLocation(evt.getX(), evt.getY());
        
        // Left mouse clicked ...
        if (evt.getButton() == 1)
        {
            // ... on root
            if (selRow == 0)
            {
                resetServerPane();
            }
            // ... on leaf
            else if (selRow > 0)
            {
                fillinServerPane(serverTree.getSelectionPath().getLastPathComponent().toString());
            }            
        }
        // Right mouse clicked ...
        else if (evt.getButton() == 3)
        {
            // ... on root
            if (selRow == 0)
            {
                resetServerPane();
                selPath = serverTree.getPathForLocation(evt.getX(), evt.getY());
                serverTree.setSelectionPath(selPath);
//                serversMenu.show(evt.getComponent(), evt.getX(), evt.getY());
                popupMenus.showServersMenu(evt);
            }
            // ... on leaf
            else if (selRow > 0)
            {
//                Select Leaf
//                selPath = serverTree.getPathForLocation(evt.getX(), evt.getY());
//                serverTree.setSelectionPath(selPath);
                
                fillinServerPane(serverTree.getSelectionPath().getLastPathComponent().toString());
//                serverMenu.show(evt.getComponent(), evt.getX(), evt.getY());
                popupMenus.showServerMenu(evt);
            }
        }
    }
    
    private void serverTreeKeyed(KeyEvent evt) throws CloneNotSupportedException
    {
        int selRow = serverTree.getSelectionPath().getPathCount();
        if (selRow == 1) { resetServerPane(); } else { fillinServerPane(serverTree.getSelectionPath().getLastPathComponent().toString()); }
    }
    
    protected void updateServer() throws CloneNotSupportedException
    {        
        int selRow = serverTree.getSelectionPath().getPathCount();
        if (selRow == 2)
        {
            Host thisHost = new Host();
            thisHost = dcmDBClient.getHostByHostname(serverTree.getSelectionPath().getLastPathComponent().toString());
            log("HostUpdate delete host: " + thisHost.getHostname(),true,true,true);
            dcmDBClient.deleteServerByHostname(serverTree.getSelectionPath().getLastPathComponent().toString()); // Delete Server (not archives)
            try { Thread.sleep(1000); } catch (InterruptedException ex) {  } // Just give deletion one second
            log("HostUpdate add Host: " + thisHost.getHostname(),true,true,true);
            inventoryServer(dcmDesktopReference, 0, thisHost); // MiddleWare Invocation
            //try { buildServerTree(); } catch (CloneNotSupportedException ex) {}
        }
    }
    
    protected void updateServers() throws CloneNotSupportedException
    {        
        log("Update of ALL servers initiated, please wait...", true, true, true);
        ArrayList<Server> thisServerList = new ArrayList<Server>();
        thisServerList = dcmDBClient.selectServers();
        for (Server server:thisServerList)
        {
            log("HostUpdate delete host: " + server.getHost().getHostname(),true,true,true);
            dcmDBClient.deleteServerByHostname(server.getHost().getHostname()); // Delete Server (not archives)
            try { Thread.sleep(1000); } catch (InterruptedException ex) {  } // Just give deletion one second
            log("HostUpdate add Host: " + server.getHost().getHostname(),true,true,true);
            inventoryServer(dcmDesktopReference, 0, server.getHost()); // MiddleWare Invocation
            //try { buildServerTree(); } catch (CloneNotSupportedException ex) {}
        }
    }
    
    protected void enablePollingSelectedServers(final boolean enableParam) throws CloneNotSupportedException
    {
        Thread enablePollingSelectedServersThread = new Thread(new Runnable()
        {
            @Override
            @SuppressWarnings({"static-access"})
            public void run()
            {
                String action = ""; if (enableParam) { action = "Enabling"; } else { action = "Disabling"; }
                int serversSelected = serverTree.getSelectionPaths().length;
                log(action + " polling for " + serversSelected + " servers, please wait...",true,true,true);
                setCursor(Cursor.WAIT_CURSOR);
                ArrayList<Host> hostList = new ArrayList<Host>();
                for (TreePath treePath:serverTree.getSelectionPaths())
                {
                    if (serverTree.getSelectionPath().getPathCount() == 2) // Make sure it isn't root selected
                    {
                        String hostname = treePath.getLastPathComponent().toString();
                        try { hostList.add(dcmDBClient.getHostByHostname(hostname)); } catch (CloneNotSupportedException ex) { }
                    }
                }
                for (Host host:hostList) { try { dcmDBClient.enableHost(host, enableParam); } catch (CloneNotSupportedException ex) {  }}
                try { buildServerTree(); } catch (CloneNotSupportedException ex) {  }
                setCursor(Cursor.DEFAULT_CURSOR);
                log(action + " polling for " + serversSelected + " servers finished.",true,true,true);
            }
        });
        enablePollingSelectedServersThread.setName("enablePollingSelectedServersThread");
        enablePollingSelectedServersThread.setDaemon(true);
        enablePollingSelectedServersThread.setPriority(Thread.NORM_PRIORITY);
        enablePollingSelectedServersThread.start();                    
    }
    
    protected void deleteServerByForm(String hostnameParam) throws CloneNotSupportedException
    {        
        TreePath selPath = null;
        dcmDBClient.deleteServerByHostname(hostnameParam);
        try { buildServerTree(); } catch (CloneNotSupportedException ex) {}
    }
    
    private void hostTableMouseClicked(java.awt.event.MouseEvent evt)
    {
        log("waarde: " + hostTable.getValueAt(hostTable.getSelectedColumn(), hostTable.getSelectedRow()).toString(),true,true,true);
    }

    // MiddleWare
    public Server getServerByHostname(String hostnameParam)
    {
        Server server = null;
        try { server = (Server) dcmDBClient.getServerByHostname(hostnameParam).clone(); } catch (CloneNotSupportedException ex) { }
        return server;
    }
    
    // MiddleWare
    public DCMUser getDCMUserByUsername(String usernameParam)
    {
        DCMUser thisDCMUser = null;
        thisDCMUser = dcmDBClient.getDCMUserByUsername(usernameParam);
        return thisDCMUser;
    }
    
    // MiddleWare
    public Host selectHostById(long idParam) throws CloneNotSupportedException
    {
        Host thisHost = null;
//        try { host = dcmMetaDBClient.selectHost(idParam).clone(); } catch (CloneNotSupportedException ex) { }
        thisHost = (Host) dcmDBClient.selectHost(idParam).clone();
        return thisHost;
    }
    
    public void hostnameChangedInHostTable(String oldHostnameParam,String newHostnameParam)
    {
        // ArchiveDir is named after hostname, so that needs to be renamed to
        File oldHostnameDir = new File(configuration.getArchiveDBDir() + oldHostnameParam + configuration.getFileSeparator());
        File newHostnameDir = new File(configuration.getArchiveDBDir() + newHostnameParam + configuration.getFileSeparator());
        if (! oldHostnameDir.renameTo(newHostnameDir))
        {
            log("Error: Renaming: " + oldHostnameDir.getPath() + " to: " + newHostnameDir.getPath(),true,true,true);
        }
        else
        {
            log("Info: Renamed: " + oldHostnameDir.getPath() + " to: " + newHostnameDir.getPath(),true,true,true);
        }                                        
    }
    
    // MiddleWare
    public void updateHost(Host hostParam, boolean generateScriptParam) throws CloneNotSupportedException
    {
        dcmDBClient.updateHost(hostParam);        
        
        if (generateScriptParam)
        {
            Server server = new Server(); server = (Server) selectServerById(hostParam.getId());
            pushPollScript(dcmDesktopReference, server);
        }
    }
    
    private void fillinServerPane(String hostnameParam) throws CloneNotSupportedException
    {
        hostScroller.setVisible(true);
        resourcesScroller.setVisible(true);

        hostTable.setBackground(new java.awt.Color(204, 204, 204));
        hostTable.setFont(hostTable.getFont().deriveFont((float)11));
        hostTable.setForeground(new java.awt.Color(51, 51, 51));
        hostTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Id", "Host", "Port", "Username", "Password", "SUPassword", "Enabled", "Command", "Comments", "Sysinfo"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Long.class,
                java.lang.String.class,
                java.lang.Integer.class,
                java.lang.String.class,
                java.lang.String.class,
                java.lang.String.class,
                java.lang.Boolean.class,
                java.lang.String.class,
                java.lang.String.class,
                java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, true, true, true, true, true, true, true, true
            };

            @Override
            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        hostTable.setAutoCreateRowSorter(true);
        hostTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        hostTable.setDoubleBuffered(true);
        hostTable.setDragEnabled(true);
        hostTable.setShowGrid(true);
        hostScroller.setViewportView(hostTable);

        Server server = new Server();
//        server =  (Server) dcmMetaDBClient.selectServerByHostname(hostnameParam).clone();
        server =  (Server) getServerByHostname(hostnameParam).clone();
        
        hostTable.setValueAt(server.getHost().getId(), 0,0);
        hostTable.setValueAt(server.getHost().getHostname(), 0,1);
        hostTable.setValueAt(server.getHost().getPort(), 0,2);
        hostTable.setValueAt(server.getHost().getUsername(), 0,3);
        hostTable.setValueAt(server.getHost().getUserPassword().replaceAll(".", "*"), 0,4); // Don't replace with \\.
        hostTable.setValueAt(server.getHost().getSuperuserPassword().replaceAll(".", "*"), 0,5); // Don't replace with \\.
        hostTable.setValueAt(server.getHost().getEnabled(), 0,6);
        hostTable.setValueAt(server.getHost().getCommand(), 0,7);
        hostTable.setValueAt(server.getHost().getComment2(), 0,8);
        hostTable.setValueAt(server.getHost().getSysinfo(), 0,9);

//      Implement the hostTable ActionListener
        
        hostTableCellAction = new AbstractAction()
        {
            @Override public void actionPerformed(ActionEvent event)
            {
//                if (debug) { log("HostTableCellAction Performance: " + event.getSource(), false,true,true); }
                DCMTableCellListener hostTableCellListener = (DCMTableCellListener)event.getSource();
                if (hostTableCellListener.getOldValue() != hostTableCellListener.getNewValue())
                {
//                    if (debug) { log("Id    : " + hostTableCellListener.getTable().getValueAt(hostTableCellListener.getRow(), 0) + " Row   : " + hostTableCellListener.getRow() + " Column: " + hostTableCellListener.getColumn() + " Old   : " + hostTableCellListener.getOldValue() + " New   : " + hostTableCellListener.getNewValue() ,false,true,true ); }
                    Host host = null; try { host = (Host) selectHostById(Long.parseLong( hostTableCellListener.getTable().getValueAt(hostTableCellListener.getRow(), 0).toString() ) ).clone(); } catch (CloneNotSupportedException ex) {   }
//                    if (debug) { log("Old Host Object fetched from DB: " + host.toString(), false,true,true); }
                    int column = hostTableCellListener.getColumn();
                    
                    hostnameUpdated = false;
                    boolean generateScript = false;
                    
                    switch (column) // Beware these table column numbers belong to the table, not to the object Host or Host Table in the metaDB (columnnumbers might differ)
                    {
                        case 1:     {
                                        host.setHostname((String)hostTableCellListener.getNewValue()); hostnameUpdated = true; log("Info: Hostname updated",true,true,true); 
                                        hostnameChangedInHostTable(hostTableCellListener.getOldValue().toString(),hostTableCellListener.getNewValue().toString());
                                        // ArchiveDir is named after hostname, so that needs to be renamed to
                                        break;
                                    }
                        case 2:     {  host.setPort(Integer.parseInt(hostTableCellListener.getNewValue().toString())); log("Info: Port updated",true,true,true); break; }
                        case 3:     {  host.setUsername((String)hostTableCellListener.getNewValue()); log("Info: Username updated",true,true,true); break; }
                        case 4:     {  host.setUserPassword((String)hostTableCellListener.getNewValue()); log("Info: UserPassword updated",true,true,true); break; }
                        case 5:     {  host.setSuperuserPassword((String)hostTableCellListener.getNewValue()); log("Info: SuperuserPassword updated",true,true,true); break; }
                        case 6:     {  /*host.setEnabled((String)hostTableCellListener.getNewValue());*/ log("Info: Enabled " + hostTableCellListener.getNewValue() + " updated",true,true,true); break; }
                        case 7:     {  host.setCommand((String)hostTableCellListener.getNewValue()); log("Info: Command updated",true,true,true); generateScript = true; break; }
                        case 8:     {  host.setComment2((String)hostTableCellListener.getNewValue()); log("Info: Comments updated",true,true,true); break; }
                        case 9:     {  host.setSysinfo((String)hostTableCellListener.getNewValue()); log("Info: Sysinfo updated",true,true,true); break; }
//                        default:    {  log("Info: This host column is NOT editable",true,true,true);
//                                        try { dcManagerReference.fillinServerPane(serverTree.getSelectionPath().getLastPathComponent().toString()); } catch (CloneNotSupportedException ex) {  } }
                    }
//                    if (debug) { log("New Host Object will be updated in DB: " + host.toString(), false,true,true); }
                    try { updateHost(host,generateScript); } catch (CloneNotSupportedException ex) {   }
                }
            }
        };
        hostTableCellListener = new DCMTableCellListener(hostTable, hostTableCellAction);

        // And another listener, just to deal with the boolean Enabled field click in this host table
        hostTable.getModel().addTableModelListener(new TableModelListener()
        {
            @Override public void tableChanged(TableModelEvent event)
            {
//                if (debug) { log("Event.column: " + event.getColumn() + " clicked",true,true,true); }
                if (event.getColumn() == 6) // NEVER forget this one if the columns in this table changes
                {
                    TableModel model = (TableModel)event.getSource();
                    
                    boolean error = false;
                    try { Long.parseLong(model.getValueAt(event.getLastRow(), 0).toString()); } catch (NumberFormatException ex) {error = true;}
                    
                    if (!error)
                    {
                        Object data = model.getValueAt(event.getLastRow(), event.getColumn());                        
//                        if (debug) { log("id: " + Long.parseLong(model.getValueAt(event.getLastRow(), 0).toString()) + " or " + model.getValueAt(event.getLastRow(), 0) + " data: " + data,true,true,true); }
                        Host host = null;
//                        try { host = dcmMetaDBClient.selectHost(Long.parseLong(model.getValueAt(event.getLastRow(), 0).toString())); } catch (CloneNotSupportedException ex) {   }
                        try { host = selectHostById(Long.parseLong(model.getValueAt(event.getLastRow(), 0).toString())); } catch (CloneNotSupportedException ex) {   }
                        if (data.toString().equals("true")) { host.setEnabled(true); } else { host.setEnabled(false); }
                        hostnameUpdated = false;
//                        dcmMetaDBClient.updateHost(host);                                                    
                        try { updateHost(host,false); } catch (CloneNotSupportedException ex) {  }
                    }
                }
            }
        });    
                
//      ResourceTable ==========================================================================        
        
        int resourcesTableRowsNeeded = server.getResourceList().size();

        Object[][] thisData = new Object[resourcesTableRowsNeeded][15];
        String[]   header = new String [] {"Id", "HostId", "Category", "ResourceType", "ValueType", "CounterType", "Resource", "PollCommand", "LastValue", "WarningLimit", "CriticalLimit", "AlertPolls", "Updated", "RRDFile", "Retention", "Enabled"};
        
        resourcesTable.setModel(new javax.swing.table.DefaultTableModel(thisData,header)
        {
            Class[] types = new Class []
	    {
		java.lang.Long.class,
		java.lang.Long.class,
		java.lang.String.class,
		java.lang.String.class,
		java.lang.String.class,
		java.lang.String.class,
		java.lang.String.class,
		java.lang.String.class,
		java.lang.Double.class,
		java.lang.Double.class,
		java.lang.Double.class,
		java.lang.Integer.class,
		java.lang.Long.class,
		java.lang.String.class,
		java.lang.Integer.class,
		java.lang.Boolean.class};
            boolean[] canEdit = new boolean []
	    {
                false, false, false, false, false, false, true, true, true, true, true, true, false, true, false, true
            };

            @Override
            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        
        resourcesTable.setFont(resourcesTable.getFont().deriveFont((float)11));
        resourcesTable.setAutoCreateRowSorter (true);
        resourcesTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        resourcesTable.setColumnSelectionAllowed(true);
        resourcesTable.setDoubleBuffered(true);
        resourcesTable.setDragEnabled(true);
        resourcesTable.setShowGrid(true);
        resourcesScroller.setViewportView(resourcesTable);

        int rowCounter = 0;
        for (Resource resource:server.getResourceList())
        {
            resourcesTable.setValueAt(resource.getId(), rowCounter, 0);
            resourcesTable.setValueAt(resource.getHostId(), rowCounter, 1);
            resourcesTable.setValueAt(resource.getCategory(), rowCounter, 2);
            resourcesTable.setValueAt(resource.getResourceType(), rowCounter, 3);
            resourcesTable.setValueAt(resource.getValueType(), rowCounter, 4);
            resourcesTable.setValueAt(resource.getCounterType(), rowCounter, 5);
            resourcesTable.setValueAt(resource.getResource(), rowCounter, 6);
            resourcesTable.setValueAt(resource.getPollCommand(), rowCounter, 7);
            resourcesTable.setValueAt(resource.getLastValue(), rowCounter, 8);
            resourcesTable.setValueAt(resource.getWarningLimit(), rowCounter, 9);
            resourcesTable.setValueAt(resource.getCriticalLimit(), rowCounter, 10);
            resourcesTable.setValueAt(resource.getAlertPolls(), rowCounter, 11);
            resourcesTable.setValueAt(resource.getUpdated().getTimeInMillis(), rowCounter, 12);
            resourcesTable.setValueAt(resource.getRRDFile(), rowCounter, 13);
            resourcesTable.setValueAt(resource.getRetentionTime(), rowCounter, 14);
            resourcesTable.setValueAt(resource.getEnabled(), rowCounter, 15);
            rowCounter++;
        }
        
        tableSplitter.setDividerLocation(40);

//      Implement the resourceTable ActionListener
        
        resourcesTableCellAction = new AbstractAction()
        {
            @Override public void actionPerformed(ActionEvent e)
            {
                DCMTableCellListener resourcesTableCellListener = (DCMTableCellListener)e.getSource();
                if (resourcesTableCellListener.getOldValue() != resourcesTableCellListener.getNewValue())
                {
//                    log("Id    : " + resourcesTableCellListener.getTable().getValueAt(resourcesTableCellListener.getRow(), 0) + " Row   : " + resourcesTableCellListener.getRow() + " Column: " + resourcesTableCellListener.getColumn() + " Old   : " + resourcesTableCellListener.getOldValue() + " New   : " + resourcesTableCellListener.getNewValue() ,true,true,true );
                    Resource resource = dcmDBClient.selectResource( Long.parseLong( resourcesTableCellListener.getTable().getValueAt(resourcesTableCellListener.getRow(), 0).toString() ) );
                    
                    int column = resourcesTableCellListener.getColumn();
                    switch (column) // Beware these table column numbers belong to the table, not to the object Host or Host Table in the metaDB (columnnumbers might differ)
                    {
                        case 6:     {  resource.setResource((String)resourcesTableCellListener.getNewValue()); log("Info: Resource updated",true,true,true); break; }
                        case 7:     {  resource.setPollCommand((String)resourcesTableCellListener.getNewValue()); log("Info: PollCommand updated",true,true,true); break; }
                        case 9:     {  resource.setWarningLimit((Double.parseDouble(resourcesTableCellListener.getNewValue().toString()))); log("Info: WarningLimit updated",true,true,true); break; }
                        case 10:    {  resource.setCriticalLimit((Double.parseDouble(resourcesTableCellListener.getNewValue().toString()))); log("Info: CriticalLimit updated",true,true,true); break; }
                        case 11:    {  resource.setAlertPolls((Integer.parseInt(resourcesTableCellListener.getNewValue().toString()))); log("Info: AlertPolls updated",true,true,true); break; }
                        case 13:    {  resource.setArchiveDBFile((String)resourcesTableCellListener.getNewValue()); log("Info: RRDFile updated",true,true,true); break; }
//                        case 14:    {  resource.setEnabled((String)resourcesTableCellListener.getNewValue()); log("Info: RRDFile updated",true,true,true); break; }
//                        default:    {  log("Info: This resource column is NOT editable",true,true,true);
//                                        try { dcManagerReference.fillinServerPane(serverTree.getSelectionPath().getLastPathComponent().toString()); } catch (CloneNotSupportedException ex) {  } }
                    }
//                    dcmMetaDBClient.updateResource(resource); Server server = new Server();
                    try { updateResource(resource); } catch (CloneNotSupportedException ex) {  }
                    
                    Server server = new Server();
//                    try { server = dcmMetaDBClient.selectServer(resource.getHostId()); } catch (CloneNotSupportedException ex) {  }
                    try { server = (Server) selectServerById(resource.getHostId()).clone(); } catch (CloneNotSupportedException ex) {  }
//                    try { PushPollScript pushPollScript = new PushPollScript(dcManagerReference, server); } catch (UnknownHostException ex) { }
                    pushPollScript(dcmDesktopReference, server);
                }
            }
        };
        resourcesTableCellListener = new DCMTableCellListener(resourcesTable, resourcesTableCellAction);
        
        // And another listener, just to overcome the boolean field in this resource table
        resourcesTable.getModel().addTableModelListener(new TableModelListener()
        {
            @Override public void tableChanged(TableModelEvent e)
            {
                if (e.getColumn() == 15) // NEVER forget this one if the columns in this table changes
                {
                    TableModel model = (TableModel)e.getSource();
                    Object data = model.getValueAt(e.getLastRow(), e.getColumn());                        
                    //log("id: " + model.getValueAt(e.getLastRow(), 0) + " data: " + data,true,true,true);
//                    Resource resource = dcmMetaDBClient.selectResource( Long.parseLong( model.getValueAt(e.getLastRow(), 0).toString() ) );
                    Resource resource = selectResourceById( Long.parseLong( model.getValueAt(e.getLastRow(), 0).toString() ) );
                    if (data.toString().equals("true")) { resource.setEnabled(true); } else { resource.setEnabled(false); }
//                    dcmMetaDBClient.updateResource(resource);                            
                    try { updateResource(resource); } catch (CloneNotSupportedException ex) {   }
                }
            }
        });    
    }

    // Begin MiddleWare
    public Server selectServerById(long idParam)
    {
        Server server = new Server();try { server = dcmDBClient.getServer(idParam); } catch (CloneNotSupportedException ex) {  }
//        Server server = new Server(); server = dcmMetaDBClient.getServer(idParam);
        return server;
    }
    // End MiddleWare
        
    // Begin MiddleWare
    public Resource selectResourceById(long idParam)
    {
        Resource resource = new Resource();try { resource = (Resource) dcmDBClient.selectResource(idParam).clone(); } catch (CloneNotSupportedException ex) {  }
        return resource;
    }
    // End MiddleWare
        
    // Begin MiddleWare
    public void pushPollScript(DCMDesktop dcManagerReference, Server serverParam)
    {
        try { DCMPushPollScript pushPollScript = new DCMPushPollScript(dcManagerReference, serverParam); } catch (UnknownHostException ex) { }
    }
    // End MiddleWare
        
    // MiddleWare
    public void updateResource(Resource resourceParam) throws CloneNotSupportedException
    {
        dcmDBClient.updateResource(resourceParam);
    }
    
    // Begin FrontEnd
    private void resetServerPane() throws CloneNotSupportedException
    {
        hostScroller.setVisible(false);
        resourcesScroller.setVisible(false);
        tableSplitter.setDividerLocation(0);

        boolean enabled = false;
        //server = (Server) javaDBClient.selectServerByHostname(hostnameParam).clone();
        hostTable.setValueAt("", 0,0);
        hostTable.setValueAt("", 0,1);
        hostTable.setValueAt("", 0,2);
        hostTable.setValueAt("", 0,3);
        hostTable.setValueAt("", 0,4);
        hostTable.setValueAt("", 0,5);
        hostTable.setValueAt(enabled, 0,6);
        
        Object[][] thisData = new Object[1][15];
        String[]   header = new String [] {"Id", "HostId", "Category", "ResourceType", "ValueType", "CounterType", "Resource", "PollCommand", "LastValue", "WarningLimit", "CriticalLimit", "AlertPolls", "Updated", "RRDFile", "Retention", "Enabled"};
        Class[] types = new Class []
	{
	    java.lang.Long.class,
	    java.lang.Integer.class,
	    java.lang.String.class,
	    java.lang.String.class,
	    java.lang.String.class,
	    java.lang.String.class,
	    java.lang.String.class,
	    java.lang.String.class,
	    java.lang.Double.class,
	    java.lang.Double.class,
	    java.lang.Double.class,
	    java.lang.Integer.class,
	    java.lang.Long.class,
	    java.lang.String.class,
	    java.lang.Integer.class,
	    java.lang.Boolean.class
	};

        resourcesTable.setModel(new javax.swing.table.DefaultTableModel(thisData,header)
        {
            Class[] types = new Class [] 
	    {
		java.lang.Long.class,
		java.lang.Integer.class,
		java.lang.String.class,
		java.lang.String.class,
		java.lang.String.class,
		java.lang.String.class,
		java.lang.String.class,
		java.lang.String.class,
		java.lang.Double.class,
		java.lang.Double.class,
		java.lang.Double.class,
		java.lang.Integer.class,
		java.lang.Long.class,
		java.lang.String.class,
		java.lang.Integer.class,
		java.lang.Boolean.class
	    };
            boolean[] canEdit = new boolean []
	    {
                false, false, false, false, false, false, true, true, true, true, true, true, false, true, false, true
            };

            @Override
            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });

        resourcesTable.setAutoCreateRowSorter(true);
        resourcesScroller.setViewportView(resourcesTable);
    }
    // End FrontEnd

    @Override    
    public synchronized void log(final String messageParam, final boolean logToStatusParam, final boolean logToApplicationParam, final boolean logToFileParam)
    {
        Thread LogThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                if (logToStatusParam)       { statusLabel.setText(messageParam); }
                if (logToApplicationParam)
                {
                    Color color = new Color(255,255,255);
                    if      ( messageParam.toLowerCase().contains("error"))     { color = Color.red; }
                    else if ( messageParam.toLowerCase().contains("failure"))   { color = Color.red; }
                    else if ( messageParam.toLowerCase().contains("success"))   { color = Color.green; }
                    else if ( messageParam.toLowerCase().contains("retry"))     { color = Color.white; }
                    else if ( messageParam.toLowerCase().contains("warning"))   { color = Color.orange; }
                    else if ( messageParam.toLowerCase().contains("advise"))    { color = Color.yellow; }
                    else                                                        { color = Color.lightGray; }
                    colorPane.append(color, messageParam + "\n");
                }
                if (logToFileParam)         { logToFile(messageParam); }
            }
        });
        LogThread.setName("LogThread");
        LogThread.setDaemon(false);
        LogThread.setPriority(Thread.NORM_PRIORITY);
        LogThread.start();
    }

    public synchronized void logToFile(final String displaymessage)
    {
        Thread logToFileThread = new Thread(new Runnable()
        {
            @Override
            @SuppressWarnings({"static-access"})
            public void run()
            {
                Calendar logCalendar = Calendar.getInstance();

                String humanDate = "" +
                String.format("%04d", logCalendar.get(Calendar.YEAR)) + "-" +
                String.format("%02d", logCalendar.get(Calendar.MONTH) + 1) + "-" +
                String.format("%02d", logCalendar.get(Calendar.DAY_OF_MONTH)) + " " +
                String.format("%02d", logCalendar.get(Calendar.HOUR_OF_DAY)) + ":" +
                String.format("%02d", logCalendar.get(Calendar.MINUTE)) + ":" +
                String.format("%02d", logCalendar.get(Calendar.SECOND));

                try { logFileWriter = new FileWriter(logFileString, true ); } catch (IOException ex) { System.out.println("Error: DCMDesktop: IOException: new FileWriter(" + logFileString + ")" + ex.getMessage()); logBuffer += humanDate + " " + displaymessage + "\r\n"; return; }
                try { logFileWriter.flush(); } catch (IOException ex) { System.out.println("Error: DCMDesktop: IOException: logFileWriter.flush() inst:1;"); logBuffer += humanDate + " " + displaymessage + "\r\n"; return; }
                try { logFileWriter.write(logBuffer + humanDate + " " + displaymessage + "\r\n"); } catch (IOException ex) { System.out.println("Error:  DCMDesktop: IOException: logFileWriter.write()"); logBuffer += humanDate + " " + displaymessage + "\r\n"; return; }
                try { logFileWriter.flush(); } catch (IOException ex) { System.out.println("Error: DCMDesktop: IOException: logFileWriter.flush() inst:2;"); logBuffer += humanDate + " " + displaymessage + "\r\n"; return; }

                logBuffer = "";

                try { logFileWriter.close(); }
                catch (IOException ex) { System.out.println("Error:  DCMDesktop: IOException: logFileWriter.close();"); }
            }
        });
        logToFileThread.setName("logToFileThread");
        logToFileThread.setDaemon(runThreadsAsDaemons);
        logToFileThread.start();
    }

    @Override
    public void dbClientReady()
    {
        dcmDBClientIsReady = true;
    }

    @Override public void updatedHost() // if the hostname changed then reload the serverTree hostname, but not with other hostchanges like Enabled change
    {
        if (debug) { log("Host Updated by DCMDBClient...",false,true,true); }
        if (hostnameUpdated) { try { buildServerTree(); } catch (CloneNotSupportedException ex) {  } }
    }

    @Override
    public void pollServerReady()
    {
    }

    public boolean getDebug()    { return debug; }

    // FrontEnd
    private synchronized void updateTimeWidgets(final Calendar startCalendarParam, final Calendar endCalendarParam)
    {
        Thread updateTimeWidgetsThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                boolean startCalendarEnabled = startDateChooserPanel.isEnabled(); startDateChooserPanel.setEnabled(true);
                boolean startCalendarLocked = startDateChooserPanel.isLocked(); startDateChooserPanel.setLocked(false);
                startDateChooserPanel.setSelectedDate(startCalendarParam);
                startDateChooserPanel.setLocked(startCalendarLocked); startDateChooserPanel.setEnabled(startCalendarEnabled);
                
                boolean endCalendarEnabled = endDateChooserPanel.isEnabled(); endDateChooserPanel.setEnabled(true);
                boolean endCalendarLocked = endDateChooserPanel.isLocked(); endDateChooserPanel.setLocked(false);
                endDateChooserPanel.setSelectedDate(endCalendarParam);
                endDateChooserPanel.setLocked(endCalendarLocked);endDateChooserPanel.setEnabled(endCalendarEnabled);

                // Set the hour/minute sliders
                startHourSlider.setValue(startCalendarParam.get(Calendar.HOUR_OF_DAY));
                startMinuteSlider.setValue(startCalendarParam.get(Calendar.MINUTE));
                endHourSlider.setValue(endCalendarParam.get(Calendar.HOUR_OF_DAY));
                endMinuteSlider.setValue(endCalendarParam.get(Calendar.MINUTE));

                // Set the hour/minute display
                String startTimeText = new String(); startTimeText = String.format("%02d:%02d", startCalendarParam.get(Calendar.HOUR_OF_DAY), startCalendarParam.get(Calendar.MINUTE)); startTimeField.setText(startTimeText);
                String endTimeText = new String(); endTimeText = String.format("%02d:%02d", endCalendarParam.get(Calendar.HOUR_OF_DAY),endCalendarParam.get(Calendar.MINUTE)); endTimeField.setText(endTimeText);
            }
        });
        updateTimeWidgetsThread.setName("updateTimeWidgetsThread");
        updateTimeWidgetsThread.setDaemon(false);
        updateTimeWidgetsThread.setPriority(Thread.NORM_PRIORITY);
        updateTimeWidgetsThread.start();
    }
    
    public static void main(final String args[])
    {
        java.awt.EventQueue.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                boolean daemon = false;
                boolean debug = false;
                for (int i=0; i < args.length; i++)
                {
                    if      ( args[i].equals("--daemon")) { daemon = true; }
                    else if ( args[i].equals("--debug")) { debug = true; }
                    else if (args[i].equals("--help")) { usage(); System.exit(1); }
                }
                try { new DCMDesktop(daemon,debug); } catch (UnsupportedLookAndFeelException ex) { System.out.println("Error: UnsupportedLookAndFeelException in main method"); }
            }
        });
    }

    private static void usage()
    {
        System.out.println("\n");
        System.out.println("Usage:   java -cp DCManager.jar DCManager [--debug]\n");
        System.out.println(DCMLicense.getCopyright());
    }

    class RunnableImpl implements Runnable {

        public RunnableImpl() {
        }

        @Override
        public void run()
        {
            // The part below takes the lines in the selectionArea and turns every column into a uniq hash for stats
            long selectedResources = 0;
            long totalResources = dcmDBClient.getResourceCount();
            long uniqueServers;
            long uniqueCategories;
            long uniqueRTypes;
            long uniqueVTypes;
            long uniqueResources;

            // Breaking down stats unique values with hashes (which can only contain unique values)
            Set<String> totHash = new HashSet<String>();
            Set<String> serverHash = new HashSet<String>();
            Set<String> categoryHash = new HashSet<String>();
            Set<String> resourceTypeHash = new HashSet<String>();
            Set<String> valueTypeHash = new HashSet<String>();
            Set<String> resourceHash = new HashSet<String>();

            String[] lineArray = new String[4];
            BufferedReader reader = new BufferedReader(new StringReader(selectionArea.getText()));
            String line = "";
            output = "";

            try
            {
                while ((line = reader.readLine()) != null)
                {
                    if (line.length()>4)
                    {
                        selectedResources++;
                        lineArray = line.split("\\s+");
                        if ((lineArray[0] != null) && (lineArray[0].length()>0)) {totHash.add(lineArray[0]);}
                        if ((lineArray[1] != null) && (lineArray[1].length()>0)) {serverHash.add(lineArray[1]);}
                        if ((lineArray[2] != null) && (lineArray[2].length()>0)) {categoryHash.add(lineArray[2]);}
                        if ((lineArray[3] != null) && (lineArray[3].length()>0)) {resourceTypeHash.add(lineArray[3]);}
                        if ((lineArray[4] != null) && (lineArray[3].length()>0)) {valueTypeHash.add(lineArray[4]);}
                        if ((lineArray[5] != null) && (lineArray[4].length()>0)) {resourceHash.add(lineArray[5]);}                    
                    }
                }

            } catch(IOException ex) { log("Error:   DCMDesktop: selectionAreaCaretUpdate: IOException: while ((line = reader.readLine())" + ex.getMessage(),true,true,true); }

    //        totalResources =    totHash.size();
    //        totalResources =    selectionArea.getLineCount();
            uniqueServers =     serverHash.size();
            uniqueCategories =  categoryHash.size();
            uniqueRTypes =      resourceTypeHash.size();
            uniqueVTypes =      valueTypeHash.size();
            uniqueResources =   resourceHash.size();

            updateSearchStatsTable(Long.toString(selectedResources) + "/" + Long.toString(totalResources),Long.toString(uniqueServers),Long.toString(uniqueCategories),Long.toString(uniqueRTypes),Long.toString(uniqueVTypes),Long.toString(uniqueResources));

            if (selectedResources > 0)
            {
                enableNavigationButtons(true);
            }
            else
            {
                enableNavigationButtons(false);
            }
        }
    }
}

// ⃘ ⚭ ⃠ ∅ ⊗ ⊝ ⌽ ⚮ ⧬ ⧭ ℗ ✹ ✱ ❋ ✺ ⹿
// ░  ☑ ☒ ⨱ ⍰ ⍐ ⍗  ▢ ◇ ☍ ⬚
// ⤫ ✗ ✕ 〷 ⌘ ⎌ ⨯
// ✁ ✃ ✂ ✄ ⌛ ☕ ♨ ⚐ ⚑ ⧥ ☠  ⎗ ⎘ ✌ ⧌ ⟁ ¹ ² ☂ ☁ ☙ ⸛ ✌ ⸟ ⧞  ˟ ῁ ⤦ ⸕   ⎗ ⎘ ☮ ✉ ࿃ Ꙭ ꙭ Ꝏ ꝏ
// ☏ ☎ ✆  ⌚ ⨶ ⍾ ⎋ ☄ ◎ ⚒ ⏏ 
// Ⓐ Ⓑ Ⓒ Ⓔ Ⓓ Ⓔ Ⓕ Ⓖ Ⓗ Ⓘ Ⓙ Ⓚ Ⓛ Ⓜ Ⓝ Ⓞ Ⓟ Ⓠ Ⓡ Ⓢ Ⓣ Ⓤ Ⓥ Ⓦ Ⓧ Ⓨ Ⓩ ␛ ␦ ® ␇ ␅ ␖ ␘ ☡ ﹟ ？ ⌗ ␛ ♯ ⸮ ❢ ℕ № ‼ ➊ ➁ ➀ ➋ ₠ ¼ ½ ¾  ₠
// ☊ ↻ ⃔ ↧ ↥ ↗ ≺ ≪ ⏎ ◁ ← ↖ ↙ ↰ ↲ ↵ ⇖ ⇙ ⇐ ⇠ ⇣ ⇦ ➘ ⟀ ⥂ ⥃ ⥳ ⥴ ⬉ ⬋ ⬑ ⬐ ￩ ⇇ ⇜ ⇤ ⤽ ⤼ ♐ ⥇  ↑ ↓ ↟ ↡ ↥ ↧ ⇑ ⇓ ⇗ ⇘ ⇞ ⇟ ⇈ ⇊ ⇪ ⇶ ⍐ ⍇ ⍈ ⍗   ⇠ ⇡ ⇣
// ↆ ⇪ ⊻ ⊼ ⏏ ▲ △ ▵ ▼ ▽ ⬆ ⬇ ⇡
// ♫ ♪ ♩
// ℡ ℡ ⫶ ⋰ ⋱ ⋯ ⁚ ♒ ⧣ ⧤ ⩶ ⎓ ⑊ ⍼ ⍭ ⍲  ̷
// ■ ▣ █  ⅓ ¼ ½ ¾ ‰ ‱
// ‣ • ‖ ⌨ ⎆ ⎗ ⎘ ⎙ ► ▸ ✈  Ⓜ Ⓢ ⎮ ❙❙ ⚭ ⚮ ❒
// ↕↔ ←↑→↓
