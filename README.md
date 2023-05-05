# financial_facts_service

Data access layer for the sticker price architecture responsible for handling data being sent to and from the database. It is a Spring Boot application that communicates with the PostgreSQL server via JPA. It establishes the database table structures and models to store public entities' financial filing data, their CIK/Symbol identity mappings, and discount information as defined below. 

## Discounts
Discount are defined in this context as being stocks that are below their current sticker price as determined by the sticker_price_service algorithm
