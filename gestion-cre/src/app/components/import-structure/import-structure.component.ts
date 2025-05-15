
///////////////////

import { Component, ViewChild, ElementRef, Output, EventEmitter } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-import-structure',
  templateUrl: './import-structure.component.html',
  styleUrls: ['./import-structure.component.css']
})
export class ImportStructureComponent {
  selectedFile!: File;
  preview: any;
  message: string = '';
  messageType: 'success' | 'error' = 'error';
  isLoading: boolean = false; // Ajout de la propriété isLoading

  @ViewChild('errorDiv') errorDiv!: ElementRef;
  @Output() importSuccess: EventEmitter<any> = new EventEmitter();

  constructor(private http: HttpClient) {}

  onFileSelected(event: any): void {
    this.selectedFile = event.target.files[0];
  }

  importPreview(): void {
    if (!this.selectedFile) {
      this.message = "Veuillez sélectionner un fichier Excel.";
      this.messageType = 'error';
      this.scrollToError();
      return;
    }

    // Activer l'indicateur de chargement
    this.isLoading = true;
    this.message = "Chargement de la prévisualisation...";
    this.messageType = 'success';

    const formData = new FormData();
    formData.append('file', this.selectedFile, this.selectedFile.name);

    const headers = new HttpHeaders()
      .set('Accept', 'application/json');

    this.http.post<any>(`${environment.creApiUrl}/api/structures/import/preview`, formData, {
      headers: headers,
      withCredentials: true,
      observe: 'response'
    }).subscribe({
      next: (response) => {
        this.preview = response.body;
        this.message = "Prévisualisation chargée.";
        this.messageType = 'success';
        this.isLoading = false; // Désactiver l'indicateur de chargement
      },
      error: (err) => {
        console.error('Erreur lors de l\'import:', err);
        this.message = "Erreur lors de la prévisualisation: " + (err.error?.message || 'Le fichier ne correspond probablement pas à une structure CRE.');
        this.messageType = 'error';
        this.isLoading = false; // Désactiver l'indicateur de chargement
        this.scrollToError();
      }
    });
  }

  // Mettre à jour également la méthode confirmImport pour gérer l'état de chargement
  confirmImport(): void {
    if (!this.preview) {
      this.message = "Aucune structure à importer.";
      this.messageType = 'error';
      this.scrollToError();
      return;
    }
    
    // Activer l'indicateur de chargement
    this.isLoading = true;
    this.message = "Importation en cours...";
    this.messageType = 'success';
    
    if (!this.preview.proposedName || this.preview.proposedName.trim() === '') {
      this.message = "Le nom de la structure est obligatoire.";
      this.messageType = 'error';
      this.scrollToError();
      return;
    }

    const headers = new HttpHeaders().set('Content-Type', 'application/json');

    this.http.post<any>(`${environment.creApiUrl}/api/structures/import/confirm`, this.preview, {
      headers: headers,
      withCredentials: true
    }).subscribe({
      next: (res) => {
        this.message = "Structure importée avec succès en base.";
        this.messageType = 'success';
        this.preview = null;
        this.importSuccess.emit();
      },
      error: (err) => {
        console.error('Erreur lors de la confirmation:', err);
        this.message = "Erreur lors de la confirmation d'import: " + (err.message || 'Une erreur inattendue est survenue.');
        this.messageType = 'error';
        this.scrollToError();
      }
    });
  }

  private scrollToError(): void {
    setTimeout(() => {
      if (this.errorDiv) {
        this.errorDiv.nativeElement.scrollIntoView({ behavior: 'smooth', block: 'start' });
      }
    }, 0);
  }

  // Méthode pour gérer le changement de longueur d'un champ
  onLongColChange(index: number, newValue: number): void {
    console.log(`Longueur du champ ${index} modifiée à ${newValue}`);
    
    // Mettre à jour la longueur du champ actuel
    this.preview.champs[index].longCol = newValue;
    
    // Calculer la position du champ suivant
    const nextPosition = this.preview.champs[index].pos + newValue;
    
    // Mettre à jour les positions de tous les champs suivants
    if (index < this.preview.champs.length - 1) {
      // Mettre à jour le champ suivant directement
      this.preview.champs[index + 1].pos = nextPosition;
      
      // Puis mettre à jour tous les autres champs en cascade
      let currentPos = nextPosition;
      for (let i = index + 1; i < this.preview.champs.length - 1; i++) {
        currentPos = this.preview.champs[i].pos + this.preview.champs[i].longCol;
        this.preview.champs[i + 1].pos = currentPos;
      }
    }
  }
  
  // Méthode pour gérer le changement de position d'un champ
  onPositionChange(index: number, newPosition: number): void {
    console.log(`Position du champ ${index} modifiée à ${newPosition}`);
    
    // Mettre à jour la position du champ actuel
    this.preview.champs[index].pos = newPosition;
    
    // Calculer la position du champ suivant
    const nextPosition = newPosition + this.preview.champs[index].longCol;
    
    // Mettre à jour les positions de tous les champs suivants
    if (index < this.preview.champs.length - 1) {
      // Mettre à jour le champ suivant directement
      this.preview.champs[index + 1].pos = nextPosition;
      
      // Puis mettre à jour tous les autres champs en cascade
      let currentPos = nextPosition;
      for (let i = index + 1; i < this.preview.champs.length - 1; i++) {
        currentPos = this.preview.champs[i].pos + this.preview.champs[i].longCol;
        this.preview.champs[i + 1].pos = currentPos;
      }
    }
  }
}




