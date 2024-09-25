package Repo

import DataBase.DatabaseFactory
import DataModel.TodoDataModel
import DataModel.UserData
import Table.TodoTable
import Table.UserTable
import model.TodoDao
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.update

class TodoRepo : TodoDao {

    override suspend fun getAllTodo(): List<TodoDataModel> =
        DatabaseFactory.dbQuery {
            TodoTable.selectAll().mapNotNull {
                rowTostudent(it)
            }
        }

    override suspend fun getAllTodoById(id: Int): List<TodoDataModel> =
        DatabaseFactory.dbQuery {
            TodoTable.select ( TodoTable.userId.eq(id)).mapNotNull {
                rowTostudent(it)
            }
        }



    override suspend fun getById(id: Int): TodoDataModel? =
        DatabaseFactory.dbQuery {
            TodoTable.select{ TodoTable.id.eq(id)}.map {
                rowTostudent(it)
            }.singleOrNull()
        }

    override suspend fun deleteById(id: Int): Boolean =
        DatabaseFactory.dbQuery {
            val result = TodoTable.deleteWhere {TodoTable.id.eq(id)}
            result > 0
        }

    override suspend fun deleteAllByUserId(userid: Int) : Boolean =
        DatabaseFactory.dbQuery {
            val result = TodoTable.deleteWhere { TodoTable.userId.eq(userid)}
            result > 0
        }


    override suspend fun updateById(id: Int, title: String, completed: Boolean): Boolean =
        DatabaseFactory.dbQuery {
            val result = TodoTable.update({TodoTable.id.eq(id)}){ student ->
                student[TodoTable.title] = title
                student[TodoTable.completed] = completed
            }
            result > 0
        }

    override suspend fun insert(titlee: String, completedd: Boolean , userid: Int): TodoDataModel? {
        var statement : InsertStatement<Number>? = null
        DatabaseFactory.dbQuery {
           statement = TodoTable.insert {
               it[title] = titlee
               it[completed] = completedd
               it[userId] = userid
           }
        }
        return rowTostudent(statement?.resultedValues?.get(0))
    }


    override suspend fun deleteAll(): Boolean =
        DatabaseFactory.dbQuery {
            TodoTable.deleteAll() > 0
        }

    override suspend fun count(): Int =
        DatabaseFactory.dbQuery {
            TodoTable.selectAll().count().toInt()
        }


    private fun rowTostudent(row : ResultRow?) : TodoDataModel?
    {
        return if (row == null)
            null
        else
            TodoDataModel(
                title = row[TodoTable.title],
                id = row[TodoTable.id],
                userId = row[TodoTable.userId],
                completed = row[TodoTable.completed]
            )
    }
}