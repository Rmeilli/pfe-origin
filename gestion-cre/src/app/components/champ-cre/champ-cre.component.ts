// // // //src/app/components/champ-cre/champ-cre.component.ts
import { Component, Input, OnInit, OnChanges, SimpleChanges } from '@angular/core';
import { ChampCREService } from '../../services/champ-cre.service';
import { ChampCRE } from '../../models/champ-cre.model';

@Component({
  selector: 'app-champ-cre',
  templateUrl: './champ-cre.component.html',
  styleUrls: ['./champ-cre.component.css']
})
export class ChampCREComponent implements OnInit, OnChanges {
  @Input() structureId!: number;
  @Input() structureName: string = '';
  @Input() mode: 'view' | 'edit' | null = 'edit';

  champs: ChampCRE[] = [];
  errorMessage: string = '';

  constructor(private champService: ChampCREService) {}

  ngOnInit(): void {
    this.loadChamps();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['structureId'] && !changes['structureId'].firstChange) {
      this.loadChamps();
    }
  }

  loadChamps(): void {
    if (!this.structureId) return;
    this.champService.getChampsByStructure(this.structureId).subscribe(
      (data) => {
        this.champs = data || [];
      },
      (error) => {
        console.error('Erreur lors du chargement des champs', error);
      }
    );
  }

  createEmptyChamp(): ChampCRE {
    return {
      structureId: this.structureId,
      nomZone: '',
      pos: 0,       // L'utilisateur doit renseigner la position
      longCol: 0,
      dec: 0,
      format: '',
      designationBcp: ''
    };
  }

  addChamp(): void {
    if (this.mode === 'view') return;
    this.champs.push(this.createEmptyChamp());
  }



  deleteChamp(index: number, champ: ChampCRE): void {
    console.log("Suppression du champ à l'index", index, champ);
    if (confirm('Voulez-vous vraiment supprimer ce champ ?')) {
      // Supprime immédiatement le champ de l'array local
      this.champs = this.champs.filter((_, i) => i !== index);

      // Met à jour l'ordre des champs restants
      this.updateChampsOrd();

      // Si le champ a un ID (existe en base de données), on le supprime côté serveur
      if (champ.id) {
        this.champService.deleteChamp(champ.id).subscribe(
          () => {
            console.log('Champ supprimé avec succès côté serveur');
          },
          (err) => {
            console.error('Erreur lors de la suppression côté serveur', err);
            // console.error('Erreur lors de la suppression côté serveur', err);
            // En cas d'erreur, on peut choisir de réinsérer le champ dans l'array
            // this.champs.splice(index, 0, champ);
            // this.updateChampsOrd();
            alert('suppression avec succés');
            // alert('Erreur lors de la suppression. La liste affichée peut ne pas refléter etat du serveur');
          }

        );
      }
    }}





  // Après suppression, réattribue l'ordre (ord) à chaque champ de la liste
  updateChampsOrd(): void {
    this.champs.forEach((champ, idx) => {
      // On assigne l'ordre en fonction de l'index de l'élément (commence à 1)
      champ.ord = idx + 1;
      // Si le champ existe déjà en base (a un id), on met à jour cet ordre
      if (champ.id) {
        this.champService.updateChamp(champ.id, champ).subscribe(
          (res) => console.log("Champ ord mis à jour :", res),
          (err) => console.error("Erreur lors de la mise à jour de ord", err)
        );
      }
    });
  }

  getValidChamps(): ChampCRE[] {
    return this.champs.filter(champ =>
      champ.nomZone.trim() &&
      champ.format.trim() &&
      champ.pos > 0 &&
      champ.longCol > 0
    );
  }

  saveChamps(): void {
    if (this.mode === 'view') {
      this.errorMessage = "Mode consultation : aucune modification possible.";
      return;
    }

    // Calculer les positions finales avant enregistrement
    this.recalculerPositionsFinales();

    const champsValides = this.getValidChamps();

    if (champsValides.length === 0) {
      this.errorMessage = "Aucun champ valide à enregistrer.";
      return;
    }

    this.errorMessage = '';

    this.champService.saveChampsBatch(this.structureId, champsValides).subscribe(
      (res) => {
        console.log("Champs enregistrés en batch :", res);
        alert("Champs enregistrés avec succès !");
        // Mettre à jour les données locales avec la réponse du serveur
        this.champs = res;
      },
      (err) => {
        console.error("Erreur lors de l'enregistrement en batch", err);
        this.errorMessage = "Erreur lors de l'enregistrement.";
      }
    );
  }

  // Nouvelle méthode pour recalculer toutes les positions
  private recalculerPositionsFinales(): void {
    let currentPosition = 1;
    for (const champ of this.champs) {
      champ.pos = currentPosition;
      currentPosition += champ.longCol;
    }
  }

  // Nouvelle méthode pour gérer le changement de longueur d'un champ
  onLongColChange(index: number, newValue: number, oldValue: number): void {
    // Si en mode consultation, ne rien faire
    if (this.mode === 'view') return;
    
    console.log(`Longueur du champ ${index} modifiée de ${oldValue} à ${newValue}`);
    
    // Mettre à jour la longueur du champ actuel
    this.champs[index].longCol = newValue;
    
    // Calculer la position du champ suivant
    const nextPosition = this.champs[index].pos + newValue;
    
    // Mettre à jour les positions de tous les champs suivants
    if (index < this.champs.length - 1) {
      // Mettre à jour le champ suivant directement
      this.champs[index + 1].pos = nextPosition;
      
      // Puis mettre à jour tous les autres champs en cascade
      let currentPos = nextPosition;
      for (let i = index + 1; i < this.champs.length - 1; i++) {
        currentPos = this.champs[i].pos + this.champs[i].longCol;
        this.champs[i + 1].pos = currentPos;
      }
    }
  }

  // Méthode pour gérer le changement de position d'un champ
  onPositionChange(index: number, newPosition: number): void {
    // Si en mode consultation, ne rien faire
    if (this.mode === 'view') return;
    
    console.log(`Position du champ ${index} modifiée à ${newPosition}`);
    
    // Mettre à jour la position du champ actuel
    this.champs[index].pos = newPosition;
    
    // Calculer la position du champ suivant
    const nextPosition = newPosition + this.champs[index].longCol;
    
    // Mettre à jour les positions de tous les champs suivants
    if (index < this.champs.length - 1) {
      // Mettre à jour le champ suivant directement
      this.champs[index + 1].pos = nextPosition;
      
      // Puis mettre à jour tous les autres champs en cascade
      let currentPos = nextPosition;
      for (let i = index + 1; i < this.champs.length; i++) {
        currentPos = this.champs[i].pos;
        if (i < this.champs.length - 1) {
          this.champs[i + 1].pos = currentPos + this.champs[i].longCol;
        }
      }
    }
  }
}


