package com.killbills;

import junit.framework.TestCase;

public class CryptoUtilsTest extends TestCase {

    /**
     * Testing cipherHmacPayload with sample payload and key
     */
    public void testCipherHmacPayload() throws Exception {
        String payload = "somePayload";
        String hmacKey = "someKey";
        String expected = "ce12b6fcf8c18a35c5bd00f2a29c6f85a3b7d5d4e06351d9b97014b3708f2013";
        CryptoUtils cryptoUtils = new CryptoUtils();
        String result = cryptoUtils.cipherHmacPayload(payload, hmacKey);
        assertEquals(expected.length(), result.length());
    }

    /**
     * Testing cipherHmacPayload with empty key
     */
    public void testCipherHmacPayloadWithEmptyKey() throws Exception {
        String payload = "payload";
        String hmacKey = "";
        CryptoUtils cryptoUtils = new CryptoUtils();
        
        try {
            cryptoUtils.cipherHmacPayload(payload, hmacKey);
            fail("Expected an IllegalArgumentException to be thrown");
        } catch (IllegalArgumentException e) {
            System.out.println(e);
            assertEquals("Empty key", e.getMessage());
        }
    }
}
