import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { StructureCRE } from '../models/structure-cre.model';
import { environment } from '../../environments/environment';
import { AuthService } from './auth.service';


@Injectable({
  providedIn: 'root'
})
export class StructureCREService {
  private apiUrl = `${environment.creApiUrl}/api/structures`;

  constructor(private http: HttpClient, private authService: AuthService) {}

  // Obtenir les en-têtes avec token d'authentification
  private getHeaders(): HttpHeaders {
    const token = this.authService.getToken();
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    });
  }

  // Ajouter une structure (avec en-tête JSON)
  addStructure(structure: StructureCRE): Observable<StructureCRE> {
    return this.http.post<StructureCRE>(this.apiUrl, structure, { headers: this.getHeaders() });
  }

  // Récupérer les structures existantes
  getStructures(): Observable<StructureCRE[]> {
    return this.http.get<StructureCRE[]>(this.apiUrl, { headers: this.getHeaders() });
  }
  // Méthode pour supprimer une structure par son id
  deleteStructure(structureId: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${structureId}`, { headers: this.getHeaders() });
  }

  // Méthode pour obtenir une structure par son id
  getStructureById(structureId: number): Observable<StructureCRE> {
    return this.http.get<StructureCRE>(`${this.apiUrl}/${structureId}`, { headers: this.getHeaders() });
  }

  // Méthode pour mettre à jour une structure
  updateStructure(structureId: number, structure: StructureCRE): Observable<StructureCRE> {
    return this.http.put<StructureCRE>(`${this.apiUrl}/${structureId}`, structure, { headers: this.getHeaders() });
  }
}
