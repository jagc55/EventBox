# Guía para desplegar EventBox en AWS EC2

## Requisitos previos

1. Una cuenta de AWS
2. Una instancia EC2 (recomendado: t3.small o superior)
3. Una AMI de Ubuntu Server 22.04 LTS
4. Un grupo de seguridad con los siguientes puertos abiertos:
   - 22 (SSH)
   - 8080 (Backend API)
   - 9200 (Elasticsearch HTTP)
   - 3306 (MySQL - opcional, solo si necesitas acceso externo)

## Pasos para el despliegue

### 1. Preparar la instancia EC2

1. Inicia sesión en la consola de AWS y ve a EC2
2. Haz clic en "Launch Instance"
3. Elige "Ubuntu Server 22.04 LTS" como AMI
4. Selecciona un tipo de instancia (recomendado: t3.small o superior)
5. Configura los detalles de la instancia según sea necesario
6. Agrega almacenamiento (recomendado: al menos 20 GB)
7. Configura el grupo de seguridad con los puertos mencionados arriba
8. Lanza la instancia y guarda el archivo .pem

### 2. Conectarse a la instancia

```bash
chmod 400 tu-clave.pem
ssh -i tu-clave.pem ubuntu@tu-ip-publica
```

### 3. Transferir archivos al servidor

Desde tu máquina local, transfiere los archivos necesarios:

```bash
scp -i tu-clave.pem -r docker-compose.yml .env backend ubuntu@tu-ip-publica:~/
```

### 4. Ejecutar script de despliegue

En tu instancia EC2:

```bash
# Dar permisos de ejecución
chmod +x deploy-aws.sh

# Ejecutar script
./deploy-aws.sh
```

### 5. Verificar el despliegue

Una vez completado el despliegue, puedes verificar que los servicios están en ejecución:

```bash
docker ps
```

Puedes comprobar los logs de cada servicio:

```bash
# Para el backend
docker logs -f backend

# Para Elasticsearch
docker logs -f elasticsearch

# Para MySQL
docker logs -f mysql
```

### 6. Configuración del Frontend (S3)

1. En la consola de AWS, ve a S3
2. Crea un nuevo bucket si no lo tienes ya
3. Habilita el alojamiento de sitios web estáticos en la configuración del bucket
4. Configura los permisos de acceso público
5. Sube los archivos compilados de tu frontend a S3
6. Actualiza el archivo de configuración de tu frontend para que apunte a la IP pública de tu instancia EC2

### 7. Mantenimiento y monitoreo

Para reiniciar los servicios:

```bash
cd ~/eventbox
docker-compose restart
```

Para detener los servicios:

```bash
cd ~/eventbox
docker-compose down
```

Para actualizar la aplicación:

```bash
# Actualizar los archivos locales (desde tu máquina local)
scp -i tu-clave.pem -r backend/* ubuntu@tu-ip-publica:~/eventbox/backend/

# Reconstruir y reiniciar los contenedores (desde la instancia EC2)
cd ~/eventbox
docker-compose down
docker-compose build
docker-compose up -d
```

## Consideraciones adicionales

- **Respaldos**: Configura respaldos periódicos del volumen de datos de MySQL
- **Escalabilidad**: Si necesitas más capacidad, considera usar RDS para MySQL y Elasticsearch Service de AWS
- **SSL/TLS**: Para producción, configura un certificado SSL con AWS Certificate Manager
- **CloudFront**: Considera usar CloudFront como CDN para tu frontend
- **Route53**: Usa Route53 para gestionar tus dominios y DNS
