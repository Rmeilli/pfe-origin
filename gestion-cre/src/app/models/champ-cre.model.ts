export interface ChampCRE {
  id?: number;  // Optionnel car généré par la BD
  structureId: number;  // Référence à la structure
  nomZone: string;
  pos: number;
  longCol: number;
  dec?: number;  // Optionnel
  format: string;
  designationBcp?: string;  // Optionnel
  ord?: number; // Ajoutez cette ligne pour la propriété "ord"

}
