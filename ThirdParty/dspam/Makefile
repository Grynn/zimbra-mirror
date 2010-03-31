DSPAM_ROOT := $(shell pwd)
P4_ROOT ?= $(shell cd $(DSPAM_ROOT)/../..; pwd)
MAKE ?= make
MAKEARGS ?= -j2

BUILD_PLATFORM ?= $(shell sh $(P4_ROOT)/ZimbraBuild/rpmconf/Build/get_plat_tag.sh)

ifeq ($(BUILD_PLATFORM), )
	BUILD_PLATFORM := "UNKNOWN"
endif

ZIMBRA_HOME ?= /opt/zimbra

ifdef BETA
	include $(DSPAM_ROOT)/../beta_versions.def
else
	include $(DSPAM_ROOT)/../versions.def
endif

DSPAM_TGZ_TARGET := \
	$(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/dspam/dspam-$(DSPAM_VERSION).tgz

MYSQL_INCLUDE_DIR := $(ZIMBRA_HOME)/mysql/include/mysql

files	:= $(wildcard src/dspam-$(DSPAM_VERSION).tar.gz)

all: checksrc allclean build check tar

checksrc:
	$(if $(files), @echo "", exit 1)

check:
	@echo "Verifying dspam build...";
	$(DSPAM_ROOT)/../zmcheck.pl -b $(ZIMBRA_HOME)/dspam-$(DSPAM_VERSION)/bin/dspam

build:
	(tar xzf src/dspam-$(DSPAM_VERSION).tar.gz; \
	cd dspam-$(DSPAM_VERSION); \
	CFLAGS="-g -O2" \
	./autogen.sh; \
	./configure --prefix=$(ZIMBRA_HOME)/dspam-$(DSPAM_VERSION) \
	--sysconfdir=$(ZIMBRA_HOME)/conf --with-logdir=$(ZIMBRA_HOME)/log \
	--enable-daemon \
	--enable-large-scale --with-storage-driver=hash_drv,mysql_drv \
	--with-mysql-includes=$(MYSQL_INCLUDE_DIR) \
	--with-mysql-libraries=$(ZIMBRA_HOME)/mysql/lib/mysql \
	--with-dspam-home-group=root \
	--with-dspam-group=root \
	--with-dspam-mode=0755 \
	--enable-debug --enable-clamav; \
	$(MAKE) $(MAKEARGS); $(MAKE) install)
	(cd $(ZIMBRA_HOME); ln -s dspam-$(DSPAM_VERSION) dspam)

tar:
	mkdir -p $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/dspam
	(cd $(ZIMBRA_HOME); tar czf $(DSPAM_TGZ_TARGET) dspam-$(DSPAM_VERSION))

p4edit: $(DSPAM_TGZ_TARGET)
	p4 add $(DSPAM_TGZ_TARGET)
	p4 edit $(DSPAM_TGZ_TARGET)

clean:
	/bin/rm -rf dspam-$(DSPAM_VERSION)

allclean: clean
	/bin/rm -rf $(ZIMBRA_HOME)/dspam-$(DSPAM_VERSION)
	/bin/rm -rf $(ZIMBRA_HOME)/dspam
	rm -f $(DSPAM_TGZ_TARGET)
