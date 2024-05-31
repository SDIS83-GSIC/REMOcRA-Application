/*
 * This file is generated by jOOQ.
 */
package remocra.db.jooq.tables.pojos

import remocra.db.jooq.enums.TypeTask
import java.io.Serializable
import java.util.UUID
import javax.annotation.processing.Generated

/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = [
        "https://www.jooq.org",
        "jOOQ version:3.19.3",
    ],
    comments = "This class is generated by jOOQ",
)
@Suppress("UNCHECKED_CAST")
data class Task(
    val taskId: UUID,
    val taskType: TypeTask,
    val taskActif: Boolean?,
    val taskPlanification: String?,
    val taskExecManuelle: Boolean?,
    val taskParametres: String?,
    val taskNotification: String?,
) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null) {
            return false
        }
        if (this::class != other::class) {
            return false
        }
        val o: Task = other as Task
        if (this.taskId != o.taskId) {
            return false
        }
        if (this.taskType != o.taskType) {
            return false
        }
        if (this.taskActif == null) {
            if (o.taskActif != null) {
                return false
            }
        } else if (this.taskActif != o.taskActif) {
            return false
        }
        if (this.taskPlanification == null) {
            if (o.taskPlanification != null) {
                return false
            }
        } else if (this.taskPlanification != o.taskPlanification) {
            return false
        }
        if (this.taskExecManuelle == null) {
            if (o.taskExecManuelle != null) {
                return false
            }
        } else if (this.taskExecManuelle != o.taskExecManuelle) {
            return false
        }
        if (this.taskParametres == null) {
            if (o.taskParametres != null) {
                return false
            }
        } else if (this.taskParametres != o.taskParametres) {
            return false
        }
        if (this.taskNotification == null) {
            if (o.taskNotification != null) {
                return false
            }
        } else if (this.taskNotification != o.taskNotification) {
            return false
        }
        return true
    }

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + this.taskId.hashCode()
        result = prime * result + this.taskType.hashCode()
        result = prime * result + (if (this.taskActif == null) 0 else this.taskActif.hashCode())
        result = prime * result + (if (this.taskPlanification == null) 0 else this.taskPlanification.hashCode())
        result = prime * result + (if (this.taskExecManuelle == null) 0 else this.taskExecManuelle.hashCode())
        result = prime * result + (if (this.taskParametres == null) 0 else this.taskParametres.hashCode())
        result = prime * result + (if (this.taskNotification == null) 0 else this.taskNotification.hashCode())
        return result
    }

    override fun toString(): String {
        val sb = StringBuilder("Task (")

        sb.append(taskId)
        sb.append(", ").append(taskType)
        sb.append(", ").append(taskActif)
        sb.append(", ").append(taskPlanification)
        sb.append(", ").append(taskExecManuelle)
        sb.append(", ").append(taskParametres)
        sb.append(", ").append(taskNotification)

        sb.append(")")
        return sb.toString()
    }
}
