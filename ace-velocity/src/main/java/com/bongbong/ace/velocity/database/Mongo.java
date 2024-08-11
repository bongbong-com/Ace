package com.bongbong.ace.velocity.database;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import lombok.RequiredArgsConstructor;
import org.bson.Document;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@RequiredArgsConstructor
public class Mongo {
    private final MongoDatabase mongoDatabase;

    public void deleteDocument(String collectionName, String id, Object value) {
        MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);
        Document document = null;

        if (collection.find(Filters.eq(id, value)).iterator().hasNext())
            document = collection.find(Filters.eq(id, value)).first();


        if (document == null) return;

        collection.deleteMany(document);
    }

    public Document getDocument(String collectionName, String id, Object value) {
        MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);

        if (collection.find(Filters.eq(id, value)).iterator().hasNext())
            return collection.find(Filters.eq(id, value)).first();

        return null;
    }

    public void massUpdate(MongoUpdate mongoUpdate) {
        massUpdate(mongoUpdate.getCollectionName(), mongoUpdate.getId(), mongoUpdate.getUpdate());
    }

    public void massUpdate(String collectionName, Object id, Map<String, Object> updates) throws LinkageError {
        MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);

        Document document = collection.find(new Document("_id", id)).first();
        if (document == null) collection.insertOne(new Document("_id", id));

        updates.forEach((key, value) -> collection.updateOne(Filters.eq("_id", id), Updates.set(key, value)));
    }

    public void createCollection(String collectionName) {
        AtomicBoolean exists = new AtomicBoolean(false);
        mongoDatabase.listCollectionNames().forEach(s -> {
            if (s.equals(collectionName)) exists.set(true);
        });

        if (!exists.get()) mongoDatabase.createCollection(collectionName);
    }

    public FindIterable<Document> getCollectionIterable(String collectionName) {
        return mongoDatabase.getCollection(collectionName).find();
    }
}
