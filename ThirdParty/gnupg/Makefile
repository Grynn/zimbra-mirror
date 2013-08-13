GNUPG_ROOT := $(shell pwd)
P4_ROOT ?= $(shell cd $(GNUPG_ROOT)/../..; pwd)
MAKE ?= make

BUILD_PLATFORM ?= $(shell sh $(P4_ROOT)/ZimbraBuild/rpmconf/Build/get_plat_tag.sh)

ifeq ($(BUILD_PLATFORM), )
	BUILD_PLATFORM := "UNKNOWN"
endif

MAKEARGS ?= -j2
ZIMBRA_HOME ?= /opt/zimbra

PATCH	:= patch -g0 -N -p1 < ../patches/tests.patch;

ifdef BETA
	include $(GNUPG_ROOT)/../beta_versions.def
else
	include $(GNUPG_ROOT)/../versions.def
endif

LDFLAGS	:=	LDFLAGS="-L$(ZIMBRA_HOME)/libgpg-error-$(GPGERROR_VERSION)/lib -L$(ZIMBRA_HOME)/libgcrypt-$(GCRYPT_VERSION)/lib \
		-L$(ZIMBRA_HOME)/libksba-$(KSBA_VERSION)/lib -L$(ZIMBRA_HOME)/libassuan-$(ASSUAN_VERSION)/lib \
		-Wl,-rpath,$(ZIMBRA_HOME)/libgpg-error-$(GPGERROR_VERSION)/lib -Wl,-rpath,$(ZIMBRA_HOME)/libgcrypt-$(GCRYPT_VERSION)/lib \
		-Wl,-rpath,$(ZIMBRA_HOME)/libksba-$(KSBA_VERSION)/lib -Wl,-rpath,$(ZIMBRA_HOME)/libassuan-$(ASSUAN_VERSION)/lib"

CPPFLAGS :=	CPPFLAGS="-I$(ZIMBRA_HOME)/libgpg-error-$(GPGERROR_VERSION)/include -I$(ZIMBRA_HOME)/libgcrypt-$(GCRYPT_VERSION) \
		-I$(ZIMBRA_HOME)/libksba-$(KSBA_VERSION)/include -I$(ZIMBRA_HOME)/libassuan-$(ASSUAN_VERSION)/include"

LD_LIBRARY_PATH	:=	LD_LIBRARY_PATH=$(ZIMBRA_HOME)/libgpg-error-$(GPGERROR_VERSION)/lib:$(ZIMBRA_HOME)/libgcrypt-$(GCRYPT_VERSION)/lib:$(ZIMBRA_HOME)/libksba-$(KSBA_VERSION)/lib:$(ZIMBRA_HOME)/libassuan-$(ASSUAN_VERSION)/lib

LD_RUN_PATH	:=	LD_RUN_PATH=$(ZIMBRA_HOME)/libgpg-error-$(GPGERROR_VERSION)/lib:$(ZIMBRA_HOME)/libgcrypt-$(GCRYPT_VERSION)/lib:$(ZIMBRA_HOME)/libksba-$(KSBA_VERSION)/lib:$(ZIMBRA_HOME)/libassuan-$(ASSUAN_VERSION)/lib

GPG_TGZ_TARGET := $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/gnupg/gnupg-$(GPG_VERSION).tgz
GPGERROR_TGZ_TARGET := $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/gnupg/libgpg-error-$(GPGERROR_VERSION).tgz
GCRYPT_TGZ_TARGET := $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/gnupg/libgcrypt-$(GCRYPT_VERSION).tgz
KSBA_TGZ_TARGET := $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/gnupg/libksba-$(KSBA_VERSION).tgz
ASSUAN_TGZ_TARGET := $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/gnupg/libassuan-$(ASSUAN_VERSION).tgz

gnupgsrc	:= $(wildcard src/gnupg-$(GPG_VERSION).tar.bz2)
gpgerrorsrc	:= $(wildcard src/libgpg-error-$(GPGERROR_VERSION).tar.bz2)
gcryptsrc	:= $(wildcard src/libgcrypt-$(GCRYPT_VERSION).tar.bz2)
ksbasrc		:= $(wildcard src/libksba-$(KSBA_VERSION).tar.bz2)
assuansrc	:= $(wildcard src/libassuan-$(ASSUAN_VERSION).tar.bz2)

all: checksrc allclean build tar

checksrc:
	$(if $(gnupgsrc), @echo "", exit 1)
	$(if $(gpgerrorsrc), @echo "", exit 1)
	$(if $(gcryptsrc), @echo "", exit 1)
	$(if $(ksbasrc), @echo "", exit 1)

build:
	(tar xjf src/libgpg-error-$(GPGERROR_VERSION).tar.bz2; \
	cd libgpg-error-$(GPGERROR_VERSION); \
	$(LDFLAGS) \
	$(LD_LIBRARY_PATH) \
	$(CPPFLAGS) \
	CFLAGS="-g -O0" \
	$(ENVMOD) ./configure --prefix=$(ZIMBRA_HOME)/libgpg-error-$(GPGERROR_VERSION); \
	$(LD_RUN_PATH) $(MAKE) $(MAKEARGS); \
	$(LD_RUN_PATH) $(MAKE) install STRIP="";)
	(cd $(ZIMBRA_HOME); ln -s libgpg-error-$(GPGERROR_VERSION) libgpg-error)
	(tar xjf src/libgcrypt-$(GCRYPT_VERSION).tar.bz2; \
	cd libgcrypt-$(GCRYPT_VERSION); \
	$(LDFLAGS) \
	$(LD_LIBRARY_PATH) \
	$(CPPFLAGS) \
	CFLAGS="-g -O0" \
	$(ENVMOD) ./configure --prefix=$(ZIMBRA_HOME)/libgcrypt-$(GCRYPT_VERSION) \
	--with-gpg-error-prefix=/opt/zimbra/libgpg-error; \
	$(LD_RUN_PATH) $(MAKE) $(MAKEARGS); \
	$(LD_RUN_PATH) $(MAKE) install STRIP="";)
	(cd $(ZIMBRA_HOME); ln -s libgcrypt-$(GCRYPT_VERSION) libgcrypt)
	(tar xjf src/libksba-$(KSBA_VERSION).tar.bz2; \
	cd libksba-$(KSBA_VERSION); \
	$(LDFLAGS) \
	$(LD_LIBRARY_PATH) \
	$(CPPFLAGS) \
	CFLAGS="-g -O0" \
	$(ENVMOD) ./configure --prefix=$(ZIMBRA_HOME)/libksba-$(KSBA_VERSION) \
	--with-libgpg-error-prefix=/opt/zimbra/libgpg-error \
	--with-gpg-error-prefix=/opt/zimbra/libgpg-error; \
	$(LD_RUN_PATH) $(MAKE) $(MAKEARGS); \
	$(LD_RUN_PATH) $(MAKE) install STRIP="";)
	(cd $(ZIMBRA_HOME); ln -s libksba-$(KSBA_VERSION) libksba)
	(tar xjf src/libassuan-$(ASSUAN_VERSION).tar.bz2; \
	cd libassuan-$(ASSUAN_VERSION); \
	$(LDFLAGS) \
	$(LD_LIBRARY_PATH) \
	$(CPPFLAGS) \
	CFLAGS="-g -O0" \
	$(ENVMOD) ./configure --prefix=$(ZIMBRA_HOME)/libassuan-$(ASSUAN_VERSION) \
	--with-libgpg-error-prefix=/opt/zimbra/libgpg-error \
	--with-gpg-error-prefix=/opt/zimbra/libgpg-error; \
	$(LD_RUN_PATH) $(MAKE) $(MAKEARGS); \
	$(LD_RUN_PATH) $(MAKE) install STRIP="";)
	(cd $(ZIMBRA_HOME); ln -s libassuan-$(ASSUAN_VERSION) libassuan)
	(tar xjf src/gnupg-$(GPG_VERSION).tar.bz2; \
	cd gnupg-$(GPG_VERSION); \
	$(PATCH) \
	$(LDFLAGS) \
	$(LD_LIBRARY_PATH) \
	$(CPPFLAGS) \
	CFLAGS="-g -O0" \
	$(ENVMOD) ./configure --prefix=$(ZIMBRA_HOME)/gnupg-$(GPG_VERSION) \
	--enable-selinux-support \
	--with-libgpg-error-prefix=/opt/zimbra/libgpg-error \
	--with-libgcrypt-prefix=/opt/zimbra/libgcrypt \
	--with-ksba-prefix=/opt/zimbra/libksba \
	--with-libassuan-prefix=/opt/zimbra/libassuan \
	--with-ldap=/opt/zimbra/openldap; \
	$(LD_RUN_PATH) $(MAKE) $(MAKEARGS); \
	$(LD_RUN_PATH) $(MAKE) install STRIP="";)
	(cd $(ZIMBRA_HOME); ln -s gnupg-$(GPG_VERSION) gnupg)

tar:
	mkdir -p $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/gnupg
	(cd $(ZIMBRA_HOME); tar czf $(GPG_TGZ_TARGET) gnupg-$(GPG_VERSION))
	(cd $(ZIMBRA_HOME); tar czf $(GPGERROR_TGZ_TARGET) libgpg-error-$(GPGERROR_VERSION))
	(cd $(ZIMBRA_HOME); tar czf $(GCRYPT_TGZ_TARGET) libgcrypt-$(GCRYPT_VERSION))
	(cd $(ZIMBRA_HOME); tar czf $(KSBA_TGZ_TARGET) libksba-$(KSBA_VERSION))
	(cd $(ZIMBRA_HOME); tar czf $(ASSUAN_TGZ_TARGET) libassuan-$(ASSUAN_VERSION))

p4edit: $(GPG_TGZ_TARGET)
	p4 add $(GPG_TGZ_TARGET)
	p4 edit $(GPG_TGZ_TARGET)

clean:
	/bin/rm -rf gnupg-$(GPG_VERSION)
	/bin/rm -rf libgpg-error-$(GPGERROR_VERSION)
	/bin/rm -rf libgcrypt-$(GCRYPT_VERSION)
	/bin/rm -rf libksba-$(KSBA_VERSION)
	/bin/rm -rf libassuan-$(ASSUAN_VERSION)

allclean: clean
	/bin/rm -rf $(ZIMBRA_HOME)/gnupg-$(GPG_VERSION)
	/bin/rm -rf $(ZIMBRA_HOME)/gnupg
	/bin/rm -rf $(ZIMBRA_HOME)/libgpg-error-$(GPGERROR_VERSION)
	/bin/rm -rf $(ZIMBRA_HOME)/libgpg-error
	/bin/rm -rf $(ZIMBRA_HOME)/libgcrypt-$(GCRYPT_VERSION)
	/bin/rm -rf $(ZIMBRA_HOME)/libgcrypt
	/bin/rm -rf $(ZIMBRA_HOME)/libksba-$(KSBA_VERSION)
	/bin/rm -rf $(ZIMBRA_HOME)/libksba
	/bin/rm -rf $(ZIMBRA_HOME)/libassuan-$(ASSUAN_VERSION)
	/bin/rm -rf $(ZIMBRA_HOME)/libassuan
	rm -f $(GPG_TGZ_TARGET)
