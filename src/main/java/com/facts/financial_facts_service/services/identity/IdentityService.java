package com.facts.financial_facts_service.services.identity;

import com.facts.financial_facts_service.constants.ModelType;
import com.facts.financial_facts_service.entities.identity.Identity;
import com.facts.financial_facts_service.entities.identity.models.BulkIdentitiesRequest;
import com.facts.financial_facts_service.entities.identity.models.SortOrder;
import com.facts.financial_facts_service.exceptions.DataNotFoundException;
import com.facts.financial_facts_service.services.identity.components.ComparatorMap;
import com.facts.financial_facts_service.services.identity.components.IdentityMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
public class IdentityService {

    Logger logger = LoggerFactory.getLogger(IdentityService.class);

    @Autowired
    IdentityMap identityMap;

    @Autowired
    ComparatorMap comparatorMap;

    public Mono<Identity> getIdentityFromIdentityMap(String cik) {
        logger.info("In identity service getting identity for cik {}", cik);
        try {
            return Mono.just(identityMap.getValue(cik).orElseGet(() -> {
                logger.error("Identity not found for cik {}", cik);
                throw new DataNotFoundException(ModelType.IDENTITY, cik);
            }));
        } catch (DataAccessException error) {
            logger.error("Error occurred while retrieving identity for cik {}: {}", cik, error.getMessage());
            throw new ResponseStatusException(HttpStatus.CONFLICT, error.getMessage());
        }
    }

    public Mono<List<Identity>> getBulkIdentities(BulkIdentitiesRequest request) {
        logger.info("In identity service getting bulk identities for {}", request);
        Map<String, Identity> currentIdentityMap = identityMap.getCurrentIdentityMap();
        checkIsValidBulkRequest(currentIdentityMap, request);
        int limit = request.getLimit();
        if (limit > currentIdentityMap.size()) {
            limit = currentIdentityMap.size();
        }
        Comparator<Identity> comparator = comparatorMap.getComparator(request.getSortBy());
        if (Objects.nonNull(request.getOrder()) && request.getOrder().equals(SortOrder.DESC)) {
            comparator = comparator.reversed();
        }
        List<Identity> identities = currentIdentityMap.values().stream()
                .sorted(comparator).toList().subList(request.getStartIndex(), limit);
        return Mono.just(identities);
    }

    private void checkIsValidBulkRequest(Map<String, Identity> currentIdentityMap, BulkIdentitiesRequest request) {
        if (request.getStartIndex() > request.getLimit()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start index cannot be greater than limit index");
        }
        if (request.getStartIndex() >= currentIdentityMap.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start index is out of bounds");
        }
        if (Objects.isNull(request.getSortBy())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "SortBy cannot be null");
        }
    }
}
