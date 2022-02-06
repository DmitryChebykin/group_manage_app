package com.example.demo.fakedata;

import lombok.AllArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManager;

@Component
@AllArgsConstructor
public class DataCleaner {

    private final ApplicationArguments applicationArguments;

    private final EntityManager entityManager;

    private final TransactionTemplate transactionTemplate;

    private final FakeLoader fakeLoader;

    @EventListener
    public void run(ApplicationReadyEvent event) {
        if (applicationArguments.containsOption("init")) {
            truncateTables();

            fakeLoader.loadFakeData();
        }
    }

    public void truncateTables() {
        transactionTemplate.execute(transactionStatus -> {
            entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0").executeUpdate();

            entityManager.createNativeQuery("TRUNCATE TABLE " + "_student_group_link").executeUpdate();

            entityManager.createNativeQuery("TRUNCATE TABLE " + "_groups").executeUpdate();

            entityManager.createNativeQuery("TRUNCATE TABLE " + "_students").executeUpdate();

            entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate();

            transactionStatus.flush();
            return null;
        });
    }
}
