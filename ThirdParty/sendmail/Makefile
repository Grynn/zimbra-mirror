SENDMAIL_ROOT := $(shell pwd)
P4_ROOT ?= $(shell cd $(SENDMAIL_ROOT)/../..; pwd)

BUILD_PLATFORM ?= $(shell sh $(P4_ROOT)/ZimbraBuild/rpmconf/Build/get_plat_tag.sh)

ifeq ($(BUILD_PLATFORM), )
	BUILD_PLATFORM := "UNKNOWN"
endif

ZIMBRA_HOME ?= /opt/zimbra

ifdef BETA
	include $(SENDMAIL_ROOT)/../beta_versions.def
else
	include $(SENDMAIL_ROOT)/../versions.def
endif

SENDMAIL_TGZ_TARGET := $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/sendmail/sendmail-$(SENDMAIL_VERSION).tgz

all: allclean build tar

build:
	(tar xzf src/sendmail.$(SENDMAIL_VERSION).tar.gz; \
	cp sendmail-$(SENDMAIL_VERSION)/devtools/OS/Darwin.9.x sendmail-$(SENDMAIL_VERSION)/devtools/OS/Darwin.10.x; \
	cd sendmail-$(SENDMAIL_VERSION)/libmilter; \
	./Build; \
	cd ../obj.*/libmilter; \
	mkdir -p $(ZIMBRA_HOME)/sendmail-$(SENDMAIL_VERSION)/lib; \
	cp -p libmilter.a $(ZIMBRA_HOME)/sendmail-$(SENDMAIL_VERSION)/lib;)
	(mkdir -p $(ZIMBRA_HOME)/sendmail-$(SENDMAIL_VERSION)/include/libmilter; \
	cp -p sendmail-$(SENDMAIL_VERSION)/include/libmilter/* $(ZIMBRA_HOME)/sendmail-$(SENDMAIL_VERSION)/include/libmilter)
	(cd $(ZIMBRA_HOME); ln -s sendmail-$(SENDMAIL_VERSION) sendmail)

tar:
	mkdir -p $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/sendmail
	(cd $(ZIMBRA_HOME); tar czf $(SENDMAIL_TGZ_TARGET) sendmail-$(SENDMAIL_VERSION))

p4edit: $(SENDMAIL_TGZ_TARGET)
	p4 add $(SENDMAIL_TGZ_TARGET)
	p4 edit $(SENDMAIL_TGZ_TARGET)

clean:
	/bin/rm -rf sendmail-$(SENDMAIL_VERSION)

allclean: clean
	/bin/rm -rf $(ZIMBRA_HOME)/sendmail-$(SENDMAIL_VERSION)
	/bin/rm -rf $(ZIMBRA_HOME)/sendmail
	rm -f $(SENDMAIL_TGZ_TARGET)
