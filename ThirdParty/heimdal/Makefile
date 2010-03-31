HEIMDAL_ROOT := $(shell pwd)
P4_ROOT ?= $(shell cd $(HEIMDAL_ROOT)/../..; pwd)
MAKE ?= make
MAKEARGS ?= -j2

BUILD_PLATFORM ?= $(shell sh $(P4_ROOT)/ZimbraBuild/rpmconf/Build/get_plat_tag.sh)

ifeq ($(BUILD_PLATFORM), )
	BUILD_PLATFORM := "UNKNOWN"
endif

ZIMBRA_HOME ?= /opt/zimbra

ifdef BETA
	include $(HEIMDAL_ROOT)/../beta_versions.def
else
	include $(HEIMDAL_ROOT)/../versions.def
endif

LIBEXT	:= so
PATCH	:=
ifeq ($(BUILD_PLATFORM), MACOSXx86)
	PATCH   := patch -g0 -N -p1 < ../patches/mac.patch;
	LIBEXT	:= dylib
endif

HEIMDAL_TGZ_TARGET := $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/heimdal/heimdal-$(HEIMDAL_VERSION).tgz
HEIMDAL_SOURCE := $(HEIMDAL_ROOT)/heimdal-$(HEIMDAL_VERSION)

files   := $(wildcard src/heimdal-$(HEIMDAL_VERSION).tar.gz)

all: checksrc allclean build check tar

checksrc:
	$(if $(files), @echo "", exit 1)

check:
	@echo "Verifying heimdal build...";
	$(HEIMDAL_ROOT)/../zmcheck.pl -b $(ZIMBRA_HOME)/heimdal-$(HEIMDAL_VERSION)/bin/hxtool
	$(HEIMDAL_ROOT)/../zmcheck.pl -l $(ZIMBRA_HOME)/heimdal-$(HEIMDAL_VERSION)/lib/libgssapi.$(LIBEXT)
	$(HEIMDAL_ROOT)/../zmcheck.pl -l $(ZIMBRA_HOME)/heimdal-$(HEIMDAL_VERSION)/lib/libkrb5.$(LIBEXT)

build:
	(tar xzf src/heimdal-$(HEIMDAL_VERSION).tar.gz; \
	cd heimdal-$(HEIMDAL_VERSION); \
	$(PATCH) \
	CFLAGS="-g -O2 -D_REENTRANT" \
	CCFLAGS="-g -O2 -D_REENTRANT" \
	COMPILE_ET="NO" \
	LDFLAGS="-L$(OPENSSL_LIB_DIR) -R$(OPENSSL_LIB_DIR)" \
	CPPFLAGS="-I$(ZIMBRA_HOME)/openssl-$(OPENSSL_VERSION)/include" \
	./configure --prefix=$(ZIMBRA_HOME)/heimdal-$(HEIMDAL_VERSION) \
	CCFLAGS="-g -O2 -D_REENTRANT" \
	--sysconfdir=/etc \
	--enable-shared \
	--enable-pthread-support \
	--with-openssl=$(ZIMBRA_HOME)/openssl-$(OPENSSL_VERSION) \
	--disable-berkeley-db \
	--disable-ndbm-db \
	--disable-afs-support \
	--without-readline \
	--without-openldap \
	--without-hesiod \
	--without-ipv6; \
	cd $(HEIMDAL_SOURCE)/include; $(MAKE) $(MAKEARGS); \
	cd $(HEIMDAL_SOURCE)/lib/roken; $(MAKE) $(MAKEARGS); \
	cd $(HEIMDAL_SOURCE)/lib/vers; $(MAKE) $(MAKEARGS); \
	cd $(HEIMDAL_SOURCE)/lib/com_err; $(MAKE) $(MAKEARGS); \
	cd $(HEIMDAL_SOURCE)/lib/asn1; $(MAKE) $(MAKEARGS); \
	cd $(HEIMDAL_SOURCE)/lib/editline; $(MAKE) $(MAKEARGS); \
	cd $(HEIMDAL_SOURCE)/lib/sl; $(MAKE) $(MAKEARGS); \
	cd $(HEIMDAL_SOURCE)/lib/hcrypto; $(MAKE) $(MAKEARGS); \
	cd $(HEIMDAL_SOURCE)/lib/wind; $(MAKE) $(MAKEARGS); \
	cd $(HEIMDAL_SOURCE)/lib/hx509; $(MAKE) $(MAKEARGS); \
	cd $(HEIMDAL_SOURCE)/lib/sqlite; $(MAKE) $(MAKEARGS); \
	cd $(HEIMDAL_SOURCE)/lib/krb5; $(MAKE) $(MAKEARGS); \
	cd $(HEIMDAL_SOURCE)/lib/ntlm; $(MAKE) $(MAKEARGS); \
	cd $(HEIMDAL_SOURCE)/lib/gssapi; $(MAKE) $(MAKEARGS); \
	cd $(HEIMDAL_SOURCE)/include; $(MAKE) install STRIP=""; \
	cd $(HEIMDAL_SOURCE)/lib/roken; $(MAKE) install STRIP=""; \
	cd $(HEIMDAL_SOURCE)/lib/com_err; $(MAKE) install STRIP=""; \
	cd $(HEIMDAL_SOURCE)/lib/asn1; $(MAKE) install STRIP=""; \
	cd $(HEIMDAL_SOURCE)/lib/editline; $(MAKE) install STRIP=""; \
	cd $(HEIMDAL_SOURCE)/lib/sl; $(MAKE) install STRIP=""; \
	cd $(HEIMDAL_SOURCE)/lib/hcrypto; $(MAKE) install STRIP=""; \
	cd $(HEIMDAL_SOURCE)/lib/wind; $(MAKE) install STRIP=""; \
	cd $(HEIMDAL_SOURCE)/lib/hx509; $(MAKE) install STRIP=""; \
	cd $(HEIMDAL_SOURCE)/lib/sqlite; $(MAKE) install STRIP=""; \
	cd $(HEIMDAL_SOURCE)/lib/krb5; $(MAKE) install STRIP=""; \
	cd $(HEIMDAL_SOURCE)/lib/ntlm; $(MAKE) install STRIP=""; \
	cd $(HEIMDAL_SOURCE)/lib/gssapi; $(MAKE) install STRIP="")
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
	rm -f $(HEIMDAL_TGZ_TARGET)
