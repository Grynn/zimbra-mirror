POSTFIX_ROOT := $(shell pwd)

P4_ROOT ?= $(shell cd $(POSTFIX_ROOT)/../..; pwd)
MAKE ?= make
SED ?= sed
MAKEARGS ?= -j2

BUILD_PLATFORM ?= $(shell sh $(P4_ROOT)/ZimbraBuild/rpmconf/Build/get_plat_tag.sh)

ZIMBRA_HOME ?= /opt/zimbra

ifdef BETA
	include $(POSTFIX_ROOT)/../beta_versions.def
else
	include $(POSTFIX_ROOT)/../versions.def
endif

DBINC	:=
DBLIB	:=
DBFLAG	:=

ifdef OCTOPUS
	DBFLAG	:= -DNO_DB
	POSTFIX_TGZ_TARGET := \
	$(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/postfix/postfix-octopus-$(POSTFIX_VERSION).tgz
	PATCH	:= patch -g0 -p1 < ../patches/postfix-no-bdb.patch;
else
	DBINC := -I/opt/zimbra/bdb-$(BDB_VERSION)/include
	DBLIB := -L/opt/zimbra/bdb-$(BDB_VERSION)/lib -ldb
	POSTFIX_TGZ_TARGET := \
	$(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/postfix/postfix-$(POSTFIX_VERSION).tgz
	PATCH	:= patch -g0 -p1 < ../patches/postfix-zimbra-bdb.patch;
endif

PCRE_DEF := -DHAS_PCRE
PCRE_INCLUDE := -I/usr/include/pcre
PCRE_LIB := -lpcre

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

ifeq ($(BUILD_PLATFORM), MACOSXx86_10.6)
	OS_REL := 10
	OS_PLAT := $(shell uname -m)
	MYSQL_VERSION := standard-$(MYSQL_VERSION_STRING)-apple-darwin$(OS_REL)-$(OS_PLAT)
	MYSQL_CLIENT_VERSION := standard-$(MYSQL_VERSION_STRING)-apple-darwin$(OS_REL)-$(OS_PLAT)
endif 

ifeq ($(BUILD_PLATFORM), MACOSXx86_10.7)
	OS_REL := 11
	OS_PLAT := $(shell uname -m)
	MYSQL_VERSION := standard-$(MYSQL_VERSION_STRING)-apple-darwin$(OS_REL)-$(OS_PLAT)
	MYSQL_CLIENT_VERSION := standard-$(MYSQL_VERSION_STRING)-apple-darwin$(OS_REL)-$(OS_PLAT)
endif 

SYSTEM_LIB := /usr/lib

ifeq ($(BUILD_PLATFORM), RHEL4_64)
	SYSTEM_LIB := /usr/lib64
endif

ifeq ($(BUILD_PLATFORM), CentOS4_64)
	SYSTEM_LIB := /usr/lib64
endif

ifeq ($(BUILD_PLATFORM), MACOSX)
	MYSQL_VERSION := standard-$(MYSQL_VERSION_STRING)-apple-darwin8.2.0-powerpc
	MYSQL_CLIENT_VERSION := standard-$(MYSQL_VERSION_STRING)-apple-darwin8.2.0-powerpc
	PCRE_DEF := -DHAS_PCRE
	PCRE_INCLUDE := -I/opt/zimbra/include
	PCRE_LIB := -L/opt/zimbra/lib -lpcre
	FD_SIZE := -DFD_SETSIZE=32768
endif

LD_RUN_PATH := /opt/zimbra/lib:/opt/zimbra/mysql-$(MYSQL_VERSION)/lib:/opt/zimbra/openldap-$(LDAP_VERSION)/lib:/opt/zimbra/openssl-$(OPENSSL_VERSION)/lib:/opt/zimbra/cyrus-sasl-$(CYRUS_VERSION)/lib:/opt/zimbra/bdb-$(BDB_VERSION)/lib
MYSQL_INC := /opt/zimbra/mysql-$(MYSQL_VERSION)/include
MYSQL_LIB := /opt/zimbra/mysql-$(MYSQL_VERSION)/lib


ifeq (MACOSXx86,$(findstring MACOSXx86,$(BUILD_PLATFORM)))
	EXTRALIBS := -lresolv
	PCRE_DEF := -DHAS_PCRE
	PCRE_INCLUDE := -I/opt/zimbra/include
	PCRE_LIB := -L/opt/zimbra/lib -lpcre
	FD_SIZE := -DFD_SETSIZE=32768
	URANDOM := -DPREFERRED_RAND_SOURCE=\"dev:/dev/urandom\"
endif

INSTALL_PARMS := install_root=/../
ifeq (MACOSXx86_10, $(findstring MACOSXx86_10, $(BUILD_PLATFORM)))
	INSTALL_PARMS := install_root=/../ setgid_group=_postdrop mail_owner=_postfix
endif

files   := $(wildcard src/postfix-$(POSTFIX_RELEASE).tar.gz)

all: checksrc allclean build install tar

checksrc:
	$(if $(files), @echo "", exit 1)

build:
	tar xzf src/postfix-$(POSTFIX_RELEASE).tar.gz
ifeq ($(BUILD_PLATFORM), MACOSXx86_10.6)
	$(SED) -i -e 's/#define RESOLVE_H_NEEDS_NAMESER8_COMPAT_H//' postfix-$(POSTFIX_RELEASE)/src/util/sys_defs.h
endif
	(mv postfix-$(POSTFIX_RELEASE) postfix-$(POSTFIX_VERSION); \
	cd postfix-$(POSTFIX_VERSION); \
	$(PATCH) \
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
	-DDEF_DATA_DIR=\"/opt/zimbra/data/postfix/data\" \
	-DUSE_SASL_AUTH \
	-DUSE_CYRUS_SASL \
	$(DBFLAG) \
	-DHAS_LDAP -DHAS_MYSQL -DUSE_TLS $(DBINC) $(PCRE_DEF) $(PCRE_INCLUDE) $(FD_SIZE) \
	$(URANDOM) \
	-DUSE_LDAP_SASL \
	-I/opt/zimbra/openldap-$(LDAP_VERSION)/include \
	-I$(MYSQL_INC) \
	-I/opt/zimbra/openssl-$(OPENSSL_VERSION)/include \
	-I/opt/zimbra/cyrus-sasl-$(CYRUS_VERSION)/include/sasl -I/usr/include' \
	AUXLIBS='$(EXTRALIBS) -L/opt/zimbra/cyrus-sasl-$(CYRUS_VERSION)/lib \
	$(DBLIB) -L/opt/zimbra/openldap-$(LDAP_VERSION)/lib $(PCRE_LIB) \
	-lldap -llber \
	-L$(MYSQL_LIB) \
	-L/opt/zimbra/openssl-$(OPENSSL_VERSION)/lib \
	-lmysqlclient -lz -lm -L$(SYSTEM_LIB) -lsasl2 -lpthread -lssl -lcrypto'; \
	LD_RUN_PATH=$(LD_RUN_PATH) $(MAKE) $(MAKEARGS);)

install:
	(cd postfix-$(POSTFIX_VERSION); \
	 $(MAKE) non-interactive-package $(INSTALL_PARMS); \
	sed -e 's|postconf|/opt/zimbra/bin/postconf|' auxiliary/qshape/qshape.pl > $(ZIMBRA_HOME)/postfix-$(POSTFIX_VERSION)/sbin/qshape.pl;\
	chmod a+rx $(ZIMBRA_HOME)/postfix-$(POSTFIX_VERSION)/sbin/qshape.pl;)
	(cd $(ZIMBRA_HOME); ln -s postfix-$(POSTFIX_VERSION) postfix)

tar:
	mkdir -p $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/postfix
	(cd $(ZIMBRA_HOME); tar czf $(POSTFIX_TGZ_TARGET) postfix-$(POSTFIX_VERSION))
	chmod -R a+w $(POSTFIX_TGZ_TARGET)

p4edit: $(POSTFIX_TGZ_TARGET)
	p4 add $(POSTFIX_TGZ_TARGET)
	p4 edit $(POSTFIX_TGZ_TARGET)

clean:
	rm -rf postfix-$(POSTFIX_VERSION)

allclean: clean zimclean

zimclean:
	rm -rf $(ZIMBRA_HOME)/postfix-$(POSTFIX_VERSION) $(ZIMBRA_HOME)/postfix
	rm -f $(POSTFIX_TGZ_TARGET)
