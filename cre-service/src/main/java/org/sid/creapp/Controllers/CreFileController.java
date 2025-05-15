package org.sid.creapp.Controllers;

import org.sid.creapp.Entities.StructureCRE;
import org.sid.creapp.Entities.ValeurCRE;
import org.sid.creapp.repositories.StructureCRERepository;
import org.sid.creapp.repositories.ValeurCRERepository;
import org.sid.creapp.services.CreFileGenerationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


//import javax.transaction.Transactional;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/crefiles")
public class CreFileController {

//    @Value("${crefiles.upload-dir}")
    @Value("${crefiles.upload-dir}")
    private String uploadDir;

    private final StructureCRERepository structureCRERepository;
    private final ValeurCRERepository valeurCRERepository;

    private final CreFileGenerationService creFileGenerationService;
    private static final Logger log = LoggerFactory.getLogger(CreFileController.class);



    public CreFileController(StructureCRERepository structureCRERepository, ValeurCRERepository valeurCRERepository, CreFileGenerationService creFileGenerationService) {
        this.structureCRERepository = structureCRERepository;
        this.valeurCRERepository = valeurCRERepository;
        this.creFileGenerationService = creFileGenerationService;
    }

    // Endpoint pour charger un ou plusieurs fichiers CRE pour une structure donnée

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFiles(
            @RequestParam("structureId") Long structureId,
            @RequestParam("files") MultipartFile[] files) {

        // Vérifier que la structure existe
        Optional<StructureCRE> structureOpt = structureCRERepository.findById(structureId);
        if (!structureOpt.isPresent()) {
            return ResponseEntity.badRequest().body("Structure CRE non trouvée avec ID: " + structureId);
        }

        // Créer un dossier pour la structure (par exemple, uploadDir/structureId)
        Path structureFolder = Paths.get(uploadDir, structureId.toString());
        try {
            Files.createDirectories(structureFolder); // Crée le dossier s'il n'existe pas
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la création du dossier pour la structure: " + e.getMessage());
        }

        List<String> fileNames = new ArrayList<>();
        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();
            try {
                Path filePath = structureFolder.resolve(fileName);
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                fileNames.add(fileName);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Erreur lors du chargement de " + fileName + " : " + e.getMessage());
            }
        }
        return ResponseEntity.ok("Fichiers chargés avec succès : " + fileNames);
    }



    @GetMapping("/download/{structureId}/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable("structureId") Long structureId, 
            @PathVariable("fileName") String fileName) {
        try {
            Path structureFolder = Paths.get(uploadDir, structureId.toString());
            Path filePath = structureFolder.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



    @GetMapping("/list/{structureId}")
    public ResponseEntity<List<String>> listFilesForStructure(
            @PathVariable("structureId") Long structureId) {
        // Vérifier que la structure existe
        if (!structureCRERepository.existsById(structureId)) {
            return ResponseEntity.badRequest().build();
        }
        try {
            Path structureFolder = Paths.get(uploadDir, structureId.toString());
            if (!Files.exists(structureFolder)) {
                // Si aucun dossier, renvoyer une liste vide
                return ResponseEntity.ok(new ArrayList<>());
            }
            List<String> fileNames = Files.list(structureFolder)
                    .filter(Files::isRegularFile)
                    .map(path -> path.getFileName().toString())
                    .collect(Collectors.toList());
            return ResponseEntity.ok(fileNames);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



//    @PostMapping("/validate/{structureId}")
//    public ResponseEntity<?> validateCreFile(
//            @PathVariable("structureId") Long structureId,
//            @RequestParam("file") MultipartFile file) {
//        // Récupérer la structure
//        Optional<StructureCRE> optStructure = structureCRERepository.findById(structureId);
//        if (!optStructure.isPresent()) {
//            return ResponseEntity.badRequest().body("Structure CRE non trouvée avec ID: " + structureId);
//        }
//        StructureCRE structure = optStructure.get();
//        List<ValeurCRE> fields = structure.getValeurs();
//        // Trier les champs par position
//        fields.sort(Comparator.comparingInt(ValeurCRE::getPos));
//
//        List<String> errors = new ArrayList<>();
//        try (BufferedReader reader = new BufferedReader(
//                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
//            String line;
//            int lineNumber = 1;
//            while ((line = reader.readLine()) != null) {
//                // Déterminer dynamiquement l'offset : trouver le premier caractère numérique dans la ligne
//                int dynamicOffset = -1;
//                for (int i = 0; i < line.length(); i++) {
//                    if (Character.isDigit(line.charAt(i))) {
//                        dynamicOffset = i;
//                        break;
//                    }
//                }
//                if (dynamicOffset == -1) {
//                    errors.add("Ligne " + lineNumber + " : aucune donnée numérique trouvée.");
//                    lineNumber++;
//                    continue;
//                }
//                // On considère que le premier chiffre trouvé correspond à la position 1 effective des données.
//                int effectiveOffset = dynamicOffset;
//
//                // Pour chaque champ défini dans la structure, extraire et valider la valeur
//                for (ValeurCRE field : fields) {
//                    int pos = field.getPos();
//                    int length = field.getLongCol();
//
//                    // Vérifier si la ligne est suffisamment longue pour extraire la valeur (en tenant compte de l'offset)
//                    if (line.length() < (pos - 1 + effectiveOffset + length)) {
//                        errors.add("Ligne " + lineNumber + " : le champ '" + field.getNomZone() +
//                                "' attend " + length + " caractères, mais la ligne est trop courte.");
//                        continue;
//                    }
//                    // Extraire la valeur brute à partir de la position corrigée
//                    String rawValue = line.substring(pos - 1 + effectiveOffset, pos - 1 + effectiveOffset + length);
//                    System.out.println("Ligne " + lineNumber + ", champ '" + field.getNomZone() +
//                            "' - pos: " + pos + ", long: " + length + ", rawValue: '" + rawValue + "'");
//
//                    String expectedFormat = field.getFormat().toUpperCase();
//                    if (expectedFormat.equals("NUMERIC") || expectedFormat.equals("DECIMAL")) {
//                        // Pour un champ numérique, on retire les espaces
//                        String trimmedValue = rawValue.trim();
//                        // Si la valeur n'est pas vide, vérifier qu'elle ne contient que des chiffres
//                        if (!trimmedValue.isEmpty()) {
//                            if (!trimmedValue.matches("^\\d+$")) {
//                                errors.add("Ligne " + lineNumber + " : le champ '" + field.getNomZone() +
//                                        "' doit être numérique. Valeur trouvée : '" + trimmedValue + "'.");
//                            }
//                        }
//                    }
//                    // (Vous pouvez ajouter ici des validations supplémentaires pour TEXT, DATETIME, etc.)
//                }
//                lineNumber++;
//            }
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Erreur lors de la validation du fichier CRE : " + e.getMessage());
//        }
//
//        if (errors.isEmpty()) {
//            return ResponseEntity.ok("Fichier conforme");
//        } else {
//            return ResponseEntity.ok(errors);
//        }
//    }




        @PostMapping("/validate/{structureId}")
        public ResponseEntity<?> validateCreFile(
                @PathVariable Long structureId,
                @RequestParam("file") MultipartFile file) {

            log.info("Début validation CRE pour structure ID={}", structureId);

            Optional<StructureCRE> optStructure = structureCRERepository.findById(structureId);
            if (optStructure.isEmpty()) {
                log.error("Structure non trouvée ID={}", structureId);
                return ResponseEntity
                        .badRequest()
                        .body(Map.of("status","ERROR",
                                "errors", List.of("Structure CRE non trouvée avec ID: " + structureId)));
            }
            List<ValeurCRE> fields = optStructure.get().getValeurs();
            fields.sort(Comparator.comparingInt(ValeurCRE::getPos));

            List<String> errors = new ArrayList<>();

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

                String line;
                int lineNumber = 1;

                while ((line = reader.readLine()) != null) {
                    // trouver offset premier digit
                    int dynamicOffset = -1;
                    for (int i = 0; i < line.length(); i++) {
                        if (Character.isDigit(line.charAt(i))) {
                            dynamicOffset = i;
                            break;
                        }
                    }
                    if (dynamicOffset < 0) {
                        String msg = "Ligne " + lineNumber + ": aucune donnée numérique.";
                        log.warn(msg);
                        errors.add(msg);
                        lineNumber++;
                        continue;
                    }
                    int effectiveOffset = dynamicOffset;

                    for (ValeurCRE field : fields) {
                        int pos    = field.getPos();
                        int length = field.getLongCol();
                        int start  = (pos - 1) + effectiveOffset;

                        if (line.length() < start + length) {
                            String msg = "Ligne " + lineNumber +
                                    ": champ '" + field.getNomZone() +
                                    "' attend " + length +
                                    " car., ligne trop courte.";
                            log.warn(msg);
                            errors.add(msg);
                            continue;
                        }

                        String rawValue = line.substring(start, start + length);
                        // **LE LOG DEBUG QUE TU VEUX**
                        log.debug("Ligne {}, champ '{}' - pos: {}, long: {}, rawValue: '{}'",
                                lineNumber, field.getNomZone(), pos, length, rawValue);

                        String trimmed = rawValue.trim();
                        String desc    = Optional.ofNullable(field.getDesignationBcp())
                                .orElse("").toLowerCase();
                        if (!desc.contains("blanc") && trimmed.isEmpty()) {
                            String msg = "Ligne " + lineNumber +
                                    ": champ '" + field.getNomZone() +
                                    "' obligatoire vide.";
                            log.warn(msg);
                            errors.add(msg);
                            continue;
                        }
                        String fmt = field.getFormat().toUpperCase();
                        if ((fmt.equals("NUMERIC") || fmt.equals("DECIMAL"))
                                && !trimmed.isEmpty()
                                && !trimmed.matches("^\\d+$")) {
                            String msg = "Ligne " + lineNumber +
                                    ": champ '" + field.getNomZone() +
                                    "' doit être numérique, trouvé='" + trimmed + "'";
                            log.warn(msg);
                            errors.add(msg);
                        }
                    }
                    lineNumber++;
                }

            } catch (Exception e) {
                log.error("Erreur validation CRE : {}", e.getMessage(), e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("status","ERROR",
                                "errors", List.of("Exception: " + e.getMessage())));
            }

            if (errors.isEmpty()) {
                log.info("Validation OK pour structure ID={}", structureId);
                return ResponseEntity.ok(Map.of("status","OK"));
            } else {
                log.info("{} erreur(s) détectée(s) pour structure ID={}", errors.size(), structureId);
                return ResponseEntity.ok(Map.of("status","ERROR", "errors", errors));
            }
        }















    /**
     * Endpoint pour générer et télécharger un fichier CRE.
     * @param structureId l'ID de la structure CRE à utiliser
     * @param lines nombre de lignes à générer (ex. 100)
     * @return le fichier CRE généré sous forme de ressource
     */


    @GetMapping("/generate/{structureId}")
    public ResponseEntity<Resource> generateCreFile(
            @PathVariable("structureId") Long structureId,
            @RequestParam(defaultValue = "100") int lines) {
        try {
            String content = creFileGenerationService.generateCreFileContent(structureId, lines);
            byte[] contentBytes = content.getBytes();
            ByteArrayResource resource = new ByteArrayResource(contentBytes);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"CRE_structure_" + structureId + ".txt\"")
                    .contentLength(contentBytes.length)
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/delete/{structureId}/{fileName:.+}")
    public ResponseEntity<?> deleteFile(
            @PathVariable("structureId") Long structureId, 
            @PathVariable("fileName") String fileName) {
        try {
            // Construire le chemin du dossier de la structure
            Path structureFolder = Paths.get(uploadDir, structureId.toString());
            // Résoudre le chemin complet du fichier
            Path filePath = structureFolder.resolve(fileName).normalize();

            // Vérifier si le fichier existe
            if (!Files.exists(filePath)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Fichier non trouvé");
            }

            // Supprimer le fichier
            Files.delete(filePath);
            return ResponseEntity.ok("Fichier supprimé avec succès");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la suppression du fichier : " + e.getMessage());
        }
    }

    //    @GetMapping("/list-all")
//    public ResponseEntity<?> listAllFiles() throws IOException {
//        try (Stream<Path> paths = Files.list(Paths.get(uploadDir))) {
//            List<String> files = paths
//                    .filter(Files::isRegularFile)
//                    .map(p -> p.getFileName().toString())
//                    .collect(Collectors.toList());
//            return ResponseEntity.ok(files);
//        }
//    }
@GetMapping("/list-all")
public ResponseEntity<?> listAllFiles() {
    try (Stream<Path> paths = Files.walk(Paths.get(uploadDir), 2)) {
        List<String> files = paths
                .filter(Files::isRegularFile)
                .map(path -> path.getParent().getFileName() + "/" + path.getFileName()) // format: structureId/filename
                .collect(Collectors.toList());
        return ResponseEntity.ok(files);
    } catch (IOException e) {
        return ResponseEntity.status(500).body("Erreur lors du listing des fichiers : " + e.getMessage());
    }
}




}

