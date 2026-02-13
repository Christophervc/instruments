package com.aplication.rest.instruments.product.utils;

import com.aplication.rest.instruments.product.Product;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.IOException;
import java.util.stream.Stream;

public class ProductExcelExporter {
    private final Stream<Product> products;

    public ProductExcelExporter(Stream<Product> products) {
        this.products = products;
    }

    public void export(HttpServletResponse response) throws IOException {
        //using SXSSFWorkbook for large datasets (streaming) (100 rows in memory )
        try (SXSSFWorkbook workbook = new SXSSFWorkbook(100)) {
            Sheet sheet = workbook.createSheet("Products");

            // 2. adding styles
            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);

            // 3. adding headers
            Row headerRow = sheet.createRow(0);
            createCell(headerRow, 0, "ID", headerStyle);
            createCell(headerRow, 1, "Name", headerStyle);
            createCell(headerRow, 2, "SKU", headerStyle);
            createCell(headerRow, 3, "Price", headerStyle);
            createCell(headerRow, 4, "Stock", headerStyle);
            createCell(headerRow, 5, "Slug", headerStyle);
            createCell(headerRow, 6, "Type", headerStyle);
            createCell(headerRow, 7, "Active", headerStyle);
            createCell(headerRow, 8, "Manufacturer", headerStyle); // Relation
            // 4. writing data (Using Stream)
            // using an array to keep track of the row count
            int[] rowCount = {1};

            products.forEach(product -> {
                Row row = sheet.createRow(rowCount[0]++);

                createCell(row, 0, product.getId().toString(), null);
                createCell(row, 1, product.getName(), null);
                createCell(row, 2, product.getSku(), null);
                createCell(row, 3, product.getPrice().toString(), null);
                String stock = (product.getStock() != null) ? product.getStock().toString() : "N/A";
                createCell(row, 4, stock, null);
                createCell(row, 5, product.getSlug(), null);
                createCell(row, 6, product.getType().toString(), null);
                createCell(row, 7, product.getActive().toString(), null);

                // handling relation with Manufacturer (avoid NullPointerException)
                String manufName = (product.getManufacturer() != null)
                        ? product.getManufacturer().getName()
                        : "N/A";
                createCell(row, 8, manufName, null);
            });
            // 5. write  HTTP Response
            ServletOutputStream outputStream = response.getOutputStream();
            workbook.write(outputStream);
            // 6. Remove temp files created by SXSSF
            workbook.dispose();
            outputStream.close();

        }
    }

    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else {
            cell.setCellValue(value.toString());
        }
        if (style != null) {
            cell.setCellStyle(style);
        }
    }

}
