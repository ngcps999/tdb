package com.qxholy.www.utils;

import com.qxholy.www.dao.TDBConnection;
import org.apache.jena.fuseki.main.FusekiServer;
import org.apache.jena.fuseki.main.cmds.FusekiMain;
import org.apache.jena.query.Dataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Project kg01
 * @Package: com.qxholy.kg01.dao
 * @Author xuda
 * @Date 2020/2/21 3:49 下午
 */
public class FusekiServerXd {

    public static void main(String[] args) {
        FusekiServer fusekiServer = FusekiServer.create()
                .port(3230)
//                .loopback(true)
//                .parseConfigFile("/Users/xuda/Downloads/apache-jena-fuseki-3.13.1/run/configuration/xd.ttl")
                .add("/rdf", TDBConnection.dataset, true)
                .build();
        fusekiServer.start();
    }
}
