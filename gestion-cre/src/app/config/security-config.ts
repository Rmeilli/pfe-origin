import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from '../services/auth.service';

@Injectable()
export class SecurityConfig implements HttpInterceptor {
  constructor(private authService: AuthService) {}

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    console.log('Intercepting request to:', request.url);
    
    // Vérification plus précise pour les endpoints d'authentification
    const isLoginRequest = request.url.endsWith('/api/auth/login') || request.url.endsWith('/login');
    const isRegisterRequest = request.url.endsWith('/api/auth/register') || request.url.endsWith('/register');
    
    // Skip authentication for auth endpoints
    if (isLoginRequest || isRegisterRequest) {
      console.log('Skipping auth token for login/register request');
      return next.handle(request);
    }

    // Add token to other requests
    const token = this.authService.getToken();
    if (token) {
      console.log('Adding auth token to request:', request.url);
      const authReq = request.clone({
        headers: request.headers.set('Authorization', `Bearer ${token}`)
      });
      return next.handle(authReq);
    }
    
    return next.handle(request);
  }
}