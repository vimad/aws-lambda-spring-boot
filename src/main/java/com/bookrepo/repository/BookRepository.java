package com.bookrepo.repository;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.bookrepo.domain.Book;
import com.bookrepo.util.AppConfig;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BookRepository {

    private final DynamoDBMapper dbMapper;

    public BookRepository() {
        String tableName = AppConfig.getOrDefault("table-name", "books");
        AmazonDynamoDB dynamoDB = AmazonDynamoDBClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
        DynamoDBMapperConfig dynamoDBMapperConfig = new DynamoDBMapperConfig.Builder()
                .withTableNameOverride(DynamoDBMapperConfig.TableNameOverride.withTableNameReplacement(tableName))
                .build();
        this.dbMapper = new DynamoDBMapper(dynamoDB, dynamoDBMapperConfig);
    }

    public List<Book> getAllBooks() {
        return dbMapper.scan(Book.class, new DynamoDBScanExpression());
    }

    public void save(Book book) {
        dbMapper.save(book);
    }
}
