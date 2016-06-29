import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.ArrayList;
import javax.swing.JFrame;

public class DCMSendMessage extends javax.swing.JFrame
{
    private DCMVergunning       dcmVergunning;
    private String              subject;
    private String              message;
    private DCMSendMessage ref;
    private boolean sloganWaitWarningBlinking = false;
    private final String dcmFirsttimePassedFile;

    public DCMSendMessage()
    {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        }
        catch (ClassNotFoundException ex) { }
        catch (InstantiationException ex) { }
        catch (IllegalAccessException ex) { }
        catch (javax.swing.UnsupportedLookAndFeelException ex) {   }
        initComponents();
        Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
        int winWidth = (int)getWidth();
        int winHeight = (int)getHeight();
        int posX = Math.round((screenDim.width / 2) - (winWidth / 2));
        int posY = Math.round((screenDim.height / 2) - (winHeight / 2));
        setLocation(posX, posY);
        dcmVergunning = new DCMVergunning(false);
//        explanationLabel.setText("<HTML>In return to your Free Server License we promise you that we only once, send the exact below message!</HTML>");
        subject = DCMLicense.getProcuct() + " " + DCMLicense.getVersion() + " Runtime Environment";
        message = "Runtime Environment: " + dcmVergunning.getVergunningPeriod() + " Free " + dcmVergunning.getServersInLicense() + " Server License.\n\n";

        ArrayList<String[]> staticPropertiesList = getJVMStaticPropertiesList();
        for (String[] propertyArray:staticPropertiesList) { message += propertyArray[0] + " " + propertyArray[1] + "\n"; }
        message += "\n";
        message += "Thank you for your cooperation and enjoy " + DCMLicense.getProcuct() + " " + DCMLicense.getVersion() + "\n\n";
        message += "Kind Regards,\n\n";
        message += "The " + DCMLicense.getProcuct()+ " Team\n";
        subjectField.setText(subject);
        messagePane.setText(message);
        dcmFirsttimePassedFile =   ".dcmfirsttimepassed";
        
        ref = this;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        helpdeskPanel = new javax.swing.JPanel();
        subjectField = new javax.swing.JTextField();
        messageScroller = new javax.swing.JScrollPane();
        messagePane = new javax.swing.JEditorPane();
        subjectLabel = new javax.swing.JLabel();
        mainLabel = new javax.swing.JLabel();
        explanationLabel = new javax.swing.JLabel();
        agreeButton = new javax.swing.JButton();
        disagreeButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setAlwaysOnTop(true);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowIconified(java.awt.event.WindowEvent evt) {
                formWindowIconified(evt);
            }
        });

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("DCMSendMessage"); // NOI18N
        helpdeskPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, bundle.getString("DCMSendMessage.helpdeskPanel.border.title"), javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Lucida Grande", 0, 14), new java.awt.Color(51, 51, 51))); // NOI18N
        helpdeskPanel.setFont(helpdeskPanel.getFont());
        helpdeskPanel.setMaximumSize(new java.awt.Dimension(500, 32767));
        helpdeskPanel.setPreferredSize(new java.awt.Dimension(400, 424));
        helpdeskPanel.setSize(new java.awt.Dimension(600, 0));

        subjectField.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        subjectField.setForeground(new java.awt.Color(102, 102, 102));
        subjectField.setToolTipText(bundle.getString("DCMSendMessage.subjectField.toolTipText")); // NOI18N

        messagePane.setFont(new java.awt.Font("Courier New", 0, 10)); // NOI18N
        messagePane.setForeground(new java.awt.Color(102, 102, 102));
        messageScroller.setViewportView(messagePane);

        subjectLabel.setFont(subjectLabel.getFont().deriveFont(subjectLabel.getFont().getSize()-2f));
        subjectLabel.setForeground(new java.awt.Color(102, 102, 102));
        subjectLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        subjectLabel.setText(bundle.getString("DCMSendMessage.subjectLabel.text")); // NOI18N

        org.jdesktop.layout.GroupLayout helpdeskPanelLayout = new org.jdesktop.layout.GroupLayout(helpdeskPanel);
        helpdeskPanel.setLayout(helpdeskPanelLayout);
        helpdeskPanelLayout.setHorizontalGroup(
            helpdeskPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(helpdeskPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(helpdeskPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(messageScroller)
                    .add(helpdeskPanelLayout.createSequentialGroup()
                        .add(subjectLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(subjectField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 594, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        helpdeskPanelLayout.setVerticalGroup(
            helpdeskPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(helpdeskPanelLayout.createSequentialGroup()
                .add(helpdeskPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(subjectField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(subjectLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(messageScroller, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 316, Short.MAX_VALUE)
                .addContainerGap())
        );

        mainLabel.setBackground(new java.awt.Color(102, 102, 102));
        mainLabel.setFont(mainLabel.getFont().deriveFont(mainLabel.getFont().getSize()+10f));
        mainLabel.setForeground(new java.awt.Color(204, 204, 204));
        mainLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        mainLabel.setText(bundle.getString("DCMSendMessage.mainLabel.text")); // NOI18N
        mainLabel.setOpaque(true);

        explanationLabel.setFont(explanationLabel.getFont());
        explanationLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        explanationLabel.setText(bundle.getString("DCMSendMessage.explanationLabel.text")); // NOI18N
        explanationLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        explanationLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        explanationLabel.setVerticalTextPosition(javax.swing.SwingConstants.TOP);

        agreeButton.setText(bundle.getString("DCMSendMessage.agreeButton.text")); // NOI18N
        agreeButton.setToolTipText(bundle.getString("DCMSendMessage.agreeButton.toolTipText")); // NOI18N
        agreeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                agreeButtonActionPerformed(evt);
            }
        });

        disagreeButton.setText(bundle.getString("DCMSendMessage.disagreeButton.text")); // NOI18N
        disagreeButton.setToolTipText(bundle.getString("DCMSendMessage.disagreeButton.toolTipText")); // NOI18N
        disagreeButton.setFocusable(false);
        disagreeButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        disagreeButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        disagreeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                disagreeButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .addContainerGap()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(helpdeskPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 692, Short.MAX_VALUE)
                            .add(explanationLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                    .add(layout.createSequentialGroup()
                        .add(188, 188, 188)
                        .add(agreeButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 166, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(disagreeButton)
                        .add(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(layout.createSequentialGroup()
                    .addContainerGap()
                    .add(mainLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 692, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        layout.linkSize(new java.awt.Component[] {agreeButton, disagreeButton}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(48, 48, 48)
                .add(explanationLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 47, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(agreeButton)
                    .add(disagreeButton))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(helpdeskPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 383, Short.MAX_VALUE)
                .addContainerGap())
            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(layout.createSequentialGroup()
                    .addContainerGap()
                    .add(mainLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 34, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(485, Short.MAX_VALUE)))
        );

        layout.linkSize(new java.awt.Component[] {agreeButton, disagreeButton}, org.jdesktop.layout.GroupLayout.VERTICAL);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void blinkLabel(final String message, final int blinkRepeats)
    {
        Thread blinkLabelThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                if (!sloganWaitWarningBlinking)
                {
                    sloganWaitWarningBlinking = true;
                    for (int i=1; i<blinkRepeats; i++)
                    {
                        mainLabel.setText(message + " please wait...");
                        try { Thread.sleep(500); } catch (InterruptedException ex) {  }
                        mainLabel.setText("");
                        try { Thread.sleep(500); } catch (InterruptedException ex) {  }
                    }
                    mainLabel.setText(message + " please wait...");
                    sloganWaitWarningBlinking = false;
                }
            }
        });
        blinkLabelThread.setName("blinkLabelThread");
        blinkLabelThread.setDaemon(false);
        blinkLabelThread.setPriority(Thread.NORM_PRIORITY);
        blinkLabelThread.start();
    }
    
    private void agreeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_agreeButtonActionPerformed
        Thread messThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                agreeButton.setEnabled(false);
                disagreeButton.setEnabled(false);
                mainLabel.setText("Thank you!");
                blinkLabel("Sending message",5);
                new DCMFileWrite(dcmFirsttimePassedFile,"");
                new DCMSendMailTLS(subject,message);
                explanationLabel.setText("Sending message finished!");
                try { Thread.sleep(1000); } catch (InterruptedException ex) {  }
                setState(JFrame.DISPOSE_ON_CLOSE);
                setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                setVisible(false);
                ref.dispose();
            }
        });
        messThread.setName("messThread");
        messThread.setDaemon(false);
        messThread.setPriority(Thread.NORM_PRIORITY);
        messThread.start();
        try { messThread.wait(10000); } catch (InterruptedException ex) {  } // I'm not interrupting are you ?
        setVisible(false);
    }//GEN-LAST:event_agreeButtonActionPerformed

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
        return staticPropertiesList;
    }

    private void disagreeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_disagreeButtonActionPerformed
        Thread messThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                agreeButton.setEnabled(false);
                disagreeButton.setEnabled(false);
                mainLabel.setText("We respect your choice!");
                explanationLabel.setText("Message delete and canceled!");
                try { Thread.sleep(2000); } catch (InterruptedException ex) {  }
                subjectField.setText("");
                messagePane.setText("");
                try { Thread.sleep(3000); } catch (InterruptedException ex) {  }
                System.exit(0);
            }
        });
        messThread.setName("messThread");
        messThread.setDaemon(false);
        messThread.setPriority(Thread.NORM_PRIORITY);
        messThread.start();
    }//GEN-LAST:event_disagreeButtonActionPerformed

    private void formWindowIconified(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowIconified
        Thread minimizeThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                System.exit(0);
            }
        });
        minimizeThread.setName("minimizeThread");
        minimizeThread.setDaemon(false);
        minimizeThread.setPriority(Thread.NORM_PRIORITY);
        minimizeThread.start();
    }//GEN-LAST:event_formWindowIconified

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
        } catch (InstantiationException ex) {
        } catch (IllegalAccessException ex) {
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
        }
        //</editor-fold>

        /*
         * Create and display the form
         */
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new DCMSendMessage().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton agreeButton;
    private javax.swing.JButton disagreeButton;
    private javax.swing.JLabel explanationLabel;
    private javax.swing.JPanel helpdeskPanel;
    private javax.swing.JLabel mainLabel;
    private javax.swing.JEditorPane messagePane;
    private javax.swing.JScrollPane messageScroller;
    private javax.swing.JTextField subjectField;
    private javax.swing.JLabel subjectLabel;
    // End of variables declaration//GEN-END:variables
}
