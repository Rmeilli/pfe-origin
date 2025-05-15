export const environment = {
  production: false,
  authApiUrl: 'http://localhost:9090',
  authApiUrl1: 'http://localhost:9091', // Nouvelle URL pour auth-service1
  creApiUrl: 'http://localhost:8082',
  keycloakUrl: 'http://localhost:8080',
  keycloakRealm: 'bp-realm', // Mis à jour pour correspondre à la configuration de auth-service1
  keycloakClientId: 'bp-client', // Mis à jour pour correspondre à la configuration de auth-service1
  apiTimeout: 30000, // 30 seconds
  defaultPageSize: 10
}; 