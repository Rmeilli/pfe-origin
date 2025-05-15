package org.sid.creapp.Controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.sid.creapp.DTO.StructurePreview;
import org.sid.creapp.Entities.StructureCRE;
import org.sid.creapp.Entities.ValeurCRE;
import org.sid.creapp.repositories.StructureCRERepository;
import org.sid.creapp.services.ExcelImportService;
import org.sid.creapp.services.StructureCREService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class StructureCREControllerTest {

    @Mock
    private StructureCRERepository structureCRERepository;

    @Mock
    private StructureCREService structureCREService;

    @Mock
    private ExcelImportService excelImportService;

    @InjectMocks
    private StructureCREController structureCREController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllStructures() {
        // Arrange
        List<StructureCRE> expectedStructures = Arrays.asList(
                new StructureCRE(1L, "Structure 1", LocalDateTime.now(), new ArrayList<>()),
                new StructureCRE(2L, "Structure 2", LocalDateTime.now(), new ArrayList<>())
        );
        when(structureCREService.getAllStructures()).thenReturn(expectedStructures);

        // Act
        List<StructureCRE> actualStructures = structureCREController.getAllStructures();

        // Assert
        assertEquals(expectedStructures, actualStructures);
        verify(structureCREService, times(1)).getAllStructures();
    }

    @Test
    void createStructure() {
        // Arrange
        StructureCRE inputStructure = new StructureCRE(null, "New Structure", LocalDateTime.now(), new ArrayList<>());
        StructureCRE savedStructure = new StructureCRE(1L, "New Structure", LocalDateTime.now(), new ArrayList<>());
        when(structureCREService.saveStructure(any(StructureCRE.class))).thenReturn(savedStructure);

        // Act
        StructureCRE result = structureCREController.createStructure(inputStructure);

        // Assert
        assertNotNull(result);
        assertEquals(savedStructure.getId(), result.getId());
        assertEquals(savedStructure.getNomStructure(), result.getNomStructure());
        verify(structureCREService, times(1)).saveStructure(any(StructureCRE.class));
    }

    @Test
    void deleteStructure() {
        // Arrange
        Long structureId = 1L;
        doNothing().when(structureCREService).deleteStructure(structureId);

        // Act
        ResponseEntity<Void> response = structureCREController.deleteStructure(structureId);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(structureCREService, times(1)).deleteStructure(structureId);
    }

    @Test
    void updateStructure() {
        // Arrange
        Long structureId = 1L;
        StructureCRE updatedStructure = new StructureCRE(structureId, "Updated Structure", LocalDateTime.now(), new ArrayList<>());
        when(structureCREService.updateStructure(eq(structureId), any(StructureCRE.class))).thenReturn(updatedStructure);

        // Act
        ResponseEntity<StructureCRE> response = structureCREController.updateStructure(structureId, updatedStructure);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(updatedStructure.getId(), response.getBody().getId());
        assertEquals(updatedStructure.getNomStructure(), response.getBody().getNomStructure());
        verify(structureCREService, times(1)).updateStructure(eq(structureId), any(StructureCRE.class));
    }

    @Test
    void importStructurePreview_Success() throws Exception {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.xlsx", MediaType.APPLICATION_OCTET_STREAM_VALUE, "test data".getBytes()
        );
        
        List<ValeurCRE> valeurs = new ArrayList<>();
        ValeurCRE valeur = new ValeurCRE();
        valeur.setNomZone("Champ1");  // Correct field: nomZone
        valeur.setDesignationBcp("Valeur1");  // Correct field: designationBcp
        valeurs.add(valeur);
        
        StructureCRE structure = new StructureCRE(1L, "Test Structure", LocalDateTime.now(), valeurs);
        List<StructureCRE> structures = Arrays.asList(structure);
        
        when(excelImportService.parseExcelFile(any())).thenReturn(structures);

        // Act
        ResponseEntity<StructurePreview> response = structureCREController.importStructurePreview(file);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Test Structure", response.getBody().getProposedName());
        assertEquals(valeurs, response.getBody().getChamps());
    }

    @Test
    void confirmImport_Success() {
        // Arrange
        StructurePreview preview = new StructurePreview();
        preview.setProposedName("Test Structure");
        
        List<ValeurCRE> champs = new ArrayList<>();
        ValeurCRE valeur = new ValeurCRE();
        valeur.setNomZone("Champ1");  // Correct field: nomZone
        valeur.setDesignationBcp("Valeur1");  // Correct field: designationBcp
        champs.add(valeur);
        preview.setChamps(champs);
        
        StructureCRE savedStructure = new StructureCRE(1L, "Test Structure", LocalDateTime.now(), champs);
        when(structureCRERepository.save(any(StructureCRE.class))).thenReturn(savedStructure);

        // Act
        ResponseEntity<StructureCRE> response = structureCREController.confirmImport(preview);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(savedStructure.getId(), response.getBody().getId());
        assertEquals(savedStructure.getNomStructure(), response.getBody().getNomStructure());
        verify(structureCRERepository, times(1)).save(any(StructureCRE.class));
    }

    @Test
    void importStructurePreview_EmptyList() throws Exception {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.xlsx", MediaType.APPLICATION_OCTET_STREAM_VALUE, "test data".getBytes()
        );
        
        when(excelImportService.parseExcelFile(any())).thenReturn(new ArrayList<>());

        // Act
        ResponseEntity<StructurePreview> response = structureCREController.importStructurePreview(file);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void importStructurePreview_Exception() throws Exception {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.xlsx", MediaType.APPLICATION_OCTET_STREAM_VALUE, "test data".getBytes()
        );
        
        when(excelImportService.parseExcelFile(any())).thenThrow(new RuntimeException("Test exception"));

        // Act
        ResponseEntity<StructurePreview> response = structureCREController.importStructurePreview(file);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void confirmImport_Exception() {
        // Arrange
        StructurePreview preview = new StructurePreview();
        preview.setProposedName("Test Structure");
        
        when(structureCRERepository.save(any(StructureCRE.class))).thenThrow(new RuntimeException("Test exception"));

        // Act
        ResponseEntity<StructureCRE> response = structureCREController.confirmImport(preview);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }
}