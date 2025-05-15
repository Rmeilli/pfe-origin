package org.sid.creapp.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sid.creapp.Entities.StructureCRE;
import org.sid.creapp.Entities.ValeurCRE;
import org.sid.creapp.repositories.StructureCRERepository;
import org.sid.creapp.repositories.ValeurCRERepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ValeurCREServiceTest {

    @Mock
    private ValeurCRERepository valeurCRERepository;

    @Mock
    private StructureCRERepository structureCRERepository;

    @InjectMocks
    private ValeurCREService valeurCREService;

    private StructureCRE structure;
    private ValeurCRE valeurCRE1;
    private ValeurCRE valeurCRE2;

    @BeforeEach
    void setUp() {
        structure = new StructureCRE();
        structure.setId(1L);
        structure.setNomStructure("Test Structure");

        valeurCRE1 = new ValeurCRE();
        valeurCRE1.setId(1L);
        valeurCRE1.setNomZone("Zone1");
        valeurCRE1.setPos(1);
        valeurCRE1.setLongCol(10);
        valeurCRE1.setStructure(structure);

        valeurCRE2 = new ValeurCRE();
        valeurCRE2.setId(2L);
        valeurCRE2.setNomZone("Zone2");
        valeurCRE2.setPos(2);
        valeurCRE2.setLongCol(20);
        valeurCRE2.setStructure(structure);
    }

    @Test
    void getValeursByStructure_ShouldReturnValeurs() {
        // Arrange
        when(valeurCRERepository.findByStructureId(1L)).thenReturn(Arrays.asList(valeurCRE1, valeurCRE2));

        // Act
        List<ValeurCRE> result = valeurCREService.getValeursByStructure(1L);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(valeurCRERepository, times(1)).findByStructureId(1L);
    }

    @Test
    void saveAllChamps_WithNewChamps_ShouldSaveAll() {
        // Arrange
        when(structureCRERepository.findById(1L)).thenReturn(Optional.of(structure));
        when(valeurCRERepository.findByStructureId(1L)).thenReturn(Arrays.asList());
        when(valeurCRERepository.saveAll(any())).thenReturn(Arrays.asList(valeurCRE1, valeurCRE2));

        // Act
        List<ValeurCRE> result = valeurCREService.saveAllChamps(1L, Arrays.asList(valeurCRE1, valeurCRE2));

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(structureCRERepository, times(1)).findById(1L);
        verify(valeurCRERepository, times(1)).saveAll(any());
    }

    @Test
    void saveAllChamps_WithExistingChamps_ShouldUpdateAll() {
        // Arrange
        ValeurCRE existingValeur = new ValeurCRE();
        existingValeur.setId(1L);
        existingValeur.setNomZone("Old Zone");
        existingValeur.setStructure(structure);

        when(structureCRERepository.findById(1L)).thenReturn(Optional.of(structure));
        when(valeurCRERepository.findByStructureId(1L)).thenReturn(Arrays.asList(existingValeur));
        when(valeurCRERepository.saveAll(any())).thenReturn(Arrays.asList(valeurCRE1));

        // Act
        List<ValeurCRE> result = valeurCREService.saveAllChamps(1L, Arrays.asList(valeurCRE1));

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(structureCRERepository, times(1)).findById(1L);
        verify(valeurCRERepository, times(1)).saveAll(any());
    }

    @Test
    void deleteValeurById_WhenExists_ShouldDelete() {
        // Arrange
        when(valeurCRERepository.existsById(1L)).thenReturn(true);
        doNothing().when(valeurCRERepository).deleteByIdCustom(1L);

        // Act & Assert
        assertDoesNotThrow(() -> valeurCREService.deleteValeurById(1L));
        verify(valeurCRERepository, times(1)).deleteByIdCustom(1L);
    }

    @Test
    void deleteValeurById_WhenNotExists_ShouldThrowException() {
        // Arrange
        when(valeurCRERepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> valeurCREService.deleteValeurById(999L));
        assertEquals("Erreur lors de la suppression: Valeur CRE non trouvée avec ID: 999", exception.getMessage());
        verify(valeurCRERepository, times(1)).existsById(999L);
        verify(valeurCRERepository, never()).deleteByIdCustom(any());
    }

    @Test
    void deleteValeursByStructure_WhenStructureExists_ShouldDeleteAll() {
        // Arrange
        when(structureCRERepository.existsById(1L)).thenReturn(true);
        doNothing().when(valeurCRERepository).deleteByStructureId(1L);

        // Act & Assert
        assertDoesNotThrow(() -> valeurCREService.deleteValeursByStructure(1L));
        verify(valeurCRERepository, times(1)).deleteByStructureId(1L);
    }

    @Test
    void deleteValeursByStructure_WhenStructureNotExists_ShouldThrowException() {
        // Arrange
        when(structureCRERepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> valeurCREService.deleteValeursByStructure(999L));
        assertEquals("Erreur lors de la suppression: Structure CRE non trouvée avec ID: 999", exception.getMessage());
        verify(structureCRERepository, times(1)).existsById(999L);
        verify(valeurCRERepository, never()).deleteByStructureId(any());
    }

    @Test
    void updateValeur_WhenExists_ShouldUpdate() {
        // Arrange
        ValeurCRE updatedValeur = new ValeurCRE();
        updatedValeur.setNomZone("Updated Zone");
        updatedValeur.setPos(5);
        updatedValeur.setLongCol(15);

        when(valeurCRERepository.findById(1L)).thenReturn(Optional.of(valeurCRE1));
        when(valeurCRERepository.save(any())).thenReturn(updatedValeur);

        // Act
        ValeurCRE result = valeurCREService.updateValeur(1L, updatedValeur);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Zone", result.getNomZone());
        verify(valeurCRERepository, times(1)).findById(1L);
        verify(valeurCRERepository, times(1)).save(any());
    }

    @Test
    void updateValeur_WhenNotExists_ShouldThrowException() {
        // Arrange
        ValeurCRE valeur = new ValeurCRE();
        valeur.setId(999L);
        when(valeurCRERepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> valeurCREService.updateValeur(999L, valeur));
        assertEquals("Erreur lors de la mise à jour: Valeur CRE non trouvée avec ID: 999", exception.getMessage());
        verify(valeurCRERepository, times(1)).findById(999L);
    }

    @Test
    void addValeursToStructure_WhenStructureExists_ShouldAddValeur() {
        // Arrange
        ValeurCRE newValeur = new ValeurCRE();
        newValeur.setNomZone("New Zone");

        when(structureCRERepository.findById(1L)).thenReturn(Optional.of(structure));
        when(valeurCRERepository.findMaxOrdByStructureId(1L)).thenReturn(1);
        when(valeurCRERepository.save(any())).thenReturn(newValeur);

        // Act
        ValeurCRE result = valeurCREService.addValeursToStructure(1L, newValeur);

        // Assert
        assertNotNull(result);
        assertEquals("New Zone", result.getNomZone());
        verify(structureCRERepository, times(1)).findById(1L);
        verify(valeurCRERepository, times(1)).save(any());
    }

    @Test
    void addValeursToStructure_WhenStructureNotExists_ShouldThrowException() {
        // Arrange
        ValeurCRE valeur = new ValeurCRE();
        when(structureCRERepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> valeurCREService.addValeursToStructure(999L, valeur));
        assertEquals("Erreur lors de l'ajout: Structure non trouvée avec ID: 999", exception.getMessage());
        verify(structureCRERepository, times(1)).findById(999L);
    }
} 