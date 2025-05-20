CREATE TABLE l_couche_crise (
    couche_id UUID REFERENCES couche(couche_id),
    crise_id UUID REFERENCES crise(crise_id),
    operationnel BOOLEAN DEFAULT FALSE,
    anticipation BOOLEAN DEFAULT FALSE,
    PRIMARY KEY (couche_id, crise_id)
);
