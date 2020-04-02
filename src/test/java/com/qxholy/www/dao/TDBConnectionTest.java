package com.qxholy.www.dao;

import com.qxholy.www.utils.Constants;
import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TDBConnectionTest {

    @Autowired
    TDBConnection tdbConnection;

    @Test
    public void importRdfFile() {
        tdbConnection.importRdfFile("order", "C:\\Users\\ngcps\\iCloudDrive\\Documents\\sell.owl");
    }

    @Test
    public void exportRdfFile() {
        tdbConnection.exportModelToFile("xd", "C:\\Users\\ngcps\\IdeaProjects\\www\\src\\main\\resources\\xdd.ttl");
    }

    @Test
    public void queryJson() {
        String productId = "123457";
//        tdbConnection.queryJson("order",Constants.PREFIX_QX + "JSON {'productId':?productId, 'productField':?productField, 'value':?value}  where{ ?productId ?productField ?value FILTER (?productId = qx:"+productId+") }");
        tdbConnection.queryJson("order",Constants.PREFIX_QX + "JSON {'productField':?productField, 'value':?value}  where{ qx:123457 ?productField ?value }");
    }

    @Test
    public void findAllModels() {
        List<String> allModels = tdbConnection.findAllModels();
        for (String allModel : allModels) {
            System.out.println(allModel);
        }
    }

    @Test
    public void getData(){
        List<Statement> statements = tdbConnection.getData("person");
        for (Statement statement : statements) {
            System.out.println(statement);
        }
    }

    @Test
    public void queryData() {
        String productId = "123457";
        String productField = "productField";
        //FILTER (?productId = qx:" + productId + ")
        TDBConnection.dataset.begin(ReadWrite.READ);
        Model model = TDBConnection.dataset.getNamedModel("order");
        List<JsonObject> list = new ArrayList<>();
        OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM, model);
        String queryStr = Constants.PREFIX_QX + "select ?productField (STR(?o) as ?value) where{ qx:"+productId+" ?"+productField+" ?o  }";
        Query query = QueryFactory.create(queryStr);
        QueryExecution execution = QueryExecutionFactory.create(query, ontModel);
        ResultSet resultSet = execution.execSelect();
        while (resultSet.hasNext()){
            JsonObject jsonObject = new JsonObject();
            QuerySolution solution = resultSet.nextSolution();
            RDFNode node = solution.get(productField);
            RDFNode object = solution.get("value");
            String s = node.toString();
            int i = s.indexOf("/") ;
            i=s.indexOf("/",i+2);
            String productFiled1 = s.substring(i+1);
            System.out.println(productFiled1);
            System.out.println(object.toString());
            jsonObject.put(productFiled1,object.toString());
            list.add(jsonObject);
        }
//        ResultSetFormatter.out(System.out, resultSet, query);
        for (JsonObject jsonObject : list) {
            System.out.println(jsonObject);
        }
        TDBConnection.dataset.end();
    }


}