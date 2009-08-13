XML_ROOT := $(shell pwd)
P4_ROOT ?= $(shell cd $(XMl_ROOT)/../..; pwd)
MAKE ?= make
MAKEARGS ?= -j2

BUILD_PLATFORM ?= $(shell sh $(P4_ROOT)/ZimbraBuild/rpmconf/Build/get_plat_tag.sh)
ZIMBRA_HOME ?= /opt/zimbra
XML_NAME = libxml2
XML_RELEASE ?= 2.6.32
XML_PATCH_LEVEL ?= 1
XML_VERSION ?= $(XML_RELEASE).$(XML_PATCH_LEVEL)
XML_TGZ_TARGET := $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/$(XML_NAME)/$(XML_NAME)-$(XML_VERSION).tgz
INSTALL_PREFIX := $(ZIMBRA_HOME)/$(XML_NAME)-$(XML_VERSION)
XML_CONFIGURE ?= --prefix=$(INSTALL_PREFIX) --enable-shared=no --enable-static=yes

ifeq ($(BUILD_PLATFORM), )
	BUILD_PLATFORM := "UNKNOWN"
endif

#ifeq ($(BUILD_PLATFORM), MACOSXx86)
	#ENVMOD := env LIBS="-lresolv" 
#endif
#ifeq ($(BUILD_PLATFORM), MACOSX)
	#ENVMOD := env LIBS="-lresolv" 
#endif

all: allclean build tar

build: $(XML_NAME)-$(XML_VERSION)
	(cd $(XML_NAME)-$(XML_VERSION); \
	patch -g0 -N -p1 < ../patches/CVE-2009-2414-CVE-2009-2416.patch; \
	CFLAGS="-g -O2 -fPIC" \
	./configure $(XML_CONFIGURE); $(MAKE) $(MAKEARGS); $(MAKE) install)
	(cp -f $(XML_NAME)-$(XML_VERSION)/COPYING $(INSTALL_PREFIX)/LICENSE)
	(cd $(ZIMBRA_HOME); ln -s $(XML_NAME)-$(XML_VERSION) $(XML_NAME))

$(XML_NAME)-$(XML_VERSION): 
	tar xzf src/$(XML_NAME)-$(XML_VERSION).tar.gz

tar:
	mkdir -p $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/$(XML_NAME)
	(cd $(ZIMBRA_HOME); tar czf $(XML_TGZ_TARGET) $(XML_NAME)-$(XML_VERSION))

p4edit: $(XML_TGZ_TARGET)

#	p4 add $(XML_TGZ_TARGET)
#	p4 edit $(XML_TGZ_TARGET)
	

clean:
	/bin/rm -rf $(XML_NAME)-$(XML_VERSION)

allclean: clean
	/bin/rm -rf $(ZIMBRA_HOME)/$(XML_NAME)-$(XML_VERSION)
	/bin/rm -rf $(ZIMBRA_HOME)/$(XML_NAME)
	rm -f $(XML_TGZ_TARGET)
