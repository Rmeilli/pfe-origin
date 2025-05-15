package org.sid.creapp.Entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
//@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "structure_cre")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class StructureCRE {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nom_structure")
    private String nomStructure;

    @Column(name = "date_creation")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dateCreation = LocalDateTime.now();

    //@OneToMany(mappedBy = "structure", cascade = CascadeType.ALL, orphanRemoval = true)
    @OneToMany(mappedBy = "structure", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<ValeurCRE> valeurs;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomStructure() {
        return nomStructure;
    }

    public void setNomStructure(String nomStructure) {
        this.nomStructure = nomStructure;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public List<ValeurCRE> getValeurs() {
        return valeurs;
    }

    public void setValeurs(List<ValeurCRE> valeurs) {
        this.valeurs = valeurs;
    }
}