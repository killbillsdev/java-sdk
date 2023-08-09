package com.killbills;

import org.apache.commons.validator.routines.BigDecimalValidator;
import org.apache.commons.validator.routines.DateValidator;
import org.apache.commons.validator.routines.IntegerValidator;
import org.apache.commons.validator.routines.UrlValidator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
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
        List<String> errors = new ArrayList<>();
        validateField(payload, "bank_id", String.class, true, errors, "^.{36}$");
        validateField(payload, "callback_url", String.class, true, errors);
        validateField(payload, "receipt_format", String.class, true, errors, "JSON", "PDF", "SVG", "PNG");
        Map<String, Object> transaction = (Map<String, Object>) payload.get("transaction");
        if (transaction != null) {
            validateField(transaction, "reference_id", String.class, true, errors);
            validateField(transaction, "amount", Double.class, true, errors);
            validateField(transaction, "customer_id", String.class, true, errors);
            validateField(transaction, "transaction_date", BigDecimal.class, true, errors);
            validateField(transaction, "store_name", String.class, true, errors);
            validateField(transaction, "billing_descriptor", String.class, true, errors);
            validateField(transaction, "siret", String.class, false, errors, "^[0-9]+$");

            Map<String, Object> payment = (Map<String, Object>) transaction.get("payment");
            if (payment != null) {
                validateField(payment, "bin", String.class, false, errors);
                validateField(payment, "last_four", String.class, false, errors);
                validateField(payment, "auth_code", String.class, false, errors);
                validateField(payment, "scheme", String.class, false, errors);
                validateField(payment, "transaction_id", String.class, false, errors);
            }

            validateField(transaction, "currency", String.class, false, errors, "EUR", "USD");
            validateField(transaction, "merchant_name", String.class, false, errors);
        }
        System.out.println("test" + errors);
        return errors.isEmpty();
    }

    private static void validateField(Map<String, Object> payload, String fieldName,
            Class<?> fieldType, boolean required, List<String> errors, Object... allowedValues) {
        Object value = payload.get(fieldName);

        if (value == null) {
            if (required) {
                errors.add("Missing required field: " + fieldName);
            }
            return;
        }

        if (!fieldType.isInstance(value)) {
            errors.add("Invalid data type for field '" + fieldName + "': expected " + fieldType.getSimpleName());
            return;
        }

        if (allowedValues.length > 0 && !isValueAllowed(value, allowedValues)) {
            errors.add("Invalid value for field '" + fieldName + "': allowed values are "
                    + Arrays.toString(allowedValues));
        }
    }

    private static boolean isValueAllowed(Object value, Object[] allowedValues) {
        for (Object allowedValue : allowedValues) {
            if (allowedValue instanceof String && value instanceof String) {
                String regex = (String) allowedValue;
                if (isValidRegex(regex) && matchesRegex((String) value, regex)) {
                    return true;
                }
            } else if (allowedValue.equals(value)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isValidRegex(String regex) {
        try {
            Pattern.compile(regex);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean matchesRegex(String value, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }
    // ... Other methods and main() method here

    public static boolean validateReceiptPayload(Map<String, Object> payload) {
        List<String> errors = new ArrayList<>();

        validateField(payload, "reference_id", String.class, true, errors);
        validateField(payload, "amount", Double.class, true, errors);
        validateField(payload, "total_tax_amount", BigDecimal.class, false, errors);
        validateField(payload, "currency", String.class, true, errors, "EUR", "USD");
        validateField(payload, "date", String.class, true, errors,
                "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(?:\\.\\d{3})?$");
        validateField(payload, "covers", Integer.class, false, errors);
        validateField(payload, "table", String.class, false, errors);
        validateField(payload, "invoice", Integer.class, false, errors);
        validateField(payload, "total_discount", BigDecimal.class, false, errors);
        validateField(payload, "mode", String.class, false, errors);
        validateField(payload, "partner_name", String.class, true, errors);

        // Validate merchant
        Map<String, Object> merchant = (Map<String, Object>) payload.get("merchant");
        if (merchant != null) {
            validateField(merchant, "merchant_name", String.class, false, errors);
            validateField(merchant, "reference_id", String.class, true, errors);
            validateField(merchant, "merchant_id", Integer.class, false, errors);
        } else {
            errors.add("Missing merchant object");
        }

        // Validate store
        Map<String, Object> store = (Map<String, Object>) payload.get("store");
        if (store != null) {
            validateField(store, "store_name", String.class, true, errors);
            validateField(store, "reference_id", String.class, true, errors);
            validateField(store, "billing_descriptor", String.class, true, errors);
            validateField(store, "siret", String.class, true, errors);
            validateField(store, "code_ape", String.class, false, errors);
            validateField(store, "tva_intra", String.class, false, errors);

            // Validate address
            Map<String, Object> address = (Map<String, Object>) store.get("address");
            if (address != null) {
                validateField(address, "postal_code", Integer.class, true, errors);
                validateField(address, "street_address", String.class, false, errors);
                validateField(address, "country", String.class, false, errors);
                validateField(address, "city", String.class, false, errors);
                validateField(address, "full_address", String.class, false, errors);
                validateField(address, "number", Integer.class, false, errors);
            } else {
                errors.add("Missing address object in store");
            }
        } else {
            errors.add("Missing store object");
        }

        // Validate taxes array
        List<Map<String, Object>> taxes = (List<Map<String, Object>>) payload.get("taxes");
        if (taxes != null) {
            for (Map<String, Object> tax : taxes) {
                validateField(tax, "description", String.class, false, errors);
                validateField(tax, "amount", Double.class, true, errors);
                validateField(tax, "rate", Integer.class, true, errors, 550, 1000, 2000);
            }
        }

        // Validate items array
        List<Map<String, Object>> items = (List<Map<String, Object>>) payload.get("items");
        if (items != null) {
            for (Map<String, Object> item : items) {
                validateField(item, "reference_id", String.class, false, errors);
                validateField(item, "name", String.class, true, errors);
                validateField(item, "description", String.class, false, errors);
                // ... continue validating other fields for items
            }
        }

        // Validate payments array
        List<Map<String, Object>> payments = (List<Map<String, Object>>) payload.get("payments");
        if (payments != null) {
            for (Map<String, Object> payment : payments) {
                validateField(payment, "bin", String.class, false, errors);
                // ... continue validating other fields for payments
            }
        }
        System.out.println("Sum: " + errors);
        return errors.isEmpty();
    }

    private static class ValidationException extends Exception {
        ValidationException(String message) {
            super(message);
        }
    }
}
