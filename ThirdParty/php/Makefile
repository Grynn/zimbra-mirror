PHP_VERSION ?= 5.2.2
HTTPD_VERSION ?= 2.2.4
ASPELL_VERSION ?= 0.60.5

PHP_ROOT := $(shell pwd)
P4_ROOT ?= $(shell cd $(PHP_ROOT)/../..; pwd)

BUILD_PLATFORM ?= $(shell sh $(P4_ROOT)/ZimbraBuild/rpmconf/Build/get_plat_tag.sh)

ifeq ($(BUILD_PLATFORM), )
	BUILD_PLATFORM := "UNKNOWN"
endif

ZIMBRA_HOME ?= /opt/zimbra

PHP_TGZ_TARGET := \
	$(PHP_ROOT)/builds/$(BUILD_PLATFORM)/php-$(PHP_VERSION).tgz

all: build tar

build:
	tar xzf php-$(PHP_VERSION).tar.gz
	(cd php-$(PHP_VERSION); ./configure --prefix=/opt/zimbra/php-$(PHP_VERSION) --with-apxs2=/opt/zimbra/httpd-$(HTTPD_VERSION)/bin/apxs --with-config-file=/opt/zimbra/conf/php.ini --with-pspell=/opt/zimbra/aspell-$(ASPELL_VERSION); make; make install)

clean:
	rm -rf php-$(PHP_VERSION)

allclean: clean
	rm -rf /opt/zimbra/php-$(PHP_VERSION)

tar: $(PHP_ROOT)/builds/$(BUILD_PLATFORM)
	(cd $(ZIMBRA_HOME); tar czf \
	$(PHP_ROOT)/builds/$(BUILD_PLATFORM)/php-$(PHP_VERSION).tgz \
	php-$(PHP_VERSION))
	chmod -R a+w $(PHP_ROOT)/builds/$(BUILD_PLATFORM)

$(PHP_ROOT)/builds/$(BUILD_PLATFORM):
	mkdir -p $@

p4edit: $(PHP_TGZ_TARGET)
	p4 add $(PHP_TGZ_TARGET)
	p4 edit $(PHP_TGZ_TARGET)
