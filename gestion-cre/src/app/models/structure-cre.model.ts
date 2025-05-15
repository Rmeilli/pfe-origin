export interface StructureCRE {
  id?: number;  // Optionnel car généré par la BD
  nomStructure: string;
  dateCreation?: string; // Changé de Date à string car c'est le format reçu du backend
  valeurs?: any[]; // Ajouté pour gérer les valeurs associées
}
