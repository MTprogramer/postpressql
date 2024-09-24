package model

import DataModel.UserData

interface UserDao
{
    suspend fun updateById(id : Int, name : String, email : String, age : Int):Boolean
    suspend fun inset(name : String, email : String, password : String, age : Int):UserData?
    suspend fun updateUser(name : String, email : String, password : String, age : Int , id: Int):Boolean?
    suspend fun getAll():List<UserData>
    suspend fun getById(id : Int):UserData?
    suspend fun getByEmail(email: String):UserData?
    suspend fun deleteById(id : Int):Boolean
    suspend fun deleteAll():Boolean
    suspend fun count():Int
}