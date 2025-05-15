package org.sid.creapp.DTO;

import lombok.*;
import org.sid.creapp.Entities.ValeurCRE;

import java.util.List;

@Getter @Setter @Data @AllArgsConstructor
public class StructurePreview {
    public StructurePreview() {
    }

//    public StructurePreview(String proposedName, List<ValeurCRE> champs) {
//        this.proposedName = proposedName;
//        this.champs = champs;
//    }


    public String getProposedName() {
        return proposedName;
    }

    public void setProposedName(String proposedName) {
        this.proposedName = proposedName;
    }

    public List<ValeurCRE> getChamps() {
        return champs;
    }

    public void setChamps(List<ValeurCRE> champs) {
        this.champs = champs;
    }

    private String proposedName;
    private List<ValeurCRE> champs;

    // getters et setters
}

