package Repo

import DataBase.DatabaseFactory
import DataModel.UserData
import Table.UserTable
import model.UserDao
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.update

class UserRepo : UserDao {

    /**
     * here is implemention of all functions
     * these all functions structured in UserDao
     */

    override suspend fun inset(name: String, email: String, age: Int): UserData? {
        var statement : InsertStatement<Number>? = null
        DatabaseFactory.dbQuery {
            statement = UserTable.insert {
                it[userName] = name
                it[userEmail] = email
                it[userAge] = age
            }
        }
        return rowTostudent(statement?.resultedValues?.get(0))
    }

    override suspend fun getAll(): List<UserData> =
        DatabaseFactory.dbQuery {
            UserTable.selectAll().mapNotNull {
                rowTostudent(it)
            }
        }


    override suspend fun getById(id: Int): UserData? =
        DatabaseFactory.dbQuery {
            UserTable.select{ UserTable.userId.eq(id)}
                .map {
                    rowTostudent(it)
                }.singleOrNull()
        }


    override suspend fun deleteById(id: Int): Boolean =
        DatabaseFactory.dbQuery {
            val result = UserTable.deleteWhere { UserTable.userId.eq(id) }
            result > 0
        }


    override suspend fun updateById(id: Int, name: String, email: String, age: Int): Boolean =
        DatabaseFactory.dbQuery {
           val result = UserTable.update({UserTable.userId.eq(id)}){ student ->
                student[userName] = name
                student[userEmail] = email
                student[userAge] = age
            }
            result > 0
        }

    override suspend fun deleteAll(): Boolean =
        DatabaseFactory.dbQuery {
            UserTable.deleteAll() > 0
        }

    override suspend fun count(): Int =
        DatabaseFactory.dbQuery {
            UserTable.selectAll().count().toInt()
        }



    private fun rowTostudent(row : ResultRow?) : UserData?
    {
        if (row == null)
            return null
        else
            return UserData(
            name = row[UserTable.userName],
            email = row[UserTable.userEmail],
            age = row[UserTable.userAge],
            password = row[UserTable.password],
            userId = row[UserTable.userId])
    }

}