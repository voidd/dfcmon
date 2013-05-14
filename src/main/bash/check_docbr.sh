#!/bin/bash

#Declarations
OK=0
WARNING=1
CRITICAL=2
UNKNOWN=3

#Input declaration
WARN=$1
CRIT=$2

#change to your Documentum installation directory
DM_HOME="/u01/documentum/product/6.7"

#dmqdocbroker
DMQDOC="${DM_HOME}/bin/dmqdocbroker"

PROGNAME=`/bin/basename $0`
GREP=/bin/grep
ECHO=/bin/echo


print_usage() {
    echo "Usage: $PROGNAME -t <docbroker_host> -p <docbroker_port> -d <docbase_name>"
}

if [ $# -lt 3 ]; then
    print_usage
    exit $UNKNOWN
fi

# make sure script is running as documentum owner user
if [ `whoami` = root ]; then
   echo "UNKNOWN: Please make sure script is running as documentum owner user"
    exit $UNKNOWN
fi

while test $# -gt 0; do
case "$1" in
        --help)
            print_help
            exit $OK
            ;;
        -h)
            print_help
            exit $OK
            ;;
        -t)
            HOSTNAME=$2;
            shift 2;
            ;;
        -p)
            PORT=$2;
            shift 2;
            ;;
        -d)
            DOCBASE=$2;
            shift 2;
            ;;
        *)
            echo "Unknown argument: $1"
            print_usage
            exit $UNKNOWN
            ;;
esac
done

#Documentum check
RESULT="`$DMQDOC -t $HOSTNAME -p $PORT -c getservermap $DOCBASE`"

if [ $? == 0 ] ; then
    CHECK_IS_OPEN="$ECHO $RESULT | $GREP 'server status' | $GREP 'Open'"
    if [ $? == 0 ] ; then
       $ECHO "OK! Docbase $DOCBASE is projected on server $HOSTNAME and opened"
       exit $OK
    fi
fi

$ECHO "Docbase $DOCBASE is not projected on server $HOSTNAME"
exit $CRITICAL
