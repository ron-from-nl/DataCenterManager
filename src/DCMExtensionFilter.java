import java.io.File;
import javax.swing.filechooser.FileFilter;

public class DCMExtensionFilter extends FileFilter
{

    private String extension;
    private String description;

    public DCMExtensionFilter(String extensionParam, String descriptionParam)
    {
        extension   = extensionParam.toLowerCase();
        description = descriptionParam;
    }

    @Override
    public boolean accept(File f) {
        return (f.isDirectory()||f.getName().toLowerCase().endsWith(extension));
    }

    @Override
    public String getDescription() {
        return description;
    }

}
