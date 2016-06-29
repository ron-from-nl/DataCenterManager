import data.MD5Converter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Calendar;
import java.util.Enumeration;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

public class DCMVergunning implements Cloneable
{
    private static final int    SECOND                          = 1000;
    private static final int    MINUTE                          = SECOND * 60;
    private static final int    HOUR                            = MINUTE * 60;
    private static final int    DAY                             = HOUR   * 24;
    private static final int    WEEK                            = DAY    * 7;
    private static final int    YEAR                            = DAY    * 365;

    public               int    CALLSPERHOUR_FREE               = 0;

    public static final String BRAND                            = "JavaSoft";
    public static final String BUSINESS                         = "ICT";
    public static final String PRODUCT                          = "DatacenterManager";
    public static final String BRAND_DESCRIPTION                = BRAND + " " + PRODUCT + " offers 21st Century Trend Analysis !";
//    public static final String VERSION                          = "v0.9.7";
    public static final String PRODUCT_DESCRIPTION              = PRODUCT + " is a fully automated datacenter control and performance, trend and scalability analysis software";
    public static final String WEBLINK                          = "http://www.voipstorm.nl/";
    public static final String REQUEST_VERGUNNINGLINK           = "http://www.voipstorm.nl:8080/en/";
    private static final String VERGUNNINGTOEKENNERTOEGANG      = "IsNwtNp4L";
    public static final String WARNING                          = "Please use " + PRODUCT + " software carefully, responsibly and according your country's legislation.";
    public static final String COPYRIGHT                        = "© " + Calendar.getInstance().get(Calendar.YEAR);
    public static final String AUTHOR                           = "Ron de Jong";

    private boolean             debug = false;
    private final static String dataDir= "data/";;
    private final String        xmlVergunningDir= dataDir + "license/";;
    private String              xmlFileName;
    private String              xmlFileBase;
    private String              xmlFileExtention;
    private boolean             vergunningLoaded;
    private boolean             vergunningOrderInProgress;
    private String              activationCodeFromFile;
    private String              vergunningCodeFromFile;
    private String              vergunningNumOfServers;
    private Calendar            vergunningStartCalendar;
    private Calendar            vergunningEndCalendar;
    private Calendar            systemTimeCalendar;
    private Calendar            ntpTimeCalendar;
//    private DCMNTPDate          ntpDate;
    private String              activationCodeFromSystem;
    private String              vergunningCodeFromSystem;
    private boolean             vergunningIsValid;
    private String              vergunningPeriod;
    private final int           FREESERVERSLICENSED = 5;
    private int                 serversLicensed;
    private String[]            status;
    private byte[]              myBytes;
    private String              vergunningInvalidReason = "";
    private String              vergunningInvalidAdvice = "";

    private String              output = "";
    private String              totOutput = "";
    private NetworkInterface    networkInterface;
    private Enumeration         networkInterfaceList;

    DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = null;
    Document xmlDocument = null;
    DCMDesktop dcmDesktop;

    // a empty constructor
    public DCMVergunning(boolean debugParam)
    {
        debug =                     debugParam;
        xmlFileName =               "";
        xmlFileBase =               "license";
        xmlFileExtention =          ".xml";
        activationCodeFromFile =    "";
        vergunningCodeFromFile =    "";
        status =                    new String[2];
        vergunningLoaded =          false;
        vergunningIsValid =         false;
        vergunningOrderInProgress = false;
        vergunningNumOfServers =    "";
        vergunningPeriod =          "UNSET";
        serversLicensed =           FREESERVERSLICENSED;
        vergunningStartCalendar =   Calendar.getInstance();
        vergunningEndCalendar =     Calendar.getInstance();
        systemTimeCalendar =        Calendar.getInstance();
        ntpTimeCalendar =           Calendar.getInstance();

//        ntpDate = new DCMNTPDate();
        status = loadVergunning();
        if (status[0].equals("1"))
        {
            vergunningIsValid =         false;
            serversLicensed =           FREESERVERSLICENSED;                    
            vergunningStartCalendar =   Calendar.getInstance();
            vergunningEndCalendar =     Calendar.getInstance();
            vergunningEndCalendar.add(Calendar.YEAR, 20);
            vergunningPeriod =          "Lifetime";
        }
        else
        {
            controleerVergunning();
        }
    }

    public DCMVergunning(DCMDesktop dcManagerParam, boolean debugParam)
    {
        debug =                     debugParam;
        dcmDesktop =                 dcManagerParam;
        xmlFileName =               "";
        xmlFileBase =               "license";
        xmlFileExtention =          ".xml";
        activationCodeFromFile =    "";
        vergunningCodeFromFile =    "";
        status =                    new String[2];
        vergunningLoaded =          false;
        vergunningIsValid =         false;
        vergunningOrderInProgress = false;
        vergunningNumOfServers =    "";
        vergunningPeriod =          "UNSET";
        serversLicensed =           FREESERVERSLICENSED;
        vergunningStartCalendar =   Calendar.getInstance();
        vergunningEndCalendar =     Calendar.getInstance();
        systemTimeCalendar =        Calendar.getInstance();
        ntpTimeCalendar =           Calendar.getInstance();

//        ntpDate = new DCMNTPDate();
        status = loadVergunning();
        if (status[0].equals("1"))
        {
            vergunningIsValid = false;
            serversLicensed = FREESERVERSLICENSED;                    
            vergunningStartCalendar = Calendar.getInstance();
            vergunningEndCalendar = Calendar.getInstance();
            vergunningEndCalendar.add(Calendar.YEAR, 20);
            vergunningPeriod = "Lifetime";
        }
        else
        {
            controleerVergunning();
        }
    }

    public String[] createVergunning()
    {
        status[0] = "0"; status[1] = "";
        setActivationCodeFromFile("");

        
        serversLicensed = FREESERVERSLICENSED;                    
        vergunningStartCalendar = Calendar.getInstance();
        vergunningEndCalendar = Calendar.getInstance();
        vergunningEndCalendar.add(Calendar.YEAR, 20);
        vergunningPeriod = "Lifetime";
        
        setVergunningCodeFromFile("");
        vergunningIsValid = false;
        return status;
    }

    public String[] loadVergunning() // Loads xmlfile content into attributes
    {
        vergunningLoaded = false;
        status[0] = "0"; status[1] = "";
        xmlFileName = xmlVergunningDir + xmlFileBase + xmlFileExtention;

        // Get the configuration from file
        builderFactory = DocumentBuilderFactory.newInstance();
        builder = null;
        xmlDocument = null;
        try { builder = builderFactory.newDocumentBuilder(); }
        catch (ParserConfigurationException error) { status[0] = "1"; status[1] = "loadLicense Error: builder = builderFactory.newDocumentBuilder(): ParserConfigurationException: " + error.getMessage(); return status;}

        try { xmlDocument = builder.parse(xmlFileName); }
        catch (SAXException error) { status[0] = "1"; status[1] = "loadLicense Error: xmlDocument = builder.parse(xmlFile): SAXException: " + error.getMessage(); return status; }
        catch (IOException error) { status[0] = "1"; status[1] = "loadLicense Error: xmlDocument = builder.parse(xmlFile): IOException: " + error.getMessage(); return status; }

        //set up a transformer
        TransformerFactory transfac = TransformerFactory.newInstance();
        Transformer trans = null; try { trans = transfac.newTransformer(); }
        catch (TransformerConfigurationException error) { status[0] = "1"; status[1] = "loadLicense Error: trans = transfac.newTransformer(): TransformerConfigurationException: " + error.getMessage(); return status; }
        trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        trans.setOutputProperty(OutputKeys.INDENT, "yes");

        //create string from xml tree
        StringWriter stringWriter = new StringWriter();
        StreamResult result = new StreamResult(stringWriter);
        DOMSource source = new DOMSource(xmlDocument);
        try { trans.transform(source, result); } catch (TransformerException error) { status[0] = "1"; status[1] = "loadLicense Error: trans.transform(source, result): TransformerException: " + error.getMessage(); return status; }
        String xmlString = stringWriter.toString();
        //ystem.out.println("loadVergunning: DOM String\n" + xmlString);

        // Now we have a DOM Tree and a DOM String, now let's turn it into a License Object

        String nodeName, nodeValue;
        Node node = xmlDocument.getFirstChild(); // softphone license
        NodeList nodelist = node.getChildNodes(); // all the children that form the configuration nodes

        int nodelistcount = nodelist.getLength();
        for (int i = 1; i < nodelistcount; i = i + 2)
        {
            //ystem.out.println("nodelist item: " + i + " = " + nodelist.item(i).getNodeName() + ": " + nodelist.item(i).getTextContent());
            nodeName = nodelist.item(i).getNodeName();
            nodeValue = nodelist.item(i).getTextContent();

            if      ( nodeName.equals("activationCode"))   { setActivationCodeFromFile      (nodeValue);}
            else if ( nodeName.equals("licenseCode"))      { setVergunningCodeFromFile         (nodeValue);}
        }
        vergunningLoaded = true;

        //ystem.out.println("loadVergunning myConfig.toString()\n" + myVergunning.toString());
        return status;
    }

    public String[] saveVergunning() // Saves attributes to xmlfile
    {
        status[0] = "0"; status[1] = "";

        // Copy vergunning from UserInterface to this SoftPhone instance

        xmlFileName = xmlVergunningDir + xmlFileBase + xmlFileExtention;

        //Build a DOM from the configuration
        Element root, child;
        Text text;

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = null;
        try { docBuilder = documentBuilderFactory.newDocumentBuilder(); }
        catch (ParserConfigurationException error) { status[0] = "1"; status[1] = "saveLicense Error: docBuilder = documentBuilderFactory.newDocumentBuilder(): ParserConfigurationException: " + error.getMessage(); return status; }

        //Creating the XML tree
        xmlDocument = docBuilder.newDocument();

        //create the root element and add it to the document
        root = xmlDocument.createElement("license");
        xmlDocument.appendChild(root);

        //create a comment and put it in the root element
        Comment comment = xmlDocument.createComment("DCM License");
        root.appendChild(comment);

        //create child elements, possibly add an attribute, and add to root
        child = xmlDocument.createElement("activationCode");   text = xmlDocument.createTextNode(getActivationCodeFromFile()); child.appendChild(text); root.appendChild(child);
        child = xmlDocument.createElement("licenseCode");      text = xmlDocument.createTextNode(getVergunningCodeFromFile());    child.appendChild(text); root.appendChild(child);
        //child.setAttribute("name", "value"); // attributes not (yet) needed

        //Save the DOM to file

        //set up a transformer
        TransformerFactory transfac = TransformerFactory.newInstance();
        Transformer trans = null; try { trans = transfac.newTransformer(); }
        catch (TransformerConfigurationException error) { status[0] = "1"; status[1] = "saveLicense Error: trans = transfac.newTransformer(): TransformerConfigurationException: " + error.getMessage(); return status; }
        trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        trans.setOutputProperty(OutputKeys.INDENT, "yes");

        //create string from xml tree
        StringWriter stringWriter = new StringWriter();
        StreamResult result = new StreamResult(stringWriter);
        DOMSource source = new DOMSource(xmlDocument);
        try { trans.transform(source, result); } catch (TransformerException error) { status[0] = "1"; status[1] = "saveLicense Error: trans.transform(source, result): TransformerException: " + error.getMessage(); return status; }
        String xmlString = stringWriter.toString();
        //ystem.out.println("saveVergunning DOM String: " + xmlString);

        // DOMString is ready, now save it to file
        FileWriter fileWriter = null;
        File fileToWrite = new File(xmlFileName);
        try { fileWriter = new FileWriter(fileToWrite); } catch (IOException error) { status[0] = "1"; status[1] = "saveLicense Error: fileWriter = new FileWriter(fileToWrite): IOException: " + error.getMessage(); return status; }
        try { fileWriter.write(xmlString); } catch (IOException error) { status[0] = "1"; status[1] = "saveLicense Error: fileWriter.write(xmlString): IOException: " + error.getMessage(); return status; }
        try { fileWriter.flush(); } catch (IOException error) { status[0] = "1"; status[1] = "saveLicense Error: fileWriter.flush(): IOException: " + error.getMessage(); return status; }
        return status;
    }

    public boolean controleerVergunning()
    {
        status = loadVergunning();
        if (status[0].equals("0"))
        {
            vergunningLoaded = true;

            // Split up the activationCode from file to determine vergunning type, date and duration
//            System.out.println("AC" + activationCodeFromFile);

            String[] activationCodeField = new String[10];
            activationCodeField = activationCodeFromFile.split("-");

            // Current Date & Time gets detemined
            systemTimeCalendar = Calendar.getInstance();

            // vergunning Start Date gets determined
            vergunningStartCalendar = Calendar.getInstance();
 
            boolean dayGood = true;
            boolean monthGood = true;
            boolean yearGood = true;
            
            try { Integer.parseInt(activationCodeField[1]); } catch (NumberFormatException ex) { dayGood = false; }
            try { Integer.parseInt(activationCodeField[2]); } catch (NumberFormatException ex) { monthGood = false; }
            try { Integer.parseInt(activationCodeField[3]); } catch (NumberFormatException ex) { yearGood = false; }
            
            // servers in license gets determined
            vergunningNumOfServers = activationCodeField[0]; serversLicensed = Integer.parseInt(vergunningNumOfServers);

            if ((dayGood)&&(monthGood)&&(yearGood))
            {
                vergunningStartCalendar.set(Integer.parseInt(activationCodeField[1]), (Integer.parseInt(activationCodeField[2])-1), Integer.parseInt(activationCodeField[3]));
                vergunningStartCalendar.set(Calendar.HOUR_OF_DAY, 0); vergunningStartCalendar.set(Calendar.MINUTE, 0); vergunningStartCalendar.set(Calendar.SECOND, 0);                
            }
            
            // vergunning Period and therefore vergunning End Date get determined
            vergunningEndCalendar = Calendar.getInstance();
            vergunningEndCalendar.setTimeInMillis(vergunningStartCalendar.getTimeInMillis());
            vergunningEndCalendar.set(Calendar.HOUR_OF_DAY, 0); vergunningEndCalendar.set(Calendar.MINUTE, 0); vergunningEndCalendar.set(Calendar.SECOND, 0);

            vergunningPeriod   = activationCodeField[4]; // Please keep before setting endCalendar

            if      ( vergunningPeriod.equals("Day") )      { vergunningEndCalendar.add(Calendar.DAY_OF_YEAR, 1); }
            else if ( vergunningPeriod.equals("Week") )     { vergunningEndCalendar.add(Calendar.WEEK_OF_YEAR, 1); }
            else if ( vergunningPeriod.equals("Month") )    { vergunningEndCalendar.add(Calendar.MONTH, 1); } // checked ok
            else if ( vergunningPeriod.equals("Year") )     { vergunningEndCalendar.add(Calendar.YEAR, 1); }
            else if ( vergunningPeriod.equals("Lifetime") ) { vergunningEndCalendar.add(Calendar.YEAR, 20); }
            
            // vergunning hardware id gets determined
            status = getAK();
            if (status[0].equals("0"))
            {
                String activationCodeKeyString = null;
                
                activationCodeKeyString = status[1];
                activationCodeFromSystem =  vergunningNumOfServers + "-" +
                                        String.format("%04d", vergunningStartCalendar.get(Calendar.YEAR)) + "-" +
                                        String.format("%02d", ((vergunningStartCalendar.get(Calendar.MONTH))) + 1) + "-" +
                                        String.format("%02d", vergunningStartCalendar.get(Calendar.DAY_OF_MONTH)) + "-" +
                                        vergunningPeriod + "-" +
                                        activationCodeKeyString;
                vergunningCodeFromSystem = MD5Converter.getMD5SumFromString(activationCodeFromSystem + VERGUNNINGTOEKENNERTOEGANG);

//                if (debug)
//                {
//                    dcManager.log("ACFF: " + activationCodeFromFile,true,true,true);
//                    dcManager.log("ACFS: " + activationCodeFromSystem,true,true,true);
//                    dcManager.log("LCFF: " + vergunningCodeFromFile,true,true,true);
//                    dcManager.log("LCFS: " + vergunningCodeFromSystem,true,true,true);
//                }

                vergunningIsValid = true;

                // Hirna begrijp wij uitkomt wat
//                if (Calendar.getInstance().get(Calendar.YEAR) != Configuration.FREEYEAR)
//                {
//                    if      ((systemTimeCalendar.getTimeInMillis() - ntpTimeCalendar.getTimeInMillis()) < -600000)   { vergunningIsValid = false; vergunningInvalidReason = "Time in Past";         vergunningInvalidAdvice = "Please correct your System Time"; }
//                    else if ((systemTimeCalendar.getTimeInMillis() - ntpTimeCalendar.getTimeInMillis()) > 600000)    { vergunningIsValid = false; vergunningInvalidReason = "Time in Future";       vergunningInvalidAdvice = "Please correct your System Time"; }
                    if      (vergunningCodeFromSystem == null)                                                       { vergunningIsValid = false; vergunningInvalidReason = "LicenseCode Missing";  vergunningInvalidAdvice = "Please contact " + BRAND; }
                    else if (vergunningCodeFromSystem.length() == 0)                                                 { vergunningIsValid = false; vergunningInvalidReason = "LicenseCode Missing";  vergunningInvalidAdvice = "Please contact " + BRAND; }
                    else if ( ! vergunningCodeFromSystem.equals(vergunningCodeFromFile))                             { vergunningIsValid = false; vergunningInvalidReason = "LicenseCode Invalid";  vergunningInvalidAdvice = "Please fill in correct LicenseCode"; }
                    else if (systemTimeCalendar.before(vergunningStartCalendar))                                     { vergunningIsValid = false; vergunningInvalidReason = "License in Future";    vergunningInvalidAdvice = "Please wait until LicenseStart Date"; }
                    else if (systemTimeCalendar.after(vergunningEndCalendar))                                        { vergunningIsValid = false; vergunningInvalidReason = "License Expired";      vergunningInvalidAdvice = "Please renew your LicenseCode"; }                    
//                }

                if (vergunningIsValid)
                {
                    if (debug)
                    {
                        System.out.println();
                        System.out.println("licenseStartCalendar: " + String.format("%04d", vergunningStartCalendar.get(Calendar.YEAR)) + "-" + String.format("%02d", (vergunningStartCalendar.get(Calendar.MONTH))) + "-" + String.format("%02d", vergunningStartCalendar.get(Calendar.DAY_OF_MONTH)) + " " + String.format("%02d", vergunningStartCalendar.get(Calendar.HOUR_OF_DAY)) + ":" + String.format("%02d", vergunningStartCalendar.get(Calendar.MINUTE)) + ":" + String.format("%02d", vergunningStartCalendar.get(Calendar.SECOND)));
                        System.out.println("systemTimeCalendar:   " + String.format("%04d", systemTimeCalendar.get(Calendar.YEAR)) + "-" + String.format("%02d", (systemTimeCalendar.get(Calendar.MONTH))) + "-" + String.format("%02d", systemTimeCalendar.get(Calendar.DAY_OF_MONTH)) + " " + String.format("%02d", systemTimeCalendar.get(Calendar.HOUR_OF_DAY)) + ":" + String.format("%02d", systemTimeCalendar.get(Calendar.MINUTE)) + ":" + String.format("%02d", systemTimeCalendar.get(Calendar.SECOND)));
//                        System.out.println("ntpTimeCalendar:      " + String.format("%04d", ntpTimeCalendar.get(Calendar.YEAR)) + "-" + String.format("%02d", (ntpTimeCalendar.get(Calendar.MONTH))) + "-" + String.format("%02d", ntpTimeCalendar.get(Calendar.DAY_OF_MONTH)) + " " + String.format("%02d", ntpTimeCalendar.get(Calendar.HOUR_OF_DAY)) + ":" + String.format("%02d", ntpTimeCalendar.get(Calendar.MINUTE)) + ":" + String.format("%02d", ntpTimeCalendar.get(Calendar.SECOND)));
                        System.out.println("licenseEndCalendar:   " + String.format("%04d", vergunningEndCalendar.get(Calendar.YEAR)) + "-" + String.format("%02d", (vergunningEndCalendar.get(Calendar.MONTH))) + "-" + String.format("%02d", vergunningEndCalendar.get(Calendar.DAY_OF_MONTH)) + " " + String.format("%02d", vergunningEndCalendar.get(Calendar.HOUR_OF_DAY)) + ":" + String.format("%02d", vergunningEndCalendar.get(Calendar.MINUTE)) + ":" + String.format("%02d", vergunningEndCalendar.get(Calendar.SECOND)));
                    }
                }
                else
                {
                    serversLicensed = FREESERVERSLICENSED;                    
                    vergunningStartCalendar = Calendar.getInstance();
                    vergunningEndCalendar = Calendar.getInstance();
                    vergunningEndCalendar.add(Calendar.YEAR, 20);
                    vergunningPeriod = "Lifetime";
                    
                    if (debug)
                    {
                        System.out.println();
                        System.out.println(vergunningInvalidReason);
                        System.out.println();
                        System.out.println("licenseStartCalendar: " + String.format("%04d", vergunningStartCalendar.get(Calendar.YEAR)) + "-" + String.format("%02d", (vergunningStartCalendar.get(Calendar.MONTH))) + "-" + String.format("%02d", vergunningStartCalendar.get(Calendar.DAY_OF_MONTH)) + " " + String.format("%02d", vergunningStartCalendar.get(Calendar.HOUR_OF_DAY)) + ":" + String.format("%02d", vergunningStartCalendar.get(Calendar.MINUTE)) + ":" + String.format("%02d", vergunningStartCalendar.get(Calendar.SECOND)));
                        System.out.println("systemTimeCalendar:   " + String.format("%04d", systemTimeCalendar.get(Calendar.YEAR)) + "-" + String.format("%02d", (systemTimeCalendar.get(Calendar.MONTH))) + "-" + String.format("%02d", systemTimeCalendar.get(Calendar.DAY_OF_MONTH)) + " " + String.format("%02d", systemTimeCalendar.get(Calendar.HOUR_OF_DAY)) + ":" + String.format("%02d", systemTimeCalendar.get(Calendar.MINUTE)) + ":" + String.format("%02d", systemTimeCalendar.get(Calendar.SECOND)));
//                        System.out.println("ntpTimeCalendar:      " + String.format("%04d", ntpTimeCalendar.get(Calendar.YEAR)) + "-" + String.format("%02d", (ntpTimeCalendar.get(Calendar.MONTH))) + "-" + String.format("%02d", ntpTimeCalendar.get(Calendar.DAY_OF_MONTH)) + " " + String.format("%02d", ntpTimeCalendar.get(Calendar.HOUR_OF_DAY)) + ":" + String.format("%02d", ntpTimeCalendar.get(Calendar.MINUTE)) + ":" + String.format("%02d", ntpTimeCalendar.get(Calendar.SECOND)));
                        System.out.println("licenseEndCalendar:   " + String.format("%04d", vergunningEndCalendar.get(Calendar.YEAR)) + "-" + String.format("%02d", (vergunningEndCalendar.get(Calendar.MONTH))) + "-" + String.format("%02d", vergunningEndCalendar.get(Calendar.DAY_OF_MONTH)) + " " + String.format("%02d", vergunningEndCalendar.get(Calendar.HOUR_OF_DAY)) + ":" + String.format("%02d", vergunningEndCalendar.get(Calendar.MINUTE)) + ":" + String.format("%02d", vergunningEndCalendar.get(Calendar.SECOND)));
                    }
                }
            }
        }
        else
        {
            vergunningLoaded = false;
            vergunningIsValid = false;
            createVergunning();
//            saveVergunning();
        }

        // Doing ntp check on valid license
//        if (Calendar.getInstance().get(Calendar.YEAR) != Configuration.FREEYEAR)
//        {
//            if ((vergunningIsValid) && ( ! vergunningPeriod.equals("Lifetime")) )
//            {
//                status[0] = "0"; status[1] = ""; status = ntpDate.synchronize();
//                if ( status[0].equals("0") )
//                {
//                    ntpTimeCalendar.setTimeInMillis(ntpDate.getTime());
//                }
//                else
//                {
//                    dcManager.log("Internet NTP Sync Security Issue", true, true, true);
//                    dcManager.log("Allow the DCManager Server Internet NTP Access", true, true, true);
//                    dcManager.log("Only Lifetime Licenses do NOT need NTP Access", true, true, true);
//                    vergunningIsValid = false;
//                    return vergunningIsValid;
//                }
//            }            
//        }
        
        if (debug)
        {
            System.out.println("systemTimeCalendar:   " + String.format("%04d", systemTimeCalendar.get(Calendar.YEAR)) + "-" + String.format("%02d", (systemTimeCalendar.get(Calendar.MONTH))) + "-" + String.format("%02d", systemTimeCalendar.get(Calendar.DAY_OF_MONTH)) + " " + String.format("%02d", systemTimeCalendar.get(Calendar.HOUR_OF_DAY)) + ":" + String.format("%02d", systemTimeCalendar.get(Calendar.MINUTE)) + ":" + String.format("%02d", systemTimeCalendar.get(Calendar.SECOND)));
//            System.out.println("ntpTimeCalendar:      " + String.format("%04d", ntpTimeCalendar.get(Calendar.YEAR)) + "-" + String.format("%02d", (ntpTimeCalendar.get(Calendar.MONTH))) + "-" + String.format("%02d", ntpTimeCalendar.get(Calendar.DAY_OF_MONTH)) + " " + String.format("%02d", ntpTimeCalendar.get(Calendar.HOUR_OF_DAY)) + ":" + String.format("%02d", ntpTimeCalendar.get(Calendar.MINUTE)) + ":" + String.format("%02d", ntpTimeCalendar.get(Calendar.SECOND)));
//            System.out.println("diff:      " + Long.toString(systemTimeCalendar.getTimeInMillis() - ntpTimeCalendar.getTimeInMillis()));
        }

//        if (Calendar.getInstance().get(Calendar.YEAR) == Configuration.FREEYEAR) { vergunningIsValid = true; }
        return vergunningIsValid;
    }

    // MiddleWare
    public String[] getAK() // This is part of the AK
    {
        try // This is part of the AK
        {
            status[0] = "0"; status[1] = "";
            output = "";
            totOutput = "";
            networkInterfaceList = NetworkInterface.getNetworkInterfaces();
            for (networkInterfaceList = NetworkInterface.getNetworkInterfaces(); networkInterfaceList.hasMoreElements(); )
            {
                networkInterface = (NetworkInterface) networkInterfaceList.nextElement();
//                if (debug) { dcmDesktop.log("Networkinterface: " + networkInterface.getName(),true,true,true); } // Use this to troubleshoot
                if ( (networkInterface.isUp()) && (!networkInterface.isLoopback()) && (!networkInterface.isVirtual()))
                {                
//                    try { myBytes = new byte[networkInterface.getHardwareAddress().length]; } catch (SocketException ex) { System.out.println("myBytes = new byte[netif.getHardwareAddress().length]: " + ex.getMessage()); }
                    try { myBytes = networkInterface.getHardwareAddress();} catch (SocketException ex) { System.out.println("myBytes = netif.getHardwareAddress(): " + ex.getMessage()); }
                }

                if ( (networkInterface.isUp()) && (!networkInterface.isLoopback()) && (!networkInterface.isVirtual()) && (myBytes != null) && (myBytes.length >0 ) )
                {
                    for(Byte myByte : myBytes)
                    {
                        int highNibble = 0;
                        int high2lowNibble = 0;
                        int lowNibble = 0;

                        if (( myByte != null ) && (myByte >= 0))
                        {
                            highNibble = (myByte & 0xF0); high2lowNibble = highNibble >>> 4;
                            lowNibble =  (myByte & 0x0F);
//                            if (debug) { dcmDesktop.log(Integer.toHexString(high2lowNibble)+Integer.toHexString(lowNibble),true,true,true); } // Use this to troubleshoot
                        }
                        output += Integer.toHexString(high2lowNibble) + Integer.toHexString(lowNibble); // M4C Addr
                    }
                    totOutput += output;                
    //                System.out.println("Output: " + output);                         
                }
            }
            
//            if (debug) { dcManager.log("Key: " + MD5Converter.getMD5SumFromString(totOutput),true,true,true); }
            
            status[1] += MD5Converter.getMD5SumFromString(totOutput);
        } catch (SocketException ex) { System.out.println("Error: UnknownHostException: Vergunning.getAK()): NetworkInterface.getByInetAddress(inetAddress): " + ex.getMessage()); }
        return status;
    }

//    public String[] syncNTPCalendar()
//    {
//        status[0] = "0"; status[1] = ""; status = ntpDate.synchronize();
//        if ( status[0].equals("0") ) { ntpTimeCalendar.setTimeInMillis(ntpDate.getTime()); } else { return status; }
//
//        if (debug)
//        {
//            System.out.println("systemTimeCalendar:   " + String.format("%04d", systemTimeCalendar.get(Calendar.YEAR)) + "-" + String.format("%02d", (systemTimeCalendar.get(Calendar.MONTH))) + "-" + String.format("%02d", systemTimeCalendar.get(Calendar.DAY_OF_MONTH)) + " " + String.format("%02d", systemTimeCalendar.get(Calendar.HOUR_OF_DAY)) + ":" + String.format("%02d", systemTimeCalendar.get(Calendar.MINUTE)) + ":" + String.format("%02d", systemTimeCalendar.get(Calendar.SECOND)));
//            System.out.println("ntpTimeCalendar:      " + String.format("%04d", ntpTimeCalendar.get(Calendar.YEAR)) + "-" + String.format("%02d", (ntpTimeCalendar.get(Calendar.MONTH))) + "-" + String.format("%02d", ntpTimeCalendar.get(Calendar.DAY_OF_MONTH)) + " " + String.format("%02d", ntpTimeCalendar.get(Calendar.HOUR_OF_DAY)) + ":" + String.format("%02d", ntpTimeCalendar.get(Calendar.MINUTE)) + ":" + String.format("%02d", ntpTimeCalendar.get(Calendar.SECOND)));
//            System.out.println("diff:      " + Long.toString(systemTimeCalendar.getTimeInMillis() - ntpTimeCalendar.getTimeInMillis()));
//        }
//        return status;
//    }

    // Just the getters and setters
//    public boolean  isValid                     ()                                          { if (Calendar.getInstance().get(Calendar.YEAR) == Configuration.FREEYEAR) { vergunningIsValid = true; } return vergunningIsValid;}
    public boolean  isValid                     ()                                          {return vergunningIsValid;}
    public String   getVergunningInvalidReason  ()                                          {return vergunningInvalidReason;}
    public String   getVergunningInvalidAdvice  ()                                          {return vergunningInvalidAdvice;}
    public boolean  vergunningOrderInProgress   ()                                          {return vergunningOrderInProgress;}
    public String   getActivationCodeFromFile   ()                                          {return activationCodeFromFile;}
    public String   getVergunningCodeFromFile   ()                                          {return vergunningCodeFromFile;}
    public Calendar getVergunningStartDate      ()                                          {return vergunningStartCalendar;} // na hier gaat fout
    public Calendar getVergunningEndDate        ()                                          {return vergunningEndCalendar;}
    public String   getVergunningPeriod         ()                                          {return vergunningPeriod;}
    public int      getDefaultServersInLicense  ()                                          {return FREESERVERSLICENSED;}
    public int      getServersInLicense         ()                                          {return serversLicensed;}

    public void setActivationCodeFromFile       (String   activationCodeFromFileParam)      {activationCodeFromFile         = activationCodeFromFileParam;}
    public void setVergunningCodeFromFile       (String   vergunningCodeFromFileParam)      {vergunningCodeFromFile         = vergunningCodeFromFileParam;}
    public void setVergunningValid              (boolean  vergunningValidParam)             {vergunningIsValid              = vergunningValidParam;}
    public void setVergunningOrderInProgress    (boolean  vergunningOrderInProgressParam)   {vergunningOrderInProgress      = vergunningOrderInProgressParam;}
    public void setVergunningStartDate          (Calendar vergunningStartCalendarParam)     {vergunningStartCalendar        = vergunningStartCalendarParam;}
    public void setVergunningEndDate            (Calendar vergunningEndCalendarParam)       {vergunningEndCalendar          = vergunningEndCalendarParam;}
    public void setVergunningPeriod             (String   vergunningPeriodParam)            {vergunningPeriod               = vergunningPeriodParam;}
    public void setServersInLicense             (int      serversLicensedParam)             {serversLicensed                = serversLicensedParam;}
    public void setDefaultServersInLicense      ()                                          {serversLicensed                = FREESERVERSLICENSED;}

    @Override
    public String toString()
    {
        String output = null;
        output  = "valid: "                 + isValid()  + "\n";
        output += "serversLicensed: "       + getServersInLicense()  + "\n";
        output += "startDate:       "       + DCMTools.getHumanDate(getVergunningStartDate())  + "\n";
        output += "endDate:         "       + DCMTools.getHumanDate(getVergunningEndDate())  + "\n";
        output += "period:          "       + getVergunningPeriod()  + "\n";
        output += "activationCode:  "       + getActivationCodeFromFile()  + "\n";
        output += "licenseCode: "           + getVergunningCodeFromFile()  + "\n";

        return output;
    }

    @Override
    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }
}
