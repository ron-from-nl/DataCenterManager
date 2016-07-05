
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
    protected int timeout;
    protected int retryMax;
    protected int retentionTime;

    public DCMAddServerForm(DCMDesktop dcmDesktopParam) throws UnsupportedLookAndFeelException
    {
        importEnabled = true;
        hostList = new ArrayList<Host>();
        dcmDesktop = dcmDesktopParam;
        host = new Host();

	retentionTime=1;
        retryMax = 1;
        timeout = 6;
        
        initComponents();
        
        retentionTime = Integer.parseInt(retentionTimeField.getText());
        timeout = Integer.parseInt(timeoutField.getText());
        retryMax = Integer.parseInt(retryField.getText());
//        timeout2 = Integer.parseInt(timeout2Field.getText());
//        retryMax2 = Integer.parseInt(retry2Field.getText());

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
        
        timeout = 6;
        retryMax = 1;

        initComponents();
        
        retentionTime = Integer.parseInt(retentionTimeField.getText());
        timeout = Integer.parseInt(timeoutField.getText());
        retryMax = Integer.parseInt(retryField.getText());

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
    private void initComponents()
    {

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
        importPanel = new javax.swing.JPanel();
        hostImportScroller = new javax.swing.JScrollPane();
        hostImportTable = new javax.swing.JTable();
        importLabel = new javax.swing.JLabel();
        importFileField = new javax.swing.JTextField();
        fieldSeparatorField = new javax.swing.JTextField();
        browsButton = new javax.swing.JButton();
        statusLabel = new javax.swing.JLabel();
        buttonPanel = new javax.swing.JPanel();
        cancelAddServerButton = new javax.swing.JButton();
        addButton = new javax.swing.JButton();
        retentionMinusButton = new javax.swing.JButton();
        retentionTimeField = new javax.swing.JTextField();
        retensionLabel = new javax.swing.JLabel();
        retentionPlusButton = new javax.swing.JButton();
        retryMinus1Button = new javax.swing.JButton();
        timeout1Label1 = new javax.swing.JLabel();
        retryField = new javax.swing.JTextField();
        retryPlus1Button = new javax.swing.JButton();
        timeoutMinus1Button = new javax.swing.JButton();
        timeout1Label = new javax.swing.JLabel();
        timeoutField = new javax.swing.JTextField();
        timeoutPlus1Button = new javax.swing.JButton();

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
        hostnameField.addKeyListener(new java.awt.event.KeyAdapter()
        {
            public void keyReleased(java.awt.event.KeyEvent evt)
            {
                hostnameFieldKeyReleased(evt);
            }
        });

        sshportField.setFont(sshportField.getFont().deriveFont(sshportField.getFont().getSize()+1f));
        sshportField.setText(bundle.getString("DCMAddServerForm.sshportField.text")); // NOI18N
        sshportField.setToolTipText(bundle.getString("DCMAddServerForm.sshportField.toolTipText")); // NOI18N
        sshportField.addKeyListener(new java.awt.event.KeyAdapter()
        {
            public void keyReleased(java.awt.event.KeyEvent evt)
            {
                sshportFieldKeyReleased(evt);
            }
        });

        usernameField.setFont(usernameField.getFont().deriveFont(usernameField.getFont().getSize()+1f));
        usernameField.setToolTipText(bundle.getString("DCMAddServerForm.usernameField.toolTipText")); // NOI18N
        usernameField.addKeyListener(new java.awt.event.KeyAdapter()
        {
            public void keyReleased(java.awt.event.KeyEvent evt)
            {
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
                    .add(hostLabel))
                .add(68, 68, 68)
                .add(serverPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(sshportField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 598, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, hostnameField)
                    .add(superuserPasswordField)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, userPasswordField)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, usernameField))
                .addContainerGap())
        );
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
                .addContainerGap(137, Short.MAX_VALUE))
        );

        tabPane.addTab(bundle.getString("DCMAddServerForm.serverPanel.TabConstraints.tabTitle"), serverPanel); // NOI18N

        importPanel.setBackground(new java.awt.Color(102, 102, 102));
        importPanel.setFont(importPanel.getFont());
        importPanel.setOpaque(false);
        importPanel.setPreferredSize(new java.awt.Dimension(785, 300));

        hostImportScroller.setFont(hostImportScroller.getFont());

        hostImportTable.setFont(new java.awt.Font("STHeiti", 0, 10)); // NOI18N
        hostImportTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][]
            {

            },
            new String []
            {
                "Line", "Hostname", "SSH Port", "Username", "Password", "Superuser Password", "Status"
            }
        )
        {
            Class[] types = new Class []
            {
                java.lang.Long.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean []
            {
                false, false, false, false, false, false, false
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
        hostImportTable.setDoubleBuffered(true);
        hostImportTable.setGridColor(javax.swing.UIManager.getDefaults().getColor("Button.background"));
        hostImportScroller.setViewportView(hostImportTable);

        importLabel.setFont(importLabel.getFont().deriveFont(importLabel.getFont().getSize()-1f));
        importLabel.setForeground(new java.awt.Color(255, 255, 255));
        importLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        importLabel.setText(bundle.getString("DCMAddServerForm.importLabel.text")); // NOI18N

        importFileField.setEditable(false);
        importFileField.setFont(importFileField.getFont().deriveFont(importFileField.getFont().getSize()-3f));
        importFileField.setToolTipText(bundle.getString("DCMAddServerForm.importFileField.toolTipText")); // NOI18N
        importFileField.setEnabled(false);
        importFileField.addKeyListener(new java.awt.event.KeyAdapter()
        {
            public void keyReleased(java.awt.event.KeyEvent evt)
            {
                importFileFieldKeyReleased(evt);
            }
        });

        fieldSeparatorField.setFont(fieldSeparatorField.getFont().deriveFont(fieldSeparatorField.getFont().getSize()-3f));
        fieldSeparatorField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        fieldSeparatorField.setText(bundle.getString("DCMAddServerForm.fieldSeparatorField.text")); // NOI18N
        fieldSeparatorField.setToolTipText(bundle.getString("DCMAddServerForm.fieldSeparatorField.toolTipText")); // NOI18N
        fieldSeparatorField.setEnabled(false);
        fieldSeparatorField.addKeyListener(new java.awt.event.KeyAdapter()
        {
            public void keyReleased(java.awt.event.KeyEvent evt)
            {
                fieldSeparatorFieldKeyReleased(evt);
            }
        });

        browsButton.setFont(browsButton.getFont().deriveFont(browsButton.getFont().getSize()-3f));
        browsButton.setText(bundle.getString("DCMAddServerForm.browsButton.text")); // NOI18N
        browsButton.setToolTipText(bundle.getString("DCMAddServerForm.browsButton.toolTipText")); // NOI18N
        browsButton.setFocusable(false);
        browsButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        browsButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        browsButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                browsButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout importPanelLayout = new org.jdesktop.layout.GroupLayout(importPanel);
        importPanel.setLayout(importPanelLayout);
        importPanelLayout.setHorizontalGroup(
            importPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(importPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(importLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(importFileField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 483, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(fieldSeparatorField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(browsButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 104, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .add(org.jdesktop.layout.GroupLayout.TRAILING, hostImportScroller)
        );
        importPanelLayout.setVerticalGroup(
            importPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(importPanelLayout.createSequentialGroup()
                .add(hostImportScroller, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 277, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(importPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(importLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(importPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                        .add(browsButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(fieldSeparatorField)
                        .add(importFileField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        tabPane.addTab(bundle.getString("DCMAddServerForm.importPanel.TabConstraints.tabTitle"), importPanel); // NOI18N

        statusLabel.setBackground(new java.awt.Color(153, 153, 153));
        statusLabel.setFont(statusLabel.getFont());
        statusLabel.setForeground(new java.awt.Color(51, 51, 51));
        statusLabel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        statusLabel.setOpaque(true);

        buttonPanel.setBackground(new java.awt.Color(102, 102, 102));

        cancelAddServerButton.setFont(cancelAddServerButton.getFont().deriveFont(cancelAddServerButton.getFont().getSize()+1f));
        cancelAddServerButton.setText(bundle.getString("DCMAddServerForm.cancelAddServerButton.text")); // NOI18N
        cancelAddServerButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                cancelAddServerButtonActionPerformed(evt);
            }
        });

        addButton.setFont(addButton.getFont().deriveFont(addButton.getFont().getSize()+1f));
        addButton.setText(bundle.getString("DCMAddServerForm.addButton.text")); // NOI18N
        addButton.setToolTipText(bundle.getString("DCMAddServerForm.addButton.toolTipText")); // NOI18N
        addButton.setEnabled(false);
        addButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                addButtonActionPerformed(evt);
            }
        });

        retentionMinusButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/minus.png"))); // NOI18N
        retentionMinusButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                retentionMinusButtonActionPerformed(evt);
            }
        });

        retentionTimeField.setEditable(false);
        retentionTimeField.setFont(retentionTimeField.getFont().deriveFont(retentionTimeField.getFont().getSize()-1f));
        retentionTimeField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        retentionTimeField.setText(bundle.getString("DCMAddServerForm.retentionTimeField.text")); // NOI18N
        retentionTimeField.setToolTipText(bundle.getString("DCMAddServerForm.retentionTimeField.toolTipText")); // NOI18N
        retentionTimeField.setEnabled(false);

        retensionLabel.setFont(retensionLabel.getFont().deriveFont(retensionLabel.getFont().getSize()-1f));
        retensionLabel.setForeground(new java.awt.Color(255, 255, 255));
        retensionLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        retensionLabel.setText(bundle.getString("DCMAddServerForm.retensionLabel.text")); // NOI18N
        retensionLabel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        retentionPlusButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/plus.png"))); // NOI18N
        retentionPlusButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                retentionPlusButtonActionPerformed(evt);
            }
        });

        retryMinus1Button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/minus.png"))); // NOI18N
        retryMinus1Button.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                retryMinus1ButtonActionPerformed(evt);
            }
        });

        timeout1Label1.setFont(timeout1Label1.getFont().deriveFont(timeout1Label1.getFont().getSize()-1f));
        timeout1Label1.setForeground(new java.awt.Color(255, 255, 255));
        timeout1Label1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        timeout1Label1.setText(bundle.getString("DCMAddServerForm.timeout1Label1.text")); // NOI18N
        timeout1Label1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        retryField.setEditable(false);
        retryField.setFont(retryField.getFont().deriveFont(retryField.getFont().getSize()-1f));
        retryField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        retryField.setText(bundle.getString("DCMAddServerForm.retryField.text")); // NOI18N
        retryField.setToolTipText(bundle.getString("DCMAddServerForm.retryField.toolTipText")); // NOI18N
        retryField.setEnabled(false);

        retryPlus1Button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/plus.png"))); // NOI18N
        retryPlus1Button.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                retryPlus1ButtonActionPerformed(evt);
            }
        });

        timeoutMinus1Button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/minus.png"))); // NOI18N
        timeoutMinus1Button.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                timeoutMinus1ButtonActionPerformed(evt);
            }
        });

        timeout1Label.setFont(timeout1Label.getFont().deriveFont(timeout1Label.getFont().getSize()-1f));
        timeout1Label.setForeground(new java.awt.Color(255, 255, 255));
        timeout1Label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        timeout1Label.setText(bundle.getString("DCMAddServerForm.timeout1Label.text")); // NOI18N
        timeout1Label.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        timeoutField.setEditable(false);
        timeoutField.setFont(timeoutField.getFont().deriveFont(timeoutField.getFont().getSize()-1f));
        timeoutField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        timeoutField.setText(bundle.getString("DCMAddServerForm.timeoutField.text")); // NOI18N
        timeoutField.setToolTipText(bundle.getString("DCMAddServerForm.timeoutField.toolTipText")); // NOI18N
        timeoutField.setEnabled(false);

        timeoutPlus1Button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/plus.png"))); // NOI18N
        timeoutPlus1Button.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                timeoutPlus1ButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout buttonPanelLayout = new org.jdesktop.layout.GroupLayout(buttonPanel);
        buttonPanel.setLayout(buttonPanelLayout);
        buttonPanelLayout.setHorizontalGroup(
            buttonPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(buttonPanelLayout.createSequentialGroup()
                .add(cancelAddServerButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 85, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(77, 77, 77)
                .add(addButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 146, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(buttonPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, retensionLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, buttonPanelLayout.createSequentialGroup()
                        .add(retentionMinusButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 39, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(retentionTimeField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 34, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(retentionPlusButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 39, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(buttonPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, timeout1Label1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, buttonPanelLayout.createSequentialGroup()
                        .add(retryMinus1Button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 39, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(retryField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 34, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(retryPlus1Button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 39, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(buttonPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, timeout1Label, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, buttonPanelLayout.createSequentialGroup()
                        .add(timeoutMinus1Button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 39, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(timeoutField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 34, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(timeoutPlus1Button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 39, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        buttonPanelLayout.linkSize(new java.awt.Component[] {retryMinus1Button, retryPlus1Button, timeoutMinus1Button, timeoutPlus1Button}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        buttonPanelLayout.linkSize(new java.awt.Component[] {retryField, timeoutField}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        buttonPanelLayout.setVerticalGroup(
            buttonPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(buttonPanelLayout.createSequentialGroup()
                .add(buttonPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, buttonPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(buttonPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(buttonPanelLayout.createSequentialGroup()
                                .add(buttonPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(timeout1Label)
                                    .add(timeout1Label1))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(buttonPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(retryMinus1Button, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .add(retryField)
                                    .add(retryPlus1Button, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .add(timeoutMinus1Button, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .add(timeoutField)
                                    .add(timeoutPlus1Button, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 37, Short.MAX_VALUE)))
                            .add(buttonPanelLayout.createSequentialGroup()
                                .add(retensionLabel)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(buttonPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(retentionTimeField)
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, retentionPlusButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .add(retentionMinusButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                    .add(buttonPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(buttonPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(cancelAddServerButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 59, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(addButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 59, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout mainPanelLayout = new org.jdesktop.layout.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, buttonPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, statusLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(tabPane)
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(mainPanelLayout.createSequentialGroup()
                .add(tabPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 341, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(buttonPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
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
            .add(layout.createSequentialGroup()
                .add(mainPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 443, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

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
	if (hostList.isEmpty())
	{
	    setVisible(false);
	    hostnameField.setText("");
	    sshportField.setText("");
	    usernameField.setText("");
	    userPasswordField.setText("");
	    superuserPasswordField.setText("");
	    host = new Host();
	    dcmDesktop.addServerCanceled();
	}
	else
	{
	    importEnabled = false;
	    statusLabel.setText("Import Canceled by User...");
	    host = new Host();
	    dcmDesktop.addServerCanceled();
	}
    }//GEN-LAST:event_cancelAddServerButtonActionPerformed

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
	if ( hostList.isEmpty() ) // This means no CSV imported
	{
	    setVisible(false);
	    host = new Host();
	    host.setHostname(hostnameField.getText());
	    host.setPort(Integer.parseInt(sshportField.getText()));
	    host.setUsername(usernameField.getText());
	    host.setUserPassword(new String(userPasswordField.getPassword()));
	    host.setSuperuserPassword(new String(superuserPasswordField.getPassword()));
	    dcmDesktop.addServerByForm();
	}
	else // Import CSV hostList
	{
	    addButton.setEnabled(false);
	    ListSelectionModel selectionModel = hostImportTable.getSelectionModel(); // Is this necesary?
//            selectionModel.setSelectionInterval(1, 2);
	    dcmDesktop.addServersByForm(hostList);
	}
    }//GEN-LAST:event_addButtonActionPerformed

    private void timeoutMinus1ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_timeoutMinus1ButtonActionPerformed
        if (Integer.parseInt(timeoutField.getText()) > 1  )
        {
            timeout = Integer.parseInt(timeoutField.getText()); timeout--; timeoutField.setText(Integer.toString(timeout));
        }
    }//GEN-LAST:event_timeoutMinus1ButtonActionPerformed

    private void timeoutPlus1ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_timeoutPlus1ButtonActionPerformed
        if (Integer.parseInt(timeoutField.getText()) < 30  )
        {
            timeout = Integer.parseInt(timeoutField.getText()); timeout++; timeoutField.setText(Integer.toString(timeout));
        }
    }//GEN-LAST:event_timeoutPlus1ButtonActionPerformed

    private void retryMinus1ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_retryMinus1ButtonActionPerformed
        if (Integer.parseInt(retryField.getText()) > 0  )
        {
            retryMax = Integer.parseInt(retryField.getText()); retryMax--; retryField.setText(Integer.toString(retryMax));
        }
    }//GEN-LAST:event_retryMinus1ButtonActionPerformed

    private void retryPlus1ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_retryPlus1ButtonActionPerformed
        if (Integer.parseInt(retryField.getText()) < 5  )
        {
            retryMax = Integer.parseInt(retryField.getText()); retryMax++; retryField.setText(Integer.toString(retryMax));
        }
    }//GEN-LAST:event_retryPlus1ButtonActionPerformed

    private void hostnameFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_hostnameFieldKeyReleased
        validateForm();
    }//GEN-LAST:event_hostnameFieldKeyReleased

    private void sshportFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_sshportFieldKeyReleased
        validateForm();
    }//GEN-LAST:event_sshportFieldKeyReleased

    private void usernameFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_usernameFieldKeyReleased
        validateForm();
    }//GEN-LAST:event_usernameFieldKeyReleased

    private void retentionMinusButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_retentionMinusButtonActionPerformed
    {//GEN-HEADEREND:event_retentionMinusButtonActionPerformed
        if (Integer.parseInt(retentionTimeField.getText()) > 1  )
        {
            retentionTime = Integer.parseInt(retentionTimeField.getText()); retentionTime--; retentionTimeField.setText(Integer.toString(retentionTime));
        }
    }//GEN-LAST:event_retentionMinusButtonActionPerformed

    private void retentionPlusButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_retentionPlusButtonActionPerformed
    {//GEN-HEADEREND:event_retentionPlusButtonActionPerformed
        if (Integer.parseInt(retentionTimeField.getText()) < 100  )
        {
            retentionTime = Integer.parseInt(retentionTimeField.getText()); retentionTime++; retentionTimeField.setText(Integer.toString(retentionTime));
        }
    }//GEN-LAST:event_retentionPlusButtonActionPerformed

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

        boolean warning = false;
        String warningMessage = "";
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
                warning = false; warningMessage = "";
                error = false; errorMessage = "";
		
                if (fieldArray[0].length() == 0)                                            { error = true; errorMessage="Error: Hostname field is empty"; }
                try { Integer.parseInt(fieldArray[1]); } catch (NumberFormatException ex)   { warning = true; errorMessage="Warning: " + ex.getMessage(); fieldArray[1] = "22"; } // SSH Port
                if (fieldArray[2].length() == 0)                                            { error = true; errorMessage="Error: Username field is empty"; }
                if (fieldArray[3].length() == 0)                                            { warning = true; warningMessage="Warning: UserPassword field is empty"; }
                
                host.setId(lineCounter);
		host.setHostname(fieldArray[0]);
		host.setPort(Integer.parseInt(fieldArray[1]));
                host.setUsername(fieldArray[2]);
		host.setUserPassword(fieldArray[3]);
		host.setSuperuserPassword(fieldArray[4]);
		
                if ( (!warning) && (!error) )
                {
		    addButton.setEnabled(true);
                    host.setEnabled(true);
                    hostList.add(host);
                    model.addRow(new Object[] {lineCounter, fieldArray[0], fieldArray[1], fieldArray[2], fieldArray[3].replaceAll(".", "*"), fieldArray[4].replaceAll(".", "*")});
                }
		else if ( (!warning) && (error) )
                {
                    host.setEnabled(false);
                    hostList.add(host);
                    model.addRow(new Object[] {lineCounter, fieldArray[0], "??", fieldArray[2], fieldArray[3].replaceAll(".", "*"), fieldArray[4].replaceAll(".", "*"), errorMessage});
                    statusLabel.setText(errorMessage);
                }
		else if ( (warning) && (!error) )
                {
		    addButton.setEnabled(true);
                    host.setEnabled(true);
                    hostList.add(host);
                    model.addRow(new Object[] {lineCounter, fieldArray[0], "??", fieldArray[2], fieldArray[3].replaceAll(".", "*"), fieldArray[4].replaceAll(".", "*"), errorMessage});
                    statusLabel.setText(errorMessage);
                }
		else if ( (warning) && (error) )
                {
                    host.setEnabled(false);
                    hostList.add(host);
                    model.addRow(new Object[] {lineCounter, fieldArray[0], "??", fieldArray[2], fieldArray[3].replaceAll(".", "*"), fieldArray[4].replaceAll(".", "*"), errorMessage});
                    statusLabel.setText(errorMessage);
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
    
    protected int getRetentionTime() { return retentionTime; }
    protected int getTimeout() { return timeout; }
    protected int getRetryMax1() { return retryMax; }
//    protected int getTimeout2() { return timeout2; }
//    protected int getRetryMax2() { return retryMax2; }

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
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton cancelAddServerButton;
    private javax.swing.JTextField fieldSeparatorField;
    private javax.swing.JScrollPane hostImportScroller;
    protected javax.swing.JTable hostImportTable;
    private javax.swing.JLabel hostLabel;
    protected javax.swing.JTextField hostnameField;
    private javax.swing.JTextField importFileField;
    private javax.swing.JLabel importLabel;
    private javax.swing.JPanel importPanel;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JLabel portLabel;
    private javax.swing.JLabel retensionLabel;
    private javax.swing.JButton retentionMinusButton;
    private javax.swing.JButton retentionPlusButton;
    protected javax.swing.JTextField retentionTimeField;
    protected javax.swing.JTextField retryField;
    private javax.swing.JButton retryMinus1Button;
    private javax.swing.JButton retryPlus1Button;
    private javax.swing.JLabel rootpwlabel;
    private javax.swing.JPanel serverPanel;
    protected javax.swing.JTextField sshportField;
    protected javax.swing.JLabel statusLabel;
    protected javax.swing.JPasswordField superuserPasswordField;
    private javax.swing.JTabbedPane tabPane;
    private javax.swing.JLabel timeout1Label;
    private javax.swing.JLabel timeout1Label1;
    protected javax.swing.JTextField timeoutField;
    private javax.swing.JButton timeoutMinus1Button;
    private javax.swing.JButton timeoutPlus1Button;
    private javax.swing.JLabel userLabel;
    protected javax.swing.JPasswordField userPasswordField;
    private javax.swing.JLabel userPasswordLabel;
    protected javax.swing.JTextField usernameField;
    // End of variables declaration//GEN-END:variables
}
