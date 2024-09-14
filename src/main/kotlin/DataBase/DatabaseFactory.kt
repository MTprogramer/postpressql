package DataBase

import Table.UserTable
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {

    // Initialize the database by connecting to it and creating the required table
    fun init() {
        // Connect to the database using the HikariCP connection pool
        Database.connect(hikari())

        // Start a transaction and create the UserTable schema if it doesn't exist
        transaction {
            SchemaUtils.create(UserTable)
        }
    }

    // Configure and return a HikariDataSource, which manages the database connection pool
    private fun hikari(): HikariDataSource {
        val config = HikariConfig() // Create a new HikariConfig object to configure the connection pool

        // Set the JDBC driver class name from an environment variable
        config.driverClassName = System.getenv("JDBC_DRIVER")

        // Set the JDBC URL for the database connection from an environment variable
        config.jdbcUrl = System.getenv("JDBC_DATABASE_URL")

        // Set the maximum number of connections in the pool to 3
        config.maximumPoolSize = 3

        // Disable auto-commit to manage transactions manually
        config.isAutoCommit = false

        // Set the transaction isolation level to "REPEATABLE READ"
        config.transactionIsolation = "TRANSACTION_REPEATABLE_READ"

        // Validate the configuration settings
        config.validate()

        // Return a HikariDataSource object created using the configuration
        return HikariDataSource(config)
    }

    // Suspend function to run a database query in the IO context
    // The block of code is executed inside a transaction and its result is returned automatically
    suspend fun <T> dbQuery(block: () -> T): T =
        withContext(Dispatchers.IO) {  // Switch to the IO dispatcher for blocking database operations
            transaction {  // Start a transaction
                block() // Execute the passed block of code, the result is automatically returned from the transaction
            }
        }
}
