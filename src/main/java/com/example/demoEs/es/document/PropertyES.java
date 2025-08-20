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


import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Document(indexName = "property_index")
@Setting(settingPath = "elasticsearch/analysis-config.json")
public class PropertyES {

    @Id
    private String id;

    private String idReleaseDocument;

    @Field(type = FieldType.Keyword)
    private String releaseStatus;

    @Field(type = FieldType.Text,analyzer = ConfigConstant.ANALYZE_ES, searchAnalyzer = ConfigConstant.ANALYZE_ES)
    private String ownerName;

    @Field(type = FieldType.Text)
    private String certificateNumber;

    @Field(type = FieldType.Text)
    private String documentNumber;

    @Field(type = FieldType.Keyword)
    private String type;

    @Field(type = FieldType.Keyword)
    private String group;

    @Field(type = FieldType.Text,analyzer = ConfigConstant.ANALYZE_ES, searchAnalyzer = ConfigConstant.ANALYZE_ES)
    private String infoData;

    @Field(type = FieldType.Keyword)
    private String fromType;

    @Field(type = FieldType.Keyword)
    private String isApproved;

    private String category;

}
