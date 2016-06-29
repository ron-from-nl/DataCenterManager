import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class DCMFileWrite
{
    public DCMFileWrite(String filename, String content)
    {
        FileWriter fileWriter = null;
        File fileToWrite = new File(filename);
        
        try { fileWriter = new FileWriter(fileToWrite); } catch (IOException error) { System.out.println("Error: fileWriter = new FileWriter(\"" + filename + "\"): IOException: " + error.getMessage()); }
        try { fileWriter.write(content.toString()); } catch (IOException error) { System.out.println("saveConfiguration Error: fileWriter.write(dcmPollerScriptFile): IOException: " + error.getMessage()); }
        try { fileWriter.flush(); } catch (IOException error) { System.out.println("saveConfiguration Error: fileWriter.flush(): IOException: " + error.getMessage()); }
    }

}