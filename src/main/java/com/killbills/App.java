package com.killbills;

import io.github.cdimascio.dotenv.Dotenv;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Hello world!
 *
 */
public class App {
    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();
        System.out.println("Hello World!");
        int sum = Sdk.add(8, 2); // Use the add method from the imported package
        System.out.println("Sum: " + sum);
        List<Map<String, Object>> stores = Sdk.getStores("prod", dotenv.get("API_KEY"));
        System.out.println("Sum: " + stores);
        Map<String, Object> data = new HashMap<>();
        data.put("id", 221188271);
        data.put("reference_id", "12345");
        data.put("amount", 100.50);
        data.put("currency", "USD");
        data.put("date", "2023-08-07T14:30:00");
        data.put("covers", 4);
        data.put("ref", 63);
        data.put("mode", "1");
        data.put("user", null);
        data.put("uuid", "00000-64d25f50-4e34ae");
        data.put("price", 900);
        data.put("table", "0");
        data.put("covers", 0);
        data.put("source", 5);
        data.put("status", 255);
        data.put("channel", "Borne");
        data.put("comment", null);
        data.put("loyalty", 0);

        List<Map<String, Object>> items = new ArrayList<>();
        Map<String, Object> item1 = new HashMap<>();
        item1.put("tax", createTaxMap(1000, 85, "TVA"));
        item1.put("name", "Salade Cesar");
        item1.put("price", 850);
        item1.put("quantity", 1);
        item1.put("sub_items", createSubItems());
        item1.put("description", "");
        item1.put("reference_id", "1c49ad5c-2610-4bd7-bbb5-e235639a0a42");
        item1.put("total_amount", 850);
        items.add(item1);

        Map<String, Object> item2 = new HashMap<>();
        item2.put("tax", createTaxMap(2000, 190, "TVA"));
        item2.put("name", "Petit-déjeuner anglais");
        item2.put("price", 950);
        item2.put("quantity", 1);
        item2.put("sub_items", createSubItems());
        item2.put("description", "");
        item2.put("reference_id", "1c49adnn5c-2610-4bd7-bbb5-e235639a0a42");
        item2.put("total_amount", 950);
        items.add(item2);

        data.put("items", items);

        data.put("customer", null);
        data.put("due_date", null);
        data.put("device_id", null);
        data.put("remote_id", "tabesto_order_c7316c0170d1075d6c6dac1c2a3c6710");
        data.put("created_at", "2023-08-08T17:29:20+02:00");
        Map<String, Object> store = new HashMap<>();
        store.put("name", "RESTAU TEST");
        store.put("store_name", "RESTAU TEST");
        store.put("billing_descriptor", "RESTAU TEST");
        store.put("siret", "6789");
        Map<String, Object> address = new HashMap<>();
        address.put("city", "Paris");
        address.put("number", 0);
        address.put("country", "FRANCE");
        address.put("postal_code", 75014);
        address.put("street_address", "17 rue du Smart Receipt");
        store.put("address", address);
        store.put("code_ape", "4410");
        store.put("tva_intra", "FR 000 000 00");
        store.put("reference_id", "1");
        store.put("business_name", "RESTAU TEST");
        data.put("store", store);
        Map<String, Object> merchant = new HashMap<>();
        merchant.put("name", "Restaurant test");
        merchant.put("reference_id", "1234");
        data.put("merchant", merchant);
        List<Map<String, Object>> transactions = new ArrayList<>();
        Map<String, Object> transaction = new HashMap<>();
        transaction.put("date", "2023-08-08T17:32:27+02:00");
        transaction.put("price", 900);
        transaction.put("method", "Espèce");
        transaction.put("device_id", 80362);
        transactions.add(transaction);
        data.put("transactions", transactions);

        data.put("id_restaurant", 911);
        data.put("total_discount", new BigDecimal("0"));
        data.put("virtual_brand_name", null);
        data.put("closed_by_device_id", 80362);
        data.put("partner_name", "clyo");
        List<Map<String, Object>> payments = new ArrayList<>();
        Map<String, Object> payment = new HashMap<>();
        payment.put("bin", "0");
        payment.put("amount", 177228);
        payment.put("scheme", "");
        payment.put("auth_code", "");
        payment.put("last_four", 0);
        payment.put("payment_type", "CB");
        payment.put("transaction_id", null);
        payment.put("transaction_date", "2023-08-09T04:08:19.873");
        payments.add(payment);
        data.put("payments", payments);

        System.out.println("Sum: " + Sdk.sendReceipt("test", data, dotenv.get("HMAC_RECEIPT")));
        Map<String, Object> payload = new HashMap<>();

        // Set values for bank_id, transaction, callback_url, partner_name, and
        // receipt_format
        payload.put("bank_id", "fbec0cb5-91c8-4b8b-a194-c018fbfe258d");
        payload.put("callback_url", "https://eolpqff4hbbj6q5.m.pipedream.net");
        payload.put("partner_name", "mooncard");
        payload.put("receipt_format", "PDF");

        // Create and set values for the transaction object
        Map<String, Object> transactiont = new HashMap<>();
        transactiont.put("siret", "123456789");
        transactiont.put("amount", (double) 122);

        Map<String, Object> paymentt = new HashMap<>();
        paymentt.put("bin", "487179");
        paymentt.put("scheme", "VISA");
        paymentt.put("lastFour", "1234");
        paymentt.put("auth_code", "a27s92");
        paymentt.put("transaction_id", "aucao_31677a");
        transactiont.put("payment", paymentt);

        transactiont.put("currency", "EUR");
        transactiont.put("store_name", "RESTAU TEST");
        transactiont.put("customer_id", "testpayloadFromKB");
        transactiont.put("merchant_id", "1cce2012-48bb-470d-9abb-67733ff5b158");
        transactiont.put("reference_id", "bc851e57-27ee-452c-aacf-7253ead56f8d");
        transactiont.put("merchant_name", "KillBills");
        transactiont.put("transaction_date", new BigDecimal(1688377878));
        transactiont.put("billing_descriptor", "TEST3");

        payload.put("transaction", transactiont);
        System.out.println(payload);
        System.out.println("tr: " + Sdk.sendBankingTransaction("test", payload, dotenv.get("HMAC_TRANSACTION")));
    }

    private static Map<String, Object> createTaxMap(int rate, int amount, String description) {
        Map<String, Object> tax = new HashMap<>();
        tax.put("rate", rate);
        tax.put("amount", amount);
        tax.put("description", description);
        return tax;
    }

    private static List<Map<String, Object>> createSubItems() {
        List<Map<String, Object>> subItems = new ArrayList<>();
        // Create and add sub-items here...
        return subItems;
    }

}
