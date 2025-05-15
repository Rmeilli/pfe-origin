package org.sid.creapp.Controllers;


import org.sid.creapp.DTO.StructurePreview;
import org.sid.creapp.Entities.StructureCRE;
import org.sid.creapp.Entities.ValeurCRE;
import org.sid.creapp.repositories.StructureCRERepository;
import org.sid.creapp.services.ExcelImportService;
import org.sid.creapp.services.StructureCREService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/structures")
// Ajouter une configuration CORS plus restrictive
@CrossOrigin(origins = {"http://localhost:4200", "https://votre-domaine-de-production.com"})
public class StructureCREController {
    private final StructureCRERepository structureCRERepository;
    private final StructureCREService structureCREService;
    private final ExcelImportService excelImportService;

    public StructureCREController(StructureCRERepository structureCRERepository, StructureCREService structureCREService, ExcelImportService excelImportService) {
        this.structureCRERepository = structureCRERepository;
        this.structureCREService = structureCREService;
        this.excelImportService = excelImportService;
    }

   // @GetMapping
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<StructureCRE> getAllStructures() {
        // Supprimer les instructions de débogage en production
        return structureCREService.getAllStructures();
    }

    @PostMapping
    public StructureCRE createStructure(@RequestBody StructureCRE structureCRE) {
        return structureCREService.saveStructure(structureCRE);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStructure(@PathVariable("id") Long id) {
        structureCREService.deleteStructure(id);
        return ResponseEntity.noContent().build();
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<StructureCRE> updateStructure(@PathVariable("id") Long id, @RequestBody StructureCRE updatedStructure) {
        StructureCRE updated = structureCREService.updateStructure(id, updatedStructure);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/import")
    public ResponseEntity<?> importStructures(@RequestParam("file") MultipartFile file) {
        try {
            List<StructureCRE> structures = excelImportService.parseExcelFile(file);
            structures.forEach(structure -> System.out.println("Import de : " + structure.getNomStructure()));

            // Sauvegarder toutes les structures importées (et leurs champs associés)
            List<StructureCRE> savedStructures = structureCRERepository.saveAll(structures);
            return ResponseEntity.ok(savedStructures);
        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de l'importation : " + e.getMessage());
        }
    }
    
    @PostMapping("/import/preview")
    public ResponseEntity<StructurePreview> importStructurePreview(@RequestParam("file") MultipartFile file) {
        // Supprimer les instructions de débogage en production
        try {
            List<StructureCRE> structures = excelImportService.parseExcelFile(file);
            if (structures.isEmpty()) {
                return ResponseEntity.badRequest().body(null);
            }

            StructureCRE structure = structures.get(0);
            StructurePreview preview = new StructurePreview();
            preview.setProposedName(structure.getNomStructure());
            preview.setChamps(structure.getValeurs());

            return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(preview);
        } catch (Exception e) {
            // Utiliser le logger au lieu de System.err pour les erreurs
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @PostMapping("/import/confirm")
    public ResponseEntity<StructureCRE> confirmImport(@RequestBody StructurePreview preview) {
        try {
            // Créer la structure
            StructureCRE structure = new StructureCRE();
            structure.setNomStructure(preview.getProposedName());
            structure.setDateCreation(LocalDateTime.now());

            // Associer les champs
            List<ValeurCRE> champs = preview.getChamps();
            for (ValeurCRE champ : champs) {
                champ.setStructure(structure);
            }
            structure.setValeurs(champs);

            // Sauvegarder en base
            StructureCRE savedStructure = structureCRERepository.save(structure);
            return ResponseEntity.ok(savedStructure);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
