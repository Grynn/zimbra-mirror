DNS_ROOT := $(shell pwd)
P4_ROOT ?= $(shell cd $(DNS_ROOT)/../..; pwd)
MAKE ?= make
MAKEARGS ?= -j2
SED ?= sed

BUILD_PLATFORM ?= $(shell sh $(P4_ROOT)/ZimbraBuild/rpmconf/Build/get_plat_tag.sh)

ZIMBRA_HOME ?= /opt/zimbra

ifdef BETA
	include $(DNS_ROOT)/../beta_versions.def
else
	include $(DNS_ROOT)/../versions.def
endif

PATCH	:= patch -g0 -N -p1 < ../patches/log-facility.patch;

UNBOUND_TGZ_TARGET := $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/unbound/unbound-$(UNBOUND_VERSION).tgz
LDNS_TGZ_TARGET := $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/unbound/ldns-$(LDNS_VERSION).tgz

LIB_DIR := /usr/lib

ifeq ($(BUILD_PLATFORM), RHEL6_64)
	LIB_DIR := /usr/lib64
endif

ifeq ($(BUILD_PLATFORM), CentOS6_64)
	LIB_DIR := /usr/lib64
endif

ifeq ($(BUILD_PLATFORM), SLES11_64)
	LIB_DIR := /usr/lib64
endif

LDFLAGS	:= LDFLAGS="-L$(OPENSSL_LIB_DIR) -L$(LDNS_LIB_DIR) -R$(OPENSSL_LIB_DIR) -R$(LDNS_LIB_DIR)"
ifeq ($(BUILD_PLATFORM), UBUNTU12_64)
	LDFLAGS	:= LDFLAGS="-L$(OPENSSL_LIB_DIR) -L$(LDNS_LIB_DIR) -Wl,-rpath,$(OPENSSL_LIB_DIR) -Wl,-rpath,$(LDNS_LIB_DIR)"
endif

unboundfiles	:= $(wildcard src/unbound-$(UNBOUND_VERSION).tar.gz)
ldnsfiles	:= $(wildcard src/ldns-$(LDNS_VERSION).tar.gz)

all: checksrc allclean build check tar

checksrc:
	$(if $(unboundfiles), @echo "", exit 1)
	$(if $(ldnsfiles), @echo "", exit 1)

check:
	@echo "Verifying unbound build...";
	$(DNS_ROOT)/../zmcheck.pl -b $(ZIMBRA_HOME)/unbound-$(UNBOUND_VERSION)/sbin/unbound

build:
	tar xzf src/ldns-$(LDNS_VERSION).tar.gz
	(cd ldns-$(LDNS_VERSION); \
	$(LDFLAGS) \
	CFLAGS="-g -O2" \
	./configure --prefix=$(ZIMBRA_HOME)/ldns-$(LDNS_VERSION) \
		--with-ssl=/opt/zimbra/openssl-$(OPENSSL_VERSION) \
		--without-pyldns --without-pyldnsx \
		--with-ca-path=/opt/zimbra/data/dns/ca \
		--with-trust-anchor=/opt/zimbra/data/dns/trust)
	(cd ldns-$(LDNS_VERSION);  LD_RUN_PATH=$(OPENSSL_LIB_DIR):$(LDNS_LIB_DIR) $(MAKE) $(MAKEARGS))
	(cd ldns-$(LDNS_VERSION); LD_RUN_PATH=$(OPENSSL_LIB_DIR):$(LDNS_LIB_DIR) $(MAKE) $(MAKEARGS) install)
	(cd $(ZIMBRA_HOME); ln -s ldns-$(LDNS_VERSION) ldns)
	tar xzf src/unbound-$(UNBOUND_VERSION).tar.gz
	(cd unbound-$(UNBOUND_VERSION); \
	$(PATCH) \
	$(LDFLAGS) \
	CFLAGS="-g -O2" \
	./configure --prefix=$(ZIMBRA_HOME)/unbound-$(UNBOUND_VERSION) \
		--with-ssl=/opt/zimbra/openssl-$(OPENSSL_VERSION) \
		--with-username=zimbra \
		--with-conf-file=/opt/zimbra/conf/unbound.conf \
		--with-pidfile=/opt/zimbra/log/unbound.pid \
		--with-chroot-dir=/opt/zimbra \
		--with-ldns=/opt/zimbra/ldns-$(LDNS_VERSION))
	(cd unbound-$(UNBOUND_VERSION);  LD_RUN_PATH=$(OPENSSL_LIB_DIR):$(LDNS_LIB_DIR) $(MAKE) $(MAKEARGS))
	(cd unbound-$(UNBOUND_VERSION); LD_RUN_PATH=$(OPENSSL_LIB_DIR):$(LDNS_LIB_DIR) $(MAKE) $(MAKEARGS) install)
	(cd $(ZIMBRA_HOME); ln -s unbound-$(UNBOUND_VERSION) unbound)

tar:
	mkdir -p $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/unbound
	(cd $(ZIMBRA_HOME); tar czf $(LDNS_TGZ_TARGET) ldns-$(LDNS_VERSION))
	(cd $(ZIMBRA_HOME); tar czf $(UNBOUND_TGZ_TARGET) unbound-$(UNBOUND_VERSION))

clean:
	/bin/rm -rf ldns-$(LDNS_VERSION)
	/bin/rm -rf unbound-$(UNBOUND_VERSION)

allclean: clean
	/bin/rm -rf $(ZIMBRA_HOME)/ldns-$(LDNS_VERSION)
	/bin/rm -rf $(ZIMBRA_HOME)/ldns
	rm -f $(LDNS_TGZ_TARGET)
	/bin/rm -rf $(ZIMBRA_HOME)/unbound-$(UNBOUND_VERSION)
	/bin/rm -rf $(ZIMBRA_HOME)/unbound
	rm -f $(UNBOUND_TGZ_TARGET)
