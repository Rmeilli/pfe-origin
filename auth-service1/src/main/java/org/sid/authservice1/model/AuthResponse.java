package org.sid.authservice1.model;

public class AuthResponse {
    private String access_token;
    private String refresh_token;
    private long expires_in;
    private String token_type;
    
    public AuthResponse() {
    }
    
    public AuthResponse(String access_token, String refresh_token, long expires_in, String token_type) {
        this.access_token = access_token;
        this.refresh_token = refresh_token;
        this.expires_in = expires_in;
        this.token_type = token_type;
    }
    
    public String getAccess_token() {
        return access_token;
    }
    
    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }
    
    public String getRefresh_token() {
        return refresh_token;
    }
    
    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }
    
    public long getExpires_in() {
        return expires_in;
    }
    
    public void setExpires_in(long expires_in) {
        this.expires_in = expires_in;
    }
    
    public String getToken_type() {
        return token_type;
    }
    
    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private String access_token;
        private String refresh_token;
        private long expires_in;
        private String token_type;
        
        public Builder access_token(String access_token) {
            this.access_token = access_token;
            return this;
        }
        
        public Builder refresh_token(String refresh_token) {
            this.refresh_token = refresh_token;
            return this;
        }
        
        public Builder expires_in(long expires_in) {
            this.expires_in = expires_in;
            return this;
        }
        
        public Builder token_type(String token_type) {
            this.token_type = token_type;
            return this;
        }
        
        public AuthResponse build() {
            return new AuthResponse(access_token, refresh_token, expires_in, token_type);
        }
    }
}
