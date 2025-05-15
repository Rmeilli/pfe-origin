package org.sid.creapp.services;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.sid.creapp.Entities.StructureCRE;
import org.sid.creapp.Entities.ValeurCRE;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ExcelImportService {

    /**
     * Parse le fichier Excel de manière flexible :
     *  - Recherche la ligne d'en-tête (Ord, Nom zone, etc.)
     *  - Détermine l'index de chaque colonne
     *  - Lit les lignes suivantes jusqu'à une ligne vide
     *  - Crée une structureCRE avec un nom vide (proposedName = "")
     *  - Rassemble les champs extraits dans structure.valeurs
     */
    public List<StructureCRE> parseExcelFile(MultipartFile file) {
        List<StructureCRE> structures = new ArrayList<>();

        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            // Création du FormulaEvaluator pour évaluer les formules
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();

            System.out.println("Début du parsing du fichier Excel : " + file.getOriginalFilename());
            System.out.println("Nombre de feuilles dans le fichier : " + workbook.getNumberOfSheets());

            // Parcours de chaque feuille
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                System.out.println("Traitement de la feuille " + i + " : " + sheet.getSheetName());
                if (sheet == null) continue;

                // 1) Trouver la ligne d'en-tête
                Row headerRow = findHeaderRow(sheet);
                if (headerRow == null) {
                    System.out.println("Impossible de trouver une ligne d'en-tête sur la feuille " + i);
                    // On crée quand même une structure vide
                    structures.add(createEmptyStructure(new ArrayList<>()));
                    continue;
                }
                System.out.println("Ligne d'en-tête trouvée à l'index : " + headerRow.getRowNum());

                // 2) Déterminer l'index de chaque colonne
                Map<String, Integer> columnIndexes = getColumnIndexes(headerRow);
                // Récupérer l'index de chaque colonne attendue
                Integer ordIndex  = columnIndexes.get("ORD");
                Integer nomZoneIndex = columnIndexes.get("NOM ZONE");
                Integer posIndex  = columnIndexes.get("POS");
                Integer longIndex = columnIndexes.get("LONG");
                Integer decIndex  = columnIndexes.get("DEC"); // "DÉC" => removeAccents
                Integer formatIndex = columnIndexes.get("FORMAT");
                Integer designationIndex = columnIndexes.get("DESIGNATION BCP");

                // 3) Lire les lignes de données après la ligne d'en-tête
                List<ValeurCRE> champs = new ArrayList<>();
                int ord = 1;
                for (int r = headerRow.getRowNum() + 1; r <= sheet.getLastRowNum(); r++) {
                    Row row = sheet.getRow(r);
                    if (row == null || isRowEmpty(row, evaluator)) {
                        System.out.println("Fin des enregistrements à la ligne " + r);
                        break;
                    }

                    // Lire chaque colonne via son index
                    String ordValue        = getCellValueAsString(row.getCell(ordIndex), evaluator);
                    String nomZoneValue    = getCellValueAsString(row.getCell(nomZoneIndex), evaluator);

                    double posValue        = getCellValueAsNumeric(row.getCell(posIndex), evaluator);
                    System.out.println("Cell pos (row=" + r + ", col=" + posIndex + ") => " +
                            (row.getCell(posIndex) != null ? row.getCell(posIndex).toString() : "null"));

                    double longValue       = getCellValueAsNumeric(row.getCell(longIndex), evaluator);
                    double decValue        = getCellValueAsNumeric(row.getCell(decIndex), evaluator);
                    String formatValue     = getCellValueAsString(row.getCell(formatIndex), evaluator);
                    String designationValue= getCellValueAsString(row.getCell(designationIndex), evaluator);

                    System.out.println("Ligne " + r + " => " +
                            "ord=" + ordValue + ", nomZone=" + nomZoneValue +
                            ", pos=" + posValue + ", longCol=" + longValue +
                            ", dec=" + decValue + ", format=" + formatValue +
                            ", designation=" + designationValue);

                    ValeurCRE champ = new ValeurCRE();
                    // On peut ignorer ordValue si on veut recalculer l'ordre
                    champ.setOrd(ord++);
                    champ.setNomZone(nomZoneValue);
                    champ.setPos((int) posValue);
                    champ.setLongCol((int) longValue);
                    champ.setDec((int) decValue);
                    champ.setFormat(formatValue);
                    champ.setDesignationBcp(designationValue);

                    champs.add(champ);
                }

                // Créer une structure avec un nom vide (l'utilisateur le renseignera plus tard)
                StructureCRE structure = createEmptyStructure(champs);
                structures.add(structure);
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du parsing du fichier Excel : " + e.getMessage());
            throw new RuntimeException("Erreur lors du parsing du fichier Excel : " + e.getMessage());
        }
        System.out.println("Nombre total de structures extraites : " + structures.size());
        return structures;
    }

    /**
     * Cherche la ligne d'en-tête (Ord, Nom zone, Pos, Long, Déc, Format, Désignation BCP)
     * dans les 20 premières lignes de la feuille.
     */
    private Row findHeaderRow(Sheet sheet) {
        for (int r = 0; r < 20; r++) {
            Row row = sheet.getRow(r);
            if (row == null) continue;
            if (containsAllHeaders(row)) {
                return row;
            }
        }
        return null;
    }

    /**
     * Vérifie si la ligne contient tous les noms de colonnes obligatoires.
     */
    private boolean containsAllHeaders(Row row) {
        // On définit la liste des en-têtes attendues
        // (sans accents, en majuscules)
        Set<String> required = new HashSet<>(Arrays.asList(
                "ORD", "NOM ZONE", "POS", "LONG", "DEC", "FORMAT", "DESIGNATION BCP"
        ));

        short lastCellNum = row.getLastCellNum();
        for (int c = 0; c < lastCellNum; c++) {
            Cell cell = row.getCell(c);
            if (cell != null) {
                String val = removeAccents(cell.getStringCellValue().trim().toUpperCase());
                // Ex: "DÉC" => "DEC", "DÉSIGNATION BCP" => "DESIGNATION BCP"
                if (required.contains(val)) {
                    required.remove(val);
                }
            }
        }
        return required.isEmpty();
    }

    /**
     * Extrait l'index de chaque colonne (Ord, Nom zone, etc.) depuis la ligne d'en-tête.
     */
    private Map<String, Integer> getColumnIndexes(Row headerRow) {
        Map<String, Integer> columnIndexes = new HashMap<>();
        short lastCellNum = headerRow.getLastCellNum();
        for (int c = 0; c < lastCellNum; c++) {
            Cell cell = headerRow.getCell(c);
            if (cell != null) {
                String val = removeAccents(cell.getStringCellValue().trim().toUpperCase());
                columnIndexes.put(val, c);
            }
        }
        return columnIndexes;
    }

    /**
     * Méthode utilitaire pour retirer les accents (ex: "DÉC" => "DEC").
     */
    private String removeAccents(String s) {
        if (s == null) return "";
        return Normalizer.normalize(s, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
    }

    /**
     * Vérifie si une ligne est vide (toutes les cellules vides).
     */
    private boolean isRowEmpty(Row row, FormulaEvaluator evaluator) {
        if (row == null) {
            return true;
        }
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != CellType.BLANK
                    && !getCellValueAsString(cell, evaluator).trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Convertit la cellule en String, en évaluant les formules si nécessaire.
     */
    private String getCellValueAsString(Cell cell, FormulaEvaluator evaluator) {
        if (cell == null) return "";
        // Évaluer la formule si besoin
        if (cell.getCellType() == CellType.FORMULA) {
            cell = evaluator.evaluateInCell(cell);
        }

        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue();
        } else if (cell.getCellType() == CellType.NUMERIC) {
            return String.valueOf(cell.getNumericCellValue());
        }
        return "";
    }

    /**
     * Convertit la cellule en double, en évaluant les formules si nécessaire.
     */
    private double getCellValueAsNumeric(Cell cell, FormulaEvaluator evaluator) {
        if (cell == null) return 0;
        // Évaluer la formule si besoin
        if (cell.getCellType() == CellType.FORMULA) {
            cell = evaluator.evaluateInCell(cell);
        }

        if (cell.getCellType() == CellType.NUMERIC) {
            return cell.getNumericCellValue();
        } else if (cell.getCellType() == CellType.STRING) {
            try {
                return Double.parseDouble(cell.getStringCellValue());
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }

    /**
     * Crée une StructureCRE vide (nom vide) avec les champs passés en paramètre.
     */
    private StructureCRE createEmptyStructure(List<ValeurCRE> champs) {
        StructureCRE structure = new StructureCRE();
        structure.setNomStructure(""); // L'utilisateur le saisira
        structure.setDateCreation(LocalDateTime.now());
        structure.setValeurs(champs);
        return structure;
    }
}