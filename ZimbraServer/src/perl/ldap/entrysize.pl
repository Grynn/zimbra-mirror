#
# Output entry size stats 
#

sub usage {
    my $availET = join(" | ", keys(%AVAIL_ET));
    my $availVB = join(" | ", keys(%AVAIL_VB));
    my $availFM = join(" | ", keys(%AVAIL_FM));    

    print "\nUsage: $0 " .
          "-f LDIF-FILE " .
          "-s SCHEMA-FILE " .
          "[-a] " .
          "[(-t entry-type)...] " .
          "[(-v verbose-on)...] " .
          "[(-o output-format)...]" .
          "\n\n";
          
    print "    entry-type: $availET\n\n";
    print "    verbose-on: $availVB\n\n";
    print "    output-format: $availFM\n\n";

    print "For example:\n    $0 -f dogfood.dmp -s ../../../build/ldap-config/zimbra.schema -t account -t cos\n\n";

    # foreach (@ARGV) {
    #     print "$_\n";
    # }
}

sub usage_junk {
    print "\nUsage: $0 " .
          "-f ldif-file " .
          "-s schema-file" .
          "[(-t $AVAIL_ET)...] " .
          "[(-v $AVAIL_VB)...] " .
          "[-a] " .
          "[(-o $AVAIL_FM)...]" .
          "\n\n";
}

sub usageError {
    print "@_\n";
    usage();
    exit(1);
}

sub checkArgs {
    my $i = 0;
    my $argc = @ARGV;
    while ($i < $argc) {
        if ($ARGV[$i] eq "-f") {
            $i++;
            if (defined($ARGV[$i])) {
                $ldifFile = $ARGV[$i];
            } else {
                usageError("missing ldif file after -v");
            }
        } elsif ($ARGV[$i] eq "-s") {
            $i++;
            if (defined($ARGV[$i])) {
                $schemaFile = $ARGV[$i];
            } else {
                usageError("missing schema file after -s");
            }
        } elsif ($ARGV[$i] eq "-a") {
            $g_doAttrStats = 1;
        } elsif ($ARGV[$i] eq "-t") {
            $i++;
            if (defined($ARGV[$i])) {
                $t = $ARGV[$i];
            } else {
                usageError("missing entry-type after -t");
            }
            
            if (!defined($AVAIL_ET{$t})) {
                usageError("invalid entry-type: $t");
            } else {
                $g_includeEntryTypeMap{$t} = 1;
            }
            
        } elsif ($ARGV[$i] eq "-v") {
            $i++;
            if (defined($ARGV[$i])) {
                $v = $ARGV[$i];
            } else {
                usageError("missing verbose-on after -v");
            }
            
            if (!defined($AVAIL_VB{$v})) {
                usageError("invalid verbose-on: $v");
            } else {
                $g_verbose{$v} = 1;
            }
        } elsif ($ARGV[$i] eq "-o") {
            $i++;
            if (defined($ARGV[$i])) {
                $o = $ARGV[$i];
            } else {
                usageError("missing output-format after -o");
            }
            
            if (!defined($AVAIL_FM{$o})) {
                usageError("invalid output-format: $o");
            } else {
                $g_outFormat = $o;
            }
        } else {
            usageError("invalid option: $ARGV[$i]");
        }
        $i++;
    }
    
    if (!defined($ldifFile)) {
        usageError("missing ldif file");
    }
    
    if (!defined($schemaFile)) {
        usageError("missing schema file");
    }

    
    print "\n\n";
    print "--------------------------------------------------\n";
    print "ldif file: $ldifFile\n";
    print "schema file: $schemaFile\n";
    print "--------------------------------------------------\n";
    print "\n\n";
}

sub noref(@) {
}

sub debug($) {
    print("=== @_\n");
}

sub warning ($) {
    print("[WARN] @_\n");
}

# Perl trim function to remove whitespace from the start and end of the string
sub trim($) {
	my $string = shift;
	$string =~ s/^\s+//;
	$string =~ s/\s+$//;
	return $string;
}

# Left trim function to remove leading whitespace
sub ltrim($) {
	my $string = shift;
	$string =~ s/^\s+//;
	return $string;
}

# Right trim function to remove trailing whitespace
sub rtrim($) {
	my $string = shift;
	$string =~ s/\s+$//;
	return $string;
}

sub captureEntryType(@) {
    local($curEntryType, $attrVal) = ($_[0], $_[1]);
    
    if ($attrVal eq "zimbraAccount") {
        if ($curEntryType ne $ET_CALENDER_RESOURCE) {
            return $ET_ACCOUNT;
        } else {
            return $curEntryType;
        }
    } elsif ($attrVal eq "zimbraAlias") {
        return $ET_ALIAS;
    } elsif ($attrVal eq "zimbraCalendarResource") {
        return $ET_CALENDER_RESOURCE;
    } elsif ($attrVal eq "zimbraGlobalConfig") {
        return $ET_CONFIG;
    } elsif ($attrVal eq "zimbraCOS") {
        return $ET_COS;
    } elsif ($attrVal eq "zimbraDataSource") {
        return $ET_DATA_SOURCE;
    } elsif ($attrVal eq "zimbraDistributionList") {
        return $ET_DISTRIBUTION_LIST;
    } elsif ($attrVal eq "zimbraDomain") {
        return $ET_DOMAIN;
    } elsif ($attrVal eq "zimbraIdentity") {
        return $ET_IDENTITY;
    } elsif ($attrVal eq "zimbraImapDataSource") {
        return $ET_IMAP_DATA_SOURCE;
    } elsif ($attrVal eq "zimbraMimeEntry") {
        return $ET_MIME;
    } elsif ($attrVal eq "zimbraObjectEntry") {
        return $ET_OBJECT;
    } elsif ($attrVal eq "zimbraPop3DataSource") {
        return $ET_POP3_DATA_SOURCE;
    } elsif ($attrVal eq "zimbraSecurityGroup") {
        return $ET_SECURITY_GROUP;
    } elsif ($attrVal eq "zimbraServer") {
        return $ET_SERVER;
    } elsif ($attrVal eq "zimbraZimletEntry") {
        return $ET_ZIMLET;
    } else {
        return $curEntryType;
    }
}

sub doStat(@) {
    local($name, $size, $dn, *stat) = @_;
    
    $stat{$name}{$ST_SUM} += $size;
    $stat{$name}{$ST_CNT} += 1;
    
    if (!defined($stat{$name}{$ST_MIN}) || $size < $stat{$name}{$ST_MIN}) {
        $stat{$name}{$ST_MIN} = $size;
        $stat{$name}{$ST_MIN_INFO} = $dn;
    }
    
    if (!defined($stat{$name}{$ST_MAX}) || $size > $stat{$name}{$ST_MAX}) {
        $stat{$name}{$ST_MAX} = $size;
        $stat{$name}{$ST_MAX_INFO} = $dn;
    }
}

sub finishAttr() {
    
    my $attrSize;
    my $useGuess;
    my $guessIt = 1;
    
    if ($guessIt == 1 && defined($g_guesstimateAttrs{$g_curAttrType})) {
        $attrSize = $g_guesstimateAttrs{$g_curAttrType};
        $useGuess = 1;
    } else {
        $attrSize = length($g_curAttrValue);
        $useGuess = 0;
    }
    # debug("[$useGuess] [$g_curAttrType] [$g_curAttrValue] [$attrSize]");
        
    if (defined($g_verbose{$VB_ATTR})) {
        print "[$VB_ATTR] $g_curAttrType: $g_curAttrValue ($attrSize)\n";
    }
    $entrySize += $attrSize;
    
    #
    # for dn we push the value as well, for displaying convinience
    #
    if ($g_curAttrType eq "dn") {
        push(@entryAttrMap, $g_curAttrType . ": " . $g_curAttrValue);
    } else {
        # not used for now, maybe no need to keep it
        push(@entryAttrMap, $g_curAttrType);
    }
    
    if ($g_curAttrType eq "objectClass") {
        $entryType = captureEntryType($entryType, $g_curAttrValue);
    }
    
    if ($g_doAttrStats == 1) {
        doStat($g_curAttrType, $attrSize, $g_curAttrValue, *g_attrStats);
    }
}

sub resetEntry() {
    undef($g_curAttrType);
    undef($g_curAttrValue);
    $entrySize = 0;
    undef(@entryAttrMap);
    $entryType = "";
}

sub includeAllEntryTypes() {
    # if entry type is not given in command line, we include all entry types
    if (keys(%g_includeEntryTypeMap) == 0) {
        return 1;
    } else {
        return 0;
    }
}

sub includeEntryType($) {
    local $et = $_[0];
   
    # if entry type is not given in command line, we include all entry types
    if (includeAllEntryTypes()) {
        return 1;
    }
    
    if (defined($g_includeEntryTypeMap{$et})) {
        return 1;
    } else {
        return 0;
   }
        
}

sub finishEntry() {
    if (defined($g_curAttrType)) {
        finishAttr();
    }

    if (defined(@entryAttrMap)) { # sanity checking for there could be extra empty lines
        $dn = $entryAttrMap[0];
        
        if ($entryType ne "") {
            if (includeEntryType($entryType) == 1) {
                doStat($entryType, $entrySize, $dn, *g_entryStats);  
            
                if (defined($g_verbose{$VB_ENTRY})) {
                    print "[$VB_ENTRY] $entryType at [$dn]: $entrySize bytes\n";
                }
            } else {
                
            }
        } else {
            if (defined($g_verbose{$VB_ENTRY})) {
                print "[$VB_ENTRY] Entry [$dn] skipped\n";
            }
        }
    }

    # reset the entry
    resetEntry();
}

sub process($) {
    my $fileName = $_[0];
    my $line;
    my @lines;
    my @parts;
    my $attrType;
    my $attrValue;
    
    open(INFO, $fileName);
    @lines = <INFO>;
    close(INFO);
    
    resetEntry();
    
    foreach $line (@lines) {
        if ($line eq "\n") {
            finishEntry();
        } else {
            # debug($line);
            # if ($line =~ /^([^:]+):[:\s]+(.*)/) {
            if ($line =~ /^(\w+):[:\s]+(.*)/) {    
                $attrType = $1;
                $attrValue = trim($2);
                
                if (defined($g_curAttrType)) {
                   # 
                   # an attr can span multiple lines
                   #
                   # presence of :(text) or ::(binary) indicates the 
                   # starting of an attribute, hecen we can "finish" 
                   # the prev attribute.
                   #
                   finishAttr();
                }
            
                $g_curAttrType = $attrType;
                $g_curAttrValue = $attrValue;
            } else {
                $g_curAttrValue .= trim($line);
            }
        }
    }
    finishEntry();
}

sub process_junk($) {
    my $fileName = $_[0];
    my $line;
    my @lines;
    my @parts;
    my $attrType;
    my $attrValue;
    
    open(INFO, $fileName);
    @lines = <INFO>;
    close(INFO);
    
    resetEntry();
    
    foreach $line (@lines) {
        if ($line eq "\n") {
            finishEntry();
        } else {
            # debug($line);
            @parts = split(/:/, $line);
            $attrType = $parts[0];
            if (defined($parts[1])) {
                if ($parts[1] eq "" && defined($parts[2])) {
                    $attrValue = trim($parts[2]);
                } else {
                    $attrValue = trim($parts[1]);
                }
                if (defined($g_curAttrType)) {
                   # 
                   # an attr can span multiple lines
                   #
                   # presence of :(text) or ::(binary) indicates the 
                   # starting of an attribute, hecen we can "finish" 
                   # the prev attribute.
                   #
                   finishAttr();
                }
            
                $g_curAttrType = $attrType;
                $g_curAttrValue = $attrValue;
            
            } else {
                $g_curAttrValue .= trim($line);
            }
        
        }
    }
    finishEntry();
}


sub outputStat(@) {
    local ($type, $name, *stat)= @_;
    local $avg = sprintf("%.2f", $stat{$name}{$ST_SUM} / $stat{$name}{$ST_CNT});
    local $delim = "\t";
    local $result;
    
    if ($g_outFormat eq $FM_TSV) {
        $result = "$type${delim}$name${delim}$avg${delim}$stat{$name}{$ST_CNT}${delim}$stat{$name}{$ST_MIN}${delim}$stat{$name}{$ST_MAX}${delim}$stat{$name}{$ST_MIN_INFO}${delim}$stat{$name}{$ST_MAX_INFO}";
    } else {
        $result = "$type [$name]:\n" .
                  "    avg = $avg\n" .
                  "    cnt = $stat{$name}{$ST_CNT}\n" .
                  "    min = $stat{$name}{$ST_MIN} ($stat{$name}{$ST_MIN_INFO})\n" .
                  "    max = $stat{$name}{$ST_MAX} ($stat{$name}{$ST_MAX_INFO})\n";
    } 
    print "$result\n";            

            
}

sub outputEmptyStat(@) {
    local ($type, $name)= @_;
    local $delim = "\t";
    local $result;
    
    if ($g_outFormat eq $FM_TSV) {
        $result = "$type${delim}$name${delim}0${delim}0${delim}0${delim}0${delim}${delim}";
    } else {
        $result = "$type [$name]:\n" .
                  "    avg = 0\n" .
                  "    cnt = 0\n" .
                  "    min = 0\n" .
                  "    max = 0\n";
    }
    print "$result\n";          
   
}

sub outputEntryStats() {
    
    local $delim = "\t";

    if (includeAllEntryTypes()) {
        # if all entry types were requested (i.e. not specifically given in one of the -t option),
        # we print stats for all entry types, even for those that do not appear in the input file  
        %list =  %AVAIL_ET;
    } else {
        # if entry types were specifically requested (specifically given in one of the -t option),
        # we print stats for requests entry types only
        %list =  %g_includeEntryTypeMap;
    }
    
    if ($g_outFormat eq $FM_TSV) {
        print "Type${delim}Entry${delim}avg${delim}cnt${delim}min${delim}max${delim}min at dn${delim}max at dn\n";
    }
    
    foreach $et (sort(keys(%list))) {
        if (defined($g_entryStats{$et})) {
            outputStat("Entry", $et, *g_entryStats);
        } else {
            outputEmptyStat("Entry", $et);
        }
    }
}

sub printAttrStats() {

    local $delim = "\t";
    
    if ($g_outFormat eq $FM_TSV) {
        print "Type${delim}Attribute type${delim}avg${delim}cnt${delim}min${delim}max${delim}min value${delim}max value\n";
    }

    foreach $at (sort(keys(%g_attrStats))) {
        outputStat("Attr", $at, *g_attrStats);
    }
}

sub loadSchemaFile($) {
    my $fileName = $_[0];
    my @lines;
    my $line;
    my $attrType;
    open(INFO, $fileName);
    @lines = <INFO>;
    close(INFO);
    
    foreach $line (@lines) {
        if ($line eq "\n") {
            undef($attrType);
        } elsif ($line =~ /^ *#/) {
            # skip comments
        } else {
            if ($line =~ /(attributetype\s+\(\s+)(.*)/) {
                $attrType = $2;
            } elsif ($line =~ /(\s+SYNTAX\s+)([\d\.]*)/) {
                $syntax = $2;
                if (defined($attrType)) {
                    # debug("[$attrType] [$syntax]");
                    # add to guesstimate set if it is one of the type of which the size will 
                    # be guestimated
                    if (defined($GUESSTIMATE_SIZE{$syntax})) {
                        $g_guesstimateAttrs{$attrType} = $GUESSTIMATE_SIZE{$syntax};
                        # debug("[$attrType] [$GUESSTIMATE_SIZE{$syntax}]");
                    }
                    undef($attrType);
                } else {
                    warning("missing attributetype for syntax [$syntax]");
                }
            }

        }
    }
}

# -----------------------------
#  Constants
# -----------------------------

# entry types
$ET_ACCOUNT           = "account";
$ET_ALIAS             = "alias";
$ET_CALENDER_RESOURCE = "calender resource";
$ET_CONFIG            = "config";
$ET_COS               = "cos";
$ET_DATA_SOURCE       = "data source";
$ET_DISTRIBUTION_LIST = "distribution list";
$ET_DOMAIN            = "domain";
$ET_IDENTITY          = "identity";
$ET_IMAP_DATA_SOURCE  = "imap data source";
$ET_MIME              = "mime";
$ET_OBJECT            = "object";
$ET_POP3_DATA_SOURCE  = "pop3 data source";
$ET_SECURITY_GROUP    = "security group";
$ET_SERVER            = "server";
$ET_ZIMLET            = "zimlet";

# verbose options
$VB_ATTR   = "ATTR";
$VB_ENTRY  = "ENTRY";

# output format
$FM_TSV     = "tsv";
$FM_DISPLAY = "display"; 


# stats elements
$ST_SUM = "sum";
$ST_CNT = "cnt";
$ST_MIN = "min";
$ST_MIN_INFO = "mininfo";
$ST_MAX = "max";
$ST_MAX_INFO = "maxinfo";

%AVAIL_ET = ($ET_ACCOUNT           => "",
             $ET_ALIAS             => "",
             $ET_CALENDER_RESOURCE => "",
             $ET_CONFIG            => "",
             $ET_COS               => "",
             $ET_DATA_SOURCE       => "",
             $ET_DISTRIBUTION_LIST => "",
             $ET_DOMAIN            => "",
             $ET_IDENTITY          => "",
             $ET_IMAP_DATA_SOURCE  => "",
             $ET_MIME              => "", 
             $ET_OBJECT            => "", 
             $ET_POP3_DATA_SOURCE  => "",
             $ET_SECURITY_GROUP    => "", 
             $ET_SERVER            => "",
             $ET_ZIMLET            => "");

%AVAIL_VB = ($VB_ATTR  => "",
             $VB_ENTRY => "");

%AVAIL_FM = ($FM_TSV     => "",
             $FM_DISPLAY => "");

#
# attribute type syntax
#
$SY_BOOLEAN   = "1.3.6.1.4.1.1466.115.121.1.7";
$SY_EMAIL     = "1.3.6.1.4.1.1466.115.121.1.26";
$SY_EMAILP    = "1.3.6.1.4.1.1466.115.121.1.26";
$SY_GENTIME   = "1.3.6.1.4.1.1466.115.121.1.24";
$SY_ID        = "1.3.6.1.4.1.1466.115.121.1.15";
$SY_DURATION  = "1.3.6.1.4.1.1466.115.121.1.26";
$SY_ENUM      = "1.3.6.1.4.1.1466.115.121.1.15";
$SY_INTEGER   = "1.3.6.1.4.1.1466.115.121.1.27";
$SY_PORT      = "1.3.6.1.4.1.1466.115.121.1.27";
$SY_LONG      = "1.3.6.1.4.1.1466.115.121.1.27";
$SY_STRING    = "1.3.6.1.4.1.1466.115.121.1.15";
$SY_REGEX     = "1.3.6.1.4.1.1466.115.121.1.15";
$SY_ASTRING   = "1.3.6.1.4.1.1466.115.121.1.26";
$SY_OSTRING   = "1.3.6.1.4.1.1466.115.121.1.40";
$SY_CSTRING   = "1.3.6.1.4.1.1466.115.121.1.15";
$SY_PHONE     = "1.3.6.1.4.1.1466.115.121.1.50";

#
# guestimate size for anything non string
#
%GUESSTIMATE_SIZE = ($SY_BOOLEAN  => 8,   # .7
#                     $SY_GENTIME  => 8,   # .24
#                     $SY_DURATION => 8,   # .26
#                     $SY_ENUM     => 8,  # .15
                     $SY_INTEGER  => 8,   # .27
                     $SY_PORT     => 8,   # .27
                     $SY_LONG     => 8   # .27
                    );

#
# just to shut perl warnings
# ... used only once: possible typo at ...
#
@NOREF = ($SY_REGEX, 
          $SY_OSTRING, 
          $SY_EMAIL, 
          $SY_PHONE, 
          $SY_ID, 
          $SY_CSTRING,
          $SY_ASTRING,
          $SY_STRING,
          $SY_EMAILP,
          $SY_GENTIME,
          $SY_DURATION,
          $SY_ENUM);

# crazy
noref(@NOREF);                        
                 
#
# Global vars
#
%g_verbose = ();
%g_attrStats = ();
%g_entryStats = ();
%g_includeEntryTypeMap = ();
$g_doAttrStats = 0;
$g_outFormat = $FM_DISPLAY;

#
# Begin processing
#
checkArgs();
loadSchemaFile($schemaFile);
process($ldifFile);
outputEntryStats();
if ($g_doAttrStats == 1) {
    print "\n\n------------------------------------------------\n";
    print "Attributes";
    print "\n------------------------------------------------\n\n";
    printAttrStats();
}