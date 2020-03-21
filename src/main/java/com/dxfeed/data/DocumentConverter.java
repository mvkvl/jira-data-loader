package com.dxfeed.data;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.apache.camel.Converter;
import org.apache.camel.TypeConverters;
import org.bson.Document;
import org.springframework.stereotype.Component;

@Component
public class DocumentConverter implements TypeConverters {

    @Converter
    public Document stringToDocument(String jsonStr) {
        return Document.parse(jsonStr);
    }

    @Converter
    public String documentToString(Document document) {
        return document.toJson();
    }


    @Converter
    public DBObject stringToDBObject(String jsonStr) {
        return new BasicDBObject(stringToDocument(jsonStr));
    }

    @Converter
    public String dbObjectToString(DBObject document) {
        return documentToString(new Document(document.toMap()));
    }


}
