package Table

import Table.UserTable.autoIncrement
import Table.UserTable.integer
import Table.UserTable.varchar
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object TodoTable : Table()
{
    val id : Column<Int> = integer("id").autoIncrement()
    val userId : Column<Int> = integer("userId").references(UserTable.userId)
    val title : Column<String> = varchar("title" , 10000 )
    val completed : Column<Boolean> = bool("completed")

    override val primaryKey: PrimaryKey = PrimaryKey(id)
}