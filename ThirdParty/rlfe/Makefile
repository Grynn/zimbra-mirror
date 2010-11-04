RLFE_ROOT := $(shell pwd)
P4_ROOT ?= $(shell cd $(RLFE_ROOT)/../..; pwd)
MAKE ?= make
MAKEARGS ?= -j2

BUILD_PLATFORM ?= $(shell sh $(P4_ROOT)/ZimbraBuild/rpmconf/Build/get_plat_tag.sh)
ZIMBRA_HOME ?= /opt/zimbra

ifdef BETA
	include $(RLFE_ROOT)/../beta_versions.def
else
	include $(RLFE_ROOT)/../versions.def
endif

RLFE_NAME=rlfe

RLFE_TGZ_TARGET := $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/$(RLFE_NAME)/$(RLFE_NAME)-$(RLFE_VERSION).tgz
INSTALL_PREFIX := $(ZIMBRA_HOME)/$(RLFE_NAME)-$(RLFE_VERSION)

RL_PATCH	:= 

ifeq ($(BUILD_PLATFORM), )
	BUILD_PLATFORM := "UNKNOWN"
endif

ifeq ($(BUILD_PLATFORM), UBUNTU10_64)
	RL_PATCH	:= patch -g0 -p1 < ../patches/rlfe.patch;
endif

ifeq ($(BUILD_PLATFORM), RHEL6_64)
	RL_PATCH	:= patch -g0 -p1 < ../patches/rlfe.patch;
endif

ifeq ($(BUILD_PLATFORM), F13_64)
	RL_PATCH	:= patch -g0 -p1 < ../patches/rlfe.patch;
endif

files	:= $(wildcard src/$(RLFE_NAME)-$(RLFE_VERSION).tgz)

all: allclean checksrc build check tar

checksrc:
	$(if $(files), @echo "", exit 1)

check:
	@echo "Verifying rlfe build...";
	$(RLFE_ROOT)/../zmcheck.pl -b $(ZIMBRA_HOME)/$(RLFE_NAME)-$(RLFE_VERSION)/bin/rlfe

build: $(RLFE_NAME)-$(RLFE_VERSION)
	(cd $(RLFE_NAME)-$(RLFE_VERSION); \
	patch -g0 -p1 < ../patches/config-h.patch; \
	patch -g0 -p1 < ../patches/extern.patch; \
	patch -g0 -p1 < ../patches/makefile-in.patch; \
	patch -g0 -p1 < ../patches/pty.patch; \
	$(RL_PATCH) \
	./configure --prefix=$(ZIMBRA_HOME)/$(RLFE_NAME)-$(RLFE_VERSION); \
	$(MAKE); \
	mkdir -p $(ZIMBRA_HOME)/$(RLFE_NAME)-$(RLFE_VERSION)/bin; \
	cp -pf rlfe $(ZIMBRA_HOME)/$(RLFE_NAME)-$(RLFE_VERSION)/bin/)
	(cd $(ZIMBRA_HOME); ln -s $(RLFE_NAME)-$(RLFE_VERSION) rlfe)

$(RLFE_NAME)-$(RLFE_VERSION): 
	tar xzf src/$(RLFE_NAME)-$(RLFE_VERSION).tgz

tar:
	mkdir -p $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/$(RLFE_NAME)
	(cd $(ZIMBRA_HOME); tar czf $(RLFE_TGZ_TARGET) $(RLFE_NAME)-$(RLFE_VERSION))

p4edit: $(RLFE_TGZ_TARGET)
	p4 add $(RLFE_TGZ_TARGET)
	p4 edit $(RLFE_TGZ_TARGET)
	

clean:
	/bin/rm -rf $(RLFE_NAME)-$(RLFE_VERSION)

allclean: clean
	/bin/rm -rf $(ZIMBRA_HOME)/$(RLFE_NAME)-$(RLFE_VERSION)
	/bin/rm -rf $(ZIMBRA_HOME)/$(RLFE_NAME)
