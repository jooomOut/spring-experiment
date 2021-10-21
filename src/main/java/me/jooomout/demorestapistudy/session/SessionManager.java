package me.jooomout.demorestapistudy.session;

import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SessionManager {
    public static final String SESSION_COOKIE_NAME = "sessionId";

    // 동시성 이슈가 있을 때 항상 ConcurrentHashMAp 사용
    private Map<String, Object> sessionStore = new ConcurrentHashMap<>();

    /* 세션 생성
    * 1. 생성
    * 2. 저장소에 저장
    * 3. 응답 쿠키를 전달
    * */
    public void createSession(Object value, HttpServletResponse response){
        // 1. ID 생성
        String sessionId = UUID.randomUUID().toString();
        // 2. 저장
        sessionStore.put(sessionId, value);
        // 3. 쿠키 생성, 전달
        response.addCookie(new Cookie(SESSION_COOKIE_NAME, sessionId));
    }

    public Object getSession(HttpServletRequest request){
        Cookie sessionCookie = findCookie(request, SESSION_COOKIE_NAME);
        if (sessionCookie == null)
            return null;
        return sessionStore.get(sessionCookie.getValue());
    }

    public void expire(HttpServletRequest request){
        Cookie cookie = findCookie(request, SESSION_COOKIE_NAME);
        if (cookie != null){
            sessionStore.remove(cookie.getValue());
        }
    }

    private Cookie findCookie(HttpServletRequest request, String cookieName){
        Cookie[] cookies = request.getCookies();
        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(cookieName))
                .findAny()
                .orElse(null)
                ;
    }

}
