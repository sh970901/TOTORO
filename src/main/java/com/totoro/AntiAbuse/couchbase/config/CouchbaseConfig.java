package com.totoro.AntiAbuse.couchbase.config;

import com.totoro.AntiAbuse.couchbase.domain.AbuseLimitDocument;
import com.totoro.AntiAbuse.couchbase.domain.AbuseLogDocument;
import com.totoro.AntiAbuse.couchbase.domain.AbuseRuleDocument;
import lombok.RequiredArgsConstructor;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.couchbase.CouchbaseClientFactory;
import org.springframework.data.couchbase.SimpleCouchbaseClientFactory;
import org.springframework.data.couchbase.config.AbstractCouchbaseConfiguration;
import org.springframework.data.couchbase.core.CouchbaseTemplate;
import org.springframework.data.couchbase.core.convert.MappingCouchbaseConverter;
import org.springframework.data.couchbase.repository.config.RepositoryOperationsMapping;

@Configuration
@RequiredArgsConstructor
//@EnableCouchbaseRepositories(basePackages={"com.totoro.AntiAbuse"})
public class CouchbaseConfig extends AbstractCouchbaseConfiguration {
    private final CouchClusterProperties couchClusterProperties;
    private final StringEncryptor jasyptStringEncryptor;

    @Override
    public String getConnectionString() {
        return couchClusterProperties.getConnectionString();
    }

    @Override
    public String getUserName() {
        return couchClusterProperties.getUserName();
    }

    @Override
    public String getPassword() {
        return couchClusterProperties.getPassword();
    }

    @Override
    public String getBucketName() {
        return couchClusterProperties.getBucketAbuseLog().getName();
    }

    @Override
    protected void configureRepositoryOperationsMapping(RepositoryOperationsMapping baseMapping) {
        try {
            baseMapping
                    .mapEntity(AbuseLogDocument.class, logTemplate())
                    .mapEntity(AbuseRuleDocument.class, ruleTemplate())
                    .mapEntity(AbuseLimitDocument.class, limitTemplate());
        } catch (Exception e) {
            throw e;
        }
    }

    @Primary
    @Bean("logClientFactory")
    public CouchbaseClientFactory logClientFactory(){
        return new SimpleCouchbaseClientFactory(getConnectionString(),authenticator(), couchClusterProperties.getBucketAbuseLog().getName());
    }

    @Bean("logTemplate")
    public CouchbaseTemplate logTemplate(){
        return new CouchbaseTemplate(logClientFactory(), new MappingCouchbaseConverter());
    }

    @Bean("ruleClientFactory")
    public CouchbaseClientFactory ruleClientFactory(){
        return new SimpleCouchbaseClientFactory(getConnectionString(),authenticator(), couchClusterProperties.getBucketAbuseRule().getName());
    }

    @Bean("ruleTemplate")
    public CouchbaseTemplate ruleTemplate(){
        return new CouchbaseTemplate(ruleClientFactory(), new MappingCouchbaseConverter());
    }

    @Bean("limitClientFactory")
    public CouchbaseClientFactory limitClientFactory(){
        return new SimpleCouchbaseClientFactory(getConnectionString(),authenticator(), couchClusterProperties.getBucketAbuseLimit().getName());
    }

    @Bean("limitTemplate")
    public CouchbaseTemplate limitTemplate(){
        return new CouchbaseTemplate(limitClientFactory(), new MappingCouchbaseConverter());
    }


}
