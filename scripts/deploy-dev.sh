#!/usr/bin/env bash
set -euo pipefail

APP_DIR="/opt/cheongyeon"
JAR_PATH="$APP_DIR/app.jar"
REPO_DIR="/home/ubuntu/cheongyeon-be"

echo "▶ move to repo"
cd "$REPO_DIR"

echo "▶ sync dev branch"
git fetch --all
git checkout dev
git reset --hard origin/dev

echo "▶ build"
./gradlew clean bootJar

echo "▶ deploy jar"
sudo mkdir -p "$APP_DIR"
sudo cp build/libs/*.jar "$JAR_PATH"
sudo chown ubuntu:ubuntu "$JAR_PATH"

echo "▶ restart service"
sudo systemctl restart cheongyeon.service
sudo systemctl status cheongyeon.service --no-pager || true

echo "✅ deploy finished"