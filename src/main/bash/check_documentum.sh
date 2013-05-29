#!/bin/bash

#Declarations
OK=0
WARNING=1
CRITICAL=2
UNKNOWN=3

#Input declaration
WARN=$1
CRIT=$2

# environment
JAVA_HOME=/usr/java

PROG=`/bin/basename $0`
GREP=/bin/grep
ECHO=/bin/echo


print_usage() {
    echo "Usage: $PROG -u <username> -p <password> -d <docbase_name> -w <sec> -c <sec> -[SiWwFC] -q <fulltext index user>"
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
        -u)
            USERNAME=$2;
            shift 2;
            ;;
        -p)
            PASSWORD=$2;
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
	-q)
	    COMMAND="-q ${2}";
	    shift 2;
	    ;;
	-S)
	    COMMAND="-S";
	    shift 2;
	    ;;
	-W)
	    COMMAND="-W";
	    shift 2;
	    ;;
	-w)
	    COMMAND="-w";
	    shift 2;
	    ;;
	-i)
	    COMMAND="-i";
	    shift 2;
	    ;;
	-F)
	    COMMAND="-F";
	    shift 2;
	    ;;
	-C)
	    COMMAND="-C";
	    shift 2;
	    ;;
        *)
            echo "Unknown argument: $1"
            print_usage
            exit $UNKNOWN
            ;;
esac
done

JAVA_MEM="-Xms512m -Xmx512m -XX:MaxPermSize=128m"
CLASSPATH="classes/main;./libs/*"
CLASS="ru.jilime.documentum.Monitor"
CMD=${JAVA_HOME}/bin/java ${JAVA_MEM} -cp ${CLASSPATH} ${CLASS} -u ${USERNAME} -p ${PASSWORD} -d ${DOCBASE} ${COMMAND}

NOW_T=`date '+%s'`
#Documentum check
RESULT="`${CMD}`"

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
