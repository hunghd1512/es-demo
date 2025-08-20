package com.example.demoEs.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import java.util.Date;


@Entity(name = "nds_data_prevent")
@Table
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DataPrevent {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "uuid", updatable = false, nullable = false)
    private String uuid;

    @Column(name = "prevent_code")
    private String preventCode;

    @Column(name = "document_number", nullable = false, length = 15)
    private String documentNumber;

    @Column(name = "issue_date", nullable = false)
    private Date issueDate;

    @Column(name = "received_document_number", length = 15)
    private String receivedDocumentNumber;

    @Column(name = "received_date")
    private Date receivedDate;

    @Column(name = "requesting_person_or_unit", nullable = false, length = 255)
    private String requestingPersonOrUnit;

    @Column(name = "requesting_person_code", nullable = false, length = 255)
    private String requestingPersonCode;

    @Column(name = "summary", nullable = false, columnDefinition = "TEXT")
    private String summary;

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private Category category;

    @Column(name = "approve_status", nullable = false)
    private String approveStatus;

    @Column(name = "update_count", nullable = false, columnDefinition = "INT DEFAULT 0")
    private int updateCount;

    @Column(name = "created_at", updatable = false)
    private Date createdAt;

    @Column(name = "updated_at")
    private Date updatedAt;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "status")
    private String status;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_action",nullable = true, length = 255)
    private String actionType;


    @PrePersist
    public void prePersist()  {
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    @PreUpdate
    public void preUpdate()  {
        this.updatedAt = new Date();
        this.updateCount++;
    }

    public enum Category {
        BLOCKING_INFORMATION,
        REFERENCE_INFORMATION
    }

}
