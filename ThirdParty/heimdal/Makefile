HEIMDAL_ROOT := $(shell pwd)
P4_ROOT ?= $(shell cd $(HEIMDAL_ROOT)/../..; pwd)

BUILD_PLATFORM ?= $(shell sh $(P4_ROOT)/ZimbraBuild/rpmconf/Build/get_plat_tag.sh)

ifeq ($(BUILD_PLATFORM), )
	BUILD_PLATFORM := "UNKNOWN"
endif

ZIMBRA_HOME ?= /opt/zimbra
HEIMDAL_VERSION ?=  1.0.1
HEIMDAL_TGZ_TARGET := $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/heimdal/heimdal-$(HEIMDAL_VERSION).tgz
HEIMDAL_SOURCE := $(HEIMDAL_ROOT)/heimdal-$(HEIMDAL_VERSION)

OPENSSL_VERSION ?= 0.9.8e

OPENSSL_LIB_DIR ?= $(ZIMBRA_HOME)/openssl-$(OPENSSL_VERSION)/lib
HEIMDAL_LIB_DIR ?= $(ZIMBRA_HOME)/heimdal-$(HEIMDAL_VERSION)/lib

all: build tar

build:
	(tar xzf src/heimdal-$(HEIMDAL_VERSION).tar.gz; \
	cd heimdal-$(HEIMDAL_VERSION); \
	CFLAGS="-g -O2 -D_REENTRANT" \
	CCFLAGS="-g -O2 -D_REENTRANT" \
	LD_RUN_PATH=$(OPENSSL_LIB_DIR):$(HEIMDAL_LIB_DIR) \
	$(ENVMOD) ./configure --prefix=$(ZIMBRA_HOME)/heimdal-$(HEIMDAL_VERSION) \
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
	cd $(HEIMDAL_SOURCE)/include; env LD_RUN_PATH=$(OPENSSL_LIB_DIR):$(HEIMDAL_LIB_DIR) make; \
	cd $(HEIMDAL_SOURCE)/lib/roken; env LD_RUN_PATH=$(OPENSSL_LIB_DIR):$(HEIMDAL_LIB_DIR) make; \
	cd $(HEIMDAL_SOURCE)/lib/vers; env LD_RUN_PATH=$(OPENSSL_LIB_DIR):$(HEIMDAL_LIB_DIR) make; \
	cd $(HEIMDAL_SOURCE)/lib/com_err; env LD_RUN_PATH=$(OPENSSL_LIB_DIR):$(HEIMDAL_LIB_DIR) make; \
	cd $(HEIMDAL_SOURCE)/lib/asn1; env LD_RUN_PATH=$(OPENSSL_LIB_DIR):$(HEIMDAL_LIB_DIR) make; \
	cd $(HEIMDAL_SOURCE)/lib/editline; env LD_RUN_PATH=$(OPENSSL_LIB_DIR):$(HEIMDAL_LIB_DIR) make; \
	cd $(HEIMDAL_SOURCE)/lib/sl; env LD_RUN_PATH=$(OPENSSL_LIB_DIR):$(HEIMDAL_LIB_DIR) make; \
	cd $(HEIMDAL_SOURCE)/lib/hcrypto; env LD_RUN_PATH=$(OPENSSL_LIB_DIR):$(HEIMDAL_LIB_DIR) make; \
	cd $(HEIMDAL_SOURCE)/lib/hx509; env LD_RUN_PATH=$(OPENSSL_LIB_DIR):$(HEIMDAL_LIB_DIR) make; \
	cd $(HEIMDAL_SOURCE)/lib/krb5; env LD_RUN_PATH=$(OPENSSL_LIB_DIR):$(HEIMDAL_LIB_DIR) make; \
	cd $(HEIMDAL_SOURCE)/lib/ntlm; env LD_RUN_PATH=$(OPENSSL_LIB_DIR):$(HEIMDAL_LIB_DIR) make; \
	cd $(HEIMDAL_SOURCE)/lib/gssapi; env LD_RUN_PATH=$(OPENSSL_LIB_DIR):$(HEIMDAL_LIB_DIR) make; \
	cd $(HEIMDAL_SOURCE)/include; env LD_RUN_PATH=$(OPENSSL_LIB_DIR):$(HEIMDAL_LIB_DIR) make install STRIP=""; \
	cd $(HEIMDAL_SOURCE)/lib/roken; env LD_RUN_PATH=$(OPENSSL_LIB_DIR):$(HEIMDAL_LIB_DIR) make install STRIP=""; \
	cd $(HEIMDAL_SOURCE)/lib/com_err; env LD_RUN_PATH=$(OPENSSL_LIB_DIR):$(HEIMDAL_LIB_DIR) make install STRIP=""; \
	cd $(HEIMDAL_SOURCE)/lib/asn1; env LD_RUN_PATH=$(OPENSSL_LIB_DIR):$(HEIMDAL_LIB_DIR) make install STRIP=""; \
	cd $(HEIMDAL_SOURCE)/lib/editline; env LD_RUN_PATH=$(OPENSSL_LIB_DIR):$(HEIMDAL_LIB_DIR) make install STRIP=""; \
	cd $(HEIMDAL_SOURCE)/lib/sl; env LD_RUN_PATH=$(OPENSSL_LIB_DIR):$(HEIMDAL_LIB_DIR) make install STRIP=""; \
	cd $(HEIMDAL_SOURCE)/lib/hcrypto; env LD_RUN_PATH=$(OPENSSL_LIB_DIR):$(HEIMDAL_LIB_DIR) make install STRIP=""; \
	cd $(HEIMDAL_SOURCE)/lib/hx509; env LD_RUN_PATH=$(OPENSSL_LIB_DIR):$(HEIMDAL_LIB_DIR) make install STRIP=""; \
	cd $(HEIMDAL_SOURCE)/lib/krb5; env LD_RUN_PATH=$(OPENSSL_LIB_DIR):$(HEIMDAL_LIB_DIR) make install STRIP=""; \
	cd $(HEIMDAL_SOURCE)/lib/ntlm; env LD_RUN_PATH=$(OPENSSL_LIB_DIR):$(HEIMDAL_LIB_DIR) make install STRIP=""; \
	cd $(HEIMDAL_SOURCE)/lib/gssapi; env LD_RUN_PATH=$(OPENSSL_LIB_DIR):$(HEIMDAL_LIB_DIR) make install STRIP="")
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
