package com.naturaltaste.recommend.application.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    DUPLICATED_EMAIL(HttpStatus.CONFLICT, "이미 가입된 이메일입니다."),
    INVALID_LOGIN(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "현재 비밀번호가 올바르지 않습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다."),
    DELETED_USER(HttpStatus.FORBIDDEN, "탈퇴한 회원입니다."),
    INVALID_RESTAURANT_SEARCH_KEYWORD(HttpStatus.BAD_REQUEST, "검색어를 입력해 주세요."),
    KAKAO_SEARCH_FAILED(HttpStatus.BAD_GATEWAY, "맛집 검색에 실패했습니다."),
    RESTAURANT_NOT_FOUND(HttpStatus.NOT_FOUND, "맛집을 찾을 수 없습니다."),
    SAVED_RESTAURANT_NOT_FOUND(HttpStatus.NOT_FOUND, "저장한 맛집을 찾을 수 없습니다."),
    COMMUNITY_POST_NOT_FOUND(HttpStatus.NOT_FOUND, "커뮤니티 게시글을 찾을 수 없습니다."),
    USER_SEARCH_KEYWORD_REQUIRED(HttpStatus.BAD_REQUEST, "검색어를 입력해 주세요."),
    FRIEND_REQUEST_NOT_FOUND(HttpStatus.NOT_FOUND, "친구 요청을 찾을 수 없습니다."),
    INVALID_FRIEND_REQUEST(HttpStatus.BAD_REQUEST, "친구 요청을 처리할 수 없습니다."),
    FRIENDSHIP_REQUIRED(HttpStatus.FORBIDDEN, "친구가 아니면 조회할 수 없습니다.");

    private final HttpStatus status;
    private final String message;
}
