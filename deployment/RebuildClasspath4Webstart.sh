#!/bin/sh


find lib  | grep jar | sed 's/^/<jar href="/' | sed 's/$/"\/>'/
