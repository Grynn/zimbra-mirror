RSYNC_ROOT := $(shell pwd)
P4_ROOT ?= $(shell cd $(RSYNC_ROOT)/../..; pwd)
MAKE ?= make
MAKEARGS ?= -j2

BUILD_PLATFORM ?= $(shell sh $(P4_ROOT)/ZimbraBuild/rpmconf/Build/get_plat_tag.sh)
ZIMBRA_HOME ?= /opt/zimbra

ifdef BETA
	include $(RSYNC_ROOT)/../beta_versions.def
else
	include $(RSYNC_ROOT)/../versions.def
endif

RSYNC_NAME=rsync
APP_TGZ_TARGET := $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/rsync/$(RSYNC_NAME)-$(RSYNC_VERSION).tgz
INSTALL_PREFIX := $(ZIMBRA_HOME)/$(RSYNC_NAME)-$(RSYNC_VERSION)

files   := $(wildcard src/$(RSYNC_NAME)-$(RSYNC_VERSION).tar.gz)

all: checksrc allclean build tar

checksrc:
	$(if $(files), @echo "", exit 1)

POPT_INCLUDE	:=
POPT_LDFLAGS	:=

ifeq (MACOSXx86,$(findstring MACOSXx86,$(BUILD_PLATFORM)))
	POPT_INCLUDE := CPPFLAGS="-I/opt/zimbra/include"
	POPT_LDFLAGS := LDFLAGS="-L/opt/zimbra/lib"
endif

build: $(RSYNC_NAME)-$(RSYNC_VERSION)
	(cd $(RSYNC_NAME)-$(RSYNC_VERSION); \
	$(POPT_LDFLAGS) \
	$(POPT_INCLUDE) \
	CFLAGS="-g -O2" \
 	./configure --prefix=$(INSTALL_PREFIX) \
	--localstatedir=$(ZIMBRA_HOME)/data/tmp \
	--mandir=$(ZIMBRA_HOME)/$(RSYNC_NAME)-$(RSYNC_VERSION)/man; \
	$(MAKE) ; $(MAKE) install)
	(cd $(ZIMBRA_HOME); ln -s $(RSYNC_NAME)-$(RSYNC_VERSION) $(RSYNC_NAME))

$(RSYNC_NAME)-$(RSYNC_VERSION): 
	tar xzf src/$(RSYNC_NAME)-$(RSYNC_VERSION).tar.gz

tar:
	mkdir -p $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/rsync
	(cd $(ZIMBRA_HOME); tar czf $(APP_TGZ_TARGET) $(RSYNC_NAME)-$(RSYNC_VERSION))

p4edit: $(APP_TGZ_TARGET)
	p4 add $(APP_TGZ_TARGET)
	p4 edit $(APP_TGZ_TARGET)
	

clean:
	/bin/rm -rf $(RSYNC_NAME)-$(RSYNC_VERSION)

allclean: clean
	/bin/rm -rf $(ZIMBRA_HOME)/$(RSYNC_NAME)-$(RSYNC_VERSION)
	/bin/rm -rf $(ZIMBRA_HOME)/$(RSYNC_NAME)
	rm -f $(APP_TGZ_TARGET)
