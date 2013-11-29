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

PROG=`/bin/basename $0`
GREP=/bin/grep
ECHO=/bin/echo


print_usage() {
    echo "Usage: $PROG -t <docbroker_host> -p <docbroker_port> -d <docbase_name> -w <sec> -c <sec>"
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
        -w)
            W_TIME=$2;
            shift 2;
            ;;
        -c)
            C_TIME=$2;
            shift 2;
            ;;
        *)
            echo "Unknown argument: $1"
            print_usage
            exit $UNKNOWN
            ;;
esac
done

NOW_T=`date '+%s'`
#Documentum check
RESULT="`$DMQDOC -t $HOSTNAME -p $PORT -c getservermap $DOCBASE`"

if [ $? == 0 ] ; then
	END_T=`date '+%s'`
	let "res = ${END_T} - ${NOW_T}"
    if [ $res -lt $W_TIME ] ; then
       $ECHO "OK!"
       exit $OK
    elif [ $res -ge $W_TIME] ; then
	    if [ $res -lt $C_TIME]; then
		    $ECHO "WARNING!"
		    exit $WARNING
	    fi
	$ECHO "CRITICAL"
	exit $CRITICAL
    fi
fi

$ECHO "ERROR while retrieving docbroker information!"
exit $CRITICAL
