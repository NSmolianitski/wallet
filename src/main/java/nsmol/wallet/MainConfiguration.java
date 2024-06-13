package nsmol.wallet;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultDSLContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class MainConfiguration {
    @Bean
    public DataSourceConnectionProvider connectionProvider(DataSource dataSource) {
        return new DataSourceConnectionProvider(dataSource);
    } 
    
    @Bean
    public org.jooq.Configuration jooqConfiguration(DataSourceConnectionProvider connectionProvider) {
        return new DefaultConfiguration()
                .set(connectionProvider)
                .set(SQLDialect.POSTGRES);
    } 
    
    @Bean
    public DSLContext dSLContext(org.jooq.Configuration configuration) {
        return new DefaultDSLContext(configuration);
    }
}
