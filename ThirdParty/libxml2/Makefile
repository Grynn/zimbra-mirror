APP_ROOT := $(shell pwd)
P4_ROOT ?= $(shell cd $(APP_ROOT)/../..; pwd)

BUILD_PLATFORM ?= $(shell sh $(P4_ROOT)/ZimbraBuild/rpmconf/Build/get_plat_tag.sh)
ZIMBRA_HOME ?= /opt/zimbra
APP_NAME = libxml2
APP_VERSION ?= 2.6.29
APP_TGZ_TARGET := $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/$(APP_NAME)/$(APP_NAME)-$(APP_VERSION).tgz
INSTALL_PREFIX := $(ZIMBRA_HOME)/$(APP_NAME)-$(APP_VERSION)
APP_CONFIGURE ?= --prefix=$(INSTALL_PREFIX) --enable-shared=no --enable-static=yes

ifeq ($(BUILD_PLATFORM), )
	BUILD_PLATFORM := "UNKNOWN"
endif

#ifeq ($(BUILD_PLATFORM), MACOSXx86)
	#ENVMOD := env LIBS="-lresolv" 
#endif
#ifeq ($(BUILD_PLATFORM), MACOSX)
	#ENVMOD := env LIBS="-lresolv" 
#endif

all: build tar

build: $(APP_NAME)-$(APP_VERSION)
	(cd $(APP_NAME)-$(APP_VERSION); \
	CFLAGS="-g -O2 -fPIC" \
	./configure $(APP_CONFIGURE); make; make install)
	(cp -f $(APP_NAME)-$(APP_VERSION)/COPYING $(INSTALL_PREFIX)/LICENSE)
	(cd $(ZIMBRA_HOME); ln -s $(APP_NAME)-$(APP_VERSION) $(APP_NAME))

$(APP_NAME)-$(APP_VERSION): 
	tar xzf src/$(APP_NAME)-$(APP_VERSION).tar.gz

tar:
	mkdir -p $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/$(APP_NAME)
	(cd $(ZIMBRA_HOME); tar czf $(APP_TGZ_TARGET) $(APP_NAME)-$(APP_VERSION))

p4edit: $(APP_TGZ_TARGET)

#	p4 add $(APP_TGZ_TARGET)
#	p4 edit $(APP_TGZ_TARGET)
	

clean:
	/bin/rm -rf $(APP_NAME)-$(APP_VERSION)

allclean: clean
	/bin/rm -rf $(ZIMBRA_HOME)/$(APP_NAME)-$(APP_VERSION)
	/bin/rm -rf $(ZIMBRA_HOME)/$(APP_NAME)
