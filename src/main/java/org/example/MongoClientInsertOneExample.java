package org.example;

import com.mongodb.*;
import com.mongodb.client.*;
import com.mongodb.internal.connection.Time;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class MongoClientInsertOneExample {
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
                List<Document> databases = mongoClient.listDatabases().into(new ArrayList<>());
                MongoDatabase database = mongoClient.getDatabase("sample_mflix");
                ListCollectionsIterable<Document> collections = database.listCollections();
                List<Long> collectionSizes = new ArrayList<>();
                List<Long> afterInsertCollectionSizes = new ArrayList<>();
                for(Document collectionDocument:collections){
                    MongoCollection collection = database.getCollection(collectionDocument.getString("name")) ;
                    System.out.println(collection.getNamespace());
                    System.out.println(collection.countDocuments());
                    collectionSizes.add(collection.countDocuments());
                    collection.insertOne(new Document("_id",new ObjectId())
                            .append("test","burhan")
                            .append("email",collection.countDocuments())
                            .append("user_id", Time.nanoTime())
                    );
                    afterInsertCollectionSizes.add(collection.countDocuments());
                }
                for(int i = 0; i < collectionSizes.size(); i++){
                    assertEquals((long)collectionSizes.get(i),afterInsertCollectionSizes.get(i) - 1);
                }
            } catch (MongoException e) {
                e.printStackTrace();
            }
        }
    }
}
