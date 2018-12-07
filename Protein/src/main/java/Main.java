import Neo.Neo4j;
import UniProt.Protein;
import UniProt.Utils;
//import jdk.tools.jlink.internal.Platform;
import uk.ac.ebi.kraken.interfaces.uniprot.comments.Comment;
import uk.ac.ebi.kraken.interfaces.uniprot.dbx.go.Go;
import uk.ac.ebi.kraken.xml.jaxb.uniprot.Uniprot;
import uk.ac.ebi.uniprot.dataservice.client.exception.ServiceException;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main {


    public static void main(String[] args) throws Exception {
        ArrayList<String> proteinList = new ArrayList<String>();

        Protein prot = new Protein();
        Float teta = (float) 0.0;

        Neo4j neo4j = new Neo4j( "bolt://localhost:7687", "neo4j", "password");
        neo4j.init();
        int numberNeighbor = 0;
        Scanner sc = new Scanner(System.in);
/*        System.out.println("------------------------------");
        System.out.println("---- 1 . Search By Id -----------------------");
        System.out.println("---- 2 . Search By Name----------------------");
        System.out.print("Entre your number : ");
        int choise = sc.nextInt();

         switch (choise){
            case 1 :
                System.out.print("Entre your ID : ");
                String id = sc.next();
                System.out.print("Neighbor number : ");
                numberNeighbor = sc.nextInt();
                prot = Utils.getProteinDetailsID(id);
                break;
            case 2 :
                System.out.print("Entre your NAME : ");
                String name = sc.next();
                System.out.print("Neighbor number : ");
                numberNeighbor = sc.nextInt();
                prot = Utils.getProteinDetailsNAME(name);
                break;
        }*/
  //interface graphique ma ptite gueule
        String[] sexe = {"Par ID", "Par Nom"};
        JOptionPane jop = new JOptionPane();
        String selectedValue = (String)jop.showInputDialog(null,
                "Veuillez choisir votre moyen de recherche de proteine !",
                "Protein Searcher",
                JOptionPane.QUESTION_MESSAGE,
                new ImageIcon("ProteinSpecificityCode.jpg"),
                sexe,
                sexe[0]);

        if (selectedValue.equals("Par ID")) {
            System.out.println("id");
            JOptionPane jopid = new JOptionPane();
            String id = jopid.showInputDialog(null, "Saisissez l'ID de la protéine recherchée", "Protein Searcher", JOptionPane.QUESTION_MESSAGE);
            prot = Utils.getProteinDetailsID(id);
        } else if (selectedValue.equals("Par Nom")) {
            System.out.println("nom");
            JOptionPane jopnom = new JOptionPane();
            String name = jopnom.showInputDialog(null, "Saisissez le nom de la protéine recherchée", "Protein Searcher", JOptionPane.QUESTION_MESSAGE);
            prot = Utils.getProteinDetailsNAME(name);
        }
        JOptionPane jopnei = new JOptionPane();
        String nei = jopnei.showInputDialog(null, "Saisissez le nombre de voisins recherchés", "Protein Searcher", JOptionPane.QUESTION_MESSAGE);
        numberNeighbor=Integer.parseInt(nei);






        // get Neighbors
        if (prot==null) {
        	JOptionPane.showMessageDialog(null, "Cette proteine n'existe pas :'(");
        }
        else {
        Protein protein = prot;

        //System.out.println(protein.getEntry().getComments());
        //TimeUnit.SECONDS.sleep(100);

        System.out.println(protein);
        proteinList.add(protein.getEntry().getPrimaryUniProtAccession().getValue());
        neo4j.printNode(protein);


        protein.setNeighbors(Utils.getNeighborsDetails(protein,numberNeighbor));
        System.out.println(protein.getNeighbors());


        protein.getNeighbors().forEach((k, v) -> {
            System.out.format("key: %s, value: %f%n", k, v);
            proteinList.add(k.getEntry().getPrimaryUniProtAccession().getValue());
            neo4j.printNodeNeighbor(k);
            neo4j.printLinkNeighbor(protein,k,v);
        });

        //TimeUnit.SECONDS.sleep(10);
       
        
        
        
        
        

        int i = 1;
        for (Protein p : protein.getNeighbors().keySet()){
            p.setNeighbors(Utils.getNeighborsDetails(p,numberNeighbor));
            System.out.println("Family number : "+i+" - "+ p.getEntry().getUniProtId() );
            System.out.println(p.getNeighbors());
            p.getNeighbors().forEach((k, v) -> {
            System.out.format("key: %s, value: %f", k, v);
                if (!proteinList.contains(k.getEntry().getPrimaryUniProtAccession().getValue())){
                    neo4j.printNodeNeighborNeighbor(k);
                    neo4j.printLinkNeighborNew(p,k,v);
                }else{
                    neo4j.printLinkNeighborNeighbor(p,k,v);
                }

            });
            //TimeUnit.SECONDS.sleep(10);
            i++;
        }
        

      
        System.out.println("getdetails");
        
        System.out.println(Utils.getNeighborsDetails(protein,numberNeighbor));
        
        HashMap<Protein,Float> neighboorsForLabel =  Filter(Utils.getNeighborsDetails(protein,numberNeighbor),teta);
        HashMap<Go,Float> annot = new HashMap<Go,Float> ();  
        
        System.out.println("filtered");

        System.out.println(neighboorsForLabel);
        
    	Iterator it = neighboorsForLabel.entrySet().iterator();
    	
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            ArrayList<Go> L =  (ArrayList<Go>) ((Protein)pair.getKey()).getEntry().getGoTerms();
			System.out.println("L");
			System.out.println(L);
			System.out.println("L.get(j)");
			System.out.println(L.get(0));
    		for (int j=0;j<L.size();j++) {
    			if (annot==null) {
					System.out.println("first null");
					System.out.println(pair.getValue());
					annot.put(L.get(j), (Float) pair.getValue());
    			}else {
    				if (annot.containsKey(L.get(j))) {
    					System.out.println("boucleif");
    					System.out.println(pair.getValue());
    					System.out.println(L.get(j));
    					Float k = (Float) annot.get(L.get(j)) + (Float) pair.getValue();
    					annot.remove(L.get(j));
    					annot.put(L.get(j),k);
    				} else {
    					System.out.println("boucleelse");
    					System.out.println(pair.getValue());
    					annot.put(L.get(j), (Float) pair.getValue());
    				}
    			}
    		}
    	}
        System.out.println("try");

        System.out.println(annot);
        Iterator ik = annot.entrySet().iterator();
        Go best = null;
        Float besta= (float) 0;
        while (ik.hasNext()) {
            Map.Entry pair = (Map.Entry)ik.next();
            if ((Float) pair.getValue()>besta) {
            	best = (Go) pair.getKey();
            	besta = (Float) pair.getValue();
            }
    		
    	}
        System.out.println(besta);
        System.out.println(best);

        System.out.println(best.toString());
        
        neo4j.printGoDomain(protein, best);

        System.out.println(proteinList);
        
        System.out.println((protein.getEntry().getGoTerms()));

        neo4j.close();
        }

    }
    
    public static HashMap<Protein,Float> Filter(HashMap<Protein,Float> p,float numb) {
    	HashMap<Protein,Float> n = new HashMap<Protein,Float>();
    	Iterator it = p.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
    		if ((Float) pair.getValue()>numb) {
    			n.put((Protein) pair.getKey(), (Float) pair.getValue());
    		}
    		
    	}
    	return n;
    }
}
