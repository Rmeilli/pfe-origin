package org.sid.creapp.repositories;

import org.sid.creapp.Entities.StructureCRE;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StructureCRERepository extends JpaRepository<StructureCRE, Long> {
    List<StructureCRE> findAll();


}
