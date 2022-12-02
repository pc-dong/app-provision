package cn.dpc.provision.persistence.repository.config;

import com.couchbase.client.java.env.ClusterEnvironment;
import com.couchbase.client.java.transactions.config.TransactionOptions;
import com.couchbase.client.java.transactions.config.TransactionsConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.couchbase.CouchbaseProperties;
import org.springframework.boot.autoconfigure.data.couchbase.CouchbaseDataProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.couchbase.config.AbstractCouchbaseConfiguration;
import org.springframework.data.couchbase.core.convert.CouchbaseCustomConversions;
import org.springframework.data.couchbase.core.convert.MappingCouchbaseConverter;
import org.springframework.data.couchbase.core.mapping.CouchbaseMappingContext;
import org.springframework.data.couchbase.repository.config.EnableReactiveCouchbaseRepositories;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.reactive.TransactionalOperator;

import java.time.Duration;

@Configuration
@Slf4j
@EnableReactiveCouchbaseRepositories
@RequiredArgsConstructor
public class CouchbaseConvertConfiguration extends AbstractCouchbaseConfiguration {

    private final CouchbaseProperties couchbaseProperties;
    private final CouchbaseDataProperties couchbaseDataProperties;
    @Bean
    public MappingCouchbaseConverter mappingCouchbaseConverter(CouchbaseMappingContext couchbaseMappingContext,
                                                               CouchbaseCustomConversions couchbaseCustomConversions) {
        MappingCouchbaseConverter converter = new CustomMappingCouchbaseConverter(couchbaseMappingContext);
        converter.setCustomConversions(couchbaseCustomConversions);
        return converter;
    }

    @Override
    protected boolean autoIndexCreation() {
        return true;
    }

    @Override
    public String getConnectionString() {
        return this.couchbaseProperties.getConnectionString();
    }

    @Override
    public String getUserName() {
        return this.couchbaseProperties.getUsername();
    }

    @Override
    public String getPassword() {
        return this.couchbaseProperties.getPassword();
    }

    @Override
    public String getBucketName() {
        return this.couchbaseDataProperties.getBucketName();
    }

}
