// Add ViewChild and ElementRef imports
import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { CreFileService } from '../../services/cre-file.service';
import { StructureCREService } from '../../services/structure-cre.service';
import { StructureCRE } from '../../models/structure-cre.model';

@Component({
  selector: 'app-cre-validation',
  templateUrl: './cre-validation.component.html',
  styleUrls: ['./cre-validation.component.css']
})
export class CreValidationComponent implements OnInit {
  // Add template reference
  @ViewChild('messageDiv') messageDiv?: ElementRef;
  // Liste des structures récupérées depuis le backend
  structures: StructureCRE[] = [];
  // ID et nom de la structure sélectionnée
  selectedStructureId?: number;
  structureName: string = '';

  // Pour la gestion du fichier CRE à valider
  selectedFile!: File;
  validationResult: any;
  // Modify message property with scroll logic
  private _message: string = '';
  get message(): string { return this._message; }
  set message(value: string) {
    this._message = value;
    this.scrollToMessage();
  }

  private scrollToMessage(): void {
    if (this.message && this.messageDiv) {
      setTimeout(() => {
        this.messageDiv?.nativeElement.scrollIntoView({
          behavior: 'smooth',
          block: 'start'
        });
      });
    }
  }

  // Liste des fichiers déjà chargés dans la structure
  availableFiles: string[] = [];

  constructor(
    private creFileService: CreFileService,
    private structureService: StructureCREService
  ) {}

  ngOnInit(): void {
    // Charger la liste des structures dès l'initialisation
    this.structureService.getStructures().subscribe({
      next: (data: StructureCRE[]) => {
        this.structures = data;
        if (this.structures.length > 0) {
          // Pré-sélectionner la première structure par défaut
          this.selectedStructureId = this.structures[0].id;
          this.structureName = this.structures[0].nomStructure;
          this.loadAvailableFiles();
        }
      },
      error: (err) => {
        console.error("Erreur lors du chargement des structures", err);
        this.message = "Erreur lors du chargement des structures.";
      }
    });
  }

  // Méthode appelée lorsque l'utilisateur change la structure dans le menu déroulant
  onStructureChange(): void {
    const selected = this.structures.find(s => s.id === this.selectedStructureId);
    if (selected) {
      this.structureName = selected.nomStructure;
      this.loadAvailableFiles();
    }
  }

  // Charger la liste des fichiers déjà chargés pour la structure sélectionnée
  loadAvailableFiles(): void {
    if (!this.selectedStructureId) return;
    this.creFileService.listFiles(this.selectedStructureId).subscribe({
      next: (files) => {
        this.availableFiles = files;
      },
      error: (err) => {
        console.error("Erreur lors du chargement des fichiers", err);
        this.message = "Erreur lors du chargement des fichiers.";
      }
    });
  }

  // Sélection du fichier CRE
  onFileSelected(event: any): void {
    this.selectedFile = event.target.files[0];
  }

  // Validation du fichier CRE par rapport à la structure sélectionnée
  validateFile(): void {
    if (!this.selectedStructureId) {
      this.message = "Veuillez sélectionner une structure.";
      return;
    }
    if (!this.selectedFile) {
      this.message = "Veuillez sélectionner un fichier CRE.";
      return;
    }
    const formData = new FormData();
    formData.append('file', this.selectedFile, this.selectedFile.name);

    this.creFileService.validateCreFile(this.selectedStructureId, this.selectedFile).subscribe({
      next: (res) => {
        this.validationResult = res;
        this.message = "Vérification terminée.";
        console.log("Résultat de validation:", res);
      },
      error: (err) => {
        console.error("Erreur lors de la vérification:", err);
        if (err.error) {
          this.message = "Erreur lors de la vérification du fichier: " + JSON.stringify(err.error);
        } else {
          this.message = "Erreur lors de la vérification du fichier.";
        }
      }
    });
  }

  // Upload du fichier CRE (si conforme ou non conforme)
  uploadFile(): void {
    if (!this.selectedFile) {
      this.message = "Aucun fichier sélectionné.";
      return;
    }
    if (!this.selectedStructureId) {
      this.message = "Veuillez sélectionner une structure.";
      return;
    }
    // Vérifier si le fichier existe déjà dans la structure
    if (this.availableFiles.includes(this.selectedFile.name)) {
      this.message = "Fichier déjà existant.";
      return;
    }

    this.creFileService.uploadFiles(this.selectedStructureId, [this.selectedFile]).subscribe({
      next: (res: any) => {
        this.message = res.message || "Fichier chargé avec succès.";
        this.loadAvailableFiles(); // Rafraîchir la liste des fichiers disponibles
      },
      error: (err) => {
        console.error("Erreur lors du chargement du fichier", err);
        this.message = "Erreur lors du chargement du fichier.";
      }
    });
  }

  // Annuler l'opération
  cancel(): void {
    this.selectedFile = undefined as any;
    this.validationResult = null;
    this.message = '';
  }

  protected readonly Array = Array;
}




