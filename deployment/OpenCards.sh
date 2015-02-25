#!/bin/sh

ocjar=opencards.jar

# prefix it if not available, which will be the case on ubuntu/linux
if [ ! -f $ocjar ]
then
    ocjar=/usr/share/opencards/$ocjar
fi

java -Xmx512m -jar $ocjar