package com.example.demoEs.es.document;

import com.example.demoEs.constant.ConfigConstant;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
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
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class) // vẫn giữ để hỗ trợ nếu dùng snake_case
@Document(indexName = "personal_data_enty_index")
@Setting(settingPath = "elasticsearch/analysis-config.json")
public class PersonalDataEntryES  {
    @Id
    private String uuid; // ID trong Elasticsearch

    @Field(type = FieldType.Text)
    private String type;

    @Field(type = FieldType.Keyword)
    private String identificationDocType;

    @Field(type = FieldType.Text, analyzer = ConfigConstant.ANALYZE_ES)
    private String fullName;

    @Field(type = FieldType.Text)
    private String idCardNumber;

    @Field(type = FieldType.Date)
    private LocalDate issueDate;

    @Field(type = FieldType.Text)
    private String issuePlace;

    @Field(type = FieldType.Date)
    private LocalDate birthDate;

    @Field(type = FieldType.Keyword)
    private String phoneNumber;

    @Field(type = FieldType.Text)
    private String emailAddress;

    @Field(type = FieldType.Text)
    private String address;

    @Field(type = FieldType.Text)
    private String nationality;

    @Field(type = FieldType.Text)
    private String otherInfo;


    @Field(type = FieldType.Text)
    private String releaseStatus;

    @Field(type = FieldType.Text , analyzer = ConfigConstant.ANALYZE_ES , searchAnalyzer = ConfigConstant.ANALYZE_ES)
    private String content;

}

