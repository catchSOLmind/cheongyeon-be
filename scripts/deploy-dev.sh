#!/usr/bin/env bash
set -euo pipefail

APP_DIR="/opt/cheongyeon"
JAR_PATH="$APP_DIR/app.jar"
REPO_DIR="/home/ubuntu/cheongyeon-be"
ENV_FILE="$APP_DIR/cheongyeon.conf" # 환경변수 주입을 위한 파일 경로

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

# 깃허브에서 받은 키를 파일로 저장
echo "▶ update environment config"
if [ -n "$OPEN_AI_API_KEY" ]; then
    echo "OPEN_AI_API_KEY=$OPEN_AI_API_KEY" | sudo tee "$ENV_FILE" > /dev/null
    sudo chmod 600 "$ENV_FILE"
    echo "API Key saved to $ENV_FILE"
else
    echo "OPEN_AI_API_KEY is missing! Skipping config update."
fi

echo "▶ restart service"
sudo systemctl restart cheongyeon.service
sudo systemctl status cheongyeon.service --no-pager || true

echo "✅ deploy finished"