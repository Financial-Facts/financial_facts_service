package com.facts.financial_facts_service.entities.facts.converter;

import com.facts.financial_facts_service.entities.facts.models.FactsWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class FactsDataConverter implements AttributeConverter<FactsWrapper, JsonBinaryType> {

    private final ObjectMapper mapper = JsonMapper.builder()
            .addModule(new JavaTimeModule()).build();

    @Override
    public JsonBinaryType convertToDatabaseColumn(FactsWrapper attribute) {
        JsonBinaryType jsonb = new JsonBinaryType(mapper, FactsWrapper.class);
        jsonb.deepCopy(attribute);
        return jsonb;
    }

    @Override
    public FactsWrapper convertToEntityAttribute(JsonBinaryType dbData) {
        return mapper.convertValue(dbData, FactsWrapper.class);
    }
}
