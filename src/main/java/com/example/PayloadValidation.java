package com.example;

import org.apache.commons.validator.routines.BigDecimalValidator;
import org.apache.commons.validator.routines.DateValidator;
import org.apache.commons.validator.routines.IntegerValidator;
import org.apache.commons.validator.routines.UrlValidator;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class PayloadValidation {

    private static final String[] VALID_SCHEMES = { "JSON", "PDF", "SVG", "PNG" };
    private static final Pattern DATE_PATTERN = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}$");

    private static final DateValidator DATE_VALIDATOR = DateValidator.getInstance();
    private static final BigDecimalValidator BIG_DECIMAL_VALIDATOR = BigDecimalValidator.getInstance();
    private static final IntegerValidator INTEGER_VALIDATOR = IntegerValidator.getInstance();
    private static final UrlValidator URL_VALIDATOR = UrlValidator.getInstance();

    public static boolean validateTransactionPayload(Map<String, Object> payload) {
        try {
            if (payload == null)
                throw new ValidationException("No payload to validate");

            String bank_id = (String) payload.get("bank_id");
            String callback_url = (String) payload.get("callback_url");
            String receipt_format = (String) payload.get("receipt_format");

            if (bank_id == null || callback_url == null || receipt_format == null) {
                throw new ValidationException("Missing required fields");
            }

            if (bank_id.length() != 36) {
                throw new ValidationException("bank_id must be exactly 36 characters");
            }

            if (!URL_VALIDATOR.isValid(callback_url)) {
                throw new ValidationException("Invalid callback_url");
            }

            if (!Stream.of(VALID_SCHEMES).anyMatch(receipt_format::equalsIgnoreCase)) {
                throw new ValidationException("Invalid receipt_format");
            }

            // Validate transaction object
            Map<String, Object> transaction = (Map<String, Object>) payload.get("transaction");
            if (transaction == null) {
                throw new ValidationException("Missing transaction object");
            }

            String reference_id = (String) transaction.get("reference_id");
            Double amount = BIG_DECIMAL_VALIDATOR.validate(transaction.get("amount").toString()).doubleValue();
            String customer_id = (String) transaction.get("customer_id");
            String transaction_date = (String) transaction.get("transaction_date");
            String store_name = (String) transaction.get("store_name");
            String billing_descriptor = (String) transaction.get("billing_descriptor");
            String siret = (String) transaction.get("siret");
            String currency = (String) transaction.get("currency");
            String merchant_name = (String) transaction.get("merchant_name");

            if (reference_id == null || amount == null || customer_id == null ||
                    transaction_date == null || store_name == null || billing_descriptor == null) {
                throw new ValidationException("Missing required fields in transaction object");
            }

            if (!DATE_PATTERN.matcher(transaction_date).matches()) {
                throw new ValidationException("Invalid transaction_date format");
            }

            if (siret != null && siret.length() != 14) {
                throw new ValidationException("siret must be exactly 14 characters");
            }

            if (!"EUR".equalsIgnoreCase(currency) && !"USD".equalsIgnoreCase(currency)) {
                throw new ValidationException("Invalid currency");
            }

            // Validate payment object within the transaction
            Map<String, Object> payment = (Map<String, Object>) transaction.get("payment");
            if (payment != null) {
                String bin = (String) payment.get("bin");
                String last_four = (String) payment.get("last_four");
                String auth_code = (String) payment.get("auth_code");
                String scheme = (String) payment.get("scheme");
                String transaction_id = (String) payment.get("transaction_id");

                if (bin != null && bin.isEmpty()) {
                    throw new ValidationException("bin cannot be empty");
                }

                if (last_four != null && last_four.isEmpty()) {
                    throw new ValidationException("last_four cannot be empty");
                }

                if (auth_code != null && auth_code.isEmpty()) {
                    throw new ValidationException("auth_code cannot be empty");
                }

                if (scheme != null && scheme.isEmpty()) {
                    throw new ValidationException("scheme cannot be empty");
                }

                if (transaction_id != null && transaction_id.isEmpty()) {
                    throw new ValidationException("transaction_id cannot be empty");
                }

                // Validate other payment fields as needed

                // ... Continue validating payment fields
            }

            // ... Continue validating other fields within the transaction object

            return true;
        } catch (ValidationException e) {
            return false;
        }
    }

    // ... Other methods and main() method here

    public static boolean validateReceiptPayload(Map<String, Object> payload) {
        try {
            if (payload == null)
                throw new ValidationException("No payload to validate");

            String reference_id = (String) payload.get("reference_id");
            Double amount = BIG_DECIMAL_VALIDATOR.validate(payload.get("amount").toString()).doubleValue();
            Double total_tax_amount = BIG_DECIMAL_VALIDATOR.validate(payload.get("total_tax_amount").toString())
                    .doubleValue();
            String currency = (String) payload.get("currency");
            String date = (String) payload.get("date");
            Integer covers = INTEGER_VALIDATOR.validate(payload.get("covers").toString());
            String table = (String) payload.get("table");
            Integer invoice = INTEGER_VALIDATOR.validate(payload.get("invoice").toString());
            Double total_discount = BIG_DECIMAL_VALIDATOR.validate(payload.get("total_discount").toString())
                    .doubleValue();
            String mode = (String) payload.get("mode");
            String partner_name = (String) payload.get("partner_name");

            if (reference_id == null || amount == null || currency == null || date == null || partner_name == null) {
                throw new ValidationException("Missing required fields in receipt payload");
            }

            if (!DATE_PATTERN.matcher(date).matches()) {
                throw new ValidationException("Invalid date format");
            }

            if (!"EUR".equalsIgnoreCase(currency) && !"USD".equalsIgnoreCase(currency)) {
                throw new ValidationException("Invalid currency");
            }

            // Validate merchant object within the receipt payload
            Map<String, Object> merchant = (Map<String, Object>) payload.get("merchant");
            if (merchant == null) {
                throw new ValidationException("Missing merchant object");
            }

            String merchant_name = (String) merchant.get("merchant_name");
            String merchant_reference_id = (String) merchant.get("reference_id");
            Integer merchant_id = INTEGER_VALIDATOR.validate(merchant.get("merchant_id").toString());

            if (merchant_name == null || merchant_reference_id == null || merchant_id == null) {
                throw new ValidationException("Missing required fields in merchant object");
            }

            // Validate store object within the receipt payload
            Map<String, Object> store = (Map<String, Object>) payload.get("store");
            if (store == null) {
                throw new ValidationException("Missing store object");
            }

            String store_name = (String) store.get("store_name");
            String store_reference_id = (String) store.get("reference_id");
            String billing_descriptor = (String) store.get("billing_descriptor");
            String siret = (String) store.get("siret");
            String code_ape = (String) store.get("code_ape");
            String tva_intra = (String) store.get("tva_intra");

            if (store_name == null || store_reference_id == null || billing_descriptor == null || siret == null) {
                throw new ValidationException("Missing required fields in store object");
            }

            if (siret.length() != 14) {
                throw new ValidationException("siret must be exactly 14 characters");
            }

            // Validate address object within the store object
            Map<String, Object> address = (Map<String, Object>) store.get("address");
            if (address == null) {
                throw new ValidationException("Missing address object in store");
            }

            String postal_code = (String) address.get("postal_code");
            String street_address = (String) address.get("street_address");
            String country = (String) address.get("country");
            String city = (String) address.get("city");
            String full_address = (String) address.get("full_address");
            Integer number = INTEGER_VALIDATOR.validate(address.get("number").toString());

            if (postal_code == null || street_address == null || country == null || city == null
                    || full_address == null) {
                throw new ValidationException("Missing required fields in address object");
            }

            // Validate taxes array within the receipt payload
            List<Map<String, Object>> taxes = (List<Map<String, Object>>) payload.get("taxes");
            if (taxes != null) {
                for (Map<String, Object> tax : taxes) {
                    String tax_description = (String) tax.get("description");
                    Double tax_amount = BIG_DECIMAL_VALIDATOR.validate(tax.get("amount").toString()).doubleValue();
                    Integer tax_rate = INTEGER_VALIDATOR.validate(tax.get("rate").toString());

                    if (tax_description == null || tax_amount == null || tax_rate == null) {
                        throw new ValidationException("Missing required fields in tax object");
                    }

                    if (tax_rate != 550 && tax_rate != 1000 && tax_rate != 2000) {
                        throw new ValidationException("Invalid tax rate");
                    }

                    // ... Continue validating tax fields
                }
            }

            // Validate items array within the receipt payload
            List<Map<String, Object>> items = (List<Map<String, Object>>) payload.get("items");
            if (items != null) {
                for (Map<String, Object> item : items) {
                    String item_reference_id = (String) item.get("reference_id");
                    String item_name = (String) item.get("name");
                    String item_description = (String) item.get("description");
                    String item_type = (String) item.get("type");
                    Integer item_quantity = INTEGER_VALIDATOR.validate(item.get("quantity").toString());
                    Double item_price = BIG_DECIMAL_VALIDATOR.validate(item.get("price").toString()).doubleValue();
                    Double item_discount = BIG_DECIMAL_VALIDATOR.validate(item.get("discount").toString())
                            .doubleValue();
                    Double item_total_amount = BIG_DECIMAL_VALIDATOR.validate(item.get("total_amount").toString())
                            .doubleValue();

                    if (item_reference_id == null || item_name == null || item_quantity == null || item_price == null) {
                        throw new ValidationException("Missing required fields in item object");
                    }

                    if (item_discount < 0 || item_discount > 100) {
                        throw new ValidationException("Invalid item discount");
                    }

                    // ... Continue validating item fields

                    // Validate subitems array within the item object
                    List<Map<String, Object>> subitems = (List<Map<String, Object>>) item.get("subitems");
                    if (subitems != null) {
                        for (Map<String, Object> subitem : subitems) {
                            String subitem_reference_id = (String) subitem.get("reference_id");
                            String subitem_name = (String) subitem.get("name");
                            String subitem_description = (String) subitem.get("description");
                            Integer subitem_quantity = INTEGER_VALIDATOR.validate(subitem.get("quantity").toString());
                            Double subitem_price = BIG_DECIMAL_VALIDATOR.validate(subitem.get("price").toString())
                                    .doubleValue();
                            Double subitem_discount = BIG_DECIMAL_VALIDATOR.validate(subitem.get("discount").toString())
                                    .doubleValue();
                            Double subitem_total_amount = BIG_DECIMAL_VALIDATOR
                                    .validate(subitem.get("total_amount").toString()).doubleValue();

                            if (subitem_reference_id == null || subitem_name == null || subitem_quantity == null
                                    || subitem_price == null) {
                                throw new ValidationException("Missing required fields in subitem object");
                            }

                            // ... Continue validating subitem fields
                        }
                    }

                    // ... Continue validating other fields within the item object
                }
            }

            // Validate payments array within the receipt payload
            List<Map<String, Object>> payments = (List<Map<String, Object>>) payload.get("payments");
            if (payments != null) {
                for (Map<String, Object> payment : payments) {
                    String payment_bin = (String) payment.get("bin");
                    String payment_last_four = (String) payment.get("last_four");
                    String payment_auth_code = (String) payment.get("auth_code");
                    String payment_scheme = (String) payment.get("scheme");
                    Double payment_amount = BIG_DECIMAL_VALIDATOR.validate(payment.get("amount").toString())
                            .doubleValue();
                    String payment_transaction_date = (String) payment.get("transaction_date");
                    String payment_transaction_id = (String) payment.get("transaction_id");
                    String payment_payment_type = (String) payment.get("payment_type");

                    // Validate payment fields as needed
                    // ... Continue validating payment fields
                }
            }

            // ... Continue validating other fields within the receipt payload

            // Return true if all validations passed
            return true;

        } catch (ValidationException e) {
            return false;
        }
    }

    private static class ValidationException extends Exception {
        ValidationException(String message) {
            super(message);
        }
    }
}
