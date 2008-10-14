POSTFIX_ROOT := $(shell pwd)

ZIMBRA_HOME ?= /opt/zimbra
POSTFIX_RELEASE ?= 2.5.3
POSTFIX_PATCH_LEVEL ?= 2z
POSTFIX_VERSION ?= $(POSTFIX_RELEASE).$(POSTFIX_PATCH_LEVEL)

P4_ROOT ?= $(shell cd $(POSTFIX_ROOT)/../..; pwd)
MAKE ?= make
MAKEARGS ?= -j2

BUILD_PLATFORM ?= $(shell sh $(P4_ROOT)/ZimbraBuild/rpmconf/Build/get_plat_tag.sh)

PCRE_DEF := -DHAS_PCRE
PCRE_INCLUDE := -I/usr/include/pcre
PCRE_LIB := -lpcre
SASL_VERSION ?= 2.1.22.3z
LDAP_VERSION ?= 2.4.12.2z
OPENSSL_VERSION ?= 0.9.8i
MYSQL_VERSION_STRING ?= 5.0.67
MYSQL_VERSION ?= standard-$(MYSQL_VERSION_STRING)-pc-linux-gnu-i686-glibc23
MYSQL_CLIENT_VERSION ?= standard-$(MYSQL_VERSION_STRING)-clientlibs
ifeq ($(BUILD_PLATFORM), MACOSX)
	MYSQL_VERSION := standard-$(MYSQL_VERSION_STRING)-apple-darwin8.2.0-powerpc
	MYSQL_CLIENT_VERSION := standard-$(MYSQL_VERSION_STRING)-apple-darwin8.2.0-powerpc
endif

ifeq (MACOSXx86,$(findstring MACOSXx86,$(BUILD_PLATFORM)))
	MYSQL_VERSION := standard-$(MYSQL_VERSION_STRING)-apple-darwin8.2.0-i686
	MYSQL_CLIENT_VERSION := standard-$(MYSQL_VERSION_STRING)-apple-darwin8.2.0-i686
endif     

SYSTEM_LIB := /usr/lib
BDB_VERSION ?= 4.7.25.2

POSTFIX_TGZ_TARGET := \
	$(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/postfix/postfix-$(POSTFIX_VERSION).tgz

ifeq ($(BUILD_PLATFORM), RHEL4_64)
	SYSTEM_LIB := /usr/lib64
endif

ifeq ($(BUILD_PLATFORM), CentOS4_64)
	SYSTEM_LIB := /usr/lib64
endif

LD_RUN_PATH := /opt/zimbra/lib:/opt/zimbra/mysql-$(MYSQL_VERSION)/lib:/opt/zimbra/mysql-$(MYSQL_VERSION)/lib/mysql:/opt/zimbra/openldap-clibs-$(LDAP_VERSION)/lib:/opt/zimbra/openssl-$(OPENSSL_VERSION)/lib:/opt/zimbra/cyrus-sasl-$(SASL_VERSION)/lib:/opt/zimbra/bdb-$(BDB_VERSION)/lib

DBINC := -I/opt/zimbra/bdb-$(BDB_VERSION)/include
DBLIB := -L/opt/zimbra/bdb-$(BDB_VERSION)/lib

ifeq ($(BUILD_PLATFORM), MACOSX)
	MYSQL_VERSION := standard-$(MYSQL_VERSION_STRING)-apple-darwin8.2.0-powerpc
	MYSQL_CLIENT_VERSION := standard-$(MYSQL_VERSION_STRING)-apple-darwin8.2.0-powerpc
	PCRE_DEF := -DHAS_PCRE
	PCRE_INCLUDE := -I/opt/zimbra/include
	PCRE_LIB := -L/opt/zimbra/lib -lpcre
	FD_SIZE := -DFD_SETSIZE=32768
endif

ifeq (MACOSXx86,$(findstring MACOSXx86,$(BUILD_PLATFORM)))
	MYSQL_VERSION := standard-$(MYSQL_VERSION_STRING)-apple-darwin8.2.0-i686
	MYSQL_CLIENT_VERSION := standard-$(MYSQL_VERSION_STRING)-apple-darwin8.2.0-i686
	PCRE_DEF := -DHAS_PCRE
	PCRE_INCLUDE := -I/opt/zimbra/include
	PCRE_LIB := -L/opt/zimbra/lib -lpcre
	FD_SIZE := -DFD_SETSIZE=32768
endif

all: prep build install tar

prep:
	rm -rf $(ZIMBRA_HOME)/postfix-$(POSTFIX_VERSION)
	mkdir -p $(ZIMBRA_HOME)/postfix-$(POSTFIX_VERSION)/conf
#	cp main.cf $(ZIMBRA_HOME)/postfix-$(POSTFIX_VERSION)/conf

build:
	(tar xzf src/postfix-$(POSTFIX_RELEASE).tar.gz; \
	mv postfix-$(POSTFIX_RELEASE) postfix-$(POSTFIX_VERSION); \
	cd postfix-$(POSTFIX_VERSION); \
	patch -g0 -p1 < ../patches/postfix-zimbra-bdb.patch; \
	patch -g0 -p1 < ../patches/postfix-main-cf-zimbra.patch; \
	$(MAKE) $(MAKEARGS)  makefiles \
	OPT="-O2" \
	DEBUG="-g" \
	CCARGS='-DDEF_COMMAND_DIR=\"/opt/zimbra/postfix-$(POSTFIX_VERSION)/sbin\" \
	-DDEF_DAEMON_DIR=\"/opt/zimbra/postfix-$(POSTFIX_VERSION)/libexec\" \
	-DDEF_CONFIG_DIR=\"/opt/zimbra/postfix-$(POSTFIX_VERSION)/conf\" \
	-DDEF_QUEUE_DIR=\"/opt/zimbra/data/postfix/spool\" \
	-DDEF_SENDMAIL_PATH=\"/opt/zimbra/postfix-$(POSTFIX_VERSION)/sbin/sendmail\" \
	-DDEF_NEWALIAS_PATH=\"/opt/zimbra/postfix-$(POSTFIX_VERSION)/sbin/newaliases\" \
	-DDEF_MAILQ_PATH=\"/opt/zimbra/postfix-$(POSTFIX_VERSION)/sbin/mailq\" \
	-DDEF_MANPAGE_DIR=\"/opt/zimbra/postfix-$(POSTFIX_VERSION)/man\" \
	-DDEF_HTML_DIR=\"no\" \
	-DDEF_README_DIR=\"no\" \
	-DDEF_SENDMAIL_PATH=\"/opt/zimbra/postfix-$(POSTFIX_VERSION)/sbin/sendmail\" \
	-DDEF_DATA_DIR=\"/opt/zimbra/data/postfix\" \
	-DUSE_SASL_AUTH \
	-DUSE_CYRUS_SASL \
	-DHAS_LDAP -DHAS_MYSQL -DUSE_TLS $(DBINC) $(PCRE_DEF) $(PCRE_INCLUDE) $(FD_SIZE) \
	-DUSE_LDAP_SASL \
	-I/opt/zimbra/openldap-$(LDAP_VERSION)/include \
	-I/opt/zimbra/mysql-$(MYSQL_VERSION)/include \
	-I/opt/zimbra/mysql-$(MYSQL_VERSION)/include/mysql \
	-I/opt/zimbra/openssl-$(OPENSSL_VERSION)/include \
	-I/opt/zimbra/cyrus-sasl-$(SASL_VERSION)/include/sasl -I/usr/include' \
	AUXLIBS='-L/opt/zimbra/cyrus-sasl-$(SASL_VERSION)/lib \
	$(DBLIB) -ldb -L/opt/zimbra/openldap-clibs-$(LDAP_VERSION)/lib $(PCRE_LIB) \
	-lldap -llber -L/opt/zimbra/mysql-$(MYSQL_VERSION)/lib \
	-L/opt/zimbra/mysql-$(MYSQL_VERSION)/lib/mysql \
	-L/opt/zimbra/openssl-$(OPENSSL_VERSION)/lib \
	-lmysqlclient -lz -lm -L$(SYSTEM_LIB) -lsasl2 -lpthread -lssl -lcrypto'; \
	LD_RUN_PATH=$(LD_RUN_PATH) $(MAKE) $(MAKEARGS);)

install:
ifeq ($(BUILD_PLATFORM), MACOSXx86_10.5)
	(cd postfix-$(POSTFIX_VERSION); \
	$(SHELL) postfix-install -non-interactive  setgid_group=_postdrop mail_owner=_postfix; \
	cp auxiliary/qshape/qshape.pl $(ZIMBRA_HOME)/postfix-$(POSTFIX_VERSION)/sbin; \
	chmod a+rx $(ZIMBRA_HOME)/postfix-$(POSTFIX_VERSION)/sbin/qshape.pl;)
	(cd $(ZIMBRA_HOME); ln -s postfix-$(POSTFIX_VERSION) postfix)
else
	(cd postfix-$(POSTFIX_VERSION); \
	$(MAKE) upgrade; \
	cp auxiliary/qshape/qshape.pl $(ZIMBRA_HOME)/postfix-$(POSTFIX_VERSION)/sbin;\
	chmod a+rx $(ZIMBRA_HOME)/postfix-$(POSTFIX_VERSION)/sbin/qshape.pl;)
	(cd $(ZIMBRA_HOME); ln -s postfix-$(POSTFIX_VERSION) postfix)
endif

tar:
	mkdir -p $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/postfix
	(cd $(ZIMBRA_HOME); tar czf $(POSTFIX_TGZ_TARGET) postfix-$(POSTFIX_VERSION))
	chmod -R a+w $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/postfix

p4edit: $(POSTFIX_TGZ_TARGET)
	p4 add $(POSTFIX_TGZ_TARGET)
	p4 edit $(POSTFIX_TGZ_TARGET)

clean:
	rm -rf postfix-$(POSTFIX_VERSION)

allclean: clean zimclean

zimclean:
	rm -rf $(ZIMBRA_HOME)/postfix-$(POSTFIX_VERSION) $(ZIMBRA_HOME)/postfix
