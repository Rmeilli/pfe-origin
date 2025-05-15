import { Injectable, PLATFORM_ID, Inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, BehaviorSubject, throwError, of, map } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { isPlatformBrowser } from '@angular/common';
import { environment } from "../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = `${environment.authApiUrl}/api/direct`;
  private isAuthenticatedSubject = new BehaviorSubject<boolean>(false);
  private tokenSubject = new BehaviorSubject<string | null>(null);
  private isBrowser: boolean;

  constructor(
    private http: HttpClient,
    @Inject(PLATFORM_ID) platformId: Object
  ) {
    this.isBrowser = isPlatformBrowser(platformId);

    if (this.isBrowser) {
      this.isAuthenticatedSubject = new BehaviorSubject<boolean>(this.hasToken());
      this.tokenSubject = new BehaviorSubject<string | null>(this.getStoredToken());

      if (this.hasToken()) {
        this.validateToken().subscribe();
      }
    }
  }

  private getStoredToken(): string | null {
    if (this.isBrowser) {
      return localStorage.getItem('auth_token');
    }
    return null;
  }

  private hasToken(): boolean {
    if (this.isBrowser) {
      return !!localStorage.getItem('auth_token');
    }
    return false;
  }



  // Getter pour le mode d'authentification
  get authMode(): string {
    return this._authMode;
  }

  // Setter pour le mode d'authentification
  set authMode(mode: string) {
    this._authMode = mode;
  }

  // Méthode pour basculer entre les modes
  // toggleAuthMode(): void {
  //   // Rotation entre les modes
  //   if (this._authMode === 'keycloak') {
  //     this._authMode = 'mock';
  //   } else if (this._authMode === 'mock') {
  //     this._authMode = 'emergency';
  //   } else if (this._authMode === 'emergency') {
  //     this._authMode = 'keycloak1';
  //   } else {
  //     this._authMode = 'keycloak';
  //   }
  //   console.log(`Mode d'authentification basculé vers: ${this._authMode}`);
  // }

  // Getter pour connaître le mode actuel
  // Remplacer la propriété authMode et ses getters/setters
  private _authMode: string = 'keycloak1'; // Toujours utiliser keycloak1 (Nouveau)

  getAuthMode(): string {
    return 'keycloak1';
  }

  // Supprimer la méthode toggleAuthMode ou la rendre inactive
  toggleAuthMode(): void {
    // Ne rien faire, nous utilisons uniquement keycloak1
    console.log('Mode d\'authentification fixé sur Keycloak (Nouveau)');
  }

  // Simplifier la méthode login pour n'utiliser que keycloak1
  login(username: string, password: string): Observable<any> {
    const httpOptions = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
        'Accept': 'application/json',
        'X-Requested-With': 'XMLHttpRequest'
      }),
      withCredentials: false
    };

    // Utiliser uniquement l'URL pour keycloak1
    const loginUrl = `${environment.authApiUrl1}/api/auth/login`;

    console.log(`Tentative de connexion à: ${loginUrl}`);
    console.log('Payload envoyé:', JSON.stringify({ username, password }));

    return this.http.post<any>(loginUrl, { username, password }, httpOptions).pipe(
      tap(response => {
        console.log('Réponse du serveur reçue:', response);
        const token = response.access_token || response.token;
        if (token) {
          if (this.isBrowser) {
            localStorage.setItem('auth_token', token);
          }
          this.tokenSubject.next(token);
          this.isAuthenticatedSubject.next(true);
        } else {
          console.error('Aucun token trouvé dans la réponse:', response);
        }
      }),
      catchError(error => {
        console.error('Erreur de connexion:', error);

        if (error.status === 0) {
          console.error('Erreur CORS ou serveur inaccessible. Vérifiez que le backend est démarré.');
        } else if (error.status === 401) {
          console.error('Identifiants incorrects ou problème d\'authentification au backend');
          return throwError(() => new Error('Identifiants incorrects.'));
        }

        return throwError(() => new Error('Échec de la connexion. Veuillez vérifier vos identifiants ou réessayer plus tard.'));
      })
    );
  }

  register(username: string, password: string): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/register`, { username, password }).pipe(
      catchError(error => {
        console.error('Erreur d\'inscription:', error);
        return throwError(() => new Error('Échec de l\'inscription. Veuillez réessayer.'));
      })
    );
  }

  logout(): Observable<any> {
    console.log('Auth service: logging out user');
    const token = this.getStoredToken();

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    if (this.isBrowser) {
      localStorage.removeItem('auth_token');
    }
    this.tokenSubject.next(null);
    this.isAuthenticatedSubject.next(false);
    console.log('Token removed from storage');

    return this.http.post<any>(`${this.apiUrl}/logout`, {}, { headers }).pipe(
      tap(() => {
        console.log('Logout API call successful');
      }),
      catchError(error => {
        console.error('Logout API error:', error);
        return throwError(() => new Error('Logout API call failed'));
      })
    );
  }

  getCurrentUsername(): Observable<string> {
    return this.getUserInfo().pipe(
      map(userInfo => userInfo.username || 'Utilisateur'),
      catchError(error => {
        console.error('Error getting username:', error);
        return of('Utilisateur');
      })
    );
  }

  getToken(): string | null {
    return this.tokenSubject.getValue();
  }

  isAuthenticated(): Observable<boolean> {
    return this.isAuthenticatedSubject.asObservable();
  }

  validateToken(): Observable<boolean> {
    const token = this.getToken();
    if (!token) {
      this.isAuthenticatedSubject.next(false);
      return new Observable<boolean>(observer => {
        observer.next(false);
        observer.complete();
      });
    }

    return this.http.get<boolean>(`${this.apiUrl}/validate-auth`, {
      headers: new HttpHeaders({
        'Authorization': `Bearer ${token}`
      })
    }).pipe(
      tap(isValid => {
        this.isAuthenticatedSubject.next(isValid);
        if (!isValid) {
          this.logout();
        }
      }),
      catchError(error => {
        console.error('Erreur de validation du token:', error);
        this.logout();
        return new Observable<boolean>(observer => {
          observer.next(false);
          observer.complete();
        });
      })
    );
  }

  getUserInfo(): Observable<any> {
    const token = this.getStoredToken();
    if (!token) {
      return throwError(() => new Error('Aucun token disponible'));
    }

    console.log('Getting user info with token:', token);

    // URL pour récupérer les informations utilisateur en fonction du mode
    let userInfoUrl = '';

    switch(this.authMode) {
      case 'keycloak':
        userInfoUrl = `${environment.authApiUrl}/api/auth/me`;
        break;
      case 'keycloak1':
        userInfoUrl = `${environment.authApiUrl1}/api/auth/me`;
        break;
      case 'mock':
        userInfoUrl = `${environment.authApiUrl}/api/direct/me`;
        break;
      case 'emergency':
        userInfoUrl = `${environment.authApiUrl}/emergency/me`;
        break;
      default:
        userInfoUrl = `${environment.authApiUrl}/emergency/me`;
    }

    console.log('Récupération des informations utilisateur depuis:', userInfoUrl);

    return this.http.get(userInfoUrl, {
      headers: new HttpHeaders({
        'Authorization': `Bearer ${token}`
      })
    }).pipe(
      tap(userInfo => console.log('User info received:', userInfo)),
      catchError(error => {
        console.error('Error getting user info:', error);
        try {
          const tokenParts = token.split('.');
          if (tokenParts.length === 3) {
            const payload = JSON.parse(atob(tokenParts[1]));
            console.log('Decoded token payload:', payload);
            return of(payload);
          }
        } catch (e) {
          console.error('Error decoding token:', e);
        }
        return throwError(() => new Error('Impossible de récupérer les informations utilisateur'));
      })
    );
  }
}
