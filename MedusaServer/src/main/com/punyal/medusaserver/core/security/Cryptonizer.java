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
package com.punyal.medusaserver.core.security;

import com.punyal.medusaserver.utils.UnitConversion;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Cryptonizer
 * @author Pablo Puñal Pereira {@literal (pablo @ punyal.com)}
 * @version 0.2
 */
public class Cryptonizer {
    
    public void Cryptonizer(){}
    
    /**
     * Method to encrypt passwords on RADIUS protocol
     * @param secretKey to use on the encryption
     * @param authenticator to use on the encryption
     * @param passWord to encrypt
     * @return encrypted password
     */
    public static String encrypt(String secretKey, String authenticator, String passWord) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] b_secretKey = UnitConversion.stringToByteArray(secretKey);
            byte[] b_authenticator = UnitConversion.hexStringToByteArray(authenticator);
            byte[] b_password = UnitConversion.stringToByteArray(passWord);
            
            if(b_authenticator.length != 16)
            throw new IllegalArgumentException("Authenticator with wrong length: "+b_authenticator.length);
            
            int len = 0;
            int tot_len = 0;
            
            // Check final length to prevent errors
            if(b_password.length%16 != 0) tot_len = 16;
            tot_len += ((int)b_password.length/16)*16;
            
            // Create crypted array
            byte[] crypted = new byte[tot_len];
            
            byte[] b_temp = new byte[b_secretKey.length+b_authenticator.length];
            byte[] c_temp = new byte[16];
            System.arraycopy(b_secretKey, 0, b_temp, 0, b_secretKey.length);
            System.arraycopy(b_authenticator, 0, b_temp, b_secretKey.length, b_authenticator.length);
            b_temp = md.digest(b_temp);

            while(len < tot_len) {
                // Copy the 16th bytes to XOR
                if((b_password.length - len) < 16) {
                    System.arraycopy(b_password, len, c_temp, 0, b_password.length-len);
                    for(int i=b_password.length-len; i<16; i ++) 
                        c_temp[i] = 0;
                } else System.arraycopy(b_password, len, c_temp, 0, 16);

                for(int i=0; i<16; i++)
                    c_temp[i] = (byte)(0xFF & ((int)c_temp[i]) ^((int)b_temp[i]));

                System.arraycopy(c_temp, 0, crypted, len, 16);
                len += 16;
            }
            return UnitConversion.ByteArray2Hex(crypted);
            
        } catch(NoSuchAlgorithmException ex) {
            System.err.println("No Such Arlgorithm Exception "+ ex);
        }
        return null;
    }
    
    /**
     * Method to encrypt passwords optimized for CoAP clients using hex strings
     * @param secretKey to use on encryption
     * @param authenticator to use on encryption
     * @param passWord to encrypt
     * @return encrypted password
     */
    public static String encryptCoAP(String secretKey, String authenticator, String passWord) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] b_secretKey = UnitConversion.stringToByteArray(secretKey);
            byte[] b_authenticator = UnitConversion.hexStringToByteArray(authenticator);
            byte[] b_password = UnitConversion.stringToByteArray(passWord);
            
            if(b_authenticator.length != 16)
            throw new IllegalArgumentException("Authenticator with wrong length: "+b_authenticator.length);
            
            int len = 0;
            int tot_len = 0;
            // Check final length to prevent errors
            if(b_password.length%16 != 0) tot_len = 16;
            tot_len += ((int)b_password.length/16)*16;
            
            // Create crypted array
            byte[] crypted = new byte[tot_len];
            
            byte[] b_temp = new byte[b_secretKey.length+b_authenticator.length];
            byte[] c_temp = new byte[16];
            System.arraycopy(b_secretKey, 0, b_temp, 0, b_secretKey.length);
            System.arraycopy(b_authenticator, 0, b_temp, b_secretKey.length, b_authenticator.length);
            b_temp = md.digest(UnitConversion.stringToByteArray(UnitConversion.ByteArray2Hex(b_temp)));

            while(len < tot_len) {
                // Copy the 16th bytes to XOR
                if((b_password.length - len) < 16) {
                    System.arraycopy(b_password, len, c_temp, 0, b_password.length-len);
                    for(int i=b_password.length-len; i<16; i ++) 
                        c_temp[i] = 0;
                } else System.arraycopy(b_password, len, c_temp, 0, 16);

                for(int i=0; i<16; i++)
                    c_temp[i] = (byte)(0xFF & ((int)c_temp[i]) ^((int)b_temp[i]));

                System.arraycopy(c_temp, 0, crypted, len, 16);
                len += 16;
            }
            return UnitConversion.ByteArray2Hex(crypted);
            
        } catch(NoSuchAlgorithmException ex) {
            System.err.println("No Such Arlgorithm Exception "+ ex);
        }
        return null;
    }
}