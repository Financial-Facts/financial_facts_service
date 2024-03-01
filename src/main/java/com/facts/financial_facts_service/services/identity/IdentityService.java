package com.facts.financial_facts_service.services.identity;

import com.facts.financial_facts_service.constants.enums.ModelType;
import com.facts.financial_facts_service.entities.identity.Identity;
import com.facts.financial_facts_service.entities.identity.models.BulkIdentitiesRequest;
import com.facts.financial_facts_service.entities.identity.models.SortOrder;
import com.facts.financial_facts_service.exceptions.DataNotFoundException;
import com.facts.financial_facts_service.exceptions.InvalidRequestException;
import com.facts.financial_facts_service.services.identity.comparators.IdentityComparator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;


@Service
public class IdentityService {

    final Logger logger = LoggerFactory.getLogger(IdentityService.class);

    @Autowired
    ConcurrentHashMap<String, Identity> identityMap;

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
        Collection<Identity> identities = identityMap.values();

        if (StringUtils.isNotBlank(request.getKeyword())) {
            identities = filterByKeyword(request, identities);
        }

        int limit = request.getLimit();
        if (limit > identities.size()) {
            limit = identities.size();
        }

        int startIndex = request.getStartIndex();
        if (startIndex > limit) {
            startIndex = limit;
        }

        boolean reverseSort = Objects.nonNull(request.getOrder()) &&
                request.getOrder().equals(SortOrder.DESC);

        List<Identity> identityList = identities
            .stream()
            .sorted(new IdentityComparator(request.getSortBy(), reverseSort))
            .toList()
            .subList(startIndex, limit);

        return Mono.just(identityList);
    }

    private Collection<Identity> filterByKeyword(
        BulkIdentitiesRequest request,
        Collection<Identity> identities
    ) {
        String keyword = request.getKeyword().toLowerCase();
        return identities.stream().filter(identity -> {
            if (Objects.nonNull(request.getSearchBy())) {
                return switch (request.getSearchBy()) {
                    case CIK -> identity.getCik().toLowerCase().contains(keyword);
                    case SYMBOL -> identity.getSymbol().toLowerCase().contains(keyword);
                    case NAME -> identity.getName().toLowerCase().contains(keyword);
                };
            }

            return identity.getName().toLowerCase().contains(keyword) ||
                    identity.getSymbol().toLowerCase().contains(keyword) ||
                    identity.getCik().toLowerCase().contains(keyword);
        }).toList();
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
        if (request.getLimit() - request.getStartIndex() > 1000) {
            request.setLimit(request.getStartIndex() + 1000);
        }
    }
}
