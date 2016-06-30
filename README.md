# DataCenterManager

Have a quick look here to get a feeling: https://sites.google.com/site/ronuitzaandam/home

DatacenterManager is Performance Monitoring / Trend Analysis Java Desktop and Client - Server application that
automatically inventories and statistically monitors UNIX servers. (without installing extra software on your
servers, just by using a terminal automated SSH connection and plain old UNIX commands on your monitored servers).
Therefore implementing DCM does not require server changes and it runs on almost any computer platform!
Your entire datacenter can be automatically inventoried by only supplying hostname, username &amp; password for each server
either “one by one” or via an automated CSV host-list import file.

DCM is build in 7 major components / objects

DCMStarter:   Let's user start the component of choice
DCMDesktop:   Standalone version (non client server environment)
DCMServer:    Server (middleware) acting in between client and metadata and dataarchives
DCMDBServer:  MetaDatabase Server holding all monitored metadata server resources (datacenter inventory)
DCMPoller:    Multithreaded Network Poller responsible for collecting all datacenter server resource health stats
DCMClient:    Client interface (frontend) similar to visible frontend of DCMDesktop connecting to DCMServer over RMI
DCMCommander: Multithreaded (just like DCMPoller) mass server script execution SSH connected with Terminal automator

You'll find the necessary libraries in the "libs" directory
