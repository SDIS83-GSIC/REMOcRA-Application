package remocra.db.jooq.bindings

import com.google.common.collect.BoundType
import com.google.common.collect.Range
import org.jooq.Binding
import org.jooq.BindingGetResultSetContext
import org.jooq.BindingGetSQLInputContext
import org.jooq.BindingGetStatementContext
import org.jooq.BindingRegisterContext
import org.jooq.BindingSQLContext
import org.jooq.BindingSetSQLOutputContext
import org.jooq.BindingSetStatementContext
import org.jooq.Converter
import org.jooq.conf.ParamType
import org.jooq.impl.DSL
import java.sql.SQLException
import java.sql.SQLFeatureNotSupportedException
import java.sql.Types
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.util.Locale
import java.util.Objects

// We're binding <T> = Object (unknown JDBC type), and <U> = Range (user type)
class PostgresTSTZRangeBinding : Binding<Any?, Range<OffsetDateTime>> {
    // The converter does all the work
    @Suppress("UNCHECKED_CAST")
    override fun converter(): Converter<Any?, Range<OffsetDateTime>?> {
        return object : Converter<Any?, Range<OffsetDateTime>?> {
            override fun from(t: Any?): Range<OffsetDateTime>? {
                if (t == null) {
                    return null
                }
                val date = "" + t

                val lowerBound = date.substring(1, date.indexOf(","))
                val upperBound = date.substring(date.indexOf(",") + 1, date.length - 1)
                val lowerBoundType = if (date.startsWith("(")) {
                    BoundType.OPEN
                } else if (date.startsWith("[")) {
                    BoundType.CLOSED
                } else {
                    throw IllegalArgumentException(
                        "Format de range non valide. Doit commencer par '(' ou '['",
                    )
                }
                val upperBoundType = if (date.endsWith(")")) {
                    BoundType.OPEN
                } else if (date.endsWith("]")) {
                    BoundType.CLOSED
                } else {
                    throw IllegalArgumentException(
                        "Format de range non valide. Doit finir par ')' ou ']'",
                    )
                }

                if ("-infinity" != lowerBound &&
                    "infinity" != upperBound &&
                    "" != lowerBound &&
                    "" != upperBound
                ) {
                    return Range.range(
                        parseOffsetDateTime(lowerBound),
                        lowerBoundType,
                        parseOffsetDateTime(upperBound),
                        upperBoundType,
                    )
                }
                if ("-infinity" != lowerBound && "" != lowerBound) {
                    return Range.downTo(parseOffsetDateTime(lowerBound), lowerBoundType)
                }
                if ("infinity" != upperBound && "" != upperBound) {
                    return Range.upTo(parseOffsetDateTime(upperBound), upperBoundType)
                }
                return Range.all()
            }

            override fun to(u: Range<OffsetDateTime>?): Any? {
                if (u == null) {
                    return null
                }
                var range = if (!u.hasLowerBound() || u.lowerBoundType() == BoundType.OPEN) "(" else "["
                range += if (!u.hasLowerBound()) "" else formatOffsetDateTime(u.lowerEndpoint())
                range += ","
                range += if (!u.hasUpperBound()) "" else formatOffsetDateTime(u.upperEndpoint())
                range += if (!u.hasUpperBound() || u.upperBoundType() == BoundType.OPEN) ")" else "]"
                return range
            }

            override fun fromType(): Class<Any?> {
                return Any::class.java as Class<Any?>
            }

            override fun toType(): Class<Range<OffsetDateTime>?> {
                return Range::class.java as Class<Range<OffsetDateTime>?>
            }
        }
    }

    // Rending a bind variable for the binding context's value and casting it to the json type
    override fun sql(ctx: BindingSQLContext<Range<OffsetDateTime>>) {
        // Depending on how you generate your SQL, you may need to explicitly distinguish
        // between jOOQ generating bind variables or inlined literals.
        if (ctx.render().paramType() == ParamType.INLINED) {
            ctx.render().visit(DSL.inline(ctx.convert(converter()).value())).sql("::tstzrange")
        } else {
            ctx.render().sql("?::tstzrange")
        }
    }

    // Registering VARCHAR types for JDBC CallableStatement OUT parameters
    @Throws(SQLException::class)
    override fun register(ctx: BindingRegisterContext<Range<OffsetDateTime>>) {
        ctx.statement().registerOutParameter(ctx.index(), Types.VARCHAR)
    }

    // Converting the Range to a String value and setting that on a JDBC PreparedStatement
    @Throws(SQLException::class)
    override fun set(ctx: BindingSetStatementContext<Range<OffsetDateTime>>) {
        ctx.statement()
            .setString(ctx.index(), Objects.toString(ctx.convert(converter()).value(), null))
    }

    // Getting a String value from a JDBC ResultSet and converting that to a Range
    @Throws(SQLException::class)
    override fun get(ctx: BindingGetResultSetContext<Range<OffsetDateTime>>) {
        ctx.convert(converter()).value(ctx.resultSet().getObject(ctx.index()))
    }

    // Getting a String value from a JDBC CallableStatement and converting that to a Range
    @Throws(SQLException::class)
    override fun get(ctx: BindingGetStatementContext<Range<OffsetDateTime>>) {
        ctx.convert(converter()).value(ctx.statement().getString(ctx.index()))
    }

    // Setting a value on a JDBC SQLOutput (useful for Oracle OBJECT types)
    @Throws(SQLException::class)
    override fun set(ctx: BindingSetSQLOutputContext<Range<OffsetDateTime>>) {
        throw SQLFeatureNotSupportedException()
    }

    // Getting a value from a JDBC SQLInput (useful for Oracle OBJECT types)
    @Throws(SQLException::class)
    override fun get(ctx: BindingGetSQLInputContext<Range<OffsetDateTime>>) {
        throw SQLFeatureNotSupportedException()
    }

    companion object {
        private val DATE_TIME_FORMATTER: DateTimeFormatter = DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(DateTimeFormatter.ISO_LOCAL_DATE)
            .appendLiteral(" ")
            .append(DateTimeFormatter.ISO_LOCAL_TIME)
            .appendOffset("+HHmm", "")
            .toFormatter(Locale.ROOT)

        private fun parseOffsetDateTime(value: String): OffsetDateTime {
            return OffsetDateTime.parse(value.replace("\"".toRegex(), ""), DATE_TIME_FORMATTER)
        }

        private fun formatOffsetDateTime(value: OffsetDateTime): String {
            return DATE_TIME_FORMATTER.format(value)
        }
    }
}
