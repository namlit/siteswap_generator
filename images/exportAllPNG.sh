#!/bin/bash

# Regular app icons (legacy, for Android < 8.0)
inkscape appicon.svg+xml --export-type=png --export-filename="../app/src/main/res/mipmap-mdpi/appicon.png" -w 48 -h 48
inkscape appicon.svg+xml --export-type=png --export-filename="../app/src/main/res/mipmap-hdpi/appicon.png" -w 72 -h 72
inkscape appicon.svg+xml --export-type=png --export-filename="../app/src/main/res/mipmap-xhdpi/appicon.png" -w 96 -h 96
inkscape appicon.svg+xml --export-type=png --export-filename="../app/src/main/res/mipmap-xxhdpi/appicon.png" -w 144 -h 144
inkscape appicon.svg+xml --export-type=png --export-filename="../app/src/main/res/mipmap-xxxhdpi/appicon.png" -w 192 -h 192

# Adaptive icon foregrounds (for Android 8.0+)
# These are 108dp x 108dp with the icon scaled to fill more space
inkscape appicon-round.svg --export-type=png --export-filename="../app/src/main/res/mipmap-mdpi/appicon_foreground.png" -w 108 -h 108
inkscape appicon-round.svg --export-type=png --export-filename="../app/src/main/res/mipmap-hdpi/appicon_foreground.png" -w 162 -h 162
inkscape appicon-round.svg --export-type=png --export-filename="../app/src/main/res/mipmap-xhdpi/appicon_foreground.png" -w 216 -h 216
inkscape appicon-round.svg --export-type=png --export-filename="../app/src/main/res/mipmap-xxhdpi/appicon_foreground.png" -w 324 -h 324
inkscape appicon-round.svg --export-type=png --export-filename="../app/src/main/res/mipmap-xxxhdpi/appicon_foreground.png" -w 432 -h 432

#inkscape favorite_star_icon.svg   --export-type=png --export-filename="./app/src/main/res/drawable/favorite_star_icon.png" -w 96 -h 96
#inkscape rotate_default_icon.svg  --export-type=png --export-filename="./app/src/main/res/drawable/rotate_default_icon.png" -w 96 -h 96
#inkscape named_siteswaps_icon.svg --export-type=png --export-filename="./app/src/main/res/drawable/named_siteswaps_icon.png" -w 96 -h 96
