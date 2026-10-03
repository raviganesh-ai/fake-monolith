@description('Name of the Azure resource group location')
param location string

@description('Name prefix for all resources')
param namePrefix string = 'fake-monolith'

@description('Azure Container Registry name (must be globally unique)')
param acrName string

@description('Container image name, including tag, in ACR (e.g. fake-monolith:latest)')
param containerImage string

@description('CPU cores for the container app')
param cpuCores float = 0.5

@description('Memory in GiB for the container app')
param memoryGiB float = 1.0

@description('Minimum number of replicas')
param minReplicas int = 1

@description('Maximum number of replicas')
param maxReplicas int = 3

@description('External ingress enabled')
param enableIngress bool = true

@description('Target port for the container app')
param targetPort int = 8080

@description('Log analytics workspace resource ID for diagnostics (optional)')
@allowed(['', 'placeholder'])
param logAnalyticsWorkspaceResourceId string = ''

// Resource: Azure Container Registry
resource acr 'Microsoft.ContainerRegistry/registries@2023-07-01' = {
  name: acrName
  location: location
  sku: {
    name: 'Basic'
  }
  properties: {
    adminUserEnabled: true
  }
}

// Resource: Container Apps Environment
resource containerEnv 'Microsoft.App/managedEnvironments@2023-05-01' = {
  name: '${namePrefix}-env'
  location: location
  properties: {
    appLogsConfiguration: logAnalyticsWorkspaceResourceId == '' ? null : {
      destination: 'log-analytics'
      logAnalyticsConfiguration: {
        customerId: split(logAnalyticsWorkspaceResourceId, '/')[8]
        sharedKey: 'REPLACE_WITH_SHARED_KEY_AT_DEPLOYMENT' // placeholder
      }
    }
  }
}

// Resource: Container App
resource containerApp 'Microsoft.App/containerApps@2023-05-01' = {
  name: '${namePrefix}-app'
  location: location
  properties: {
    managedEnvironmentId: containerEnv.id
    configuration: {
      activeRevisionsMode: 'Multiple'
      ingress: enableIngress ? {
        external: true
        targetPort: targetPort
        transport: 'auto'
      } : null
      registries: [
        {
          server: '${acr.properties.loginServer}'
          username: acr.listCredentials().username
          passwordSecretRef: 'acr-password'
        }
      ]
      secrets: [
        {
          name: 'acr-password'
          value: acr.listCredentials().passwords[0].value
        }
      ]
    }
    template: {
      containers: [
        {
          name: '${namePrefix}-container'
          image: '${acr.properties.loginServer}/${containerImage}'
          resources: {
            cpu: cpuCores
            memory: '${memoryGiB}Gi'
          }
          env: [
            {
              name: 'SERVER_PORT'
              value: string(targetPort)
            },
            {
              name: 'SPRING_PROFILES_ACTIVE'
              value: 'default'
            }
          ]
          probes: [
            {
              type: 'Liveness'
              httpGet: {
                path: '/actuator/health'
                port: targetPort
              }
              initialDelaySeconds: 60
              periodSeconds: 30
            },
            {
              type: 'Readiness'
              httpGet: {
                path: '/actuator/health'
                port: targetPort
              }
              initialDelaySeconds: 60
              periodSeconds: 30
            }
          ]
        }
      ]
      scale: {
        minReplicas: minReplicas
        maxReplicas: maxReplicas
      }
    }
  }
}

output containerAppUrl string = enableIngress ? 'https://${containerApp.name}.${location}.azurecontainerapps.io' : ''
