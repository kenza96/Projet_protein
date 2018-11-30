package UniProt;

import uk.ac.ebi.kraken.interfaces.uniprot.Gene;
import uk.ac.ebi.kraken.interfaces.uniprot.ProteinDescription;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.kraken.interfaces.uniprot.citationsNew.Citation;
import uk.ac.ebi.kraken.interfaces.uniprot.description.Field;
import uk.ac.ebi.kraken.interfaces.uniprot.description.FieldType;
import uk.ac.ebi.kraken.interfaces.uniprot.description.Name;
import uk.ac.ebi.uniprot.dataservice.client.Client;
import uk.ac.ebi.uniprot.dataservice.client.ServiceFactory;
import uk.ac.ebi.uniprot.dataservice.client.exception.ServiceException;
import uk.ac.ebi.uniprot.dataservice.client.uniprot.UniProtQueryBuilder;
import uk.ac.ebi.uniprot.dataservice.client.uniprot.UniProtService;
import uk.ac.ebi.uniprot.dataservice.query.Query;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static uk.ac.ebi.uniprot.dataservice.client.examples.PrintUtils.printExampleHeader;
import static uk.ac.ebi.uniprot.dataservice.client.examples.PrintUtils.printSearchResults;

public class TestUniProt {

    private static final int DISPLAY_ENTRY_SIZE = 25;

    public static void main(String[] args) throws ServiceException {
        ServiceFactory serviceFactoryInstance = Client.getServiceFactoryInstance();
        UniProtService uniProtService = serviceFactoryInstance.getUniProtQueryService();
        System.out.println("Staring up search service");
        uniProtService.start();
        //--------------------------------------------------------------------
        /*printExampleHeader("Search by protein ID");
        String searchTerm = "CYC_HUMAN";
        System.out.println("Search for entry with ID: " + searchTerm);
        Query query = UniProtQueryBuilder.id(searchTerm);
        Protein protein = new Protein();

        UniProtSearchExamples.SearchExecutor searchExecutor = new UniProtSearchExamples.SearchExecutor(uniProtService) {
            @Override
            List<String> extractValues(UniProtEntry entry) {
                protein.setEntry(entry);
                return Collections.singletonList(entry.getUniProtId().getValue());
            }

        };


        Map<String, List<String>> entries = searchExecutor.executeSearch(query);
        printSearchResults(entries);
        System.out.println(protein);

        //-------------------------------------------------------------------
        /*printExampleHeader("Search for entries with partial protein name");

        String searchTerm = "Cytochrome c";
        System.out
                .printf("Search for first %d protein entries with protein name: %s%n", DISPLAY_ENTRY_SIZE, searchTerm);

        Query query = UniProtQueryBuilder.proteinName(searchTerm);

        UniProtSearchExamples.SearchExecutor searchExecutor = new UniProtSearchExamples.SearchExecutor(uniProtService) {
            @Override
            List<String> extractValues(UniProtEntry entry) {
                ProteinDescription description = entry.getProteinDescription();

                Name recName = description.getRecommendedName();
                String recFullText = formatFullName(recName);

                return Collections.singletonList(recFullText);
            }

            private String formatFullName(Name name) {
                List<Field> fullNameFields = name.getFieldsByType(FieldType.FULL);

                String nameText = "";

                if (!fullNameFields.isEmpty()) {
                    Field fullNameField = fullNameFields.get(0);

                    nameText = name.getNameType().getValue() + " ";
                    nameText += fullNameField.getType().getValue() + ": ";
                    nameText += fullNameField.getValue();

                }

                return nameText;
            }
        };

        Map<String, List<String>> entries = searchExecutor.executeSearch(query, DISPLAY_ENTRY_SIZE);
        printSearchResults(entries);*/
        //---------------------------------------------------------------
        /*printExampleHeader("Search by publication title");

        String title = "Sequence analysis of the genome of the unicellular cyanobacterium " +
                "Synechocystis sp. strain PCC6803. II. Sequence determination of the " +
                "entire genome and assignment of potential protein-coding regions.";

        System.out.println("Searching for entries with publication title: " + title);
        Query query = UniProtQueryBuilder.title(title);

        UniProtSearchExamples.SearchExecutor searchExecutor = new UniProtSearchExamples.SearchExecutor(uniProtService) {
            @Override
            List<String> extractValues(UniProtEntry entry) {

                List<Citation> citations = entry.getCitationsNew();
                return citations.stream().filter(Citation::hasTitle).map(c -> c.getTitle().getValue())
                        .collect(Collectors.toList());
            }
        };

        Map<String, List<String>> entries = searchExecutor.executeSearch(query, DISPLAY_ENTRY_SIZE);

        printSearchResults(entries);*/
        //------------------------------------------------------------
        printExampleHeader("Search for fragmented entries");

        String searchTerm = "CYCS";
        System.out.printf("Search for first %d protein entries that come from gene: %s%n", DISPLAY_ENTRY_SIZE,
                searchTerm);

        Query query = UniProtQueryBuilder.gene(searchTerm);

        UniProtSearchExamples.SearchExecutor searchExecutor = new UniProtSearchExamples.SearchExecutor(uniProtService) {
            @Override
            List<String> extractValues(UniProtEntry entry) {
                List<Gene> genes = entry.getGenes();

                return genes.stream().map(gene -> gene.getGeneName().getValue()).collect(Collectors.toList());
            }
        };

        Map<String, List<String>> entries = searchExecutor.executeSearch(query, DISPLAY_ENTRY_SIZE);
        printSearchResults(entries);
    }


}
