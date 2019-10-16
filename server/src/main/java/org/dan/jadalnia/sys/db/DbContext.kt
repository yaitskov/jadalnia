package org.dan.jadalnia.sys.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jooq.DSLContext
import org.jooq.SQLDialect.valueOf
import org.jooq.impl.DSL.using
import org.jooq.impl.DataSourceConnectionProvider
import org.jooq.impl.DefaultConfiguration
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy
import org.springframework.transaction.annotation.EnableTransactionManagement
import javax.inject.Named
import javax.sql.DataSource

@EnableTransactionManagement
@EnableAspectJAutoProxy(proxyTargetClass = true)
class DbContext {
    companion object {
        const val DSL_CONTEXT = "dslContext"
        const val TRANSACTION_MANAGER = "transactionManager"
        const val DATA_SOURCE = "dataSource"
    }

    @Bean(name = [DATA_SOURCE])
    fun dataSource(
            @Value("\${db.data.source}") driver: String,
            @Value("\${db.username}") user: String,
            @Value("\${db.password}") password: String,
            @Value("\${db.jdbc.url}") url: String): DataSource {
        val config = HikariConfig()
        config.dataSourceClassName = driver
        //config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.addDataSourceProperty("user", user)
        config.addDataSourceProperty("password", password)
        config.addDataSourceProperty("url", url)
        //config.addDataSourceProperty("characterEncoding", "utf8");
        //config.addDataSourceProperty("useUnicode", "true");
        config.connectionTestQuery = "select 1"
        config.maximumPoolSize = 1
        config.minimumIdle = 1
        config.isAutoCommit = false
        config.transactionIsolation = "TRANSACTION_READ_COMMITTED"
        config.poolName = "db-pool"
        return HikariDataSource(config)
    }

    @Bean(name = [TRANSACTION_MANAGER])
    fun txManager(
            @Value("\${db.tx.timeout.seconds}") timeoutSeconds: Int,
            @Named(DATA_SOURCE) dataSource: DataSource)
            :
            DataSourceTransactionManager {
        val result = DataSourceTransactionManager(dataSource)
        result.defaultTimeout = timeoutSeconds
        return result
    }

    @Bean(name = [DSL_CONTEXT])
    fun dslContext(
            @Value("\${db.dialect}") sqlDialect: String,
            @Named(DATA_SOURCE) dataSource: DataSource): DSLContext {
        return using(DefaultConfiguration()
                .set(DataSourceConnectionProvider(
                        TransactionAwareDataSourceProxy(dataSource)))
                .set(valueOf(sqlDialect)))
    }

    @Bean
    fun daoUpdater() = DaoUpdater()
}
