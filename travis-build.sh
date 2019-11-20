#!/bin/bash
set -e

export EXIT_STATUS=0

./gradlew --console=plain complete:books:test || EXIT_STATUS=$?

if [[ $EXIT_STATUS -ne 0 ]]; then
  exit $EXIT_STATUS
fi

./gradlew --console=plain complete:analytics:test || EXIT_STATUS=$?

if [[ $EXIT_STATUS -ne 0 ]]; then
  exit $EXIT_STATUS
fi

cd complete;

echo "Starting services"
./gradlew run -parallel --console=plain &
PID1=$!

echo "Waiting 20 seconds for microservices to start"

sleep 20

./gradlew acceptance:test --rerun-tasks --console=plain || EXIT_STATUS=$?

killall -9 java

if [ $EXIT_STATUS -ne 0 ]; then
  exit $EXIT_STATUS
fi

cd ..;

curl -O https://raw.githubusercontent.com/micronaut-projects/micronaut-guides/master/travis/build-guide
chmod 777 build-guide

./build-guide || EXIT_STATUS=$?

curl -O https://raw.githubusercontent.com/micronaut-projects/micronaut-guides/master/travis/republish-guides-website.sh
chmod 777 republish-guides-website.sh

./republish-guides-website.sh || EXIT_STATUS=$?

exit $EXIT_STATUS
