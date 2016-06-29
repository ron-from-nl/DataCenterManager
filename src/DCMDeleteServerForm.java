import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class DCMDeleteServerForm extends javax.swing.JFrame
{
    String hostname;
    ArrayList<String> serverList = new ArrayList<String>();
    DCMDesktop dcManager;

    public DCMDeleteServerForm(DCMDesktop dcManagerParam) throws UnsupportedLookAndFeelException
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
    private void initComponents() {

        headerLabel = new javax.swing.JLabel();
        deletePanel = new javax.swing.JPanel();
        hostLabel = new javax.swing.JLabel();
        deleteArchives = new javax.swing.JCheckBox();
        deleteButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        serverListScroller = new javax.swing.JScrollPane();
        serverListArea = new javax.swing.JTextArea();

        setAlwaysOnTop(true);
        setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        setResizable(false);

        headerLabel.setBackground(new java.awt.Color(51, 51, 51));
        headerLabel.setFont(headerLabel.getFont().deriveFont(headerLabel.getFont().getSize()+5f));
        headerLabel.setForeground(new java.awt.Color(204, 204, 204));
        headerLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("DCMDeleteServerForm"); // NOI18N
        headerLabel.setText(bundle.getString("DCMDeleteServerForm.headerLabel.text")); // NOI18N
        headerLabel.setOpaque(true);

        deletePanel.setBackground(new java.awt.Color(153, 153, 153));
        deletePanel.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        deletePanel.setFont(deletePanel.getFont());

        hostLabel.setFont(hostLabel.getFont().deriveFont(hostLabel.getFont().getSize()+5f));
        hostLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        hostLabel.setText(bundle.getString("DCMDeleteServerForm.hostLabel.text")); // NOI18N

        deleteArchives.setFont(deleteArchives.getFont().deriveFont(deleteArchives.getFont().getSize()-1f));
        deleteArchives.setForeground(new java.awt.Color(51, 51, 51));
        deleteArchives.setText(bundle.getString("DCMDeleteServerForm.deleteArchives.text")); // NOI18N
        deleteArchives.setToolTipText(bundle.getString("DCMDeleteServerForm.deleteArchives.toolTipText")); // NOI18N

        deleteButton.setFont(deleteButton.getFont().deriveFont(deleteButton.getFont().getSize()+1f));
        deleteButton.setText(bundle.getString("DCMDeleteServerForm.deleteButton.text")); // NOI18N
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });

        cancelButton.setFont(cancelButton.getFont().deriveFont(cancelButton.getFont().getSize()+1f));
        cancelButton.setText(bundle.getString("DCMDeleteServerForm.cancelButton.text")); // NOI18N
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        serverListScroller.setBackground(new java.awt.Color(204, 204, 204));
        serverListScroller.setFont(serverListScroller.getFont());

        serverListArea.setBackground(new java.awt.Color(204, 204, 204));
        serverListArea.setColumns(20);
        serverListArea.setFont(serverListArea.getFont());
        serverListArea.setRows(5);
        serverListScroller.setViewportView(serverListArea);

        org.jdesktop.layout.GroupLayout deletePanelLayout = new org.jdesktop.layout.GroupLayout(deletePanel);
        deletePanel.setLayout(deletePanelLayout);
        deletePanelLayout.setHorizontalGroup(
            deletePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(deletePanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(deletePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, serverListScroller)
                    .add(deletePanelLayout.createSequentialGroup()
                        .add(cancelButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(deleteButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .add(deleteArchives)
                    .add(hostLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        deletePanelLayout.setVerticalGroup(
            deletePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(deletePanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(hostLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(serverListScroller, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(deleteArchives)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(deletePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(cancelButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(deleteButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE))
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(headerLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 576, Short.MAX_VALUE)
            .add(deletePanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(headerLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 34, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(deletePanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        setVisible(false);
        ArrayList<String> serverList = new ArrayList<String>();
        
        BufferedReader reader = new BufferedReader(new StringReader(serverListArea.getText()));
        String line = "";

        try
        {
            while ((line = reader.readLine()) != null)
            {
                if (line.length()>1)
                {
                    serverList.add(line);
                }
            }

        } catch(IOException e) { e.printStackTrace(); }

//        dcManager.deleteServerByForm(deleteArchives.isSelected());
        dcManager.deleteServersByList(serverList, deleteArchives.isSelected());
}//GEN-LAST:event_deleteButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        setVisible(false);
        dcManager.deleteServerCanceled();
}//GEN-LAST:event_cancelButtonActionPerformed
    
    protected void setServerList(ArrayList<String> serverListParam)
    {
        serverListArea.setText("");
        for (String server:serverListParam)
        {
            serverListArea.append(server+"\n");
        }
    }
    
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
    private javax.swing.JTextArea serverListArea;
    private javax.swing.JScrollPane serverListScroller;
    // End of variables declaration//GEN-END:variables
}
