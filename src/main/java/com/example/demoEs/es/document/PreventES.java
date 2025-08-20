package com.example.demoEs.es.document;


import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.util.List;
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Document(indexName = "prevent_index")
@Setting(settingPath = "elasticsearch/analysis-config.json")
public class PreventES {

    @Id
    private String id;

    @Field(type = FieldType.Nested)
    private DataPreventES dataPreventES;

    @Field(type = FieldType.Nested)
    private List<PersonalDataEntryES> personalsES;

    @Field(type = FieldType.Nested)
    private List<OrganizationDataEntryES> organizationsES;

}
