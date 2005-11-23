
# This is CPAN.pm's systemwide configuration file. This file provides
# defaults for users, and the values can be changed in a per-user
# configuration file. The user-config file is being looked for as
# ~/.cpan/CPAN/MyConfig.pm.

#
# Replace occurences of BUILD_ROOT with
# the location of the zimbra source:
# ThirdParty/Perl
#

$CPAN::Config = {
  'build_cache' => q[10],
  'build_dir' => q[BUILD_ROOT/.cpan/build],
  'cache_metadata' => q[1],
  'cpan_home' => q[BUILD_ROOT/.cpan],
  'dontload_hash' => {  },
  'ftp' => q[/usr/bin/ftp],
  'ftp_proxy' => q[],
  'getcwd' => q[cwd],
  'gpg' => q[],
  'gzip' => q[/usr/bin/gzip],
  'histfile' => q[BUILD_ROOT/.cpan/histfile],
  'histsize' => q[100],
  'http_proxy' => q[],
  'inactivity_timeout' => q[0],
  'index_expire' => q[1],
  'inhibit_startup_message' => q[0],
  'keep_source_where' => q[BUILD_ROOT/.cpan/sources],
  'lynx' => q[],
  'make' => q[/usr/bin/make],
  'make_arg' => q[],
  'make_install_arg' => q[],
  'makepl_arg' => q[PREFIX=BUILD_ROOT/zimbramon LIB=BUILD_ROOT/zimbramon/lib EXPATLIBPATH=/opt/local/lib EXPATINCPATH=/opt/local/include],
  'ncftp' => q[],
  'ncftpget' => q[],
  'no_proxy' => q[],
  'pager' => q[/usr/bin/less],
  'prerequisites_policy' => q[ask],
  'scan_cache' => q[atstart],
  'shell' => q[/bin/sh],
  'tar' => q[/usr/bin/tar],
  'term_is_latin' => q[1],
  'unzip' => q[/usr/bin/unzip],
  'urllist' => [q[ftp://ftp.perl.org/pub/CPAN/]],
  'wget' => q[],
};
1;
__END__
