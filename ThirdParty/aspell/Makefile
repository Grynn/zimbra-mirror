ASPELL_ROOT := $(shell pwd)
P4_ROOT ?= $(shell cd $(ASPELL_ROOT)/../..; pwd)

BUILD_PLATFORM ?= $(shell sh $(P4_ROOT)/ZimbraBuild/rpmconf/Build/get_plat_tag.sh)

ifeq ($(BUILD_PLATFORM), )
        BUILD_PLATFORM := "UNKNOWN"
endif

ZIMBRA_HOME ?= /opt/zimbra

ASPELL_VERSION := 0.60.3

ASPELL_DICTS := \
	aspell6-en-6.0-0

all: build install dict-install tar

build:
	tar xzf aspell-$(ASPELL_VERSION).tar.gz
	(cd aspell-$(ASPELL_VERSION); \
	./configure --prefix=/opt/zimbra/aspell-$(ASPELL_VERSION); make)

install:
	(cd aspell-$(ASPELL_VERSION); make install)
	(cd $(ZIMBRA_HOME); ln -s aspell-$(ASPELL_VERSION) aspell)

dict-install:
	export PATH=$(ZIMBRA_HOME)/aspell-$(ASPELL_VERSION)/bin:$$PATH
	for dict in $(ASPELL_DICTS); do \
		tar xjf $$dict.tar.bz2; \
		(cd $$dict; PATH=$(ZIMBRA_HOME)/aspell-$(ASPELL_VERSION)/bin:$$PATH ./configure; \
		PATH=$(ZIMBRA_HOME)/aspell-$(ASPELL_VERSION)/bin:$$PATH make; \
		PATH=$(ZIMBRA_HOME)/aspell-$(ASPELL_VERSION)/bin:$$PATH make install) \
	done

tar:
	(cd $(ZIMBRA_HOME); tar czf \
	$(ASPELL_ROOT)/builds/$(BUILD_PLATFORM)/aspell-$(ASPELL_VERSION).tgz \
	aspell-$(ASPELL_VERSION))

clean: dict-clean
	rm -rf aspell-$(ASPELL_VERSION)

dict-clean:
	for dict in $(ASPELL_DICTS); do \
		rm -rf $$dict; \
	done

allclean: clean
	(cd $(ZIMBRA_HOME); rm -rf aspell-$(ASPELL_VERSION) aspell)

