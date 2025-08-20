package com.example.demoEs.es.document;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class) // vẫn giữ để hỗ trợ nếu dùng snake_case
@Document(indexName = "data_prevent_index")
public class DataPreventES {

    @Id
    @Field(type = FieldType.Keyword)
    @JsonProperty("uuid")
    private String uuid;


    @Field(type = FieldType.Keyword)
    @JsonProperty("preventCode")
    private String preventCode;

    @Field(type = FieldType.Text)
    @JsonProperty("documentNumber")
    private String documentNumber;

    @Field(type = FieldType.Date)
    @JsonProperty("issueDate")
    private Date issueDate;

    @Field(type = FieldType.Keyword)
    @JsonProperty("receivedDocumentNumber")
    private String receivedDocumentNumber;

    @Field(type = FieldType.Date)
    @JsonProperty("receivedDate")
    private Date receivedDate;

    @Field(type = FieldType.Text)
    @JsonProperty("requestingPersonOrUnit")
    private String requestingPersonOrUnit;

    @Field(type = FieldType.Keyword)
    @JsonProperty("requestingPersonCode")
    private String requestingPersonCode;

    @Field(type = FieldType.Text)
    @JsonProperty("summary")
    private String summary;

    @Field(type = FieldType.Keyword)
    @JsonProperty("approveStatus")
    private String approveStatus;

    @Field(type = FieldType.Text)
    @JsonProperty("note")
    private String note;

    @Field(type = FieldType.Keyword)
    @JsonProperty("category")
    private String category;

    @Field(type = FieldType.Integer)
    @JsonProperty("updateCount")
    private int updateCount;


    @Field(type = FieldType.Keyword)
    @JsonProperty("status")
    private String status;

    // Suggestion fields for autocomplete functionality
    @Field(type = FieldType.Completion)
    private String[] suggest;

    public enum Category {
        BLOCKING_INFORMATION,
        REFERENCE_INFORMATION
    }
}
