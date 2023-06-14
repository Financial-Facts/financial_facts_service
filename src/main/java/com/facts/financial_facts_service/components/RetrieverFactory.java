package com.facts.financial_facts_service.components;

import com.facts.financial_facts_service.constants.Constants;
import com.facts.financial_facts_service.constants.ModelType;
import com.facts.financial_facts_service.entities.facts.retriever.GaapRetriever;
import com.facts.financial_facts_service.entities.facts.retriever.IRetriever;
import com.facts.financial_facts_service.entities.facts.retriever.IfrsRetriever;
import com.facts.financial_facts_service.exceptions.DataNotFoundException;
import lombok.NoArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class RetrieverFactory implements Constants {

    public IRetriever getRetriever(String cik, String facts) {
        JSONObject json = new JSONObject(facts);
        JSONObject factsJson = (JSONObject) json.get(FACTS);
        if (factsJson.has(US_GAAP)) {
            return new GaapRetriever(cik, factsJson);
        }
        if (factsJson.has(IFRS_FULL)) {
            return new IfrsRetriever(cik, factsJson);
        }
        throw new DataNotFoundException(ModelType.FACTS, cik);
    }
}
