package com.example.demoEs.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "property",
        indexes = {
                @Index(name = "id_number_idx", columnList = "id_number")
        }
)
public class Property {

    @Id
    @UuidGenerator
    @Column(name = "uuid")
    private String uuid;

    @Column(name = "id_number", nullable = false)
    private String idNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "from_type",nullable = false)
    private String from;

    @Column(name = "`group`", nullable = false)
    private String group;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "data", columnDefinition = "TEXT")
    private String data;

}
