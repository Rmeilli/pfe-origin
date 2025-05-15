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

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreFileGenerationServiceTest {

    @Mock
    private StructureCRERepository structureCRERepository;

    @InjectMocks
    private CreFileGenerationService creFileGenerationService;

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
        valeurCRE1.setLongCol(5);
        valeurCRE1.setDesignationBcp("TEST1");

        valeurCRE2 = new ValeurCRE();
        valeurCRE2.setId(2L);
        valeurCRE2.setNomZone("Zone2");
        valeurCRE2.setPos(2);
        valeurCRE2.setLongCol(3);
        valeurCRE2.setDesignationBcp("ABC");

        structure.setValeurs(Arrays.asList(valeurCRE1, valeurCRE2));
    }

    @Test
    void generateCreFileContent_WhenStructureExists_ShouldGenerateContent() {
        // Arrange
        when(structureCRERepository.findById(1L)).thenReturn(Optional.of(structure));

        // Act
        String result = creFileGenerationService.generateCreFileContent(1L, 2);

        // Assert
        assertNotNull(result);
        assertTrue(result.startsWith("Test Structure\n"));
        String[] lines = result.split("\n");
        assertEquals(3, lines.length); // Header + 2 lines
        assertEquals("TEST1ABC", lines[1].trim());
        assertEquals("TEST1ABC", lines[2].trim());
        verify(structureCRERepository, times(1)).findById(1L);
    }

    @Test
    void generateCreFileContent_WhenStructureNotExists_ShouldThrowException() {
        // Arrange
        when(structureCRERepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> creFileGenerationService.generateCreFileContent(999L, 1));
        assertEquals("Structure non trouvée avec ID: 999", exception.getMessage());
        verify(structureCRERepository, times(1)).findById(999L);
    }

    @Test
    void generateCreFileContent_WithEmptyDesignationBcp_ShouldUseSpaces() {
        // Arrange
        valeurCRE1.setDesignationBcp(null);
        valeurCRE2.setDesignationBcp("");
        when(structureCRERepository.findById(1L)).thenReturn(Optional.of(structure));

        // Act
        String result = creFileGenerationService.generateCreFileContent(1L, 1);

        // Assert
        assertNotNull(result);
        String[] lines = result.split("\n");
        assertEquals(2, lines.length); // Header + 1 line
        assertEquals("     ", lines[1].substring(0, 5)); // 5 spaces for valeurCRE1
        assertEquals("   ", lines[1].substring(5, 8)); // 3 spaces for valeurCRE2
        verify(structureCRERepository, times(1)).findById(1L);
    }

    @Test
    void generateCreFileContent_WithLongDesignationBcp_ShouldTruncate() {
        // Arrange
        valeurCRE1.setDesignationBcp("ThisIsTooLong");
        valeurCRE2.setDesignationBcp("AlsoTooLong");
        when(structureCRERepository.findById(1L)).thenReturn(Optional.of(structure));

        // Act
        String result = creFileGenerationService.generateCreFileContent(1L, 1);

        // Assert
        assertNotNull(result);
        String[] lines = result.split("\n");
        assertEquals(2, lines.length); // Header + 1 line
        assertEquals("ThisI", lines[1].substring(0, 5)); // Truncated to 5 chars
        assertEquals("Als", lines[1].substring(5, 8)); // Truncated to 3 chars
        verify(structureCRERepository, times(1)).findById(1L);
    }

    @Test
    void generateCreFileContent_WithZeroLines_ShouldGenerateOnlyHeader() {
        // Arrange
        when(structureCRERepository.findById(1L)).thenReturn(Optional.of(structure));

        // Act
        String result = creFileGenerationService.generateCreFileContent(1L, 0);

        // Assert
        assertNotNull(result);
        String[] lines = result.split("\n");
        assertEquals(1, lines.length); // Only header
        assertEquals("Test Structure", lines[0]);
        verify(structureCRERepository, times(1)).findById(1L);
    }
} 