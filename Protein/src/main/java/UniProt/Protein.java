package UniProt;

import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.kraken.interfaces.uniprot.description.Field;
import uk.ac.ebi.kraken.interfaces.uniprot.description.FieldType;
import uk.ac.ebi.kraken.interfaces.uniprot.description.Name;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Protein {

    private UniProtEntry entry;
    HashMap<Protein,Float> neighbors = new HashMap<Protein,Float>();
    
    public Protein() {
    }

    public Protein(UniProtEntry entry) {
        this.entry = entry;
    }

    public UniProtEntry getEntry() {
        return entry;
    }

    public void setEntry(UniProtEntry entry) {
        this.entry = entry;
    }

    public HashMap<Protein, Float> getNeighbors() {
        return neighbors;
    }

    public void setNeighbors(HashMap<Protein, Float> neighbors) {
        this.neighbors = neighbors;
    }

    @Override
    public String toString() {
    	
        return "Protein{" +
                "accession='" + entry.getPrimaryUniProtAccession().getValue() + '\'' +
                ", name='" + entry.getUniProtId().getValue() + '\'' +
                ", fullName='" + formatFullName(entry.getProteinDescription().getRecommendedName()) + '\'' +
                ", type='" + entry.getType().getValue() + '\'' +
                ", genePrimary='" + entry.getGenes().get(0).getGeneName().getValue() + '\'' +
                ", organism='" + entry.getOrganism().getScientificName().getValue() + '\'' +
                ", title='" + entry.getCitationsNew().get(0).getTitle().getValue() + '\'' +
                '}';
    }

    public String formatFullName(Name name) {
        List<Field> fullNameFields = name.getFieldsByType(FieldType.FULL);

        String nameText = "";
        
        if (!fullNameFields.isEmpty()) {
            Field fullNameField = fullNameFields.get(0);
            nameText += fullNameField.getValue();

        }

        return nameText;
    }


    public boolean equals(Protein p) {
        if(this.getEntry().getPrimaryUniProtAccession().getValue().equals(p.getEntry().getPrimaryUniProtAccession().getValue()))
            return true;
        else
            return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(entry);
    }
}
