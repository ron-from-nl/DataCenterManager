import java.awt.Color;
import javax.swing.JTextPane;
import javax.swing.text.*;

public class DCMColorPane extends JTextPane
{
    private StyleContext styleContext;
    private AttributeSet attributeSet;
    
    public DCMColorPane()
    {
        setContentType("text/html");
        styleContext = StyleContext.getDefaultStyleContext();
        
        // Create and add the main document style
        Style defaultStyle = styleContext.getStyle(StyleContext.DEFAULT_STYLE);
        final Style mainStyle = styleContext.addStyle("MainStyle", defaultStyle);
        StyleConstants.setLeftIndent(mainStyle, 16);
        StyleConstants.setRightIndent(mainStyle, 16);
        StyleConstants.setFirstLineIndent(mainStyle, 16);
        StyleConstants.setFontFamily(mainStyle, "serif");
        StyleConstants.setFontSize(mainStyle, 12);
    }
    
    public synchronized void append(Color color, String message)
    {
        attributeSet = styleContext.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, color);
        setCharacterAttributes(attributeSet, true);
        
        setCaretPosition(getDocument().getLength());
        replaceSelection(message);
    }
}
