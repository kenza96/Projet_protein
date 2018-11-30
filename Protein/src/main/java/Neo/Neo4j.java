package Neo;

import UniProt.Protein;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;
import org.neo4j.driver.v1.TransactionWork;

import java.util.HashMap;

import static org.neo4j.driver.v1.Values.parameters;

public class Neo4j implements AutoCloseable
{
    private final Driver driver;

    public Neo4j( String uri, String user, String password )
    {
        driver = GraphDatabase.driver( uri, AuthTokens.basic( user, password ) );
    }

    @Override
    public void close() throws Exception
    {
        driver.close();
    }

    public void printNode(Protein protein)
    {
        String[] nodes =
                {"accession",protein.getEntry().getPrimaryUniProtAccession().getValue(),
                "name", protein.getEntry().getUniProtId().getValue(),
                "fullName", protein.formatFullName(protein.getEntry().getProteinDescription().getRecommendedName()),
                "type", protein.getEntry().getType().getValue(),
                "genePrimary", protein.getEntry().getGenes().get(0).getGeneName().getValue(),
                "organism", protein.getEntry().getOrganism().getScientificName().getValue(),
                "title", protein.getEntry().getCitationsNew().get(0).getTitle().getValue()};

        try ( Session session = driver.session() )
        {
            String greeting = session.writeTransaction( new TransactionWork<String>()
            {
                @Override
                public String execute( Transaction tx )
                {
                    StatementResult result = tx.run( "CREATE (p:Protein { " +
                            "accession : $accession ,"+
                            "name : $name ,"+
                            "fullName : $fullName ,"+
                            "type : $type ,"+
                            "genePrimary : $genePrimary ,"+
                            "organism : $organism ,"+
                            "title : $title"+
                            "})",parameters(nodes) );

                    return "";
                }
            } );
            System.out.println( greeting );
        }
    }

    public void printNodeNeighbor(Protein protein)
    {
        String[] nodes =
                {"accession",protein.getEntry().getPrimaryUniProtAccession().getValue(),
                        "name", protein.getEntry().getUniProtId().getValue(),
                        "fullName", protein.formatFullName(protein.getEntry().getProteinDescription().getRecommendedName()),
                        "type", protein.getEntry().getType().getValue(),
                        "genePrimary", protein.getEntry().getGenes().get(0).getGeneName().getValue(),
                        "organism", protein.getEntry().getOrganism().getScientificName().getValue(),
                        "title", protein.getEntry().getCitationsNew().get(0).getTitle().getValue()};

        try ( Session session = driver.session() )
        {
            String greeting = session.writeTransaction( new TransactionWork<String>()
            {
                @Override
                public String execute( Transaction tx )
                {
                    StatementResult result = tx.run( "CREATE (p:NeighborProt { " +
                            "accession : $accession ,"+
                            "name : $name ,"+
                            "fullName : $fullName ,"+
                            "type : $type ,"+
                            "genePrimary : $genePrimary ,"+
                            "organism : $organism ,"+
                            "title : $title"+
                            "})",parameters(nodes) );

                    return "";
                }
            } );
            System.out.println( greeting );
        }
    }

    public void printNodeNeighborNeighbor(Protein protein)
    {
        String[] nodes =
                {"accession",protein.getEntry().getPrimaryUniProtAccession().getValue(),
                        "name", protein.getEntry().getUniProtId().getValue(),
                        "fullName", protein.formatFullName(protein.getEntry().getProteinDescription().getRecommendedName()),
                        "type", protein.getEntry().getType().getValue(),
                        "genePrimary", protein.getEntry().getGenes().get(0).getGeneName().getValue(),
                        "organism", protein.getEntry().getOrganism().getScientificName().getValue(),
                        "title", protein.getEntry().getCitationsNew().get(0).getTitle().getValue()};

        try ( Session session = driver.session() )
        {
            String greeting = session.writeTransaction( new TransactionWork<String>()
            {
                @Override
                public String execute( Transaction tx )
                {
                    StatementResult result = tx.run( "CREATE (p:NeighborNeighborProt { " +
                            "accession : $accession ,"+
                            "name : $name ,"+
                            "fullName : $fullName ,"+
                            "type : $type ,"+
                            "genePrimary : $genePrimary ,"+
                            "organism : $organism ,"+
                            "title : $title"+
                            "})",parameters(nodes) );

                    return "";
                }
            } );
            System.out.println( greeting );
        }
    }

    public void printLinkNeighbor(Protein protein,Protein protein1,float link)
    {

        try ( Session session = driver.session() )
        {
            String greeting = session.writeTransaction( new TransactionWork<String>()
            {
                @Override
                public String execute( Transaction tx )
                {
                    StatementResult result = tx.run( "MATCH (a:Protein{accession:\""+protein.getEntry().getPrimaryUniProtAccession().getValue()+"\"})" +
                            ",(b:NeighborProt{accession:\""+protein1.getEntry().getPrimaryUniProtAccession().getValue()+"\"})" +
                            "CREATE (a)-[r:Relation{jackard:\""+Float.toString(link)+"\"}]->(b)");

                    return "";
                }
            } );
            System.out.println( greeting );
        }
    }

    public void printLinkNeighborNeighbor(Protein protein,Protein protein1,float link)
    {

        try ( Session session = driver.session() )
        {
            String greeting = session.writeTransaction( new TransactionWork<String>()
            {
                @Override
                public String execute( Transaction tx )
                {
                    StatementResult result = tx.run( "MATCH (a:NeighborProt{accession:\""+protein.getEntry().getPrimaryUniProtAccession().getValue()+"\"})" +
                            ",(b:NeighborProt{accession:\""+protein1.getEntry().getPrimaryUniProtAccession().getValue()+"\"})" +
                            "CREATE (a)-[r:Relation{jackard:\""+Float.toString(link)+"\"}]->(b)");

                    return "";
                }
            } );
            System.out.println( greeting );
        }
    }

    public void printLinkNeighborNew(Protein protein,Protein protein1,float link)
    {

        try ( Session session = driver.session() )
        {
            String greeting = session.writeTransaction( new TransactionWork<String>()
            {
                @Override
                public String execute( Transaction tx )
                {
                    StatementResult result = tx.run( "MATCH (a:NeighborProt{accession:\""+protein.getEntry().getPrimaryUniProtAccession().getValue()+"\"})" +
                            ",(b:NeighborNeighborProt{accession:\""+protein1.getEntry().getPrimaryUniProtAccession().getValue()+"\"})" +
                            "CREATE (a)-[r:Relation{jackard:\""+Float.toString(link)+"\"}]->(b)");

                    return "";
                }
            } );
            System.out.println( greeting );
        }
    }

    public void init()
    {

        try ( Session session = driver.session() )
        {
            String greeting = session.writeTransaction( new TransactionWork<String>()
            {
                @Override
                public String execute( Transaction tx )
                {
                    StatementResult result = tx.run( "MATCH (n) OPTIONAL MATCH (n)-[r]-() DELETE n,r");

                    return "";
                }
            } );
            System.out.println( greeting );
        }
    }



}