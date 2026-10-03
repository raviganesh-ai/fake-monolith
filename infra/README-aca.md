Fake Monolith - Azure Container Apps Deployment
==============================================

Overview
--------
This document describes how to deploy the Fake Monolith Spring Boot application to Azure Container Apps using the provided Bicep template and Dockerfile. The application remains a single monolithic container, preserving existing behavior and tests.

Prerequisites
-------------
- Azure subscription and resource group
- Azure CLI (az) installed and logged in
- Bicep CLI (or az bicep) installed
- Docker installed locally
- Java and Maven installed locally (for building the application)

Local Build and Test
--------------------
1. Run unit and integration tests:
   mvn clean verify

2. Build the Docker image:
   docker build -t fake-monolith:latest .

3. Run the container locally:
   docker run -p 8080:8080 fake-monolith:latest

4. Verify health endpoint:
   curl http://localhost:8080/actuator/health

Azure Container Registry
------------------------
1. Create Azure Container Registry (ACR):
   az acr create \
     --resource-group <rg-name> \
     --name <acr-name> \
     --sku Basic

2. Log in to ACR:
   az acr login --name <acr-name>

3. Tag and push image:
   docker tag fake-monolith:latest <acr-name>.azurecr.io/fake-monolith:latest
   docker push <acr-name>.azurecr.io/fake-monolith:latest

Deploy Azure Container Apps
---------------------------
1. Deploy the Bicep template:
   az deployment group create \
     --resource-group <rg-name> \
     --template-file infra/aca.bicep \
     --parameters \
       location=<azure-region> \
       acrName=<acr-name> \
       containerImage="fake-monolith:latest"

2. Retrieve container app URL from deployment outputs or Azure Portal.

3. Validate the application via browser and health endpoint:
   curl https://<container-app-name>.<region>.azurecontainerapps.io/actuator/health

Notes
-----
- The application uses an in-memory H2 database, so data is not persisted across container restarts.
- Scaling settings (minReplicas, maxReplicas, CPU, memory) can be adjusted via Bicep parameters.
- SERVER_PORT and SPRING_PROFILES_ACTIVE are passed as environment variables to the container app.
