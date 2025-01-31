CREATE TYPE remocra.CARROYAGE_DFCI_TYPE AS ENUM ('CARROYAGE_100KM', 'CARROYAGE_20KM', 'CARROYAGE_2KM', 'CARROYAGE_1KM');

CREATE TABLE remocra.carroyage_dfci
(
    carroyage_dfci_id                  UUID PRIMARY KEY,
    carroyage_dfci_geometrie           geometry                    NOT NULL,
    carroyage_dfci_coordonneee         TEXT                        NOT NULL,
    carroyage_dfci_carroyage_dfci_type remocra.CARROYAGE_DFCI_TYPE NOT NULL
)
