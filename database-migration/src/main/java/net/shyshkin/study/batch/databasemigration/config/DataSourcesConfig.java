package net.shyshkin.study.batch.databasemigration.config;

import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
public class DataSourcesConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    DataSource dataSource() {
        return DataSourceBuilder
                .create()
                .build();
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.university-datasource")
    DataSource universityDataSource() {
        return DataSourceBuilder
                .create()
                .build();
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.destination-datasource")
    DataSource destinationDataSource() {
        return DataSourceBuilder
                .create()
                .build();
    }

    @Bean
    EntityManagerFactory posgresqlEntityManagerFactory() {
        var lem = new LocalContainerEntityManagerFactoryBean();
        lem.setDataSource(universityDataSource());
        lem.setPackagesToScan("net.shyshkin.study.batch.databasemigration.posgresql.entity");
        lem.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        lem.setPersistenceProviderClass(HibernatePersistenceProvider.class);
        lem.afterPropertiesSet();
        return lem.getObject();
    }

    @Bean
    EntityManagerFactory mysqlEntityManagerFactory() {
        var lem = new LocalContainerEntityManagerFactoryBean();
        lem.setDataSource(destinationDataSource());
        lem.setPackagesToScan("net.shyshkin.study.batch.databasemigration.mysql.entity");
        lem.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        lem.setPersistenceProviderClass(HibernatePersistenceProvider.class);
        lem.afterPropertiesSet();
        return lem.getObject();
    }

}
