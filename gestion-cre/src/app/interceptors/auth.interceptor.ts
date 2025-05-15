import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor
} from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  
  // Remove the AuthService injection
  constructor() {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    // Get token directly from localStorage instead of using AuthService
    const token = localStorage.getItem('auth_token'); // Utiliser la même clé que dans le MockAuthService
    
    if (token) {
      console.log('Intercepteur Auth: Ajout du token aux en-têtes');
      const cloned = request.clone({
        headers: request.headers.set('Authorization', `Bearer ${token}`)
      });
      return next.handle(cloned);
    }
    
    return next.handle(request);
  }
}