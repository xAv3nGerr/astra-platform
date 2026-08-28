package pl.v3bc.platform.registry.database;


import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import lombok.Getter;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.bson.UuidRepresentation;

/**
 * @Author: v3bc_
 * @Date: 8/15/26
 * @Project: astramc-paper
   */

@Getter
public class DatabaseRegistry {
    private Datastore datastore;

    public void connect(String databaseConnectionUri, String databasePrefix) {
        Configurator.setLevel("org.mongodb.driver", org.apache.logging.log4j.Level.OFF);
        MongoClientSettings settings = MongoClientSettings.builder().applyConnectionString(new ConnectionString(databaseConnectionUri)).uuidRepresentation(UuidRepresentation.STANDARD).build();
        this.datastore = Morphia.createDatastore(MongoClients.create(settings), databasePrefix);
    }
}

