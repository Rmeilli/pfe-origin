#!/bin/bash
set -e

# Mettre à jour les paquets
apt-get update

# Installer Maven
apt-get install -y maven

# Installer Node.js et npm
apt-get install -y curl
curl -sL https://deb.nodesource.com/setup_18.x | bash -
apt-get install -y nodejs

# Vérifier les installations
mvn --version
node --version
npm --version

# Installer les plugins Jenkins
jenkins-plugin-cli --plugins git workflow-aggregator pipeline-stage-view docker-workflow
