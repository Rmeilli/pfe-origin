package org.sid.creapp.Entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "valeurs_cre")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ValeurCRE {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "structure_id", nullable = false)
    @JsonBackReference
    private StructureCRE structure;

    private Integer ligne = 0;
    private Integer ord = 0;

    @Column(name = "nom_zone", nullable = false)
    private String nomZone;

    @Column(nullable = false)
    private Integer pos;

    @Column(name = "long_col", nullable = false)
    private Integer longCol;

    @Column(nullable = false)
    private Integer dec = 0;

    @Column(nullable = false)
    private String format;

    @Column(name = "designation_bcp")
    private String designationBcp;

    // Getters et setters avec gestion des valeurs par défaut
    public Integer getDec() {
        return dec != null ? dec : 0;
    }

    public void setDec(Integer dec) {
        this.dec = dec != null ? dec : 0;
    }

    public Integer getOrd() {
        return ord != null ? ord : 0;
    }

    public void setOrd(Integer ord) {
        this.ord = ord != null ? ord : 0;
    }

    public Integer getLigne() {
        return ligne != null ? ligne : 0;
    }

    public void setLigne(Integer ligne) {
        this.ligne = ligne != null ? ligne : 0;
    }
}
