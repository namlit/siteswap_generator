#!/bin/bash

inkscape appicon.svg --export-png ../app/src/main/res/mipmap-mdpi/appicon.png -w 48 -h 48
inkscape appicon.svg --export-png ../app/src/main/res/mipmap-hdpi/appicon.png -w 72 -h 72
inkscape appicon.svg --export-png ../app/src/main/res/mipmap-xhdpi/appicon.png -w 96 -h 96
inkscape appicon.svg --export-png ../app/src/main/res/mipmap-xxhdpi/appicon.png -w 144 -h 144
inkscape appicon.svg --export-png ../app/src/main/res/mipmap-xxxhdpi/appicon.png -w 192 -h 192