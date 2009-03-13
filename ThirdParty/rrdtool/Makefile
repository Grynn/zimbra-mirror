APP_ROOT := $(shell pwd)
P4_ROOT ?= $(shell cd $(APP_ROOT)/../..; pwd)
MAKE ?= make
MAKEARGS ?= -j2

BUILD_PLATFORM ?= $(shell sh $(P4_ROOT)/ZimbraBuild/rpmconf/Build/get_plat_tag.sh)
ZIMBRA_HOME ?= /opt/zimbra

PNG_NAME ?= libpng
PNG_VERSION ?= 1.2.34

FREETYPE_NAME ?= freetype
FREETYPE_VERSION ?= 2.3.7

LIBART_NAME ?= libart_lgpl
LIBART_VERSION ?= 2.3.20

APP_NAME=rrdtool
APP_RELEASE ?= 1.2.30
APP_VERSION ?= $(APP_RELEASE)
APP_TGZ_TARGET := $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/rrdtool/$(APP_NAME)-$(APP_VERSION).tar.gz
INSTALL_PREFIX := $(ZIMBRA_HOME)/zimbramon/$(APP_NAME)-$(APP_VERSION)

PERL_DIR	:= $(ZIMBRA_HOME)/zimbramon
PERL_LIB_DIR	:= $(ZIMBRA_HOME)/zimbramon/lib

ifeq ($(BUILD_PLATFORM), )
	BUILD_PLATFORM := "UNKNOWN"
endif

ifeq ($(BUILD_PLATFORM), MACOSXx86)
	ENVMOD := env LIBS="-lresolv" CFLAGS="-I$(INSTALL_PREFIX)/include -I$(INSTALL_PREFIX)/include/libart-2.0" \
		LDFLAGS="-L$(INSTALL_PREFIX)/lib" \
		LD_RUN_PATH=$(INSTALL_PREFIX)/lib \
		CPPFLAGS="-I$(INSTALL_PREFIX)/include -I$(INSTALL_PREFIX)/include/libart-2.0 -I$(INSTALL_PREFIX)/include/freetype2"
endif

ifeq ($(BUILD_PLATFORM), MACOSXx86_10.5)
	ENVMOD := env LIBS="-lresolv" CFLAGS="-I$(INSTALL_PREFIX)/include -I$(INSTALL_PREFIX)/include/libart-2.0" \
		LDFLAGS="-L$(INSTALL_PREFIX)/lib" \
		LD_RUN_PATH=$(INSTALL_PREFIX)/lib \
		CPPFLAGS="-I$(INSTALL_PREFIX)/include -I$(INSTALL_PREFIX)/include/libart-2.0 -I$(INSTALL_PREFIX)/include/freetype2"
endif

ifeq ($(BUILD_PLATFORM), MACOSX)
	ENVMOD := env LIBS="-lresolv" CFLAGS="-I$(INSTALL_PREFIX)/include -I$(INSTALL_PREFIX)/include/libart-2.0" \
		LDFLAGS="-L$(INSTALL_PREFIX)/lib" \
		LD_RUN_PATH=$(INSTALL_PREFIX)/lib \
		CPPFLAGS="-I$(INSTALL_PREFIX)/include -I$(INSTALL_PREFIX)/include/libart-2.0 -I$(INSTALL_PREFIX)/include/freetype2"
endif

ifeq ($(BUILD_PLATFORM), RHEL5_64)
	ENVMOD := env CFLAGS="-fPIC -I$(INSTALL_PREFIX)/include -I$(INSTALL_PREFIX)/include/libart-2.0" \
		LDFLAGS="-fPIC -L$(INSTALL_PREFIX)/lib" \
		LD_RUN_PATH=$(INSTALL_PREFIX)/lib \
		CPPFLAGS="-I$(INSTALL_PREFIX)/include -I$(INSTALL_PREFIX)/include/libart-2.0 -I$(INSTALL_PREFIX)/include/freetype2"
endif

ifeq ($(ENVMOD), )
	ENVMOD := env CFLAGS="-I$(INSTALL_PREFIX)/include -I$(INSTALL_PREFIX)/include/libart-2.0" \
		LDFLAGS="-L$(INSTALL_PREFIX)/lib" \
		LD_RUN_PATH=$(INSTALL_PREFIX)/lib \
		CPPFLAGS="-I$(INSTALL_PREFIX)/include -I$(INSTALL_PREFIX)/include/libart-2.0 -I$(INSTALL_PREFIX)/include/freetype2"
endif

all: allclean build tar

build:
	(tar xzf src/$(PNG_NAME)-$(PNG_VERSION).tar.gz; \
	cd $(PNG_NAME)-$(PNG_VERSION); \
	patch -N -g0 -p1 < ../patches/libtoolize.patch; \
	./autogen.sh; \
	env CFLAGS="-g -O2 -fPIC" ./configure \
		--prefix=$(INSTALL_PREFIX) \
		--mandir=$(INSTALL_PREFIX)/man \
		--enable-shared=no; \
	$(MAKE) $(MAKEARGS); $(MAKE) install; \
	cd $(APP_ROOT); \
	tar xfz src/$(FREETYPE_NAME)-$(FREETYPE_VERSION).tar.gz; \
	cd $(FREETYPE_NAME)-$(FREETYPE_VERSION); \
	env CFLAGS="-g -O2 -fPIC" ./configure \
		--enable-shared=no \
		--prefix=$(INSTALL_PREFIX); \
	$(MAKE) $(MAKEARGS); $(MAKE) install; \
	cd $(APP_ROOT); \
	tar xfz src/$(LIBART_NAME)-$(LIBART_VERSION).tar.gz; \
	cd $(LIBART_NAME)-$(LIBART_VERSION); \
	./configure --enable-shared=no --prefix=$(INSTALL_PREFIX); \
	$(MAKE) $(MAKEARGS); $(MAKE) install; \
	rm -rf $(INSTALL_PREFIX)/lib/pkgconfig; \
	rm -rf $(INSTALL_PREFIX)/share; \
	rm -rf $(INSTALL_PREFIX)/bin; \
	cd $(APP_ROOT); \
	tar xzf src/$(APP_NAME)-$(APP_RELEASE).tar.gz; \
	rm -rf $(PERL_DIR)/lib; \
	cd $(APP_NAME)-$(APP_VERSION); \
	$(ENVMOD) ./configure --prefix=$(INSTALL_PREFIX) \
		--enable-static --enable-shared=no \
		--with-perl-options="PREFIX=$(PERL_DIR) LIB=$(PERL_LIB_DIR) LIBS='-L$(INSTALL_PREFIX)/lib -lm -lpng -lz -lfreetype'" \
		--disable-tcl --disable-ruby  --disable-python; \
	$(MAKE) $(MAKEARGS); $(MAKE) install; \
	rm -rf $(INSTALL_PREFIX)/share/rrdtool/examples)
	rm -rf $(PERL_DIR)/share
	(cp -f $(APP_NAME)-$(APP_VERSION)/COPYING $(INSTALL_PREFIX)/LICENSE)
	(mkdir -p $(INSTALL_PREFIX)/var)
	(cd $(INSTALL_PREFIX)/..; ln -s $(APP_NAME)-$(APP_VERSION) $(APP_NAME))

tar:
	mkdir -p $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/rrdtool
	(cd $(PERL_DIR); tar czf $(APP_TGZ_TARGET) $(APP_NAME)-$(APP_VERSION) lib)

p4edit: $(APP_TGZ_TARGET)
	p4 add $(APP_TGZ_TARGET)
	p4 edit $(APP_TGZ_TARGET)
	

clean:
	/bin/rm -rf $(APP_NAME)-$(APP_VERSION)
	/bin/rm -rf $(PNG_NAME)-$(PNG_VERSION)
	/bin/rm -rf $(FREETYPE_NAME)-$(FREETYPE_VERSION)
	/bin/rm -rf $(LIBART_NAME)-$(LIBART_VERSION)

allclean: clean
	/bin/rm -rf $(ZIMBRA_HOME)/zimbramon/$(APP_NAME)-$(APP_VERSION)
	/bin/rm -rf $(ZIMBRA_HOME)/zimbramon/$(APP_NAME)
	/bin/rm -rf $(PERL_DIR)/lib
	rm -rf $(APP_TGZ_TARGET)
