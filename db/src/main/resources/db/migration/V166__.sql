CREATE TABLE remocra.risque_express (
    risque_express_id uuid PRIMARY KEY,
    risque_express_libelle text  NULL,
    risque_express_geometries JSONB NOT NULL
)


