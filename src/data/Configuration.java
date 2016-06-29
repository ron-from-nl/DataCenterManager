package data;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

public class Configuration implements Cloneable
{
    public  static final   boolean DEBUG = true; // Basically prevents standard input/output (STDOUT) redirection to be disabled
    public  static final int FREEYEAR = 2011;
    private static String  dataDir;
    private static String  databasesDir;
    private static String  backupDir;
    private static String  javadb;
    private static String  archiveDBDir;
    private static String  xmlConfigDir =  dataDir + "config/";
    private static String  xmlFileName;
    private static String  xmlFileBase;
    private static String  xmlFileExtention;
    private static String  domain;
    private static String  toegang;
    private static String  register;
    private static String[] status;
    public static final String WEBLINK = "http://www.voipstorm.nl/";
    
    private ConfigurationCaller configurationCaller;

    DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = null;
    Document xmlDocument = null;
    private  String platform;
    private  String fileSeparator;
    private  String lineTerminator;
    private  String soundsDir;
    private  String scriptsDir;
    private  String imagesDir;
    private  String vergunningDir;
    private  String configDir;
    private  String logDir;
    private  String binDir;
    private  File file;

    public Configuration(ConfigurationCaller configurationCallerParam)
    {
        configurationCaller = configurationCallerParam;
        platform = System.getProperty("os.name").toLowerCase();
        if ( platform.indexOf("windows") != -1 ) { fileSeparator = "\\"; lineTerminator = "\r\n"; } else { fileSeparator = "/"; lineTerminator = "\r\n"; }

        dataDir =       "data" + fileSeparator;
        databasesDir =  dataDir + "databases" + fileSeparator;
        backupDir =     dataDir + "backup" + fileSeparator;
        javadb =        "DCMMetaDB";
        archiveDBDir =  databasesDir + "DCMArchiveDB" + fileSeparator;
        soundsDir =     dataDir + "sounds" + fileSeparator;
        scriptsDir =    dataDir + "scripts" + fileSeparator;
        imagesDir =     dataDir + "images" + fileSeparator;
        vergunningDir = dataDir + "license" + fileSeparator;
        configDir =     dataDir + "config" + fileSeparator;
        binDir =        dataDir + "bin" + fileSeparator;
        logDir =        dataDir + "log" + fileSeparator;

        boolean missingDirsDetected = false;
        boolean missingCriticalDirsDetected = false;
        file = new File(dataDir);       if (!file.exists()) { if (new File(dataDir).mkdir())        { missingDirsDetected = true; configurationCallerParam.log("Action:  Config: Creating missing directory: " + dataDir, false, true, false); } }
        file = new File(logDir);        if (!file.exists()) { if (new File(logDir).mkdir())         { missingDirsDetected = true; configurationCallerParam.log("Action:  Config: Creating missing directory: " + logDir, false, true, false); } }
        file = new File(soundsDir);     if (!file.exists()) { if (new File(soundsDir).mkdir())      { missingDirsDetected = true; configurationCallerParam.log("Action:  Config: Creating missing directory: " + soundsDir, false, true, false); missingCriticalDirsDetected = true; } }
        file = new File(scriptsDir);    if (!file.exists()) { if (new File(scriptsDir).mkdir())     { missingDirsDetected = true; configurationCallerParam.log("Action:  Config: Creating missing directory: " + scriptsDir, false, true, false); missingCriticalDirsDetected = true; } }
        file = new File(imagesDir);     if (!file.exists()) { if (new File(imagesDir).mkdir())      { missingDirsDetected = true; configurationCallerParam.log("Action:  Config: Creating missing directory: " + imagesDir, false, true, false); missingCriticalDirsDetected = true; } }
        file = new File(vergunningDir); if (!file.exists()) { if (new File(vergunningDir).mkdir())  { missingDirsDetected = true; configurationCallerParam.log("Action:  Config: Creating missing directory: " + vergunningDir, false, true, false); } }
        file = new File(databasesDir);  if (!file.exists()) { if (new File(databasesDir).mkdir())   { missingDirsDetected = true; configurationCallerParam.log("Action:  Config: Creating missing directory: " + databasesDir, false, true, false); } }
        file = new File(backupDir);     if (!file.exists()) { if (new File(backupDir).mkdir())      { missingDirsDetected = true; configurationCallerParam.log("Action:  Config: Creating missing directory: " + backupDir, false, true, false); } }
        file = new File(archiveDBDir);  if (!file.exists()) { if (new File(archiveDBDir).mkdir())   { missingDirsDetected = true; configurationCallerParam.log("Action:  Config: Creating missing directory: " + archiveDBDir, false, true, false); } }
        file = new File(configDir);     if (!file.exists()) { if (new File(configDir).mkdir())      { missingDirsDetected = true; configurationCallerParam.log("Action:  Config: Creating missing directory: " + configDir, false, true, false); } }
        file = new File(binDir);        if (!file.exists()) { if (new File(binDir).mkdir())         { missingDirsDetected = true; configurationCallerParam.log("Action:  Config: Creating missing directory: " + binDir, false, true, false); missingCriticalDirsDetected = true; } }
        if ( missingCriticalDirsDetected )  { configurationCallerParam.log("Status:  Config: Critical directories were missing !", false, true, false); try { Thread.sleep(4000); } catch (InterruptedException ex) { } }
        if ( missingDirsDetected )          { configurationCallerParam.log("Success: Config: DCM directory structure built", false, true, false); try { Thread.sleep(1000); } catch (InterruptedException ex) { } }

        xmlFileName         = "";
        xmlFileBase         = "network";
        xmlFileExtention    = ".xml";
        domain              = "voipstorm.nl";
        register            = "0";
        status              = new String[2];
    }

    // a full Constructor
    public Configuration(   
                            ConfigurationCaller configurationCallerParam,
                            String  domainParam,
                            String  registerParam
                        )
                        {
                            this(configurationCallerParam);
                            domain         = domainParam;
                            register       = registerParam;
                        }

    public Configuration()
    {
        platform = System.getProperty("os.name").toLowerCase();
        if ( platform.indexOf("windows") != -1 ) { fileSeparator = "\\"; lineTerminator = "\r\n"; } else { fileSeparator = "/"; lineTerminator = "\r\n"; }

        dataDir =       "data" + fileSeparator;
        databasesDir =  dataDir + "databases" + fileSeparator;
        backupDir =     dataDir + "backup" + fileSeparator;
        javadb =        "DCMMetaDB";
        archiveDBDir =       databasesDir + "DCMArchiveDB" + fileSeparator;
        soundsDir =     dataDir + "sounds" + fileSeparator;
        scriptsDir =    dataDir + "scripts" + fileSeparator;
        imagesDir =     dataDir + "images" + fileSeparator;
        vergunningDir = dataDir + "license" + fileSeparator;
        configDir =     dataDir + "config" + fileSeparator;
        binDir =        dataDir + "bin" + fileSeparator;
        logDir =        dataDir + "log" + fileSeparator;

        boolean missingDirsDetected = false;
        boolean missingCriticalDirsDetected = false;
        file = new File(dataDir);       if (!file.exists()) { if (new File(dataDir).mkdir())        { missingDirsDetected = true; log("Action:  Config: Creating missing directory: " + dataDir); } }
        file = new File(logDir);        if (!file.exists()) { if (new File(logDir).mkdir())         { missingDirsDetected = true; log("Action:  Config: Creating missing directory: " + logDir); } }
        file = new File(soundsDir);     if (!file.exists()) { if (new File(soundsDir).mkdir())      { missingDirsDetected = true; log("Action:  Config: Creating missing directory: " + soundsDir); missingCriticalDirsDetected = true; } }
        file = new File(scriptsDir);    if (!file.exists()) { if (new File(scriptsDir).mkdir())     { missingDirsDetected = true; log("Action:  Config: Creating missing directory: " + scriptsDir); missingCriticalDirsDetected = true; } }
        file = new File(imagesDir);     if (!file.exists()) { if (new File(imagesDir).mkdir())      { missingDirsDetected = true; log("Action:  Config: Creating missing directory: " + imagesDir); missingCriticalDirsDetected = true; } }
        file = new File(vergunningDir); if (!file.exists()) { if (new File(vergunningDir).mkdir())  { missingDirsDetected = true; log("Action:  Config: Creating missing directory: " + vergunningDir); } }
        file = new File(databasesDir);  if (!file.exists()) { if (new File(databasesDir).mkdir())   { missingDirsDetected = true; log("Action:  Config: Creating missing directory: " + databasesDir); } }
        file = new File(backupDir);     if (!file.exists()) { if (new File(backupDir).mkdir())      { missingDirsDetected = true; log("Action:  Config: Creating missing directory: " + backupDir); } }
        file = new File(archiveDBDir);  if (!file.exists()) { if (new File(archiveDBDir).mkdir())   { missingDirsDetected = true; log("Action:  Config: Creating missing directory: " + archiveDBDir); } }
        file = new File(configDir);     if (!file.exists()) { if (new File(configDir).mkdir())      { missingDirsDetected = true; log("Action:  Config: Creating missing directory: " + configDir); } }
        file = new File(binDir);        if (!file.exists()) { if (new File(binDir).mkdir())         { missingDirsDetected = true; log("Action:  Config: Creating missing directory: " + binDir); missingCriticalDirsDetected = true; } }
        if ( missingCriticalDirsDetected )  { log("Critical directories were missing !"); try { Thread.sleep(4000); } catch (InterruptedException ex) { } }
        if ( missingDirsDetected )          { log("DCM directory structure built"); try { Thread.sleep(1000); } catch (InterruptedException ex) { } }

        xmlFileName         = "";
        xmlFileBase         = "network";
        xmlFileExtention    = ".xml";
        domain              = "voipstorm.nl";
        register            = "0";
        status              = new String[2];
    }
    
    public String[] createConfiguration()
    {
        status[0] = "0"; status[1] = "";
        setDomain("");
        setRegister("0");
        return status;
    }
    
    public String getFileSeparator() { return fileSeparator; }

    public String[] loadConfiguration(String configNumberParam) // Loads xmlfile content into attributes
    {
        status[0] = "0"; status[1] = "";
        if ( (configNumberParam != null) && ( ! configNumberParam.equals("")))
        { xmlFileName = xmlConfigDir + xmlFileBase + configNumberParam + xmlFileExtention; }
        else
        { xmlFileName = xmlConfigDir + xmlFileBase + "1" + xmlFileExtention; }

        // Get the configuration from file
        builderFactory = DocumentBuilderFactory.newInstance();
        builder = null;
        xmlDocument = null;
        try { builder = builderFactory.newDocumentBuilder(); }
        catch (ParserConfigurationException error) { status[0] = "1"; status[1] = "loadConfiguration Error: builder = builderFactory.newDocumentBuilder(): ParserConfigurationException: " + error.getMessage(); return status;}

        try { xmlDocument = builder.parse(xmlFileName); }
        catch (SAXException error) { status[0] = "1"; status[1] = "loadConfiguration Error: xmlDocument = builder.parse(xmlFile): SAXException: " + error.getMessage(); return status; }
        catch (IOException error) { status[0] = "1"; status[1] = "loadConfiguration Error: xmlDocument = builder.parse(xmlFile): IOException: " + error.getMessage(); return status; }

        //set up a transformer
        TransformerFactory transfac = TransformerFactory.newInstance();
        Transformer trans = null; try { trans = transfac.newTransformer(); }
        catch (TransformerConfigurationException error) { status[0] = "1"; status[1] = "loadConfiguration Error: trans = transfac.newTransformer(): TransformerConfigurationException: " + error.getMessage(); return status; }
        trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        trans.setOutputProperty(OutputKeys.INDENT, "yes");

        //create string from xml tree
        StringWriter stringWriter = new StringWriter();
        StreamResult result = new StreamResult(stringWriter);
        DOMSource source = new DOMSource(xmlDocument);
        try { trans.transform(source, result); } catch (TransformerException error) { status[0] = "1"; status[1] = "loadConfiguration Error: trans.transform(source, result): TransformerException: " + error.getMessage(); return status; }
        String xmlString = stringWriter.toString();
        //ystem.out.println("loadConfiguration: DOM String\n" + xmlString);

        // Now we have a DOM Tree and a DOM String, now let's turn it into a Configuration Object

        String nodeName, nodeValue;
        Node node = xmlDocument.getFirstChild(); // softphoneconfiguration
        NodeList nodelist = node.getChildNodes(); // all the children that form the configuration nodes

        int nodelistcount = nodelist.getLength();
        for (int i = 1; i < nodelistcount; i = i + 2)
        {
            //ystem.out.println("nodelist item: " + i + " = " + nodelist.item(i).getNodeName() + ": " + nodelist.item(i).getTextContent());
            nodeName = nodelist.item(i).getNodeName();
            nodeValue = nodelist.item(i).getTextContent();

            if      ( nodeName.equals("domain"))         { setDomain         (nodeValue);}
            else if ( nodeName.equals("register"))       { setRegister       (nodeValue);}
        }

        //ystem.out.println("loadConfiguration myConfig.toString()\n" + myConfiguration.toString());
        return status;
    }

    public String[] saveConfiguration(String configNumberParam) // Saves attributes to xmlfile
    {
        status[0] = "0"; status[1] = "";

        // Copy config from UserInterface to this SoftPhone instance

        if ( (configNumberParam != null) && ( ! configNumberParam.equals("")))
        { xmlFileName = xmlConfigDir + xmlFileBase + configNumberParam + xmlFileExtention; }
        else
        { xmlFileName = xmlConfigDir + xmlFileBase + "1" + xmlFileExtention; }

        //Build a DOM from the configuration
        Element root, child;
        Text text;

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = null;
        try { docBuilder = documentBuilderFactory.newDocumentBuilder(); }
        catch (ParserConfigurationException error) { status[0] = "1"; status[1] = "saveConfiguration Error: docBuilder = documentBuilderFactory.newDocumentBuilder(): ParserConfigurationException: " + error.getMessage(); return status; }

        //Creating the XML tree
        xmlDocument = docBuilder.newDocument();

        //create the root element and add it to the document
        root = xmlDocument.createElement("softphoneconfiguration");
        xmlDocument.appendChild(root);

        //create a comment and put it in the root element
        Comment comment = xmlDocument.createComment("SoftPhone Configuration");
        root.appendChild(comment);

        //create child elements, possibly add an attribute, and add to root
        child = xmlDocument.createElement("domain");            text = xmlDocument.createTextNode(getDomain());         child.appendChild(text); root.appendChild(child);
        child = xmlDocument.createElement("register");          text = xmlDocument.createTextNode(getRegister());       child.appendChild(text); root.appendChild(child);
        //child.setAttribute("name", "value"); // attributes not (yet) needed

        //Save the DOM to file

        //set up a transformer
        TransformerFactory transfac = TransformerFactory.newInstance();
        Transformer trans = null; try { trans = transfac.newTransformer(); }
        catch (TransformerConfigurationException error) { status[0] = "1"; status[1] = "saveConfiguration Error: trans = transfac.newTransformer(): TransformerConfigurationException: " + error.getMessage(); return status; }
        trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        trans.setOutputProperty(OutputKeys.INDENT, "yes");

        //create string from xml tree
        StringWriter stringWriter = new StringWriter();
        StreamResult result = new StreamResult(stringWriter);
        DOMSource source = new DOMSource(xmlDocument);
        try { trans.transform(source, result); } catch (TransformerException error) { status[0] = "1"; status[1] = "saveConfiguration Error: trans.transform(source, result): TransformerException: " + error.getMessage(); return status; }
        String xmlString = stringWriter.toString();
        //ystem.out.println("saveConfiguration DOM String: " + xmlString);

        // DOMString is ready, now save it to file
        FileWriter fileWriter = null;
        File fileToWrite = new File(xmlFileName);
        try { fileWriter = new FileWriter(fileToWrite); } catch (IOException error) { status[0] = "1"; status[1] = "saveConfiguration Error: fileWriter = new FileWriter(fileToWrite): IOException: " + error.getMessage(); return status; }
        try { fileWriter.write(xmlString); } catch (IOException error) { status[0] = "1"; status[1] = "saveConfiguration Error: fileWriter.write(xmlString): IOException: " + error.getMessage(); return status; }
        try { fileWriter.flush(); } catch (IOException error) { status[0] = "1"; status[1] = "saveConfiguration Error: fileWriter.flush(): IOException: " + error.getMessage(); return status; }
        return status;
    }

    // Just the getters and setters
    public String  getDomain         ()                             {return domain;}
    public String  getRegister       ()                             {return register;}

    public String  getDataDir        ()                             {return dataDir;}
    public String  getSoundsDir      ()                             {return soundsDir;}
    public String  getScriptsDir     ()                             {return scriptsDir;}
    public String  getImagesDir      ()                             {return imagesDir;}
    public String  getDatabasesDir   ()                             {return databasesDir;}
    public String  getBackupDir      ()                             {return backupDir;}
    public String  getArchiveDBDir   ()                             {return archiveDBDir;}
    public String  getLogDir         ()                             {return logDir;}
    public String  getJavaDB         ()                             {return javadb;}

    public void setDomain                   (String  domainParam)   {domain   = domainParam;}
    public void setRegister                 (String  registerParam) {register = registerParam;}

    @Override
    public String toString()
    {
        String output = null;
        output  = "domain: "                + getDomain()                   + "\n";
        output += "register: "              + getRegister()                 + "\n";

        return output;
    }

    public void log(String messageParam)
    {
        System.out.println(messageParam);
    }

    @Override
    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }
}
