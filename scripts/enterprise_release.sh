#!/usr/bin/env bash
set -e

# Use full history for release
git checkout -B "${TRAVIS_BRANCH}"

git config user.email "${GIT_EMAIL}"

if [ -z ${RELEASE_VERSION} ] || [ -z ${DEVELOPMENT_VERSION} ]; then
    echo "Please provide a Release and Development version"
    exit 1
fi

# Check if it's a hotfix version by counting the number of dots in the version number.
if [ $(echo "${RELEASE_VERSION}" | grep -o "\." | wc -l) == 3 ]; then
  PROFILE="hotfix-release"
else
  PROFILE="enterprise-release"
fi

mvn -B \
    -Dusername="${GIT_USERNAME}" \
    -Dpassword="${GIT_PASSWORD}" \
    -DreleaseVersion=${RELEASE_VERSION} \
    -DdevelopmentVersion=${DEVELOPMENT_VERSION} \
    -DscmCommentPrefix="[maven-release-plugin][skip ci] " \
    -DuseReleaseProfile=false \
    "-Darguments=-DskipTests -Penterprise -Prelease-enterprise,${PROFILE}" \
    release:clean release:prepare release:perform
