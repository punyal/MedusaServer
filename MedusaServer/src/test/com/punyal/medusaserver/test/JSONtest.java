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
        
        String test1 = "{\"Uri-Path\":\"foo\",\"authenticator\":\"fjkasdfhjsdjhfdsf\"}";
        JSONObject json2 = (JSONObject)JSONValue.parse(test1);
        System.out.println(json2.get("Uri-Path"));
        System.out.println(json2.get("authenticator"));
        
        
        String test = "{\"Uri-Path\":\"helloWorld\", \"Block2\":2, \"Unknown (100)\":0x54}";
        System.out.println(test);
        test = test.replace("0x", "");
        System.out.println(test);
        JSONObject json3 = (JSONObject)JSONValue.parse(test);
        
        System.out.println(json3.get("Unknown (100)"));
        
        
        
        String real = "{\"Uri-Path\":\"helloWorld\", \"Unknown (100)\":0xc5f379b7b0a037a8}";
        System.out.println(real);
        real = real.replace("0x", "");
        System.out.println(real);
        JSONObject json4 = (JSONObject)JSONValue.parse(real);
        
        System.out.println(json4.get("Unknown (100)"));
        System.out.println("# Test (STOP)");
        
    }

}
    