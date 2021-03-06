#!/bin/bash

# Update the info.xml files with random toolkit names, so different
# users can run tests concurrently.

# Use the same random name for each toolkit.
NAME=$(head -c16 <(tr -dc '[:lower:]' < /dev/urandom 2>/dev/null))

for i in $(find . -name info.xml.tmpl); do
    sed -e "s/com.example./com.example.${NAME}./" "$i" > "${i/.tmpl/}"
done
