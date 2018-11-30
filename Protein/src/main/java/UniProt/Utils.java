package UniProt;

import uk.ac.ebi.kraken.interfaces.uniprot.DatabaseCrossReference;
import uk.ac.ebi.kraken.interfaces.uniprot.Gene;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.uniprot.dataservice.client.Client;
import uk.ac.ebi.uniprot.dataservice.client.ServiceFactory;
import uk.ac.ebi.uniprot.dataservice.client.exception.ServiceException;
import uk.ac.ebi.uniprot.dataservice.client.uniprot.UniProtQueryBuilder;
import uk.ac.ebi.uniprot.dataservice.client.uniprot.UniProtService;
import uk.ac.ebi.uniprot.dataservice.query.Query;

import java.util.*;
import java.util.stream.Collectors;

import static uk.ac.ebi.uniprot.dataservice.client.examples.PrintUtils.printExampleHeader;
import static uk.ac.ebi.uniprot.dataservice.client.examples.PrintUtils.printSearchResults;



public class Utils {

    public static Protein getProteinDetailsID(String proteinDetails) throws ServiceException {
        System.out.println("Staring up search service");
        ServiceFactory serviceFactoryInstance = Client.getServiceFactoryInstance();
        UniProtService uniProtService = serviceFactoryInstance.getUniProtQueryService();
        uniProtService.start();
        //--------------------------------------------------------------------
        printExampleHeader("Search by protein ID");
        String searchTerm = proteinDetails;
        System.out.println("Search for entry with ID: " + searchTerm);
        Query query = UniProtQueryBuilder.accession(searchTerm);
        Protein protein = new Protein();
        UniProtSearchExamples.SearchExecutor searchExecutor = new UniProtSearchExamples.SearchExecutor(uniProtService) {
            @Override
            List<String> extractValues(UniProtEntry entry) {
                protein.setEntry(entry);
                return Collections.singletonList(entry.getUniProtId().getValue());
            }

        };

        Map<String, List<String>> entries = searchExecutor.executeSearch(query);
        //printSearchResults(entries);
        uniProtService.stop();
        return protein;
    }

    public static Protein getProteinDetailsNAME(String proteinDetails) throws ServiceException {
        System.out.println("Staring up search service");
        ServiceFactory serviceFactoryInstance = Client.getServiceFactoryInstance();
        UniProtService uniProtService = serviceFactoryInstance.getUniProtQueryService();
        uniProtService.start();
        //--------------------------------------------------------------------
        printExampleHeader("Search by protein NAME");
        String searchTerm = proteinDetails;
        System.out.println("Search for entry with NAME: " + searchTerm);
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
        //printSearchResults(entries);
        uniProtService.stop();
        return protein;
    }


    public static HashMap<Protein,Float> getNeighborsDetails(Protein prot,int DISPLAY_ENTRY_SIZE) throws ServiceException {
        ServiceFactory serviceFactoryInstance = Client.getServiceFactoryInstance();
        UniProtService uniProtService = serviceFactoryInstance.getUniProtQueryService();
        uniProtService.start();
        Query queryneighbors = UniProtQueryBuilder.gene(prot.getEntry().getGenes().get(0).getGeneName().getValue());
        List<Protein> proteins = new ArrayList<Protein>();
        UniProtSearchExamples.SearchExecutor searchExecutor = new UniProtSearchExamples.SearchExecutor(uniProtService) {
            @Override
            List<String> extractValues(UniProtEntry entry) {
                List<Gene> genes = entry.getGenes();
                proteins.add(new Protein(entry));
                return genes.stream().map(gene -> gene.getGeneName().getValue()).collect(Collectors.toList());
            }
        };

        Map<String, List<String>> neighbors = searchExecutor.executeSearch(queryneighbors, DISPLAY_ENTRY_SIZE);
        //printSearchResults(neighbors);
        for (Protein p : proteins){
            int i = 0;
            int j = p.getEntry().getDatabaseCrossReferences().size();
            for (DatabaseCrossReference reference : p.getEntry().getDatabaseCrossReferences()){

                if (prot.getEntry().getDatabaseCrossReferences().contains(reference)){
                    i++;
                }

            }
            j+= prot.getEntry().getDatabaseCrossReferences().size()-i;
            if (!p.equals(prot)){
                prot.getNeighbors().put(p, (float)i/j);
            }
        }
        uniProtService.stop();
        return prot.getNeighbors();
    }

}