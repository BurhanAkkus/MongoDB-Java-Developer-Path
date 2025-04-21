package org.example;

import com.mongodb.*;
import com.mongodb.client.*;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.Arrays;

import static com.mongodb.client.model.Sorts.descending;

public class MongoClientAggregateExample {
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
                // Restaurants in Brooklyn
                Bson matchStage = Aggregates.match(Filters.eq("borough","Brooklyn"));
                // Group by cuisine
                Bson groupStage = Aggregates.group("$cuisine", Accumulators.sum("count",1));
                Bson sortStage = Aggregates.sort(descending("count"));
                restaurants.aggregate(Arrays.asList(matchStage,groupStage,sortStage)).forEach(doc -> System.out.println(doc.toJson()));
            } catch (MongoException e) {
                e.printStackTrace();
            }
        }
    }
}
