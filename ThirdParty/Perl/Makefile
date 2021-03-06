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
PERL_BIN_TGZ_DEST := $(PERL_TGZ_DEST_DIR)/perlbin.tgz
PERL_TGZ_DEST	:= $(PERL_TGZ_DEST_DIR)/perllib.tgz
PERL_SA_RULES	:= $(PERL_TGZ_DEST_DIR)/perl-sa-rules.tgz

ifeq ($(BUILD_PLATFORM), )
        BUILD_PLATFORM := "UNKNOWN"
endif

DEST_DIR		:= $(PERL_ROOT)/zimbramon
DEST_LIB_DIR	:= $(DEST_DIR)/lib
DEST_BIN_DIR	:= $(DEST_DIR)/bin

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
CMIRROR	?= http://zre-matrix.eng.zimbra.com/cpan/

BDB_INCLUDE	:= $(ZIMBRA_HOME)/bdb-$(BDB_VERSION)/include
LDAP_INCLUDE 	:= $(ZIMBRA_HOME)/openldap/include
SASL_INCLUDE	:= $(ZIMBRA_HOME)/cyrus-sasl/include

# Order is important here
PRE_PERL_LIBS	:= \
	Test::Simple \
	Try::Tiny \
	Test::Fatal \
	ExtUtils::MakeMaker \
	T/TO/TOKUHIROM/Test-Requires-0.06.tar.gz \
	List::Util \
	CPAN::Meta::YAML \
	JSON::PP \
	Parse::CPAN::Meta \
	CPAN::Meta::Requirements \
	ExtUtils::Manifest \
	JSON::PP::Compat5006 \
	File::Copy::Recursive \
	parent \
	version \
	CPAN::Meta \
	ExtUtils::MakeMaker \
	Params::Util \
	List::MoreUtils \
	Sub::Install \
	Package::DeprecationManager \
	Package::Stash::XS \
	Data::OptList \
	Sub::Exporter \
	MRO::Compat \
	Task::Weaken \
	Eval::Closure \
	Module::Runtime \
	Dist::CheckConflicts \
	Module::Implementation \
	Package::Stash \
	ExtUtils::Config \
	ExtUtils::InstallPaths \
	Test::Harness \
	ExtUtils::Helpers \
	Module::Build::Tiny \
	Class::Load \
	Perl::OSType \
	Module::Metadata \
	ExtUtils::CBuilder \
	ExtUtils::ParseXS \
	Pod::Simple \
	Pod::Man \
	Module::Build \
	Class::Load::XS \
	Sub::Name \
	Carp \
	Sub::Exporter::Progressive \
	Devel::GlobalDestruction::XS \
	Devel::GlobalDestruction \
	Test::Tester \
	Test::NoWarnings \
	Test::Deep \
	CPAN::Meta::Check \
	Test::CheckDeps \
	Devel::StackTrace \
	Moose \
	Array::Compare \
	Test::Pod \
	File::Path \
	File::Temp \
	File::Spec \
	File::Slurp \
	File::Slurp::Tiny \
	Tree::DAG_Node \
	Sub::Uplevel \
	Test::Exception \
	Test::Warn \
	Net::CIDR \
	Socket::Linux

PERL_LIBS 	:= \
	ExtUtils::Constant \
	Socket \
	IO \
	Scalar::Util \
	IO::Socket::SSL \
	Socket6 \
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
	Mozilla::CA \
	LWP::MediaTypes \
	Encode::Locale \
	HTTP::Date \
	IO::HTML \
	HTTP::Message \
	HTTP::Negotiate \
	File::Listing \
	HTTP::Daemon \
	HTTP::Cookies \
	WWW::RobotRules \
	LWP::UserAgent \
	LWP::Protocol::https \
	IO::Stringy \
	Date::Parse \
	Mail::Mailer \
	MIME::Types \
	MIME::Lite \
	Locale::Maketext::Simple \
	Params::Check \
	Module::Load \
	Module::CoreList \
	Module::Load::Conditional \
	IPC::Cmd \
	Pod::Escapes \
	Class::Data::Inheritable \
	Exception::Class \
	Data::Dumper \
	Algorithm::Diff \
	Text::Diff \
	Test::Differences \
	Test::Most \
	autodie \
	Convert::BinHex \
	MIME::Tools \
	Class::Inspector \
	IO::SessionData \
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
	Test::Inter \
	Date::Manip \
	Time::HiRes \
	Math::BigInt \
	Convert::ASN1 \
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
	XML::SAX::Base \
	XML::NamespaceSupport \
	XML::SAX \
	XML::SAX::Expat \
	XML::Simple \
	IO::Socket::INET6 \
	NetAddr::IP \
	IO::Socket::IP \
	Net::DNS::Resolver::Programmable \
	Geography::Countries \
	IP::Country \
	DBD::SQLite \
	Filesys::Df \
	LMDB_File \
	Data::Validate::IP \
	Unix::Getrusage

NET_SSLeay := \
	Net-SSLeay-1.55

CRYPT_SSLeay := \
	Crypt-SSLeay-0.64

CRYPT_RANDOM := \
	Crypt-OpenSSL-Random-0.06

CRYPT_RSA := \
	Crypt-OpenSSL-RSA-0.27

DBFILE_PERL_LIBS := \
	DB_File

BDB_PERL_LIBS := \
	BerkeleyDB-0.51

MAIL_SPF := \
	Mail-SPF-v2.9.0

SA_PERL_LIBS := \
	Mail-SpamAssassin-$(SA_VERSION)

SWATCH := \
	swatch-3.2.3

TERM_READKEY := \
	TermReadKey-2.30.02
	
INNOTOP := \
	innotop-1.9.1

LDAPAPI := \
	Net-LDAPapi-3.0.3

XML_PARSER := \
	XML-Parser-2.41

NET_SERVER := \
	Net-Server-2.007

PERL_LDAP := \
	perl-ldap-0.53

DBD_PERL_LIBS 	:= \
	DBD-mysql-4.024

ZMQ_CONSTANTS	:= \
	ZMQ-Constants-1.03

ZMQ_LIB	:= \
	ZMQ-LibZMQ3-1.13

PERL_MM_USE_DEFAULT	:= 1
export PERL_MM_USE_DEFAULT

# TARGETS

CLEAN_TARGETS	:=	\
		$(TMPDIR) \
		$(DEST_DIR) \
		$(PERL_TGZ_DEST) \
		$(PERL_SA_RULES) \
		$(PERL_BIN_TGZ_DEST)

all: allclean setup $(DEST_LIB_DIR) $(DEST_BIN_DIR) $(PERL_TGZ_DEST_DIR) build check tarball

build:
	mkdir -p $(TMPDIR)
	@echo "Logging to $(LOGFILE)"

	@for lib in $(CRYPT_SSLeay); do \
		echo -n "Compiling perl lib $$lib ..."; \
		cp $(PERL_ROOT)/$$lib.tar.gz $(TMPDIR); \
		(cd $(TMPDIR); tar xfz $$lib.tar.gz; cd $$lib; \
			patch -g0 -N -p1 < $(PERL_ROOT)/patches/crypt-ssleay-include.patch; \
			$(PERL) -I$(DEST_LIB_DIR) Makefile.PL PREFIX=$(DEST_DIR) LIB=$(DEST_LIB_DIR) LIBS="-L$(ZIMBRA_HOME)/openssl-$(OPENSSL_VERSION)/lib -lssl -lcrypto -lz" LDFLAGS="-L$(ZIMBRA_HOME)/openssl-$(OPENSSL_VERSION)/lib -Wl,-rpath,$(ZIMBRA_HOME)/openssl-$(OPENSSL_VERSION)/lib" INC="-I$(ZIMBRA_HOME)/openssl-$(OPENSSL_VERSION)/include"; \
			LD_RUN_PATH="$(ZIMBRA_HOME)/openssl-$(OPENSSL_VERSION)/lib" $(MAKE); $(MAKE) install;) >>$(LOGFILE) 2>&1; \
		if [ $$? -ne 0 ]; then  echo "FAILED.";  else  echo "done.";  fi \
	done

	@for lib in $(CRYPT_RANDOM); do \
		echo -n "Compiling perl lib $$lib ..."; \
		cp $(PERL_ROOT)/$$lib.tar.gz $(TMPDIR); \
		(cd $(TMPDIR); tar xfz $$lib.tar.gz; cd $$lib; \
			$(PERL) -I$(DEST_LIB_DIR) Makefile.PL PREFIX=$(DEST_DIR) LIB=$(DEST_LIB_DIR) LIBS="-L$(ZIMBRA_HOME)/openssl-$(OPENSSL_VERSION)/lib -Wl,-rpath,$(ZIMBRA_HOME)/openssl-$(OPENSSL_VERSION)/lib -lssl -lcrypto" INC="-I$(ZIMBRA_HOME)/openssl-$(OPENSSL_VERSION)/include"; \
			LD_RUN_PATH="$(ZIMBRA_HOME)/openssl-$(OPENSSL_VERSION)/lib" $(MAKE); $(MAKE) install;) >>$(LOGFILE) 2>&1; \
		if [ $$? -ne 0 ]; then  echo "FAILED.";  else  echo "done.";  fi \
	done

	@for lib in $(CRYPT_RSA); do \
		echo -n "Compiling perl lib $$lib ..."; \
		cp $(PERL_ROOT)/$$lib.tar.gz $(TMPDIR); \
		(cd $(TMPDIR); tar xfz $$lib.tar.gz; cd $$lib; \
			$(PERL) -I$(DEST_LIB_DIR) Makefile.PL PREFIX=$(DEST_DIR) LIB=$(DEST_LIB_DIR) LIBS="-L$(ZIMBRA_HOME)/openssl-$(OPENSSL_VERSION)/lib -lssl -lcrypto" INC="-I$(ZIMBRA_HOME)/openssl-$(OPENSSL_VERSION)/include"; \
			LD_RUN_PATH="$(ZIMBRA_HOME)/openssl-$(OPENSSL_VERSION)/lib" $(MAKE); $(MAKE) install;) >>$(LOGFILE) 2>&1; \
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
	
	@for lib in $(PERL_LDAP); do \
		echo -n "Compiling perl lib $$lib ..."; \
		cp $(PERL_ROOT)/$$lib.tar.gz $(TMPDIR); \
		(cd $(TMPDIR); tar xfz $$lib.tar.gz; cd $$lib; \
		patch -g0 -N -p1 < $(PERL_ROOT)/patches/net-ldap-keepalive.patch; \
		$(PERL) -I$(DEST_LIB_DIR) Makefile.PL PREFIX=$(DEST_DIR) LIB=$(DEST_LIB_DIR); \
		$(MAKE); $(MAKE) install;) >>$(LOGFILE) 2>&1; \
		if [ $$? -ne 0 ]; then  echo "FAILED.";  else  echo "done.";  fi \
	done
	
	@for lib in $(NET_SERVER); do \
		echo -n "Compiling perl lib $$lib ..."; \
		cp $(PERL_ROOT)/$$lib.tar.gz $(TMPDIR); \
		(cd $(TMPDIR); tar xfz $$lib.tar.gz; cd $$lib; \
		$(PERL) -I$(DEST_LIB_DIR) Makefile.PL PREFIX=$(DEST_DIR) LIB=$(DEST_LIB_DIR); \
		$(MAKE); $(MAKE) install;) >>$(LOGFILE) 2>&1; \
		if [ $$? -ne 0 ]; then  echo "FAILED.";  else  echo "done.";  fi \
	done
	
	@for lib in $(XML_PARSER); do \
		echo -n "Compiling perl lib $$lib ..."; \
		cp $(PERL_ROOT)/$$lib.tar.gz $(TMPDIR); \
		(cd $(TMPDIR); tar xfz $$lib.tar.gz; cd $$lib; \
		$(PERL) -I$(DEST_LIB_DIR) Makefile.PL PREFIX=$(DEST_DIR) LIB=$(DEST_LIB_DIR); \
		$(MAKE); $(MAKE) install;) >>$(LOGFILE) 2>&1; \
		if [ $$? -ne 0 ]; then  echo "FAILED.";  else  echo "done.";  fi \
	done

	@for lib in $(ZMQ_CONSTANTS); do \
		echo -n "Compiling perl lib $$lib ..."; \
		cp $(PERL_ROOT)/$$lib.tar.gz $(TMPDIR); \
		(cd $(TMPDIR); tar xfz $$lib.tar.gz; cd $$lib; \
		$(PERL) -I$(DEST_LIB_DIR) Makefile.PL PREFIX=$(DEST_DIR) LIB=$(DEST_LIB_DIR); \
		$(MAKE); $(MAKE) install;) >>$(LOGFILE) 2>&1; \
		if [ $$? -ne 0 ]; then  echo "FAILED.";  else  echo "done.";  fi \
	done

	@for lib in $(ZMQ_LIB); do \
		echo -n "Compiling perl lib $$lib ..."; \
		cp $(PERL_ROOT)/$$lib.tar.gz $(TMPDIR); \
		(cd $(TMPDIR); tar xfz $$lib.tar.gz; cd $$lib; \
		patch -g0 -N -p1 < $(PERL_ROOT)/patches/zmq-make.patch; \
		PKG_CONFIG_PATH=$(ZIMBRA_HOME)/zeromq-$(ZMQ_VERSION)/lib/pkgconfig ZMQ_HOME=$(ZIMBRA_HOME)/zeromq-$(ZMQ_VERSION) $(PERL) -I$(DEST_LIB_DIR) Makefile.PL PREFIX=$(DEST_DIR) LIB=$(DEST_LIB_DIR); \
		$(SED) -i .bak -e 's#^OTHERLDFLAGS = #OTHERLDFLAGS = -Wl,-rpath,$(ZIMBRA_HOME)/zeromq-$(ZMQ_VERSION)/lib#' Makefile; \
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
			--libs="-L$(ZIMBRA_HOME)/mariadb/lib -L/opt/zimbra/openssl/lib -lmysqlclient -lpthread -lz -lm -lrt -lssl -lcrypto -ldl" \
			--mysql_config="$(ZIMBRA_HOME)/mariadb/bin/mysql_config"; \
		$(SED) -i .bak -e 's#-arch x86_64 -arch i386 -arch ppc -bundle -undefined dynamic_lookup -L/usr/local/lib#-arch x86_64 -bundle -undefined dynamic_lookup#' Makefile; \
		$(SED) -i .bak -e 's#-arch x86_64 -arch i386 -arch ppc -L/usr/local/lib#-arch x86_64#' Makefile; \
		$(SED) -i .bak -e 's#-arch x86_64 -arch i386 -arch ppc -g -pipe -fno-common -DPERL_DARWIN -fno-strict-aliasing -I/usr/local/include#-arch x86_64 -g -pipe -fno-common -DPERL_DARWIN -fno-strict-aliasing#' Makefile; \
		$(SED) -i .bak -e 's#-arch x86_64 -arch i386 -bundle -undefined dynamic_lookup -L/usr/local/lib -fstack-protector#-arch x86_64 -bundle -undefined dynamic_lookup -fstack-protector#' Makefile; \
		$(SED) -i .bak -e 's#-arch x86_64 -arch i386 -fstack-protector -L/usr/local/lib#-arch x86_64 -fstack-protector#' Makefile; \
		$(SED) -i .bak -e 's#-arch x86_64 -arch i386 -g -pipe -fno-common -DPERL_DARWIN -fno-strict-aliasing -fstack-protector -I/usr/local/include#-arch x86_64 -g -pipe -fno-common -DPERL_DARWIN -fno-strict-aliasing -fstack-protector#' Makefile; \
		LD_RUN_PATH=/opt/zimbra/lib:/opt/zimbra/mariadb/lib:/opt/zimbra/openssl/lib $(MAKE); $(MAKE) install;) >> $(LOGFILE) 2>&1; \
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
			rm -rf $(DEST_DIR)/opt; \
			rm -rf /opt/zimbra/conf/spamassassin; \
			rm -rf ../../spamassassin; \
			rm -rf ../../sa-rules; \
			mkdir -p /opt/zimbra/conf/spamassassin; \
			cp -pfR rules/* /opt/zimbra/conf/spamassassin; \
			perl -T -I ../../zimbramon/lib ../../zimbramon/bin/sa-update --updatedir=../../sa-rules; \
			mkdir -p ../../spamassassin; \
			cp -pfR rules/* ../../spamassassin; \
			cp -pfR ../../sa-rules/updates_spamassassin_org/* ../../spamassassin; \
			rm -rf /opt/zimbra/conf/spamassassin;) >> $(LOGFILE) 2>&1; \
		if [ $$? -ne 0 ]; then  echo "FAILED.";  else  echo "done.";  fi \
	done

	@for lib in $(SWATCH); do \
		echo -n "Compiling perl lib $$lib ..."; \
		cp $(PERL_ROOT)/$$lib.tar.gz $(TMPDIR); \
		(cd $(TMPDIR); tar xzf $$lib.tar.gz; cd $$lib; \
			$(SED) -i .bak -e 's#/usr/lib/sendmail /usr/sbin/sendmail#/usr/lib/sendmail /usr/sbin/sendmail /opt/zimbra/postfix/sbin/sendmail#' lib/Swatch/Actions.pm; \
			rm -f lib/Swatch/Actions.pm.bak; \
			$(PERL) -I$(DEST_LIB_DIR) Makefile.PL PREFIX=$(DEST_DIR) LIB=$(DEST_LIB_DIR); \
			$(MAKE); $(MAKE) install;) >> $(LOGFILE) 2>&1; \
		if [ $$? -ne 0 ]; then  echo "FAILED.";  else  echo "done.";  fi \
	done

	@for lib in $(TERM_READKEY); do \
		echo -n "Compiling perl lib $$lib ..."; \
		cp $(PERL_ROOT)/$$lib.tar.gz $(TMPDIR); \
		(cd $(TMPDIR); tar xzf $$lib.tar.gz; cd $$lib; \
			$(PERL) -I$(DEST_LIB_DIR) Makefile.PL PREFIX=$(DEST_DIR) LIB=$(DEST_LIB_DIR); \
			$(MAKE); $(MAKE) install;) >> $(LOGFILE) 2>&1; \
		if [ $$? -ne 0 ]; then  echo "FAILED.";  else  echo "done.";  fi \
	done

	@for lib in $(INNOTOP); do \
		echo -n "Compiling perl lib $$lib ..."; \
		cp $(PERL_ROOT)/$$lib.tar.gz $(TMPDIR); \
		(cd $(TMPDIR); tar xzf $$lib.tar.gz; cd $$lib; \
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
	tar cfz $(PERL_BIN_TGZ_DEST) sa-compile sa-learn sa-update spamassassin spamc spamd innotop); \
	if [ $$? -ne 0 ]; then  echo "FAILED.";  else  echo "done.";  fi
	@echo -n "Creating $(PERL_SA_RULES)..."; \
	(tar cfz $(PERL_SA_RULES) spamassassin); \
	if [ $$? -ne 0 ]; then  echo "FAILED.";  else  echo "done.";  fi
	rm -rf spamassassin;
	rm -rf sa-rules;
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
