export const environment = {
  production: true,
  authApiUrl: '/auth-api',  // Will be replaced by actual production URL
  authApiUrl1: '/auth-api1', // Will be replaced by actual production URL for auth-service1
  creApiUrl: '/cre-api',    // Will be replaced by actual production URL
  keycloakUrl: '/auth',     // Will be replaced by actual Keycloak URL
  keycloakRealm: 'bp-realm', // Updated to match auth-service1 configuration
  keycloakClientId: 'bp-client', // Updated to match auth-service1 configuration
  apiTimeout: 60000, // 60 seconds for production
  defaultPageSize: 20
};