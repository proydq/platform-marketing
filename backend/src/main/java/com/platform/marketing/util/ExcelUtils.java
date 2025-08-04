package com.platform.marketing.util;

import com.platform.marketing.dto.CustomerImportDto;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ExcelUtils {
    public static List<CustomerImportDto> parseExcel(InputStream inputStream) {
        List<CustomerImportDto> list = new ArrayList<>();
        try {
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                CustomerImportDto dto = new CustomerImportDto();
                dto.setName(row.getCell(0).getStringCellValue());
                dto.setPhone(row.getCell(1).getStringCellValue());
                dto.setEmail(row.getCell(2).getStringCellValue());
                dto.setCompany(row.getCell(3).getStringCellValue());
                dto.setPosition(row.getCell(4).getStringCellValue());
                dto.setRemark(row.getCell(5).getStringCellValue());
                list.add(dto);
            }
            workbook.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return list;
    }
}
