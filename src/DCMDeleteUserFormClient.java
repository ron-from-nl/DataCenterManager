import data.DCMUser;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class DCMDeleteUserFormClient extends javax.swing.JFrame
{
    DCMUser dcmUser;
    DCMClient dcManagerClient;

    public DCMDeleteUserFormClient(DCMClient dcManagerParam) throws UnsupportedLookAndFeelException
    {
        dcManagerClient = dcManagerParam;
        dcmUser = new DCMUser();
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
    private void initComponents() {

        headerLabel = new javax.swing.JLabel();
        deletePanel = new javax.swing.JPanel();
        hostLabel = new javax.swing.JLabel();
        usernameLabel = new javax.swing.JLabel();
        deleteButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setAlwaysOnTop(true);
        setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        setResizable(false);

        headerLabel.setBackground(new java.awt.Color(51, 51, 51));
        headerLabel.setFont(headerLabel.getFont().deriveFont(headerLabel.getFont().getSize()+5f));
        headerLabel.setForeground(new java.awt.Color(204, 204, 204));
        headerLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("DCMDeleteUserFormClient"); // NOI18N
        headerLabel.setText(bundle.getString("DCMDeleteUserFormClient.headerLabel.text")); // NOI18N
        headerLabel.setOpaque(true);

        deletePanel.setBackground(new java.awt.Color(153, 153, 153));
        deletePanel.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        deletePanel.setFont(deletePanel.getFont());

        hostLabel.setFont(hostLabel.getFont().deriveFont(hostLabel.getFont().getSize()+1f));
        hostLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        hostLabel.setText(bundle.getString("DCMDeleteUserFormClient.hostLabel.text")); // NOI18N

        usernameLabel.setBackground(new java.awt.Color(204, 204, 204));
        usernameLabel.setFont(usernameLabel.getFont().deriveFont(usernameLabel.getFont().getSize()+1f));
        usernameLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        usernameLabel.setOpaque(true);

        deleteButton.setFont(deleteButton.getFont().deriveFont(deleteButton.getFont().getSize()+1f));
        deleteButton.setText(bundle.getString("DCMDeleteUserFormClient.deleteButton.text")); // NOI18N
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });

        cancelButton.setFont(cancelButton.getFont().deriveFont(cancelButton.getFont().getSize()+1f));
        cancelButton.setText(bundle.getString("DCMDeleteUserFormClient.cancelButton.text")); // NOI18N
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout deletePanelLayout = new org.jdesktop.layout.GroupLayout(deletePanel);
        deletePanel.setLayout(deletePanelLayout);
        deletePanelLayout.setHorizontalGroup(
            deletePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(deletePanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(deletePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(deletePanelLayout.createSequentialGroup()
                        .add(cancelButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(deleteButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 417, Short.MAX_VALUE))
                    .add(deletePanelLayout.createSequentialGroup()
                        .add(hostLabel)
                        .add(18, 18, 18)
                        .add(usernameLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 175, Short.MAX_VALUE)))
                .addContainerGap())
        );
        deletePanelLayout.setVerticalGroup(
            deletePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(deletePanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(deletePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(hostLabel)
                    .add(usernameLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(45, 45, 45)
                .add(deletePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(deleteButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 56, Short.MAX_VALUE)
                    .add(cancelButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 56, Short.MAX_VALUE))
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(headerLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 551, Short.MAX_VALUE)
            .add(deletePanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(headerLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 34, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(deletePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        setVisible(false);
        try { dcManagerClient.deleteDCMUserByForm(dcmUser.getId()); } catch (CloneNotSupportedException ex) {}
}//GEN-LAST:event_deleteButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        setVisible(false);
        dcManagerClient.deleteServerCanceled();
}//GEN-LAST:event_cancelButtonActionPerformed

    protected void setDCMUser(DCMUser dcmUserParam)
    {
        dcmUser = dcmUserParam;
        usernameLabel.setText(dcmUser.getUsername());
    }
    
    public void close()
    {
        close();
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton deleteButton;
    private javax.swing.JPanel deletePanel;
    private javax.swing.JLabel headerLabel;
    private javax.swing.JLabel hostLabel;
    private javax.swing.JLabel usernameLabel;
    // End of variables declaration//GEN-END:variables
}
