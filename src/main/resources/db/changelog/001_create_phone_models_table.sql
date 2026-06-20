--liquibase formatted sql

--changeset admin:2024-01-01-01
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'ecommerce' AND table_name = 'phone_models'
CREATE TABLE ecommerce.phone_models (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    year INTEGER,
    price NUMERIC(10, 2),
    cpu_model VARCHAR(255),
    hard_disk_size VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
--rollback DROP TABLE ecommerce.phone_models;

--changeset admin:2024-01-01-02
CREATE INDEX idx_phone_models_name ON ecommerce.phone_models(name);
--rollback DROP INDEX ecommerce.idx_phone_models_name;

