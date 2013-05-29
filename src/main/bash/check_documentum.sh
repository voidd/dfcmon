#!/bin/bash

#Declarations
OK=0
WARNING=1
CRITICAL=2
UNKNOWN=3

# environment
JAVA_HOME=/usr/java/latest

PROG=`/bin/basename $0`
GREP=/bin/grep
ECHO=/bin/echo


print_usage() {
    echo "Usage: $PROG -u <username> -p <password> -d <docbase_name> -w <sec> -c <sec> -[SiWwFC] -q <fulltext index user>"
}

if [ $# -lt 4 ]; then
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
            WARN=$2;
            shift 2;
            ;;
        -c)
            CRIT=$2;
            shift 2;
            ;;
	-q)
	    COMMAND="-q ${2}";
	    shift 2;
	    ;;
	-S)
	    COMMAND="-S";
	    shift 1;
	    ;;
	-W)
	    COMMAND="-W";
	    shift 1;
	    ;;
	-w)
	    COMMAND="-w";
	    shift 1;
	    ;;
	-i)
	    COMMAND="-i";
	    shift 1;
	    ;;
	-F)
	    COMMAND="-F";
	    shift 1;
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

# command list
JAVA_MEM="-Xms128m -Xmx128m -XX:MaxPermSize=64m"
CLASSPATH="classes/main;./libs/*"
CLASS="ru.jilime.documentum.Monitor"
CMD=${JAVA_HOME}/bin/java ${JAVA_MEM} -cp ${CLASSPATH} ${CLASS} -u ${USERNAME} -p ${PASSWORD} -d ${DOCBASE} ${COMMAND}

# check what time - this is a start point
#NOW_T=`date '+%s'`

# time check
RESULT="`${CMD}`"

if [ ${COMMAND} eq "-C" && $? == 0 ] ; then
	content
fi

content() {
if [ $RESULT == 0 ] ; then
	$ECHO "OK!"
	exit $OK
elif [ ${RESULT} == 1 ] ; then
	$ECHO "WARNING!"
	exit $WARNING
fi
}
	
	

if [ $? == 0 ] ; then
    if [ $RESULT -lt $WARN ] ; then
       $ECHO "OK!"
       exit $OK
    elif [ $RESULT -ge $WARN] ; then
	    if [ $RESULT -lt $CRIT]; then
		    $ECHO "WARNING!"
		    exit $WARNING
	    fi
	$ECHO "CRITICAL"
	exit $CRITICAL
    fi
fi

$ECHO "ERROR while retrieving information from Documentum!"
$ECHO $!
exit $CRITICAL
