package org.example;

import com.mongodb.*;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.InsertManyResult;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class MongoClientFindExample {
    public static void main(String[] args) {
        String connectionString = "mongodb+srv://admin:admin@cluster0.8opdgyl.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0";
        ServerApi serverApi = ServerApi.builder()
                .version(ServerApiVersion.V1)
                .build();

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .serverApi(serverApi)
                .build();

        // Create a new client and connect to the server
        try (MongoClient mongoClient = MongoClients.create(settings)) {
            try {
                // Send a ping to confirm a successful connection
                MongoDatabase sample_restaurantsDB = mongoClient.getDatabase("sample_restaurants");
                MongoCollection<Document> restaurants = sample_restaurantsDB.getCollection("restaurants");
                System.out.println(restaurants.countDocuments());
                MongoCursor<Document> cursor = restaurants.find().iterator();
                while(cursor.hasNext()) {
                    System.out.println(cursor.next().toJson());
                }
                Document kadikoy = restaurants.find(Filters.and(Filters.eq("borough","Kadikoy"))).first();
                System.out.println(kadikoy.toJson());

            } catch (MongoException e) {
                e.printStackTrace();
            }
        }
    }
}
