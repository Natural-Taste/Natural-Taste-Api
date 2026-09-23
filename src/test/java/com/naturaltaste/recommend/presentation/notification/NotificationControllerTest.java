package com.naturaltaste.recommend.presentation.notification;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void findAndReadFriendRequestNotification() throws Exception {
        AuthFixture requester = signup("notification-requester@example.com", "요청자");
        AuthFixture receiver = signup("notification-receiver@example.com", "수신자");

        mockMvc.perform(post("/friends/requests")
                        .header("Authorization", "Bearer " + requester.accessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "receiverId": %d
                                }
                                """.formatted(receiver.userId())))
                .andExpect(status().isCreated());

        String notificationResponse = mockMvc.perform(get("/notifications")
                        .header("Authorization", "Bearer " + receiver.accessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value("FRIEND_REQUEST"))
                .andExpect(jsonPath("$[0].read").value(false))
                .andReturn()
                .getResponse()
                .getContentAsString();
        Long notificationId = objectMapper.readTree(notificationResponse).get(0).get("id").longValue();

        mockMvc.perform(get("/notifications/unread-count")
                        .header("Authorization", "Bearer " + receiver.accessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.unreadCount").value(1));

        mockMvc.perform(patch("/notifications/{notificationId}/read", notificationId)
                        .header("Authorization", "Bearer " + receiver.accessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.read").value(true));
    }

    @Test
    void communityCommentAndRecommendationCreateAuthorNotifications() throws Exception {
        AuthFixture author = signup("notification-author@example.com", "작성자");
        AuthFixture actor = signup("notification-actor@example.com", "활동자");
        Long postId = createPostAndReadId(author.accessToken());

        mockMvc.perform(post("/community/posts/{postId}/comments", postId)
                        .header("Authorization", "Bearer " + actor.accessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "content": "댓글입니다."
                                }
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/community/posts/{postId}/recommend", postId)
                        .header("Authorization", "Bearer " + actor.accessToken()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/notifications")
                        .header("Authorization", "Bearer " + author.accessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value("COMMUNITY_RECOMMENDATION"))
                .andExpect(jsonPath("$[1].type").value("COMMUNITY_COMMENT"));

        mockMvc.perform(patch("/notifications/read-all")
                        .header("Authorization", "Bearer " + author.accessToken()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/notifications/unread-count")
                        .header("Authorization", "Bearer " + author.accessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.unreadCount").value(0));
    }

    private Long createPostAndReadId(String accessToken) throws Exception {
        String createResponse = mockMvc.perform(post("/community/posts")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "알림 테스트",
                                  "content": "알림 테스트 게시글",
                                  "restaurant": {
                                    "provider": "KAKAO",
                                    "providerPlaceId": "notification-place-1",
                                    "name": "알림 식당",
                                    "address": "서울시 강남구",
                                    "latitude": 37.1234567,
                                    "longitude": 127.1234567,
                                    "category": "음식점",
                                    "phone": "02-000-0000",
                                    "placeUrl": "https://place.map.kakao.com/notification-place-1"
                                  }
                                }
                                """))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readTree(createResponse).get("id").longValue();
    }

    private AuthFixture signup(String email, String name) throws Exception {
        String response = mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "%s",
                                  "password": "password1234",
                                  "name": "%s"
                                }
                                """.formatted(email, name)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(response);
        return new AuthFixture(
                jsonNode.get("userId").longValue(),
                jsonNode.get("accessToken").asText()
        );
    }

    private record AuthFixture(Long userId, String accessToken) {
    }
}
