RRDTOOL_ROOT := $(shell pwd)
P4_ROOT ?= $(shell cd $(RRDTOOL_ROOT)/../..; pwd)
MAKE ?= make
MAKEARGS ?= -j2

BUILD_PLATFORM ?= $(shell sh $(P4_ROOT)/ZimbraBuild/rpmconf/Build/get_plat_tag.sh)
ZIMBRA_HOME ?= /opt/zimbra

ifdef BETA
	include $(RRDTOOL_ROOT)/../beta_versions.def
else
	include $(RRDTOOL_ROOT)/../versions.def
endif

PNG_NAME	:= libpng
FREETYPE_NAME	:= freetype
LIBART_NAME	:= libart_lgpl
RRDTOOL_NAME	:= rrdtool

RRDTOOL_TGZ_TARGET := $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/rrdtool/$(RRDTOOL_NAME)-$(RRDTOOL_VERSION).tar.gz
INSTALL_PREFIX := $(ZIMBRA_HOME)/zimbramon/$(RRDTOOL_NAME)-$(RRDTOOL_VERSION)

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

ifeq ($(BUILD_PLATFORM), RHEL6_64)
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

filesa   := $(wildcard src/$(PNG_NAME)-$(PNG_VERSION).tar.gz)
filesb   := $(wildcard src/$(FREETYPE_NAME)-$(FREETYPE_VERSION).tar.gz)
filesc   := $(wildcard src/$(LIBART_NAME)-$(LIBART_VERSION).tar.gz)
filesd   := $(wildcard src/$(RRDTOOL_NAME)-$(RRDTOOL_VERSION).tar.gz)

all: checksrc allclean build tar

checksrc:
	$(if $(filesa), @echo "", exit 1)
	$(if $(filesb), @echo "", exit 1)
	$(if $(filesc), @echo "", exit 1)
	$(if $(filesd), @echo "", exit 1)

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
	cd $(RRDTOOL_ROOT); \
	tar xfz src/$(FREETYPE_NAME)-$(FREETYPE_VERSION).tar.gz; \
	cd $(FREETYPE_NAME)-$(FREETYPE_VERSION); \
	env CFLAGS="-g -O2 -fPIC" ./configure \
		--enable-shared=no \
		--prefix=$(INSTALL_PREFIX); \
	$(MAKE) $(MAKEARGS); $(MAKE) install; \
	cd $(RRDTOOL_ROOT); \
	tar xfz src/$(LIBART_NAME)-$(LIBART_VERSION).tar.gz; \
	cd $(LIBART_NAME)-$(LIBART_VERSION); \
	./configure --enable-shared=no --prefix=$(INSTALL_PREFIX); \
	$(MAKE) $(MAKEARGS); $(MAKE) install; \
	rm -rf $(INSTALL_PREFIX)/lib/pkgconfig; \
	rm -rf $(INSTALL_PREFIX)/share; \
	rm -rf $(INSTALL_PREFIX)/bin; \
	cd $(RRDTOOL_ROOT); \
	tar xzf src/$(RRDTOOL_NAME)-$(RRDTOOL_VERSION).tar.gz; \
	rm -rf $(PERL_DIR)/lib; \
	cd $(RRDTOOL_NAME)-$(RRDTOOL_VERSION); \
	$(ENVMOD) ./configure --prefix=$(INSTALL_PREFIX) \
		--enable-static --enable-shared=no \
		--with-perl-options="PREFIX=$(PERL_DIR) LIB=$(PERL_LIB_DIR) LIBS='-L$(INSTALL_PREFIX)/lib -lm -lpng -lz -lfreetype'" \
		--disable-tcl --disable-ruby  --disable-python; \
	$(MAKE) $(MAKEARGS); $(MAKE) install; \
	rm -rf $(INSTALL_PREFIX)/share/rrdtool/examples)
	rm -rf $(PERL_DIR)/share
	(cp -f $(RRDTOOL_NAME)-$(RRDTOOL_VERSION)/COPYING $(INSTALL_PREFIX)/LICENSE)
	(mkdir -p $(INSTALL_PREFIX)/var)
	(cd $(INSTALL_PREFIX)/..; ln -s $(RRDTOOL_NAME)-$(RRDTOOL_VERSION) $(RRDTOOL_NAME))

tar:
	mkdir -p $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/rrdtool
	(cd $(PERL_DIR); tar czf $(RRDTOOL_TGZ_TARGET) $(RRDTOOL_NAME)-$(RRDTOOL_VERSION) lib)

p4edit: $(RRDTOOL_TGZ_TARGET)
	p4 add $(RRDTOOL_TGZ_TARGET)
	p4 edit $(RRDTOOL_TGZ_TARGET)
	

clean:
	/bin/rm -rf $(RRDTOOL_NAME)-$(RRDTOOL_VERSION)
	/bin/rm -rf $(PNG_NAME)-$(PNG_VERSION)
	/bin/rm -rf $(FREETYPE_NAME)-$(FREETYPE_VERSION)
	/bin/rm -rf $(LIBART_NAME)-$(LIBART_VERSION)

allclean: clean
	/bin/rm -rf $(ZIMBRA_HOME)/zimbramon/$(RRDTOOL_NAME)-$(RRDTOOL_VERSION)
	/bin/rm -rf $(ZIMBRA_HOME)/zimbramon/$(RRDTOOL_NAME)
	/bin/rm -rf $(PERL_DIR)/lib
	rm -rf $(RRDTOOL_TGZ_TARGET)
