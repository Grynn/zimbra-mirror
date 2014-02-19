#!/bin/sh

# PROVIDE: amavis_mc
# REQUIRE: LOGIN
# BEFORE: amavisd
# KEYWORD: shutdown

#
# Add the following lines to /etc/rc.conf to enable amavis-mc:
#
#amavis_mc_enable="YES"

. /etc/rc.subr

name="amavis_mc"
rcvar=`set_rcvar`
command="/usr/local/sbin/amavis-mc"
command_interpreter="/usr/bin/perl"
pidfile="/var/amavis/amavis-mc.pid"

required_files="/usr/local/sbin/amavis-services"

load_rc_config $name

amavis_mc_enable=${amavis_mc_enable:-"NO"}
amavis_mc_flags=${amavis_mc_flags:-"-P ${pidfile}"}
amavis_mc_user=${amavis_mc_user:-"vscan"}
amavis_mc_group=${amavis_mc_group:-"vscan"}

run_rc_command "$1"
