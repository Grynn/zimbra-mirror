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
comm_crt=comm.crt

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
zimbra_csr_directory=${zimbra_home}/ssl/csr

##????Which variable is for the zimbarAdmin directory?
current_csr_4_download=${zimbra_home}/mailboxd/webapps/zimbraAdmin/tmp/current.csr
#zimbra_comm_csr_directory=${zimbra_home}/ssl/comm_csr
#zimbra_self_csr_directory=${zimbra_home}/ssl/self_csr
#TODO: zimbra_cert_manager needs to be created during the installation time
cert_ext_jar=${zimbra_home}/lib/ext/zimbra_cert_manager/zimbra_cert_manager.jar


# this avoid "unable to write 'random state' errors from openssl
#echo "zimbra_tmp_directory = ${zimbra_tmp_directory} "
#mkdir -p ${zimbra_tmp_directory}
export RANDFILE=${zimbra_home}/ssl/.rnd
#export HOME=${zimbra_tmp_directory}
touch $RANDFILE

#Default subject with the RDN values
SUBJECT="/C=US/ST=CA/L=San Mateo/O=Zimbra/OU=Zimbra Collaboration Suite/CN=${zimbra_server_hostname}"
validation_days=365

#OUTPUT_PREFIX="##### OUTPUT:"
ERROR_PREFIX="XXXXX ERROR:"

if [ -f "${zimbra_java_home}/lib/security/cacerts" ]; then
	CACERTS=${zimbra_java_home}/lib/security/cacerts
else
	CACERTS=${zimbra_java_home}/jre/lib/security/cacerts
fi

clean () {
	if [ x"${1}" != "x" ]; then
		ACTION_ROOT_DIR=${1}
	fi
	
	if [ -d "${ACTION_ROOT_DIR}" ]; then
		appendix=`date +%Y%m%d%H%M%S`
		echo "** Backup ${ACTION_ROOT_DIR}  to ${ACTION_ROOT_DIR}.${appendix} "
		mv ${ACTION_ROOT_DIR} ${ACTION_ROOT_DIR}.${appendix}
	fi
}

initActionDir () {
	if [ x"${1}" != "x" ]; then
			ACTION_ROOT_DIR=${1}
	fi
	
	if [ ! -d "${ACTION_ROOT_DIR}" ]; then 
			echo "** Create directory ${ACTION_ROOT_DIR} "
			mkdir -p ${ACTION_ROOT_DIR}
		fi
		
		if [ ! -d "${ACTION_ROOT_DIR}/server" ]; then
			echo "** Create directory ${ACTION_ROOT_DIR}/server "
			mkdir -p ${ACTION_ROOT_DIR}/server
	fi
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
		echo "${ERROR_PREFIX}  fully qualified host name not found - please correct"
		exit 1
	fi

}

createKey() {

	echo "** Creating private key and CSR in Directory ${ACTION_ROOT_DIR}"
	echo
	
	echo openssl req -batch -subj "${SUBJECT}" -config ${ACTION_ROOT_DIR}/zmssl.cnf -new -newkey rsa:1024 -nodes -out ${ACTION_ROOT_DIR}/${zimbra_csr}   -keyout ${ACTION_ROOT_DIR}/${zimbra_key_priv}
	
	openssl req -batch -subj "${SUBJECT}" -config ${ACTION_ROOT_DIR}/zmssl.cnf -new -newkey rsa:1024 -nodes -out ${ACTION_ROOT_DIR}/${zimbra_csr}   -keyout ${ACTION_ROOT_DIR}/${zimbra_key_priv}
	
}


createCert() {

	echo "** Self-sign ${zimbra_ssl_directory}/${zimbra_crt} to create cert ${zimbra_ssl_directory}/${zimbra_crt}, which is also the CA"
	echo
	#echo " openssl x509 -trustout -signkey ${zimbra_ssl_directory}/${zimbra_key_priv} -days 365 -req -in ${zimbra_ssl_directory}/${zimbra_csr}  -out ${zimbra_ssl_directory}/${zimbra_crt} "

	openssl x509 -trustout -signkey ${zimbra_ssl_directory}/${zimbra_key_priv} -days ${validation_days} -req -in ${zimbra_ssl_directory}/${zimbra_csr} -set_serial `date "+%s"` -out ${zimbra_ssl_directory}/${zimbra_crt}
	
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



createMailboxKeystore () {
	echo "** Create the keystore ${mailboxd_keystore} "
	
	#3. Delete, then load a PEM encoded certificate in the jetty.crt file into a JSSE keystore:
	rm -f ${mailboxd_keystore}
	#keytool -import -noprompt -keystore  ${mailboxd_keystore}  -alias jetty -file ${zimbra_ssl_directory}/${zimbra_crt} -trustcacerts -storepass zimbra
	
	#4. Loading keys and certificates via PKCS12 (you need both the private key and the certificate in the keystore.)
	#openssl pkcs12  -inkey ${zimbra_ssl_directory}/${zimbra_key_priv} -passin pass:zimbra -in ${zimbra_ssl_directory}/${zimbra_crt} -export -out ${zimbra_ssl_directory}/jetty.pkcs12 -passout pass:zimbra
	
	openssl pkcs12  -inkey ${zimbra_ssl_directory}/${zimbra_key_priv} -in ${zimbra_ssl_directory}/${jetty_crt} -export -out ${zimbra_ssl_directory}/jetty.pkcs12 -passout pass:zimbra
	
	#5. Load the resulting PKCS12 file into a JSSE keystore
	#TODO: jetty.jar may not exist. Build script needs to create a symbolic link
	#TODO: Need to modify the PKCS12Import source, so the script won't prompt for the keystore password, refer to http://www.jdocs.com/tab/113/org/mortbay/util/PKCS12Import.html
	java -classpath ${cert_ext_jar} com.zimbra.cert.MyPKCS12Import ${zimbra_ssl_directory}/jetty.pkcs12 ${mailboxd_keystore}
}


importCA() {
	echo "** Importing CA ${zimbra_ssl_directory}/${zimbra_crt}"
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
	ln -s ca.pem ${zimbra_conf_directory}/ca/`openssl x509 -hash -noout -in ${zimbra_conf_directory}/ca/ca.pem`.0
	
	importCA
}



createConf() {
	cat ${zimbra_conf_directory}/zmssl.cnf.new | sed -e "s/@@HOSTNAME@@/$zimbra_server_hostname/"  > ${ACTION_ROOT_DIR}/zmssl.cnf
}

createSerial() {
  SER=`date "+%s"`
  echo "$SER" > ${zimbra_ssl_directory}/ca/ca.srl
}

createServerCert() {
	echo "*  Create Server certificate {zimbra_ssl_directory}/${server_crt}"
	
	echo "** Creating server cert request ${zimbra_ssl_directory}/${server_csr}"
	echo
	
	#rm -rf ${zimbra_ssl_directory}/newCA
	#mkdir -p ${zimbra_ssl_directory}/newCA/newcerts
	#touch ${zimbra_ssl_directory}/newCA/index.txt
	#createSerial
		
	openssl req -new -nodes -out ${zimbra_ssl_directory}/${server_csr} -keyout ${zimbra_ssl_directory}/${server_key} -newkey rsa:1024 -config ${zimbra_ssl_directory}/zmssl.cnf  -subj "${SUBJECT}" -batch || exit 1
	
	echo "** Signing cert request ${zimbra_ssl_directory}/${server_csr}"
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
	
	if [ x"${in_cert}" = "x" ]; then
		if [  x"$app" = "xmailbox" ]; then
			in_cert=${zimbra_ssl_directory}/${mailbox_crt}
		elif [  x"$app" = "xserver" ]; then
			in_cert=${zimbra_ssl_directory}/${server_crt}
		else
			usage
		fi
	fi
 	#echo "openssl x509  -in ${in_cert} -dates -subject -issuer -noout"
	openssl x509  -in ${in_cert} -dates -subject -issuer -noout
}

showcsr () {
	in_csr=$1
	if [ x"${in_csr}" = "x" ]; then
		in_csr=${zimbra_csr_directory}/${zimbra_csr}
		if [ ! -f ${in_csr} ]; then 
			in_csr=${zimbra_ssl_directory}/${zimbra_csr}
		fi
	fi
	#echo "openssl req -in ${in_csr} -subject -noout" 
	openssl req -in ${in_csr} -subject -noout
	
}

gencsr () {
	
	echo "** Generate the CSR"
	ACTION_ROOT_DIR=${zimbra_csr_directory}
	
	if [ -d ${ACTION_ROOT_DIR} ]; then			
		if [ x"${IS_NEW_CSR}" != "x-new" ]; then
			echo "${ERROR_PREFIX} The Certificate Signing Request already existed."
			usage
		fi
		
	fi
	
	clean 
	initActionDir
	createConf
	createKey
	
	cp -f ${zimbra_csr_directory}/${zimbra_csr} ${current_csr_4_download}
}

install () {
	#Arg 1 = [self|comm] (Required)
	#Arg 2 = <validation_days> (Optional)
	
	if [ x"${1}" = "x" ] || [  x"${1}" != "xself" -a x"${1}" != "xcomm" ]; then
		usage
	else	
		if [ x"${2}" != "x" ]; then
			validation_days=$2
		fi
		
		csr_dir=${zimbra_csr_directory} 
		
		if [ x"${1}" = "xcomm" ]; then
			dir=${zimbra_csr_directory}
		fi
		
		if [ ! -d "${csr_dir}" ]; then
			echo "${ERROR_PREFIX} ${csr_dir} is not found!"
			usage
		fi
		
		if [ x"${1}" = "xcomm" ]; then
			if [ -f "${csr_dir}/${comm_crt}" ]; then
				echo "*** ${csr_dir}/${comm_crt} is found."
			else
				echo "${ERROR_PREFIX}  ${csr_dir}/${comm_crt} is NOT found."
				usage
			fi
		fi
		
		echo "** Install Certs from ${csr_dir}  ...."
		
		clean ${zimbra_ssl_directory}
		mv ${csr_dir} ${zimbra_ssl_directory}
		#Delete the current.csr
		rm -f ${current_csr_4_download}
		

		createCert
		
		if [ x"${1}" = "xcomm" ]; then
			cp -f ${zimbra_ssl_directory}/${comm_crt} ${zimbra_ssl_directory}/${mailbox_crt}
		elif [ x"${1}" = "xself" ]; then
			cp -f  ${zimbra_ssl_directory}/${zimbra_crt} ${zimbra_ssl_directory}/${mailbox_crt}
		fi
	fi
	
	createMailboxKeystore
	
	createServerCert
	
	deployCert

}

verifycrt () {
	key=$1
	crt=$2
	
	if [ x"${1}" = "x" ]; then
		key=${zimbra_csr_directory}/${zimbra_key_priv} 
	fi
	
	if [ x"${2}" = "x" ]; then
		crt=${zimbra_csr_directory}/${comm_crt}
	fi
	
	if [ ! -f $key ]; then
		echo "${ERROR_PREFIX} Can't find private key  ${key}  "
		exit 1
	elif [ ! -f $crt ]; then
		echo "${ERROR_PREFIX} Can't find certificate ${crt} "
		exit 1
	else
		key_md5=`openssl rsa -noout -modulus -in ${key} | openssl md5`
		crt_md5=`openssl x509 -noout -modulus -in ${crt} | openssl md5`
	
		echo "key_md5=${key_md5}"
		echo "crt_md5=${crt_md5}"	
	fi
	
	if [ x"${key_md5}" != "x"  -a  x"${key_md5}" = x"${crt_md5}" ] ; then
		echo "Matched: valid certificate and private key matching pair"
	else
		echo "${ERROR_PREFIX} Unmatching certificate and private key pair"
		exit 1 
	fi
}


###Main Execution###

usage () {
	echo "Usage: "
	echo "1) $0 view [mailbox|server] <certfile>"
	echo "2) $0 gencsr  <-new> <subject> "
	echo "3) $0 install [self|comm] <validation_days>"
	echo "4) $0 viewcsr <csr_file>"
	echo "5) $0 verifycrt <priv_key> <certfile>"
	echo
	echo "Comments:  "
	echo "1) Default <certfile> is ${zimbra_ssl_directory}/${server_crt} for server and  ${zimbra_ssl_directory}/${mailbox_crt} for mailbox. "
	echo "2) Default <subject> is \"/C=US/ST=N_A/L=N_A/O=Zimbra Collaboration Suite/CN=${zimbra_server_hostname}\" "
	echo "3) Default <validation_days> is 365. "
	echo "4) install self is to instlal the certificates using self signed csr is in ${zimbra_csr_directory}"
	echo "5) install comm is to install the certificates using commercially signed certificate in ${zimbra_csr_directory} "
	echo "6) default <csr_file> is ${zimbra_csr_directory}/${zimbra_csr}, then  ${zimbra_ssl_directory}/${zimbra_csr} "
	echo "7) for verifycrt, by default priv_key is ${zimbra_csr_directory}/${zimbra_key_priv} and the certfile is ${zimbra_csr_directory}/${comm_crt} "

	echo
	
	exit 1;
}


if [ $# = 0 ]; then
  usage
fi

ACTION=$1
shift

ACTION_ROOT_DIR=${zimbra_ssl_directory}
 
# check for valid usage
if [ x"$ACTION" = "xview" ]; then
	 showCertInfo $@ 
elif [ x"$ACTION" = "xgencsr" ]; then
	
	if [ x"$1" = "x-new" ]; then
		IS_NEW_CSR=$1 #Allow the scripts to overwrite the existing csr
		shift
	fi
	
	#Set SUBJECT
	subj=$1
	if [ "x${subj}" != "x" ]; then 
		echo "SUBJECT=${subj}"
		SUBJECT=${subj}
	fi

	gencsr $@
	
elif [ x"$ACTION" = "xinstall" ]; then
	install $@
elif [ x"$ACTION" = "xviewcsr" ]; then
	showcsr	$@
elif [ x"$ACTION" = "xverifycrt" ]; then
	verifycrt $@
else
	usage
fi

exit 0 

