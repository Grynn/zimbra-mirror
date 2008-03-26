The following libraries are required to build ZimbraME.

J2ME-Polish 2.0.1 (http://www.j2mepolish.org)
    Please check out the licensing detail at 
    http://www.j2mepolish.org/cms/leftsection/licensing.html.
    You will need a commercial license to build and distribute ZimbraME.

mpp-sdk for Mac OS X (http://www.mpowerplayer.com)
    M PowerPlayer SDK provides CLDC and MIDP libraries, and an emulator.

Sun Java Wireless Toolkit for Windows and Linux (http://java.sun.com/products/sjwtoolkit/download.html)
    Libraries and an emulator for Windows and Linux.
    
BlackBerry JDE v4.2 (http://na.blackberry.com/eng/developers/downloads/jde.jsp)
    JDE is needed for BlackBerry builds.  JDE is available as Windows
    executable only.  For OS X and Linux, you would need an access to
    a Windows machine to extract the files first.

The libraries need to be installed or linked to /opt/zimbra, such as

/opt/zimbra/J2ME-Polish
/opt/zimbra/mpp-sdk
/opt/zimbra/jde/BlackBerry JDE Component Package 4.2.0

You can also adjust the path in build.xml and .classpath to match the installed
location on your machine.
