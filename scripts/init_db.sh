#!/usr/bin/env bash
set -euo pipefail

CONTAINER_NAME="mysql-dev"
MYSQL_IMAGE="mysql:8"
MYSQL_PORT="3306"
MYSQL_ROOT_PASSWORD="root"
MYSQL_DB_NAME="user_management"

echo "Initializing MySQL container: ${CONTAINER_NAME}"

if ! command -v docker >/dev/null 2>&1; then
	echo "Error: docker is not installed or not on PATH."
	exit 1
fi

if docker ps -a --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
	echo "Container ${CONTAINER_NAME} already exists. Starting it..."
	docker start "${CONTAINER_NAME}" >/dev/null
else
	echo "Creating container ${CONTAINER_NAME} on port ${MYSQL_PORT}..."
	docker run \
		--name "${CONTAINER_NAME}" \
		-e "MYSQL_ROOT_PASSWORD=${MYSQL_ROOT_PASSWORD}" \
		-e "MYSQL_DATABASE=${MYSQL_DB_NAME}" \
		-p "${MYSQL_PORT}:3306" \
		-d "${MYSQL_IMAGE}" >/dev/null
fi

echo "MySQL should be available at localhost:${MYSQL_PORT}."
echo "Database: ${MYSQL_DB_NAME} | User: root | Password: ${MYSQL_ROOT_PASSWORD}"