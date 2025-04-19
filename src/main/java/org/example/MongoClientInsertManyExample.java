package org.example;

import com.mongodb.*;
import com.mongodb.client.*;
import com.mongodb.client.result.InsertManyResult;
import com.mongodb.internal.connection.Time;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class MongoClientInsertManyExample {
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

                List<Document> docsToInsert = new ArrayList<>();
                Document restaurant1 = new Document()
                        .append("borough","Yeni Mahalle")
                        .append("cuisine","Kebap")
                        .append("name","KoseKebap");
                docsToInsert.add(restaurant1);
                Document restaurant2 = new Document()
                        .append("borough","Kadikoy")
                        .append("cuisine","Burger")
                        .append("name","EkmekArasiKofte");
                docsToInsert.add(restaurant2);

                InsertManyResult result = restaurants.insertMany(docsToInsert);
                result.getInsertedIds().forEach((x,y) -> System.out.println(y.asObjectId()));

            } catch (MongoException e) {
                e.printStackTrace();
            }
        }
    }
}
