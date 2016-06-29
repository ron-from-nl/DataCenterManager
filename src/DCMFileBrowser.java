import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;

public class DCMFileBrowser extends javax.swing.JFrame {

    private String platform;
    private String fileSeparator;
    private File file;
    private File directory;
    private DCMAddServerForm addServerForm;
    private DCMExtensionFilter extensionFilter;

    public DCMFileBrowser() {
        initComponents();
        platform = "";
        fileSeparator = "";
        Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
        int winWidth = (int)getWidth();
        int winHeight = (int)getHeight();
        int posX = Math.round((screenDim.width / 2) - (winWidth / 2));
        int posY = Math.round((screenDim.height / 2) - (winHeight / 2));
        setLocation(posX, posY);

        file = new File("");
        directory = new File("");
        platform = System.getProperty("os.name").toLowerCase();
        if ( platform.indexOf("windows") != -1 ) { fileSeparator = "\\"; } else { fileSeparator = "/"; }
//        try { directory = new File(new File(".").getCanonicalPath() + fileSeparator + "data" + fileSeparator + "sounds"); } catch (IOException ex) { }
        try { directory = new File(new File(".").getCanonicalPath()); } catch (IOException ex) { }
    }

        public DCMFileBrowser(DCMAddServerForm addServerFormParam) {
        this();
        addServerForm = addServerFormParam;
        fileChooser.setCurrentDirectory(directory);
        fileChooser.setMultiSelectionEnabled(false);
        extensionFilter = new DCMExtensionFilter(".csv","Comma Separated Format (*.csv)");
        fileChooser.addChoosableFileFilter(extensionFilter);
        fileChooser.setFileFilter(extensionFilter);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        fileChooser = new javax.swing.JFileChooser();

        setAlwaysOnTop(true);

        fileChooser.setDialogTitle("Select Host ImportFile (*.csv)");
        fileChooser.setFont(new java.awt.Font("STHeiti", 0, 10));
        fileChooser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileChooserActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, fileChooser, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 570, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(fileChooser, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void fileChooserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileChooserActionPerformed
        addServerForm.setImportFile(fileChooser.getCurrentDirectory() + fileSeparator + fileChooser.getName(fileChooser.getSelectedFile()));
        setVisible(false);
    }//GEN-LAST:event_fileChooserActionPerformed

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new DCMFileBrowser().setVisible(true);
            }
        });
    }
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JFileChooser fileChooser;
    // End of variables declaration//GEN-END:variables

}
