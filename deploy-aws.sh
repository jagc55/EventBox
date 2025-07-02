#!/bin/bash

# Script para preparar y desplegar la aplicación en AWS EC2

# Actualiza el sistema
echo "Actualizando el sistema..."
sudo apt-get update
sudo apt-get upgrade -y

# Instalar Docker
echo "Instalando Docker..."
sudo apt-get install -y apt-transport-https ca-certificates curl software-properties-common
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
sudo add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable"
sudo apt-get update
sudo apt-get install -y docker-ce docker-ce-cli containerd.io

# Iniciar y habilitar Docker
echo "Iniciando y habilitando Docker..."
sudo systemctl start docker
sudo systemctl enable docker

# Instalar Docker Compose
echo "Instalando Docker Compose..."
sudo curl -L "https://github.com/docker/compose/releases/download/v2.24.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# Dar permisos a usuario actual para usar Docker
sudo usermod -aG docker ${USER}

# Crear carpeta para la aplicación
mkdir -p ~/eventbox

# Copiar archivos necesarios a la carpeta
cp docker-compose.yml ~/eventbox/
cp .env ~/eventbox/
cp -r backend ~/eventbox/

# Ir a la carpeta de la aplicación
cd ~/eventbox

# Levantar los contenedores
echo "Levantando los contenedores..."
docker-compose up -d

echo "¡Despliegue completado con éxito!"
echo "Puedes acceder al backend en http://$(curl -s http://checkip.amazonaws.com):8080"
echo "Elasticsearch está disponible en http://$(curl -s http://checkip.amazonaws.com):9200"
