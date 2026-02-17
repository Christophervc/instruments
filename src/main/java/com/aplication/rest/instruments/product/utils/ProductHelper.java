package com.aplication.rest.instruments.product.utils;

import com.aplication.rest.instruments.product.Product;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

@Component

public class ProductHelper {

    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

    public String generateSlug(String input, UUID id) {
        if (input == null) throw new IllegalArgumentException("Input cannot be null");
        if (id == null) throw new IllegalArgumentException("Id cannot be null");

        String nowhitespace = WHITESPACE.matcher(input).replaceAll("-"); //replace all whitespaces
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = NONLATIN.matcher(normalized).replaceAll("");
        String cleanSlug = slug.toLowerCase(Locale.ENGLISH);
        cleanSlug = cleanSlug.replaceAll("-+", "-");
        cleanSlug = cleanSlug.replaceAll("^-|-$", "");
        String uniqueSuffix = id.toString().substring(24);

        return cleanSlug + "-" + uniqueSuffix;
    }

    public String generateSku(Product product) {
        String manufacturerCode = "GEN";
        if (product.getManufacturer() != null && product.getManufacturer().getName() != null) {
            manufacturerCode = getFirstChars(product.getManufacturer().getName(), 3);
        }
        String modelCode = "INS";
        if (product.getName() != null) {
            modelCode = getFirstChars(product.getName(), 3);
        }
        String randomID = UUID.randomUUID().toString().substring(0,4);
        return (manufacturerCode + "-" + modelCode + "-" + randomID).toUpperCase();
    }

    private String getFirstChars(String text, int length) {
        String cleanText = text.replaceAll("\\s+", "");
        return cleanText.substring(0, Math.min(cleanText.length(), length));
    }

}
