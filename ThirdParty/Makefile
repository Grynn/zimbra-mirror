SHELL	:= /bin/bash
THIRDPARTY_ROOT := $(shell pwd)
P4_ROOT ?= $(shell cd $(THIRDPARTY_ROOT)/..; pwd)

BUILD_PLATFORM ?= $(shell sh $(P4_ROOT)/ZimbraBuild/rpmconf/Build/get_plat_tag.sh)
ifeq ($(BUILD_PLATFORM), )
	BUILD_PLATFORM := "UNKNOWN"
endif

OS_PLATFORM ?= $(shell uname -p)

ifdef BETA
	include $(THIRDPARTY_ROOT)/beta_versions.def
else
	include $(THIRDPARTY_ROOT)/versions.def
endif

MAKE := make
ECHO := /bin/echo
MAKEARGS ?= -j5

ZIMBRA_HOME ?= /opt/zimbra

DIRS := openssl \
	mysql \
	bdb \
	libxml2 \
	heimdal \
	curl \
	cyrus-sasl \
	libtool \
	openldap \
	aspell \
	sendmail \
	clamav \
	pcre \
	apache-httpd \
	php \
	expat \
	popt \
	postfix \
	dspam \
	rrdtool \
	rsync \
	snmp \
	memcached \
	nginx \
	tcmalloc \
	altermime

LOGFILE := ThirdParty.make.log

all: allclean $(ZIMBRA_HOME) build 

$(ZIMBRA_HOME):
	mkdir -p $@

build:
	 @(for dir in $(DIRS); do \
		$(ECHO) -n "*** Building in $$dir "; \
		$(MAKE) -C $$dir >> $(LOGFILE) 2>&1; \
		if [ $$? -ne 0 ]; then  $(ECHO) "FAILED.";  else  $(ECHO) "SUCCEEDED.";  fi ; \
	done)
	@($(MAKE) -C apache-httpd tar >> $(LOGFILE) 2>&1)
	@($(MAKE) -C pcre binrm >> $(LOGFILE) 2>&1)
	@($(ECHO) -n "*** Building in Perl "; $(MAKE) -C Perl ) 

clean:
	for dir in $(DIRS); do \
		$(ECHO) "*** Building clean in $$dir"; \
		$(MAKE) -C $$dir clean; \
	done
	@($(ECHO) "*** Building clean in Perl"; $(MAKE) -C Perl clean )

allclean:
	for dir in $(DIRS); do \
		$(ECHO) "*** Building allclean in $$dir"; \
		$(MAKE) -C $$dir allclean; \
	done
	@($(ECHO) "*** Building allclean in Perl"; $(MAKE) -C Perl allclean )
	rm -f $(LOGFILE)
	rm -rf $(ZIMBRA_HOME)/conf
	rm -rf $(ZIMBRA_HOME)/log
	rm -rf $(ZIMBRA_HOME)/zimbramon
	rm -rf $(ZIMBRA_HOME)/data

mysql: force
	(cd $@; $(MAKE) > $(LOGFILE).$$ 2>&1)

bdb: force
	(cd $@; $(MAKE) > $(LOGFILE).$$ 2>&1)

libtool: force
	(cd $@; $(MAKE) > $(LOGFILE).$$ 2>&1)

openldap: force
	(cd $@; $(MAKE) > $(LOGFILE).$$ 2>&1)

cyrus-sasl: force
	(cd $@; $(MAKE) > $(LOGFILE).$$ 2>&1)

aspell: force
	(cd $@; $(MAKE) > $(LOGFILE).$$ 2>&1)

clamav: force
	(cd $@; $(MAKE) > $(LOGFILE).$$ 2>&1)

postfix: force
	(cd $@; $(MAKE) > $(LOGFILE).$$ 2>&1)

perl:
	(cd $@; $(MAKE))

p4edit:
	for dir in $(DIRS); do \
		$(ECHO) "***  p4 editing in $$dir"; \
		$(MAKE) -C $$dir p4edit; \
	done
	@($(ECHO) -n "*** p4 editing in Perl "; $(MAKE) -C Perl p4edit ) 


force: ;
