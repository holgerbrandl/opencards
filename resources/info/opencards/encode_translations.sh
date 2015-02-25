#!/bin/sh
# this script will convert the utf-encoded translation files into ascii (which is required by java)
#cd resources/info/opencards

native2ascii -encoding utf8 translation_el_unencoded.properties translation_el.properties
native2ascii -encoding utf8 translation_bg_unencoded.properties translation_bg.properties
native2ascii -encoding utf8 translation_zh_unencoded.properties translation_zh.properties
native2ascii -encoding utf8 translation_ja_unencoded.properties translation_ja.properties