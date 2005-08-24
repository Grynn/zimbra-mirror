#!/bin/bash

accounts=`zmprov gaa`

for a in $accounts; do
    mh=`zmprov ga $a | grep '^zimbraMailHost' | awk -F: '{ print $2; }'`
    cmd="zmprov ma $a zimbraMailHost $mh"
    if [ "x$1" = "x-f" ]; then
        echo "Running: $cmd"
        $cmd
    else
        echo "WillRun: $cmd"
    fi
done
