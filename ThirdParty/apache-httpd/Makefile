HTTPD_ROOT := $(shell pwd)
P4_ROOT ?= $(shell cd $(HTTPD_ROOT)/../..; pwd)

BUILD_PLATFORM ?= $(shell sh $(P4_ROOT)/ZimbraBuild/rpmconf/Build/get_plat_tag.sh)

HTTPD_VERSION ?= 2.0.54

ZIMBRA_HOME ?= /opt/zimbra

all: build tar

build:
	tar xzf httpd-$(HTTPD_VERSION).tar.gz
	(cd httpd-$(HTTPD_VERSION); ./configure --prefix=/opt/zimbra/httpd-$(HTTPD_VERSION) --enable-so; make; make install)

tar:
	mkdir -p $(HTTPD_ROOT)/builds/$(BUILD_PLATFORM)
	(cd $(ZIMBRA_HOME); tar czf \
	$(HTTPD_ROOT)/builds/$(BUILD_PLATFORM)/httpd-$(HTTPD_VERSION).tgz \
	httpd-$(HTTPD_VERSION))
	chmod -R a+w \
	$(HTTPD_ROOT)/builds/$(BUILD_PLATFORM)/httpd-$(HTTPD_VERSION).tgz

clean:
	rm -rf httpd-$(HTTPD_VERSION)

allclean: clean
	rm -rf $(ZIMBRA_HOME)/httpd-$(HTTPD_VERSION)
	rm -rf $(ZIMBRA_HOME)/httpd
