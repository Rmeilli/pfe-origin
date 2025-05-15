//org/sid/creapp/Controllers/ValeurCREController.java
package org.sid.creapp.Controllers;

import org.sid.creapp.Entities.ValeurCRE;
import org.sid.creapp.repositories.ValeurCRERepository;
import org.sid.creapp.services.ValeurCREService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

/**
 * Controller for managing ValeurCRE entities
 */
@RestController
@RequestMapping("/api/valeurs")
// Remplacer @CrossOrigin(origins = "*") par une configuration plus restrictive
@CrossOrigin(origins = {"http://localhost:4200", "https://domaine-de-production.com"})
public class ValeurCREController {
    private static final Logger logger = LoggerFactory.getLogger(ValeurCREController.class);
    private final ValeurCREService valeurCREService;
    private final ValeurCRERepository valeurCRERepository;

    public ValeurCREController(ValeurCREService valeurCREService, ValeurCRERepository valeurCRERepository) {
        this.valeurCREService = valeurCREService;
        this.valeurCRERepository = valeurCRERepository;
    }

    /**
     * Get all values for a specific structure
     * @param structureId The ID of the structure
     * @return List of ValeurCRE sorted by position
     */
    @GetMapping("/structure/{structureId}")
    public ResponseEntity<?> getValeursByStructure(
            @PathVariable(name = "structureId", required = true) Long structureId) {
        try {
            logger.info("Fetching values for structure ID: {}", structureId);
            List<ValeurCRE> valeurs = valeurCREService.getValeursByStructure(structureId);
            valeurs.sort(Comparator.comparingInt(ValeurCRE::getPos));
            return ResponseEntity.ok(valeurs);
        } catch (Exception e) {
            logger.error("Error fetching values for structure ID {}: {}", structureId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching values: " + e.getMessage());
        }
    }

    /**
     * Add a new value to a structure
     * @param structureId The ID of the structure
     * @param valeurCRE The value to add
     * @return The saved value
     */
    @PostMapping("/structure/{structureId}")
    public ResponseEntity<?> addValeursToStructure(
            @PathVariable(name = "structureId", required = true) Long structureId,
            @RequestBody ValeurCRE valeurCRE) {
        try {
            logger.info("Adding new value to structure ID: {}", structureId);
            ValeurCRE savedChamp = valeurCREService.addValeursToStructure(structureId, valeurCRE);
            return ResponseEntity.ok(savedChamp);
        } catch (Exception e) {
            logger.error("Error adding value to structure ID {}: {}", structureId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error adding value: " + e.getMessage());
        }
    }

    /**
     * Delete a value by its ID
     * @param id The ID of the value to delete
     * @return Success message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteValeurById(
            @PathVariable(name = "id", required = true) Long id) {
        try {
            logger.info("Deleting value with ID: {}", id);
            valeurCREService.deleteValeurById(id);
            return ResponseEntity.ok("Valeur CRE supprimée avec succès");
        } catch (Exception e) {
            logger.error("Error deleting value with ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting value: " + e.getMessage());
        }
    }

    /**
     * Delete all values for a structure
     * @param structureId The ID of the structure
     * @return Success message
     */
    @DeleteMapping("/structure/{structureId}")
    public ResponseEntity<?> deleteValeursByStructure(
            @PathVariable(name = "structureId", required = true) Long structureId) {
        try {
            logger.info("Deleting all values for structure ID: {}", structureId);
            valeurCREService.deleteValeursByStructure(structureId);
            return ResponseEntity.ok("Toutes les valeurs CRE de la structure ont été supprimées avec succès");
        } catch (Exception e) {
            logger.error("Error deleting values for structure ID {}: {}", structureId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting values: " + e.getMessage());
        }
    }

    /**
     * Update a value
     * @param valeurId The ID of the value to update
     * @param updatedValeur The updated value data
     * @return The updated value
     */
    @PutMapping("/{valeurId}")
    public ResponseEntity<?> updateValeur(
            @PathVariable(name = "valeurId", required = true) Long valeurId,
            @RequestBody ValeurCRE updatedValeur) {
        try {
            logger.info("Updating value with ID: {}", valeurId);
            ValeurCRE valeur = valeurCREService.updateValeur(valeurId, updatedValeur);
            return ResponseEntity.ok(valeur);
        } catch (Exception e) {
            logger.error("Error updating value with ID {}: {}", valeurId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating value: " + e.getMessage());
        }
    }

    /**
     * Save multiple values for a structure in batch
     * @param structureId The ID of the structure
     * @param champs List of values to save
     * @return List of saved values
     */
    @PostMapping("/batch/structure/{structureId}")
    public ResponseEntity<?> saveChampsBatch(
            @PathVariable(name = "structureId", required = true) Long structureId,
            @RequestBody List<ValeurCRE> champs) {
        try {
            logger.info("Saving batch of values for structure ID: {}", structureId);
            
            // Validation des champs requis
            for (ValeurCRE champ : champs) {
                if (champ.getNomZone() == null || champ.getNomZone().trim().isEmpty()) {
                    return ResponseEntity.badRequest().body("Le nom de zone est requis pour tous les champs");
                }
                if (champ.getPos() <= 0) {
                    return ResponseEntity.badRequest().body("La position doit être supérieure à 0");
                }
                if (champ.getLongCol() <= 0) {
                    return ResponseEntity.badRequest().body("La longueur doit être supérieure à 0");
                }
                if (champ.getFormat() == null || champ.getFormat().trim().isEmpty()) {
                    return ResponseEntity.badRequest().body("Le format est requis pour tous les champs");
                }
                // Si dec n'est pas fourni, on le met à 0 par défaut
                if (champ.getDec() < 0) {
                    champ.setDec(0);
                }
            }//

            List<ValeurCRE> savedChamps = valeurCREService.saveAllChamps(structureId, champs);
            return ResponseEntity.ok(savedChamps);
        } catch (Exception e) {
            logger.error("Error saving batch of values for structure ID {}: {}", structureId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error saving values: " + e.getMessage());
        }
    }
}









