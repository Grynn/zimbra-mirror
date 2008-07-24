APP_ROOT := $(shell pwd)
P4_ROOT ?= $(shell cd $(APP_ROOT)/../..; pwd)
MAKE ?= make
MAKEARGS ?= -j2

BUILD_PLATFORM ?= $(shell sh $(P4_ROOT)/ZimbraBuild/rpmconf/Build/get_plat_tag.sh)
ZIMBRA_HOME ?= /opt/zimbra

PNG_NAME ?= libpng
PNG_VERSION ?= 1.2.29

PIXMAN_VERSION ?= 0.11.8
PIXMAN_NAME = pixman

CAIRO_VERSION ?= 1.6.4
CAIRO_NAME ?= cairo

APP_NAME=rrdtool
APP_RELEASE ?= 1.3.1
APP_VERSION ?= $(APP_RELEASE)
APP_TGZ_TARGET := $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/rrdtool/$(APP_NAME)-$(APP_VERSION).tar.gz
INSTALL_PREFIX := $(ZIMBRA_HOME)/zimbramon/$(APP_NAME)-$(APP_VERSION)

ifeq ($(BUILD_PLATFORM), )
	BUILD_PLATFORM := "UNKNOWN"
endif

ifeq (MACOSXx86,$(findstring MACOSXx86,$(BUILD_PLATFORM)))
	ENVMOD := env LIBS="-lresolv" CFLAGS="-I/opt/zimbra/zimbramon/$(APP_NAME)-$(APP_VERSION)/include" \
		LDFLAGS="-L/opt/zimbra/zimbramon/$(APP_NAME)-$(APP_VERSION)/lib" \
		CPPFLAGS="-I/opt/zimbra/zimbramon/$(APP_NAME)-$(APP_VERSION)/include -I/opt/zimbra/zimbramon/$(APP_NAME)-$(APP_VERSION)/include/freetype2"
endif
ifeq ($(BUILD_PLATFORM), MACOSX)
	ENVMOD := env LIBS="-lresolv" CFLAGS="-I/opt/zimbra/zimbramon/$(APP_NAME)-$(APP_VERSION)/include" \
		LDFLAGS="-L/opt/zimbra/zimbramon/$(APP_NAME)-$(APP_VERSION)/lib" \
		CPPFLAGS="-I/opt/zimbra/zimbramon/$(APP_NAME)-$(APP_VERSION)/include -I/opt/zimbra/zimbramon/$(APP_NAME)-$(APP_VERSION)/include/freetype2"
endif
ifeq ($(BUILD_PLATFORM), RHEL5_64)
	ENVMOD := env CFLAGS="-fPIC -I/opt/zimbra/zimbramon/$(APP_NAME)-$(APP_VERSION)/include" \
		LDFLAGS="-fPIC -L/opt/zimbra/zimbramon/$(APP_NAME)-$(APP_VERSION)/lib" \
		CPPFLAGS="-I/opt/zimbra/zimbramon/$(APP_NAME)-$(APP_VERSION)/include -I/opt/zimbra/zimbramon/$(APP_NAME)-$(APP_VERSION)/include/freetype2"
endif

ifeq ($(ENVMOD), )
ENVMOD := env CFLAGS="-I/opt/zimbra/zimbramon/$(APP_NAME)-$(APP_VERSION)/include" \
	LDFLAGS="-L/opt/zimbra/zimbramon/$(APP_NAME)-$(APP_VERSION)/lib" \
	CPPFLAGS="-I/opt/zimbra/zimbramon/$(APP_NAME)-$(APP_VERSION)/include -I/opt/zimbra/zimbramon/$(APP_NAME)-$(APP_VERSION)/include/freetype2"
endif

all: build tar

build:
	(tar xzf src/$(PNG_NAME)-$(PNG_VERSION).tar.gz; \
	cd $(PNG_NAME)-$(PNG_VERSION); \
	patch -N -g0 -p1 < ../patches/libtoolize.patch; \
	./autogen.sh; \
	./configure --prefix=/opt/zimbra/zimbramon/$(APP_NAME)-$(APP_VERSION) --mandir=/opt/zimbra/zimbramon/$(APP_NAME)-$(APP_VERSION)/man --enable-shared=no; \
	$(MAKE) $(MAKEARGS); $(MAKE) install; \
	cd $(APP_ROOT); \
	tar xfz src/$(PIXMAN_NAME)-$(PIXMAN_VERSION).tar.gz; \
	cd $(PIXMAN_NAME)-$(PIXMAN_VERSION); \
	./configure --enable-shared=no --prefix=/opt/zimbra/zimbramon/$(APP_NAME)-$(APP_VERSION); \
	$(MAKE) $(MAKEARGS); $(MAKE) install; \
	cd $(APP_ROOT); \
	tar xfz src/$(CAIRO_NAME)-$(CAIRO_VERSION).tar.gz; \
	cd $(CAIRO_NAME)-$(CAIRO_VERSION); \
	pixman_CFLAGS=/opt/zimbra/zimbramon/$(APP_NAME)-$(APP_VERSION)/include pixman_LIBS=/opt/zimbra/zimbramon/$(APP_NAME)-$(APP_VERSION)/lib \
	png_REQUIRES=libpng \
	png_CFLAGS=/opt/zimbra/zimbramon/$(APP_NAME)-$(APP_VERSION)/include png_LIBS=/opt/zimbra/zimbramon/$(APP_NAME)-$(APP_VERSION)/lib \
	./configure --enable-shared=no --prefix=/opt/zimbra/zimbramon/$(APP_NAME)-$(APP_VERSION) \
		--enable-xlib=no --enable-xlib-xrender=no --enable-xcb=no --enable-quartz=no --enable-quartz-font=no \
		--enable-quartz-image=no --enable-win32=no --enable-win32-font=no --enable-os2=no --enable-beos=no \
		--enable-png=yes --enable-glitz=no --enable-directfb=no --enable-freetype=no --enable-ps=yes \
		--enable-pdf=yes --enable-svg=yes; \
	$(MAKE) $(MAKEARGS); $(MAKE) install; \
	rm -rf /opt/zimbra/zimbramon/$(APP_NAME)-$(APP_VERSION)/lib/pkgconfig; \
	rm -rf /opt/zimbra/zimbramon/$(APP_NAME)-$(APP_VERSION)/share; \
	rm -rf /opt/zimbra/zimbramon/$(APP_NAME)-$(APP_VERSION)/bin; \
	cd $(APP_ROOT); \
	tar xzf src/$(APP_NAME)-$(APP_RELEASE).tar.gz; \
	cd $(APP_NAME)-$(APP_VERSION); \
	$(ENVMOD) ./configure --prefix=$(INSTALL_PREFIX) --enable-static --enable-shared=no \
	 --disable-tcl --disable-ruby  --disable-python; \
	$(MAKE) $(MAKEARGS); $(MAKE) install; \
	rm -rf /opt/zimbra/zimbramon/$(APP_NAME)-$(APP_VERSION)/share/rrdtool/examples)
	(cp -f $(APP_NAME)-$(APP_VERSION)/COPYING $(INSTALL_PREFIX)/LICENSE)
	(mkdir -p $(INSTALL_PREFIX)/var)
	(cd $(INSTALL_PREFIX)/..; ln -s $(APP_NAME)-$(APP_VERSION) $(APP_NAME))

tar:
	mkdir -p $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/rrdtool
	(cd $(INSTALL_PREFIX)/..; tar czf $(APP_TGZ_TARGET) $(APP_NAME)-$(APP_VERSION))

p4edit: $(APP_TGZ_TARGET)
	p4 add $(APP_TGZ_TARGET)
	p4 edit $(APP_TGZ_TARGET)
	

clean:
	/bin/rm -rf $(APP_NAME)-$(APP_VERSION)
	/bin/rm -rf $(PNG_NAME)-$(PNG_VERSION)
	/bin/rm -rf $(CAIRO_NAME)-$(CAIRO_VERSION)
	/bin/rm -rf $(PIXMAN_NAME)-$(PIXMAN_VERSION)

allclean: clean
	/bin/rm -rf $(ZIMBRA_HOME)/zimbramon/$(APP_NAME)-$(APP_VERSION)
	/bin/rm -rf $(ZIMBRA_HOME)/zimbramon/$(APP_NAME)
