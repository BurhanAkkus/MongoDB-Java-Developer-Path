package org.example;

import com.mongodb.*;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;

public class MongoTransactionExample {

    private static void transactMoney(String fromAccountId, String toAccountId, Long amount, MongoCollection<Document> bankCollection,MongoClient client){

        final ClientSession clientSession = client.startSession();

        TransactionBody trxBody = new TransactionBody() {
            @Override
            public Object execute() {
                Bson query1 = Filters.eq("account_id",fromAccountId);
                Bson update1 = Updates.combine(Updates.inc("balance",-amount));

                Bson query2 = Filters.eq("account_id",toAccountId);
                Bson update2 = Updates.combine(Updates.inc("balance",amount));

                Document payerAccount = bankCollection.find(query1).first();
                System.out.println("Current Balance of the Payer: " + payerAccount.getLong("balance"));

                Document receiverAccount = bankCollection.find(query2).first();
                System.out.println("Current Balance of the Receiver: " + receiverAccount.getLong("balance"));

                bankCollection.updateOne(clientSession,query1,update1);
                bankCollection.updateOne(clientSession,query2,update2);

                payerAccount = bankCollection.find(query1).first();
                System.out.println("Current Balance of the Payer: " + payerAccount.getLong("balance"));

                receiverAccount = bankCollection.find(query2).first();
                System.out.println("Current Balance of the Receiver: " + receiverAccount.getLong("balance"));

                return "Transaction executed flawlessly.!";
            }
        };

        try{
            clientSession.withTransaction(trxBody);
        }catch (Exception e){
            System.out.println(e);
        }finally {
            clientSession.close();
        }
    }
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
                MongoDatabase bank = mongoClient.getDatabase("bank");
                MongoCollection<Document> accounts = bank.getCollection("accounts");

                Bson query = Filters.eq("account_id","1");
                Bson update = Updates.combine(Updates.set("balance",1453L));
                UpdateResult result = accounts.updateOne(query,update,new UpdateOptions().upsert(true));
                Bson query2 = Filters.eq("account_id","2");
                Bson update2 = Updates.combine(Updates.set("balance",1553L));
                UpdateResult result2 = accounts.updateOne(query2,update2,new UpdateOptions().upsert(true));

                transactMoney("1","2",555L,accounts,mongoClient);

            } catch (MongoException e) {
                e.printStackTrace();
            }
        }
    }
}
