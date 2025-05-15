
import { Component, Input, OnInit, OnChanges, SimpleChanges } from '@angular/core';
import { CreFileService } from '../../services/cre-file.service';
import { ChangeDetectorRef } from '@angular/core';

@Component({
  selector: 'app-cre-file-flow',
  templateUrl: './cre-file-flow.component.html',
  styleUrls: ['./cre-file-flow.component.css']
})
export class CreFileFlowComponent implements OnInit, OnChanges {
  @Input() structureId!: number;
  @Input() structureName: string = '';
  @Input() mode: 'view' | 'edit' | null = 'edit';

  selectedFile!: File;
  availableFiles: string[] = [];
  selectedFileName: string = '';

  isValidated: boolean = false;
  validationResult: any = null;
  validationMessage: string = '';
  message: string = '';

  constructor(private creFileService: CreFileService, private cd: ChangeDetectorRef) {}

  ngOnInit(): void {
    if (this.structureId) {
      this.loadAvailableFiles();
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['structureId'] && !changes['structureId'].isFirstChange()) {
      this.loadAvailableFiles();
    }
  }

  onFileSelected(event: any): void {
    this.selectedFile = event.target.files[0];
    this.resetValidation();
  }

  private resetValidation(): void {
    this.isValidated = false;
    this.validationResult = null;
    this.validationMessage = '';
    this.message = '';
  }

  validateFile(): void {
    if (!this.selectedFile) {
      this.message = "Veuillez sélectionner un fichier CRE.";
      return;
    }
    this.creFileService.validateCreFile(this.structureId, this.selectedFile).subscribe({
      next: (res) => {
        this.isValidated = true;
        if (typeof res === 'string' && res === 'Fichier conforme') {
          this.validationResult = 'OK';
          this.validationMessage = "Fichier conforme";
        } else if (Array.isArray(res)) {
          this.validationResult = res;
          this.validationMessage = "Fichier invalide";
        } else {
          this.validationResult = res;
          this.validationMessage = "Réponse inattendue du serveur";
        }
        this.cd.detectChanges();
      },
      error: (err) => {
        console.error("Erreur lors de la validation:", err);
        this.message = "Erreur lors de la validation du fichier.";
      }
    });
  }

  uploadFile(): void {
    if (!this.selectedFile) {
      this.message = "Aucun fichier sélectionné.";
      return;
    }

    const existingFileName = this.availableFiles.find(file => file === this.selectedFile.name);
    if (existingFileName) {
      this.message = "Le fichier existe déjà.";
      return;
    }

    this.creFileService.uploadFiles(this.structureId, [this.selectedFile]).subscribe({
      next: () => {
        this.message = "Fichier chargé avec succès.";
        this.loadAvailableFiles();
      },
      error: (err) => {
        console.error("Erreur lors du chargement:", err);
        this.message = "Erreur lors du chargement du fichier.";
      }
    });
  }

  loadAvailableFiles(): void {
    this.creFileService.listFiles(this.structureId).subscribe({
      next: (files) => {
        this.availableFiles = files;
        if (files.length > 0) {
          this.selectedFileName = files[0];
        }
        this.cd.detectChanges();
      },
      error: (err) => {
        console.error("Erreur lors du chargement des fichiers disponibles:", err);
      }
    });
  }

  downloadFile(): void {
    if (!this.selectedFileName) {
      this.message = "Veuillez sélectionner un fichier à télécharger.";
      return;
    }
    this.creFileService.downloadUploadedFile(this.structureId, this.selectedFileName).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = this.selectedFileName;
        a.click();
        window.URL.revokeObjectURL(url);
      },
      error: (err) => {
        console.error("Erreur lors du téléchargement", err);
        this.message = "Erreur lors du téléchargement du fichier.";
      }
    });
  }

  generateCreFile(): void {
    if (!this.structureId) {
      this.message = "Sélectionnez une structure.";
      return;
    }
    this.creFileService.generateCreFile(this.structureId).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `CRE_structure_${this.structureName}.txt`;
        a.click();
        window.URL.revokeObjectURL(url);
      },
      error: (err) => {
        console.error("Erreur lors de la génération du fichier CRE", err);
        this.message = "Erreur lors de la génération du fichier CRE.";
      }
    });
  }

  cancel(): void {
    this.resetValidation();
    this.selectedFile = undefined as any;
  }

  deleteFile(filename: string): void {
    const confirmed = confirm(`Êtes-vous sûr de vouloir supprimer le fichier "${filename}" ?`);
    if (!confirmed) return;

    this.creFileService.deleteFile(this.structureId, filename).subscribe({
      next: () => {
        this.message = `Fichier "${filename}" supprimé avec succès.`;
        this.loadAvailableFiles();
      },
      error: (err) => {
        console.error("Erreur lors de la suppression du fichier:", err);
        this.message = "Erreur lors de la suppression du fichier.";
      }
    });
  }

  protected readonly Array = Array;
}





