package model

import DataModel.UserData

interface UserDao
{
    suspend fun updateById(id : Int, name : String, email : String, age : Int):Boolean
    suspend fun inset(name : String, email : String, age : Int):UserData?
    suspend fun getAll():List<UserData>
    suspend fun getById(id : Int):UserData?
    suspend fun deleteById(id : Int):Boolean
    suspend fun deleteAll():Boolean
    suspend fun count():Int
}