package DataBase

import Table.TodoTable
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
            SchemaUtils.create(TodoTable)

            // Alter the UserTable to add a new column 'phone_number' if it doesn't already exist
            addColumnIfNotExists("user", "password", "VARCHAR(15)")
        }
    }

    // Function to add a column to a table if it does not exist
    private fun addColumnIfNotExists(tableName: String, columnName: String, columnType: String) {
        // SQL command to check if the column already exists
        val checkColumnQuery = """
        SELECT column_name 
        FROM information_schema.columns 
        WHERE table_name='$tableName' AND column_name='$columnName';
    """

        // Execute the check and add column if not found
        transaction {
            // Check if column exists
            val columnExists = exec(checkColumnQuery) { rs ->
                rs.next() // Move the cursor to the first row
            }

            // If the column doesn't exist, add it to the table
            if (!columnExists!!) {
                val addColumnQuery = """ALTER TABLE "$tableName" ADD COLUMN $columnName $columnType;"""
                exec(addColumnQuery) // Execute the ALTER TABLE query
            }
        }
    }



    // Configure and return a HikariDataSource, which manages the database connection pool
    private fun hikari(): HikariDataSource {
        val config = HikariConfig() // Create a new HikariConfig object to configure the connection pool

        // Set the JDBC driver class name from an environment variable

        // Log or throw if JDBC_DRIVER is not set
        val driverClassName = System.getenv("JDBC_DRIVER") ?: throw IllegalArgumentException("JDBC_DRIVER environment variable not set")
        config.driverClassName = driverClassName

        // Log or throw if JDBC_DATABASE_URL is not set
        val jdbcUrl = System.getenv("JDBC_DATABASE_URL") ?: throw IllegalArgumentException("JDBC_DATABASE_URL environment variable not set")
        config.jdbcUrl = jdbcUrl

        println("JDBC Driver: ${System.getenv("JDBC_DRIVER")}")
        println("JDBC URL: ${System.getenv("JDBC_DATABASE_URL")}")



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
