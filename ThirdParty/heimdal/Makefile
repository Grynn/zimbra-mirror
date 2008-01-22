HEIMDAL_ROOT := $(shell pwd)
P4_ROOT ?= $(shell cd $(HEIMDAL_ROOT)/../..; pwd)

BUILD_PLATFORM ?= $(shell sh $(P4_ROOT)/ZimbraBuild/rpmconf/Build/get_plat_tag.sh)

ifeq ($(BUILD_PLATFORM), )
	BUILD_PLATFORM := "UNKNOWN"
endif

ZIMBRA_HOME ?= /opt/zimbra
HEIMDAL_RELEASE ?= 0.7.2
HEIMDAL_PATCH_LEVEL ?= 1z
HEIMDAL_VERSION ?= $(HEIMDAL_RELEASE).$(HEIMDAL_PATCH_LEVEL)

HEIMDAL_TGZ_TARGET := $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/heimdal/heimdal-$(HEIMDAL_VERSION).tgz
HEIMDAL_SOURCE := $(HEIMDAL_ROOT)/heimdal-$(HEIMDAL_VERSION)

OPENSSL_VERSION ?= 0.9.8g

OPENSSL_LIB_DIR ?= $(ZIMBRA_HOME)/openssl-$(OPENSSL_VERSION)/lib
HEIMDAL_LIB_DIR ?= $(ZIMBRA_HOME)/heimdal-$(HEIMDAL_VERSION)/lib

all: build tar

build:
	(tar xzf src/heimdal-$(HEIMDAL_RELEASE).tar.gz; \
	mv heimdal-$(HEIMDAL_RELEASE) heimdal-$(HEIMDAL_VERSION); \
	cd heimdal-$(HEIMDAL_VERSION); \
	patch -p1 < ../patches/setuid.patch; \
	CFLAGS="-g -O2 -D_REENTRANT" \
	CCFLAGS="-g -O2 -D_REENTRANT" \
	COMPILE_ET="NO" \
	LDFLAGS="-L$(OPENSSL_LIB_DIR) -R$(OPENSSL_LIB_DIR)" \
	CPPFLAGS="-I$(ZIMBRA_HOME)/openssl-$(OPENSSL_VERSION)/include" \
	$(ENVMOD) ./configure --prefix=$(ZIMBRA_HOME)/heimdal-$(HEIMDAL_VERSION) \
	CCFLAGS="-g -O2 -D_REENTRANT" \
	--sysconfdir=/etc \
	--enable-shared \
	--enable-pthread-support \
	--with-openssl=$(ZIMBRA_HOME)/openssl-$(OPENSSL_VERSION) \
	--disable-berkeley-db \
	--disable-ndbm-db \
	--without-readline \
	--without-openldap \
	--without-hesiod \
	--without-ipv6; \
	cd $(HEIMDAL_SOURCE)/include; make; \
	cd $(HEIMDAL_SOURCE)/lib/roken; make; \
	cd $(HEIMDAL_SOURCE)/lib/vers; make; \
	cd $(HEIMDAL_SOURCE)/lib/com_err; make; \
	cd $(HEIMDAL_SOURCE)/lib/asn1; make; \
	cd $(HEIMDAL_SOURCE)/lib/krb5; make; \
	cd $(HEIMDAL_SOURCE)/lib/gssapi; make; \
	cd $(HEIMDAL_SOURCE)/include; make install STRIP=""; \
	cd $(HEIMDAL_SOURCE)/lib/roken; make install STRIP=""; \
	cd $(HEIMDAL_SOURCE)/lib/com_err; make install STRIP=""; \
	cd $(HEIMDAL_SOURCE)/lib/asn1; make install STRIP=""; \
	cd $(HEIMDAL_SOURCE)/lib/krb5; make install STRIP=""; \
	cd $(HEIMDAL_SOURCE)/lib/gssapi; make install STRIP="")
	(cd $(ZIMBRA_HOME); ln -s heimdal-$(HEIMDAL_VERSION) heimdal)

tar:
	mkdir -p $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/heimdal
	(cd $(ZIMBRA_HOME); tar czf $(HEIMDAL_TGZ_TARGET) heimdal-$(HEIMDAL_VERSION))

p4edit: $(HEIMDAL_TGZ_TARGET)
	p4 add $(HEIMDAL_TGZ_TARGET)
	p4 edit $(HEIMDAL_TGZ_TARGET)
	

clean:
	/bin/rm -rf heimdal-$(HEIMDAL_VERSION)

allclean: clean
	/bin/rm -rf $(ZIMBRA_HOME)/heimdal-$(HEIMDAL_VERSION)
	/bin/rm -rf $(ZIMBRA_HOME)/heimdal
