package org.sid.creapp.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.sid.creapp.Entities.StructureCRE;
import org.sid.creapp.Entities.ValeurCRE;
import org.sid.creapp.repositories.StructureCRERepository;
import org.sid.creapp.repositories.ValeurCRERepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ValeurCREService {
    private static final Logger logger = LoggerFactory.getLogger(ValeurCREService.class);
    
    @PersistenceContext
    private EntityManager entityManager;
    private final ValeurCRERepository valeurCRERepository;
    private final StructureCRERepository structureCRERepository;

    public ValeurCREService(ValeurCRERepository valeurCRERepository, StructureCRERepository structureCRERepository) {
        this.valeurCRERepository = valeurCRERepository;
        this.structureCRERepository = structureCRERepository;
    }

    public List<ValeurCRE> getValeursByStructure(Long structureId) {
        return valeurCRERepository.findByStructureId(structureId);
    }

    @Transactional
    public List<ValeurCRE> saveAllChamps(Long structureId, List<ValeurCRE> champs) {
        try {
            logger.debug("Début de saveAllChamps pour structure {}", structureId);
            
            // Récupérer la structure associée
            StructureCRE structure = structureCRERepository.findById(structureId)
                    .orElseThrow(() -> new RuntimeException("Structure non trouvée avec ID: " + structureId));

            // Récupérer tous les champs existants de la structure
            List<ValeurCRE> existingFields = valeurCRERepository.findByStructureId(structureId);
            Map<Long, ValeurCRE> existingFieldsMap = existingFields.stream()
                    .collect(Collectors.toMap(ValeurCRE::getId, field -> field));

            List<ValeurCRE> champsToSave = new ArrayList<>();
            
            for (ValeurCRE champ : champs) {
                // Initialiser les valeurs par défaut
                if (champ.getDec() == null) champ.setDec(0);
                if (champ.getOrd() == null) champ.setOrd(0);
                if (champ.getLigne() == null) champ.setLigne(0);
                
                // Définir la structure pour chaque champ
                champ.setStructure(structure);

                if (champ.getId() != null && existingFieldsMap.containsKey(champ.getId())) {
                    // Mise à jour d'un champ existant
                    ValeurCRE existingField = existingFieldsMap.get(champ.getId());
                    existingField.setNomZone(champ.getNomZone());
                    existingField.setPos(champ.getPos());
                    existingField.setLongCol(champ.getLongCol());
                    existingField.setDec(champ.getDec());
                    existingField.setFormat(champ.getFormat());
                    existingField.setDesignationBcp(champ.getDesignationBcp());
                    existingField.setOrd(champ.getOrd());
                    existingField.setLigne(champ.getLigne());
                    champsToSave.add(existingField);
                } else {
                    // Nouveau champ
                    champ.setId(null); // Assurer que c'est traité comme un nouveau champ
                    champsToSave.add(champ);
                }
            }

            // Sauvegarder tous les champs
            List<ValeurCRE> savedFields = valeurCRERepository.saveAll(champsToSave);
            logger.debug("Fin de saveAllChamps: {} champs sauvegardés", savedFields.size());
            return savedFields;

        } catch (Exception e) {
            logger.error("Erreur lors de la sauvegarde des champs pour la structure {}: {}", structureId, e.getMessage());
            throw new RuntimeException("Erreur lors de la sauvegarde des champs: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void deleteValeurById(Long id) {
        try {
            if (!valeurCRERepository.existsById(id)) {
                throw new RuntimeException("Valeur CRE non trouvée avec ID: " + id);
            }
            valeurCRERepository.deleteByIdCustom(id);
            valeurCRERepository.flush();
        } catch (Exception e) {
            logger.error("Erreur lors de la suppression de la valeur {}: {}", id, e.getMessage());
            throw new RuntimeException("Erreur lors de la suppression: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void deleteValeursByStructure(Long structureId) {
        try {
            if (!structureCRERepository.existsById(structureId)) {
                throw new RuntimeException("Structure CRE non trouvée avec ID: " + structureId);
            }
            valeurCRERepository.deleteByStructureId(structureId);
        } catch (Exception e) {
            logger.error("Erreur lors de la suppression des valeurs pour la structure {}: {}", structureId, e.getMessage());
            throw new RuntimeException("Erreur lors de la suppression: " + e.getMessage(), e);
        }
    }

    @Transactional
    public ValeurCRE updateValeur(Long valeurId, ValeurCRE updatedValeur) {
        try {
            return valeurCRERepository.findById(valeurId).map(existing -> {
                existing.setNomZone(updatedValeur.getNomZone());
                existing.setPos(updatedValeur.getPos());
                existing.setLongCol(updatedValeur.getLongCol());
                existing.setDec(updatedValeur.getDec() != null ? updatedValeur.getDec() : 0);
                existing.setFormat(updatedValeur.getFormat());
                existing.setDesignationBcp(updatedValeur.getDesignationBcp());
                return valeurCRERepository.save(existing);
            }).orElseThrow(() -> new RuntimeException("Valeur CRE non trouvée avec ID: " + valeurId));
        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour de la valeur {}: {}", valeurId, e.getMessage());
            throw new RuntimeException("Erreur lors de la mise à jour: " + e.getMessage(), e);
        }
    }

    @Transactional
    public ValeurCRE addValeursToStructure(Long structureId, ValeurCRE valeurCRE) {
        try {
            return structureCRERepository.findById(structureId).map(structure -> {
                valeurCRE.setStructure(structure);
                Integer maxOrd = valeurCRERepository.findMaxOrdByStructureId(structureId);
                int nextOrd = (maxOrd != null) ? maxOrd + 1 : 1;
                valeurCRE.setOrd(nextOrd);
                return valeurCRERepository.save(valeurCRE);
            }).orElseThrow(() -> new RuntimeException("Structure non trouvée avec ID: " + structureId));
        } catch (Exception e) {
            logger.error("Erreur lors de l'ajout de la valeur à la structure {}: {}", structureId, e.getMessage());
            throw new RuntimeException("Erreur lors de l'ajout: " + e.getMessage(), e);
        }
    }
}