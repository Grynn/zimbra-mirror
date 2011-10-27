# Makefile for entire install tree, for RPM packages.

# EXECUTABLES
SHELL	:= /bin/bash
PERL_ROOT ?= $(shell pwd)
P4_ROOT ?= $(shell cd $(PERL_ROOT)/../..; pwd)
ZIMBRA_HOME ?= /opt/zimbra
JAVA_HOME		:= /usr/local/java
export JAVA_HOME
TMPDIR	:= tmp
BUILD_PLATFORM ?= $(shell sh $(P4_ROOT)/ZimbraBuild/rpmconf/Build/get_plat_tag.sh)
PERL_TGZ_DEST_DIR := $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/perl
PERL_TGZ_DEST	:= $(PERL_TGZ_DEST_DIR)/perllib.tgz
PERL_BIN_TGZ_DEST := $(PERL_TGZ_DEST_DIR)/perlbin.tgz

ifeq ($(BUILD_PLATFORM), )
        BUILD_PLATFORM := "UNKNOWN"
endif

DEST_DIR		:= $(PERL_ROOT)/zimbramon
DEST_LIB_DIR	:= $(DEST_DIR)/lib
DEST_BIN_DIR	:= $(DEST_DIR)/bin

ifeq ($(BUILD_PLATFORM), MACOSX)
	DEST_BIN_DIR  :=	$(DEST_DIR)/usr/bin
endif

ifeq ($(BUILD_PLATFORM), MACOSXx86_10.5)
	DEST_BIN_DIR  :=	$(DEST_DIR)/bin
endif

MODULE_WWW	:= LWP::Protocol::https
MODULE_NETADDR_IP	:= NetAddr::IP
MODULE_EXTUTIL_MAKEMAKER	:= \
	ExtUtils::MakeMaker \
	CPAN::Meta::YAML \
	JSON::PP \
	Parse::CPAN::Meta \
	Version::Requirements \
	ExtUtils::Manifest \
	JSON::PP::Compat5006 \
	File::Copy::Recursive \
	version \
	CPAN::Meta \
	ExtUtils::MakeMaker

LWP_LIBS        := \
	Mozilla::CA \
	LWP::MediaTypes \
	Encode::Locale \
	HTTP::Date \
	HTTP::Message \
	HTTP::Negotiate \
	File::Listing \
	HTTP::Daemon \
	HTTP::Cookies \
	WWW::RobotRules \
	LWP::UserAgent

MODULE_BUILD_LIBS       := \
	Locale::Maketext::Simple \
	Params::Check \
	Module::Load \
	Module::CoreList \
	Module::Load::Conditional \
	IPC::Cmd \
	ExtUtils::CBuilder \
	S/SM/SMUELLER/ExtUtils-ParseXS-3.03_03.tar.gz \
	Test::Harness \
	D/DA/DAGOLDEN/Module-Build-0.3607.tar.gz

ifeq (RHEL4,$(findstring RHEL4,$(BUILD_PLATFORM)))
	MODULE_BUILD_LIBS	:= K/KW/KWILLIAMS/Module-Build-0.2808.tar.gz
	MODULE_WWW	:= G/GA/GAAS/libwww-perl-5.837.tar.gz
	MODULE_NETADDR_IP	:= M/MI/MIKER/NetAddr-IP-4.040.tar.gz
	MODULE_EXTUTIL_MAKEMAKER	:=  \
		ExtUtils::Manifest \
		ExtUtils::Command \
		ExtUtils::Installed \
		ExtUtils::Packlist \
		ExtUtils::Install \
		version \
		M/MS/MSCHWERN/ExtUtils-MakeMaker-6.56.tar.gz
	LWP_LIBS	:=
endif

ifeq (RHEL5,$(findstring RHEL5,$(BUILD_PLATFORM)))
	MODULE_BUILD_LIBS	:= K/KW/KWILLIAMS/Module-Build-0.2808.tar.gz
endif

ifeq (SLES10_64,$(findstring SLES10_64,$(BUILD_PLATFORM)))
	MODULE_BUILD_LIBS	:= K/KW/KWILLIAMS/Module-Build-0.2808.tar.gz
endif

ifeq (UBUNTU8,$(findstring UBUNTU8,$(BUILD_PLATFORM)))
	MODULE_BUILD_LIBS	:= K/KW/KWILLIAMS/Module-Build-0.2808.tar.gz
endif

LOGFILE ?= ThirdParty-Perllibs.log

PERL_VERSION :=	$(shell /usr/bin/perl -e 'print "$$]"')
BUILD_TYPE	:= PREFIX
LBIT		:=

ifeq (5.01,$(findstring 5.01,$(PERL_VERSION)))
	BUILD_TYPE	:= INSTALL_BASE
	LBIT		:= (cd $(DEST_LIB_DIR); ln -s ../lib perl5)
endif

ifdef BETA
	include $(PERL_ROOT)/../beta_versions.def
else
	include $(PERL_ROOT)/../versions.def
endif

PERL 	:= $(shell which perl)
GZIP    := $(shell which gzip)
BZIP2   := $(shell which bzip2)
CURL    := $(shell which curl)
TAR     := $(shell which tar)
FTP     := $(shell which ftp)
LYNX    := $(shell which lynx)
GPG     := $(shell which gpg)
LESS    := $(shell which less)
WGET    := $(shell which wget)
MAKE    := $(shell which make)
SED     := $(shell which sed)
UNZIP	:= $(shell which unzip)
CMIRROR	?= http://zre-matrix.eng.vmware.com/cpan/

BDB_INCLUDE	:= $(ZIMBRA_HOME)/bdb-$(BDB_VERSION)/include
MYSQL_INCLUDE	:= $(ZIMBRA_HOME)/mysql/include/mysql
LDAP_INCLUDE 	:= $(ZIMBRA_HOME)/openldap/include
SASL_INCLUDE	:= $(ZIMBRA_HOME)/cyrus-sasl/include

ifeq (MACOSX,$(findstring MACOSX,$(BUILD_PLATFORM)))
	EXPAT_INCLUDE := EXPATINCPATH=$(ZIMBRA_HOME)/include
	EXPAT_LIB := EXPATLIBPATH=$(ZIMBRA_HOME)/lib
endif

# DESTINATIONS

# Order is important here
PRE_PERL_LIBS	:= \
	Tree::DAG_Node \
	Test::Simple \
	Sub::Uplevel \
	Test::Exception \
	Test::Warn

PERL_LIBS 	:= \
	IO \
	Scalar::Util \
	IO::Socket::SSL \
	Socket6 \
	$(MODULE_EXTUTIL_MAKEMAKER) \
	DBI \
	Compress::Raw::Bzip2 \
	Compress::Raw::Zlib \
	IO::Compress::Base \
	IO::Zlib \
	Carp::Clan \
	Storable \
	Bit::Vector \
	HTML::Tagset \
	HTML::Parser \
	URI \
	Net::HTTP \
	Net::SMTP \
	Proc::ProcessTable \
	HTTP::Parser \
	$(LWP_LIBS) \
	$(MODULE_WWW) \
	IO::Stringy \
	E/ER/ERYQ/MIME-Lite-2.117.tar.gz \
	$(MODULE_BUILD_LIBS) \
	Date::Parse \
	Pod::Escapes \
	Pod::Simple \
	Test::Pod \
	Mail::Mailer \
	Convert::BinHex \
	Test::Tester \
	Test::NoWarnings \
	Test::Deep \
	File::Temp \
	D/DS/DSKOLL/MIME-tools-5.502.tar.gz \
	Task::Weaken \
	Class::Inspector \
	SOAP::Lite \
	Net::Telnet \
	Config::IniFiles \
	Config::Properties \
	Cache::FastMmap \
	Device::SerialPort \
	Date::Calc \
	Devel::Symdump \
	Pod::Coverage \
	Test::Pod::Coverage \
	S/SB/SBECK/Date-Manip-5.56.tar.gz \
	Time::HiRes \
	Convert::ASN1 \
	Net::LDAP \
	Unix::Syslog \
	Parallel::ForkManager \
	Package::Constants \
	Archive::Tar \
	Digest::SHA \
	Digest::SHA1 \
	Digest::HMAC \
	Convert::TNEF \
	Convert::UUlib \
	Net::IP \
	Net::DNS \
	File::Tail \
	File::Grep \
	Data::UUID \
	List::Compare \
	Getopt::Easy \
	Crypt::SaltedHash \
	Archive::Zip  \
	Error \
	Mail::DKIM \
	K/KH/KHAMPTON/XML-SAX-Base-1.04.tar.gz \
	XML::NamespaceSupport \
	XML::SAX \
	XML::SAX::Expat \
	XML::Simple \
	IO::Socket::INET6 \
	$(MODULE_NETADDR_IP) \
	Net::DNS::Resolver::Programmable \
	Geography::Countries \
	IP::Country \
	DBD::SQLite

ARRAY_COMPARE	:= \
	Array-Compare-1.17

NET_SSLeay := \
	Net-SSLeay-1.36

CRYPT_SSLeay := \
	Crypt-SSLeay-0.58

CRYPT_RANDOM := \
	Crypt-OpenSSL-Random-0.04

CRYPT_RSA := \
	Crypt-OpenSSL-RSA-0.27

DBFILE_PERL_LIBS := \
	DB_File

BDB_PERL_LIBS := \
	BerkeleyDB-0.43

MAIL_SPF := \
	Mail-SPF-v2.008

SA_PERL_LIBS := \
	Mail-SpamAssassin-$(SA_VERSION)

SWATCH := \
	swatch-3.2.3

LDAPAPI := \
	Net-LDAPapi-3.0.3

XML_PARSER := \
	XML-Parser-2.40

NET_SERVER := \
	Net-Server-0.99

DBD_PERL_LIBS 	:= \
	DBD-mysql-4.020

PERL_MM_USE_DEFAULT	:= 1
export PERL_MM_USE_DEFAULT

# TARGETS

CLEAN_TARGETS	:=	\
		$(TMPDIR) \
		$(DEST_DIR) \
		$(PERL_TGZ_DEST) \
		$(PERL_BIN_TGZ_DEST)

all: allclean setup $(DEST_LIB_DIR) $(DEST_BIN_DIR) $(PERL_TGZ_DEST_DIR) build check tarball

build:
	mkdir -p $(TMPDIR)
	@echo "Logging to $(LOGFILE)"

	@for lib in $(CRYPT_SSLeay); do \
		echo -n "Compiling perl lib $$lib ..."; \
		cp $(PERL_ROOT)/$$lib.tar.gz $(TMPDIR); \
		(cd $(TMPDIR); tar xfz $$lib.tar.gz; cd $$lib; \
			$(PERL) -I$(DEST_LIB_DIR) Makefile.PL PREFIX=$(DEST_DIR) LIB=$(DEST_LIB_DIR) --lib=$(ZIMBRA_HOME)/openssl-$(OPENSSL_VERSION)/lib LDFLAGS="-L$(ZIMBRA_HOME)/openssl-$(OPENSSL_VERSION)/lib -R$(ZIMBRA_HOME)/openssl-$(OPENSSL_VERSION)/lib"; \
			LD_RUN_PATH="$(ZIMBRA_HOME)/openssl-$(OPENSSL_VERSION)/lib" $(MAKE); $(MAKE) install;) >>$(LOGFILE) 2>&1; \
		if [ $$? -ne 0 ]; then  echo "FAILED.";  else  echo "done.";  fi \
	done

	@for lib in $(CRYPT_RANDOM); do \
		echo -n "Compiling perl lib $$lib ..."; \
		cp $(PERL_ROOT)/$$lib.tar.gz $(TMPDIR); \
		(cd $(TMPDIR); tar xfz $$lib.tar.gz; cd $$lib; \
			$(PERL) -I$(DEST_LIB_DIR) Makefile.PL PREFIX=$(DEST_DIR) LIB=$(DEST_LIB_DIR) LIBS="-L$(ZIMBRA_HOME)/openssl-$(OPENSSL_VERSION)/lib -R$(ZIMBRA_HOME)/openssl-$(OPENSSL_VERSION)/lib -lssl -lcrypto" INC="-I$(ZIMBRA_HOME)/openssl-$(OPENSSL_VERSION)/include"; \
			LD_RUN_PATH="$(ZIMBRA_HOME)/openssl-$(OPENSSL_VERSION)/lib" $(MAKE); $(MAKE) install;) >>$(LOGFILE) 2>&1; \
		if [ $$? -ne 0 ]; then  echo "FAILED.";  else  echo "done.";  fi \
	done

	@for lib in $(CRYPT_RSA); do \
		echo -n "Compiling perl lib $$lib ..."; \
		cp $(PERL_ROOT)/$$lib.tar.gz $(TMPDIR); \
		(cd $(TMPDIR); tar xfz $$lib.tar.gz; cd $$lib; \
			$(PERL) -I$(DEST_LIB_DIR) Makefile.PL PREFIX=$(DEST_DIR) LIB=$(DEST_LIB_DIR) LIBS="-L$(ZIMBRA_HOME)/openssl-$(OPENSSL_VERSION)/lib -R$(ZIMBRA_HOME)/openssl-$(OPENSSL_VERSION)/lib -lssl -lcrypto" INC="-I$(ZIMBRA_HOME)/openssl-$(OPENSSL_VERSION)/include"; \
			LD_RUN_PATH="$(ZIMBRA_HOME)/openssl-$(OPENSSL_VERSION)/lib" $(MAKE); $(MAKE) install;) >>$(LOGFILE) 2>&1; \
		if [ $$? -ne 0 ]; then  echo "FAILED.";  else  echo "done.";  fi \
	done

	@for lib in $(ARRAY_COMPARE); do \
		echo -n "Compiling perl lib $$lib ..."; \
		cp $(PERL_ROOT)/$$lib.tar.gz $(TMPDIR); \
		(cd $(TMPDIR); tar xfz $$lib.tar.gz; cd $$lib; \
			$(PERL) -I$(DEST_LIB_DIR) Makefile.PL PREFIX=$(DEST_DIR) LIB=$(DEST_LIB_DIR); \
			$(MAKE); $(MAKE) install;) >>$(LOGFILE) 2>&1; \
		if [ $$? -ne 0 ]; then  echo "FAILED.";  else  echo "done.";  fi \
	done

	@for lib in $(PRE_PERL_LIBS); do \
		echo -n "Compiling perl lib $$lib ..."; \
		LDFLAGS="-fPIC" CFLAGS="-fPIC" BERKELEYDB_INCLUDE=$(BDB_INCLUDE) BERKELEYDB_LIB=$(BDB_LIB_DIR) $(PERL) -I$(DEST_LIB_DIR) -MCPAN -e "$$CPAN::Config->{make_arg}.=' CCCDLFLAGS="-fPIC" CFLAGS="-fPIC" LDFLAGS="-fPIC"'; $$CPAN::Config->{makepl_arg}.=' CCCDLFLAGS="-fPIC" CFLAGS="-fPIC" LDFLAGS="-fPIC"'; force (\"install\", \"$$lib\")" >> $(LOGFILE) 2>&1; \
		if [ $$? -ne 0 ]; then  echo "FAILED.";  else  echo "done.";  fi \
	done

	@for lib in $(NET_SSLeay); do \
		echo -n "Compiling perl lib $$lib ..."; \
		cp $(PERL_ROOT)/$$lib.tar.gz $(TMPDIR); \
		(cd $(TMPDIR); tar xfz $$lib.tar.gz; cd $$lib; \
		OPENSSL_PREFIX=$(ZIMBRA_HOME)/openssl-$(OPENSSL_VERSION) $(PERL) -I$(DEST_LIB_DIR) Makefile.PL PREFIX=$(DEST_DIR) LIB=$(DEST_LIB_DIR); \
		LD_RUN_PATH="$(ZIMBRA_HOME)/openssl-$(OPENSSL_VERSION)" $(MAKE); $(MAKE) install;) >>$(LOGFILE) 2>&1; \
		if [ $$? -ne 0 ]; then  echo "FAILED.";  else  echo "done.";  fi \
	done
	
	@for lib in $(NET_SERVER); do \
		echo -n "Compiling perl lib $$lib ..."; \
		cp $(PERL_ROOT)/$$lib.tar.gz $(TMPDIR); \
		(cd $(TMPDIR); tar xfz $$lib.tar.gz; cd $$lib; \
		patch -g0 -N -p1 < $(PERL_ROOT)/patches/Net-Server.patch; \
		patch -g0 -N -p1 < $(PERL_ROOT)/patches/Net-Server-0.99-IPv6.patch; \
		$(PERL) -I$(DEST_LIB_DIR) Makefile.PL PREFIX=$(DEST_DIR) LIB=$(DEST_LIB_DIR); \
		$(MAKE); $(MAKE) install;) >>$(LOGFILE) 2>&1; \
		if [ $$? -ne 0 ]; then  echo "FAILED.";  else  echo "done.";  fi \
	done
	
	@for lib in $(XML_PARSER); do \
		echo -n "Compiling perl lib $$lib ..."; \
		cp $(PERL_ROOT)/$$lib.tar.gz $(TMPDIR); \
		(cd $(TMPDIR); tar xfz $$lib.tar.gz; cd $$lib; \
		$(PERL) -I$(DEST_LIB_DIR) Makefile.PL PREFIX=$(DEST_DIR) LIB=$(DEST_LIB_DIR) $(EXPAT_LIB) $(EXPAT_INCLUDE); \
		$(MAKE); $(MAKE) install;) >>$(LOGFILE) 2>&1; \
		if [ $$? -ne 0 ]; then  echo "FAILED.";  else  echo "done.";  fi \
	done

	@for lib in $(PERL_LIBS); do \
		echo -n "Compiling perl lib $$lib ..."; \
		LDFLAGS="-fPIC" CFLAGS="-fPIC" BERKELEYDB_INCLUDE=$(BDB_INCLUDE) BERKELEYDB_LIB=$(BDB_LIB_DIR) $(PERL) -I$(DEST_LIB_DIR) -MCPAN -e "$$CPAN::Config->{make_arg}.=' CCCDLFLAGS="-fPIC" CFLAGS="-fPIC" LDFLAGS="-fPIC"'; $$CPAN::Config->{makepl_arg}.=' CCCDLFLAGS="-fPIC" CFLAGS="-fPIC" LDFLAGS="-fPIC"'; force (\"install\", \"$$lib\")" >> $(LOGFILE) 2>&1; \
		if [ $$? -ne 0 ]; then  echo "FAILED.";  else  echo "done.";  fi \
	done

	@for lib in $(LDAPAPI); do \
		echo -n "Compiling perl lib $$lib ..."; \
		cp $(PERL_ROOT)/$$lib.tar.gz $(TMPDIR); \
		(cd $(TMPDIR); tar xfz $$lib.tar.gz; cd $$lib; \
			$(PERL) -I$(DEST_LIB_DIR) Makefile.PL PREFIX=$(DEST_DIR) LIB=$(DEST_LIB_DIR) \
				-sdk openldap -lib_path $(LDAP_LIB_DIR) -include_path $(LDAP_INCLUDE) -sasl_include_path $(SASL_INCLUDE); \
			LD_RUN_PATH="$(LDAP_LIB_DIR)" $(MAKE); $(MAKE) install;) >> $(LOGFILE) 2>&1; \
		if [ $$? -ne 0 ]; then echo "FAILED."; else echo "done."; fi \
	done

	@for lib in $(BDB_PERL_LIBS); do \
		echo -n "Compiling perl lib $$lib ..."; \
		cp $(PERL_ROOT)/$$lib.tar.gz $(TMPDIR); \
		(cd $(TMPDIR); tar xfz $$lib.tar.gz; cd $$lib; \
		BERKELEYDB_INCLUDE=$(BDB_INCLUDE) BERKELEYDB_LIB=$(BDB_LIB_DIR) $(BDB_FILE_ARGS) $(PERL) -I$(DEST_LIB_DIR) Makefile.PL PREFIX=$(DEST_DIR) LIB=$(DEST_LIB_DIR); \
		LD_RUN_PATH=$(BDB_LIB_DIR) $(MAKE); $(MAKE) install;) >> $(LOGFILE) 2>&1; \
		if [ $$? -ne 0 ]; then  echo "FAILED.";  else  echo "done.";  fi \
	done

	@for lib in $(DBFILE_PERL_LIBS); do \
		echo -n "Compiling perl lib $$lib ..."; \
		LD_RUN_PATH=$(ZIMBRA_HOME)/$(BDB_LIB_DIR) DB_FILE_INCLUDE=$(BDB_INCLUDE) DB_FILE_LIB=$(BDB_LIB_DIR) $(DB_FILE_ARGS) $(PERL) -I$(DEST_LIB_DIR) -MCPAN -e "force (\"install\", \"$$lib\")" >> $(LOGFILE) 2>&1; \
		if [ $$? -ne 0 ]; then  echo "FAILED.";  else  echo "done.";  fi \
	done

	@for lib in $(DBD_PERL_LIBS); do \
		echo -n "Compiling perl lib $$lib ..."; \
		cp $(PERL_ROOT)/$$lib.tar.gz $(TMPDIR); \
		(cd $(TMPDIR); tar xfz $$lib.tar.gz; cd $$lib; \
		$(PERL) -I$(DEST_LIB_DIR) Makefile.PL PREFIX=$(DEST_DIR) LIB=$(DEST_LIB_DIR) --ssl --nocatchstderr \
			--libs="-L$(ZIMBRA_HOME)/mysql/lib -L/opt/zimbra/openssl/lib -lmysqlclient -lpthread -lz -lm -lrt -lssl -lcrypto -ldl" \
			--mysql_config="$(ZIMBRA_HOME)/mysql/bin/mysql_config"; \
		$(SED) -i .bak -e 's#-arch x86_64 -arch i386 -arch ppc -bundle -undefined dynamic_lookup -L/usr/local/lib#-arch x86_64 -bundle -undefined dynamic_lookup#' Makefile; \
		$(SED) -i .bak -e 's#-arch x86_64 -arch i386 -arch ppc -L/usr/local/lib#-arch x86_64#' Makefile; \
		$(SED) -i .bak -e 's#-arch x86_64 -arch i386 -arch ppc -g -pipe -fno-common -DPERL_DARWIN -fno-strict-aliasing -I/usr/local/include#-arch x86_64 -g -pipe -fno-common -DPERL_DARWIN -fno-strict-aliasing#' Makefile; \
		$(SED) -i .bak -e 's#-arch x86_64 -arch i386 -bundle -undefined dynamic_lookup -L/usr/local/lib -fstack-protector#-arch x86_64 -bundle -undefined dynamic_lookup -fstack-protector#' Makefile; \
		$(SED) -i .bak -e 's#-arch x86_64 -arch i386 -fstack-protector -L/usr/local/lib#-arch x86_64 -fstack-protector#' Makefile; \
		$(SED) -i .bak -e 's#-arch x86_64 -arch i386 -g -pipe -fno-common -DPERL_DARWIN -fno-strict-aliasing -fstack-protector -I/usr/local/include#-arch x86_64 -g -pipe -fno-common -DPERL_DARWIN -fno-strict-aliasing -fstack-protector#' Makefile; \
		LD_RUN_PATH=/opt/zimbra/lib:/opt/zimbra/mysql/lib:/opt/zimbra/openssl/lib $(MAKE); $(MAKE) install;) >> $(LOGFILE) 2>&1; \
		if [ $$? -ne 0 ]; then  echo "FAILED.";  else  echo "done.";  fi \
	done

	@for lib in $(MAIL_SPF); do \
		echo -n "Compiling perl lib $$lib ..."; \
		cp $(PERL_ROOT)/$$lib.tar.gz $(TMPDIR); \
		(cd $(TMPDIR); tar xfz $$lib.tar.gz; cd $$lib; \
		$(PERL) -I$(DEST_LIB_DIR) Build.PL --prefix $(DEST_DIR) --install_path lib=$(DEST_LIB_DIR) --install_path sbin=$(DEST_DIR)/sbin; \
		./Build test; \
		./Build install --uninst 1;) >> $(LOGFILE) 2>&1; \
		if [ $$? -ne 0 ]; then  echo "FAILED.";  else  echo "done.";  fi \
	done

	@for lib in $(SA_PERL_LIBS); do \
		echo -n "Compiling perl lib $$lib" ...; \
		cp $(PERL_ROOT)/$$lib.tar.gz $(TMPDIR); \
		(cd $(TMPDIR); tar xzf $$lib.tar.gz; cd $$lib; \
			$(PERL) -I$(DEST_LIB_DIR) Makefile.PL PREFIX=$(ZIMBRA_HOME)/zimbramon LIB=$(ZIMBRA_HOME)/zimbramon/lib DATADIR=$(ZIMBRA_HOME)/conf/spamassassin CONFDIR=/opt/zimbra/conf/spamassassin LOCALSTATEDIR=$(ZIMBRA_HOME)/data/spamassassin; \
			$(MAKE); $(MAKE) install DESTDIR=$(DEST_DIR); \
			cp -R $(DEST_DIR)/opt/zimbra/zimbramon/* $(DEST_DIR); \
			rm -rf $(DEST_DIR)/opt;) >> $(LOGFILE) 2>&1; \
		if [ $$? -ne 0 ]; then  echo "FAILED.";  else  echo "done.";  fi \
	done

	@for lib in $(SWATCH); do \
		echo -n "Compiling perl lib $$lib ..."; \
		cp $(PERL_ROOT)/$$lib.tar.gz $(TMPDIR); \
		(cd $(TMPDIR); tar xzf $$lib.tar.gz; cd $$lib; \
			$(SED) -i .bak -e 's#/usr/lib/sendmail /usr/sbin/sendmail#/opt/zimbra/postfix/sbin/sendmail#' lib/Swatch/Actions.pm; \
			rm -f lib/Swatch/Actions.pm.bak; \
			BERKELEYDB_INCLUDE=$(BDB_INCLUDE) BERKELEYDB_LIB=$(BDB_LIB_DIR) \
			$(PERL) -I$(DEST_LIB_DIR) Makefile.PL PREFIX=$(DEST_DIR) LIB=$(DEST_LIB_DIR); \
			$(MAKE); $(MAKE) install;) >> $(LOGFILE) 2>&1; \
		if [ $$? -ne 0 ]; then  echo "FAILED.";  else  echo "done.";  fi \
	done

check:
	@echo "Verifying perl module build...";
	$(PERL_ROOT)/zmcheckbuiltmodules.pl >> $(LOGFILE) 2>&1;

tarball:
	@echo -n "Creating $(PERL_TGZ_DEST)..."; \
	(cd $(DEST_LIB_DIR); rm -f i386-linux-thread-multi/goferperf.pl; \
	tar czf $(PERL_TGZ_DEST) .); \
	if [ $$? -ne 0 ]; then  echo "FAILED.";  else  echo "done.";  fi
	@echo -n "Creating $(PERL_BIN_TGZ_DEST)..."; \
	(cd $(DEST_BIN_DIR); \
	tar cfz $(PERL_BIN_TGZ_DEST) sa-compile sa-learn sa-update spamassassin spamc spamd); \
	if [ $$? -ne 0 ]; then  echo "FAILED.";  else  echo "done.";  fi
	chmod -R a+w $(PERL_TGZ_DEST_DIR)/*

$(PERL_TGZ_DEST_DIR):
	mkdir -p $@

$(DEST_LIB_DIR):
	mkdir -p $@
	$(LBIT)

$(DEST_BIN_DIR):
	mkdir -p $@

setup:
	mkdir -p ${HOME}/.cpan/CPAN
	sed -e "s|@@LYNX@@|$(LYNX)|g" -e "s|@@WGET@@|$(WGET)|g" -e "s|@@LESS@@|$(LESS)|g" -e "s|@@GPG@@|$(GPG)|g" -e "s|@@UNZIP@@|$(UNZIP)|g" -e "s|@@CURL@@|$(CURL)|g" -e "s|@@BZIP2@@|$(BZIP2)|g" -e "s|@@MAKE@@|$(MAKE)|g" -e "s|@@FTP@@|$(FTP)|g" -e "s|@@TAR@@|$(TAR)|g" -e "s|@@GZIP@@|$(GZIP)|g" -e "s|@@BUILD_TYPE@@|$(BUILD_TYPE)|g" -e "s|@@BUILD_ROOT@@|$(PERL_ROOT)|g" -e "s|@@CMIRROR@@|$(CMIRROR)|g" MyConfig.pm.template > ${HOME}/.cpan/CPAN/MyConfig.pm 

p4edit: $(PERL_TGZ_DEST) 
	p4 add $(PERL_TGZ_DEST)
	p4 edit $(PERL_TGZ_DEST)

allclean:
	rm -rf $(CLEAN_TARGETS)
	rm -f $(LOGFILE)

clean:
	rm -rf $(CLEAN_TARGETS)
