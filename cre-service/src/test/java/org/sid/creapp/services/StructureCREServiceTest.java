package org.sid.creapp.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sid.creapp.Entities.StructureCRE;
import org.sid.creapp.repositories.StructureCRERepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StructureCREServiceTest {

    @Mock
    private StructureCRERepository structureCRERepository;

    @InjectMocks
    private StructureCREService structureCREService;

    private StructureCRE structureCRE1;
    private StructureCRE structureCRE2;

    @BeforeEach
    void setUp() {
        structureCRE1 = new StructureCRE();
        structureCRE1.setId(1L);
        structureCRE1.setNomStructure("Structure 1");

        structureCRE2 = new StructureCRE();
        structureCRE2.setId(2L);
        structureCRE2.setNomStructure("Structure 2");
    }

    @Test
    void getAllStructures_ShouldReturnAllStructures() {
        // Arrange
        when(structureCRERepository.findAll()).thenReturn(Arrays.asList(structureCRE1, structureCRE2));

        // Act
        List<StructureCRE> structures = structureCREService.getAllStructures();

        // Assert
        assertNotNull(structures);
        assertEquals(2, structures.size());
        verify(structureCRERepository, times(1)).findAll();
    }

    @Test
    void getStructureById_WhenStructureExists_ShouldReturnStructure() {
        // Arrange
        when(structureCRERepository.findById(1L)).thenReturn(Optional.of(structureCRE1));

        // Act
        Optional<StructureCRE> result = structureCREService.getStructureById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(structureCRE1.getNomStructure(), result.get().getNomStructure());
        verify(structureCRERepository, times(1)).findById(1L);
    }

    @Test
    void getStructureById_WhenStructureDoesNotExist_ShouldReturnEmpty() {
        // Arrange
        when(structureCRERepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<StructureCRE> result = structureCREService.getStructureById(999L);

        // Assert
        assertTrue(result.isEmpty());
        verify(structureCRERepository, times(1)).findById(999L);
    }

    @Test
    void saveStructure_ShouldReturnSavedStructure() {
        // Arrange
        when(structureCRERepository.save(any(StructureCRE.class))).thenReturn(structureCRE1);

        // Act
        StructureCRE savedStructure = structureCREService.saveStructure(structureCRE1);

        // Assert
        assertNotNull(savedStructure);
        assertEquals(structureCRE1.getNomStructure(), savedStructure.getNomStructure());
        verify(structureCRERepository, times(1)).save(structureCRE1);
    }

    @Test
    void deleteStructure_WhenStructureExists_ShouldDeleteStructure() {
        // Arrange
        when(structureCRERepository.existsById(1L)).thenReturn(true);
        doNothing().when(structureCRERepository).deleteById(1L);

        // Act & Assert
        assertDoesNotThrow(() -> structureCREService.deleteStructure(1L));
        verify(structureCRERepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteStructure_WhenStructureDoesNotExist_ShouldThrowException() {
        // Arrange
        when(structureCRERepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> structureCREService.deleteStructure(999L));
        assertEquals("Structure CRE non trouvée avec l'ID: 999", exception.getMessage());
        verify(structureCRERepository, never()).deleteById(any());
    }

    @Test
    void updateStructure_WhenStructureExists_ShouldUpdateStructure() {
        // Arrange
        StructureCRE updatedStructure = new StructureCRE();
        updatedStructure.setNomStructure("Updated Structure");
        
        when(structureCRERepository.findById(1L)).thenReturn(Optional.of(structureCRE1));
        when(structureCRERepository.save(any(StructureCRE.class))).thenReturn(structureCRE1);

        // Act
        StructureCRE result = structureCREService.updateStructure(1L, updatedStructure);

        // Assert
        assertNotNull(result);
        verify(structureCRERepository, times(1)).findById(1L);
        verify(structureCRERepository, times(1)).save(any(StructureCRE.class));
    }

    @Test
    void updateStructure_WhenStructureDoesNotExist_ShouldThrowException() {
        // Arrange
        when(structureCRERepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> structureCREService.updateStructure(999L, new StructureCRE()));
        assertEquals("Structure CRE non trouvée avec l'ID: 999", exception.getMessage());
        verify(structureCRERepository, never()).save(any());
    }
}