package org.diveintojee.poc.persistence.search;

import org.diveintojee.poc.domain.Classified;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.internal.InternalSearchHit;
import org.elasticsearch.search.internal.InternalSearchHits;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * User: lgueye Date: 17/09/12 Time: 18:05
 */
@RunWith(MockitoJUnitRunner.class)
public class SearchResponseToClassifiedsListConverterTest {

    @Mock
    private JsonByteArrayToClassifiedConverter byteArrayToClassifiedConverter;

    @InjectMocks
    private
    SearchResponseToClassifiedsListConverter
            underTest = new SearchResponseToClassifiedsListConverter();

    @Test
    public void convertShouldSucceed() throws Exception {

        SearchResponse searchResponse = mock(SearchResponse.class);

        InternalSearchHit searchHit = mock(InternalSearchHit.class);
        SearchHits searchHits = new InternalSearchHits(new InternalSearchHit[]{searchHit}, 45, 4.2f);
        when(searchResponse.getHits()).thenReturn(searchHits);
        byte[] classifiedAsBytes = "{}".getBytes();
        when(searchHit.source()).thenReturn(classifiedAsBytes);
        Classified classified = mock(Classified.class);
        when(byteArrayToClassifiedConverter.convert(classifiedAsBytes)).thenReturn(classified);
        List<Classified> classifieds = underTest.convert(searchResponse);
        assertSame(classified, classifieds.get(0));
    }

}
