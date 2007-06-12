APP_ROOT := $(shell pwd)
P4_ROOT ?= $(shell cd $(APP_ROOT)/../..; pwd)

BUILD_PLATFORM ?= $(shell sh $(P4_ROOT)/ZimbraBuild/rpmconf/Build/get_plat_tag.sh)
ZIMBRA_HOME ?= /opt/zimbra

PNG_NAME ?= libpng
PNG_VERSION ?= 1.2.18

FREETYPE_NAME ?= freetype
FREETYPE_VERSION ?= 2.3.4

LIBART_NAME ?= libart_lgpl
LIBART_VERSION ?= 2.3.19

APP_NAME=rrdtool
APP_RELEASE ?= 1.2.23
APP_VERSION ?= $(APP_RELEASE)
APP_TGZ_TARGET := $(APP_ROOT)/builds/$(BUILD_PLATFORM)/$(APP_NAME)-$(APP_VERSION).tar.gz
INSTALL_PREFIX := $(ZIMBRA_HOME)/zimbramon/$(APP_NAME)-$(APP_VERSION)

ifeq ($(BUILD_PLATFORM), )
	BUILD_PLATFORM := "UNKNOWN"
endif

ifeq ($(BUILD_PLATFORM), MACOSXx86)
	ENVMOD := env LIBS="-lresolv" CFLAGS="-I/opt/zimbra/zimbramon/$(APP_NAME)-$(APP_VERSION)/include -I/opt/zimbra/zimbramon/$(APP_NAME)-$(APP_VERSION)/include/libart-2.0" \
		 LDFLAGS="-L/opt/zimbra/zimbramon/$(APP_NAME)-$(APP_VERSION)/lib"
endif
ifeq ($(BUILD_PLATFORM), MACOSX)
	ENVMOD := env LIBS="-lresolv" CFLAGS="-I/opt/zimbra/zimbramon/$(APP_NAME)-$(APP_VERSION)/include -I/opt/zimbra/zimbramon/$(APP_NAME)-$(APP_VERSION)/include/libart-2.0" \
		LDFLAGS="-L/opt/zimbra/zimbramon/$(APP_NAME)-$(APP_VERSION)/lib"
endif
ifeq ($(BUILD_PLATFORM), RHEL5_64)
	ENVMOD := env CFLAGS="-fPIC -I/opt/zimbra/zimbramon/$(APP_NAME)-$(APP_VERSION)/include -I/opt/zimbra/zimbramon/$(APP_NAME)-$(APP_VERSION)/include/libart-2.0" \
		LDFLAGS="-fPIC -L/opt/zimbra/zimbramon/$(APP_NAME)-$(APP_VERSION)/lib"
endif

ifndef $(ENVMOD)
ENVMOD := env CFLAGS="-I/opt/zimbra/zimbramon/$(APP_NAME)-$(APP_VERSION)/include -I/opt/zimbra/zimbramon/$(APP_NAME)-$(APP_VERSION)/include/libart-2.0" \
		LDFLAGS="-L/opt/zimbra/zimbramon/$(APP_NAME)-$(APP_VERSION)/lib"
endif

all: build tar

build:
	(tar xzf $(PNG_NAME)-$(PNG_VERSION).tar.gz; \
	cd $(PNG_NAME)-$(PNG_VERSION); \
	./configure --prefix=/opt/zimbra/zimbramon/$(APP_NAME)-$(APP_VERSION) --mandir=/opt/zimbra/zimbramon/$(APP_NAME)-$(APP_VERSION)/man --enable-shared=no; \
	make; make install; \
	cd $(APP_ROOT); \
	tar xfz $(FREETYPE_NAME)-$(FREETYPE_VERSION).tar.gz; \
	cd $(FREETYPE_NAME)-$(FREETYPE_VERSION); \
	./configure --enable-shared=no --prefix=/opt/zimbra/zimbramon/$(APP_NAME)-$(APP_VERSION); \
	make; make install; \
	cd $(APP_ROOT); \
	tar xfz $(LIBART_NAME)-$(LIBART_VERSION).tar.gz; \
	cd $(LIBART_NAME)-$(LIBART_VERSION); \
	./configure --enable-shared=no --prefix=/opt/zimbra/zimbramon/$(APP_NAME)-$(APP_VERSION); \
	make; make install; \
	rm -rf /opt/zimbra/zimbramon/$(APP_NAME)-$(APP_VERSION)/lib/pkgconfig; \
	rm -rf /opt/zimbra/zimbramon/$(APP_NAME)-$(APP_VERSION)/share; \
	rm -rf /opt/zimbra/zimbramon/$(APP_NAME)-$(APP_VERSION)/bin; \
	cd $(APP_ROOT); \
	tar xzf $(APP_NAME)-$(APP_RELEASE).tar.gz; \
	cd $(APP_NAME)-$(APP_VERSION); \
	$(ENVMOD) ./configure --prefix=$(INSTALL_PREFIX) --enable-static --enable-shared=no \
	 --disable-tcl --disable-ruby  --disable-python; \
	make; make install; \
	rm -rf /opt/zimbra/zimbramon/$(APP_NAME)-$(APP_VERSION)/share/rrdtool/examples)
	(cp -f $(APP_NAME)-$(APP_VERSION)/COPYING $(INSTALL_PREFIX)/LICENSE)
	(mkdir -p $(INSTALL_PREFIX)/var)
	(cd $(INSTALL_PREFIX)/..; ln -s $(APP_NAME)-$(APP_VERSION) $(APP_NAME))

tar:
	mkdir -p $(APP_ROOT)/builds/$(BUILD_PLATFORM)
	(cd $(INSTALL_PREFIX)/..; tar czf $(APP_ROOT)/builds/$(BUILD_PLATFORM)/$(APP_NAME)-$(APP_VERSION).tar.gz $(APP_NAME)-$(APP_VERSION))

p4edit: $(APP_TGZ_TARGET)
	p4 add $(APP_TGZ_TARGET)
	p4 edit $(APP_TGZ_TARGET)
	

clean:
	/bin/rm -rf $(APP_NAME)-$(APP_VERSION)

allclean: clean
	/bin/rm -rf $(ZIMBRA_HOME)/$(APP_NAME)-$(APP_VERSION)
	/bin/rm -rf $(ZIMBRA_HOME)/$(APP_NAME)
