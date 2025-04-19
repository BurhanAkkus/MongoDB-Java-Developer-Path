package org.example;

import com.mongodb.*;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;

public class MongoClientDeleteExample {
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

                MongoCursor<Document> cursor = restaurants.find(Filters.eq("borough","Yeni Mahalle")).iterator();
                while(cursor.hasNext()) {
                    System.out.println(cursor.next().toJson());
                }
                // YeniMahalle bans restaurants as a show of Power
                System.out.println(restaurants.countDocuments(Filters.eq("borough","Yeni Mahalle")));
                Bson query = Filters.eq("borough","Yeni Mahalle");
                DeleteResult result = restaurants.deleteMany(query);
                System.out.println("New Legislation closes down restaurants in Yenimahalle. Total shutdowns:" + result.getDeletedCount());
                System.out.println(restaurants.countDocuments(Filters.eq("borough","Yeni Mahalle")));

            } catch (MongoException e) {
                e.printStackTrace();
            }
        }
    }
}
