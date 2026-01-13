package remocra.usecase.rapportpersonnalise

import jakarta.inject.Inject
import jakarta.ws.rs.core.StreamingOutput
import org.geotools.api.data.DataStoreFactorySpi
import org.geotools.api.data.SimpleFeatureStore
import org.geotools.api.feature.simple.SimpleFeature
import org.geotools.data.DataUtilities
import org.geotools.data.shapefile.ShapefileDataStoreFactory
import org.geotools.feature.simple.SimpleFeatureBuilder
import org.geotools.feature.simple.SimpleFeatureTypeBuilder
import org.geotools.geometry.jts.JTSFactoryFinder
import org.geotools.referencing.CRS
import org.locationtech.jts.geom.MultiLineString
import org.locationtech.jts.geom.MultiPoint
import org.locationtech.jts.geom.MultiPolygon
import org.locationtech.jts.io.WKTReader
import remocra.GlobalConstants
import remocra.app.AppSettings
import remocra.data.GenererRapportPersonnaliseData
import remocra.data.enums.ErrorType
import remocra.db.RapportPersonnaliseRepository
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractUseCase
import remocra.usecase.document.DocumentUtils
import java.nio.charset.StandardCharsets
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.io.path.inputStream
import kotlin.io.path.name
import kotlin.io.path.writeText

/**
 * Permet d'exporter les données de la requêtes en csv
 */
class ExportDataCarteRapportPersonnaliseUseCase : AbstractUseCase() {

    @Inject
    private lateinit var rapportPersonnaliseRepository: RapportPersonnaliseRepository

    @Inject
    private lateinit var documentUtils: DocumentUtils

    @Inject
    private lateinit var appSettings: AppSettings

    companion object {
        private val DOSSIER_TMP_RAPPORT_PERONNALISE = GlobalConstants.DOSSIER_DATA.resolve("rapport_personnalise")
        private const val FILE_NAME = "rapport_personnalise_shapefile"
    }

    fun execute(genererRapportPersonnaliseData: GenererRapportPersonnaliseData): StreamingOutput {
        // Fichiers qui vont être créés
        val files: Array<String> = arrayOf(
            "$FILE_NAME.dbf",
            "$FILE_NAME.fix",
            "$FILE_NAME.prj",
            "$FILE_NAME.shp",
            "$FILE_NAME.shx",
        )
        documentUtils.ensureDirectory(DOSSIER_TMP_RAPPORT_PERONNALISE)

        try {
            var requete =
                rapportPersonnaliseRepository.getSqlRequete(genererRapportPersonnaliseData.rapportPersonnaliseId)

            // On remplace avec les données paramètres fournies
            genererRapportPersonnaliseData.listeParametre.forEach {
                requete = requete.replace(it.rapportPersonnaliseParametreCode, it.value.toString())
            }

            val result = rapportPersonnaliseRepository.executeSqlRapport(requete)

            // On construit l'objet qui contient une liste de clé valeur
            val data: MutableList<Map<String, Any?>> = mutableListOf()
            for (record in result) {
                val row: MutableMap<String, Any?> = mutableMapOf()
                for (field in record.fields()) {
                    row[field.name] = record.getValue(field)
                }
                data.add(row)
            }

            // Crée un type de feature dynamique
            val builder = SimpleFeatureTypeBuilder()

            // Attention doit s'appeler "the_geom" pour que cela fonctionne
            // On ne peut pas passer le type "Geometry", il faut que ça soit un type précis. On se base doit sur le premier élément
            val geometrie = data.firstOrNull { it["geometrie"] != null }?.toString()

            if (geometrie == null) {
                throw IllegalArgumentException("Aucune géométrie n'est présente.")
            }

            if (geometrie.contains("POINT")) {
                builder.add("the_geom", MultiPoint::class.java)
            } else if (geometrie.contains("LINESTRING")) {
                builder.add("the_geom", MultiLineString::class.java)
            } else if (geometrie.contains("POLYGON")) {
                builder.add("the_geom", MultiPolygon::class.java)
            } else {
                throw IllegalArgumentException("Impossible de trouver le type de géométrie")
            }

            // Ajouter les autres colonnes
            result.fields().forEach { field ->
                if (field.name != "geometrie") {
                    builder.add(field.name, field.type)
                }
            }

            builder.name = FILE_NAME
            val crs = CRS.decode(appSettings.epsg.name)
            builder.crs = crs
            val featureType = builder.buildFeatureType()

            // Crée un fichier .shp
            val newFile = DOSSIER_TMP_RAPPORT_PERONNALISE.resolve("$FILE_NAME.shp")
            val params: MutableMap<String, Any?> = HashMap()
            params["url"] = newFile.toUri().toURL()
            params["charset"] = StandardCharsets.UTF_8.name()
            val factory: DataStoreFactorySpi = ShapefileDataStoreFactory()
            val dataStore = factory.createNewDataStore(params)
            dataStore.createSchema(featureType)

            // Ajoute les features
            val geometryFactory = JTSFactoryFinder.getGeometryFactory()
            val featureBuilder = SimpleFeatureBuilder(featureType)
            try {
                val featureStore = dataStore.getFeatureSource(FILE_NAME) as SimpleFeatureStore
                val features = ArrayList<SimpleFeature>()
                for (row in data) {
                    // La géométrie doit être ajoutée en première
                    val g = WKTReader().read(row["geometrie"] as String)
                    g.srid = appSettings.srid
                    featureBuilder.add(geometryFactory.createGeometry(g))

                    // On boucle sur les autres attributs
                    featureType.attributeDescriptors.map { attr -> attr.localName }
                        .filter { attr -> attr != "the_geom" }
                        .forEach { featureBuilder.add(row.getOrDefault(it, null)) }

                    val feature = featureBuilder.buildFeature(null)
                    features.add(feature)
                }
                featureStore.addFeatures(DataUtilities.collection(features))
            } catch (e: Exception) {
                throw RemocraResponseException(ErrorType.RAPPORT_PERSO_SHP, e.message)
            }

            val wkt = crs.toWKT()

            // Écrire dans un fichier .prj
            val prjFile = DOSSIER_TMP_RAPPORT_PERONNALISE.resolve("$FILE_NAME.prj")
            prjFile.writeText(wkt)

            val output = StreamingOutput { output ->
                val out = ZipOutputStream(output)
                files.forEach { fileString ->
                    val file = DOSSIER_TMP_RAPPORT_PERONNALISE.resolve(fileString)
                    file.inputStream().use { fis ->
                        val zipEntry = ZipEntry(file.name)
                        out.putNextEntry(zipEntry)
                        fis.copyTo(out)
                        out.closeEntry()
                    }
                }
                out.flush()
                out.close()

                // Suppression des fichiers
                documentUtils.deleteDirectory(DOSSIER_TMP_RAPPORT_PERONNALISE)
            }

            return output
        } catch (e: Exception) {
            // On supprime les fichiers
            documentUtils.deleteDirectory(DOSSIER_TMP_RAPPORT_PERONNALISE)

            throw RemocraResponseException(ErrorType.RAPPORT_PERSO_SHP, e.message)
        }
    }
}
