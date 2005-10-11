# Makefile for entire install tree, for RPM packages.

# EXECUTABLES

PERL_ROOT ?= $(shell pwd)
P4_ROOT ?= $(shell cd $(PERL_ROOT)/../..; pwd)
ZIMBRA_HOME ?= /opt/zimbra

PERL 	:= $(shell which perl)

BDB_VERSION ?= 4.2.52.2
BDB_INC ?= $(ZIMBRA_HOME)/sleepycat-$(BDB_VERSION)/include
BDB_LIB ?= $(ZIMBRA_HOME)/sleepycat-$(BDB_VERSION)/lib

# DESTINATIONS

# Order is important here

DBD_PERL_LIBS 	:= \
	DBD-mysql-2.9005_3 \

PERL_LIBS 	:= \
	DBI-1.47 \
	HTML-Tagset-3.03 \
	HTML-Parser-3.36 \
	URI-1.31 \
	libwww-perl-5.800 \
	HTTP-Parser-0.02 \
	IO-stringy-1.220 \
	MIME-Lite-3.01 \
	MailTools-1.62 \
	MIME-tools-5.411 \
	SOAP-Lite-0.55 \
	XML-Parser-2.34 \
	Net-Telnet-3.03 \
	Device-SerialPort-1.000002 \
	Date-Calc-5.4 \
	DateManip-5.42a \
	TimeDate-1.16 \
	Time-HiRes-1.65 \
	swatch-3.1.1 \
	perl-ldap-0.3202 \
	Convert-ASN1-0.18 \
	Crypt-SSLeay-0.51 \
	Net-Server-0.85 \
	Net-Server-0.87 \
	Unix-Syslog-0.99 \
	Compress-Zlib-1.34 \
	BerkeleyDB-0.26 \
	Digest-SHA1-2.10 \
	Convert-TNEF-0.17 \
	Convert-UUlib-1.051 \
	Archive-Tar-1.24 \
	Net-IP-1.23 \
	Net-DNS-0.51 \
	Archive-Zip-1.14 

DBFILE_PERL_LIBS := \
	DB_File-1.811

SA_PERL_LIBS = \
	Mail-SpamAssassin-3.0.4

PERL_MM_USE_DEFAULT	:= 1
export PERL_MM_USE_DEFAULT

JAVA_HOME		:= /usr/local/java
export JAVA_HOME

TMPDIR	:= tmp

PERL_LIB_SOURCE  := $(shell pwd)

P4_ROOT ?= $(shell cd $(PERL_LIB_SOURCE)/../..; pwd)

BUILD_PLATFORM ?= $(shell sh $(P4_ROOT)/ZimbraBuild/rpmconf/Build/get_plat_tag.sh)

PERL_TGZ_DEST_DIR := $(PERL_LIB_SOURCE)/builds/$(BUILD_PLATFORM)
PERL_TGZ_DEST	:= $(PERL_TGZ_DEST_DIR)/perllib.tgz

ifeq ($(BUILD_PLATFORM), )
        BUILD_PLATFORM := "UNKNOWN"
endif

DEST_DIR		:= $(PERL_LIB_SOURCE)/zimbramon
DEST_LIB_DIR	:= $(DEST_DIR)/lib

# TARGETS

CLEAN_TARGETS	=	\
		$(TMPDIR) \
		$(DEST_DIR) \
		perllib.tgz

all: $(DEST_LIB_DIR) $(PERL_TGZ_DEST_DIR)
	mkdir -p $(TMPDIR)
	export BERKELEYDB_INCLUDE=$(BDB_INC)
	export BERKELEYDB_LIB=$(BDB_LIB)

	@for lib in $(PERL_LIBS); do \
		echo "Compiling perl lib $$lib"; \
		cp $(PERL_LIB_SOURCE)/$$lib.tar.gz $(TMPDIR); \
		(cd $(TMPDIR); tar xzf $$lib.tar.gz; cd $$lib; \
		BERKELEYDB_INCLUDE=$(BDB_INC) BERKELEYDB_LIB=$(BDB_LIB) \
		$(PERL) -I$(DEST_LIB_DIR) Makefile.PL PREFIX=$(DEST_DIR) LIB=$(DEST_LIB_DIR); \
		make; make install;) \
	done

	@for lib in $(DBFILE_PERL_LIBS); do \
		echo "Compiling perl lib $$lib"; \
		cp $(PERL_LIB_SOURCE)/$$lib.tar.gz $(TMPDIR); \
		(cd $(TMPDIR); tar xzf $$lib.tar.gz; cd $$lib; \
		cp config.in config.in.fcs; \
		sed -e 's|^INCLUDE.*|INCLUDE = /opt/zimbra/sleepycat/include|' \
		-e 's|^LIB.*|LIB = /opt/zimbra/sleepycat/lib|' \
		config.in.fcs > config.in; \
		$(PERL) -I$(DEST_LIB_DIR) Makefile.PL PREFIX=$(DEST_DIR) LIB=$(DEST_LIB_DIR); \
		make; make install;) \
	done

	@for lib in $(DBD_PERL_LIBS); do \
		cp $(PERL_LIB_SOURCE)/$$lib.tar.gz $(TMPDIR); \
		(cd $(TMPDIR); tar xzf $$lib.tar.gz; cd $$lib; \
		$(PERL) -I$(DEST_LIB_DIR) Makefile.PL PREFIX=$(DEST_DIR) LIB=$(DEST_LIB_DIR) --mysql_config=/opt/zimbra/mysql/bin/mysql_config --libs="-L/opt/zimbra/mysql/lib -lmysqlclient -lz -lcrypt -lnsl -lm"; \
		make; make install;) \
	done	

	@for lib in $(SA_PERL_LIBS); do \
		cp $(PERL_LIB_SOURCE)/$$lib.tar.gz $(TMPDIR); \
		(cd $(TMPDIR); tar xzf $$lib.tar.gz; cd $$lib; \
		$(PERL) -I$(DEST_LIB_DIR) Makefile.PL PREFIX=$(DEST_DIR) LIB=$(DEST_LIB_DIR) DATADIR=$(DEST_DIR)/share CONFDIR=$(DEST_DIR)/spamassassin; \
		make; make install;) \
	done	

	(cd $(DEST_LIB_DIR); tar czf $(PERL_TGZ_DEST) .)
	chmod -R a+w $(PERL_TGZ_DEST_DIR)

$(PERL_TGZ_DEST_DIR):
	mkdir -p $@

$(DEST_LIB_DIR):
	mkdir -p $@

clean:
	rm -rf $(CLEAN_TARGETS)
