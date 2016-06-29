import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class DCMDeleteServersForm extends javax.swing.JFrame
{
    String hostname;
    DCMDesktop dcmDesktop;

    public DCMDeleteServersForm(DCMDesktop dcManagerParam) throws UnsupportedLookAndFeelException
    {
        dcmDesktop = dcManagerParam;
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
        hostnameLabel = new javax.swing.JLabel();
        deleteArchives = new javax.swing.JCheckBox();
        deleteButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setAlwaysOnTop(true);
        setResizable(false);

        headerLabel.setBackground(new java.awt.Color(51, 51, 51));
        headerLabel.setFont(new java.awt.Font("STHeiti", 0, 18)); // NOI18N
        headerLabel.setForeground(new java.awt.Color(204, 204, 204));
        headerLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("DCMDeleteServersForm"); // NOI18N
        headerLabel.setText(bundle.getString("DCMDeleteServersForm.headerLabel.text")); // NOI18N
        headerLabel.setOpaque(true);

        deletePanel.setBackground(new java.awt.Color(153, 153, 153));
        deletePanel.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        hostLabel.setFont(hostLabel.getFont().deriveFont(hostLabel.getFont().getSize()+1f));
        hostLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        hostLabel.setText(bundle.getString("DCMDeleteServersForm.hostLabel.text")); // NOI18N

        hostnameLabel.setBackground(new java.awt.Color(255, 0, 0));
        hostnameLabel.setFont(hostnameLabel.getFont().deriveFont(hostnameLabel.getFont().getSize()+1f));
        hostnameLabel.setForeground(new java.awt.Color(255, 255, 255));
        hostnameLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        hostnameLabel.setText(bundle.getString("DCMDeleteServersForm.hostnameLabel.text")); // NOI18N
        hostnameLabel.setOpaque(true);

        deleteArchives.setFont(deleteArchives.getFont().deriveFont(deleteArchives.getFont().getSize()-1f));
        deleteArchives.setForeground(new java.awt.Color(51, 51, 51));
        deleteArchives.setText(bundle.getString("DCMDeleteServersForm.deleteArchives.text")); // NOI18N
        deleteArchives.setToolTipText(bundle.getString("DCMDeleteServersForm.deleteArchives.toolTipText")); // NOI18N

        deleteButton.setFont(deleteButton.getFont().deriveFont(deleteButton.getFont().getSize()+1f));
        deleteButton.setText(bundle.getString("DCMDeleteServersForm.deleteButton.text")); // NOI18N
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });

        cancelButton.setFont(cancelButton.getFont().deriveFont(cancelButton.getFont().getSize()+1f));
        cancelButton.setText(bundle.getString("DCMDeleteServersForm.cancelButton.text")); // NOI18N
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
                    .add(deleteArchives)
                    .add(deletePanelLayout.createSequentialGroup()
                        .add(cancelButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(deleteButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 437, Short.MAX_VALUE))
                    .add(deletePanelLayout.createSequentialGroup()
                        .add(hostLabel)
                        .add(18, 18, 18)
                        .add(hostnameLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 294, Short.MAX_VALUE)))
                .addContainerGap())
        );
        deletePanelLayout.setVerticalGroup(
            deletePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(deletePanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(deletePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(hostLabel)
                    .add(hostnameLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(18, 18, 18)
                .add(deleteArchives)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
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
        try { dcmDesktop.deleteServersByForm(deleteArchives.isSelected()); } catch (CloneNotSupportedException ex) {   }
}//GEN-LAST:event_deleteButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        setVisible(false);
        dcmDesktop.deleteServerCanceled();
}//GEN-LAST:event_cancelButtonActionPerformed

    public void close()
    {
        close();
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JCheckBox deleteArchives;
    private javax.swing.JButton deleteButton;
    private javax.swing.JPanel deletePanel;
    private javax.swing.JLabel headerLabel;
    private javax.swing.JLabel hostLabel;
    private javax.swing.JLabel hostnameLabel;
    // End of variables declaration//GEN-END:variables
}
