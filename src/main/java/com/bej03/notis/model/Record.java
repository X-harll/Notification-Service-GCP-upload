package com.bej03.notis.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Entity
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class Record {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String field1;
    private String field2;
    private String field3;

    private String batchId;

    // Enum field to represent status
    @Enumerated(EnumType.STRING)
    private RecordStatus status = RecordStatus.PENDING;

    // Constructor for creating records without an ID
    public Record(String field1, String field2, String field3, String batchId) {
        this.field1 = field1;
        this.field2 = field2;
        this.field3 = field3;
        this.batchId = batchId;
        this.status = RecordStatus.PENDING;
    }
}
