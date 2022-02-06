package com.example.demo.repository;

import java.sql.Timestamp;

public interface StudentInfo {
    Timestamp getCreatedAt();

    String getfullName();
}
