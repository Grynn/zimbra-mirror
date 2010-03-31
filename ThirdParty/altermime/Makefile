ALTMIME_ROOT := $(shell pwd)
P4_ROOT ?= $(shell cd $(ALTMIME_ROOT)/../..; pwd)
MAKE ?= make
MAKEARGS ?= -j2

BUILD_PLATFORM ?= $(shell sh $(P4_ROOT)/ZimbraBuild/rpmconf/Build/get_plat_tag.sh)
ZIMBRA_HOME ?= /opt/zimbra

ifdef BETA
	include $(ALTMIME_ROOT)/../beta_versions.def
else
	include $(ALTMIME_ROOT)/../versions.def
endif

ALTMIME_NAME=altermime
ALTMIME_TGZ_TARGET := $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/$(ALTMIME_NAME)/$(ALTMIME_NAME)-$(ALTMIME_VERSION).tgz
INSTALL_PREFIX := $(ZIMBRA_HOME)/$(ALTMIME_NAME)-$(ALTMIME_VERSION)

ifeq ($(BUILD_PLATFORM), )
	BUILD_PLATFORM := "UNKNOWN"
endif

files	:= $(wildcard src/$(ALTMIME_NAME)-$(ALTMIME_VERSION).tar.gz)

all: checksrc build check tar

checksrc:
	$(if $(files), @echo "", exit 1)

check:
	@echo "Verifying altermime build...";
	$(ALTMIME_ROOT)/../zmcheck.pl -b $(ZIMBRA_HOME)/$(ALTMIME_NAME)-$(ALTMIME_VERSION)/bin/altermime

build: $(ALTMIME_NAME)-$(ALTMIME_VERSION)
	(cd $(ALTMIME_NAME)-$(ALTMIME_VERSION); $(MAKE))
	(mkdir -p $(INSTALL_PREFIX)/bin)
	(cp -f $(ALTMIME_NAME)-$(ALTMIME_VERSION)/LICENCE $(INSTALL_PREFIX)/LICENSE)
	(cp -f $(ALTMIME_NAME)-$(ALTMIME_VERSION)/$(ALTMIME_NAME) $(INSTALL_PREFIX)/bin)
	(cd $(ZIMBRA_HOME); ln -s $(ALTMIME_NAME)-$(ALTMIME_VERSION) $(ALTMIME_NAME))

$(ALTMIME_NAME)-$(ALTMIME_VERSION): 
	tar xzf src/$(ALTMIME_NAME)-$(ALTMIME_VERSION).tar.gz

tar:
	mkdir -p $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/$(ALTMIME_NAME)
	(cd $(ZIMBRA_HOME); tar czf $(ALTMIME_TGZ_TARGET) $(ALTMIME_NAME)-$(ALTMIME_VERSION))

p4edit: $(ALTMIME_TGZ_TARGET)
	p4 add $(ALTMIME_TGZ_TARGET)
	p4 edit $(ALTMIME_TGZ_TARGET)
	

clean:
	/bin/rm -rf $(ALTMIME_NAME)-$(ALTMIME_VERSION)

allclean: clean
	/bin/rm -rf $(ZIMBRA_HOME)/$(ALTMIME_NAME)-$(ALTMIME_VERSION)
	/bin/rm -rf $(ZIMBRA_HOME)/$(ALTMIME_NAME)
