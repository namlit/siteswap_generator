#!/usr/bin/env sh

SCRIPT=
SCRIPTDIR=$(realpath "$(dirname "$0")")

version_file="$SCRIPTDIR/app/build.gradle"

version=$(grep versionName app/build.gradle | cut -d '"' -f2)

tag="v$version"
tag_message=$(git log -1 --no-merges --format="%s%n%n%b" "$version_file")

echo "Creating tag '$tag'"
echo "Tag message is:\n"
echo "$tag_message"

git tag -a -m "$tag_message" "$tag"