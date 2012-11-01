package org.diveintojee.poc.persistence.search;

import com.google.common.collect.Lists;
import org.diveintojee.poc.domain.Account;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author louis.gueye@gmail.com
 */
@Component(SearchResponseToAccountsListConverter.BEAN_ID)
public class SearchResponseToAccountsListConverter implements Converter<SearchResponse, List<Account>> {

    public static final String BEAN_ID = "SearchResponseToAccountsListConverter";

    @Autowired
    private JsonByteArrayToAccountConverter byteArrayToAccountConverter;

    @Override
    public List<Account> convert(SearchResponse source) {
        List<Account> accounts = Lists.newArrayList();
        if (source != null) {
            SearchHits searchHits = source.getHits();
            for (SearchHit searchHit : searchHits) {
                byte[] accountAsBytes = searchHit.source();
                Account account = byteArrayToAccountConverter.convert(accountAsBytes);
                accounts.add(account);
            }
        }
        return accounts;
    }

}
