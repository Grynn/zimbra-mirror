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
	DBD::mysql \

PERL_LIBS 	:= \
	DBI \
	HTML::Tagset \
	HTML::Parser \
	URI \
	Net::HTTP \
	HTTP::Parser \
	IO::Stringy \
	MIME::Lite \
	Mail::Mailer \
	MIME::Tools \
	SOAP::Lite \
	XML::Parser \
	Net::Telnet \
	Device::SerialPort \
	Date::Calc \
	Date::Manip \
	Date::Parse \
	Time::HiRes \
	Net::LDAP \
	Convert::ASN1 \
	Crypt::SSLeay \
	Net::Server \
	Net::Server \
	Unix::Syslog \
	Compress::Zlib \
	BerkeleyDB \
	Digest::SHA1 \
	Convert::TNEF \
	Convert::UUlib \
	Archive::Tar \
	Net::IP \
	Net::DNS \
	Archive::Zip 

DBFILE_PERL_LIBS := \
	DB_File

SA_PERL_LIBS = \
	Mail::SpamAssassin

SWATCH = \
	swatch-3.1.1

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

	@for lib in $(PERL_LIBS); do \
		echo "Compiling perl lib $$lib"; \
		BERKELEYDB_INCLUDE=$(BDB_INC) BERKELEYDB_LIB=$(BDB_LIB) perl -MCPAN -e "force (\"install\", \"$$lib\")" ; \
	done

	@for lib in $(DBFILE_PERL_LIBS); do \
		echo "Compiling perl lib $$lib"; \
		perl -MCPAN -e "force (\"install\", \"$$lib\")" ; \
	done

	echo "Compiling perl lib DBD::mysql"; \
	perl -MCPAN -e 'install DBI; $$CPAN::Config->{makepl_arg}.=" --libs=\"-lmysqlclient -lz -lm\" --cflags=\"-I/opt/zimbra/mysql/include -Os -arch ppc -fno-common\" "; force ("install", "DBD::mysql")' ; \

	@for lib in $(SA_PERL_LIBS); do \
		echo "Compiling perl lib $$lib"; \
		perl -MCPAN -e "force (\"install\", \"$$lib\")" ; \
	done	

	@for lib in $(SWATCH); do \
		echo "Compiling perl lib $$lib"; \
		cp $(PERL_LIB_SOURCE)/$$lib.tar.gz $(TMPDIR); \
		(cd $(TMPDIR); tar xzf $$lib.tar.gz; cd $$lib; \
			BERKELEYDB_INCLUDE=$(BDB_INC) BERKELEYDB_LIB=$(BDB_LIB) \
			$(PERL) -I$(DEST_LIB_DIR) Makefile.PL PREFIX=$(DEST_DIR) LIB=$(DEST_LIB_DIR); \
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
