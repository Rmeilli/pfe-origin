package org.sid.creapp.services;

import org.sid.creapp.Entities.StructureCRE;
import org.sid.creapp.repositories.StructureCRERepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class StructureCREService {
    private final StructureCRERepository structureCRERepository;

    public StructureCREService(StructureCRERepository structureCRERepository) {
        this.structureCRERepository = structureCRERepository;
    }

    public List<StructureCRE> getAllStructures() {
        return structureCRERepository.findAll();
    }

    public Optional<StructureCRE> getStructureById(Long id) {
        return structureCRERepository.findById(id);
    }

    public StructureCRE saveStructure(StructureCRE structureCRE) {
        return structureCRERepository.save(structureCRE);
    }

    @Transactional
    public void deleteStructure(Long id) {
        if (structureCRERepository.existsById(id)) {
            structureCRERepository.deleteById(id);
        } else {
            throw new RuntimeException("Structure CRE non trouvée avec l'ID: " + id);
        }
    }
    @Transactional
    public StructureCRE updateStructure(Long id, StructureCRE newStructure) {
        return structureCRERepository.findById(id).map(existingStructure -> {
            existingStructure.setNomStructure(newStructure.getNomStructure());
            return structureCRERepository.save(existingStructure);
        }).orElseThrow(() -> new RuntimeException("Structure CRE non trouvée avec l'ID: " + id));
    }

}