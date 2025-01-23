ALTER type remocra.type_module add value 'DASHBOARD';

ALTER TYPE historique.type_objet ADD VALUE 'DASHBOARD';

ALTER TYPE remocra."DROIT" ADD VALUE 'DASHBOARD_R';

CREATE TYPE remocra.type_dashboard_components AS ENUM (
    'PIECHART',
    'GAUGE',
    'HORIZONTALCHAR',
    'TABLE',
    'COUNTER',
    'MAP'
);

CREATE TABLE remocra.dashboard_query
(
    dashboard_query_id                UUID PRIMARY KEY,
    dashboard_query_title       TEXT NOT NULL,
    dashboard_query_query         TEXT NOT NULL
);

CREATE TABLE remocra.dashboard_component
(
    dashboard_component_id                UUID PRIMARY KEY,
    dashboard_component_dahsboard_query_id UUID NOT NULL REFERENCES remocra.dashboard_query(dashboard_query_id),
    dashboard_component_key remocra.type_dashboard_components NOT NULL,
    dashboard_component_title       TEXT NOT NULL,
    dashboard_component_config         JSONB NOT NULL
);

CREATE TABLE remocra.dashboard
(
    dashboard_id                UUID PRIMARY KEY,
    dashboard_title       TEXT NOT NULL
);

CREATE TABLE remocra.dashboard_config
(
    dashboard_config_dashboard_component_id                UUID NOT NULL REFERENCES remocra.dashboard_component(dashboard_component_id),
    dashboard_config_dashboard_id UUID NOT NULL REFERENCES remocra.dashboard(dashboard_id),
    dashboard_config_dashboard_component_position_config       JSONB NOT NULL
);

CREATE TABLE remocra.l_dashboard_profil
 (
     profil_utilisateur_id UUID UNIQUE NOT NULL REFERENCES remocra.profil_utilisateur(profil_utilisateur_id),
     dashboard_id UUID NOT NULL REFERENCES remocra.dashboard(dashboard_id)
 );
