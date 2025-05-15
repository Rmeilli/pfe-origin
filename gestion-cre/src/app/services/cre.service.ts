import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class CreService {
  private apiUrl = `${environment.creApiUrl}/api`;

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {}

  /**
   * Crée les en-têtes avec le token d'authentification
   */
  private getHeaders(): HttpHeaders {
    const token = this.authService.getToken();
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    });
  }

  /**
   * Récupère la liste des CREs
   */
  getAllCres(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/cres`, {
      headers: this.getHeaders()
    });
  }

  /**
   * Récupère un CRE par son ID
   */
  getCre(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/cres/${id}`, {
      headers: this.getHeaders()
    });
  }

  /**
   * Crée un nouveau CRE
   */
  createCre(creData: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/cres`, creData, {
      headers: this.getHeaders()
    });
  }

  /**
   * Met à jour un CRE existant
   */
  updateCre(id: number, creData: any): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/cres/${id}`, creData, {
      headers: this.getHeaders()
    });
  }

  /**
   * Supprime un CRE
   */
  deleteCre(id: number): Observable<any> {
    return this.http.delete<any>(`${this.apiUrl}/cres/${id}`, {
      headers: this.getHeaders()
    });
  }

  /**
   * Télécharge un fichier CRE
   */
  downloadCre(fileId: number): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/cres/download/${fileId}`, {
      headers: new HttpHeaders({
        'Authorization': `Bearer ${this.authService.getToken()}`
      }),
      responseType: 'blob'
    });
  }

  /**
   * Téléverse un fichier CRE
   */
  uploadCre(file: File, metadata: any): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('metadata', JSON.stringify(metadata));

    return this.http.post(`${this.apiUrl}/cres/upload`, formData, {
      headers: new HttpHeaders({
        'Authorization': `Bearer ${this.authService.getToken()}`
      })
    });
  }
}
