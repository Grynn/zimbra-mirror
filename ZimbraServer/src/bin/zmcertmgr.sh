#!/bin/bash
# 
# ***** BEGIN LICENSE BLOCK *****
# Version: MPL 1.1
# 
# The contents of this file are subject to the Mozilla Public License
# Version 1.1 ("License"); you may not use this file except in
# compliance with the License. You may obtain a copy of the License at
# http://www.zimbra.com/license
# 
# Software distributed under the License is distributed on an "AS IS"
# basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
# the License for the specific language governing rights and limitations
# under the License.
# 
# The Original Code is: Zimbra Collaboration Suite Server.
# 
# The Initial Developer of the Original Code is Zimbra, Inc.
# Portions created by Zimbra are Copyright (C) 2005, 2006 Zimbra, Inc.
# All Rights Reserved.
# 
# Contributor(s):
# 
# ***** END LICENSE BLOCK *****
# 

if [ x`whoami` != "xzimbra" ]; then
  echo "$0 must be run as user zimbra"
  exit 1
fi

zimbra_key_priv=zimbra.priv
zimbra_crt=zimbra.crt
zimbra_csr=zimbra.csr
ca_pem=ca/ca.pem
ca_csr=ca/ca.csr
ca_key=ca/ca.key
server_key=server/server.key
server_crt=server/server.crt
server_csr=server/server.csr
jetty_crt=mailbox.crt
mailbox_crt=mailbox.crt

validation_days=5500
#Sample subject with the RDN values
#SUBJECT=/C=JavaGuo/ST=EFD/L=ABC/O=Zimbra\ TestCert/CN=admindev.zimbra.com
SUBJECT=/C=US
platform=`/opt/zimbra/libexec/get_plat_tag.sh 2> /dev/null` || exit 1
source `dirname $0`/zmshutil || exit 1
zmsetvars \
  zimbra_home \
  zimbra_log_directory \
  zimbra_tmp_directory \
  zimbra_java_home \
  mailboxd_directory \
  mailboxd_keystore \
  postfix_smtpd_tls_cert_file \
  postfix_smtpd_tls_key_file
  
#echo "postfix_smtpd_tls_key_file = $postfix_smtpd_tls_key_file "
#echo "postfix_smtpd_tls_cert_file = $postfix_smtpd_tls_cert_file "
#echo "mailboxd_keystore = $mailboxd_keystore "
#echo "mailboxd_directory = $mailboxd_directory "

zimbra_ssl_directory=${zimbra_home}/ssl/zimbra
export JAVA_HOME=$zimbra_java_home
zimbra_conf_directory=${zimbra_home}/conf
#TODO: zimbra_cert_manager needs to be created during the installation time
cert_ext_jar=${zimbra_home}/lib/ext/zimbra_cert_manager/zimbra_cert_manager.jar

# this avoid "unable to write 'random state' errors from openssl
#echo "zimbra_tmp_directory = ${zimbra_tmp_directory} "
#mkdir -p ${zimbra_tmp_directory}
export RANDFILE=${zimbra_ssl_directory}/../.rnd
#export HOME=${zimbra_tmp_directory}
touch $RANDFILE


if [ -f "${zimbra_java_home}/lib/security/cacerts" ]; then
	CACERTS=${zimbra_java_home}/lib/security/cacerts
else
	CACERTS=${zimbra_java_home}/jre/lib/security/cacerts
fi

clean () {
	appendix=`date +%Y%m%d%H%M%S`
	echo "**Backup ${zimbra_ssl_directory}  to ${zimbra_ssl_directory}.${appendix} "
	mv ${zimbra_ssl_directory} ${zimbra_ssl_directory}.${appendix}
	echo "**Create directory ${zimbra_ssl_directory} "
	mkdir -p ${zimbra_ssl_directory}
	mkdir -p ${zimbra_ssl_directory}/server
}

getHostInfo() {

	if [ "x$platform" = "xMACOSX" -o "x$platform" = "xMACOSXx86" ]; then
		HH=`hostname`
		return
	else
		H=`hostname --fqdn`
	fi
	HH=`echo $H | grep '\.'`

	if [ "x$HH" = "x" ]; then
		echo "Error - fully qualified host name not found - please correct"
		exit
	fi

}

createKey() {

	echo "** Creating private key and CSR"
	echo
	
	echo openssl req -batch -config ${zimbra_ssl_directory}/zmssl.cnf -new -newkey rsa:1024 -nodes -out ${zimbra_ssl_directory}/${zimbra_csr}   -keyout ${zimbra_ssl_directory}/${zimbra_key_priv}
	
	openssl req -batch -config ${zimbra_ssl_directory}/zmssl.cnf -new -newkey rsa:1024 -nodes -out ${zimbra_ssl_directory}/${zimbra_csr}   -keyout ${zimbra_ssl_directory}/${zimbra_key_priv}
	
}


createCert() {

	echo "** Creating Cert"
	echo
	#echo " openssl x509 -trustout -signkey ${zimbra_ssl_directory}/${zimbra_key_priv} -days 365 -req -in ${zimbra_ssl_directory}/${zimbra_csr}  -out ${zimbra_ssl_directory}/${zimbra_crt} "

	openssl x509 -trustout -signkey ${zimbra_ssl_directory}/${zimbra_key_priv} -days ${validation_days} -req -in ${zimbra_ssl_directory}/${zimbra_csr} -set_serial `date "+%s"` -out ${zimbra_ssl_directory}/${zimbra_crt}
	
	#createCA

}

#NOT USED
createCA () {
	mkdir -p ${zimbra_ssl_directory}/ca
	mkdir -p ${zimbra_ssl_directory}/cert
	mkdir -p ${zimbra_ssl_directory}/server

	cp -f ${zimbra_ssl_directory}/${zimbra_key_priv} ${zimbra_ssl_directory}/${ca_key}
	cp -f ${zimbra_ssl_directory}/${zimbra_csr} ${zimbra_ssl_directory}/${ca_csr}
	cp -f ${zimbra_ssl_directory}/${zimbra_crt} ${zimbra_ssl_directory}/${ca_pem}
}


selfSign () {
	echo "** Self-sign the cert"
	echo 
	
	#1. generates a key pair (private and public key) in the file jetty.key:
	# openssl genrsa -des3 -out ${zimbra_ssl_directory}/${zimbra_key_priv}  -passout pass:zimbra
	createKey
	
	#2. generates a certificate for the key into the file jetty.crt
	# openssl req -new -x509 -key ${zimbra_ssl_directory}/${zimbra_key_priv} -passin pass:zimbra -out ${zimbra_ssl_directory}/${zimbra_crt} -days ${validation_days} -set_serial `date "+%s"` -config ${zimbra_ssl_directory}/zmssl.cnf -batch || exit 1
	createCert
	
	#3. Delete, then load a PEM encoded certificate in the jetty.crt file into a JSSE keystore:
	rm -f ${mailboxd_keystore}
	#keytool -import -noprompt -keystore  ${mailboxd_keystore}  -alias jetty -file ${zimbra_ssl_directory}/${zimbra_crt} -trustcacerts -storepass zimbra
	
	#4. Loading keys and certificates via PKCS12 (you need both the private key and the certificate in the keystore.)
	#openssl pkcs12  -inkey ${zimbra_ssl_directory}/${zimbra_key_priv} -passin pass:zimbra -in ${zimbra_ssl_directory}/${zimbra_crt} -export -out ${zimbra_ssl_directory}/jetty.pkcs12 -passout pass:zimbra
	cp -f  ${zimbra_ssl_directory}/${zimbra_crt} ${zimbra_ssl_directory}/${jetty_crt}
	openssl pkcs12  -inkey ${zimbra_ssl_directory}/${zimbra_key_priv} -in ${zimbra_ssl_directory}/${jetty_crt} -export -out ${zimbra_ssl_directory}/jetty.pkcs12 -passout pass:zimbra
	
	#5. Load the resulting PKCS12 file into a JSSE keystore
	#TODO: jetty.jar may not exist. Build script needs to create a symbolic link
	#TODO: Need to modify the PKCS12Import source, so the script won't prompt for the keystore password, refer to http://www.jdocs.com/tab/113/org/mortbay/util/PKCS12Import.html
	java -classpath ${cert_ext_jar} com.zimbra.cert.MyPKCS12Import ${zimbra_ssl_directory}/jetty.pkcs12 ${mailboxd_keystore}
}

commericalSigned () {
	echo "**Create commercially signed key"
	createKey



}

importCA() {
	echo "** Importing CA"
	echo
	keytool -delete -alias my_ca -keystore ${CACERTS} -storepass changeit
	keytool -import -noprompt -keystore ${CACERTS} -file ${zimbra_ssl_directory}/${zimbra_crt} -alias my_ca -storepass changeit	
}


deployCert () {
	echo " cp -f ${zimbra_crt} ${postfix_smtpd_tls_cert_file} "
	cp -f ${zimbra_ssl_directory}/${mailbox_crt} ${postfix_smtpd_tls_cert_file}
	
	echo " cp -f ${zimbra_key_priv} ${postfix_smtpd_tls_key_file} "
    	cp -f ${zimbra_ssl_directory}/${zimbra_key_priv} ${postfix_smtpd_tls_key_file}
    	
    	cp -f ${zimbra_ssl_directory}/${server_crt}   ${zimbra_conf_directory}/slapd.crt
	cp -f ${zimbra_ssl_directory}/${server_key} ${zimbra_conf_directory}/slapd.key
    	
    	cp -f ${zimbra_ssl_directory}/${server_crt}   ${zimbra_conf_directory}/perdition.pem
	cp -f ${zimbra_ssl_directory}/${server_key} ${zimbra_conf_directory}/perdition.key
	
	cp -f ${zimbra_ssl_directory}/${server_crt}   ${zimbra_conf_directory}/nginx.crt
	cp -f ${zimbra_ssl_directory}/${server_key} ${zimbra_conf_directory}/nginx.key
	
	mkdir -p ${zimbra_conf_directory}/ca
	cp -f ${zimbra_ssl_directory}/${zimbra_key_priv} ${zimbra_conf_directory}/ca/ca.key
	cp -f ${zimbra_ssl_directory}/${zimbra_crt}  ${zimbra_conf_directory}/ca/ca.pem
	
	importCA
}



createConf() {

	ALTNAMES=""
	for alt in $*; do
		if [ "x$ALTNAMES" = "x" ]; then
			ALTNAMES="subjectAltName = DNS:${zimbra_server_hostname},DNS:${alt}"
		else
			ALTNAMES="${ALTNAMES},DNS:${alt}"
		fi
	done

	cat ${zimbra_conf_directory}/zmssl.cnf.new | sed -e "s/@@HOSTNAME@@/$zimbra_server_hostname/" \
		-e "s/@@ALTNAMES@@/$ALTNAMES/" > ${zimbra_ssl_directory}/zmssl.cnf
}

createSerial() {
  SER=`date "+%s"`
  echo "$SER" > ${zimbra_ssl_directory}/ca/ca.srl
}

createCertReq() {
	
	
	echo "** Creating server cert request"
	echo
	
	rm -rf ${zimbra_ssl_directory}/newCA
	#mkdir -p ${zimbra_ssl_directory}/newCA/newcerts
	#touch ${zimbra_ssl_directory}/newCA/index.txt
	#createSerial
		
	openssl req -new -nodes -out ${zimbra_ssl_directory}/${server_csr} -keyout ${zimbra_ssl_directory}/${server_key} -newkey rsa:1024 -config ${zimbra_ssl_directory}/zmssl.cnf  -batch || exit 1
}

signCertReq() {

	echo "** Signing cert request"
	echo

#	openssl ca -out ${zimbra_ssl_directory}/server/server.crt -notext -config ${zimbra_ssl_directory}/zmssl.cnf -in ${zimbra_ssl_directory}/server/server.csr -keyfile ${zimbra_ssl_directory}/ca/ca.key   -cert ${zimbra_ssl_directory}/ca/ca.pem -batch || exit 1
	
	
	
	openssl x509 -req -in ${zimbra_ssl_directory}/${server_csr}  -CA ${zimbra_ssl_directory}/${zimbra_crt} -CAkey ${zimbra_ssl_directory}/${zimbra_key_priv} -days ${validation_days} -set_serial `date "+%s"`  -out ${zimbra_ssl_directory}/${server_crt}
	
}

showCertInfo() {
	app=$1
	in_cert=$2
	
	if [ "x${app}" = "x" ]  || [  x"$app" != "xmailbox" -a x"$app" != "xserver" ]; then
		usage
	fi
	
	if [ "x${in_cert}" = "x" ]; then
		if [  x"$app" != "xmailbox" ]; then
			in_cert=${zimbra_ssl_directory}/${mailbox_crt}
		elif [  x"$app" != "xserver" ]; then
			in_cert=${zimbra_ssl_directory}/${server_crt}
		else
			usage
		fi
	fi
 
	openssl x509  -in ${in_cert} -dates -subject -issuer -noout
}


###Main Execution###

usage () {
	echo "Usage: "
	echo "1) $0 view [mailbox|server] <certfile>"
	echo "2) $0 install "
	
	echo "Comments:  Default <certfile> is ${zimbra_ssl_directory}/${server_crt} for server and  ${zimbra_ssl_directory}/${mailbox_crt} for mailbox. "
	exit 1;
}


if [ $# = 0 ]; then
  usage
fi

ACTION=$1
shift

# check for valid usage
if [ x"$ACTION" = "x" ] || [  x"$ACTION" != "xview" -a x"$ACTION" != "xinstall" ]; then
 	usage
elif [ x"$ACTION" = "xview" ]; then
	 showCertInfo $@ 
elif [ x"$ACTION" = "xinstall" ]; then
	clean 
	
	#zmcreateca
	#createCA
	
	createConf "$@" 
	
	selfSign 
	
	createCertReq
	
	signCertReq
	
	deployCert
fi

exit 0 

