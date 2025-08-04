package com.platform.marketing.dto;

import lombok.Data;

@Data
public class CustomerImportDto {
    private String name;
    private String phone;
    private String email;
    private String company;
    private String position;
    private String remark;
}
