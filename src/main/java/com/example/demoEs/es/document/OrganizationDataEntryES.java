package com.example.demoEs.es.document;


import com.example.demoEs.constant.ConfigConstant;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Document(indexName = "organization_data_entry")
@Setting(settingPath = "elasticsearch/analysis-config.json")
public class OrganizationDataEntryES {

    @Id
    private String uuid;
    @Field( type = FieldType.Text)
    private String type;
    @NotNull(message = "legalDocType is required")

    @Field(type = FieldType.Keyword)
    private String legalDocType;

    // Thông tin tổ chức
    @NotBlank(message = "organizationName is required")
    @Field(type = FieldType.Text)
    private String organizationName;

    @NotBlank(message = "taxCode is required")
    @Field(type = FieldType.Text)
    private String taxCode;

    @NotNull(message = "issueDate is required")
    @Field(type = FieldType.Date)
    private LocalDate issueDate;

    @NotBlank(message = "issuePlace is required")
    @Field(type = FieldType.Text)
    private String issuePlace;

    @NotBlank(message = "organizationPhone is required")
    @Field(type = FieldType.Keyword)
    private String organizationPhone;

    @NotBlank(message = "organizationAddress is required")
    @Field(type = FieldType.Text)
    private String organizationAddress;

    // Thông tin đại diện tổ chức
    @NotBlank(message = "representativeFullName is required")
    @Field(type = FieldType.Text)
    private String representativeFullName;

    @NotBlank(message = "representativeIdCardNumber is required")
    @Field(type = FieldType.Keyword)
    private String representativeIdCardNumber;

    @NotNull(message = "representativeIssueDate is required")
    @Field(type = FieldType.Date)
    private LocalDate representativeIssueDate;

    @NotBlank(message = "representativeIssuePlace is required")
    @Field(type = FieldType.Text)
    private String representativeIssuePlace;

    @Field(type = FieldType.Text)
    private String otherInfo;


    @Field(type = FieldType.Text)
    private String releaseStatus;

    @Field(type = FieldType.Text , analyzer = ConfigConstant.ANALYZE_ES , searchAnalyzer = ConfigConstant.ANALYZE_ES)
    private String content;

    private String updatedBy;

    private Date lastModifiedDate;

}
