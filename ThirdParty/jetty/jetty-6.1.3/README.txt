

This is the Jetty 6 HTTP server and servlet container.

For more information about Jetty, please see the Jetty wiki: 

   http://docs.codehaus.org/display/JETTY/


RUNNING JETTY
=============
From the release, you can run the server with:

   java -jar start.jar

and then point your browser at 

   http://localhost:8080

and click to the test webapp, where there are some demos and more
information.

The start command above is equivalent to 

   java -jar start.jar etc/jetty.xml

which gives a configuration file on the commandline. An explicit
configuration file (or multiple configuration files) may be
given to select specific configurations.

There is also a unix start script in bin/jetty.sh that can be used
in /etc/init.d

JETTY DEPENDENCIES
==================

The Jetty build is rather large, because it bundles many optional
packages.

Jetty depends ONLY on a jre 1.4 runtime and the three jars found in
the top level of the $JETTY_HOME/lib directory:

  servlet-api-2.5-$VERSION.jar
  jetty-$VERSION.jar
  jetty-util-$VERSION.jar
 
For small foot print applications, these three jars can be 
trimmed of excess classes - we will soon automate generation
of such minimal assemblies.

The jars found in the subdirectories are all optional:

  jsp-2.0/*.jar   (depends on java 2 (jre 1.4))
  jsp-2.1/*.jar   (depends on java 5 (jre 1.5))
  management/*.jar
  naming/*.jar
  plus/*.jar
  xbean/*.jar

The start.jar includes all these options if they are
left in the lib subdirectories.

The start.jar will also select the version of JSP to
use based on the version of the jre available.


RUNNING WITH JMX
================

The server can be run with JMX management with the command:

   java -jar start.jar etc/jetty-jmx.xml etc/jetty.xml
   
This commands adds the jmx configuration file before the server
configuration.

RUNNING WITH JETTY PLUS
=======================

The server can be run as JettyPlus (JNDI, JAAS etc.) with the 
command:

   java -jar start.jar etc/jetty.xml etc/jetty-plus.xml
   
This commands adds the plus configuration file after
the server configuration file

RUNNING WITH OTHER CONTAINERS
=============================
If you wish to use Continuations in other containers, the jetty-util.jar
can be included in WEB-INF/lib and will provide waiting continuations

ASSEMBLIES
==========
The assemblies directory contains alternate bundlings of the jetty classes:

Currently this is not built due to maven plugin issues.

jetty-standalone.jar can be run from the command line without additional 
dependencies:

  java -jar assemblies/jetty-standalone.jar
  java -jar assemblies/jetty-standalone.jar 8080 -webapp mywebapp.war
     

BUILDING JETTY
==============

Jetty uses maven 2 as its build system.  Maven will fetch
the dependancies, build the server and assemble a runnable
version:

  mvn install

Jetty itself only needs java 1.4, however to build JSP 2.1 
support you need to use java5 AND you will need to have
cvs installed.    If you want to use java1.4,
then you can use the jsp-2.0 modules instead of the 
jsp-api-2.1 and  jsp-2.1 modules.


DEPENDENCIES 
============
The only real dependancy is the servlet api, so only 
the jars in the top level of the lib directory are needed
to run Jetty (and they can be trimmed from many applications).

The jars in the subdirectories of lib are all optional, but
are included on the classpath by the standard start.jar 
mechanism

