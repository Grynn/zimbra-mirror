#!/usr/bin/env python
#
# Tool to mine information from "WindowsTimeZoneInfo.txt" for information to use to update "../windows-names"
# "WindowsTimeZoneInfo.txt" can be refreshed by:
# .  Update Windows 7 (or later?) to have the latest timezone information.
#    Suggest going to http://www.microsoft.com/time and following the links from there.
# .  Compile and run "getWindowsTimezoneInfo.cs" by e.g.:
#        Launch the Windows SDK Command prompt:
#        csc getWindowsTimezoneInfo.cs
#        getWindowsTimezoneInfo
#    This creates file "C:\Temp\WindowsTimeZoneInfo.txt"
#
# When run with UK English locale, the timezone "Display Names" used ":" in the offset description rather than ".".
# e.g. "(UTC-03:00) Montevideo" instead of "(UTC-03.00) Montevideo"
# Either the original descriptions in "../windows-names" contained typos or the names are locale dependant.
# For now, assuming the latter and sticking with "." in "../windows-names" and NOT
# Modern Outlook programs use the ID values, it is assumed that older programs use the "Display Name" values
# "../windows-names" is now updated for both settings.
#
# Only outputs lines where it thinks updates may be needed.

from optparse import OptionParser
import sys
import codecs
import os

# parse arguments
usage = ("Usage: %prog [options]\n")
parser = OptionParser(usage)
parser.add_option("-o", "--oldWindowsFile", dest="oldWindowsFile",
        type="string", default="../windows-names",
        help="OLDWINDOWSNAMES contains Link lines mapping Windows TZ names to Olson names(default %default)",
        metavar="OLDWINDOWSNAMES")
parser.add_option("-d", "--windowsTimezoneInfo", dest="windowsTimezoneInfo",
        type="string", default="WindowsTimeZoneInfo.txt",
        help="WTZINFO contains info about Windows Timezones (default %default)",
        metavar="WTZINFO")


sc_name = sys.argv[0]

(options, args) = parser.parse_args()

if not os.path.exists(options.oldWindowsFile):
    print "OLD windows-names File '{0}' does not exist".format(options.oldWindowsFile)
    sys.exit(1)
if not os.path.exists(options.windowsTimezoneInfo):
    print "NEW windows timezone info File '{0}' does not exist".format(options.windowsTimezoneInfo)
    sys.exit(1)

def getLinesForFile(winFile):
    inFile = codecs.open(winFile, 'r', "utf-8")
    inLines = inFile.readlines()
    return inLines

wToOlsen = {}
oldWindowsFileLines = getLinesForFile(options.oldWindowsFile)
for line in oldWindowsFileLines:
    linkDetails = line.split('\t')
    if linkDetails[0] != "Link":
        continue
    winName = linkDetails[len(linkDetails) - 1].strip()
    wToOlsen[winName] = linkDetails[1]

winTZInfo = getLinesForFile(options.windowsTimezoneInfo)
for line in winTZInfo:
    lineDetails = line.split(':')
    if lineDetails[0] == "ID":
        tzid = lineDetails[1].strip()
        qtzid = '"' + tzid + '"'
    elif lineDetails[0] == "   Display Name":
        displayName = line.strip()[13:].strip()
        dKey = '"' + displayName + '"'
        dkey2 = dKey.replace(":", ".")
        if dkey2 in wToOlsen:
            if not qtzid in wToOlsen:
                if not tzid == wToOlsen[dkey2]:
                    print "Link\t" + wToOlsen[dkey2] + '\t\t' + qtzid
        else:
            if qtzid in wToOlsen:
                print "Missing " + dkey2
            else:
                print "Missing [" + qtzid + "] and [" + dkey2 + "]"

sys.exit(0)
