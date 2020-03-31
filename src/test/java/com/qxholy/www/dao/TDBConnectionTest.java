package com.qxholy.www.dao;

import com.qxholy.www.utils.Constants;
import org.apache.jena.rdf.model.Statement;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TDBConnectionTest {

    @Autowired
    TDBConnection tdbConnection;

    @Test
    public void importRdfFile() {
        tdbConnection.importRdfFile("person", "C:\\Users\\ngcps\\IdeaProjects\\www\\src\\main\\resources\\person.ttl");
    }

    @Test
    public void exportRdfFile() {
        tdbConnection.exportModelToFile("xd", "C:\\Users\\ngcps\\IdeaProjects\\www\\src\\main\\resources\\xdd.ttl");
    }

    @Test
    public void queryData() {
        tdbConnection.queryData("person", Constants.PREFIX_QXHOLY + " select ?id  where {?id qx:name 'xuda'}");
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


}