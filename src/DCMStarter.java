import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class DCMStarter extends javax.swing.JFrame
{
    private DCMShell shell;
    private boolean debug = false;
    private boolean blockWindow = false;
    private Color buttonForegroundColor;
    private String serverFieldExample;
    private boolean sloganWaitWarningBlinking = false;
    private boolean serverFieldsBlinking = false;
    private boolean serverFieldChanged = false;
    private String dataDir;
    private String sloganText;
    private File file;
    private DCMStarter dcmStarterReference;
    private String country;
    private String language;
    private String[][] lang;
    private Locale currentLocale;
    private Locale targetLocale;

    public DCMStarter()
    {
        UIManager.put("Label.font",new Font("Courier",Font.ITALIC,12));
        initComponents();
        setTitle(DCMLicense.getProcuct() + " " + DCMLicense.getVersion());
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
        shell = new DCMShell(debug); // For starting the poller
//        websiteButton.setText("Check Version " + DCMLicense.getVersion());
//        versionLabel.setText(DCMLicense.getVersion());
        buttonForegroundColor = new Color(255,255,255);
        sloganText = "A UNIX Server can Run, but it can't Hide";
        serverFieldExample = "127.0.0.1";
        serverField.setText(serverFieldExample);
        dataDir = "data";
        file = new File(dataDir);
        blockWindow = true;
        country = System.getProperty("user.country");
        language = System.getProperty("user.language");
        currentLocale = new Locale(language, country);
        targetLocale = new Locale(language, country);
        lang = new String[][]
        {
//                                                                                NL     FR      AR     RU      PT      IT      EN      ES      DE
{  "ألماني", "الأسبانية", "الإنجليزية","الإيطالي", "البرتغالية", "الروسية", "العربية", "فرنسي", "هولندي"  },                                                                          //
                                            {"Arabisch",    "Deutsch",  "Englisch",     "Französisch",  "Holländisch",  "Italienisch",   "Portugiesisch",   "Russisch",     "Spanisch"},    // German       1
                                            {"Arabic",      "Dutch",    "English",      "French",       "German",       "Italian",       "Portuguese",      "Russian",      "Spanish"},     // English      2
                                            {"árabe",       "holandês", "Inglês",       "alemán",       "francés",      "español",       "italiano",        "portugués",    "ruso"},        // Spanish      3
                                            {"allemand",    "anglaise", "arabe",        "espagnole",    "français",     "hollandaise",   "italien",         "portugaise",   "russes"},      // French       4
                                            {"arabo",       "francese", "inglese",      "italiano",     "olandese",     "portoghese",    "russo",           "spagnolo",     "tedesco"},     // Italian      5
                                            {"Arabisch",    "Duits",    "Engels",       "Frans",        "Italiaans",    "Nederlands",    "Portugees",       "Russisch",     "Spaans"},      // Dutch        6
                                            {"alemão",      "árabe",    "espanhol",     "francês",      "holandês",     "Inglês",        "italiano",        "português",    "russo"},       // Portuguese   7
                                            {"английский",  "арабский", "голландский",  "испанский",    "итальянский",  "немецкий",      "португальский",   "русский",      "французский"}  // Russian      8
//                                               EN              AR          NL              ES               IT             DE              PT                  RU              FR
        };
        String loc = Locale.getDefault().getLanguage(); //                                                                                                                                                   R  C                    R  C
        if           (loc.equals("ar")) { languageBox.removeAllItems(); for (String mylang:lang[0]) { languageBox.addItem(mylang); } targetLocale = new Locale("ar", "SA"); languageBox.setSelectedItem(lang[0][6]); }
        else if      (loc.equals("de")) { languageBox.removeAllItems(); for (String mylang:lang[1]) { languageBox.addItem(mylang); } targetLocale = new Locale("de", "DE"); languageBox.setSelectedItem(lang[1][1]); }
        else if      (loc.equals("en")) { languageBox.removeAllItems(); for (String mylang:lang[2]) { languageBox.addItem(mylang); } targetLocale = new Locale("en", "US"); languageBox.setSelectedItem(lang[2][2]); }
        else if      (loc.equals("es")) { languageBox.removeAllItems(); for (String mylang:lang[3]) { languageBox.addItem(mylang); } targetLocale = new Locale("es", "ES"); languageBox.setSelectedItem(lang[3][5]); }
        else if      (loc.equals("fr")) { languageBox.removeAllItems(); for (String mylang:lang[4]) { languageBox.addItem(mylang); } targetLocale = new Locale("fr", "FR"); languageBox.setSelectedItem(lang[4][4]); }
        else if      (loc.equals("it")) { languageBox.removeAllItems(); for (String mylang:lang[5]) { languageBox.addItem(mylang); } targetLocale = new Locale("it", "IT"); languageBox.setSelectedItem(lang[5][3]); }
        else if      (loc.equals("nl")) { languageBox.removeAllItems(); for (String mylang:lang[6]) { languageBox.addItem(mylang); } targetLocale = new Locale("nl", "NL"); languageBox.setSelectedItem(lang[6][5]); }
        else if      (loc.equals("pt")) { languageBox.removeAllItems(); for (String mylang:lang[7]) { languageBox.addItem(mylang); } targetLocale = new Locale("pt", "BR"); languageBox.setSelectedItem(lang[7][7]); }
        else if      (loc.equals("ru")) { languageBox.removeAllItems(); for (String mylang:lang[8]) { languageBox.addItem(mylang); } targetLocale = new Locale("ru", "RU"); languageBox.setSelectedItem(lang[8][7]); }
        else                            { languageBox.removeAllItems(); for (String mylang:lang[1]) { languageBox.addItem(mylang); }                                        languageBox.setSelectedItem(lang[2][2]); }
        blockWindow = false;
        dcmStarterReference = this;
    }

    private void languageSelected()
    {
        if (! blockWindow)
        {
            String selItem = String.valueOf(languageBox.getSelectedItem());
            for (int x = 0; x < (lang.length ); x++) { if ( selItem.equals(lang[0][6]) || selItem.equals(lang[1][0]) || selItem.equals(lang[2][0]) || selItem.equals(lang[3][0]) || selItem.equals(lang[4][2]) ||
                                                            selItem.equals(lang[5][0]) || selItem.equals(lang[6][0]) || selItem.equals(lang[7][1]) || selItem.equals(lang[8][1]) ) { targetLocale = new Locale("ar", "SA"); Locale.setDefault(targetLocale); currentLocale.setDefault(targetLocale); }}
            
            for (int x = 0; x < (lang.length ); x++) { if ( selItem.equals(lang[0][0]) || selItem.equals(lang[1][1]) || selItem.equals(lang[2][4]) || selItem.equals(lang[3][3]) || selItem.equals(lang[4][0]) ||
                                                            selItem.equals(lang[5][8]) || selItem.equals(lang[6][1]) || selItem.equals(lang[7][0]) || selItem.equals(lang[8][5]) ) { targetLocale = new Locale("de", "DE"); Locale.setDefault(targetLocale); currentLocale.setDefault(targetLocale); }}
            
            for (int x = 0; x < (lang.length ); x++) { if ( selItem.equals(lang[0][2]) || selItem.equals(lang[1][2]) || selItem.equals(lang[2][2]) || selItem.equals(lang[3][2]) || selItem.equals(lang[4][1]) ||
                                                            selItem.equals(lang[5][2]) || selItem.equals(lang[6][2]) || selItem.equals(lang[7][5]) || selItem.equals(lang[8][0]) ) { targetLocale = new Locale("en", "EN"); Locale.setDefault(targetLocale); currentLocale.setDefault(targetLocale); }}
            
            for (int x = 0; x < (lang.length ); x++) { if ( selItem.equals(lang[0][1]) || selItem.equals(lang[1][8]) || selItem.equals(lang[2][8]) || selItem.equals(lang[3][5]) || selItem.equals(lang[4][3]) ||
                                                            selItem.equals(lang[5][7]) || selItem.equals(lang[6][8]) || selItem.equals(lang[7][2]) || selItem.equals(lang[8][3]) ) { targetLocale = new Locale("es", "ES"); Locale.setDefault(targetLocale); currentLocale.setDefault(targetLocale); }}
            
            for (int x = 0; x < (lang.length ); x++) { if ( selItem.equals(lang[0][7]) || selItem.equals(lang[1][3]) || selItem.equals(lang[2][3]) || selItem.equals(lang[3][4]) || selItem.equals(lang[4][4]) ||
                                                            selItem.equals(lang[5][1]) || selItem.equals(lang[6][3]) || selItem.equals(lang[7][3]) || selItem.equals(lang[8][8]) ) { targetLocale = new Locale("fr", "FR"); Locale.setDefault(targetLocale); currentLocale.setDefault(targetLocale); }}
            
            for (int x = 0; x < (lang.length ); x++) { if ( selItem.equals(lang[0][3]) || selItem.equals(lang[1][5]) || selItem.equals(lang[2][5]) || selItem.equals(lang[3][6]) || selItem.equals(lang[4][6]) ||
                                                            selItem.equals(lang[5][3]) || selItem.equals(lang[6][4]) || selItem.equals(lang[7][6]) || selItem.equals(lang[8][4]) ) { targetLocale = new Locale("it", "IT"); Locale.setDefault(targetLocale); currentLocale.setDefault(targetLocale); }}
            
            for (int x = 0; x < (lang.length ); x++) { if ( selItem.equals(lang[0][8]) || selItem.equals(lang[1][4]) || selItem.equals(lang[2][1]) || selItem.equals(lang[3][1]) || selItem.equals(lang[4][5]) ||
                                                            selItem.equals(lang[5][4]) || selItem.equals(lang[6][5]) || selItem.equals(lang[7][4]) || selItem.equals(lang[8][2]) ) { targetLocale = new Locale("nl", "NL"); Locale.setDefault(targetLocale); currentLocale.setDefault(targetLocale); }}
            
            for (int x = 0; x < (lang.length ); x++) { if ( selItem.equals(lang[0][4]) || selItem.equals(lang[1][6]) || selItem.equals(lang[2][6]) || selItem.equals(lang[3][7]) || selItem.equals(lang[4][7]) ||
                                                            selItem.equals(lang[5][5]) || selItem.equals(lang[6][6]) || selItem.equals(lang[7][7]) || selItem.equals(lang[8][6]) ) { targetLocale = new Locale("pt", "BR"); Locale.setDefault(targetLocale); currentLocale.setDefault(targetLocale); }}
            
            for (int x = 0; x < (lang.length ); x++) { if ( selItem.equals(lang[0][5]) || selItem.equals(lang[1][7]) || selItem.equals(lang[2][7]) || selItem.equals(lang[3][8]) || selItem.equals(lang[4][8]) ||
                                                            selItem.equals(lang[5][6]) || selItem.equals(lang[6][7]) || selItem.equals(lang[7][8]) || selItem.equals(lang[8][7]) ) { targetLocale = new Locale("ru", "RU"); Locale.setDefault(targetLocale); currentLocale.setDefault(targetLocale); }}
            
            setLocale(targetLocale);
            setVisible(false);
            new DCMStarter().setVisible(true);            
        }        
    }
    
    public static void setUIFont (javax.swing.plaf.FontUIResource f)
    {
        java.util.Enumeration keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements())
        {
            Object key = keys.nextElement();
            Object value = UIManager.get (key);
            if (value instanceof javax.swing.plaf.FontUIResource)
            UIManager.put (key, f);
        }
    }    
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        jLayeredPane1 = new javax.swing.JLayeredPane();
        portField = new javax.swing.JTextField();
        serverField = new javax.swing.JTextField();
        languageBox = new javax.swing.JComboBox();
        daemonsVisibleBox = new javax.swing.JCheckBox();
        websiteButton = new javax.swing.JButton();
        headerLabel = new javax.swing.JLabel();
        sloganLabel = new javax.swing.JLabel();
        debugCheckbox = new javax.swing.JCheckBox();
        desktopLabel = new javax.swing.JLabel();
        serverLabel = new javax.swing.JLabel();
        clientLabel = new javax.swing.JLabel();
        pollerLabel = new javax.swing.JLabel();
        backgroundLabel = new javax.swing.JLabel();
        infoScroller = new javax.swing.JScrollPane();
        infoArea = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("DCMStarter"); // NOI18N
        setTitle(bundle.getString("DCMStarter.title")); // NOI18N
        setFont(new java.awt.Font("Apple Chancery", 0, 10)); // NOI18N
        setResizable(false);

        jLayeredPane1.setBackground(new java.awt.Color(51, 51, 51));
        jLayeredPane1.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jLayeredPane1.setOpaque(true);

        portField.setFont(new java.awt.Font("Courier New", 1, 12)); // NOI18N
        portField.setForeground(java.awt.Color.gray);
        portField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        portField.setText(bundle.getString("DCMStarter.portField.text")); // NOI18N
        portField.setToolTipText(bundle.getString("DCMStarter.portField.toolTipText")); // NOI18N
        portField.setBorder(null);
        jLayeredPane1.add(portField);
        portField.setBounds(250, 410, 50, 15);

        serverField.setFont(new java.awt.Font("Courier New", 1, 12)); // NOI18N
        serverField.setForeground(java.awt.Color.gray);
        serverField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        serverField.setText(bundle.getString("DCMStarter.serverField.text")); // NOI18N
        serverField.setToolTipText(bundle.getString("DCMStarter.serverField.toolTipText")); // NOI18N
        serverField.setBorder(null);
        serverField.setOpaque(false);
        serverField.addKeyListener(new java.awt.event.KeyAdapter()
        {
            public void keyReleased(java.awt.event.KeyEvent evt)
            {
                serverFieldKeyReleased(evt);
            }
        });
        jLayeredPane1.add(serverField);
        serverField.setBounds(50, 410, 200, 15);

        languageBox.setFont(languageBox.getFont().deriveFont(languageBox.getFont().getSize()-3f));
        languageBox.setMaximumRowCount(25);
        languageBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Arabic", "English", "Portuguese", "Russian" }));
        languageBox.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                languageBoxActionPerformed(evt);
            }
        });
        jLayeredPane1.add(languageBox);
        languageBox.setBounds(520, 80, 100, 20);

        daemonsVisibleBox.setFont(daemonsVisibleBox.getFont().deriveFont(daemonsVisibleBox.getFont().getSize()-5f));
        daemonsVisibleBox.setForeground(new java.awt.Color(204, 204, 204));
        daemonsVisibleBox.setSelected(true);
        daemonsVisibleBox.setText(bundle.getString("DCMStarter.daemonsVisibleBox.text")); // NOI18N
        daemonsVisibleBox.setToolTipText(bundle.getString("DCMStarter.daemonsVisibleBox.toolTipText")); // NOI18N
        jLayeredPane1.add(daemonsVisibleBox);
        daemonsVisibleBox.setBounds(140, 430, 230, 20);

        websiteButton.setFont(new java.awt.Font("STHeiti", 0, 10)); // NOI18N
        websiteButton.setText(bundle.getString("DCMStarter.websiteButton.text")); // NOI18N
        websiteButton.setToolTipText(bundle.getString("DCMStarter.websiteButton.toolTipText")); // NOI18N
        websiteButton.setActionCommand(bundle.getString("DCMStarter.websiteButton.actionCommand")); // NOI18N
        websiteButton.setFocusable(false);
        websiteButton.setMaximumSize(new java.awt.Dimension(90, 13));
        websiteButton.setMinimumSize(new java.awt.Dimension(90, 13));
        websiteButton.setPreferredSize(new java.awt.Dimension(90, 13));
        websiteButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                websiteButtonActionPerformed(evt);
            }
        });
        jLayeredPane1.add(websiteButton);
        websiteButton.setBounds(0, 80, 80, 20);

        headerLabel.setFont(headerLabel.getFont().deriveFont(headerLabel.getFont().getStyle() | java.awt.Font.BOLD, headerLabel.getFont().getSize()+35));
        headerLabel.setForeground(new java.awt.Color(255, 255, 255));
        headerLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        headerLabel.setText(bundle.getString("DCMStarter.headerLabel.text")); // NOI18N
        jLayeredPane1.add(headerLabel);
        headerLabel.setBounds(0, 4, 620, 70);

        sloganLabel.setFont(new java.awt.Font("Courier", 1, 14)); // NOI18N
        sloganLabel.setForeground(new java.awt.Color(204, 204, 204));
        sloganLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        sloganLabel.setText(bundle.getString("DCMStarter.sloganLabel.text")); // NOI18N
        jLayeredPane1.add(sloganLabel);
        sloganLabel.setBounds(0, 70, 620, 40);

        debugCheckbox.setFont(debugCheckbox.getFont().deriveFont(debugCheckbox.getFont().getSize()-5f));
        debugCheckbox.setForeground(new java.awt.Color(204, 204, 204));
        debugCheckbox.setText(bundle.getString("DCMStarter.debugCheckbox.text")); // NOI18N
        debugCheckbox.setToolTipText(bundle.getString("DCMStarter.debugCheckbox.toolTipText")); // NOI18N
        debugCheckbox.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                debugCheckboxActionPerformed(evt);
            }
        });
        jLayeredPane1.add(debugCheckbox);
        debugCheckbox.setBounds(440, 430, 90, 20);

        desktopLabel.setFont(desktopLabel.getFont());
        desktopLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        desktopLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/desktop.png"))); // NOI18N
        desktopLabel.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED), bundle.getString("DCMStarter.desktopLabel.border.title"), javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.BELOW_TOP, new java.awt.Font("STHeiti", 0, 18), new java.awt.Color(255, 255, 255))); // NOI18N
        desktopLabel.setOpaque(true);
        desktopLabel.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mousePressed(java.awt.event.MouseEvent evt)
            {
                desktopLabelMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt)
            {
                desktopLabelMouseReleased(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt)
            {
                desktopLabelMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt)
            {
                desktopLabelMouseEntered(evt);
            }
        });
        jLayeredPane1.add(desktopLabel);
        desktopLabel.setBounds(0, 110, 310, 160);

        serverLabel.setFont(serverLabel.getFont());
        serverLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        serverLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/server.png"))); // NOI18N
        serverLabel.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED), bundle.getString("DCMStarter.serverLabel.border.title"), javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.BELOW_TOP, new java.awt.Font("STHeiti", 0, 18), new java.awt.Color(255, 255, 255))); // NOI18N
        serverLabel.setOpaque(true);
        serverLabel.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mousePressed(java.awt.event.MouseEvent evt)
            {
                serverLabelMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt)
            {
                serverLabelMouseReleased(evt);
            }
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                serverLabelMouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt)
            {
                serverLabelMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt)
            {
                serverLabelMouseEntered(evt);
            }
        });
        jLayeredPane1.add(serverLabel);
        serverLabel.setBounds(0, 270, 310, 164);

        clientLabel.setFont(clientLabel.getFont());
        clientLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        clientLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/client.png"))); // NOI18N
        clientLabel.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED), bundle.getString("DCMStarter.clientLabel.border.title"), javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.BELOW_TOP, new java.awt.Font("STHeiti", 0, 18), new java.awt.Color(255, 255, 255))); // NOI18N
        clientLabel.setOpaque(true);
        clientLabel.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mousePressed(java.awt.event.MouseEvent evt)
            {
                clientLabelMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt)
            {
                clientLabelMouseReleased(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt)
            {
                clientLabelMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt)
            {
                clientLabelMouseEntered(evt);
            }
        });
        jLayeredPane1.add(clientLabel);
        clientLabel.setBounds(310, 110, 310, 160);

        pollerLabel.setFont(pollerLabel.getFont());
        pollerLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        pollerLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/poller.png"))); // NOI18N
        pollerLabel.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED), bundle.getString("DCMStarter.pollerLabel.border.title"), javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.BELOW_TOP, new java.awt.Font("STHeiti", 0, 18), new java.awt.Color(255, 255, 255))); // NOI18N
        pollerLabel.setOpaque(true);
        pollerLabel.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mousePressed(java.awt.event.MouseEvent evt)
            {
                pollerLabelMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt)
            {
                pollerLabelMouseReleased(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt)
            {
                pollerLabelMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt)
            {
                pollerLabelMouseEntered(evt);
            }
        });
        jLayeredPane1.add(pollerLabel);
        pollerLabel.setBounds(310, 270, 310, 164);

        backgroundLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        backgroundLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/worldmap.jpg"))); // NOI18N
        backgroundLabel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLayeredPane1.add(backgroundLabel);
        backgroundLabel.setBounds(0, 0, 620, 110);

        infoScroller.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        infoScroller.setForeground(new java.awt.Color(255, 255, 51));
        infoScroller.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        infoScroller.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        infoArea.setBackground(new java.awt.Color(51, 51, 51));
        infoArea.setColumns(20);
        infoArea.setEditable(false);
        infoArea.setFont(new java.awt.Font("STHeiti", 0, 12)); // NOI18N
        infoArea.setForeground(new java.awt.Color(204, 204, 204));
        infoArea.setRows(5);
        infoArea.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        infoArea.setMinimumSize(new java.awt.Dimension(2, 20));
        infoArea.setPreferredSize(new java.awt.Dimension(262, 80));
        infoScroller.setViewportView(infoArea);

        jLayeredPane1.add(infoScroller);
        infoScroller.setBounds(0, 450, 620, 40);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLayeredPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 620, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLayeredPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 492, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void desktopLabelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_desktopLabelMouseEntered
        desktopLabel.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED), "Start Desktop", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.BELOW_TOP, new java.awt.Font("STHeiti", 0, 18), buttonForegroundColor)); // NOI18N
        infoArea.setText("Start Standalone Desktop Application\njava -cp DCManager.jar DCMDesktop");
    }//GEN-LAST:event_desktopLabelMouseEntered

    private void desktopLabelMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_desktopLabelMouseExited
        desktopLabel.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED), "Desktop", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.BELOW_TOP, new java.awt.Font("STHeiti", 0, 18), buttonForegroundColor)); // NOI18N
//        infoArea.setText("");
    }//GEN-LAST:event_desktopLabelMouseExited

    private void clientLabelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_clientLabelMouseEntered
        clientLabel.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED), "Start Client", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.BELOW_TOP, new java.awt.Font("STHeiti", 0, 18), buttonForegroundColor)); // NOI18N
        infoArea.setText("Start Network Client (Requires Server)\njava -cp DCManager.jar DCMClient");
    }//GEN-LAST:event_clientLabelMouseEntered

    private void clientLabelMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_clientLabelMouseExited
        clientLabel.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED), "Client", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.BELOW_TOP, new java.awt.Font("STHeiti", 0, 18), buttonForegroundColor)); // NOI18N
//        infoArea.setText("");
    }//GEN-LAST:event_clientLabelMouseExited

    private void serverLabelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_serverLabelMouseEntered
        serverLabel.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED), "Start Server", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.BELOW_TOP, new java.awt.Font("STHeiti", 0, 18), buttonForegroundColor)); // NOI18N
        infoArea.setText("Start Network Server (Used by Network Client)\njava -Djava.rmi.server.hostname=server.domain.com -cp DCManager.jar DCMServer 20000 #(portnumber)");
        blinkServerFields(4);
    }//GEN-LAST:event_serverLabelMouseEntered

    private void blinkServerFields(final int blinkcycle)
    {
        if ((!serverFieldsBlinking) && (serverField.isEnabled()) && (portField.isEnabled()))
        {
            Thread blinkThread = new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    serverFieldsBlinking = true;
                    for (int i=1; i<blinkcycle; i++)
                    {
                        serverField.setForeground(Color.black);
                        portField.setForeground(Color.black);
                        try { Thread.sleep(125); } catch (InterruptedException ex) {  }
                        serverField.setForeground(Color.white);
                        portField.setForeground(Color.white);
                        try { Thread.sleep(125); } catch (InterruptedException ex) {  }
                    }
//                    serverField.setForeground(new Color(204,204,204));
//                    portField.setForeground(new Color(204,204,204));
                    serverField.setForeground(Color.GRAY);
                    portField.setForeground(Color.GRAY);
                    serverFieldsBlinking = false;
                }
            });
            blinkThread.setName("blinkThread");
            blinkThread.setDaemon(false);
            blinkThread.setPriority(Thread.NORM_PRIORITY);
            blinkThread.start();                    
        }
    }
    
    private void blinkSloganWaitWarning(final String message, final int blinkRepeats)
    {
        if (!sloganWaitWarningBlinking)
        {
            desktopLabel.setEnabled(false);
            clientLabel.setEnabled(false);
            serverLabel.setEnabled(false);
            serverLabel.setEnabled(false);
            pollerLabel.setEnabled(false);
            serverField.setEnabled(false);
            portField.setEnabled(false);

            sloganWaitWarningBlinking = true;
            for (int i=1; i<blinkRepeats; i++)
            {
                sloganLabel.setText(message + " Please Wait...");
                try { Thread.sleep(500); } catch (InterruptedException ex) {  }
                sloganLabel.setText(message + "               ");
                try { Thread.sleep(500); } catch (InterruptedException ex) {  }
            }
            sloganLabel.setText(sloganText);
            sloganWaitWarningBlinking = false;

            desktopLabel.setEnabled(true);
            clientLabel.setEnabled(true);
            serverLabel.setEnabled(true);
            pollerLabel.setEnabled(true);
            serverField.setEnabled(true);
            portField.setEnabled(true);
        }
    }
    
    private void serverLabelMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_serverLabelMouseExited
        serverLabel.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED), "Server", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.BELOW_TOP, new java.awt.Font("STHeiti", 0, 18), buttonForegroundColor)); // NOI18N
        blinkServerFields(2);
//        infoArea.setText("");
    }//GEN-LAST:event_serverLabelMouseExited

    private void pollerLabelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pollerLabelMouseEntered
        pollerLabel.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED), "Start Poller", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.BELOW_TOP, new java.awt.Font("STHeiti", 0, 18), buttonForegroundColor)); // NOI18N
        infoArea.setText("Start Poller (Collects Datacenter Statistics)\njava -Duser.country=US -Duser.language=en -cp DCManager.jar DCMPoller");
    }//GEN-LAST:event_pollerLabelMouseEntered

    private void pollerLabelMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pollerLabelMouseExited
        pollerLabel.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED), "Poller", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.BELOW_TOP, new java.awt.Font("STHeiti", 0, 18), buttonForegroundColor)); // NOI18N
//        infoArea.setText("");
    }//GEN-LAST:event_pollerLabelMouseExited

    private void desktopLabelMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_desktopLabelMousePressed
        desktopLabel.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED), "Start Desktop", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.BELOW_TOP, new java.awt.Font("STHeiti", 0, 18), buttonForegroundColor)); // NOI18N
    }//GEN-LAST:event_desktopLabelMousePressed

    private void desktopLabelMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_desktopLabelMouseReleased
        desktopLabel.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED), "Start Desktop", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.BELOW_TOP, new java.awt.Font("STHeiti", 0, 18), buttonForegroundColor)); // NOI18N
        dcmStarterReference = this;
        Thread blinkDesktopWaitWarningThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                if (desktopLabel.isEnabled())
                {
                    String daemonParamString = ""; if (daemonsVisibleBox.isSelected()) { daemonParamString = ""; } else { daemonParamString = " --daemon "; }
                    shell.startDesktop(256, " -Duser.country=" + targetLocale.getCountry() + " -Duser.language=" + targetLocale.getLanguage() + " ", daemonParamString);
                    if (!file.exists()) { blinkSloganWaitWarning("Desktop Initiating", 15); } else { blinkSloganWaitWarning("Desktop Starting", 5); }
                    dcmStarterReference.dropWindow();                    
                }
            }
        });
        blinkDesktopWaitWarningThread.setName("blinkDesktopWaitWarningThread");
        blinkDesktopWaitWarningThread.setDaemon(false);
        blinkDesktopWaitWarningThread.setPriority(Thread.NORM_PRIORITY);
        blinkDesktopWaitWarningThread.start();                    
    }//GEN-LAST:event_desktopLabelMouseReleased

    private void clientLabelMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_clientLabelMousePressed
        clientLabel.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED), "Start Client", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.BELOW_TOP, new java.awt.Font("STHeiti", 0, 18), buttonForegroundColor)); // NOI18N
    }//GEN-LAST:event_clientLabelMousePressed

    private void clientLabelMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_clientLabelMouseReleased
        clientLabel.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED), "Start Client", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.BELOW_TOP, new java.awt.Font("STHeiti", 0, 18), buttonForegroundColor)); // NOI18N
        dcmStarterReference = this;
        Thread blinkClientWaitWarningThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                if (clientLabel.isEnabled())
                {
                    String daemonParamString = ""; if (daemonsVisibleBox.isSelected()) { daemonParamString = ""; } else { daemonParamString = " --daemon "; }
                    shell.startClient(128, " -Duser.country=" + targetLocale.getCountry() + " -Duser.language=" + targetLocale.getLanguage() + " ", daemonParamString);
                    blinkSloganWaitWarning("Client Starting", 5);
                    dcmStarterReference.dropWindow();
                }
            }
        });
        blinkClientWaitWarningThread.setName("blinkClientWaitWarningThread");
        blinkClientWaitWarningThread.setDaemon(false);
        blinkClientWaitWarningThread.setPriority(Thread.NORM_PRIORITY);
        blinkClientWaitWarningThread.start();                    
    }//GEN-LAST:event_clientLabelMouseReleased

    private void serverLabelMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_serverLabelMousePressed
        serverLabel.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED), "Start Server", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.BELOW_TOP, new java.awt.Font("STHeiti", 0, 18), buttonForegroundColor)); // NOI18N
    }//GEN-LAST:event_serverLabelMousePressed

    private void serverLabelMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_serverLabelMouseReleased
        serverLabel.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED), "Start Server", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.BELOW_TOP, new java.awt.Font("STHeiti", 0, 18), buttonForegroundColor)); // NOI18N
        dcmStarterReference = this;
        Thread blinkServerWaitWarningThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                if (serverFieldChanged)
                {
        //            dcmStarterRef.dropWindow();
                    if (serverLabel.isEnabled())
                    {
                        String daemonParamString = ""; if (daemonsVisibleBox.isSelected()) { daemonParamString = ""; } else { daemonParamString = " --daemon "; }
                        shell.startServer(256, "-server -Djava.rmi.server.hostname=" + serverField.getText(), Integer.parseInt(portField.getText()),daemonParamString);
                        if (!file.exists()) { blinkSloganWaitWarning("Server Initiating", 10); } else { blinkSloganWaitWarning("Server Starting", 4); }
                    }
                }
                else { blinkServerFields(2); }
            }
        });
        blinkServerWaitWarningThread.setName("blinkServerWaitWarningThread");
        blinkServerWaitWarningThread.setDaemon(false);
        blinkServerWaitWarningThread.setPriority(Thread.NORM_PRIORITY);
        blinkServerWaitWarningThread.start();                               
    }//GEN-LAST:event_serverLabelMouseReleased

    private void pollerLabelMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pollerLabelMousePressed
        pollerLabel.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED), "Start Poller", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.BELOW_TOP, new java.awt.Font("STHeiti", 0, 18), buttonForegroundColor)); // NOI18N
    }//GEN-LAST:event_pollerLabelMousePressed

    private void pollerLabelMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pollerLabelMouseReleased
        pollerLabel.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED), "Start Poller", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.BELOW_TOP, new java.awt.Font("STHeiti", 0, 18), buttonForegroundColor)); // NOI18N
        dcmStarterReference = this;
        Thread blinkPollerWaitWarningThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
        //        dcmStarterRef.dropWindow();
                if (pollerLabel.isEnabled())
                {
                    String daemonParamString = ""; if (daemonsVisibleBox.isSelected()) { daemonParamString = ""; } else { daemonParamString = " --daemon "; }
//                    shell.startPoller(128, "" + language + " ",daemonParamString);
                    shell.startPoller(128," -Duser.country=US -Duser.language=en ", daemonParamString);
                    if (!file.exists()) { blinkSloganWaitWarning("Poller Initiating", 10); } else { blinkSloganWaitWarning("Poller Starting", 4); }
                }
            }
        });
        blinkPollerWaitWarningThread.setName("blinkPollerWaitWarningThread");
        blinkPollerWaitWarningThread.setDaemon(false);
        blinkPollerWaitWarningThread.setPriority(Thread.NORM_PRIORITY);
        blinkPollerWaitWarningThread.start();                    
    }//GEN-LAST:event_pollerLabelMouseReleased

    private void debugCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_debugCheckboxActionPerformed
        if ( debugCheckbox.isSelected() )
        {
            shell.setDebug(true);
            debugCheckbox.setForeground(Color.ORANGE);
            buttonForegroundColor = Color.red;
            desktopLabel.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED), "Desktop", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.BELOW_TOP, new java.awt.Font("STHeiti", 0, 18), buttonForegroundColor)); // NOI18N
            clientLabel.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED), "Client", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.BELOW_TOP, new java.awt.Font("STHeiti", 0, 18), buttonForegroundColor)); // NOI18N
            serverLabel.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED), "Server", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.BELOW_TOP, new java.awt.Font("STHeiti", 0, 18), buttonForegroundColor)); // NOI18N
            pollerLabel.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED), "Poller", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.BELOW_TOP, new java.awt.Font("STHeiti", 0, 18), buttonForegroundColor)); // NOI18N
        }
        else
        { 
            shell.setDebug(false);
            debugCheckbox.setForeground(Color.LIGHT_GRAY);
            buttonForegroundColor = Color.white;
            desktopLabel.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED), "Desktop", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.BELOW_TOP, new java.awt.Font("STHeiti", 0, 18), buttonForegroundColor)); // NOI18N
            clientLabel.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED), "Client", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.BELOW_TOP, new java.awt.Font("STHeiti", 0, 18), buttonForegroundColor)); // NOI18N
            serverLabel.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED), "Server", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.BELOW_TOP, new java.awt.Font("STHeiti", 0, 18), buttonForegroundColor)); // NOI18N
            pollerLabel.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED), "Poller", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.BELOW_TOP, new java.awt.Font("STHeiti", 0, 18), buttonForegroundColor)); // NOI18N
        }
    }//GEN-LAST:event_debugCheckboxActionPerformed

    private void serverFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_serverFieldKeyReleased
        serverFieldChanged = true;
        if ( ( evt.getKeyCode() == 10 ) && (! serverField.getText().equals(serverFieldExample)) )
        {
            dcmStarterReference = this;
            Thread blinkServerWaitWarningThread = new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    if (serverFieldChanged)
                    {
            //            dcmStarterRef.dropWindow();
                        if (serverLabel.isEnabled())
                        {
                            if (!file.exists()) { blinkSloganWaitWarning("Server Initiating", 10); } else { blinkSloganWaitWarning("Server Starting", 4); }
                            String daemonParamString = ""; if (daemonsVisibleBox.isSelected()) { daemonParamString = ""; } else { daemonParamString = " --daemon "; }
                            shell.startServer(256, "-server -Djava.rmi.server.hostname=" + serverField.getText(), Integer.parseInt(portField.getText()),daemonParamString);
                        }
                    }
                    else { blinkServerFields(2); }
                }
            });
            blinkServerWaitWarningThread.setName("blinkClientWaitWarningThread");
            blinkServerWaitWarningThread.setDaemon(false);
            blinkServerWaitWarningThread.setPriority(Thread.NORM_PRIORITY);
            blinkServerWaitWarningThread.start();                                           
        }
    }//GEN-LAST:event_serverFieldKeyReleased

    private void websiteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_websiteButtonActionPerformed
        try { java.awt.Desktop.getDesktop().browse(java.net.URI.create("http://sites.google.com/site/ronuitzaandam/")); } catch (IOException ex) { }
    }//GEN-LAST:event_websiteButtonActionPerformed

    private void languageBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_languageBoxActionPerformed
        languageSelected();
    }//GEN-LAST:event_languageBoxActionPerformed

    private void serverLabelMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_serverLabelMouseClicked
    {//GEN-HEADEREND:event_serverLabelMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_serverLabelMouseClicked

    private void dropWindow()
    {
        Thread dropWindowThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                System.exit(0);
//                setState ( Frame.ICONIFIED );
                
            }
        });
        dropWindowThread.setName("dropWindowThread");
        dropWindowThread.setDaemon(false);
        dropWindowThread.setPriority(Thread.NORM_PRIORITY);
        dropWindowThread.start();                    
    }
    
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(DCMStarter.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DCMStarter.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DCMStarter.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DCMStarter.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new DCMStarter().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel backgroundLabel;
    private javax.swing.JLabel clientLabel;
    private javax.swing.JCheckBox daemonsVisibleBox;
    private javax.swing.JCheckBox debugCheckbox;
    private javax.swing.JLabel desktopLabel;
    private javax.swing.JLabel headerLabel;
    private javax.swing.JTextArea infoArea;
    private javax.swing.JScrollPane infoScroller;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JComboBox languageBox;
    private javax.swing.JLabel pollerLabel;
    private javax.swing.JTextField portField;
    private javax.swing.JTextField serverField;
    private javax.swing.JLabel serverLabel;
    private javax.swing.JLabel sloganLabel;
    private javax.swing.JButton websiteButton;
    // End of variables declaration//GEN-END:variables
}
