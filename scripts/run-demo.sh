#!/usr/bin/env sh
set -eu
cd "$(dirname "$0")/.."
if [ -f dist/category-user-admin.war ]; then
 exec java -jar dist/category-user-admin.war
else
 exec sh ./mvnw spring-boot:run
fi
