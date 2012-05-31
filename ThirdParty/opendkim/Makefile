OPENDKIM_ROOT := $(shell pwd)
P4_ROOT ?= $(shell cd $(OPENDKIM_ROOT)/../..; pwd)
MAKE ?= make

BUILD_PLATFORM ?= $(shell sh $(P4_ROOT)/ZimbraBuild/rpmconf/Build/get_plat_tag.sh)

ifeq ($(BUILD_PLATFORM), )
	BUILD_PLATFORM := "UNKNOWN"
endif

MAKEARGS ?= -j2
ZIMBRA_HOME ?= /opt/zimbra

PATCH	:=

ifdef BETA
	include $(OPENDKIM_ROOT)/../beta_versions.def
else
	include $(OPENDKIM_ROOT)/../versions.def
endif

LDFLAGS	:=	LDFLAGS="-L$(OPENSSL_LIB_DIR) -L$(CYRUS_LIB_DIR) -L$(LDAP_LIB_DIR) -L$(LIBTOOL_LIB_DIR) -L$(ZIMBRA_HOME)/libxml2/lib \
		 -Wl,-rpath,$(OPENSSL_LIB_DIR) -Wl,-rpath,$(CYRUS_LIB_DIR) -Wl,-rpath,$(LDAP_LIB_DIR) -Wl,-rpath,$(LIBTOOL_LIB_DIR)"

DKIM_TGZ_TARGET := $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/opendkim/opendkim-$(DKIM_VERSION).tgz

files	:= $(wildcard src/opendkim-$(DKIM_VERSION).tar.gz)

all: checksrc allclean build tar

checksrc:
	$(if $(files), @echo "", exit 1)

build:
	(tar xzf src/opendkim-$(DKIM_VERSION).tar.gz; \
	cd opendkim-$(DKIM_VERSION); \
	patch -g0 -N -p1 < ../patches/dkim-mdb.patch; \
	$(LDFLAGS) \
	LD_LIBRARY_PATH=$(OPENSSL_LIB_DIR):$(CYRUS_LIB_DIR):$(LIBTOOL_LIB_DIR) \
	ac_cv_path_SENDMAIL_PATH=/opt/zimbra/postfix/sbin/sendmail \
	CPPFLAGS="-I$(ZIMBRA_HOME)/libxml2/include/libxml2 -I$(ZIMBRA_HOME)/cyrus-sasl-$(CYRUS_VERSION)/include -I$(ZIMBRA_HOME)/libxml2/include -I$(ZIMBRA_HOME)/curl/include \
		  -I$(ZIMBRA_HOME)/openssl-$(OPENSSL_VERSION)/include -I$(ZIMBRA_HOME)/libtool-$(LIBTOOL_VERSION)/include $(CPPFLAG)" \
	CFLAGS="-g -O0" \
	$(ENVMOD) ./configure --prefix=$(ZIMBRA_HOME)/opendkim-$(DKIM_VERSION) \
	--enable-poll \
	--enable-adsp_lists \
	--enable-atps \
	--enable-rate_limit \
	--enable-replace_rules \
	--enable-redirect \
	--enable-resign \
	--enable-select_canonicalization \
	--enable-selector_header \
	--enable-sender_macro \
	--enable-vbr \
	--enable-xtags \
	--enable-default_sender \
	--enable-rpath \
	--enable-arlib \
	--with-openssl=/opt/zimbra/openssl \
	--with-milter=/opt/zimbra/sendmail \
	--with-libmemcached=/opt/zimbra/libmemcached \
	--with-openldap=/opt/zimbra/openldap \
	--with-sasl=/opt/zimbra/cyrus-sasl \
	--with-libcurl=/opt/zimbra/curl \
	--with-mdb=/opt/zimbra/openldap \
	--without-db \
	--with-libxml2=/opt/zimbra/libxml2; \
	LD_RUN_PATH=$(OPENSSL_LIB_DIR):$(CYRUS_LIB_DIR):$(LIBTOOL_LIB_DIR) $(MAKE) $(MAKEARGS); \
	LD_RUN_PATH=$(OPENSSL_LIB_DIR):$(CYRUS_LIB_DIR):$(LIBTOOL_LIB_DIR) $(MAKE) install STRIP="";)
	(cd $(ZIMBRA_HOME); ln -s opendkim-$(DKIM_VERSION) opendkim)

tar:
	mkdir -p $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/opendkim
	(cd $(ZIMBRA_HOME); tar czf $(DKIM_TGZ_TARGET) opendkim-$(DKIM_VERSION))

p4edit: $(DKIM_TGZ_TARGET)
	p4 add $(DKIM_TGZ_TARGET)
	p4 edit $(DKIM_TGZ_TARGET)

clean:
	/bin/rm -rf opendkim-$(DKIM_VERSION)

allclean: clean
	/bin/rm -rf $(ZIMBRA_HOME)/opendkim-$(DKIM_VERSION)
	/bin/rm -rf $(ZIMBRA_HOME)/opendkim
	rm -f $(DKIM_TGZ_TARGET)
