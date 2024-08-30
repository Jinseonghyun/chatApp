package jin.chatapp.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    // 유저 저장 (사용자를 저장하고 추가된 사용자를 /user/topic에 가입된 모든 클라이언트에 브로드캐스팅)
    @MessageMapping("/user.addUser")  // 대상 /user.addUser로 전송된 WebSocket 메시지를 처리합니다. STOMP 클라이언트는 이 대상으로 메시지를 보냅니다.
    @SendTo("/user/topic")        // 메서드의 반환 값이 /user/topic 대상으로 전송되어 이 주제의 모든 구독자에게 응답을 브로드캐스트해야 함을 지정
    public User addUser(@Payload User user) { // User 객체는 WebSocket 메시지 페이로드로 수신
        service.saveUser(user);
        return user;
    }

    // 유저 연결 끊기
    @MessageMapping("/user.disconnectUser")
    @SendTo("/user/topic")
    public User disconnect(@Payload User user) {
        service.disconnect(user);
        return user;
    }

    // 연결된 사용자 찾기 (현재 연결되어 있는 모든 사용자(즉, ONLINE 상태인 사용자)의 목록을 반환)
    @GetMapping("/users") // 메서드를 매핑하여 /users에 대한 HTTP GET 요청을 처리
    public ResponseEntity<List<User>> findConnectedUsers() {
        return ResponseEntity.ok(service.findConnectedUsers()); // 사용자 목록은 HTTP 상태가 '200 OK'인 'ResponseEntity'로 반환
    }
}
