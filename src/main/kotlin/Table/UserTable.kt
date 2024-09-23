package Table

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object UserTable : Table()
{
    /**
     * user table is creating table in postpresql db
     * Table() have all insert put post delete function
     */

    val userId : Column<Int> = integer("userId").autoIncrement()
    val userAge : Column<Int> = integer("userAge")
    val userName : Column<String> = varchar("userName" , 10000 )
    val password : Column<String> = varchar("password" , 1000 )
    val userEmail : Column<String> = varchar("userEmail" , 10000 ).uniqueIndex()

    override val primaryKey: PrimaryKey = PrimaryKey(userId)

}