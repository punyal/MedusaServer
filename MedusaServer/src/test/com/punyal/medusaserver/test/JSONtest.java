/*******************************************************************************
 * MedusaServer - Multi Protocol Access Control Server
 * 
 * Copyright (c) 2015 - Pablo Puñal Pereira <pablo@punyal.com>
 *                      EISLAB : Luleå University of Technology
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 ******************************************************************************/
package com.punyal.medusaserver.test;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import sun.management.snmp.jvminstr.JvmRTBootClassPathTableMetaImpl;

public class JSONtest {
    public static void main(String[] args) {
        System.out.println("# Test (START)");
        JSONObject json = new JSONObject();
        json.put("name", "foo");
        json.put("authenticator", "fjkasdfhjsdjhfdsf");
        System.out.println(json.toString());
        
       
        JSONObject json2 = (JSONObject)JSONValue.parse(json.toString());
        System.out.println(json2.get("name"));
        System.out.println(json2.get("authenticator"));
        
   
        
        System.out.println("# Test (STOP)");
    }

}
    