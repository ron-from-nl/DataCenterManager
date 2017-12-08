import data.Configuration;
import data.ConfigurationCaller;
import data.Host;
import data.Server;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.text.BadLocationException;

public class DCMCommanderClient extends javax.swing.JFrame  implements ConfigurationCaller, DCMRemoteCommandCaller
{
    
//  STDIN & STDOUT Redirection
    public class FilteredStream extends FilterOutputStream
    {
        public FilteredStream(OutputStream aStream)
        {
            super(aStream);
        }

        @Override
        public void write(byte b[]) throws IOException
        {
//            String aString = new String(b);
//            StringBuffer aString = new StringBuffer();
//            aString.append(b);
        }

        @Override
        public void write(byte b[], int off, int len) throws IOException
        {
//            String aString = new String(b , off , len);
//            StringBuffer aString = new StringBuffer();
//            aString.
        }
    }
//  End Redirection

    private Server                  host;
    private ArrayList<Host>         hostList;
    private StringBuffer            command;
    private int                     sessionTimeout = 5;

    private int                     superuserPasswordsCounter = 0;
    private long                    totalCounter = 0;
    private int                     serversCounter = 0;
    private int                     commandCounter = 0;
    private int                     lastCommandCounter = 0;
    private int                     serverInstanceCounter = 0;
    private String                  scriptTip = "# Commands or Script to Execute";

//    private DCMVergunning           dcmVergunning;
    private ArrayList<Server>       serverList;

    private final int               CMDSTAGE = 2; // 2 = Commander Command
    private boolean                 dcmDBClientIsReady = false;

    private String                  text;
    private static final String     THISPRODUCT = "DCMCommanderClient";

    private final Configuration     configuration;
//    private DCMDBClient             dcmDBClient;
//    private DCMDBClientCaller       dcmDBClientCaller;
//    private Thread                  dcmDBClientThread;
    private DCMRemoteCommandCaller  dcmRemoteCommandCaller;
    private ConfigurationCaller     configurationCaller;
    private boolean                 debug = false;
    private boolean                 threaded = false;
    protected DCMCommanderClientRMI dcmCommanderClientRMI;

    public DCMCommanderClient(String dcmServerNameParam, String dcmServerPort, boolean debugParam)
    {
        debug = debugParam;
//      STDIN & STDOUT Redirection
        if (!debug)
        {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            FilteredStream filteredStream = new FilteredStream(byteArrayOutputStream);
            PrintStream aPrintStream  = new PrintStream(filteredStream);
            System.setOut(aPrintStream); System.setErr(aPrintStream);
        } else { log(THISPRODUCT + " debugging enabled...", true,true,true); }
        dcmCommanderClientRMI = new DCMCommanderClientRMI(this);
                
        setMinimumSize(new Dimension(849, 750));
//        setPreferredSize(new Dimension(Toolkit.getDefaultToolkit().getScreenSize().width,Toolkit.getDefaultToolkit().getScreenSize().height));
        initComponents();
        try
        { UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel"); }
        catch (ClassNotFoundException ex) { }
        catch (InstantiationException ex) { }
        catch (IllegalAccessException ex) { }
        catch (UnsupportedLookAndFeelException ex) { }
        Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
        int winWidth = (int)getWidth();
        int winHeight = (int)getHeight();
        int posX = Math.round((screenDim.width / 2) - (winWidth / 2));
        int posY = Math.round((screenDim.height / 2) - (winHeight / 2));
        setLocation(posX, posY);

        dcmRemoteCommandCaller = this;
//        dcmDBClientCaller = this;
        configuration = new Configuration(configurationCaller);
        setTitle(DCMLicense.getProcuct() + " " + DCMLicense.getVersion());

        if ( dcmCommanderClientRMI.connect(dcmServerNameParam, dcmServerPort) ) { outputArea.append("Connected to DCMServer"); } else { outputArea.append("Could NOT connect to DCMServer !!!"); }

        commandField.setText(scriptTip);

        updateSearchStatsTable("0","0","0");
        serversListValidation();
        commandFieldValidation();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        searchServerPanel = new javax.swing.JPanel();
        searchExactButton = new javax.swing.JButton();
        searchField = new javax.swing.JTextField();
        searchNonExactButton = new javax.swing.JButton();
        selectionScroller = new javax.swing.JScrollPane();
        serversList = new javax.swing.JTextArea();
        searchStatsScroller = new javax.swing.JScrollPane();
        searchStatsTable = new javax.swing.JTable();
        executeSequentialButton = new javax.swing.JButton();
        executeParallelButton = new javax.swing.JButton();
        timeoutField = new javax.swing.JTextField();
        spawnSpeed = new javax.swing.JTextField();
        progressBar = new javax.swing.JProgressBar();
        splitter = new javax.swing.JSplitPane();
        commandPanel = new javax.swing.JPanel();
        topScroller = new javax.swing.JScrollPane();
        commandField = new javax.swing.JEditorPane();
        outputPanel = new javax.swing.JPanel();
        outputScroller = new javax.swing.JScrollPane();
        outputArea = new javax.swing.JTextArea();
        copyrightLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setBackground(new java.awt.Color(204, 204, 204));
        setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("DCMCommanderClient"); // NOI18N
        searchServerPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), bundle.getString("DCMCommanderClient.searchServerPanel.border.title"), javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Lucida Grande", 0, 12), new java.awt.Color(102, 102, 102))); // NOI18N
        searchServerPanel.setFont(searchServerPanel.getFont());
        searchServerPanel.setOpaque(false);
        searchServerPanel.setPreferredSize(new java.awt.Dimension(190, 314));

        searchExactButton.setFont(searchExactButton.getFont().deriveFont(searchExactButton.getFont().getSize()-3f));
        searchExactButton.setText(bundle.getString("DCMCommanderClient.searchExactButton.text")); // NOI18N
        searchExactButton.setToolTipText(bundle.getString("DCMCommanderClient.searchExactButton.toolTipText")); // NOI18N
        searchExactButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        searchExactButton.setMaximumSize(new java.awt.Dimension(80, 10));
        searchExactButton.setMinimumSize(new java.awt.Dimension(80, 10));
        searchExactButton.setPreferredSize(new java.awt.Dimension(80, 10));
        searchExactButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchExactButtonActionPerformed(evt);
            }
        });

        searchField.setFont(searchField.getFont().deriveFont(searchField.getFont().getSize()-3f));
        searchField.setForeground(new java.awt.Color(102, 102, 102));
        searchField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        searchField.setToolTipText(bundle.getString("DCMCommanderClient.searchField.toolTipText")); // NOI18N
        searchField.setMaximumSize(new java.awt.Dimension(900, 20));
        searchField.setMinimumSize(new java.awt.Dimension(14, 20));
        searchField.setPreferredSize(new java.awt.Dimension(48, 20));
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                searchFieldKeyReleased(evt);
            }
        });

        searchNonExactButton.setFont(searchNonExactButton.getFont().deriveFont(searchNonExactButton.getFont().getSize()-3f));
        searchNonExactButton.setText(bundle.getString("DCMCommanderClient.searchNonExactButton.text")); // NOI18N
        searchNonExactButton.setToolTipText(bundle.getString("DCMCommanderClient.searchNonExactButton.toolTipText")); // NOI18N
        searchNonExactButton.setMaximumSize(new java.awt.Dimension(80, 20));
        searchNonExactButton.setMinimumSize(new java.awt.Dimension(80, 20));
        searchNonExactButton.setPreferredSize(new java.awt.Dimension(112, 20));
        searchNonExactButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchNonExactButtonActionPerformed(evt);
            }
        });

        selectionScroller.setFont(selectionScroller.getFont());

        serversList.setBackground(new java.awt.Color(204, 204, 204));
        serversList.setColumns(20);
        serversList.setFont(new java.awt.Font("Courier New", 1, 8)); // NOI18N
        serversList.setForeground(new java.awt.Color(51, 51, 51));
        serversList.setRows(5);
        serversList.setToolTipText(bundle.getString("DCMCommanderClient.serversList.toolTipText")); // NOI18N
        serversList.setDoubleBuffered(true);
        serversList.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                serversListCaretUpdate(evt);
            }
        });
        selectionScroller.setViewportView(serversList);

        searchStatsScroller.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        searchStatsScroller.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        searchStatsTable.setBackground(new java.awt.Color(204, 204, 204));
        searchStatsTable.setFont(searchStatsTable.getFont().deriveFont(searchStatsTable.getFont().getSize()-4f));
        searchStatsTable.setForeground(new java.awt.Color(102, 102, 102));
        searchStatsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Tot", "Servers", "OS's"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Long.class, java.lang.Long.class, java.lang.Long.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
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
        searchStatsTable.setShowGrid(true);
        searchStatsScroller.setViewportView(searchStatsTable);

        executeSequentialButton.setFont(executeSequentialButton.getFont().deriveFont(executeSequentialButton.getFont().getSize()-3f));
        executeSequentialButton.setText(bundle.getString("DCMCommanderClient.executeSequentialButton.text")); // NOI18N
        executeSequentialButton.setToolTipText(bundle.getString("DCMCommanderClient.executeSequentialButton.toolTipText")); // NOI18N
        executeSequentialButton.setEnabled(false);
        executeSequentialButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                executeSequentialButtonActionPerformed(evt);
            }
        });

        executeParallelButton.setFont(executeParallelButton.getFont().deriveFont(executeParallelButton.getFont().getSize()-3f));
        executeParallelButton.setText(bundle.getString("DCMCommanderClient.executeParallelButton.text")); // NOI18N
        executeParallelButton.setToolTipText(bundle.getString("DCMCommanderClient.executeParallelButton.toolTipText")); // NOI18N
        executeParallelButton.setActionCommand(bundle.getString("DCMCommanderClient.executeParallelButton.actionCommand")); // NOI18N
        executeParallelButton.setEnabled(false);
        executeParallelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                executeParallelButtonActionPerformed(evt);
            }
        });

        timeoutField.setFont(timeoutField.getFont().deriveFont(timeoutField.getFont().getSize()-3f));
        timeoutField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        timeoutField.setText(bundle.getString("DCMCommanderClient.timeoutField.text")); // NOI18N
        timeoutField.setToolTipText(bundle.getString("DCMCommanderClient.timeoutField.toolTipText")); // NOI18N
        timeoutField.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        timeoutField.setSize(new java.awt.Dimension(28, 26));

        spawnSpeed.setFont(spawnSpeed.getFont().deriveFont(spawnSpeed.getFont().getSize()-3f));
        spawnSpeed.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        spawnSpeed.setText(bundle.getString("DCMCommanderClient.spawnSpeed.text")); // NOI18N
        spawnSpeed.setToolTipText(bundle.getString("DCMCommanderClient.spawnSpeed.toolTipText")); // NOI18N
        spawnSpeed.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        spawnSpeed.setSize(new java.awt.Dimension(28, 26));

        progressBar.setFont(progressBar.getFont().deriveFont(progressBar.getFont().getSize()-3f));
        progressBar.setToolTipText(bundle.getString("DCMCommanderClient.progressBar.toolTipText")); // NOI18N
        progressBar.setStringPainted(true);

        org.jdesktop.layout.GroupLayout searchServerPanelLayout = new org.jdesktop.layout.GroupLayout(searchServerPanel);
        searchServerPanel.setLayout(searchServerPanelLayout);
        searchServerPanelLayout.setHorizontalGroup(
            searchServerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(searchServerPanelLayout.createSequentialGroup()
                .add(searchExactButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 103, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(searchField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(searchNonExactButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 101, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, searchStatsScroller, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 864, Short.MAX_VALUE)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, searchServerPanelLayout.createSequentialGroup()
                .add(timeoutField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(spawnSpeed, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 35, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(progressBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 482, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(executeSequentialButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(executeParallelButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 135, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(selectionScroller)
        );
        searchServerPanelLayout.setVerticalGroup(
            searchServerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(searchServerPanelLayout.createSequentialGroup()
                .add(searchServerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(searchField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(searchExactButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 29, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(searchNonExactButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(selectionScroller, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 115, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(searchStatsScroller, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(searchServerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(executeParallelButton, 0, 0, Short.MAX_VALUE)
                    .add(executeSequentialButton, 0, 0, Short.MAX_VALUE)
                    .add(progressBar, 0, 0, Short.MAX_VALUE)
                    .add(spawnSpeed)
                    .add(timeoutField))
                .addContainerGap(13, Short.MAX_VALUE))
        );

        splitter.setDividerLocation(150);
        splitter.setDividerSize(2);
        splitter.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        splitter.setSize(new java.awt.Dimension(244, 200));

        topScroller.setFont(topScroller.getFont());

        commandField.setFont(new java.awt.Font("Courier New", 1, 10)); // NOI18N
        commandField.setToolTipText(bundle.getString("DCMCommanderClient.commandField.toolTipText")); // NOI18N
        commandField.setPreferredSize(new java.awt.Dimension(100, 50));
        commandField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                commandFieldFocusGained(evt);
            }
        });
        commandField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                commandFieldKeyReleased(evt);
            }
        });
        topScroller.setViewportView(commandField);

        org.jdesktop.layout.GroupLayout commandPanelLayout = new org.jdesktop.layout.GroupLayout(commandPanel);
        commandPanel.setLayout(commandPanelLayout);
        commandPanelLayout.setHorizontalGroup(
            commandPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, topScroller, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 872, Short.MAX_VALUE)
        );
        commandPanelLayout.setVerticalGroup(
            commandPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, topScroller, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE)
        );

        splitter.setLeftComponent(commandPanel);

        outputScroller.setAutoscrolls(true);
        outputScroller.setFont(outputScroller.getFont());

        outputArea.setBackground(new java.awt.Color(204, 204, 204));
        outputArea.setColumns(20);
        outputArea.setEditable(false);
        outputArea.setFont(new java.awt.Font("Courier New", 1, 10)); // NOI18N
        outputArea.setRows(5);
        outputArea.setToolTipText(bundle.getString("DCMCommanderClient.outputArea.toolTipText")); // NOI18N
        outputScroller.setViewportView(outputArea);

        org.jdesktop.layout.GroupLayout outputPanelLayout = new org.jdesktop.layout.GroupLayout(outputPanel);
        outputPanel.setLayout(outputPanelLayout);
        outputPanelLayout.setHorizontalGroup(
            outputPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 872, Short.MAX_VALUE)
            .add(outputPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(outputScroller, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 872, Short.MAX_VALUE))
        );
        outputPanelLayout.setVerticalGroup(
            outputPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 164, Short.MAX_VALUE)
            .add(outputPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(outputScroller, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE))
        );

        splitter.setRightComponent(outputPanel);

        copyrightLabel.setBackground(new java.awt.Color(153, 153, 153));
        copyrightLabel.setFont(new java.awt.Font("STHeiti", 1, 14)); // NOI18N
        copyrightLabel.setForeground(new java.awt.Color(51, 51, 51));
        copyrightLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        copyrightLabel.setText(bundle.getString("DCMCommanderClient.copyrightLabel.text")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(searchServerPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 876, Short.MAX_VALUE)
            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(splitter))
            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(copyrightLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 876, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(searchServerPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 236, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(334, Short.MAX_VALUE))
            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(layout.createSequentialGroup()
                    .add(246, 246, 246)
                    .add(splitter)
                    .addContainerGap()))
            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(layout.createSequentialGroup()
                    .addContainerGap(556, Short.MAX_VALUE)
                    .add(copyrightLabel)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void searchExactButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchExactButtonActionPerformed
            setSelectionArea(searchExactHosts(searchField.getText()));
    }//GEN-LAST:event_searchExactButtonActionPerformed

    private void searchFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchFieldKeyReleased
//        entre = 10)
        if ( evt.getKeyCode() == 10 )
        {
            if (searchField.getText().length() > 0)
            {
//                setSelectionArea(dcmMetaDBClient.convertSearchStringToResourcesText(searchField.getText(),false));
                setSelectionArea(searchNonExactHosts(searchField.getText())); // MiddleWare Invocation
            }
            else
            {
                setSelectionArea("");
            }            
        }
    }//GEN-LAST:event_searchFieldKeyReleased

    private void searchNonExactButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchNonExactButtonActionPerformed
            setSelectionArea(searchNonExactHosts(searchField.getText()));
    }//GEN-LAST:event_searchNonExactButtonActionPerformed

    private void serversListCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_serversListCaretUpdate
        Thread selectionAreaCaretUpdateThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                // The part below takes the lines in the selectionArea and turns every column into a uniq hash for stats
                long uniqueHostIDs;
                long uniqueServers;
                long uniqueOSs;

                // Breaking down stats unique values with hashes (which can only contain unique values)
                Set<String> hostIdHash = new HashSet<String>();
                Set<String> serverHash = new HashSet<String>();
                Set<String> ossHash = new HashSet<String>();

                String[] lineArray = new String[2];
                totalCounter = 0;
                BufferedReader reader = new BufferedReader(new StringReader(serversList.getText()));
                String line = "";
                String output = "";

                try
                {
                    while ((line = reader.readLine()) != null)
                    {
                        if (line.length()>4)
                        {
                            lineArray = line.split("\\s+");
                            if ((lineArray[0] != null) && (lineArray[0].length()>0)) {totalCounter++;}
                            if ((lineArray[1] != null) && (lineArray[1].length()>0)) {serverHash.add(lineArray[1]);}
                            if ((lineArray[2] != null) && (lineArray[2].length()>0)) {ossHash.add(lineArray[2]);}
                        }
                    }

                } catch(IOException e) { e.printStackTrace(); }
                updateSearchStatsTable(Long.toString(totalCounter),Long.toString(serverHash.size()),Long.toString(ossHash.size()));
            }
        });
        selectionAreaCaretUpdateThread.setName("selectionAreaCaretUpdateThread");
        selectionAreaCaretUpdateThread.setDaemon(false);
        selectionAreaCaretUpdateThread.setPriority(Thread.NORM_PRIORITY);
        selectionAreaCaretUpdateThread.start();
        serversListValidation();
    }//GEN-LAST:event_serversListCaretUpdate

    private void executeParallelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_executeParallelButtonActionPerformed
        threaded = true; execute();        
    }//GEN-LAST:event_executeParallelButtonActionPerformed

    private void commandFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_commandFieldFocusGained
        if (commandField.getText().equals(scriptTip)) {             commandField.setText("");         }
    }//GEN-LAST:event_commandFieldFocusGained

    private void commandFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_commandFieldKeyReleased
        commandFieldValidation();
    }//GEN-LAST:event_commandFieldKeyReleased

    private void executeSequentialButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_executeSequentialButtonActionPerformed
        threaded = false; execute();        
    }//GEN-LAST:event_executeSequentialButtonActionPerformed

    // FrontEnd
    private void setSelectionArea(String dataParam)
    {
        serversList.setText(dataParam);
    }

    private void serversListValidation()
    {
        serversCounter = 0;
        text     = serversList.getText();
        for (int lineCounter = 0; lineCounter <= serversList.getLineCount();lineCounter++)
        {
            try
            {
                String line = text.substring(serversList.getLineStartOffset(lineCounter), serversList.getLineEndOffset(lineCounter));
                if (contains(line, "[a-zA-Z0-9]+")) { serversCounter++; }
            }
            catch (BadLocationException ex) { }
        }
        if (serversCounter == 0)
        {
            serversList.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 0, 0)));
        }
        else
        {
            serversList.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 255, 0)));
        }
        validateForm();
    }

//    public ArrayList<Server> getServerList() throws CloneNotSupportedException
//    {
//        // JavaDB & RRDB creation finished, now update serversTree
//        ArrayList<Server> serverList = new ArrayList<Server>();
//        serverList = dcmCommanderClientRMI.getServerList();
//        return serverList;
//    }
//    
    // Begin MiddleWare
    private String searchExactHosts(String searchString)
    {
        serverList = new ArrayList<Server>();
        try { serverList = dcmCommanderClientRMI.getServerList(); } catch (CloneNotSupportedException ex) {   }
        String output = "";
        ArrayList<Server> serverListLimited = new ArrayList<Server>();
        if ( dcmCommanderClientRMI.getServersInLicense() < serverList.size()) // We can not get a sublist from the serverList that is larger than serverList (outofbounds error)
        {
            for (Server server:serverList.subList(0, dcmCommanderClientRMI.getServersInLicense())) {serverListLimited.add(server);}
        }
        else
        {
            serverListLimited = serverList;
        }        

        if (searchString.length() > 0) { output = dcmCommanderClientRMI.searchExactHosts(searchField.getText(),serverListLimited); }
        return output;
    }    
    // End MiddleWare
    
    // Begin MiddleWare
    private String searchNonExactHosts(String searchString)
    {
        serverList = new ArrayList<Server>();
        try { serverList = dcmCommanderClientRMI.getServerList(); } catch (CloneNotSupportedException ex) {   }
        String output = "";
        ArrayList<Server> serverListLimited = new ArrayList<Server>();
        if ( dcmCommanderClientRMI.getServersInLicense() < serverList.size()) // We can not get a sublist from the serverList that is larger than serverList (outofbounds error)
        {
            for (Server server:serverList.subList(0, dcmCommanderClientRMI.getServersInLicense())) {serverListLimited.add(server);}
        }
        else
        {
            serverListLimited = serverList;
        }        

        if (searchString.length() > 0) { output = dcmCommanderClientRMI.searchNonExactHosts(searchField.getText(),serverListLimited); }
        return output;
    }    
    // End MiddleWare

    private void commandFieldValidation()
    {
        if ((contains(commandField.getText(), "[a-zA-Z0-9]+")) && (! commandField.getText().equals(scriptTip)))
        {
            commandCounter = 1;
        }
        else
        {
            commandCounter = 0;
        }
        if (commandCounter == 0)
        {
            commandField.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 0, 0)));
        }
        else
        {
            commandField.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 255, 0)));
        }
//        commandCounterField.setText(Integer.toString(commandCounter));
        validateForm();
    }

    private void validateForm()
    {
        if ( (commandCounter > 0) && (serversCounter > 0) )
        {
            executeParallelButton.setEnabled(true);
            executeSequentialButton.setEnabled(true);
            
            Thread blinkThread = new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    if (lastCommandCounter != commandCounter)
                    {
                        for (int i=1; i<=4;i++)
                        {
                            executeSequentialButton.setForeground(new Color(255,255,255));
                            executeParallelButton.setForeground(new Color(0,0,0));
                            try { Thread.sleep(100); } catch (InterruptedException ex) { }
                            executeSequentialButton.setForeground(new Color(0,0,0));
                            executeParallelButton.setForeground(new Color(255,255,255));
                            try { Thread.sleep(100); } catch (InterruptedException ex) { }
                        }
                        executeSequentialButton.setForeground(new Color(0,0,0));
                        executeParallelButton.setForeground(new Color(0,0,0));
                    }
                    lastCommandCounter = commandCounter;
                }
            });
            blinkThread.setName("blinkThread");
            blinkThread.setDaemon(false);
            blinkThread.setPriority(Thread.NORM_PRIORITY);
            blinkThread.start();
        }
        else
        {
            executeParallelButton.setEnabled(false);
            executeSequentialButton.setEnabled(false);
            lastCommandCounter = commandCounter;
        }
    }
    
    // Begin FronEnd
    private void updateSearchStatsTable(String hostIds, String serversParam,String ossParam)
    {
        searchStatsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Tot: " + hostIds, "Servers: " + serversParam, "OS's: " + ossParam
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Long.class, java.lang.Long.class, java.lang.Long.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
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
        searchStatsTable.getTableHeader().setFont(new java.awt.Font("STHeiti", 0, 10));
        searchStatsTable.getTableHeader().setBackground(new java.awt.Color(204, 204, 204));
        searchStatsTable.getTableHeader().setForeground(new java.awt.Color(102, 102, 102));
        searchStatsScroller.setViewportView(searchStatsTable);
    }
    
    private void execute()
    {
        Thread executeThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                outputArea.setText("");
                sessionTimeout = Integer.parseInt(timeoutField.getText());

// Build the hostList<Host> Object from the serversList                

                hostList = new ArrayList<Host>();
                hostList = getHostList(serversList.getText()); for (final Host host : hostList) { log("host: " + host.getHostname(), true, true, true); }
                progressBar.setMaximum(hostList.size());

// Here all arrays have been build ===============================================
                
                command = new StringBuffer();
                command.append(commandField.getText());
                serverInstanceCounter = 0;

// Start Loop
                for (final Host host : hostList)
                {
                    progressBar.setValue(serverInstanceCounter);
                    if (threaded)
                    {
                        Thread parallelExecutionThread = new Thread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                String userPrivIndicator = ""; if (host.getSuperuserPassword().length() == 0) { userPrivIndicator = "[U]"; } else { userPrivIndicator = "[S]"; }
                                log("============================= Host " + host.getHostname() + " " + userPrivIndicator + "============================", true, true, true);
                                String output = ""; try { output = dcmCommanderClientRMI.remoteCommand(CMDSTAGE, host, command, true, sessionTimeout, debug); } catch (CloneNotSupportedException ex) { } // CMDStage 2 = Commander
                                outputArea.append(output);
                            }
                        });
                        parallelExecutionThread.setName("parallelExecutionThread");
                        parallelExecutionThread.setDaemon(false);
                        parallelExecutionThread.setPriority(Thread.NORM_PRIORITY);
                        parallelExecutionThread.start();
                    }
                    else
                    {
                        String userPrivIndicator = ""; if (host.getSuperuserPassword().length() == 0) { userPrivIndicator = "[U]"; } else { userPrivIndicator = "[S]"; }
                        log("============================= Host " + host.getHostname() + " " + userPrivIndicator + "============================", true, true, true);
                        String output = ""; try { output = dcmCommanderClientRMI.remoteCommand(CMDSTAGE, host, command, true, sessionTimeout, debug); } catch (CloneNotSupportedException ex) { } // CMDStage 2 = Commander
                        outputArea.append(output);
                    }

                    serverInstanceCounter++;
                    progressBar.setValue(serverInstanceCounter);
                    outputScroller.getVerticalScrollBar().setValue(outputScroller.getVerticalScrollBar().getMaximum());
                    try { Thread.sleep(Integer.parseInt(spawnSpeed.getText())); } catch (InterruptedException ex) { }
                }
            }
        });
        executeThread.setName("executeThread");
        executeThread.setDaemon(false);
        executeThread.setPriority(Thread.NORM_PRIORITY);
        executeThread.start();
    }
    
//  Begin Middleware
    public ArrayList<Host> getHostList(String hostLines)
    {
        ArrayList<Host> hostList = null; try { hostList = dcmCommanderClientRMI.getHostList(hostLines); } catch (CloneNotSupportedException ex) { }
        return hostList;
    }
//  End Middleware
    
    private boolean contains( String wholeString, String regexParam ) // Better Use java.lang.String.indexOf method
    {
        boolean output = false;

        Pattern pattern = Pattern.compile(regexParam);
        Matcher matcher = pattern.matcher(wholeString);
//        if (matcher.matches()) { output = true; }
        if (matcher.find()) { output = true; }
        return output;
    }

    private String cleanString(String stringParam)
    {
        return stringParam.replaceAll("(\\r|\\n)", "");
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JEditorPane commandField;
    private javax.swing.JPanel commandPanel;
    private javax.swing.JLabel copyrightLabel;
    private javax.swing.JButton executeParallelButton;
    private javax.swing.JButton executeSequentialButton;
    private javax.swing.JTextArea outputArea;
    private javax.swing.JPanel outputPanel;
    private javax.swing.JScrollPane outputScroller;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JButton searchExactButton;
    private javax.swing.JTextField searchField;
    private javax.swing.JButton searchNonExactButton;
    private javax.swing.JPanel searchServerPanel;
    private javax.swing.JScrollPane searchStatsScroller;
    private javax.swing.JTable searchStatsTable;
    private javax.swing.JScrollPane selectionScroller;
    private javax.swing.JTextArea serversList;
    private javax.swing.JTextField spawnSpeed;
    private javax.swing.JSplitPane splitter;
    private javax.swing.JTextField timeoutField;
    private javax.swing.JScrollPane topScroller;
    // End of variables declaration//GEN-END:variables

    public synchronized void log(final int serverInstance, final String messageParam)
    {
        Thread feedbackThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                outputArea.append(messageParam + "\n");
                outputScroller.getVerticalScrollBar().setValue(outputScroller.getVerticalScrollBar().getMaximum());
            }
        });
        feedbackThread.setName("feedbackThread");
        feedbackThread.setDaemon(false);
        feedbackThread.setPriority(Thread.NORM_PRIORITY);
        feedbackThread.start();
    }    
    
    public void log(final String messageParam, final boolean logToStatusParam, final boolean logToApplicationParam, final boolean logToFileParam)
    {
        Thread logThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                if ((messageParam != null) && (outputArea != null))
                {
                    outputArea.append(messageParam + "\n");
                    outputScroller.getVerticalScrollBar().setValue(outputScroller.getVerticalScrollBar().getMaximum());                    
                }
            }
        });
        logThread.setName("logThread");
        logThread.setDaemon(false);
        logThread.setPriority(Thread.NORM_PRIORITY);
        logThread.start();
    }

    public void dbClientReady() { dcmDBClientIsReady = true; }

    public void updatedHost() {  }

    public synchronized void remoteFinalCommandSuccessResponse(final int stageParam, final String stdOutParam, final String stdErrParam)
    {
        Thread remoteFinalCommandSuccessResponseThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                outputArea.append(stdOutParam + "\n");
                outputScroller.getVerticalScrollBar().setValue(outputScroller.getVerticalScrollBar().getMaximum());
            }
        });
        remoteFinalCommandSuccessResponseThread.setName("remoteFinalCommandSuccessResponseThread");
        remoteFinalCommandSuccessResponseThread.setDaemon(false);
        remoteFinalCommandSuccessResponseThread.setPriority(Thread.NORM_PRIORITY);
        remoteFinalCommandSuccessResponseThread.start();
    }

    public synchronized void remoteCommandSuccessResponse(final int stageParam, final String stdOutParam, final String stdErrParam)
    {
        Thread remoteCommandSuccessResponseThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                outputArea.append(stdOutParam + "\n");
                outputScroller.getVerticalScrollBar().setValue(outputScroller.getVerticalScrollBar().getMaximum());
            }
        });
        remoteCommandSuccessResponseThread.setName("remoteCommandSuccessResponseThread");
        remoteCommandSuccessResponseThread.setDaemon(false);
        remoteCommandSuccessResponseThread.setPriority(Thread.NORM_PRIORITY);
        remoteCommandSuccessResponseThread.start();
    }

    public synchronized void remoteCommandFailureResponse(final int stageParam, final String messageParam)
    {
        Thread remoteCommandFailureResponseThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                outputArea.append(messageParam + "\n");
                outputScroller.getVerticalScrollBar().setValue(outputScroller.getVerticalScrollBar().getMaximum());
            }
        });
        remoteCommandFailureResponseThread.setName("remoteCommandFailureResponseThread");
        remoteCommandFailureResponseThread.setDaemon(false);
        remoteCommandFailureResponseThread.setPriority(Thread.NORM_PRIORITY);
        remoteCommandFailureResponseThread.start();
    }

    public void inventoryReady(Server serverParam) {  }

//    public static void main(final String args[])
//    {
//        java.awt.EventQueue.invokeLater(new Runnable()
//        {
//
//            public void run()
//            {
//                boolean debug = false;
//                
//                for (int i=0; i < args.length; i++)
//                {
//                    if(args[i].equals("--debug"))         { debug = true; }
//                    else if(args[i].equals("--help"))     { usage(); System.exit(1); }
//                    else                                  { usage(); System.exit(1); }
//                }
//                try { new DCMCommanderClient(debug).setVisible(true); } catch (Exception ex) { System.out.println("Error: main(): "); }
//            }
//        });
//    }
//
    private static void usage()
    {
        System.out.println("\n");
        System.out.println("Usage:   java -cp DCManager.jar DCMCommander [--debug]\n");
        System.out.println(DCMLicense.getCopyright());
    }
}
