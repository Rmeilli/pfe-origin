package org.sid.creapp.services;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sid.creapp.Entities.StructureCRE;
import org.sid.creapp.Entities.ValeurCRE;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ExcelImportServiceTest {

    @InjectMocks
    private ExcelImportService excelImportService;

    private MockMultipartFile validExcelFile;
    private MockMultipartFile emptyExcelFile;
    private MockMultipartFile invalidExcelFile;

    @BeforeEach
    void setUp() throws IOException {
        // Créer un fichier Excel valide
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Test Sheet");
            
            // Créer la ligne d'en-tête
            Row headerRow = sheet.createRow(0);
            String[] headers = {"ORD", "NOM ZONE", "POS", "LONG", "DÉC", "FORMAT", "DESIGNATION BCP"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            // Créer quelques lignes de données
            Row dataRow1 = sheet.createRow(1);
            dataRow1.createCell(0).setCellValue(1);
            dataRow1.createCell(1).setCellValue("Zone1");
            dataRow1.createCell(2).setCellValue(1);
            dataRow1.createCell(3).setCellValue(5);
            dataRow1.createCell(4).setCellValue(0);
            dataRow1.createCell(5).setCellValue("X");
            dataRow1.createCell(6).setCellValue("Test1");

            Row dataRow2 = sheet.createRow(2);
            dataRow2.createCell(0).setCellValue(2);
            dataRow2.createCell(1).setCellValue("Zone2");
            dataRow2.createCell(2).setCellValue(2);
            dataRow2.createCell(3).setCellValue(3);
            dataRow2.createCell(4).setCellValue(0);
            dataRow2.createCell(5).setCellValue("9");
            dataRow2.createCell(6).setCellValue("Test2");

            // Convertir en bytes
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            validExcelFile = new MockMultipartFile(
                "test.xlsx",
                "test.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                outputStream.toByteArray()
            );
        }

        // Créer un fichier Excel vide
        try (Workbook workbook = new XSSFWorkbook()) {
            workbook.createSheet("Empty Sheet");
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            emptyExcelFile = new MockMultipartFile(
                "empty.xlsx",
                "empty.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                outputStream.toByteArray()
            );
        }

        // Créer un fichier Excel invalide (sans en-têtes)
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Invalid Sheet");
            Row dataRow = sheet.createRow(0);
            dataRow.createCell(0).setCellValue("Invalid Data");
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            invalidExcelFile = new MockMultipartFile(
                "invalid.xlsx",
                "invalid.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                outputStream.toByteArray()
            );
        }
    }

    @Test
    void parseExcelFile_WithValidFile_ShouldParseCorrectly() {
        // Act
        List<StructureCRE> structures = excelImportService.parseExcelFile(validExcelFile);

        // Assert
        assertNotNull(structures);
        assertEquals(1, structures.size());
        
        StructureCRE structure = structures.get(0);
        assertNotNull(structure);
        assertEquals("", structure.getNomStructure()); // Nom vide par défaut
        assertNotNull(structure.getValeurs());
        assertEquals(2, structure.getValeurs().size());

        // Vérifier la première valeur
        ValeurCRE valeur1 = structure.getValeurs().get(0);
        assertEquals(1, valeur1.getOrd());
        assertEquals("Zone1", valeur1.getNomZone());
        assertEquals(1, valeur1.getPos());
        assertEquals(5, valeur1.getLongCol());
        assertEquals(0, valeur1.getDec());
        assertEquals("X", valeur1.getFormat());
        assertEquals("Test1", valeur1.getDesignationBcp());

        // Vérifier la deuxième valeur
        ValeurCRE valeur2 = structure.getValeurs().get(1);
        assertEquals(2, valeur2.getOrd());
        assertEquals("Zone2", valeur2.getNomZone());
        assertEquals(2, valeur2.getPos());
        assertEquals(3, valeur2.getLongCol());
        assertEquals(0, valeur2.getDec());
        assertEquals("9", valeur2.getFormat());
        assertEquals("Test2", valeur2.getDesignationBcp());
    }

    @Test
    void parseExcelFile_WithEmptyFile_ShouldCreateEmptyStructure() {
        // Act
        List<StructureCRE> structures = excelImportService.parseExcelFile(emptyExcelFile);

        // Assert
        assertNotNull(structures);
        assertEquals(1, structures.size());
        
        StructureCRE structure = structures.get(0);
        assertNotNull(structure);
        assertEquals("", structure.getNomStructure());
        assertNotNull(structure.getValeurs());
        assertTrue(structure.getValeurs().isEmpty());
    }

    @Test
    void parseExcelFile_WithInvalidFile_ShouldCreateEmptyStructure() {
        // Act
        List<StructureCRE> structures = excelImportService.parseExcelFile(invalidExcelFile);

        // Assert
        assertNotNull(structures);
        assertEquals(1, structures.size());
        
        StructureCRE structure = structures.get(0);
        assertNotNull(structure);
        assertEquals("", structure.getNomStructure());
        assertNotNull(structure.getValeurs());
        assertTrue(structure.getValeurs().isEmpty());
    }

    @Test
    void parseExcelFile_WithNullFile_ShouldThrowException() {
        // Act & Assert
        assertThrows(RuntimeException.class, () -> excelImportService.parseExcelFile(null));
    }

    @Test
    void parseExcelFile_WithInvalidFileFormat_ShouldThrowException() {
        // Arrange
        MockMultipartFile invalidFormatFile = new MockMultipartFile(
            "test.txt",
            "test.txt",
            "text/plain",
            "Invalid content".getBytes()
        );

        // Act & Assert
        assertThrows(RuntimeException.class, () -> excelImportService.parseExcelFile(invalidFormatFile));
    }
} 