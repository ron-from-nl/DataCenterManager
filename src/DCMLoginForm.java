import data.MD5Converter;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class DCMLoginForm extends javax.swing.JFrame
{
    String hostname;
    DCMDesktop dcManager;

    public DCMLoginForm(DCMDesktop dcManagerParam) throws UnsupportedLookAndFeelException
    {
        dcManager = dcManagerParam;
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
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        windowPanel = new javax.swing.JPanel();
        loginPanel = new javax.swing.JPanel();
        usernameField = new javax.swing.JTextField();
        passwordField = new javax.swing.JPasswordField();
        userLabel = new javax.swing.JLabel();
        passwordLabel = new javax.swing.JLabel();
        loginButton = new javax.swing.JButton();
        connectLabel = new javax.swing.JLabel();
        statusLabel = new javax.swing.JLabel();

        setAlwaysOnTop(true);
        setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        setResizable(false);

        windowPanel.setBackground(new java.awt.Color(153, 153, 153));
        windowPanel.setFont(windowPanel.getFont());

        loginPanel.setBackground(new java.awt.Color(153, 153, 153));
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("DCMLoginForm"); // NOI18N
        loginPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, bundle.getString("DCMLoginForm.loginPanel.border.title"), javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Lucida Grande", 0, 12), new java.awt.Color(51, 51, 51))); // NOI18N
        loginPanel.setFont(loginPanel.getFont());

        usernameField.setBackground(new java.awt.Color(204, 204, 204));
        usernameField.setFont(usernameField.getFont().deriveFont(usernameField.getFont().getSize()-1f));
        usernameField.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        usernameField.setText(bundle.getString("DCMLoginForm.usernameField.text")); // NOI18N
        usernameField.addKeyListener(new java.awt.event.KeyAdapter()
        {
            public void keyReleased(java.awt.event.KeyEvent evt)
            {
                usernameFieldKeyReleased(evt);
            }
        });

        passwordField.setBackground(new java.awt.Color(204, 204, 204));
        passwordField.setFont(passwordField.getFont().deriveFont(passwordField.getFont().getSize()-1f));
        passwordField.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        passwordField.setToolTipText(bundle.getString("DCMLoginForm.passwordField.toolTipText")); // NOI18N
        passwordField.setFocusCycleRoot(true);
        passwordField.addKeyListener(new java.awt.event.KeyAdapter()
        {
            public void keyReleased(java.awt.event.KeyEvent evt)
            {
                passwordFieldKeyReleased(evt);
            }
        });

        userLabel.setFont(userLabel.getFont().deriveFont(userLabel.getFont().getSize()-1f));
        userLabel.setForeground(new java.awt.Color(51, 51, 51));
        userLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        userLabel.setText(bundle.getString("DCMLoginForm.userLabel.text")); // NOI18N

        passwordLabel.setFont(passwordLabel.getFont().deriveFont(passwordLabel.getFont().getSize()-1f));
        passwordLabel.setForeground(new java.awt.Color(51, 51, 51));
        passwordLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        passwordLabel.setText(bundle.getString("DCMLoginForm.passwordLabel.text")); // NOI18N

        loginButton.setFont(loginButton.getFont().deriveFont(loginButton.getFont().getSize()+1f));
        loginButton.setText(bundle.getString("DCMLoginForm.loginButton.text")); // NOI18N
        loginButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                loginButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout loginPanelLayout = new org.jdesktop.layout.GroupLayout(loginPanel);
        loginPanel.setLayout(loginPanelLayout);
        loginPanelLayout.setHorizontalGroup(
            loginPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(loginPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(loginPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, loginButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(loginPanelLayout.createSequentialGroup()
                        .add(loginPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(usernameField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE)
                            .add(userLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(loginPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(loginPanelLayout.createSequentialGroup()
                                .add(passwordLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(7, 7, 7))
                            .add(passwordField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 230, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );

        loginPanelLayout.linkSize(new java.awt.Component[] {passwordField, usernameField}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        loginPanelLayout.setVerticalGroup(
            loginPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(loginPanelLayout.createSequentialGroup()
                .add(loginPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(userLabel)
                    .add(passwordLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(loginPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(usernameField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(passwordField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(loginButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 41, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        loginPanelLayout.linkSize(new java.awt.Component[] {passwordField, usernameField}, org.jdesktop.layout.GroupLayout.VERTICAL);

        connectLabel.setBackground(new java.awt.Color(51, 51, 51));
        connectLabel.setFont(connectLabel.getFont().deriveFont(connectLabel.getFont().getSize()+5f));
        connectLabel.setForeground(new java.awt.Color(204, 204, 204));
        connectLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        connectLabel.setText(bundle.getString("DCMLoginForm.connectLabel.text")); // NOI18N
        connectLabel.setOpaque(true);

        statusLabel.setFont(statusLabel.getFont().deriveFont(statusLabel.getFont().getSize()-2f));
        statusLabel.setForeground(new java.awt.Color(51, 51, 51));
        statusLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusLabel.setText(bundle.getString("DCMLoginForm.statusLabel.text")); // NOI18N
        statusLabel.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED, new java.awt.Color(204, 204, 204), new java.awt.Color(51, 51, 51)));

        org.jdesktop.layout.GroupLayout windowPanelLayout = new org.jdesktop.layout.GroupLayout(windowPanel);
        windowPanel.setLayout(windowPanelLayout);
        windowPanelLayout.setHorizontalGroup(
            windowPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(connectLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(loginPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, statusLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 492, Short.MAX_VALUE)
        );
        windowPanelLayout.setVerticalGroup(
            windowPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(windowPanelLayout.createSequentialGroup()
                .add(connectLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 34, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(loginPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(statusLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(windowPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(windowPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void loginButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loginButtonActionPerformed
        login();
}//GEN-LAST:event_loginButtonActionPerformed

    private void usernameFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_usernameFieldKeyReleased
        if ( evt.getKeyCode() == 10 )
        {
            login();
        }
    }//GEN-LAST:event_usernameFieldKeyReleased

    private void passwordFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_passwordFieldKeyReleased
        if ( evt.getKeyCode() == 10 )
        {
            login();
        }
    }//GEN-LAST:event_passwordFieldKeyReleased

    public void resetPassword()
    {
        passwordField.setText("");
//        toFront();
//        setState(JFrame.ICONIFIED);        
//        setState(JFrame.NORMAL);        
//        repaint();
    }
    
    private void login()
    {
        if ((usernameField.getText().length() > 0) && (new String(passwordField.getPassword()).length()>0))
        {
            dcManager.continueAfterLogin(usernameField.getText(), MD5Converter.getMD5SumFromString(new String(passwordField.getPassword())));
        }
        else
        {
            status("Please fillin all fields...");
        }
    
    }
    
    public void status(String status) { statusLabel.setText(status); }
    
    public void close()
    {
        close();
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel connectLabel;
    private javax.swing.JButton loginButton;
    private javax.swing.JPanel loginPanel;
    private javax.swing.JPasswordField passwordField;
    private javax.swing.JLabel passwordLabel;
    private javax.swing.JLabel statusLabel;
    private javax.swing.JLabel userLabel;
    private javax.swing.JTextField usernameField;
    private javax.swing.JPanel windowPanel;
    // End of variables declaration//GEN-END:variables
}
