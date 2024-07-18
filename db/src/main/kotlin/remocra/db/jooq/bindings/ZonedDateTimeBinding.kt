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
import org.jooq.impl.DSL
import org.jooq.impl.DSL.keyword
import java.sql.SQLFeatureNotSupportedException
import java.sql.Types
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

class ZonedDateTimeBinding : Binding<OffsetDateTime, ZonedDateTime> {
    override fun converter(): Converter<OffsetDateTime, ZonedDateTime> {
        return object : Converter<OffsetDateTime, ZonedDateTime> {
            override fun from(p0: OffsetDateTime?): ZonedDateTime? {
                return p0?.toZonedDateTime()
            }

            override fun to(p0: ZonedDateTime?): OffsetDateTime? {
                return p0?.withZoneSameInstant(ZoneId.systemDefault())?.toOffsetDateTime()
            }

            override fun fromType(): Class<OffsetDateTime> {
                return OffsetDateTime::class.java
            }

            override fun toType(): Class<ZonedDateTime> {
                return ZonedDateTime::class.java
            }
        }
    }

    override fun register(p0: BindingRegisterContext<ZonedDateTime>?) {
        p0?.statement()!!.registerOutParameter(p0.index(), Types.TIMESTAMP_WITH_TIMEZONE)
    }

    override fun set(p0: BindingSetStatementContext<ZonedDateTime>?) {
        p0?.statement()!!.setString(p0.index(), p0.convert<Any>(converter()).value().toString())
    }

    override fun set(p0: BindingSetSQLOutputContext<ZonedDateTime>?) {
        throw SQLFeatureNotSupportedException()
    }

    override fun get(p0: BindingGetResultSetContext<ZonedDateTime>) {
        val valueToParse = p0.resultSet().getString(p0.index())
        if (valueToParse == null) { // TODO: Pour le moment, la lecture de valeur Null ne fonctionne pas
            p0.convert(converter()).value(null)
        } else {
            p0.convert(converter()).value(OffsetDateTime.parse(valueToParse.replace(" ", "T")))
        }
    }

    override fun get(p0: BindingGetStatementContext<ZonedDateTime>?) {
        val valueToParse = p0?.statement()?.getString(p0.index())
        if (valueToParse == null) {
            p0?.convert(converter())?.value(null)
        } else {
            p0.convert(converter()).value(OffsetDateTime.parse(valueToParse.replace(" ", "T")))
        }
    }

    override fun get(p0: BindingGetSQLInputContext<ZonedDateTime>?) {
        throw SQLFeatureNotSupportedException()
    }

    override fun sql(p0: BindingSQLContext<ZonedDateTime>) {
        var value: OffsetDateTime? = p0.convert(converter()).value()
        if (value == null) {
            p0.render().visit(DSL.`val`(value, OffsetDateTime::class.java)).sql("::timestamptz")
            return
        }
        p0.render().visit(keyword("cast")).sql('(' + p0.variable() + ' ').visit(keyword("as timestamptz)"))
    }
}
