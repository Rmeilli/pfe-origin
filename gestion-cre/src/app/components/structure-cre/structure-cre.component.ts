// import { Component, OnInit } from '@angular/core';
// //import { StructureCRE } from 'src/app/models/structure-cre.model';
// //import { StructureCREService } from 'src/app/services/structure-cre.service';
// import {StructureCRE} from "../../models/structure-cre.model";
// import {StructureCREService} from "../../services/structure-cre.service";
//
// @Component({
//   selector: 'app-structure-cre',
//   templateUrl: './structure-cre.component.html',
//   styleUrls: ['./structure-cre.component.css']
// })
// export class StructureCREComponent implements OnInit {
//   structures: StructureCRE[] = [];
//   newStructure: StructureCRE = { nomStructure: '' };
//   errorMessage: string = '';
//
//   selectedStructureId: number | null = null;
//   selectedStructureName: string | null = null;
//
//   // 'view' pour consulter, 'edit' pour modifier
//   mode: 'view' | 'edit' | null = null;
//
//   constructor(private structureService: StructureCREService) {}
//
//   ngOnInit(): void {
//     this.loadStructures();
//   }
//
//   loadStructures(): void {
//     this.structureService.getStructures().subscribe(
//       (data) => {
//         this.structures = data;
//       },
//       (error) => {
//         console.error('Erreur de chargement', error);
//       }
//     );
//   }
//
//   addStructure(): void {
//     if (!this.newStructure.nomStructure.trim()) {
//       this.errorMessage = 'Le nom de la structure est obligatoire !';
//       return;
//     }
//     this.structureService.addStructure(this.newStructure).subscribe(
//       (data) => {
//         this.structures.push(data);
//         this.newStructure.nomStructure = '';
//         this.errorMessage = '';
//       },
//       (error) => {
//         console.error('Erreur lors de l’ajout', error);
//         this.errorMessage = "Une erreur est survenue lors de l'ajout.";
//       }
//     );
//   }
//
//   // Consulter la structure (lecture seule)
//   consultStructure(id: number): void {
//     this.selectedStructureId = null; // Force la destruction du composant enfant
//     this.selectedStructureName=null;
//     setTimeout(() => {
//       this.selectedStructureId = id;// Recréera le composant enfant
//       this.mode = 'view';
//     });
//   }
//
//   // Modifier la structure (édition)
//   editStructure(id: number): void {
//     this.selectedStructureId = null; // Force la destruction du composant enfant
//     this.selectedStructureName=null;
//     setTimeout(() => {
//       this.selectedStructureId = id;
//       this.mode = 'edit';
//     });
//   }
//
// }

import { Component, OnInit } from '@angular/core';
// //import { StructureCRE } from 'src/app/models/structure-cre.model';
// //import { StructureCREService } from 'src/app/services/structure-cre.service';
import {StructureCRE} from "../../models/structure-cre.model";
import {StructureCREService} from "../../services/structure-cre.service";

@Component({
  selector: 'app-structure-cre',
  templateUrl: './structure-cre.component.html',
  styleUrls: ['./structure-cre.component.css']
})
export class StructureCREComponent implements OnInit {
  structures: StructureCRE[] = [];
  newStructure: StructureCRE = { nomStructure: '' };
  errorMessage: string = '';

  selectedStructureId: number | null = null;
  selectedStructureName: string | null = null;
  mode: 'view' | 'edit' | null = null;

  constructor(private structureService: StructureCREService) {}

  ngOnInit(): void {
    this.loadStructures();
  }

  loadStructures(): void {
    this.structureService.getStructures().subscribe(
      (data) => {
        this.structures = data;
      },
      (error) => {
        console.error('Erreur de chargement', error);
      }
    );
  }

  addStructure(): void {
    if (!this.newStructure.nomStructure.trim()) {
      this.errorMessage = 'Le nom de la structure est obligatoire !';
      return;
    }
    this.structureService.addStructure(this.newStructure).subscribe(
      (data) => {
        this.structures.push(data);
        this.newStructure.nomStructure = '';
        this.errorMessage = '';
      },
      (error) => {
        console.error('Erreur lors de l’ajout', error);
        this.errorMessage = "Une erreur est survenue lors de l'ajout.";
      }
    );
  }

  // Sélection pour consultation (view)
  consultStructure(structure: StructureCRE): void {
    if (structure.id !== undefined) {
      this.selectedStructureId = structure.id;
      this.selectedStructureName = structure.nomStructure;
      this.mode = 'view';
    } else {
      console.error('Structure ID is undefined');
      // Handle the error case, maybe show a message to the user
    }
  }


  // Sélection pour modification (edit)
  editStructure(structure: StructureCRE): void {
    if (structure.id !== undefined) {
      this.selectedStructureId = structure.id;
      this.selectedStructureName = structure.nomStructure;
      this.mode = 'edit';
    } else {
      console.error('Structure ID is undefined');
      // Handle the error case, maybe show a message to the user
    }
  }

  // Supprimer une structure
  deleteStructure(structure: StructureCRE): void {
    if (confirm(`Voulez-vous vraiment supprimer la structure "${structure.nomStructure}" ?`)) {
      this.structureService.deleteStructure(structure.id!).subscribe(
        () => {
          // Mise à jour de la liste
          this.structures = this.structures.filter(s => s.id !== structure.id);
          if (this.selectedStructureId === structure.id) {
            this.selectedStructureId = null;
            this.selectedStructureName = null;
            this.mode = null;
          }
        },
        (err) => {
          console.error('Erreur lors de la suppression', err);
        }
      );
    }
  }

  reloadStructures(): void {
    this.structureService.getStructures().subscribe(
      (data) => {
        this.structures = data;
      },
      (error) => console.error("Erreur lors du chargement des structures", error)
    );
  }

}

