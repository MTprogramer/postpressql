package model

import DataModel.TodoDataModel

interface TodoDao
{
    suspend fun getAllTodo():List<TodoDataModel>
    suspend fun getById(id: Int) : TodoDataModel?
    suspend fun deleteById(id : Int) : Boolean
    suspend fun deleteAllByUserId(userid : Int) : Boolean
    suspend fun updateById(id : Int, title : String, completed : Boolean) : Boolean
    suspend fun insert(title : String, completed : Boolean , userId : Int) : TodoDataModel?
    suspend fun deleteAll() : Boolean
    suspend fun count() : Int
}