package com.yashaswi.expense_tracker_api.service;

import com.yashaswi.expense_tracker_api.dto.expense.ExportExpenseResponse;
import com.yashaswi.expense_tracker_api.entity.Expense;
import com.yashaswi.expense_tracker_api.mapper.EntityToDtoMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

@Service
@Slf4j
public class CsvExportService {
    public String exportExpensesToCSV(List<Expense> expenses) throws IOException {
        StringWriter sw = new StringWriter();
        try (CSVPrinter csvPrinter = new CSVPrinter(sw, CSVFormat.DEFAULT.builder().setHeader("ID", "Description", "Amount", "Data", "Category").build())) {
            for (Expense expense : expenses) {
                ExportExpenseResponse dto = EntityToDtoMapper.exportExpenseToDto(expense);
                csvPrinter.printRecord(
                        dto.getId(),
                        dto.getDescription(),
                        dto.getAmount(),
                        dto.getDate(),
                        dto.getExpenseCategory()
                );
            }
        }

        return sw.toString();
    }
}
