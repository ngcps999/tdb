package com.qxholy.www.utils;

import org.springframework.stereotype.Component;

/**
 * @Project kg01
 * @Package: com.qxholy.kg01.utils
 * @Author xuda
 * @Date 2020/2/17 8:15 下午
 */
@Component
public class Constants {

    public final static String QX_URL = "http://www.qxholy.com/";
    public final static String M_SELL = "sell";
    public final static String PRODUCT_CLASS = QX_URL+"Product";


    public final static String PREFIX_QXHOLY = "PREFIX qx: <http://www.qxholy.com/>";
    public final static String PREFIX_XSD = "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> ";
    public final static String PREFIX_RDF = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>";
    public final static String PREFIX_RDFS = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>";
    public final static String PREFIX_OWL = "PREFIX owl: <http://www.w3.org/2002/07/owl#>";

    public final static String PREFIX_QX = PREFIX_QXHOLY + PREFIX_XSD + PREFIX_RDF + PREFIX_RDFS + PREFIX_OWL;
}
