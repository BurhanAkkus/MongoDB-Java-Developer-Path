package org.example;

import com.mongodb.*;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;

public class MongoClientUpdateExample {
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

                MongoCursor<Document> cursor = restaurants.find(Filters.eq("name","EkmekArasiKofte")).iterator();
                while(cursor.hasNext()) {
                    System.out.println(cursor.next().toJson());
                }
                // Burger gets added to Turkish Vocabulary
                System.out.println(restaurants.countDocuments(Filters.eq("name","EkmekArasiKofte")));
                Bson query = Filters.eq("name","EkmekArasiKofte");
                Bson updates = Updates.combine(Updates.set("cuisine","Burger"));
                UpdateResult result = restaurants.updateMany(query,updates);
                System.out.println(restaurants.countDocuments(Filters.eq("name","EkmekArasiKofte")));
                System.out.println(restaurants.countDocuments(Filters.eq("cuisine","Burger")));

            } catch (MongoException e) {
                e.printStackTrace();
            }
        }
    }
}
