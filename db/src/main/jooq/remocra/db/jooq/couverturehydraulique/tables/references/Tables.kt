/*
 * This file is generated by jOOQ.
 */
package remocra.db.jooq.couverturehydraulique.tables.references

import org.jooq.Configuration
import org.jooq.Field
import org.jooq.Record
import org.jooq.Result
import org.locationtech.jts.geom.Geometry
import remocra.db.jooq.couverturehydraulique.tables.Batiment
import remocra.db.jooq.couverturehydraulique.tables.CouvertureTracee
import remocra.db.jooq.couverturehydraulique.tables.CouvertureTraceePei
import remocra.db.jooq.couverturehydraulique.tables.Etude
import remocra.db.jooq.couverturehydraulique.tables.LEtudeCommune
import remocra.db.jooq.couverturehydraulique.tables.LEtudeDocument
import remocra.db.jooq.couverturehydraulique.tables.PeiProjet
import remocra.db.jooq.couverturehydraulique.tables.PlusProchePei
import remocra.db.jooq.couverturehydraulique.tables.Reseau
import remocra.db.jooq.couverturehydraulique.tables.Sommet
import remocra.db.jooq.couverturehydraulique.tables.TempDistance
import remocra.db.jooq.couverturehydraulique.tables.TypeEtude
import remocra.db.jooq.couverturehydraulique.tables.VoieLaterale
import java.util.UUID

/**
 * The table <code>couverturehydraulique.batiment</code>.
 */
val BATIMENT: Batiment = Batiment.BATIMENT

/**
 * Couverture hydraulique résultante de la simulation. Il s'agit de la
 * couverture totale issue de toutes les couvertures hydrauliques de la table
 * couverturehydraulique.couverture_tracee_pei
 */
val COUVERTURE_TRACEE: CouvertureTracee = CouvertureTracee.COUVERTURE_TRACEE

/**
 * The table <code>couverturehydraulique.couverture_tracee_pei</code>.
 */
val COUVERTURE_TRACEE_PEI: CouvertureTraceePei = CouvertureTraceePei.COUVERTURE_TRACEE_PEI

/**
 * The table <code>couverturehydraulique.etude</code>.
 */
val ETUDE: Etude = Etude.ETUDE

/**
 * The table <code>couverturehydraulique.l_etude_commune</code>.
 */
val L_ETUDE_COMMUNE: LEtudeCommune = LEtudeCommune.L_ETUDE_COMMUNE

/**
 * The table <code>couverturehydraulique.l_etude_document</code>.
 */
val L_ETUDE_DOCUMENT: LEtudeDocument = LEtudeDocument.L_ETUDE_DOCUMENT

/**
 * The table <code>couverturehydraulique.pei_projet</code>.
 */
val PEI_PROJET: PeiProjet = PeiProjet.PEI_PROJET

/**
 * The table <code>couverturehydraulique.plus_proche_pei</code>.
 */
val PLUS_PROCHE_PEI: PlusProchePei = PlusProchePei.PLUS_PROCHE_PEI

/**
 * Call <code>couverturehydraulique.plus_proche_pei</code>.
 */
fun PLUS_PROCHE_PEI(
    configuration: Configuration,
    geomclic: Geometry?,
    distanceMaxParcours: Int?,
    idreseauimporte: UUID?,
): Result<Record> = configuration.dsl().selectFrom(
    remocra.db.jooq.couverturehydraulique.tables.PlusProchePei.PLUS_PROCHE_PEI.call(
        geomclic,
        distanceMaxParcours,
        idreseauimporte,
    ),
).fetch()

/**
 * Get <code>couverturehydraulique.plus_proche_pei</code> as a table.
 */
fun PLUS_PROCHE_PEI(
    geomclic: Geometry?,
    distanceMaxParcours: Int?,
    idreseauimporte: UUID?,
): PlusProchePei = remocra.db.jooq.couverturehydraulique.tables.PlusProchePei.PLUS_PROCHE_PEI.call(
    geomclic,
    distanceMaxParcours,
    idreseauimporte,
)

/**
 * Get <code>couverturehydraulique.plus_proche_pei</code> as a table.
 */
fun PLUS_PROCHE_PEI(
    geomclic: Field<Geometry?>,
    distanceMaxParcours: Field<Int?>,
    idreseauimporte: Field<UUID?>,
): PlusProchePei = remocra.db.jooq.couverturehydraulique.tables.PlusProchePei.PLUS_PROCHE_PEI.call(
    geomclic,
    distanceMaxParcours,
    idreseauimporte,
)

/**
 * The table <code>couverturehydraulique.reseau</code>.
 */
val RESEAU: Reseau = Reseau.RESEAU

/**
 * The table <code>couverturehydraulique.sommet</code>.
 */
val SOMMET: Sommet = Sommet.SOMMET

/**
 * Table permettant de stocker les informations nécessaire au parcours de graph;
 * basé sur l'algorithme de Dijkstra
 */
val TEMP_DISTANCE: TempDistance = TempDistance.TEMP_DISTANCE

/**
 * The table <code>couverturehydraulique.type_etude</code>.
 */
val TYPE_ETUDE: TypeEtude = TypeEtude.TYPE_ETUDE

/**
 * The table <code>couverturehydraulique.voie_laterale</code>.
 */
val VOIE_LATERALE: VoieLaterale = VoieLaterale.VOIE_LATERALE
