package remocra.db.jooq.bindings

import org.jooq.Binding
import org.jooq.BindingGetResultSetContext
import org.jooq.BindingGetSQLInputContext
import org.jooq.BindingGetStatementContext
import org.jooq.BindingRegisterContext
import org.jooq.BindingSQLContext
import org.jooq.BindingSetSQLOutputContext
import org.jooq.BindingSetStatementContext
import org.jooq.Converter
import org.jooq.Geometry
import org.jooq.impl.DSL
import org.locationtech.jts.io.WKBReader
import org.locationtech.jts.io.WKTWriter
import java.sql.SQLException
import java.sql.SQLFeatureNotSupportedException
import java.sql.Types

class GeometryBinding : Binding<Geometry, org.locationtech.jts.geom.Geometry> {

    private val reader: ThreadLocal<WKBReader> = ThreadLocal.withInitial(::WKBReader)
    private val writer: ThreadLocal<WKTWriter> = ThreadLocal.withInitial(::WKTWriter)

    override fun converter(): Converter<Geometry, org.locationtech.jts.geom.Geometry> {
        return object : Converter<Geometry, org.locationtech.jts.geom.Geometry> {
            override fun from(geom: Geometry?): org.locationtech.jts.geom.Geometry? =
                geom?.data()
                    ?.takeUnless { it == "null" }
                    ?.let { reader.get().read(WKBReader.hexToBytes(it)) }

            override fun to(geom: org.locationtech.jts.geom.Geometry?): Geometry? =
                geom?.let { Geometry.valueOf(writer.get().write(it)) }

            override fun fromType(): Class<Geometry> {
                return Geometry::class.java
            }

            override fun toType(): Class<org.locationtech.jts.geom.Geometry> {
                return org.locationtech.jts.geom.Geometry::class.java
            }
        }
    }

    @Throws(SQLException::class)
    override fun register(ctx: BindingRegisterContext<org.locationtech.jts.geom.Geometry>) {
        ctx.statement().registerOutParameter(ctx.index(), Types.BLOB)
    }

    override fun set(ctx: BindingSetStatementContext<org.locationtech.jts.geom.Geometry>) {
        ctx.statement().setString(ctx.index(), ctx.convert<Any>(converter()).value() as String)
    }

    override fun set(ctx: BindingSetSQLOutputContext<org.locationtech.jts.geom.Geometry>?) {
        throw SQLFeatureNotSupportedException()
    }

    override fun get(ctx: BindingGetResultSetContext<org.locationtech.jts.geom.Geometry>) {
        ctx.convert(converter()).value(Geometry.valueOf(ctx.resultSet().getString(ctx.index())))
    }

    override fun get(ctx: BindingGetStatementContext<org.locationtech.jts.geom.Geometry>) {
        ctx.convert(converter()).value(Geometry.valueOf(ctx.statement().getString(ctx.index())))
    }

    override fun get(ctx: BindingGetSQLInputContext<org.locationtech.jts.geom.Geometry>?) {
        throw SQLFeatureNotSupportedException()
    }

    override fun sql(ctx: BindingSQLContext<org.locationtech.jts.geom.Geometry>) {
        val value: Geometry? = ctx.convert(converter()).value()
        if (value == null) {
            ctx.render().visit(DSL.`val`(value, Geometry::class.java)).sql("::geometry")
            return
        }

        if (ctx.value().srid == 0) {
            throw IllegalArgumentException("Le SRID doit être renseigné")
        }

        ctx.render()
            .sql("ST_GeomFromText(")
            .visit(DSL.`val`(value.data()))
            .sql(("," + ctx.value().srid) + ")")
    }
}
