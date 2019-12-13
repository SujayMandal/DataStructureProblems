package com.ca.umg.business.transaction.mongo.dao;

import java.io.IOException;

import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.Mongo;

import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;


public class BaseMongoTest {

    private static final String LOCALHOST = "127.0.0.1";
    private static final String DB_NAME = "ra_transaction_documents";
    private static final int MONGO_TEST_PORT = 27018;
    private static Mongo mongo;
    private static MongoTemplate template;

    private BaseMongoTest() {

    }

    public static MongoTemplate setTemplate() throws IOException {
        final MongodStarter runtime = MongodStarter.getDefaultInstance();
        IMongodConfig mongodConfig = new MongodConfigBuilder().version(Version.Main.PRODUCTION)
                    .net(new Net(MONGO_TEST_PORT, Network.localhostIsIPv6())).configServer(false).build();
            MongodExecutable mongodExecutable = runtime.prepare(mongodConfig);
        mongodExecutable.start();
            mongo = new Mongo(LOCALHOST, MONGO_TEST_PORT);
        template = new MongoTemplate(mongo, DB_NAME);

        return template;
    }

}
