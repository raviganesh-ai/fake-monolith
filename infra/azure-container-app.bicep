@description('Name of the Azure Container Apps environment')
param environmentName string

@description('Location for the Container Apps environment')
param location string = resourceGroup().location

@description('Name of the Container App')
param containerAppName string = 'fake-monolith-app'

@description('Container image to deploy (e.g., myregistry.azurecr.io/fake-monolith:latest)')
param containerImage string

@description('CPU cores allocated per replica')
param cpu float = 0.5

@description('Memory in GiB allocated per replica')
param memory float = 1.0

@description('Min replica count for the Container App')
param minReplicas int = 1

@description('Max replica count for the Container App')
param maxReplicas int = 3

resource containerEnv 'Microsoft.App/managedEnvironments@2024-03-01' = {
  name: environmentName
  location: location
  properties: {
    appLogsConfiguration: {
      destination: 'log-analytics'
    }
  }
}

resource containerApp 'Microsoft.App/containerApps@2024-03-01' = {
  name: containerAppName
  location: location
  properties: {
    managedEnvironmentId: containerEnv.id
    configuration: {
      ingress: {
        external: true
        targetPort: 8080
      }
    }
    template: {
      containers: [
        {
          name: 'fake-monolith'
          image: containerImage
          resources: {
            cpu: cpu
            memory: memory
          }
          probes: [
            {
              type: 'Liveness'
              httpGet: {
                path: '/actuator/health/liveness'
                port: 8080
              }
              initialDelaySeconds: 30
              periodSeconds: 10
            }
            {
              type: 'Readiness'
              httpGet: {
                path: '/actuator/health/readiness'
                port: 8080
              }
              initialDelaySeconds: 30
              periodSeconds: 10
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
