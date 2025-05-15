package org.sid.creapp.repositories;

import jakarta.persistence.LockModeType;
import org.sid.creapp.Entities.ValeurCRE;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ValeurCRERepository extends JpaRepository<ValeurCRE, Long> {

    @Query("SELECT COALESCE(MAX(v.ord), 0) FROM ValeurCRE v WHERE v.structure.id = :structureId")
    Integer findMaxOrdByStructureId(@Param("structureId") Long structureId);

    // Optionnel : version avec verrouillage pour éviter les doublons
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT COALESCE(MAX(v.ord), 0) FROM ValeurCRE v WHERE v.structure.id = :structureId")
    Integer findMaxOrdByStructureIdWithLock(@Param("structureId") Long structureId);

    // Récupérer les champs en forçant un tri par 'ord' puis 'id' en cas d'égalité
    @Query("SELECT v FROM ValeurCRE v WHERE v.structure.id = :structureId ORDER BY v.ord, v.id")
    List<ValeurCRE> findByStructureIdOrdered(@Param("structureId") Long structureId);

    // Méthode existante
    List<ValeurCRE> findByStructureId(Long structureId);

    @Modifying
    @Transactional
    @Query("DELETE FROM ValeurCRE v WHERE v.structure.id = :structureId")
    void deleteByStructureId(@Param("structureId") Long structureId);

    @Modifying
    @Transactional
    @Query("DELETE FROM ValeurCRE v WHERE v.id = :id")
    void deleteByIdCustom(@Param("id") Long id);

}
