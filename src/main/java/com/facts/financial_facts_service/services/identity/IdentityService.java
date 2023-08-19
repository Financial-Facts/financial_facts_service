package com.facts.financial_facts_service.services.identity;

import com.facts.financial_facts_service.constants.enums.ModelType;
import com.facts.financial_facts_service.entities.identity.Identity;
import com.facts.financial_facts_service.entities.identity.models.BulkIdentitiesRequest;
import com.facts.financial_facts_service.entities.identity.models.SortBy;
import com.facts.financial_facts_service.entities.identity.models.SortOrder;
import com.facts.financial_facts_service.exceptions.DataNotFoundException;
import com.facts.financial_facts_service.exceptions.InvalidRequestException;
import com.facts.financial_facts_service.services.identity.comparators.IdentityCikComparator;
import com.facts.financial_facts_service.services.identity.comparators.IdentityNameComparator;
import com.facts.financial_facts_service.services.identity.comparators.IdentitySymbolComparator;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;


@Service
public class IdentityService {

    final Logger logger = LoggerFactory.getLogger(IdentityService.class);

    @Autowired
    ConcurrentHashMap<String, Identity> identityMap;

    ConcurrentHashMap<SortBy, Comparator<Identity>> comparatorMap;

    @PostConstruct
    public void init() {
        comparatorMap = new ConcurrentHashMap<>();
        comparatorMap.put(SortBy.CIK, new IdentityCikComparator());
        comparatorMap.put(SortBy.NAME, new IdentityNameComparator());
        comparatorMap.put(SortBy.SYMBOL, new IdentitySymbolComparator());
    }

    public Mono<Identity> getIdentityFromIdentityMap(String cik) {
        logger.info("In identity service getting identity for cik {}", cik);
        Identity identity = identityMap.get(cik);
        if (Objects.isNull(identity)) {
            logger.error("Identity not found for cik {}", cik);
            throw new DataNotFoundException(ModelType.IDENTITY, cik);
        }
        return Mono.just(identity);
    }

    public Mono<List<Identity>> getBulkIdentities(BulkIdentitiesRequest request) {
        logger.info("In identity service getting bulk identities for {}", request);
        checkIsValidBulkRequest(request);
        int limit = request.getLimit();
        if (limit > identityMap.size()) {
            limit = identityMap.size();
        }
        Comparator<Identity> comparator = comparatorMap.get(request.getSortBy());
        if (Objects.nonNull(request.getOrder()) && request.getOrder().equals(SortOrder.DESC)) {
            comparator = comparator.reversed();
        }
        List<Identity> identities = identityMap.values().stream()
                .sorted(comparator).toList().subList(request.getStartIndex(), limit);
        return Mono.just(identities);
    }

    private void checkIsValidBulkRequest(BulkIdentitiesRequest request) {
        if (request.getStartIndex() > request.getLimit()) {
            throw new InvalidRequestException("Start index cannot be greater than limit index");
        }
        if (request.getStartIndex() >= identityMap.size()) {
            throw new InvalidRequestException("Start index is out of bounds");
        }
        if (Objects.isNull(request.getSortBy())) {
            throw new InvalidRequestException("Invalid sort by in request");
        }
        if (request.getLimit() - request.getStartIndex() > 100) {
            request.setLimit(request.getStartIndex() + 100);
        }
    }
}
