package com.example.template.services.common.configuration;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.redisson.Redisson;
import org.redisson.api.NameMapper;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.support.NullValue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.StringUtils;

import javax.inject.Inject;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * @title: redis缓存相关配置类
 * @author: trifolium
 * @date: 2019/4/18 13:35
 * @modified :
 */
@Configuration
@EnableCaching
public class RedisConfiguration {

    @Inject
    private RedisProperties redisProperties;

    @Inject
    private AppConfig appConfig;

    @Value("${spring.jackson.time-zone:'UTC+8'}")
    private String jsonTimeZone;

    @Value("${spring.jackson.date-format:'yyyy-MM-dd HH:mm:ss'}")
    private String dateFormat;


    @Bean(name = "redisTemplate")
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer(createGenericObjectMapper()));
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer(createGenericObjectMapper()));

        redisTemplate.setConnectionFactory(redisConnectionFactory);

        return redisTemplate;
    }

    /**
     * 配置redisson
     */
    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient() {


        String PROTOCOL = "redis://";

        Config config = new Config();

        config.setCodec(new JsonJacksonCodec(createGenericObjectMapper()));
        RedisProperties.Sentinel sentinel = redisProperties.getSentinel();
        RedisProperties.Cluster redisPropertiesCluster = redisProperties.getCluster();

        Long timeout = redisProperties.getTimeout() == null ? null : redisProperties.getTimeout().toMillis();
        Long connectTimeout = redisProperties.getConnectTimeout() == null ? null
                : redisProperties.getConnectTimeout().toMillis();

        int threadCount = Math.max(Runtime.getRuntime().availableProcessors(), 4);
        config.setThreads(threadCount);
        config.setNettyThreads(threadCount + 1);
        TransportMode transportMode = TransportMode.NIO;
//        if (SystemUtil.getOsInfo().isLinux()) {
//            transportMode = TransportMode.EPOLL;
//        }
//        if (SystemUtil.getOsInfo().isMacOsX()) {
//            transportMode = TransportMode.KQUEUE;
//        }
        config.setTransportMode(transportMode);
        if (redisPropertiesCluster != null) {
            //集群redis
            ClusterServersConfig clusterServersConfig = config.useClusterServers();
            if (timeout != null) {
                clusterServersConfig.setTimeout(Math.toIntExact(timeout));
            }
            if (connectTimeout != null) {
                clusterServersConfig.setConnectTimeout(Math.toIntExact(connectTimeout));
            }
            for (String cluster : redisPropertiesCluster.getNodes()) {
                clusterServersConfig.addNodeAddress(PROTOCOL + cluster);
            }
            if (StringUtils.hasText(redisProperties.getPassword())) {
                clusterServersConfig.setPassword(redisProperties.getPassword());
            }
            clusterServersConfig.setTimeout((int) redisProperties.getTimeout().toMillis());
            clusterServersConfig.setPingConnectionInterval(30000);
            clusterServersConfig.setNameMapper(new NameMapper() {
                @Override
                public String map(String name) {
                    return appConfig.getCacheKeyPrefix() + name;
                }

                @Override
                public String unmap(String name) {
                    return name.substring(appConfig.getCacheKeyPrefix().length());
                }
            });
        } else if (StringUtils.hasText(redisProperties.getHost())) {
            //单点redis
            SingleServerConfig singleServerConfig = config.useSingleServer().
                    setAddress(PROTOCOL + redisProperties.getHost() + ":" + redisProperties.getPort());
            if (timeout != null) {
                singleServerConfig.setTimeout(Math.toIntExact(timeout));
            }
            if (connectTimeout != null) {
                singleServerConfig.setConnectTimeout(Math.toIntExact(connectTimeout));
            }
            if (StringUtils.hasText(redisProperties.getPassword())) {
                singleServerConfig.setPassword(redisProperties.getPassword());
            }
            singleServerConfig.setTimeout((int) redisProperties.getTimeout().toMillis());
            singleServerConfig.setPingConnectionInterval(30000);
            singleServerConfig.setDatabase(redisProperties.getDatabase());
            singleServerConfig.setNameMapper(new NameMapper() {
                @Override
                public String map(String name) {
                    return appConfig.getCacheKeyPrefix() + name;
                }

                @Override
                public String unmap(String name) {
                    return name.substring(appConfig.getCacheKeyPrefix().length());
                }
            });
        } else if (sentinel != null) {
            //哨兵模式
            SentinelServersConfig sentinelServersConfig = config.useSentinelServers();
            sentinelServersConfig.setMasterName(sentinel.getMaster());
            for (String node : sentinel.getNodes()) {
                sentinelServersConfig.addSentinelAddress(PROTOCOL + node);
            }
            if (StringUtils.hasText(redisProperties.getPassword())) {
                sentinelServersConfig.setPassword(redisProperties.getPassword());
            }
            if (timeout != null) {
                sentinelServersConfig.setTimeout(Math.toIntExact(timeout));
            }
            if (connectTimeout != null) {
                sentinelServersConfig.setConnectTimeout(Math.toIntExact(connectTimeout));
            }
            sentinelServersConfig.setPingConnectionInterval(30000);
            sentinelServersConfig.setDatabase(redisProperties.getDatabase());
            sentinelServersConfig.setNameMapper(new NameMapper() {
                @Override
                public String map(String name) {
                    return appConfig.getCacheKeyPrefix() + name;
                }

                @Override
                public String unmap(String name) {
                    return name.substring(appConfig.getCacheKeyPrefix().length());
                }
            });
        }
        config.setLockWatchdogTimeout(6_000);
        return Redisson.create(config);
    }

    private ObjectMapper createGenericObjectMapper() {
        final ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.setTimeZone(TimeZone.getTimeZone(jsonTimeZone));

        objectMapper.setDateFormat(new SimpleDateFormat(dateFormat));
        objectMapper.setDefaultLeniency(Boolean.FALSE);

        objectMapper.registerModule(new SimpleModule().addSerializer(new StdSerializer<>(NullValue.class) {
            private String classIdentifier;

            @Override
            public void serialize(NullValue value, JsonGenerator jGen, SerializerProvider provider)
                    throws IOException {
                classIdentifier = StringUtils.hasText(classIdentifier) ? classIdentifier : "@class";
                jGen.writeStartObject();
                jGen.writeStringField(classIdentifier, NullValue.class.getName());
                jGen.writeEndObject();
            }
        }));


        objectMapper.activateDefaultTyping(objectMapper.getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.NON_FINAL);

        return objectMapper;
    }

    @Bean
    public RedisCacheConfiguration redisCacheConfiguration() {
        GenericJackson2JsonRedisSerializer jackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer(
                createGenericObjectMapper());
        RedisCacheConfiguration configuration = RedisCacheConfiguration.defaultCacheConfig();
        configuration = configuration.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                jackson2JsonRedisSerializer));
        return configuration;
    }

    @Bean
    public ValueOperations<String, Object> valueOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForValue();
    }

    @Bean
    public HashOperations<String, String, Object> hashOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForHash();
    }

    @Bean
    public ListOperations<String, Object> listOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForList();
    }

    @Bean
    public SetOperations<String, Object> setOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForSet();
    }

    @Bean
    public ZSetOperations<String, Object> zSetOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForZSet();
    }

    @Bean
    public GeoOperations<String, Object> geoOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForGeo();
    }

    @Bean
    public HyperLogLogOperations<String, Object> hyperLogLogOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForHyperLogLog();
    }

}
