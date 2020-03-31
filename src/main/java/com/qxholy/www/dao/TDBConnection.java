package com.qxholy.www.dao;

import lombok.extern.slf4j.Slf4j;
import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.sdb.SDBFactory;
import org.apache.jena.sdb.Store;
import org.apache.jena.sdb.StoreDesc;
import org.apache.jena.sdb.sql.JDBC;
import org.apache.jena.sdb.sql.SDBConnection;
import org.apache.jena.sdb.store.DatabaseType;
import org.apache.jena.sdb.store.LayoutType;
import org.apache.jena.tdb2.TDB2Factory;
import org.springframework.stereotype.Repository;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.qxholy.www.utils.Constants.PREFIX_QXHOLY;


/**
 * @Project kg01
 * @Package: com.qxholy.kg01.dao
 * @Author xuda
 * @Date 2020/2/20 6:43 下午
 */
@Slf4j
@Repository
public class TDBConnection {

    public static final String tdb_path = "D:\\tdbpath";

    public TDBConnection() {
    }



    public static Model ontModel = ontModel();

    public static final Model modeltmp = null;

    public static final Dataset dataset = TDB2Factory.createDataset(tdb_path);

    private static Model ontModel() {
        OntModelSpec spec = new OntModelSpec(OntModelSpec.OWL_DL_MEM);
        OntModel ontModel = ModelFactory.createOntologyModel(spec, modeltmp);
        return ontModel;
    }

    /**
     * 将rdf文件导入到model中
     *
     * @param modelName
     * @param rdfFilePath
     */
    public void importRdfFile(String modelName, String rdfFilePath) {
        dataset.begin(ReadWrite.WRITE);
        ontModel = dataset.getNamedModel(modelName);
        ontModel.read(rdfFilePath);
        dataset.commit();
        dataset.end();
    }

    /**
     * 删除model
     *
     * @param modelName
     */
    public void removeModel(String modelName) {
        if (!dataset.isInTransaction()) {
            dataset.begin(ReadWrite.WRITE);
        }
        try {
            dataset.removeNamedModel(modelName);
            dataset.commit();
            log.info(modelName + ": 已被移除！");
        } finally {
            dataset.end();
        }
    }

    /**
     * 关闭TDB连接
     */
    public void closeTDB() {
        dataset.close();
    }

    /**
     * 是否存在该model
     *
     * @param modelName
     * @return
     */
    public boolean findTDB(String modelName) {
        boolean result;
        dataset.begin(ReadWrite.READ);
        try {
            if (dataset.containsNamedModel(modelName)) {
                result = true;
            } else {
                result = false;
            }
        } finally {
            dataset.end();
        }
        return result;
    }

    /**
     * 查询所有model
     *
     * @return
     */
    public List<String> findAllModels() {
        dataset.begin(ReadWrite.READ);
        List<String> uriList = new ArrayList<>();
        try {
            Iterator<String> names = dataset.listNames();
            String name = null;
            while (names.hasNext()) {
                name = names.next();
                uriList.add(name);
            }
        } finally {
            dataset.end();
        }
        return uriList;
    }

    /**
     * 根据model名称查询单个model
     *
     * @param modelName
     * @return
     */
    public Model findModelByName(String modelName) {

        dataset.begin(ReadWrite.READ);
        ontModel = dataset.getNamedModel(modelName);
        dataset.end();
        return ontModel;
    }

    /**
     * 查看默认model
     *
     * @return
     */
    public Model getDefaultModel() {
        dataset.begin(ReadWrite.READ);
        try {
            ontModel = dataset.getDefaultModel();
            dataset.commit();
        } finally {
            dataset.end();
        }
        return ontModel;
    }


    /**
     * 增加model中的三元组
     *
     * @param modelName
     * @param subject
     * @param property
     * @param object
     */
    public void addStatement(String modelName, String subject, String property, String object) {


        try {
            dataset.begin(ReadWrite.WRITE);
            ontModel = dataset.getNamedModel(modelName);
            Statement statement = ontModel.createStatement(
                    ontModel.createResource(subject),
                    ontModel.createProperty(property),
                    ontModel.createResource(object)
            );
            ontModel.add(statement);
            dataset.commit();
            dataset.end();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询model中的三元组
     *
     * @param modelName
     * @param subject
     * @return
     */
    public List<Statement> getStatements(String modelName, String subject, String property, String object) {
        List<Statement> statements = new ArrayList<>();
        dataset.begin(ReadWrite.READ);
        ontModel = dataset.getNamedModel(modelName);
        Selector selector = new SimpleSelector(
                (subject != null) ? ontModel.createResource(subject) : (Resource) null,
                (property != null) ? ontModel.createProperty(property) : (Property) null,
                (object != null) ? ontModel.createResource(object) : (Resource) null
        );
        StmtIterator stmtIterator = ontModel.listStatements(selector);
        while (stmtIterator.hasNext()) {
            Statement statement = stmtIterator.next();
            statements.add(statement);
        }
        dataset.commit();
        dataset.end();
        return statements;
//        dataset.begin(ReadWrite.READ);
//        if (model != null) model.close();
//        dataset.end();
    }

    public List<Statement> getData(String modelName) {
        List<Statement> statementList = new ArrayList<>();
        dataset.begin(ReadWrite.READ);
        try {
            ontModel = dataset.getNamedModel(modelName);
            StmtIterator stmtIterator = ontModel.listStatements();
            while (stmtIterator.hasNext()) {
                Statement statement = stmtIterator.next();
                statementList.add(statement);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dataset.end();
        }
        return statementList;
    }

    /**
     * 删除model中的三元组
     *
     * @param modelName
     * @param subject
     * @param property
     * @param object
     */
    public void removeStatement(String modelName, String subject, String property, String object) {
        dataset.begin(ReadWrite.WRITE);
        try {
            ontModel = dataset.getNamedModel(modelName);
            Statement stmt = ontModel.createStatement
                    (
                            ontModel.createResource(subject),
                            ontModel.createProperty(property),
                            ontModel.createResource(object)
                    );
            ontModel.remove(stmt);
            dataset.commit();
            log.info("删除三元组： " + subject + " " + property + " " + object);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dataset.end();
        }
    }

    public Model exportModelToFile(String modelName, String newFilePath) {
        ontModel = dataset.getNamedModel(modelName);
        dataset.begin(ReadWrite.WRITE);
        try {
            FileOutputStream outputStream = new FileOutputStream(newFilePath);
            ontModel.write(outputStream, "N3");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        dataset.commit();
        dataset.end();
        return ontModel;
    }

    public void queryJson(String modelName, String queryStr) {
        dataset.begin(ReadWrite.READ);
        Model model = dataset.getNamedModel(modelName);
        OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM, model);
        Query query = QueryFactory.create(queryStr, Syntax.syntaxARQ);
        QueryExecution execution = QueryExecutionFactory.create(query, ontModel);
        try {
            Iterator<JsonObject> jsonItems = execution.execJsonItems();
            while (jsonItems.hasNext()) {
                JsonObject object = jsonItems.next();
                System.out.println(object);
            }
//            ResultSetFormatter.out(System.out, jsonItems, query);
//
        } finally {
            dataset.end();
            execution.close();
        }
    }

    public ResultSet queryData(String modelName, String queryStr) {
        dataset.begin(ReadWrite.READ);
        Model model = dataset.getNamedModel(modelName);
        OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM, model);
        Query query = QueryFactory.create(queryStr);
        QueryExecution execution = QueryExecutionFactory.create(query, ontModel);
        try {
            ResultSet resultSet = execution.execSelect();
            ResultSetFormatter.out(System.out, resultSet, query);
            return resultSet;
        } finally {
            dataset.end();
            execution.close();
        }
    }

    public void queryDataFromFile(Model model, String query) {
        QueryFactory.create(query);
        QueryExecution qexec = QueryExecutionFactory.create(query, model);
        try {
            ResultSet response = qexec.execSelect();

            while (response.hasNext()) {
                QuerySolution soln = response.nextSolution();
                RDFNode name = soln.get("?object");
                if (name != null) {
                    System.out.println("Hello to " + name.toString());
                }
            }
        } finally {
            qexec.close();
        }

    }

    public void findProductById(Object id) {
        dataset.begin(ReadWrite.READ);
        Model sell = dataset.getNamedModel("sell");
        String queryStr = PREFIX_QXHOLY + "SELECT * WHERE { ?productId qx:productId ?value } ";
        QueryFactory.create();
    }

}

