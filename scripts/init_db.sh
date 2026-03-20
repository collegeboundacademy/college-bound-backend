#!/bin/bash

# Create MYSQL database using docker containers
brew services stop mysql

docker run --name mysql-dev -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=user_management -p 3306:3306 -d mysql:8