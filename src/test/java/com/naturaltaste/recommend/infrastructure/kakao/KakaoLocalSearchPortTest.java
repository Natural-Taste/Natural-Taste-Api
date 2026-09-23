package com.naturaltaste.recommend.infrastructure.kakao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.queryParam;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withForbiddenRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.naturaltaste.recommend.application.common.BusinessException;
import com.naturaltaste.recommend.application.common.ErrorCode;
import com.naturaltaste.recommend.application.usecase.restaurant.RestaurantSearchResult;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class KakaoLocalSearchPortTest {

    @Test
    void searchSendsKakaoAuthorizationHeader() {
        RestClient.Builder builder = RestClient.builder().baseUrl("https://dapi.kakao.com");
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        KakaoLocalSearchPort port = new KakaoLocalSearchPort(builder.build(), "test-rest-api-key");
        server.expect(requestTo(containsString("/v2/local/search/keyword.json")))
                .andExpect(requestTo(containsString("query=%EC%B4%88%EB%B0%A5")))
                .andExpect(queryParam("category_group_code", "FD6"))
                .andExpect(header("Authorization", "KakaoAK test-rest-api-key"))
                .andRespond(withSuccess("""
                        {
                          "documents": [
                            {
                              "id": "1",
                              "place_name": "테스트 초밥",
                              "address_name": "서울시 강남구",
                              "road_address_name": "서울시 강남구 테헤란로",
                              "x": "127.027",
                              "y": "37.499",
                              "category_name": "음식점 > 일식",
                              "phone": "02-123-4567",
                              "place_url": "https://place.map.kakao.com/1"
                            }
                          ]
                        }
                        """, MediaType.APPLICATION_JSON));

        List<RestaurantSearchResult> results = port.search("초밥", null, null);

        assertThat(results).hasSize(1);
        assertThat(results.getFirst().provider()).isEqualTo("KAKAO");
        assertThat(results.getFirst().name()).isEqualTo("테스트 초밥");
        server.verify();
    }

    @Test
    void searchRejectsMissingRestApiKey() {
        KakaoLocalSearchPort port = new KakaoLocalSearchPort(RestClient.builder().build(), "");

        assertThatThrownBy(() -> port.search("초밥", null, null))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.KAKAO_SEARCH_FAILED);
    }

    @Test
    void searchRejectsRestApiKeyWithAuthorizationPrefix() {
        KakaoLocalSearchPort port = new KakaoLocalSearchPort(RestClient.builder().build(), "KakaoAK test-rest-api-key");

        assertThatThrownBy(() -> port.search("초밥", null, null))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.KAKAO_SEARCH_FAILED);
    }

    @Test
    void searchThrowsBusinessExceptionWhenKakaoReturnsForbidden() {
        RestClient.Builder builder = RestClient.builder().baseUrl("https://dapi.kakao.com");
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        KakaoLocalSearchPort port = new KakaoLocalSearchPort(builder.build(), "test-rest-api-key");
        server.expect(requestTo(containsString("/v2/local/search/keyword.json")))
                .andRespond(withForbiddenRequest().body("""
                        {"errorType":"AccessDeniedError","message":"cannot access API"}
                        """));

        assertThatThrownBy(() -> port.search("초밥", null, null))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.KAKAO_SEARCH_FAILED);
        server.verify();
    }
}
