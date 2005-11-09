# 
# ***** BEGIN LICENSE BLOCK *****
# Version: MPL 1.1
# 
# The contents of this file are subject to the Mozilla Public License
# Version 1.1 ("License"); you may not use this file except in
# compliance with the License. You may obtain a copy of the License at
# http://www.zimbra.com/license
# 
# Software distributed under the License is distributed on an "AS IS"
# basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
# the License for the specific language governing rights and limitations
# under the License.
# 
# The Original Code is: Zimbra Collaboration Suite Server.
# 
# The Initial Developer of the Original Code is Zimbra, Inc.
# Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
# All Rights Reserved.
# 
# Contributor(s):
# 
# ***** END LICENSE BLOCK *****
# 

use strict;
use warnings;

$ENV{PATH}="/bin:/usr/bin:/sbin:/usr/sbin:/opt/zimbra/bin";

package ZmSetupWindowController;

use CamelBones qw(:All);

use ZmSetupLogWindowController;

my $newinstall = 0;

my %options = ();

my %config = ();

my @packageList = (
				   "zimbra-core",
				   "zimbra-ldap",
				   "zimbra-store",
				   "zimbra-mta",
				   "zimbra-snmp",
				   "zimbra-logger",
				   "zimbra-apache",
				   "zimbra-spell",
				   );

my %installedPackages = ();
my %enabledPackages = ();

my $zimbraHome = "/opt/zimbra";

my $ldapConfigured = 0;
my $ldapRunning = 0;
my $sqlConfigured = 0;
my $sqlRunning = 0;
my $loggerSqlConfigured = 0;
my $loggerSqlRunning = 0;

my $ldapPassChanged = 0;
my $ldapPasswordsMatch = 0;
my $ldapPasswordString;

my $platform;

my $logfile = "/tmp/zmsetup.log.$$";

class ZmSetupWindowController {
    'super' => 'NSObject',
    'properties' => [
		'windowController', 
		'ldapHostLabel', 
		'ldapPortLabel', 
		'ldapPasswordLabel',
		'ldapPasswordConfirmLabel',
		'ldapPasswordConfirmTitle',
		'applyButton',
		'generalTab',
		'ldapTab',
		'loggerTab',
		'mtaTab',
		'snmpTab',
		'spellTab',
		'storeTab',
		'ldapEnabled',
		'createDomainLabel',
		'doCreateDomain',
		'doCreateAdminUser',
		'createAdminUserLabel',
		'createAdminUserPasswordLabel',
		'createAdminUserPasswordText',
		'storeEnabled',
		'webServerModeText',
		'webServerModeSelector',
		'popPortText',
		'popPortLabel',
		'popSslPortText',
		'popSslPortLabel',
		'imapPortText',
		'imapPortLabel',
		'imapSslPortText',
		'imapSslPortLabel',
		'smtpHostText',
		'smtpHostLabel',
		'useSpellServer',
		'spellServerText',
		'spellServerLabel',
		'mtaEnabled',
		'saEnabled',
		'clamEnabled',
		'clamAddressLabel',
		'clamAddressText',
		'snmpEnabled',
		'snmpTrapsEnabled',
		'snmpTrapHostText',
		'snmpTrapHostLabel',
		'snmpSmtpEnabled',
		'snmpSmtpSourceText',
		'snmpSmtpSourceLabel',
		'snmpSmtpDestText',
		'snmpSmtpDestLabel',
		'loggerEnabled',
		'spellEnabled',
		'setupTabView',
		'logicalHostnameLabel',
		],
};

sub init : Selector(init) ReturnType(@) {
    my ($self) = @_;
    
    $self->setWindowController(
            NSWindowController->alloc()->initWithWindowNibName_owner("ZmSetup", $self));
    $self->windowController()->window();
	
	my $id = `/usr/bin/id -u `;
	chomp $id;
	if ($id) {
		NSLog ("Must run as root!");
		exit 0;
	}
	$self->verifyConfig();
	$self->setupTabView()->selectFirstTabViewItem(1);

	open LOGFILE, ">$logfile" or die "Can't open $logfile: $!\n";
	
	my $ol = select (LOGFILE);
	$| = 1;
	select ($ol);

	$platform = "MACOSX";

	logMessage("Running on $platform as $id\n");
	logMessage("Operations logged to $logfile\n");
	
	getInstalledPackages();
	
	$self->setInstalledDependencies();
	
	setDefaults();

	$self->populateControlValues();
		
	getSystemStatus();
	
	if (!$ldapRunning && $ldapConfigured) {
		startLdap();
	}
		
    return $self;
}

sub logMessage {
	my ($msg) = @_;
	print LOGFILE "$msg";
	chomp $msg;
	NSLog($msg);
}

sub populateControlValues {
	my $self = shift;
	$self->logicalHostnameLabel()->setStringValue($config{HOSTNAME});
	if ($config{LDAPHOST} ne "") {
		$self->ldapHostLabel()->setStringValue($config{LDAPHOST});
	}
	if ($config{LDAPPORT} ne "") {
		$self->ldapPortLabel()->setStringValue(" ".$config{LDAPPORT}." ");
	}
	if ($config{LDAPPASS} ne "") {
		$self->ldapPasswordLabel()->setStringValue("*******");
		$self->ldapPasswordConfirmLabel()->setStringValue("*******");
		$self->ldapPasswordConfirmLabel()->setEditable(0);
		$self->ldapPasswordConfirmLabel()->setSelectable(0);
		$self->ldapPasswordConfirmTitle()->setHidden(0);
		$self->ldapPasswordConfirmLabel()->setHidden(0);
		$ldapPasswordsMatch = 1;
	}
	
	if ($config{DOCREATEDOMAIN} eq "yes") {
		$self->doCreateDomain()->setState(1);
		$self->doEnableCreateDomain();
	}
	if ($config{CREATEDOMAIN} ne "") {
		$self->createDomainLabel()->setStringValue($config{CREATEDOMAIN});
	}
	
	if ($config{DOCREATEADMIN} eq "yes") {
		$self->doCreateAdminUser()->setState(1);
		$self->doEnableCreateAdminUser();
	}
	if ($config{CREATEADMIN} ne "") {
		$self->createAdminUserLabel()->setStringValue($config{CREATEADMIN});
	}
	
	if ($config{CREATEADMINPASS} ne "") {
		$self->adminUserPasswordLabel()->setStringValue("*******");
	}
	
	$self->webServerModeSelector()->selectItemWithTitle(uc($config{MODE}));
	$self->popPortLabel()->setStringValue($config{POPPORT});
	$self->popSslPortLabel()->setStringValue($config{POPSSLPORT});
	$self->imapPortLabel()->setStringValue($config{IMAPPORT});
	$self->imapSslPortLabel()->setStringValue($config{IMAPSSLPORT});
	$self->smtpHostLabel()->setStringValue($config{SMTPHOST});
	if ($config{SPELLURL} ne "") {
		$self->useSpellServer()->setState(1);
		$self->spellServerLabel()->setStringValue($config{SPELLURL});
		$self->doEnableUseSpell();
	}
	
	if ($config{RUNSA} eq "yes") {
		$self->saEnabled()->setState(1);
	}
	
	if ($config{RUNAV} eq "yes") {
		$self->doEnableClam();
		$self->clamEnabled()->setState(1);
	}
	
	if ($config{AVUSER} ne "") {
		$self->clamAddressLabel()->setStringValue($config{AVUSER});
	}
	
	if ($config{SNMPNOTIFY}) {
		$self->doEnableSnmp();
		$self->snmpTrapsEnabled()->setState(1);
		$self->doEnableSnmpTraps();
		$self->snmpTrapHostLabel()->setStringValue($config{SNMPTRAPHOST});
	}
	if ($config{SMTPNOTIFY}) {
		$self->doEnableSnmpSmtp();
		$self->snmpSmtpEnabled()->setState(1);
		$self->snmpSmtpSourceLabel()->setStringValue($config{SMTPSOURCE});
		$self->snmpSmtpDestLabel()->setStringValue($config{SMTPDEST});
	}
}

sub setInstalledDependencies {
	my $self = shift;
	if (defined ($installedPackages{"zimbra-ldap"})) {
		$self->ldapEnabled()->setEnabled(1);
		$self->ldapEnabled()->setState(1);
		$self->doEnableLdap();
	} else {
		$self->ldapEnabled()->setEnabled(0);
		$self->doDisableLdap();
	}
	
	if (defined ($installedPackages{"zimbra-store"})) {
		$self->storeEnabled()->setEnabled(1);
		$self->storeEnabled()->setState(1);
		$self->doEnableStore();
	} else {
		$self->storeEnabled()->setEnabled(0);
		$self->doDisableStore();
	}
	
	if (defined ($installedPackages{"zimbra-mta"})) {
		$self->mtaEnabled()->setEnabled(1);
		$self->mtaEnabled()->setState(1);
		$self->doEnableMta();
	} else {
		$self->mtaEnabled()->setEnabled(0);
		$self->mtaEnabled()->setState(0);
		$self->doDisableMta();
	}
	
	if (defined ($installedPackages{"zimbra-snmp"})) {
		$self->snmpEnabled()->setEnabled(1);
		$self->snmpEnabled()->setState(1);
		$self->doEnableSnmp();
	} else {
		$self->snmpEnabled()->setEnabled(0);
		$self->snmpEnabled()->setState(0);
		$self->doDisableSnmp();
	}
	
	if (defined ($installedPackages{"zimbra-logger"})) {
		$self->loggerEnabled()->setEnabled(1);
		$self->loggerEnabled()->setState(1);
	} else {
		$self->loggerEnabled()->setEnabled(0);
		$self->loggerEnabled()->setState(0);
	}
	
	if (defined ($installedPackages{"zimbra-spell"})) {
		$self->spellEnabled()->setEnabled(1);
		$self->spellEnabled()->setState(1);
	} else {
		$self->spellEnabled()->setEnabled(0);
		$self->spellEnabled()->setState(0);
	}
	
}

sub verifyConfig {
	my $self = shift;
	if (!$ldapPasswordsMatch) {
		$self->applyButton()->setEnabled(0);
		return;
	}
	
	if ($config{LDAPHOST} eq "") {
		$self->applyButton()->setEnabled(0);
		return;
	}
	
	if ($config{LDAPPORT} eq "") {
		$self->applyButton()->setEnabled(0);
		return;
	}
	if ($config{CREATEADMINPASS} eq "") {
		$self->applyButton()->setEnabled(0);
		return;
	}
		
	$self->applyButton()->setEnabled(1);
}

sub editLogicalHostnameAction : Selector(editLogicalHostnameAction:) IBAction {
	my ($self, $sender) = @_;
	my $old = $config{HOSTNAME};
	$config{HOSTNAME} = $sender->stringValue();
	if ($config{SMTPHOST} eq $old) {
		$config{SMTPHOST} = $config{HOSTNAME};
	}
	if ($config{SNMPTRAPHOST} eq $old) {
		$config{SNMPTRAPHOST} = $config{HOSTNAME};
	}
	if ($config{LDAPHOST} eq $old) {
		$config{LDAPHOST} = $config{HOSTNAME};
	}
	if ($config{CREATEDOMAIN} eq $old) {
		$config{CREATEDOMAIN} = $config{HOSTNAME};
		my ($u,$d) = split ('@', $config{CREATEADMIN});
		$config{CREATEADMIN} = $u.'@'.$config{CREATEDOMAIN};
	}
	my ($suser,$sdomain) = split ('@', $config{SMTPSOURCE}, 2);
	if ($sdomain eq $old) {
		$config{SMTPSOURCE} = $suser.'@'.$config{CREATEDOMAIN};
	}
	($suser,$sdomain) = split ('@', $config{SMTPDEST}, 2);
	if ($sdomain eq $old) {
		$config{SMTPDEST} = $suser.'@'.$config{CREATEDOMAIN};
	}
	if ($config{SPELLURL} eq "http://${old}:7780/aspell.php") {
		$config{SPELLURL} = "http://$config{HOSTNAME}:7780/aspell.php";
	}
	$self->verifyConfig();
}

sub editLdapHostAction : Selector(editLdapHostAction:) IBAction {
	my ($self, $sender) = @_;
	$config{LDAPHOST} = $sender->stringValue();
	$self->verifyConfig();
}

sub editLdapPortAction : Selector(editLdapPortAction:) IBAction {
	my ($self, $sender) = @_;
	my $i = $sender->stringValue();
	if (int($i) eq $i) {
		$config{LDAPPORT} = $sender->stringValue();
	} else {
		$sender->setStringValue("");
	}
	$self->verifyConfig();
}

sub editLdapPasswordAction : Selector(editLdapPasswordAction:) IBAction {
	my ($self, $sender) = @_;
	$ldapPasswordsMatch = 0;
	if (length($sender->stringValue()) >= 6) {
		$ldapPasswordString = $sender->stringValue();
		$sender->setStringValue("*******");
		$self->ldapPasswordConfirmTitle()->setHidden(0);
		$self->ldapPasswordConfirmLabel()->setHidden(0);
		$self->ldapPasswordConfirmLabel()->setStringValue("");
		$self->ldapPasswordConfirmLabel()->setEditable(1);
		$self->ldapPasswordConfirmLabel()->setSelectable(1);
	} else {
		$sender->setStringValue("");
		$self->ldapPasswordConfirmTitle()->setHidden(1);
		$self->ldapPasswordConfirmLabel()->setHidden(1);
		$self->ldapPasswordConfirmLabel()->setEditable(0);
		$self->ldapPasswordConfirmLabel()->setSelectable(0);
	}
	$self->verifyConfig();
}

sub editLdapPasswordConfirmAction : Selector(editLdapPasswordConfirmAction:) IBAction {
	my ($self, $sender) = @_;
	if ($sender->stringValue eq $ldapPasswordString) {
		$config{LDAPPASS} = $sender->stringValue();
		$sender->setStringValue("*******");
		$sender->setEditable(0);
		$sender->setSelectable(0);
		$ldapPasswordsMatch = 1;
	} else {
		$sender->setStringValue("Incorrect");
	}
	$self->verifyConfig();
}

sub applyAction : Selector(applyAction:) IBAction {
	my ($self, $sender) = @_;
	applyConfig();
}

sub enableLdapAction : Selector(enableLdapAction:) IBAction {
	my ($self, $sender) = @_;
	if ($sender->state()) {
		$self->doEnableLdap();
	} else {
		$self->doDisableLdap();
	}
}

sub doEnableLdap {
	my $self = shift;
	$enabledPackages{"zimbra-ldap"} = "Enabled";
	$self->doCreateDomain()->setEnabled(1);
}

sub doDisableLdap {
	my $self = shift;
	$enabledPackages{"zimbra-ldap"} = "Disabled";
	$self->doCreateDomain()->setEnabled(0);
	$self->doDisableCreateDomain();
}

sub doCreateDomainAction : Selector(doCreateDomainAction:) IBAction {
	my ($self, $sender) = @_;
	if ($sender->state()) {
		$self->doEnableCreateDomain();
	} else {
		$self->doDisableCreateDomain();
	}
}

sub doEnableCreateDomain {
	my $self = shift;
	$self->createDomainLabel()->setEnabled(1);
	$self->doCreateAdminUser()->setEnabled(1);
}

sub doDisableCreateDomain {
	my $self = shift;
	
	$self->doCreateDomain()->setState(0);
	$self->createDomainLabel()->setEnabled(0);
	$self->doCreateAdminUser()->setEnabled(0);
	$self->doDisableCreateAdminUser();
}

sub editCreateDomainAction : Selector(editCreateDomainAction:) IBAction {
	my ($self, $sender) = @_;
	$config{CREATEDOMAIN} = $sender->stringValue();
	my $au = $self->createAdminUserLabel()->stringValue();
	my @parts = split ('@', $au);
	$au = $parts[0].'@'.$config{CREATEDOMAIN};
	$self->createAdminUserLabel()->setStringValue($au);
	$config{CREATEADMINUSER} = $au;
}

sub doCreateAdminUserAction : Selector(doCreateAdminUserAction:) IBAction {
	my ($self, $sender) = @_;
	if ($sender->state()) {
		$self->doEnableCreateAdminUser();
	} else {
		$self->doDisableCreateAdminUser();
	}
}

sub doDisableCreateAdminUser {
	my $self = shift;
	$self->doCreateAdminUser()->setState(0);
	$self->createAdminUserLabel()->setEnabled(0);
	$self->createAdminUserPasswordLabel()->setEnabled(0);
	$self->createAdminUserPasswordText()->setEnabled(0);
}

sub doEnableCreateAdminUser {
	my $self = shift;
	$self->createAdminUserLabel()->setEnabled(1);
	$self->createAdminUserPasswordLabel()->setEnabled(1);
	$self->createAdminUserPasswordText()->setEnabled(1);
}

sub editAdminUserAction : Selector(editAdminUserAction:) IBAction {
	my ($self, $sender) = @_;
	$config{CREATEADMIN} = $sender->stringValue();
	$self->verifyConfig();
}

sub editAdminUserPasswordAction : Selector(editAdminUserPasswordAction:) IBAction {
	my ($self, $sender) = @_;
	my $p = $sender->stringValue();
	if (length($p) >= 6) {
		$config{CREATEADMINPASS} = $sender->stringValue()
	} else {
		$sender->setStringValue($config{CREATEADMINPASS});
	}
	$self->verifyConfig();
}


sub enableStoreAction : Selector(enableStoreAction:) IBAction {
	my ($self, $sender) = @_;
	if ($sender->state()) {
		$self->doEnableStore();
	} else {
		$self->doDisableStore();
	}
}

sub doEnableStore {
	my $self = shift;
	$enabledPackages{"zimbra-store"} = "Enabled";
	$self->webServerModeText()->setEnabled(1);
	$self->webServerModeSelector()->setEnabled(1);
	$self->popPortLabel()->setEnabled(1);
	$self->popPortText()->setEnabled(1);
	$self->popSslPortLabel()->setEnabled(1);
	$self->popSslPortText()->setEnabled(1);
	$self->imapPortLabel()->setEnabled(1);
	$self->imapPortText()->setEnabled(1);
	$self->imapSslPortLabel()->setEnabled(1);
	$self->imapSslPortText()->setEnabled(1);
	$self->smtpHostLabel()->setEnabled(1);
	$self->smtpHostText()->setEnabled(1);
	$self->useSpellServer()->setEnabled(1);
}

sub doDisableStore {
	my $self = shift;
	$enabledPackages{"zimbra-store"} = "Disabled";
	$self->webServerModeText()->setEnabled(0);
	$self->webServerModeSelector()->setEnabled(0);
	$self->popPortLabel()->setEnabled(0);
	$self->popPortText()->setEnabled(0);
	$self->popSslPortLabel()->setEnabled(0);
	$self->popSslPortText()->setEnabled(0);
	$self->imapPortLabel()->setEnabled(0);
	$self->imapPortText()->setEnabled(0);
	$self->imapSslPortLabel()->setEnabled(0);
	$self->imapSslPortText()->setEnabled(0);
	$self->smtpHostLabel()->setEnabled(0);
	$self->smtpHostText()->setEnabled(0);
	$self->useSpellServer()->setEnabled(0);
	$self->useSpellServer()->setState(0);
#$self->doDisableSpell();
}

sub enableUseSpellAction : Selector(enableUseSpellAction:) IBAction {
	my ($self, $sender) = @_;
	if ($sender->state()) {
		$self->doEnableUseSpell();
	} else {
		$self->doDisableUseSpell();
	}
}

sub doEnableUseSpell {
	my $self = shift;
	$self->spellServerLabel()->setEnabled(1);
	$self->spellServerText()->setEnabled(1);
	$config{USESPELL} = "yes";
}

sub doDisableUseSpell {
	my $self = shift;
	$self->spellServerLabel()->setEnabled(0);
	$self->spellServerText()->setEnabled(0);
	$config{USESPELL} = "no";
}

sub selectWebServerModeAction : Selector(selectWebServerModeAction:) IBAction {
	my ($self, $sender) = @_;
	$config{MODE} = lc($sender->titleOfSelectedItem());
}

sub editPopPortAction : Selector(editPopPortAction:) IBAction {
	my ($self, $sender) = @_;
	my $i = $sender->stringValue();
	if (int($i) eq $i) {
		$config{POPPORT} = $sender->stringValue();
	} else {
		$sender->setStringValue("");
	}
	$self->verifyConfig();
}

sub editPopSslPortAction : Selector(editPopSslPortAction:) IBAction {
	my ($self, $sender) = @_;
	my $i = $sender->stringValue();
	if (int($i) eq $i) {
		$config{POPSSLPORT} = $sender->stringValue();
	} else {
		$sender->setStringValue("");
	}
	$self->verifyConfig();
}

sub editImapPortAction : Selector(editImapPortAction:) IBAction {
	my ($self, $sender) = @_;
	my $i = $sender->stringValue();
	if (int($i) eq $i) {
		$config{IMAPPORT} = $sender->stringValue();
	} else {
		$sender->setStringValue("");
	}
	$self->verifyConfig();
}

sub editImapSslPortAction : Selector(editImapSslPortAction:) IBAction {
	my ($self, $sender) = @_;
	my $i = $sender->stringValue();
	if (int($i) eq $i) {
		$config{IMAPSSLPORT} = $sender->stringValue();
	} else {
		$sender->setStringValue("");
	}
	$self->verifyConfig();
}

sub editSmtpHostAction : Selector(editSmtpHostAction:) IBAction {
	my ($self, $sender) = @_;
	$config{SMTPHOST} = $sender->stringValue();
}

sub editSpellServerAction : Selector(editSpellServerAction:) IBAction {
	my ($self, $sender) = @_;
	my $s = $sender->stringValue();
	if ($s =~ m|http://.+/aspell.php|) {
		$config{SPELLURL} = $s;
	} else {
		$config{SPELLURL} = "http://${s}:7780/aspell.php";
		$sender->setStringValue($config{SPELLURL});
	}
}

sub enableMtaAction : Selector(enableMtaAction:) IBAction {
	my ($self, $sender) = @_;
	if ($sender->state()) {
		$self->doEnableMta();
	} else {
		$self->doDisableMta();
	}
}

sub doEnableMta {
	my $self = shift;
	$enabledPackages{"zimbra-mta"} = "Enabled";
	$self->saEnabled()->setEnabled(1);
	$self->clamEnabled()->setEnabled(1);
}

sub doDisableMta {
	my $self = shift;
	$enabledPackages{"zimbra-mta"} = "Disabled";
	$self->doDisableSa();
	$self->saEnabled()->setState(0);
	$self->saEnabled()->setEnabled(0);
	$self->doDisableClam();
	$self->clamEnabled()->setState(0);
	$self->clamEnabled()->setEnabled(0);
}

sub enableSaAction : Selector(enableSaAction:) IBAction {
	my ($self, $sender) = @_;
	if ($sender->state()) {
		$self->doEnableSa();
	} else {
		$self->doDisableSa();
	}
}

sub doEnableSa {
	my $self = shift;
}

sub doDisableSa {
	my $self = shift;
}

sub enableClamAction : Selector(enableClamAction:) IBAction {
	my ($self, $sender) = @_;
	if ($sender->state()) {
		$self->doEnableClam();
	} else {
		$self->doDisableClam();
	}
}

sub doEnableClam {
	my $self = shift;
	$self->clamAddressLabel()->setEnabled(1);
	$self->clamAddressText()->setEnabled(1);
}

sub doDisableClam {
	my $self = shift;
	$self->clamAddressLabel()->setEnabled(0);
	$self->clamAddressText()->setEnabled(0);
}

sub editClamAddressAction : Selector(editClamAddressAction:) IBAction {
	my ($self, $sender) = @_;
	$config{AVUSER} = $sender->stringValue();
}

sub enableSnmpAction : Selector(enableSnmpAction:) IBAction {
	my ($self, $sender) = @_;
	if ($sender->state()) {
		$self->doEnableSnmp();
	} else {
		$self->doDisableSnmp();
	}
}

sub doEnableSnmp {
	my $self = shift;
	$enabledPackages{"zimbra-snmp"} = "Enabled";
	$self->snmpTrapsEnabled()->setEnabled(1);
	$self->snmpSmtpEnabled()->setEnabled(1);
}

sub doDisableSnmp {
	my $self = shift;
	$enabledPackages{"zimbra-snmp"} = "Disabled";
	$self->snmpTrapsEnabled()->setEnabled(0);
	$self->snmpTrapsEnabled()->setState(0);
	$self->doDisableSnmpTraps();
	$self->snmpSmtpEnabled()->setEnabled(0);
	$self->snmpSmtpEnabled()->setState(0);
	$self->doDisableSnmpSmtp();
}

sub enableSnmpTrapsAction : Selector(enableSnmpTrapsAction:) IBAction {
	my ($self, $sender) = @_;
	if ($sender->state()) {
		$self->doEnableSnmpTraps();
	} else {
		$self->doDisableSnmpTraps();
	}
}

sub doEnableSnmpTraps {
	my $self = shift;
	$self->snmpTrapHostText()->setEnabled(1);
	$self->snmpTrapHostLabel()->setEnabled(1);
	$config{SNMPNOTIFY} = 1;
}

sub doDisableSnmpTraps {
	my $self = shift;
	$self->snmpTrapHostText()->setEnabled(0);
	$self->snmpTrapHostLabel()->setEnabled(0);
	$config{SNMPNOTIFY} = 0;
}

sub editSnmpTrapHostAction : Selector(editSnmpTrapHostAction:) IBAction {
	my ($self, $sender) = @_;
	$config{SNMPTRAPHOST} = $sender->stringValue();
}

sub enableSnmpSmtpAction : Selector(enableSnmpSmtpAction:) IBAction {
	my ($self, $sender) = @_;
	if ($sender->state()) {
		$self->doEnableSnmpSmtp();
	} else {
		$self->doDisableSnmpSmtp();
	}
}

sub doEnableSnmpSmtp {
	my $self = shift;
	$self->snmpSmtpSourceText()->setEnabled(1);
	$self->snmpSmtpSourceLabel()->setEnabled(1);
	$self->snmpSmtpDestText()->setEnabled(1);
	$self->snmpSmtpDestLabel()->setEnabled(1);
	$config{SMTPNOTIFY} = 1;
}

sub doDisableSnmpSmtp {
	my $self = shift;
	$self->snmpSmtpSourceText()->setEnabled(0);
	$self->snmpSmtpSourceLabel()->setEnabled(0);
	$self->snmpSmtpDestText()->setEnabled(0);
	$self->snmpSmtpDestLabel()->setEnabled(0);
	$config{SMTPNOTIFY} = 0;
}

sub editSnmpSmtpSourceAction : Selector(editSnmpSmtpSourceAction:) IBAction {
	my ($self, $sender) = @_;
	$config{SMTPSOURCE} = $sender->stringValue();
}

sub editSnmpSmtpDestAction : Selector(editSnmpSmtpDestAction:) IBAction {
	my ($self, $sender) = @_;
	$config{SMTPDEST} = $sender->stringValue();
}

sub enableLoggerAction : Selector(enableLoggerAction:) IBAction {
	my ($self, $sender) = @_;
	if ($sender->state()) {
		$enabledPackages{"zimbra-logger"} = "Enabled";
	} else {
		$enabledPackages{"zimbra-logger"} = "Disabled";
	}
}

sub enableSpellAction : Selector(enableSpellAction:) IBAction {
	my ($self, $sender) = @_;
	if ($sender->state()) {
		$enabledPackages{"zimbra-spell"} = "Enabled";
	} else {
		$enabledPackages{"zimbra-spell"} = "Disabled";
	}
}

####
####

sub saveConfig {
	my $fname = "/opt/zimbra/config.$$";
	$fname = askNonBlank ("Save config in file:", $fname);
	
	if (open CONF, ">$fname") {
		logMessage("Saving config in $fname...");
		foreach (sort keys %config) {
# Don't write passwords
			if (/PASS/) {next;} 
			print CONF "$_=$config{$_}\n";
		}
		print CONF "INSTALL_PACKAGES=\"";
		foreach (sort keys %installedPackages) {
			print CONF "$_ ";
		}
		print CONF "\"\n";
		close CONF;
		logMessage("Done\n");
	} else {
		logMessage("Can't open $fname: $!\n");
	}
}

sub loadConfig {
	my $filename = shift;
	open (CONF, $filename) or die "Can't open $filename: $!";
	my @lines = <CONF>;
	close CONF;
	foreach (@lines) {
		chomp;
		my ($k, $v) = split ('=', $_, 2);
		$config{$k} = $v;
	}
	
	$config{ALLOWSELFSIGNED} = "true";
}

sub getInstalledPackages {
	
	logMessage("Checking for installed packages\n");
	foreach my $p (@packageList) {
		if (isInstalled($p)) {
			$installedPackages{$p} = $p;
			$enabledPackages{$p} = "Enabled";
		}	
	}
}

sub isInstalled {
	my $pkg = shift;
	
	logMessage("Checking for $pkg...\n");
	my $pkgQuery;
	
	my $good = 1;
	if ($platform eq "DEBIAN3.1") {
		$pkgQuery = "dpkg -s $pkg | egrep '^Status: ' | grep 'not-installed'";
	} elsif ($platform eq "MACOSX") {
		$pkgQuery = "test -d /Library/Receipts/${pkg}*";
		$good = 0;
	} else {
		$pkgQuery = "rpm -q $pkg";
		$good = 0;
	}
	
	my $rc = 0xffff & system ("$pkgQuery >> $logfile.cmd 2>&1");
	$rc >>= 8;
	return ($rc == $good);
	
}

sub genRandomPass {
	open RP, "/opt/zimbra/bin/zmjava com.zimbra.cs.util.RandomPassword 8 10|" or
	die "Can't generate random password: $!\n";
	my $rp = <RP>;
	close RP;
	chomp $rp;
	return $rp;
}

sub getSystemStatus {
	
	if (isEnabled("zimbra-ldap")) {
		if (-f "$zimbraHome/openldap-data/mail.bdb") {
			$ldapConfigured = 1;
			$ldapRunning = 0xffff & system("/opt/zimbra/bin/ldap status > /dev/null 2>&1");
			$ldapRunning = ($ldapRunning)?0:1;
		} else {
			$config{DOCREATEDOMAIN} = "yes";
			$config{DOCREATEADMIN} = "yes";
		}
	}
	
	if (isEnabled("zimbra-store")) {
		if (-d "$zimbraHome/db/data") {
			$sqlConfigured = 1;
			$sqlRunning = 0xffff & system("/opt/zimbra/bin/mysqladmin status > /dev/null 2>&1");
			$sqlRunning = ($sqlRunning)?0:1;
		}
	}
	
	if (isEnabled("zimbra-logger")) {
		if (-d "$zimbraHome/logger/db/data") {
			$loggerSqlConfigured = 1;
			$loggerSqlRunning = 0xffff & 
				system("/opt/zimbra/bin/logmysqladmin status > /dev/null 2>&1");
			$loggerSqlRunning = ($loggerSqlRunning)?0:1;
		}
	}
	
	if (isEnabled("zimbra-mta")) {
		if ($config{SMTPHOST} eq "") {
			$config{SMTPHOST} = $config{HOSTNAME};
		}
	}
}

sub setDefaults {
	logMessage("Setting defaults...");
	$config{EXPANDMENU} = "no";
	$config{REMOVE} = "no";
	$config{UPGRADE} = "yes";
	$config{LDAPPORT} = 389;
	$config{USESPELL} = "no";
	$config{SPELLURL} = "";
	
	$config{IMAPPORT} = 143;
	$config{IMAPSSLPORT} = 993;
	$config{POPPORT} = 110;
	$config{POPSSLPORT} = 995;
	
	if ($platform eq "MACOSX") {
		setLocalConfig ("zimbra_java_home", "/usr");
		$config{HOSTNAME} = `/bin/hostname`;
	} else {
		$config{HOSTNAME} = `/bin/hostname --fqdn`;
	}
	chomp $config{HOSTNAME};
	
	$config{SMTPHOST} = "";
	$config{SNMPTRAPHOST} = $config{HOSTNAME};
	$config{DOCREATEDOMAIN} = "no";
	$config{CREATEDOMAIN} = $config{HOSTNAME};
	$config{DOCREATEADMIN} = "no";
	if (isEnabled("zimbra-ldap")) {
		$config{DOCREATEDOMAIN} = "yes";
		$config{DOCREATEADMIN} = "yes";
		$config{LDAPPASS} = genRandomPass();
		$config{LDAPHOST} = $config{HOSTNAME};
	}
	$config{CREATEADMIN} = "admin\@$config{CREATEDOMAIN}";
	
	if (isEnabled("zimbra-mta")) {
		$config{SMTPHOST} = $config{HOSTNAME};
		$config{RUNAV} = "yes";
		$config{RUNSA} = "yes";
	}
	
	if (isEnabled("zimbra-spell")) {
		$config{SPELLURL} = "http://$config{HOSTNAME}:7780/aspell.php";
	}
	
	$config{SMTPSOURCE} = $config{CREATEADMIN};
	$config{SMTPDEST} = $config{CREATEADMIN};
	$config{AVUSER} = $config{CREATEADMIN};
	$config{SNMPNOTIFY} = "yes";
	$config{SMTPNOTIFY} = "yes";
	$config{STARTSERVERS} = "yes";
	
	$config{MODE} = "http";
	
	$config{CREATEADMINPASS} = "";
	
	if ( -f "/opt/zimbra/.newinstall") {
		$newinstall = 1;
		my $t = time()+(60*60*24*60);
		my @d = localtime($t);
		$config{EXPIRY} = sprintf ("%04d%02d%02d",$d[5]+1900,$d[4]+1,$d[3]);
	} else {
		$config{DOCREATEDOMAIN} = "no";
		$config{DOCREATEADMIN} = "no";
		setDefaultsFromLocalConfig();
	}
	logMessage("Done\n");
}


sub setDefaultsFromLocalConfig {
	$config{HOSTNAME} = getLocalConfig ("zimbra_server_hostname");
	$config{LDAPPORT} = getLocalConfig ("ldap_port");
	$config{LDAPHOST} = getLocalConfig ("ldap_host");
	$config{LDAPPASS} = getLocalConfig ("ldap_root_password");
	$config{SQLROOTPASS} = getLocalConfig ("mysql_root_password");
	$config{LOGSQLROOTPASS} = getLocalConfig ("mysql_logger_root_password");
	$config{ZIMBRASQLPASS} = getLocalConfig ("zimbra_mysql_password");
	$config{ZIMBRALOGSQLPASS} = getLocalConfig ("zimbra_logger_mysql_password");
}

sub isEnabled {
	my $package = shift;
	return ($enabledPackages{$package} eq "Enabled");
}

sub verifyLdap {
	# My laptop can't always find itself...
	my $H = $config{LDAPHOST};
	if (($config{LDAPHOST} eq $config{HOSTNAME}) && !$ldapConfigured) {
		return 0;
	}
	if ($config{LDAPHOST} eq $config{HOSTNAME}) {
		$H = "localhost";
	}
	logMessage("Checking ldap on ${H}:$config{LDAPPORT}...");

	my $ldapsearch = "$zimbraHome/bin/ldapsearch";
	my $args = "-x -h ${H} -p $config{LDAPPORT} ".
		"-D 'uid=zimbra,cn=admins,cn=zimbra' -w $config{LDAPPASS}";

	my $rc = 0xffff & system ("$ldapsearch $args > /tmp/zmsetup.ldap.out 2>&1");

	if ($rc) { logMessage("FAILED\n"); } 
	else {logMessage("Success\n");}
	return $rc;

}

sub runAsZimbra {
	my $cmd = shift;
	if ($cmd =~ /init/) {
		# Suppress passwords in log file
		my $c = (split ' ', $cmd)[0];
		logMessage("*** Running as zimbra user: $c\n");
	} else {
		logMessage("*** Running as zimbra user: $cmd\n");
	}
	my $rc;
	$rc = 0xffff & system("/usr/bin/su - zimbra -c \"$cmd\" >> $logfile.cmd 2>&1");
	return $rc;
}

sub getLocalConfig {
	my $key = shift;
	logMessage("Getting local config $key\n");
	my $val = `/opt/zimbra/bin/zmlocalconfig -s -m nokey ${key}`;
	chomp $val;
	return $val;
}

sub setLocalConfig {
	my $key = shift;
	my $val = shift;
	logMessage("Setting local config $key to $val\n");
	runAsZimbra("/opt/zimbra/bin/zmlocalconfig -f -e ${key}=${val}");
}

sub applyConfig {

	if ( -f "/opt/zimbra/.newinstall") {
		unlink "/opt/zimbra/.newinstall";
	}
	# This is the postinstall config
	my $installedServiceStr = "";
	my $enabledServiceStr = "";

	setLocalConfig ("zimbra_server_hostname", $config{HOSTNAME});

	setLocalConfig ("ldap_host", $config{LDAPHOST});
	setLocalConfig ("ldap_port", $config{LDAPPORT});
	my $uid = `id -u zimbra`;
	chomp $uid;
	my $gid = `id -g zimbra`;
	chomp $gid;
	setLocalConfig ("zimbra_uid", $uid);
	setLocalConfig ("zimbra_gid", $gid);
	setLocalConfig ("zimbra_user", "zimbra");

	if (defined $config{AVUSER}) {
		setLocalConfig ("av_notify_user", $config{AVUSER})
	}
	if (defined $config{AVDOMAIN}) {
		setLocalConfig ("av_notify_domain", $config{AVDOMAIN})
	}
	if (defined $config{EXPIRY}) {
		setLocalConfig ("trial_expiration_date", $config{EXPIRY});
	}

	if (!$ldapConfigured && isEnabled("zimbra-ldap")) {
		logMessage("Initializing ldap...\n");
		runAsZimbra ("/opt/zimbra/libexec/zmldapinit $config{LDAPPASS}");
		logMessage("Done\n");
	} elsif (isEnabled("zimbra-ldap")) {
		# zmldappasswd starts ldap and re-applies the ldif
		if ($ldapPassChanged) {
			logMessage("Setting ldap password...\n");
			runAsZimbra 
				("/opt/zimbra/openldap/sbin/slapindex -f /opt/zimbra/conf/slapd.conf");
			runAsZimbra ("/opt/zimbra/bin/zmldappasswd --root $config{LDAPPASS}");
			runAsZimbra ("/opt/zimbra/bin/zmldappasswd $config{LDAPPASS}");
			logMessage("Done\n");
		} else {
			logMessage("Starting ldap...\n");
			runAsZimbra 
				("/opt/zimbra/openldap/sbin/slapindex -f /opt/zimbra/conf/slapd.conf");
			runAsZimbra ("ldap start");
			runAsZimbra ("zmldapapplyldif");
			logMessage("Done\n");
		}
	} else {
		setLocalConfig ("ldap_root_password", $config{LDAPPASS});
		setLocalConfig ("zimbra_ldap_password", $config{LDAPPASS});
	}

	logMessage("Creating server entry for $config{HOSTNAME}...");
	runAsZimbra("/opt/zimbra/bin/zmprov cs $config{HOSTNAME}");
	logMessage("Done\n");

	if (isEnabled("zimbra-store")) {
		if ($config{USESPELL} eq "yes") {
			logMessage("Setting spell check URL to $config{SPELLURL}...\n");
			runAsZimbra("/opt/zimbra/bin/zmprov ms $config{HOSTNAME} ".
				"zimbraSpellCheckURL $config{SPELLURL}");
			logMessage("Done\n");
		}
		logMessage("Setting service ports on $config{HOSTNAME}...\n");
		runAsZimbra("/opt/zimbra/bin/zmprov ms $config{HOSTNAME} ".
			"zimbraImapBindPort $config{IMAPPORT} zimbraImapSSLBindPort $config{IMAPSSLPORT} ".
			"zimbraPop3BindPort $config{POPPORT} zimbraPop3SSLBindPort $config{POPSSLPORT}");
		logMessage("Done\n");
		addServerToHostPool();
	}

	if (!$ldapConfigured && isEnabled("zimbra-ldap")) {
		if ($config{DOCREATEDOMAIN} eq "yes") {
			logMessage("Creating domain $config{CREATEDOMAIN}...\n");
			runAsZimbra("/opt/zimbra/bin/zmprov cd $config{CREATEDOMAIN}");
			runAsZimbra("/opt/zimbra/bin/zmprov mcf zimbraDefaultDomainName $config{CREATEDOMAIN}");
			logMessage("Done\n");
			if ($config{DOCREATEADMIN} eq "yes") {
				logMessage("Creating user $config{CREATEADMIN}...\n");
				runAsZimbra("/opt/zimbra/bin/zmprov ca ".
					"$config{CREATEADMIN} \'$config{CREATEADMINPASS}\' ".
					"zimbraIsAdminAccount TRUE");
				logMessage("Done\n");
				logMessage("Creating postmaster alias...\n");
				runAsZimbra("/opt/zimbra/bin/zmprov aaa $config{CREATEADMIN} root\@$config{CREATEDOMAIN}");
				runAsZimbra("/opt/zimbra/bin/zmprov aaa $config{CREATEADMIN} postmaster\@$config{CREATEDOMAIN}");
				logMessage("Done\n");
			}
		}
	}

	if (!$sqlConfigured && isEnabled("zimbra-store")) {
		logMessage("Initializing store sql database...\n");
		runAsZimbra ("/opt/zimbra/libexec/zmmyinit");
		logMessage("Done\n");
		logMessage("Setting zimbraSmtpHostname for $config{HOSTNAME}\n");
		runAsZimbra("/opt/zimbra/bin/zmprov ms $config{HOSTNAME} ".
			"zimbraSmtpHostname $config{SMTPHOST}");
		logMessage("Done\n");
	}

	if (!$loggerSqlConfigured && isEnabled("zimbra-logger")) {
		logMessage("Initializing store sql database...\n");
		runAsZimbra ("/opt/zimbra/libexec/zmloggerinit");
		logMessage("Done\n");
	} 

	if (isEnabled("zimbra-logger")) {
		runAsZimbra ("/opt/zimbra/bin/zmprov mcf zimbraLogHostname $config{HOSTNAME}");
	}

	if (isEnabled("zimbra-mta")) {
		logMessage("Initializing mta config...\n");
		runAsZimbra ("/opt/zimbra/libexec/zmmtainit $config{LDAPHOST}");
		logMessage("Done\n");
		$installedServiceStr .= "zimbraServiceInstalled antivirus ";
		$installedServiceStr .= "zimbraServiceInstalled antispam ";
		if ($config{RUNAV} eq "yes") {
			$enabledServiceStr .= "zimbraServiceEnabled antivirus ";
		}
		if ($config{RUNSA} eq "yes") {
			$enabledServiceStr .= "zimbraServiceEnabled antispam ";
		}
	}

	if (isEnabled("zimbra-snmp")) {
		logMessage("Configuring SNMP...\n");
		setLocalConfig ("snmp_notify", $config{SNMPNOTIFY});
		setLocalConfig ("smtp_notify", $config{SMTPNOTIFY});
		setLocalConfig ("snmp_trap_host", $config{SNMPTRAPHOST});
		setLocalConfig ("smtp_source", $config{SMTPSOURCE});
		setLocalConfig ("smtp_destination", $config{SMTPDEST});
		runAsZimbra ("/opt/zimbra/libexec/zmsnmpinit");
		logMessage("Done\n");
	}

	if (isEnabled("zimbra-spell")) {
		logMessage("Configuring Spell server...\n");
		$enabledServiceStr .= "zimbraServiceEnabled spell ";
		logMessage("Done\n");
	}

	foreach my $p (keys %installedPackages) {
		if ($p eq "zimbra-core") {next;}
		if ($p eq "zimbra-apache") {next;}
		$p =~ s/zimbra-//;
		if ($p eq "store") {$p = "mailbox";}
		$installedServiceStr .= "zimbraServiceInstalled $p ";
	}

	foreach my $p (keys %enabledPackages) {
		if ($p eq "zimbra-core") {next;}
		if ($p eq "zimbra-apache") {next;}
		if ($enabledPackages{$p} eq "Enabled") {
			$p =~ s/zimbra-//;
			if ($p eq "store") {$p = "mailbox";}
			$enabledServiceStr .= "zimbraServiceEnabled $p ";
		}
	}

	logMessage("Setting services on $config{HOSTNAME}\n");
	runAsZimbra ("/opt/zimbra/bin/zmprov ms $config{HOSTNAME} $installedServiceStr");
	runAsZimbra ("/opt/zimbra/bin/zmprov ms $config{HOSTNAME} $enabledServiceStr");
	logMessage("Done\n");

	if (isEnabled("zimbra-store") || isEnabled("zimbra-mta")) {
		logMessage("Setting up SSL...\n");
		if (-f "/opt/zimbra/java/jre/lib/security/cacerts") {
			`chmod 777 /opt/zimbra/java/jre/lib/security/cacerts >> $logfile.cmd 2>&1`;
		}
		setLocalConfig ("ssl_allow_untrusted_certs", "TRUE");
		if (!-f "/opt/zimbra/tomcat/conf/keystore") {
			runAsZimbra("cd /opt/zimbra; zmcreatecert");
		}
		if (isEnabled("zimbra-store")) {
			if (!-f "/opt/zimbra/tomcat/conf/keystore") {
				runAsZimbra("cd /opt/zimbra; zmcertinstall mailbox");
			}
			runAsZimbra("cd /opt/zimbra; zmtlsctl $config{MODE}");
		}
		if (isEnabled("zimbra-mta")) {
			if (! (-f "/opt/zimbra/conf/smtpd.key" || 
				-f "/opt/zimbra/conf/smtpd.crt")) {
				runAsZimbra("cd /opt/zimbra; zmcertinstall mta ".
					"/opt/zimbra/ssl/ssl/server/smtpd.crt ".
					"/opt/zimbra/ssl/ssl/ca/ca.key");
			}
		}
	}

	setupCrontab();
#postinstall::configure();

	if ($config{STARTSERVERS} eq "yes") {
		runAsZimbra ("/opt/zimbra/bin/zmcontrol start");
		runAsZimbra ("/opt/zimbra/bin/zmcontrol status");
	}

	if ($newinstall) {
		runAsZimbra ("/opt/zimbra/bin/zmsshkeygen");
		sleep 2;
		runAsZimbra ("/opt/zimbra/bin/zmupdateauthkeys");
	}

	getSystemStatus();

	close LOGFILE;

	exit 0;
}

sub setupCrontab {

	logMessage("Setting up Zimbra User's crontab\n");
	
	`/usr/bin/crontab -u zimbra -l > /tmp/crontab.zimbra.orig`;
	my $rc = 0xffff & system("grep ZIMBRASTART /tmp/crontab.zimbra.orig > /dev/null 2>&1");
	if ($rc) {
		`cat /dev/null > /tmp/crontab.zimbra.orig`;
	}
	$rc = 0xffff & system("grep ZIMBRAEND /tmp/crontab.zimbra.orig > /dev/null 2>&1");
	if ($rc) {
		`cat /dev/null > /tmp/crontab.zimbra.orig`;
	}
	`cat /tmp/crontab.zimbra.orig | sed -e '/# ZIMBRASTART/,/# ZIMBRAEND/d' > /tmp/crontab.zimbra.proc`;
	`cp -f /opt/zimbra/zimbramon/crontabs/crontab /tmp/crontab.zimbra`;

	if (isEnabled("zimbra-store")) {
		`cat /opt/zimbra/zimbramon/crontabs/crontab.store >> /tmp/crontab.zimbra`;
	}

	if (isEnabled("zimbra-logger")) {
		`cat /opt/zimbra/zimbramon/crontabs/crontab.logger >> /tmp/crontab.zimbra`;
	}

	if (isEnabled("zimbra-mta")) {
		`cat /opt/zimbra/zimbramon/crontabs/crontab.mta >> /tmp/crontab.zimbra`;
	}

	`echo "# ZIMBRAEND -- DO NOT EDIT ANYTHING BETWEEN THIS LINE AND ZIMBRASTART" >> /tmp/crontab.zimbra`;
	`cat /tmp/crontab.zimbra.proc >> /tmp/crontab.zimbra`;

	`/usr/bin/crontab -u zimbra /tmp/crontab.zimbra`;
	
	logMessage("Crontab setup complete\n");

}


sub addServerToHostPool {
	logMessage("Adding $config{HOSTNAME} to zimbraMailHostPool in default COS\n");
	my $id = `/opt/zimbra/bin/zmprov gs $config{HOSTNAME} | grep zimbraId | sed -e 's/zimbraId: //'`;
	chomp $id;

	my $hp = `/opt/zimbra/bin/zmprov gc default | grep zimbraMailHostPool | sed 's/zimbraMailHostPool: //'`;
	chomp $hp;

	my @HP = split (' ', $hp);

	my $n = "";

	foreach (@HP) {
		chomp;
		$n .= "zimbraMailHostPool $_ ";
	}

	$n .= "zimbraMailHostPool $id";

	`/opt/zimbra/bin/zmprov mc default $n >> $logfile.cmd 2>&1`;
	logMessage("Done\n");
}

sub startLdap {
	logMessage("Starting ldap...\n");
	runAsZimbra 
		("/opt/zimbra/openldap/sbin/slapindex -f /opt/zimbra/conf/slapd.conf");
	runAsZimbra ("ldap start");
	runAsZimbra ("zmldapapplyldif");
	logMessage("Done\n");
}

1;
