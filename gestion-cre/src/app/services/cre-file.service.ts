import { Injectable } from '@angular/core';
import { HttpClient, HttpEvent, HttpRequest, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { AuthService } from './auth.service';
import { HttpErrorResponse } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class CreFileService {
  private apiUrl = `${environment.creApiUrl}/api/crefiles`;

  constructor(private http: HttpClient, private authService: AuthService) {}

  private getHeaders(): HttpHeaders {
    const token = this.authService.getToken();
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
  }

  private getTextHeaders(): HttpHeaders {
    const token = this.authService.getToken();
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'text/plain'
    });
  }

  uploadFiles(structureId: number, files: File[]): Observable<any> {
    const formData: FormData = new FormData();
    formData.append('structureId', structureId.toString());
    files.forEach(file => {
      formData.append('files', file, file.name);
    });
    return this.http.post(`${this.apiUrl}/upload`, formData, {
      headers: new HttpHeaders({
        'Authorization': `Bearer ${this.authService.getToken()}`
      }),
      responseType: 'text'
    });
  }


  // generateCreFile(structureId: number): Observable<Blob> {
  //   return this.http.get(`${this.apiUrl}/generate/${structureId}`, { responseType: 'blob' });
  // }

  // downloadUploadedFile(fileName: string): Observable<Blob> {
  //   return this.http.get(`${this.apiUrl}/download/${fileName}`, { responseType: 'blob' });
  // }
  //
  // // Nouvelle méthode pour lister les fichiers disponibles
  // listFiles(structureId: number): Observable<string[]> {
  //   return this.http.get<string[]>(`${this.apiUrl}/list/${structureId}`);
  // }

  downloadUploadedFile(structureId: number, fileName: string): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/download/${structureId}/${fileName}`, { 
      headers: this.getHeaders(),
      responseType: 'blob' 
    });
  }

  // listFiles(structureId: number): Observable<string[]> {
  //   return this.http.get<string[]>(`${this.apiUrl}/list/${structureId}`);
  // }

  listFiles(structureId: number): Observable<string[]> {
    const url = `${this.apiUrl}/list/${structureId}?t=${new Date().getTime()}`;
    return this.http.get<string[]>(url, { headers: this.getHeaders() });
  }
  validateCreFile(structureId: number, file: File): Observable<string | string[]> {
    const formData = new FormData();
    formData.append('file', file);
    // Ajout des informations spécifiques sur le format CRE
    formData.append('startPosition', '227');
    formData.append('lineLength', '1001');
    
    return this.http.post(`${environment.creApiUrl}/api/crefiles/validate/${structureId}`, formData, { 
      headers: this.getHeaders(),
      responseType: 'text'
    });
  }

  generateCreFile(structureId: number, lines: number = 100): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/generate/${structureId}?lines=${lines}`, { 
      headers: this.getHeaders(),
      responseType: 'blob' 
    });
  }
  deleteFile(structureId: number, fileName: string): Observable<any> {
    return this.http.delete(`${this.apiUrl}/delete/${structureId}/${fileName}`, { headers: this.getHeaders() });
  }



}
