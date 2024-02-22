package com.guner.consumer.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChargingRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String sourceGsm;
    private String targetGsm;
    private Date transactionDate;
}

