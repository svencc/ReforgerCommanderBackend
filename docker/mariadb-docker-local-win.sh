#!/bin/sh

# variables
MARIADB_CONTAINER_NAME="refCom-mariadb"
MARIADB_PORT="8033"
MARIADB_DATA_DIRECTORY="db-datadir"
MARIADB_USER="refComUser"
MARIADB_PASSWORD="refComPwd"
MARIADB_ROOT_PASSWORD="refComRootPwd"
PROJECT_FOLDER=$(pwd)
PROJECT_DB_NAME=refCom_db

docker pull mariadb

# Look if the docker db container already exists
DOCKER_DBCONTAINER_FILTER_STRIMG=$(docker container ls --all)
echo $DOCKER_DBCONTAINER_FILTER_STRIMG
if [[ $DOCKER_DBCONTAINER_FILTER_STRIMG == *"$MARIADB_CONTAINER_NAME"* ]]; then
  echo "Container $MARIADB_CONTAINER_NAME was found!"
  # start if exists
  docker start $MARIADB_CONTAINER_NAME
  # wait until db is startet and initialized
  sleep 10
  # run mariadb-docker-local cli, connected to running
  docker run --name $MARIADB_CONTAINER_NAME-cli -it --rm mariadb mysql -hhost.docker.internal --port=$MARIADB_PORT -uroot -p$MARIADB_ROOT_PASSWORD
else
  echo "Container $MARIADB_CONTAINER_NAME is missing!"
  # create container:
  mkdir -p "../$MARIADB_DATA_DIRECTORY"
  winpty docker run -p $MARIADB_PORT:3306 -v "$PROJECT_FOLDER"/../$MARIADB_DATA_DIRECTORY:/var/lib/mysql --detach --name $MARIADB_CONTAINER_NAME --env MARIADB_USER=$MARIADB_USER --env MARIADB_PASSWORD=$MARIADB_PASSWORD --env MARIADB_ROOT_PASSWORD=$MARIADB_ROOT_PASSWORD mariadb:latest
  winpty docker start $MARIADB_CONTAINER_NAME
  # wait until db is startet and initialized
  sleep 10
  echo "Create $MARIADB_CONTAINER_NAME DB!"
  # run mariadb-docker-local cli, execute CREATE DATABASE query
  winpty docker run --name $MARIADB_CONTAINER_NAME-cli -it --rm mariadb mysql -hhost.docker.internal --port=$MARIADB_PORT -uroot -p$MARIADB_ROOT_PASSWORD -e "CREATE DATABASE $PROJECT_DB_NAME;"
  # run mariadb-docker-local cli, connected to running
  winpty docker run --name $MARIADB_CONTAINER_NAME-cli -it --rm mariadb mysql -hhost.docker.internal --port=$MARIADB_PORT -uroot -p$MARIADB_ROOT_PASSWORD
  sleep 999
fi