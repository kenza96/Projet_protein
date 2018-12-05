import Neo.Neo4j;
import UniProt.Protein;
import UniProt.Utils;
//import jdk.tools.jlink.internal.Platform;
import uk.ac.ebi.kraken.interfaces.uniprot.comments.Comment;
import uk.ac.ebi.kraken.xml.jaxb.uniprot.Uniprot;
import uk.ac.ebi.uniprot.dataservice.client.exception.ServiceException;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
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

        Neo4j neo4j = new Neo4j( "bolt://localhost:7687", "neo4j", "password");
        neo4j.init();
        int numberNeighbor = 0;
        Scanner sc = new Scanner(System.in);
        /*System.out.println("------------------------------");
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
        Protein protein = prot;

        //System.out.println(protein.getEntry().getComments());
        TimeUnit.SECONDS.sleep(100);

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

        System.out.println(proteinList);

        neo4j.close();


    }
}
