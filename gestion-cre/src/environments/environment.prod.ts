export const environment = {
  production: true,
  authApiUrl: '/auth-api',  // Will be replaced by actual production URL
  creApiUrl: '/cre-api',    // Will be replaced by actual production URL
  keycloakUrl: '/auth',     // Will be replaced by actual Keycloak URL
  keycloakRealm: 'auth-service',
  keycloakClientId: 'auth-service-client',
  apiTimeout: 60000, // 60 seconds for production
  defaultPageSize: 20
}; 