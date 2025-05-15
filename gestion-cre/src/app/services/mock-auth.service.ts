import { Injectable } from '@angular/core';
import { Observable, of, BehaviorSubject } from 'rxjs';
import { delay } from 'rxjs/operators';

/**
 * Service d'authentification mock pour le développement
 * Contourne complètement le backend et simule une authentification réussie
 */
@Injectable({
  providedIn: 'root'
})
export class MockAuthService {
  private isAuthenticatedSubject = new BehaviorSubject<boolean>(false);
  private tokenSubject = new BehaviorSubject<string | null>(null);
  private useKeycloak = false; // Toujours en mode mock
  
  // Token JWT mock statique
  private mockToken = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6ImhhbXphIiwiaWF0IjoxNTE2MjM5MDIyLCJleHAiOjE2NDYyMzkwMjIsInJvbGVzIjpbIlJPTEVfVVNFUiJdfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c';
  
  // Méthodes pour la compatibilité avec AuthService
  toggleAuthMode(): void {
    console.log('Mode mock toujours actif, impossible de basculer');
  }
  
  getAuthMode(): string {
    return 'Mock';
  }

  constructor() {
    // Vérifier si un token est déjà stocké
    const storedToken = localStorage.getItem('auth_token');
    if (storedToken) {
      this.tokenSubject.next(storedToken);
      this.isAuthenticatedSubject.next(true);
    }
  }

  login(username: string, password: string): Observable<any> {
    console.log('MOCK AUTH: Simulation de connexion pour', username);
    
    // Stocker le token dans localStorage
    localStorage.setItem('auth_token', this.mockToken);
    
    // Mettre à jour les subjects
    this.tokenSubject.next(this.mockToken);
    this.isAuthenticatedSubject.next(true);
    
    // Simuler un délai réseau de 500ms
    return of({ token: this.mockToken }).pipe(delay(500));
  }

  logout(): Observable<any> {
    console.log('MOCK AUTH: Déconnexion');
    
    // Supprimer le token du localStorage
    localStorage.removeItem('auth_token');
    
    // Mettre à jour les subjects
    this.tokenSubject.next(null);
    this.isAuthenticatedSubject.next(false);
    
    return of({ success: true }).pipe(delay(300));
  }

  getCurrentUsername(): Observable<string> {
    return of('hamza');
  }

  getToken(): string | null {
    return this.tokenSubject.getValue();
  }

  isAuthenticated(): Observable<boolean> {
    return this.isAuthenticatedSubject.asObservable();
  }

  validateToken(): Observable<boolean> {
    return of(true);
  }

  getUserInfo(): Observable<any> {
    return of({
      sub: '1234567890',
      name: 'hamza',
      email: 'hamza@example.com',
      roles: ['ROLE_USER']
    });
  }
}
