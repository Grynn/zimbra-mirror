SHELL := /bin/bash
ASPELL_ROOT := $(shell pwd)
P4_ROOT ?= $(shell cd $(ASPELL_ROOT)/../..; pwd)
MAKE ?= make
MAKEARGS ?= -j2

BUILD_PLATFORM ?= $(shell sh $(P4_ROOT)/ZimbraBuild/rpmconf/Build/get_plat_tag.sh)

ifeq ($(BUILD_PLATFORM), )
        BUILD_PLATFORM := "UNKNOWN"
endif

ZIMBRA_HOME ?= /opt/zimbra

ifdef BETA
	include $(ASPELL_ROOT)/../beta_versions.def
else
	include $(ASPELL_ROOT)/../versions.def
endif

ASPELL_TGZ_TARGET := \
	$(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/aspell/aspell-$(ASPELL_VERSION).tgz

files	:= $(wildcard src/aspell-$(ASPELL_VERSION).tar.gz)

ASPELL_DICTS := \
	aspell6-en-6.0-0 \
	aspell-fr-0.50-3 \
	aspell-nl-0.50-2 \
	aspell-sv-0.51-0 \
	aspell5-da-1.4.42-1 \
	aspell6-ar-1.2-0 \
	aspell6-de-20030222-1 \
	aspell6-es-1.9a-1 \
	aspell6-hi-0.02-0 \
	aspell6-hu-0.99.4.2-0 \
	aspell6-it-2.2_20050523-0 \
	aspell6-pl-6.0_20061121-0 \
	aspell6-pt_BR-20090702-0 \
	aspell6-ru-0.99f7-1

all: checksrc allclean build install dict-install tar

checksrc:
	$(if $(files), @echo "", exit 1)

build:
	tar xzf src/aspell-$(ASPELL_VERSION).tar.gz
	(cd aspell-$(ASPELL_VERSION); \
	./configure --prefix=/opt/zimbra/aspell-$(ASPELL_VERSION); $(MAKE) $(MAKEARGS))

install:
	(cd aspell-$(ASPELL_VERSION); $(MAKE) $(MAKEARGS) install)
	(cd $(ZIMBRA_HOME); ln -s aspell-$(ASPELL_VERSION) aspell)

dict-install:
	export PATH=$(ZIMBRA_HOME)/aspell-$(ASPELL_VERSION)/bin:$$PATH
	for dict in $(ASPELL_DICTS); do \
		tar xjf src/$$dict.tar.bz2; \
		(cd $$dict; PATH=$(ZIMBRA_HOME)/aspell-$(ASPELL_VERSION)/bin:$$PATH ./configure; \
		PATH=$(ZIMBRA_HOME)/aspell-$(ASPELL_VERSION)/bin:$$PATH $(MAKE) $(MAKEARGS); \
		PATH=$(ZIMBRA_HOME)/aspell-$(ASPELL_VERSION)/bin:$$PATH $(MAKE) $(MAKEARGS) install) \
	done
	echo "add zimbra.rws" >> $(ZIMBRA_HOME)/aspell-$(ASPELL_VERSION)/lib/aspell-0.60/en.multi
	echo "add zimbra.rws" >> $(ZIMBRA_HOME)/aspell-$(ASPELL_VERSION)/lib/aspell-0.60/en_US.multi

tar: $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/aspell
	(cd $(ZIMBRA_HOME); tar czf \
	$(ASPELL_TGZ_TARGET) \
	aspell-$(ASPELL_VERSION))
	chmod -R a+w $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/aspell/*

$(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/aspell:
	mkdir -p $@

p4edit: $(ASPELL_TGZ_TARGET)
	p4 add $(ASPELL_TGZ_TARGET)
	p4 edit $(ASPELL_TGZ_TARGET)

clean: dict-clean
	rm -rf aspell-$(ASPELL_VERSION)

dict-clean:
	for dict in $(ASPELL_DICTS); do \
		rm -rf $$dict; \
	done

allclean: clean
	(rm -rf $(ZIMBRA_HOME)/aspell-$(ASPELL_VERSION) $(ZIMBRA_HOME)/aspell)
	rm -f $(ASPELL_TGZ_TARGET)

