#!/bin/bash

OS="$(tr '[:upper:]' '[:lower:]' <<< $(uname -s))"
GITHUB_BIN="bin/${OS}/amd64/github-release"
USER=camunda
REPO=camunda-bpm-platform-osgi
ARTIFACT_NAME=camunda-bpm-karaf-assembly-${RELEASE_VERSION}

wget https://github.com/aktau/github-release/releases/download/v0.5.3/${OS}-amd64-github-release.tar.bz2
tar xvjf ${OS}-amd64-github-release.tar.bz2

${GITHUB_BIN} release \
    --user ${USER} \
    --repo ${REPO} \
    --tag ${RELEASE_VERSION} \
    --name "the wolf of source street" \
    --description "Not a movie, contrary to popular opinion. Still, my first release!" \
    --pre-release

${GITHUB_BIN} upload \
    --user ${USER} \
    --repo ${REPO} \
    --tag ${RELEASE_VERSION} \
    --name "${ARTIFACT_NAME}.tar.gz" \
    --file "camunda-bpm-karaf-assembly/target/${ARTIFACT_NAME}.tar.gz"

${GITHUB_BIN} upload \
    --user ${USER} \
    --repo ${REPO} \
    --tag ${RELEASE_VERSION} \
    --name "${ARTIFACT_NAME}.zip" \
    --file "camunda-bpm-karaf-assembly/target/${ARTIFACT_NAME}.zip"
