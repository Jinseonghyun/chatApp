package jin.chatapp.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate; // Spring의 메시징 기능을 활용하여 STOMP 프로토콜을 통해 클라이언트에게 메시지를 전송하는 데 사용
    private final ChatMessageService chatMessageService; // ChatMessageService는 채팅 메시지의 저장 및 조회를 처리하는 서비스

    @MessageMapping("/chat") // WebSocket 메시지를 처리하는 메서드임을 나타냅니다. 클라이언트가 /chat으로 메시지를 전송하면 이 메서드가 호출
    public void processMessage(@Payload ChatMessage chatMessage) { // ChatMessage 객체를 매개변수로 받아서 처리합니다. @Payload 어노테이션은 메시지 본문이 ChatMessage 객체로 변환
        ChatMessage savedMsg = chatMessageService.save(chatMessage); // ChatMessageService를 호출하여 chatMessage를 저장하고, 저장된 메시지를 savedMsg로 받습니다.
        messagingTemplate.convertAndSendToUser( // SimpMessagingTemplate를 사용하여 특정 사용자에게 메시지를 전송합니다. 메시지는 STOMP 프로토콜을 통해 /user/{recipientId}/queue/messages로 전송
                chatMessage.getRecipientId(), "/queue/messages",  // 메시지를 받을 사용자의 ID를 가져옵니다. "/queue/messages": 메시지를 전송할 목적지입니다. 이 경로는 사용자 큐의 메시지 대기열 나타냅니다.
                new ChatNotification( // 객체를 생성하여 전송할 메시지를 구성합니다. 이 객체는 메시지 ID, 발신자 ID, 수신자 ID, 그리고 메시지 내용을 포함
                        savedMsg.getId(),
                        savedMsg.getSenderId(),
                        savedMsg.getRecipientId(),
                        savedMsg.getContent()
                )
        );
    }

    @GetMapping("/messages/{senderId}/{recipientId}") // HTTP GET 요청을 처리하며, URL 경로의 {senderId}와 {recipientId}를 URL 경로 변수로 사용합니다. 이 경로로 요청이 들어오면 이 메서드가 호출
    public ResponseEntity<List<ChatMessage>> findChatMessages(@PathVariable String senderId,
                                                              @PathVariable String recipientId) { //  senderId와 recipientId를 경로 변수로 받아서, 해당 채팅 메시지를 조회합니다. ResponseEntity는 HTTP 응답을 구성하는 데 사용
        return ResponseEntity
                .ok(chatMessageService.findChatMessages(senderId, recipientId)); // HTTP 200 OK 응답을 반환합니다. 응답 본문에는 chatMessageService.findChatMessages(senderId, recipientId) 호출 결과로 얻어진 채팅 메시지 목록이 포함
    }
}