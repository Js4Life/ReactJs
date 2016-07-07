#!/bin/bash
# Usage: cputrack 4286 test

filepath=/Users/leticiachoo/Desktop/cputrack   # modify as desired
interval=20                         # reports per minute
timelimit=3000                      # how long to run, in seconds

mydate=`date "+%H:%M:%S"`           # the timestamp
freq=$((60/$interval))              # for sleep function

while [ "$SECONDS" -le "$timelimit" ] ; do
  ps -p$1 -opid -opcpu -ocomm -c | grep $1 | sed "s/^/$mydate /" >> $filepath/$2.txt
  sleep $freq
  mydate=`date "+%H:%M:%S"`
done