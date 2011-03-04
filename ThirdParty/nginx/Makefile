NGINX_ROOT := $(shell pwd)
P4_ROOT ?= $(shell cd $(NGINX_ROOT)/../..; pwd)

BUILD_PLATFORM ?= $(shell sh $(P4_ROOT)/ZimbraBuild/rpmconf/Build/get_plat_tag.sh)

ifeq ($(BUILD_PLATFORM), )
	BUILD_PLATFORM := "UNKNOWN"
endif

ZIMBRA_HOME ?= /opt/zimbra

ifdef BETA
	include $(NGINX_ROOT)/../beta_versions.def
else
	include $(NGINX_ROOT)/../versions.def
endif

PCREINC	:=
PCRELIB	:=

ifeq (MACOSX,$(findstring MACOSX,$(BUILD_PLATFORM)))
	PCREINC	:= -I/opt/zimbra/include
	PCRELIB	:= -L/opt/zimbra/lib
endif

NGINX_TGZ_TARGET := $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/nginx/nginx-$(NGINX_VERSION)-zimbra.tgz
NGINX_INSTALL := $(ZIMBRA_HOME)/nginx-$(NGINX_VERSION)-zimbra

WITH_CC_OPT := -g \
	-I/opt/zimbra/cyrus-sasl-$(CYRUS_VERSION)/include \
	-I/opt/zimbra/openssl-$(OPENSSL_VERSION)/include \
	$(PCREINC)

WITH_LD_OPT :=	-L$(CYRUS_LIB_DIR) \
	-L$(OPENSSL_LIB_DIR) \
	$(PCRELIB)

all: allclean build tar

build:
	(cd nginx-$(NGINX_VERSION)-zimbra; \
	./configure \
            --prefix=$(ZIMBRA_HOME)/nginx-$(NGINX_VERSION)-zimbra \
            --with-cc-opt="$(WITH_CC_OPT)" \
            --with-ld-opt="$(WITH_LD_OPT)" \
            --with-debug \
            --with-ipv6 \
            --with-http_ssl_module \
            --with-http_stub_status_module \
            --with-pcre \
            --with-http_upstream_zmauth_module \
            --with-mail \
            --with-mail-sasl \
            --with-mail_ssl_module \
            --error-log-path=/opt/zimbra/log/nginx.log \
            --http-log-path=/opt/zimbra/log/nginx.access.log \
            --http-client-body-temp-path=$(ZIMBRA_HOME)/data/tmp/nginx/client \
            --http-proxy-temp-path=$(ZIMBRA_HOME)/data/tmp/nginx/proxy \
            --http-fastcgi-temp-path=$(ZIMBRA_HOME)/data/tmp/nginx/fastcgi; )
	(cd nginx-$(NGINX_VERSION)-zimbra; \
	LD_RUN_PATH="$(CYRUS_LIB_DIR):$(OPENSSL_LIB_DIR)" make; make install)
	(cd $(ZIMBRA_HOME); ln -s $(NGINX_INSTALL) nginx)

tar:
	mkdir -p $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/nginx
	(cd $(ZIMBRA_HOME); tar czf $(NGINX_TGZ_TARGET) nginx-$(NGINX_VERSION)-zimbra)

p4edit: $(NGINX_TGZ_TARGET)
	p4 add $(NGINX_TGZ_TARGET)
	p4 edit $(NGINX_TGZ_TARGET)
	

clean:
	-make -C nginx-$(NGINX_VERSION)-zimbra clean

allclean: clean
	/bin/rm -rf $(ZIMBRA_HOME)/nginx-$(NGINX_VERSION)-zimbra
	/bin/rm -rf $(ZIMBRA_HOME)/nginx
	rm -f $(NGINX_TGZ_TARGET)
