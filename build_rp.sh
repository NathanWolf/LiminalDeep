#!/bin/bash
cd "$( dirname "$0" )"
rm -r target
mkdir target
cd target
mkdir rp
cd rp

echo "** BUILDING RP **"

cp -R ../../src/rp/* .

# Clean and zip
find . -name ".DS_Store" -type f -delete
zip -q -X -r ../LiminalDeep-RP.zip *
cd ..

echo "** COPYING TO MINECRAFT **"
cp *.zip ~/Library/Application\ Support/minecraft/resourcepacks/
