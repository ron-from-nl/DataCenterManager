
import data.Host;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.*;
import java.util.ArrayList;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.DefaultTableModel;

public class DCMAddServerForm extends javax.swing.JFrame
{
    private Host host;
    private DCMDesktop dcmDesktop;
    private DCMFileBrowser dcmFileBrowser;
    private ArrayList<Host> hostList;
    private String importFile;
    private String[] fieldArray;
    protected ListSelectionModel selectionModel;
    protected boolean importEnabled;
    protected int timeout1;
    protected int retryMax1;
    protected int timeout2;
    protected int retryMax2;

    public DCMAddServerForm(DCMDesktop dcmDesktopParam) throws UnsupportedLookAndFeelException
    {
        importEnabled = true;
        hostList = new ArrayList<Host>();
        dcmDesktop = dcmDesktopParam;
        host = new Host();

        timeout1 = 6;
        retryMax1 = 1;
        timeout2 = 6;
        retryMax2 = 1;
        
        initComponents();
        
        timeout1 = Integer.parseInt(timeout1Field.getText());
        retryMax1 = Integer.parseInt(retry1Field.getText());
        timeout2 = Integer.parseInt(timeout2Field.getText());
        retryMax2 = Integer.parseInt(retry2Field.getText());

        selectionModel = hostImportTable.getSelectionModel();
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
    }

    private DCMAddServerForm() {
        importEnabled = true;
        hostList = new ArrayList<Host>();
        host = new Host();
        
        timeout1 = 6;
        retryMax1 = 1;
        timeout2 = 6;
        retryMax2 = 1;
        
        initComponents();
        
        timeout1 = Integer.parseInt(timeout1Field.getText());
        retryMax1 = Integer.parseInt(retry1Field.getText());
        timeout2 = Integer.parseInt(timeout2Field.getText());
        retryMax2 = Integer.parseInt(retry2Field.getText());

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
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        tabPane = new javax.swing.JTabbedPane();
        serverPanel = new javax.swing.JPanel();
        hostnameField = new javax.swing.JTextField();
        sshportField = new javax.swing.JTextField();
        usernameField = new javax.swing.JTextField();
        userPasswordField = new javax.swing.JPasswordField();
        superuserPasswordField = new javax.swing.JPasswordField();
        rootpwlabel = new javax.swing.JLabel();
        userPasswordLabel = new javax.swing.JLabel();
        portLabel = new javax.swing.JLabel();
        hostLabel = new javax.swing.JLabel();
        userLabel = new javax.swing.JLabel();
        cancelAddServerButton = new javax.swing.JButton();
        addButton = new javax.swing.JButton();
        timeoutMinus1Button = new javax.swing.JButton();
        timeout1Field = new javax.swing.JTextField();
        timeoutPlus1Button = new javax.swing.JButton();
        timeout1Label = new javax.swing.JLabel();
        retryMinus1Button = new javax.swing.JButton();
        retry1Field = new javax.swing.JTextField();
        retryPlus1Button = new javax.swing.JButton();
        timeout1Label1 = new javax.swing.JLabel();
        importPanel = new javax.swing.JPanel();
        hostImportScroller = new javax.swing.JScrollPane();
        hostImportTable = new javax.swing.JTable();
        importLabel = new javax.swing.JLabel();
        importFileField = new javax.swing.JTextField();
        fieldSeparatorField = new javax.swing.JTextField();
        browsButton = new javax.swing.JButton();
        cancelImportButton = new javax.swing.JButton();
        importButton = new javax.swing.JButton();
        timeoutMinus2Button = new javax.swing.JButton();
        timeout2Field = new javax.swing.JTextField();
        timeoutPlus2Button = new javax.swing.JButton();
        timeout2Label = new javax.swing.JLabel();
        retry2Label1 = new javax.swing.JLabel();
        retryPlus2Button = new javax.swing.JButton();
        retry2Field = new javax.swing.JTextField();
        retryMinus2Button = new javax.swing.JButton();
        statusLabel = new javax.swing.JLabel();

        setAlwaysOnTop(true);
        setBackground(new java.awt.Color(102, 102, 102));
        setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        setMinimumSize(new java.awt.Dimension(802, 400));

        mainPanel.setBackground(new java.awt.Color(102, 102, 102));
        mainPanel.setFont(mainPanel.getFont());

        tabPane.setBackground(new java.awt.Color(102, 102, 102));
        tabPane.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        tabPane.setFocusable(false);
        tabPane.setFont(tabPane.getFont().deriveFont(tabPane.getFont().getSize()+1f));

        serverPanel.setBackground(new java.awt.Color(102, 102, 102));
        serverPanel.setFocusCycleRoot(true);
        serverPanel.setFont(serverPanel.getFont());
        serverPanel.setOpaque(false);

        hostnameField.setFont(hostnameField.getFont().deriveFont(hostnameField.getFont().getSize()+1f));
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("DCMAddServerForm"); // NOI18N
        hostnameField.setToolTipText(bundle.getString("DCMAddServerForm.hostnameField.toolTipText")); // NOI18N
        hostnameField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                hostnameFieldKeyReleased(evt);
            }
        });

        sshportField.setFont(sshportField.getFont().deriveFont(sshportField.getFont().getSize()+1f));
        sshportField.setText(bundle.getString("DCMAddServerForm.sshportField.text")); // NOI18N
        sshportField.setToolTipText(bundle.getString("DCMAddServerForm.sshportField.toolTipText")); // NOI18N
        sshportField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                sshportFieldKeyReleased(evt);
            }
        });

        usernameField.setFont(usernameField.getFont().deriveFont(usernameField.getFont().getSize()+1f));
        usernameField.setToolTipText(bundle.getString("DCMAddServerForm.usernameField.toolTipText")); // NOI18N
        usernameField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                usernameFieldKeyReleased(evt);
            }
        });

        userPasswordField.setFont(userPasswordField.getFont().deriveFont(userPasswordField.getFont().getSize()+1f));
        userPasswordField.setToolTipText(bundle.getString("DCMAddServerForm.userPasswordField.toolTipText")); // NOI18N

        superuserPasswordField.setBackground(new java.awt.Color(204, 204, 204));
        superuserPasswordField.setFont(superuserPasswordField.getFont().deriveFont(superuserPasswordField.getFont().getSize()+1f));
        superuserPasswordField.setToolTipText(bundle.getString("DCMAddServerForm.superuserPasswordField.toolTipText")); // NOI18N

        rootpwlabel.setFont(rootpwlabel.getFont().deriveFont(rootpwlabel.getFont().getSize()+1f));
        rootpwlabel.setForeground(new java.awt.Color(255, 255, 255));
        rootpwlabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        rootpwlabel.setText(bundle.getString("DCMAddServerForm.rootpwlabel.text")); // NOI18N

        userPasswordLabel.setFont(userPasswordLabel.getFont().deriveFont(userPasswordLabel.getFont().getSize()+1f));
        userPasswordLabel.setForeground(new java.awt.Color(255, 255, 255));
        userPasswordLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        userPasswordLabel.setText(bundle.getString("DCMAddServerForm.userPasswordLabel.text")); // NOI18N

        portLabel.setFont(portLabel.getFont().deriveFont(portLabel.getFont().getSize()+1f));
        portLabel.setForeground(new java.awt.Color(255, 255, 255));
        portLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        portLabel.setText(bundle.getString("DCMAddServerForm.portLabel.text")); // NOI18N

        hostLabel.setFont(hostLabel.getFont().deriveFont(hostLabel.getFont().getSize()+1f));
        hostLabel.setForeground(new java.awt.Color(255, 255, 255));
        hostLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        hostLabel.setText(bundle.getString("DCMAddServerForm.hostLabel.text")); // NOI18N

        userLabel.setFont(userLabel.getFont().deriveFont(userLabel.getFont().getSize()+1f));
        userLabel.setForeground(new java.awt.Color(255, 255, 255));
        userLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        userLabel.setText(bundle.getString("DCMAddServerForm.userLabel.text")); // NOI18N

        cancelAddServerButton.setFont(cancelAddServerButton.getFont().deriveFont(cancelAddServerButton.getFont().getSize()+1f));
        cancelAddServerButton.setText(bundle.getString("DCMAddServerForm.cancelAddServerButton.text")); // NOI18N
        cancelAddServerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelAddServerButtonActionPerformed(evt);
            }
        });

        addButton.setFont(addButton.getFont().deriveFont(addButton.getFont().getSize()+1f));
        addButton.setText(bundle.getString("DCMAddServerForm.addButton.text")); // NOI18N
        addButton.setToolTipText(bundle.getString("DCMAddServerForm.addButton.toolTipText")); // NOI18N
        addButton.setEnabled(false);
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        timeoutMinus1Button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/minus.png"))); // NOI18N
        timeoutMinus1Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                timeoutMinus1ButtonActionPerformed(evt);
            }
        });

        timeout1Field.setEditable(false);
        timeout1Field.setFont(timeout1Field.getFont().deriveFont(timeout1Field.getFont().getSize()-1f));
        timeout1Field.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        timeout1Field.setText(bundle.getString("DCMAddServerForm.timeout1Field.text")); // NOI18N
        timeout1Field.setToolTipText(bundle.getString("DCMAddServerForm.timeout1Field.toolTipText")); // NOI18N
        timeout1Field.setEnabled(false);

        timeoutPlus1Button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/plus.png"))); // NOI18N
        timeoutPlus1Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                timeoutPlus1ButtonActionPerformed(evt);
            }
        });

        timeout1Label.setFont(timeout1Label.getFont().deriveFont(timeout1Label.getFont().getSize()-1f));
        timeout1Label.setForeground(new java.awt.Color(255, 255, 255));
        timeout1Label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        timeout1Label.setText(bundle.getString("DCMAddServerForm.timeout1Label.text")); // NOI18N
        timeout1Label.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        retryMinus1Button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/minus.png"))); // NOI18N
        retryMinus1Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                retryMinus1ButtonActionPerformed(evt);
            }
        });

        retry1Field.setEditable(false);
        retry1Field.setFont(retry1Field.getFont().deriveFont(retry1Field.getFont().getSize()-1f));
        retry1Field.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        retry1Field.setText(bundle.getString("DCMAddServerForm.retry1Field.text")); // NOI18N
        retry1Field.setToolTipText(bundle.getString("DCMAddServerForm.retry1Field.toolTipText")); // NOI18N
        retry1Field.setEnabled(false);

        retryPlus1Button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/plus.png"))); // NOI18N
        retryPlus1Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                retryPlus1ButtonActionPerformed(evt);
            }
        });

        timeout1Label1.setFont(timeout1Label1.getFont().deriveFont(timeout1Label1.getFont().getSize()-1f));
        timeout1Label1.setForeground(new java.awt.Color(255, 255, 255));
        timeout1Label1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        timeout1Label1.setText(bundle.getString("DCMAddServerForm.timeout1Label1.text")); // NOI18N
        timeout1Label1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        org.jdesktop.layout.GroupLayout serverPanelLayout = new org.jdesktop.layout.GroupLayout(serverPanel);
        serverPanel.setLayout(serverPanelLayout);
        serverPanelLayout.setHorizontalGroup(
            serverPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(serverPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(serverPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(userPasswordLabel)
                    .add(userLabel)
                    .add(rootpwlabel)
                    .add(portLabel)
                    .add(hostLabel)
                    .add(cancelAddServerButton))
                .add(68, 68, 68)
                .add(serverPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(superuserPasswordField)
                    .add(userPasswordField)
                    .add(usernameField)
                    .add(sshportField)
                    .add(hostnameField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 519, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, serverPanelLayout.createSequentialGroup()
                        .add(addButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 259, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(serverPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, timeout1Label1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, serverPanelLayout.createSequentialGroup()
                                .add(retryMinus1Button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 39, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(retry1Field, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 34, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(retryPlus1Button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 39, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(serverPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, timeout1Label, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, serverPanelLayout.createSequentialGroup()
                                .add(timeoutMinus1Button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 39, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(timeout1Field, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 34, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(timeoutPlus1Button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 39, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))))
                .add(55, 55, 55))
        );

        serverPanelLayout.linkSize(new java.awt.Component[] {retryMinus1Button, retryPlus1Button, timeoutMinus1Button, timeoutPlus1Button}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        serverPanelLayout.linkSize(new java.awt.Component[] {retry1Field, timeout1Field}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        serverPanelLayout.setVerticalGroup(
            serverPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, serverPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(serverPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(hostnameField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(hostLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(serverPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(sshportField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(portLabel))
                .add(12, 12, 12)
                .add(serverPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(usernameField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(userLabel))
                .add(14, 14, 14)
                .add(serverPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(userPasswordField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(userPasswordLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 13, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(serverPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(superuserPasswordField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(rootpwlabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 26, Short.MAX_VALUE)
                .add(serverPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(timeout1Label)
                    .add(timeout1Label1))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(serverPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, serverPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(serverPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(addButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 37, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(cancelAddServerButton))
                        .add(timeoutMinus1Button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(timeout1Field, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(timeoutPlus1Button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, serverPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(retryMinus1Button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(retry1Field, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(retryPlus1Button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        serverPanelLayout.linkSize(new java.awt.Component[] {addButton, cancelAddServerButton, retry1Field, retryMinus1Button, retryPlus1Button, timeout1Field, timeoutMinus1Button, timeoutPlus1Button}, org.jdesktop.layout.GroupLayout.VERTICAL);

        tabPane.addTab(bundle.getString("DCMAddServerForm.serverPanel.TabConstraints.tabTitle"), serverPanel); // NOI18N

        importPanel.setBackground(new java.awt.Color(102, 102, 102));
        importPanel.setFont(importPanel.getFont());
        importPanel.setOpaque(false);

        hostImportScroller.setFont(hostImportScroller.getFont());

        hostImportTable.setFont(new java.awt.Font("STHeiti", 0, 10)); // NOI18N
        hostImportTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Line", "Hostname", "SSH Port", "Username", "Password", "Superuser Password", "Status"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Long.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        hostImportTable.setDoubleBuffered(true);
        hostImportTable.setGridColor(javax.swing.UIManager.getDefaults().getColor("Button.background"));
        hostImportTable.setShowGrid(true);
        hostImportScroller.setViewportView(hostImportTable);

        importLabel.setFont(importLabel.getFont().deriveFont(importLabel.getFont().getSize()-1f));
        importLabel.setForeground(new java.awt.Color(255, 255, 255));
        importLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        importLabel.setText(bundle.getString("DCMAddServerForm.importLabel.text")); // NOI18N

        importFileField.setEditable(false);
        importFileField.setFont(importFileField.getFont().deriveFont(importFileField.getFont().getSize()-3f));
        importFileField.setToolTipText(bundle.getString("DCMAddServerForm.importFileField.toolTipText")); // NOI18N
        importFileField.setEnabled(false);
        importFileField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                importFileFieldKeyReleased(evt);
            }
        });

        fieldSeparatorField.setFont(fieldSeparatorField.getFont().deriveFont(fieldSeparatorField.getFont().getSize()-3f));
        fieldSeparatorField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        fieldSeparatorField.setText(bundle.getString("DCMAddServerForm.fieldSeparatorField.text")); // NOI18N
        fieldSeparatorField.setToolTipText(bundle.getString("DCMAddServerForm.fieldSeparatorField.toolTipText")); // NOI18N
        fieldSeparatorField.setEnabled(false);
        fieldSeparatorField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fieldSeparatorFieldKeyReleased(evt);
            }
        });

        browsButton.setFont(browsButton.getFont().deriveFont(browsButton.getFont().getSize()-3f));
        browsButton.setText(bundle.getString("DCMAddServerForm.browsButton.text")); // NOI18N
        browsButton.setToolTipText(bundle.getString("DCMAddServerForm.browsButton.toolTipText")); // NOI18N
        browsButton.setFocusable(false);
        browsButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        browsButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        browsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browsButtonActionPerformed(evt);
            }
        });

        cancelImportButton.setFont(cancelImportButton.getFont().deriveFont(cancelImportButton.getFont().getSize()+1f));
        cancelImportButton.setText(bundle.getString("DCMAddServerForm.cancelImportButton.text")); // NOI18N
        cancelImportButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelImportButtonActionPerformed(evt);
            }
        });

        importButton.setFont(importButton.getFont().deriveFont(importButton.getFont().getSize()+1f));
        importButton.setText(bundle.getString("DCMAddServerForm.importButton.text")); // NOI18N
        importButton.setToolTipText(bundle.getString("DCMAddServerForm.importButton.toolTipText")); // NOI18N
        importButton.setActionCommand(bundle.getString("DCMAddServerForm.importButton.actionCommand")); // NOI18N
        importButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importButtonActionPerformed(evt);
            }
        });

        timeoutMinus2Button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/minus.png"))); // NOI18N
        timeoutMinus2Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                timeoutMinus2ButtonActionPerformed(evt);
            }
        });

        timeout2Field.setEditable(false);
        timeout2Field.setFont(timeout2Field.getFont().deriveFont(timeout2Field.getFont().getSize()-1f));
        timeout2Field.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        timeout2Field.setText(bundle.getString("DCMAddServerForm.timeout2Field.text")); // NOI18N
        timeout2Field.setToolTipText(bundle.getString("DCMAddServerForm.timeout2Field.toolTipText")); // NOI18N
        timeout2Field.setEnabled(false);

        timeoutPlus2Button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/plus.png"))); // NOI18N
        timeoutPlus2Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                timeoutPlus2ButtonActionPerformed(evt);
            }
        });

        timeout2Label.setFont(timeout2Label.getFont().deriveFont(timeout2Label.getFont().getSize()-1f));
        timeout2Label.setForeground(new java.awt.Color(255, 255, 255));
        timeout2Label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        timeout2Label.setText(bundle.getString("DCMAddServerForm.timeout2Label.text")); // NOI18N
        timeout2Label.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(51, 51, 51), 1, true));

        retry2Label1.setFont(retry2Label1.getFont().deriveFont(retry2Label1.getFont().getSize()-1f));
        retry2Label1.setForeground(new java.awt.Color(255, 255, 255));
        retry2Label1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        retry2Label1.setText(bundle.getString("DCMAddServerForm.retry2Label1.text")); // NOI18N
        retry2Label1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(51, 51, 51), 1, true));

        retryPlus2Button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/plus.png"))); // NOI18N
        retryPlus2Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                retryPlus2ButtonActionPerformed(evt);
            }
        });

        retry2Field.setEditable(false);
        retry2Field.setFont(retry2Field.getFont().deriveFont(retry2Field.getFont().getSize()-1f));
        retry2Field.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        retry2Field.setText(bundle.getString("DCMAddServerForm.retry2Field.text")); // NOI18N
        retry2Field.setToolTipText(bundle.getString("DCMAddServerForm.retry2Field.toolTipText")); // NOI18N
        retry2Field.setEnabled(false);

        retryMinus2Button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/minus.png"))); // NOI18N
        retryMinus2Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                retryMinus2ButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout importPanelLayout = new org.jdesktop.layout.GroupLayout(importPanel);
        importPanel.setLayout(importPanelLayout);
        importPanelLayout.setHorizontalGroup(
            importPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(importPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(importPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(importLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(cancelImportButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 121, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(importPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(importPanelLayout.createSequentialGroup()
                        .add(importFileField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 223, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(fieldSeparatorField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(browsButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 104, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(importButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 367, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(importPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, retry2Label1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 124, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, importPanelLayout.createSequentialGroup()
                        .add(retryMinus2Button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 39, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(6, 6, 6)
                        .add(retry2Field, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 34, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(6, 6, 6)
                        .add(retryPlus2Button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 39, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(importPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, timeout2Label, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 124, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, importPanelLayout.createSequentialGroup()
                        .add(timeoutMinus2Button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 39, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(6, 6, 6)
                        .add(timeout2Field, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 34, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(6, 6, 6)
                        .add(timeoutPlus2Button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 39, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .add(6, 6, 6))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, hostImportScroller)
        );
        importPanelLayout.setVerticalGroup(
            importPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(importPanelLayout.createSequentialGroup()
                .add(hostImportScroller, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 224, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(importPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(importLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(timeout2Label, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(importPanelLayout.createSequentialGroup()
                        .add(3, 3, 3)
                        .add(importPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(importPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                .add(importFileField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(fieldSeparatorField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(browsButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(retry2Label1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(importPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(cancelImportButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(timeoutMinus2Button, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(timeoutPlus2Button, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(timeout2Field)
                    .add(retryMinus2Button, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, retryPlus2Button, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(retry2Field)
                    .add(importButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        importPanelLayout.linkSize(new java.awt.Component[] {cancelImportButton, retry2Field, retryMinus2Button, retryPlus2Button, timeout2Field, timeoutMinus2Button, timeoutPlus2Button}, org.jdesktop.layout.GroupLayout.VERTICAL);

        tabPane.addTab(bundle.getString("DCMAddServerForm.importPanel.TabConstraints.tabTitle"), importPanel); // NOI18N

        statusLabel.setBackground(new java.awt.Color(153, 153, 153));
        statusLabel.setFont(statusLabel.getFont());
        statusLabel.setForeground(new java.awt.Color(51, 51, 51));
        statusLabel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        statusLabel.setOpaque(true);

        org.jdesktop.layout.GroupLayout mainPanelLayout = new org.jdesktop.layout.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, statusLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(tabPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 787, Short.MAX_VALUE)
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(mainPanelLayout.createSequentialGroup()
                .add(tabPane)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(statusLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(mainPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(mainPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void importButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importButtonActionPerformed
        importButton.setEnabled(false);
        ListSelectionModel selectionModel = hostImportTable.getSelectionModel(); // Is this necesary?
//        selectionModel.setSelectionInterval(1, 2);
        dcmDesktop.addServersByForm(hostList);
}//GEN-LAST:event_importButtonActionPerformed

    private void cancelImportButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelImportButtonActionPerformed
        importEnabled = false;
        statusLabel.setText("Import Canceled by User...");
        host = new Host();
        dcmDesktop.addServerCanceled();
}//GEN-LAST:event_cancelImportButtonActionPerformed

    protected void setRowSelection(int startParam, int endParam) { selectionModel.setSelectionInterval(startParam, endParam);}
    
    private void browsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browsButtonActionPerformed
        dcmFileBrowser = new DCMFileBrowser(this);
        dcmFileBrowser.setVisible(true);
    }//GEN-LAST:event_browsButtonActionPerformed

    private void fieldSeparatorFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldSeparatorFieldKeyReleased
        if (
                (evt.getKeyCode() != 127) &&
                (evt.getKeyCode() != 8) && 
                (evt.getKeyCode() != 37) && 
                (evt.getKeyCode() != 40) && 
                (evt.getKeyCode() != 39) && 
                (evt.getKeyCode() != 38) &&
                (evt.getKeyCode() != 16) &&
                (fieldSeparatorField.getText().length() == 1)
           )
        {
            setImportFile(importFile);
        }
    }//GEN-LAST:event_fieldSeparatorFieldKeyReleased

    private void importFileFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_importFileFieldKeyReleased
        setImportFile(importFile);
    }//GEN-LAST:event_importFileFieldKeyReleased

    private void cancelAddServerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelAddServerButtonActionPerformed
        setVisible(false);
        hostnameField.setText("");
        sshportField.setText("");
        usernameField.setText("");
        userPasswordField.setText("");
        superuserPasswordField.setText("");
        host = new Host();
        dcmDesktop.addServerCanceled();
    }//GEN-LAST:event_cancelAddServerButtonActionPerformed

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        setVisible(false);
        host = new Host();
        host.setHostname(hostnameField.getText());
        host.setPort(Integer.parseInt(sshportField.getText()));
        host.setUsername(usernameField.getText());
        host.setUserPassword(new String(userPasswordField.getPassword()));
        host.setSuperuserPassword(new String(superuserPasswordField.getPassword()));
        dcmDesktop.addServerByForm();
    }//GEN-LAST:event_addButtonActionPerformed

    private void timeoutMinus2ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_timeoutMinus2ButtonActionPerformed
        if (Integer.parseInt(timeout2Field.getText()) > 1  )
        {
            timeout2 = Integer.parseInt(timeout2Field.getText()); timeout2--; timeout2Field.setText(Integer.toString(timeout2));
        }
    }//GEN-LAST:event_timeoutMinus2ButtonActionPerformed

    private void timeoutPlus2ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_timeoutPlus2ButtonActionPerformed
        if (Integer.parseInt(timeout2Field.getText()) < 30  )
        {
            timeout2 = Integer.parseInt(timeout2Field.getText()); timeout2++; timeout2Field.setText(Integer.toString(timeout2));
        }
    }//GEN-LAST:event_timeoutPlus2ButtonActionPerformed

    private void timeoutMinus1ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_timeoutMinus1ButtonActionPerformed
        if (Integer.parseInt(timeout1Field.getText()) > 1  )
        {
            timeout1 = Integer.parseInt(timeout1Field.getText()); timeout1--; timeout1Field.setText(Integer.toString(timeout1));
        }
    }//GEN-LAST:event_timeoutMinus1ButtonActionPerformed

    private void timeoutPlus1ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_timeoutPlus1ButtonActionPerformed
        if (Integer.parseInt(timeout1Field.getText()) < 30  )
        {
            timeout1 = Integer.parseInt(timeout1Field.getText()); timeout1++; timeout1Field.setText(Integer.toString(timeout1));
        }
    }//GEN-LAST:event_timeoutPlus1ButtonActionPerformed

    private void retryMinus1ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_retryMinus1ButtonActionPerformed
        if (Integer.parseInt(retry1Field.getText()) > 0  )
        {
            retryMax1 = Integer.parseInt(retry1Field.getText()); retryMax1--; retry1Field.setText(Integer.toString(retryMax1));
        }
    }//GEN-LAST:event_retryMinus1ButtonActionPerformed

    private void retryPlus1ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_retryPlus1ButtonActionPerformed
        if (Integer.parseInt(retry1Field.getText()) < 5  )
        {
            retryMax1 = Integer.parseInt(retry1Field.getText()); retryMax1++; retry1Field.setText(Integer.toString(retryMax1));
        }
    }//GEN-LAST:event_retryPlus1ButtonActionPerformed

    private void retryPlus2ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_retryPlus2ButtonActionPerformed
        if (Integer.parseInt(retry2Field.getText()) < 5  )
        {
            retryMax2 = Integer.parseInt(retry2Field.getText()); retryMax2++; retry2Field.setText(Integer.toString(retryMax2));
        }
    }//GEN-LAST:event_retryPlus2ButtonActionPerformed

    private void retryMinus2ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_retryMinus2ButtonActionPerformed
        if (Integer.parseInt(retry2Field.getText()) > 0  )
        {
            retryMax2 = Integer.parseInt(retry2Field.getText()); retryMax2--; retry2Field.setText(Integer.toString(retryMax2));
        }
    }//GEN-LAST:event_retryMinus2ButtonActionPerformed

    private void hostnameFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_hostnameFieldKeyReleased
        validateForm();
    }//GEN-LAST:event_hostnameFieldKeyReleased

    private void sshportFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_sshportFieldKeyReleased
        validateForm();
    }//GEN-LAST:event_sshportFieldKeyReleased

    private void usernameFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_usernameFieldKeyReleased
        validateForm();
    }//GEN-LAST:event_usernameFieldKeyReleased

    private void validateForm()
    {
        if ( (hostnameField.getText().isEmpty()) || (sshportField.getText().isEmpty()) || (usernameField.getText().isEmpty()) )
        {
            addButton.setEnabled(false);
        }
        else
        {
            addButton.setEnabled(true);
        }
    }
    
    public void setImportFile(String importFileParam) // The fileBrowser has openend a file
    {
        importFile = importFileParam;
        if (! importFileField.isEnabled()) { importFileField.setEnabled(true); }
        if (! fieldSeparatorField.isEnabled()) { fieldSeparatorField.setEnabled(true); }
        importFileField.setText(importFileParam);
        dcmFileBrowser.setVisible(false);
        long lineCounter = 1;
        hostList = new ArrayList<Host>();
        File file = new File(importFileParam);
        BufferedReader in = null;
        try { in = new BufferedReader(new FileReader(file)); } catch (FileNotFoundException ex) { }
        String line = "";
        hostImportTable.setFont(new java.awt.Font("STHeiti", 0, 10)); // NOI18N
        hostImportTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Line", "Hostname", "SSH Port", "Username", "Password", "Superuser Password", "Status"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Long.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
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
        hostImportTable.setDoubleBuffered(true);
        hostImportTable.setGridColor(javax.swing.UIManager.getDefaults().getColor("Button.background"));
        hostImportTable.setShowGrid(true);
        hostImportScroller.setViewportView(hostImportTable);

        boolean error = false;
        String errorMessage = "";
        DefaultTableModel model = new DefaultTableModel();
        model = (DefaultTableModel) hostImportTable.getModel();
        try
        {
            while ((line = in.readLine()) != null)
            {
                line = line.replace(" ", "");
                Host host = new Host();
                fieldArray = new String[5]; fieldArray = line.split(fieldSeparatorField.getText(),5);
                error = false; errorMessage = "";
                if (fieldArray[0].length() == 0)                                            { error = true; errorMessage="Error: Hostname field is empty"; }
                try { Integer.parseInt(fieldArray[1]); } catch (NumberFormatException ex)   { error = true; errorMessage="Error: " + ex.getMessage(); fieldArray[1] = "0"; }
                if (fieldArray[2].length() == 0)                                            { error = true; errorMessage="Error: Username field is empty"; }
                if (fieldArray[3].length() == 0)                                            { error = true; errorMessage="Error: UserPassword field is empty"; }
                
                host.setId(lineCounter);host.setHostname(fieldArray[0]); host.setPort(Integer.parseInt(fieldArray[1]));
                host.setUsername(fieldArray[2]); host.setUserPassword(fieldArray[3]);host.setSuperuserPassword(fieldArray[4]);
                if (error )
                {
                    host.setEnabled(false);
                    hostList.add(host);
                    model.addRow(new Object[] {lineCounter, fieldArray[0], "??", fieldArray[2], fieldArray[3].replaceAll(".", "*"), fieldArray[4].replaceAll(".", "*"), errorMessage});
                    statusLabel.setText(errorMessage);
                }
                else
                {
                    host.setEnabled(true);
                    hostList.add(host);
                    model.addRow(new Object[] {lineCounter, fieldArray[0], fieldArray[1], fieldArray[2], fieldArray[3].replaceAll(".", "*"), fieldArray[4].replaceAll(".", "*")});
                }
                lineCounter++;
            }
        }
        catch (IOException ex) {  }
        try { in.close(); } catch (IOException ex) {  }
    }

    public Host getHost() throws CloneNotSupportedException
    {
        return (Host) host.clone();
    }
    
    public void close()
    {
        close();
    }
    
    protected int getTimeout1() { return timeout1; }
    protected int getRetryMax1() { return retryMax1; }
    protected int getTimeout2() { return timeout2; }
    protected int getRetryMax2() { return retryMax2; }

    protected ArrayList<Host> getImportHostList() { return hostList; }
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new DCMAddServerForm().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JButton browsButton;
    private javax.swing.JButton cancelAddServerButton;
    private javax.swing.JButton cancelImportButton;
    private javax.swing.JTextField fieldSeparatorField;
    private javax.swing.JScrollPane hostImportScroller;
    protected javax.swing.JTable hostImportTable;
    private javax.swing.JLabel hostLabel;
    protected javax.swing.JTextField hostnameField;
    private javax.swing.JButton importButton;
    private javax.swing.JTextField importFileField;
    private javax.swing.JLabel importLabel;
    private javax.swing.JPanel importPanel;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JLabel portLabel;
    protected javax.swing.JTextField retry1Field;
    protected javax.swing.JTextField retry2Field;
    private javax.swing.JLabel retry2Label1;
    private javax.swing.JButton retryMinus1Button;
    private javax.swing.JButton retryMinus2Button;
    private javax.swing.JButton retryPlus1Button;
    private javax.swing.JButton retryPlus2Button;
    private javax.swing.JLabel rootpwlabel;
    private javax.swing.JPanel serverPanel;
    protected javax.swing.JTextField sshportField;
    protected javax.swing.JLabel statusLabel;
    protected javax.swing.JPasswordField superuserPasswordField;
    private javax.swing.JTabbedPane tabPane;
    protected javax.swing.JTextField timeout1Field;
    private javax.swing.JLabel timeout1Label;
    private javax.swing.JLabel timeout1Label1;
    protected javax.swing.JTextField timeout2Field;
    private javax.swing.JLabel timeout2Label;
    private javax.swing.JButton timeoutMinus1Button;
    private javax.swing.JButton timeoutMinus2Button;
    private javax.swing.JButton timeoutPlus1Button;
    private javax.swing.JButton timeoutPlus2Button;
    private javax.swing.JLabel userLabel;
    protected javax.swing.JPasswordField userPasswordField;
    private javax.swing.JLabel userPasswordLabel;
    protected javax.swing.JTextField usernameField;
    // End of variables declaration//GEN-END:variables
}
