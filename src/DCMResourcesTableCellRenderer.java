import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class DCMResourcesTableCellRenderer extends DefaultTableCellRenderer
{
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {
        if (value instanceof String)
        {
            setHorizontalAlignment(JLabel.CENTER);
            setValue((String)value);
//            setIcon(null);
        }

        if (value instanceof ImageIcon)
        {
            setHorizontalAlignment(JLabel.CENTER);
            setIcon((ImageIcon)value);
//            setValue("");
        }
        return this;
  }
}
