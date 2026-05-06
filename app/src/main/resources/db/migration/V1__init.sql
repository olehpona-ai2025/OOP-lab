CREATE TABLE farm_area_entity
(
    id          UUID    NOT NULL,
    name        VARCHAR(255),
    area        INTEGER NOT NULL,
    plant_name  VARCHAR(255),
    plant_state VARCHAR(255),
    plant_age   INTEGER NOT NULL,
    version     BIGINT,
    CONSTRAINT pk_farmareaentity PRIMARY KEY (id)
);

CREATE TABLE warehouse_entity
(
    id      VARCHAR(255) NOT NULL,
    count   INTEGER      NOT NULL,
    version BIGINT,
    CONSTRAINT pk_warehouseentity PRIMARY KEY (id)
);

CREATE TABLE worker_entity
(
    id            UUID    NOT NULL,
    profile_name  VARCHAR(255),
    work_progress INTEGER NOT NULL,
    state         INTEGER NOT NULL,
    farm_area_id  UUID,
    CONSTRAINT pk_workerentity PRIMARY KEY (id)
);

ALTER TABLE worker_entity
    ADD CONSTRAINT FK_WORKERENTITY_ON_FARMAREAID FOREIGN KEY (farm_area_id) REFERENCES farm_area_entity (id) ON DELETE SET NULL;