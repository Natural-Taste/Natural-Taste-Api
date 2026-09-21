package com.naturaltaste.recommend.presentation.community;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class CommunityPostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createListDetailAndSaveRestaurantFromPost() throws Exception {
        String accessToken = signupAndReadToken("community-user@example.com");

        String createResponse = mockMvc.perform(post("/community/posts")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "오늘의 초밥집",
                                  "content": "점심에 가기 좋았습니다.",
                                  "imageUrl": "https://example.com/sushi.jpg",
                                  "restaurant": {
                                    "provider": "KAKAO",
                                    "providerPlaceId": "community-1",
                                    "name": "초밥집",
                                    "address": "서울시 강남구",
                                    "latitude": 37.1234567,
                                    "longitude": 127.1234567,
                                    "category": "음식점 > 일식",
                                    "phone": "02-000-0000",
                                    "placeUrl": "https://place.map.kakao.com/community-1"
                                  }
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("오늘의 초밥집"))
                .andExpect(jsonPath("$.imageUrl").value("https://example.com/sushi.jpg"))
                .andExpect(jsonPath("$.restaurant.name").value("초밥집"))
                .andReturn()
                .getResponse()
                .getContentAsString();
        Long postId = objectMapper.readTree(createResponse).get("id").longValue();

        mockMvc.perform(get("/community/posts")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(postId));

        mockMvc.perform(get("/community/posts/{postId}", postId)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("점심에 가기 좋았습니다."))
                .andExpect(jsonPath("$.commentCount").value(0))
                .andExpect(jsonPath("$.recommendationCount").value(0))
                .andExpect(jsonPath("$.recommended").value(false));

        mockMvc.perform(post("/community/posts/{postId}/recommend", postId)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recommendationCount").value(1))
                .andExpect(jsonPath("$.recommended").value(true));

        mockMvc.perform(post("/community/posts/{postId}/comments", postId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "content": "저도 가보고 싶어요."
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("저도 가보고 싶어요."));

        mockMvc.perform(get("/community/posts/{postId}/comments", postId)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("저도 가보고 싶어요."));

        mockMvc.perform(post("/community/posts/{postId}/save", postId)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.saved").value(true));
    }

    @Test
    void deletePostAllowsOnlyAuthor() throws Exception {
        String authorToken = signupAndReadToken("community-delete-author@example.com");
        String otherToken = signupAndReadToken("community-delete-other@example.com");
        Long postId = createPostAndReadId(authorToken, "delete-post-1");

        mockMvc.perform(delete("/community/posts/{postId}", postId)
                        .header("Authorization", "Bearer " + otherToken))
                .andExpect(status().isForbidden());

        mockMvc.perform(delete("/community/posts/{postId}", postId)
                        .header("Authorization", "Bearer " + authorToken))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/community/posts/{postId}", postId)
                        .header("Authorization", "Bearer " + authorToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteCommentAllowsOnlyAuthor() throws Exception {
        String postAuthorToken = signupAndReadToken("community-comment-post-author@example.com");
        String commentAuthorToken = signupAndReadToken("community-comment-author@example.com");
        Long postId = createPostAndReadId(postAuthorToken, "delete-comment-1");
        String commentResponse = mockMvc.perform(post("/community/posts/{postId}/comments", postId)
                        .header("Authorization", "Bearer " + commentAuthorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "content": "삭제할 댓글입니다."
                                }
                                """))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        Long commentId = objectMapper.readTree(commentResponse).get("id").longValue();

        mockMvc.perform(delete("/community/posts/{postId}/comments/{commentId}", postId, commentId)
                        .header("Authorization", "Bearer " + postAuthorToken))
                .andExpect(status().isForbidden());

        mockMvc.perform(delete("/community/posts/{postId}/comments/{commentId}", postId, commentId)
                        .header("Authorization", "Bearer " + commentAuthorToken))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/community/posts/{postId}/comments", postId)
                        .header("Authorization", "Bearer " + postAuthorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    private Long createPostAndReadId(String accessToken, String placeId) throws Exception {
        String createResponse = mockMvc.perform(post("/community/posts")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "삭제 테스트",
                                  "content": "삭제 권한 확인",
                                  "imageUrl": "https://example.com/delete.jpg",
                                  "restaurant": {
                                    "provider": "KAKAO",
                                    "providerPlaceId": "%s",
                                    "name": "삭제 식당",
                                    "address": "서울시 강남구",
                                    "latitude": 37.1234567,
                                    "longitude": 127.1234567,
                                    "category": "음식점",
                                    "phone": "02-000-0000",
                                    "placeUrl": "https://place.map.kakao.com/%s"
                                  }
                                }
                                """.formatted(placeId, placeId)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readTree(createResponse).get("id").longValue();
    }

    private String signupAndReadToken(String email) throws Exception {
        String response = mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "%s",
                                  "password": "password1234",
                                  "name": "사용자"
                                }
                                """.formatted(email)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(response);
        return jsonNode.get("accessToken").asText();
    }
}
