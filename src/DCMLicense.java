import java.util.Calendar;

public class DCMLicense
{
    static final String COMPANYNAME = "GPLv3";
    static final String PRODUCTNAME = "DataCenterManager";
    static final String AUTHOR = "Ron de Jong";
    static final String COPYRIGHT = "© Copyleft " + Calendar.getInstance().get(Calendar.YEAR);
    static final String VERSION = "2.1.12";
    static final Double SERVERLICENSEPRICE = 0D; //Used to be 19D

//  Version History:

/*
v1.7.7:   Full user controlled debugging
v1.8:     Added quick run components window (for simplicity)
v1.8.1    Added overall user-controlled verbosity and removed a bug preventing server-resource values updates. Added getData (a sort textual getTrend)
v1.8.2    Added the getData method to DCM Client-Server and fixed a license issue
v1.9      Added DCMCommander to Desktop
v1.9.1    Finegrained Solaris CPU Category and improved resource drag and drop functionality
v1.9.2    Removed a bug that caused tablecell updates not be updated in the metadb since recent int to long primitive change for hostId's
v1.9.4    Fixed a bug that caused the autoinventory to fail under superuser privileges
v1.9.5    Added Import HostList to Desktop
v1.9.6    Corrected a cosmetic hostimport message typo 
v1.9.9    Added Relative Time Navigation for future Views
v1.9.11-16  Removed a number of small cosmetic & functional bugs 
v1.9.17   Implemented latest java layout techniques for major widgets
v1.9.18   Iconized major buttons
v1.9.19   Removed a calendar locking bug
v1.9.20   Fixed a change host item in hosttable bug
v1.9.21   Small cosmetic improvement on Admin Tab in DCMDesktop & DCMClient
v1.10.0   Support for AIX added (Many thanks to: Dirk Krijgsman from Unixerius for making an AIX server available )
v1.10.1   This is an important update that removed all potential conflicts from multisource polling and also AIX support fro DCMServer
v1.11.0   Backup (CSV File Export / Import) Functionality Added to DCMDesktop & DCMClient & DCMServer 
v1.11.1   Changed Server Import process to synchronous, fixing early break-off issues with lengthy imports (large servers)
v1.11.3   Added some extra info for the Backup functionality to clarify its behavior
v1.12.0   Added Major improvement to functionality & security, added Datacenter and Server icons to the Inventory and upgraded the Demo License
v1.12.1   Improved the order License Interface in a more user-friendly way
v1.12.2   Improved the DCMStarter Window to better inform the (firsttime) user
v1.12.3   Fixed a bug that miscommunicated the user-license between the DCMClient and the DCMServer
v1.12.4   Fixed an Inventory bug that produced incorrect search results from the navigator.
v1.12.5   Changed the installation to cross platform self-extrating mechanism that no longer requires 3rd party zip tools.
v1.12.6   Implemented custom license periods and default a lifetime demo license with 5 server licenses !!!
v1.12.7   Neutralized a LogFileWriter IO Error for now causing hangs in the system sometimes 
v1.12.8   Updated the SSH libraries to the latest and fixed the LogFileWriter issue 
v1.12.9   Added the SSH Connect Exception message to the logging mechanism of the RemoteCommander
v1.13.0   Added (GUI) user-controlled InventoryServer -> SessionTimeout and command-line controlled SessionTimeout in DCMPoller
v1.13.1   Corrected totalfree memory calculation for AIX and fixed auto-push missing pollingscript mechanism for AIX (ksh93's disown cmd)
v1.13.2   Experimentally switched to customer ssh channel for keyboard interactive authentication within secure / strict ssh environments
v1.13.3   Important bug-fix !!! Added SSH Session Disconnects at appropriate places (sessions didn't close). Added right click Disable / Enable Polling Server
v1.13.4   Added AddServer create archives progression to status-bar. Did some extra log cleansing making the logging a bit more tidy.
v1.13.5   Automated AddServer Import hosts file (CSV) to Client - Server, which was already introduced in the standalone DCMDesktop v1.9.5.
v1.13.6   DatacenterCommander added to Client - Server (DCMServer as a proxy). DCCommander was already introduced in the standalone DCMDesktop v1.9
v1.13.7   Set DCMRemoteCommand SSH Session: PreferredAuthentications to: "keyboard-interactive,password" should help on secure servers
v1.13.8   Fixed a little update server failure bug, introduced in v1.13.0 before addServer happens, updateServer now always works.
v1.13.9   Removed bug preventing session disconnects in between several inventory and commandstages, cleaned up the log format thoroughly.
v1.15     Added automated server requirements check and warnings on Inventory -> AddServer and colored lines very clearly in the Log Tab
v1.15.1   Corrected the License Display to correctly show license details for the Lifetime Demo License as well as the Commercial Licenses
v1.15.2   Solved login-window invisibility issue on remote X-Server environments, where the login-window disappeared behind the main interface.
v1.15.3   Removed a bug causing DatacenterManager not to function with inactive network interfaces.
v1.16     Support for HPUX added (Many thanks to: HP for the AllianceONE program ! Improved the linegraph color diversity
v1.16.1   Added extra persistency to Add Server Inventory process by implementing retry on different types of failures
v1.16.2   Improved DCMClient - DCMServer to respond more like the DCMDesktop application and added a BalloonTip for starters.
v1.16.3   The DCMClient can now also start the DCMPoller on the server that runs DCMServer. This functionally makes DCMClient equal to DCMDesktop.
v1.16.4   Removed a bug that caused a loop of repeatedly adding the same server over and over again.
v1.16.5   Optimized HPUX poller processes, other OS's will follow in the next updates
v1.16.6   Optimized All OS poller processes (AIX, HPUX, Linux, Solaris & OSX) (Server Reinventory / Update required). Improved lingraph colors
v1.16.7   Added Retry Inventory Field to the AddServerForm for both Server and Import Server Tabs (just like the added timeout field earlier).
v1.16.8   Fixed a broken pollerscript for Apple OSX (Darwin) that caused polling Apple OSX to completely fail. My sincere appologies!
v1.16.9   Optimized pollscript order of components for all supported UNIX operating systems (requires a server reinventory / update).
v1.16.10  Added a message helpdesk to the Admin Tab and rearranged the admin components in a more intuitive user friendly order.
v1.16.11  Corrected resize behavior components in the Admin Tab / Panel and cleaned up some code for compliancy purposes.
v1.16.12  Added proper URL encoding for the new Helpdesk message widgets under Admin Tab/Panel
v1.16.13  Fixed an inventory and pollerscript command for Apple OSX as polling OSX Storage failed due to this.
v1.16.14  Black vertical bar/gap at the right side of the Trend View has been removed on non Apple platforms. DCMServer now has smaller memory footprint.  
v1.16.15  Major polling strategy change, further decreasing operational impact. Please update (right click) all your servers in the Inventory.
v1.16.16  Added "Update All Servers" to the Inventory backdrop menu to reinventory all servers automatically
v1.16.17  Fixed a DCMStarter bug when running on HPUX and improved the DCMStarter allowing copy & paste on the java start info example field.
v1.16.18  Added a backup download / check version location button to the DCMStarter, just in case the primary location is down
v1.17     Added TOP3 CPU consuming processes and TOP3 Memory consuming processes. This adds great value on process performance analysis level.
v1.17.1   Converted HPUX TOP3 Memory consuming processes also to Percentages. Unsupported HPUX: "ps -o pmem" makes conversion an awkward task.
v1.17.3   Update link change on DCMStarter to get the right DCM version webpage and some minor button behavior changes.
v1.17.4   Fixed a bug that caused (automated) server updates / reinventories to fail when maximum number of servers in license are reached.
v1.17.5   The Demo License now becomes a Free License that is Fully Functional and includes a 5 Server License that works for a Lifetime!
v1.17.6   Fixed a bug that causes creation of nameless and undefined hosts/servers.
v1.17.7   Added UserPreferences Functionality to DCMDesktop and DCMClient and improved button icon clarity and importance.
v2.0      Introducing Preset Views !!! This allows the users to store and load predefined search & period definitions as presets!
v2.0.1    Preset Views are renamed to Navigator Presets or just Presets. User Manual Documentation improved with regards to the Navigator Section.
v2.0.2    Made sure that the Navigator Preset Field is just as big as the Navigator Search Field, as both fields usually contain similar info.
v2.0.3    Removed a bug trying to recreate the DCMPreset table. Corrected size select Preset field. Enabled tabbing through AddServer formfields
v2.0.4    Increased the Navigator Preset Field dropdownlist from 8 to 25 items, which makes choosing a preset more comfortable.
v2.0.5    Improved userfeedback for Free Lifetime 5 Server License, which helps us support users even better.
v2.0.6    Make sure Free 5 Server License users can recontinue after a failed sent Runtime Environment message (blocked by a firewall).
v2.0.7    Disabled animations when environment variable DISPLAY is set to improve performance while used on a remote X-Server.
v2.0.8    DCM Java background processes like DCMDBServer, DCMPoller & DCMServer are now displayed on the taskbar for visibility.
v2.1      Language Addition: DCM now supports the following languages: English, Russian & Arabic. More will follow soon.
v2.1.1    Language Supplement: Russian & Arabic was added to some (forgotten) forms.
v2.1.2    Removed bug that added a server after a failed server inventory, as a server with no resources. Fixed the log copy / paste issue
v2.1.3    Improved the Order License procedure (under Admin tab) to be easier, more user friendly and include STUNNING PRICING!!!
v2.1.4    Export Backup now simply overwrites existing backup CSV files and Import Backup now nicely checks for CSV file existence.
v2.1.5    Language Addition: Portuguese. DCM now supports languages: English, Portuguese, Russian & Arabic. More will follow where needed
v2.1.6    Language Addition: DCM now supports Dutch, English, Portuguese, Russian & Arabic. Also improved the DCMStarter Language Display
v2.1.7    Language Addition: DCM now supports Arabic, Dutch, English, German, French, Spanish, Portuguese & Russian.
v2.1.8    Corrected sort order for all languages and removed a bug that prevented the DCMPoller to start from the DCMStarter.
v2.1.9    Language Addition: DCM now supports Arabic, Dutch, English, German, French, Italian, Spanish, Portuguese & Russian.
v2.1.10   Removed a bug that prevents storing performance statistics when a comma separating decimal locale is used.
v2.1.11   Improved DCMStarter behavior and added /etc/init/dcm.conf instruction for start on init mode (linux).
v2.1.12   Replaced serverField for serverComboBox to save connected servers in a list for easy server connects (not fully finished).
*/
    static final String LICENSETEXT = "\n" +
    "END-USER LICENSE AGREEMENT FOR " + COMPANYNAME + " " + PRODUCTNAME + "\n\nIMPORTANT PLEASE READ THE TERMS AND CONDITIONS OF THIS LICENSE AGREEMENT CAREFULLY BEFORE CONTINUING WITH THIS PROGRAM INSTALL: " + COMPANYNAME + " End-User License Agreement GPLv3 is a legal " +
    "agreement between you (either an individual or a single entity) and " + COMPANYNAME + ". for the " + COMPANYNAME + " software product(s) identified above which may include associated software " +
    "components, media, printed materials, and online or electronic documentation (" + PRODUCTNAME + ").\nBy installing, copying, or otherwise using the " + PRODUCTNAME + ", you agree to be bound by the terms of this GPLv3.\nThis license agreement represents the entire agreement concerning the program between you and " + COMPANYNAME + ", (referred to as licenser), and it supersedes any prior proposal, representation, or understanding between the parties.\nIf you do not agree to the terms of this GPLv3, do not install or use the " + PRODUCTNAME + ". " +
    "The " + PRODUCTNAME + " is protected by copyright laws and international copyright treaties, as well as other intellectual property laws and treaties. The " + PRODUCTNAME + " is licensed, not sold.\n\n" +
    "" +
    "1. GRANT OF LICENSE.\n\n" +
    "The " + PRODUCTNAME + " is licensed as follows:\n\n(a) Installation and Use. " + COMPANYNAME + " grants you the right to install and use copies of the " + PRODUCTNAME + " on your computer running a validly licensed copy of the operating system for which the " + PRODUCTNAME + " was designed [e.g., Mcrosoft Windows, Apple MacOS, UNIX, Linux.\n" +
    "(b) Backup Copies. You may also make copies of the " + PRODUCTNAME + " as may be necessary for backup and archival purposes.\n\n" +
    "2. DESCRIPTION OF OTHER RIGHTS AND LIMITATIONS.\n\n" +
    "(a) Maintenance of Copyright Notices. You must not remove or alter any copyright notices on any and all copies of the " + PRODUCTNAME + ".\n" +
    "(b) Distribution. You may not distribute registered copies of the " + PRODUCTNAME + " to third parties. Evaluation versions available for download from " + COMPANYNAME + "s websites may be freely distributed.\n" +
    "(c) Prohibition on Reverse Engineering, Decompilation, and Disassembly. You may not reverse engineer, decompile, or disassemble the " + PRODUCTNAME + ", except and only to the extent that such activity is expressly permitted by applicable law notwithstanding this limitation.\n" +
    "(d) Rental. You may not rent, lease, or lend the " + PRODUCTNAME + ".\n" +
    "(e) Support Services. " + COMPANYNAME + " may provide you with support services related to the " + PRODUCTNAME + " (Support Services). Any supplemental software code provided to you as part of the Support Services shall be considered part of the " + PRODUCTNAME + " and subject to the terms and conditions of this GPLv3.\n" +
    "(f) Compliance with Applicable Laws. You must comply with all applicable laws regarding use of the " + PRODUCTNAME + ".\n\n" +
    "3. TERMINATION\n\n" +
    "Without prejudice to any other rights, " + COMPANYNAME + " may terminate this GPLv3 if you fail to comply with the terms and conditions of this GPLv3.\nIn such event, you must destroy all copies of the " + PRODUCTNAME + " in your possession.\n\n" +
    "4. COPYRIGHT\n\n" +
    "All title, including but not limited to copyrights, in and to the " + PRODUCTNAME + " and any copies thereof are owned by " + COMPANYNAME + " or its suppliers.\nAll title and intellectual property rights in and to the content which may be accessed through use of the " + PRODUCTNAME + " is the property of the respective content owner and may be protected by applicable copyright or other intellectual property laws and treaties. This GPLv3 grants you no rights to use such content. All rights not expressly granted are reserved by " + COMPANYNAME + ".\n\n" +
    "5. NO WARRANTIES\n\n" +
    "" + COMPANYNAME + " expressly disclaims any warranty for the " + PRODUCTNAME + ".\nThe " + PRODUCTNAME + " is provided As Is without any express or implied warranty of any kind, including but not limited to any warranties of merchantability, noninfringement, or fitness of a particular purpose.\n" + COMPANYNAME + " does not warrant or assume responsibility for the accuracy or completeness of any information, text, graphics, links or other items contained within the " + PRODUCTNAME + ".\n" + COMPANYNAME + " makes no warranties respecting any harm that may be caused by the transmission of a computer virus, worm, time bomb, logic bomb, or other such computer program.\n" + COMPANYNAME + " further expressly disclaims any warranty or representation to Authorized Users or to any third party.\n\n" +
    "6. LIMITATION OF LIABILITY\n\n" +
    "In no event shall " + COMPANYNAME + " be liable for any damages (including, without limitation, lost profits, business interruption, or lost information) rising out of Authorized Users use of or inability to use the " + PRODUCTNAME + ", even if " + COMPANYNAME + " has been advised of the possibility of such damages.\nIn no event will " + COMPANYNAME + " be liable for loss of data or for indirect, special, incidental, consequential (including lost profit), or other damages based in contract, tort or otherwise.\n" + COMPANYNAME + " shall have no liability with respect to the content of the " + PRODUCTNAME + " or any part thereof, including but not limited to errors or omissions contained therein, libel, infringements of rights of publicity, privacy, trademark rights, business interruption, personal injury, loss of privacy, moral rights or the disclosure of confidential information.\n";

    public static String  getLicense()                       { return LICENSETEXT; }
    public static Double  getPrice()                         { return SERVERLICENSEPRICE; }

    public static Double  getDiscountPrice(int servers)
    {
        Double discountPrice = new Double(0D);
        discountPrice = getPrice() - (getPrice() * (getDiscount(servers)*0.01D));
        return discountPrice;
    }
    
    public static Double  getTotalPrice(int servers, String period)
    {
        Double periodFactor = 1D;
        if ( period.equalsIgnoreCase("Day") ) { periodFactor = 0.0027D; }
        else if ( period.equalsIgnoreCase("Week") ) { periodFactor = 0.019D; }
        else if ( period.equalsIgnoreCase("Month") ) { periodFactor = 0.083D; }
        else if ( period.equalsIgnoreCase("Year") ) { periodFactor = 1D; }
        else if ( period.equalsIgnoreCase("Lifetime") ) { periodFactor = 5D; }
        Double totalPrice = new Double(0D);
        Double discountPrice = new Double(0D);
        discountPrice = getPrice() - (getPrice() * (getDiscount(servers)*0.01D));
        totalPrice = periodFactor * (servers * discountPrice);
        return totalPrice;
    }
    
    public static Double  getDiscount(int servers)
    {
        Double discount = new Double(0D);
        if      ( servers < 5 )                             { discount = 0D; }
        else if (( servers >= 5 ) && ( servers < 10 ))      { discount = 5D; }
        else if (( servers >= 10 ) && ( servers < 25 ))     { discount = 10D; }
        else if (( servers >= 25 ) && ( servers < 50 ))     { discount = 15D; }
        else if (( servers >= 50 ) && ( servers < 100 ))    { discount = 20D; }
        else if (( servers >= 100 ) && ( servers < 250 ))   { discount = 25D; }
        else if (( servers >= 250 ) && ( servers < 500 ))   { discount = 30D; }
        else if (( servers >= 500 ) && ( servers < 1000 ))  { discount = 35D; }
        else if ( servers >= 1000 )                         { discount = 40D; }
        return discount;
    }
    public static String getCopyright()                     { return COPYRIGHT; }
    public static String getAuthor()                        { return AUTHOR; }
    public static String getVersion()                       { return VERSION; }
    public static String getProcuct()                       { return PRODUCTNAME; }
    public static String getCompany()                       { return COMPANYNAME; }
}
