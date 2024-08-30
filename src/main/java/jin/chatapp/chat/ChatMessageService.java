package jin.chatapp.chat;

import jin.chatapp.chatroom.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageService {
    private final ChatMessageRepository repository; // 채팅 메시지를 데이터베이스에 저장하고 조회하는 기능을 제공
    private final ChatRoomService chatRoomService; // 채팅방의 ID를 관리하는 서비스

    // 메서드는 채팅 메시지를 저장하며, 채팅방 ID를 확인하거나 필요 시 생성
    public ChatMessage save(ChatMessage chatMessage) { // ChatMessage 객체를 저장하는 기능을 제공합니다. 저장된 메시지를 반환
        var chatId = chatRoomService
                .getChatRoomId(chatMessage.getSenderId(), chatMessage.getRecipientId(), true) // ChatRoomService를 사용하여 senderId와 recipientId로 채팅방 ID를 가져옵니다. true를 전달하여 채팅방이 없을 경우 새로운 채팅방을 생성
                .orElseThrow(); // Optional이 값이 없을 경우 예외를 던집니다.
        chatMessage.setChatId(chatId); // 채팅 메시지 객체에 방금 얻은 chatId를 설정합니다. 이는 메시지가 어느 채팅방에 속하는지를 나타냅니다.
        repository.save(chatMessage); // ChatMessageRepository를 사용하여 채팅 메시지를 데이터베이스에 저장
        return chatMessage; //  저장된 채팅 메시지를 반환
    }

    // 사용자 쌍에 대한 채팅 메시지를 조회하며, 채팅방이 없을 경우 빈 리스트를 반환
    public List<ChatMessage> findChatMessages(String senderId, String recipientId) { // senderId와 recipientId에 대한 채팅 메시지 목록을 조회
        var chatId = chatRoomService.getChatRoomId(senderId, recipientId, false); // ChatRoomService를 사용하여 senderId와 recipientId로 채팅방 ID를 가져옵니다. false를 전달하여 채팅방이 없을 경우 새로운 채팅방을 생성하지 않습니다.
        return chatId.map(repository::findByChatId).orElse(new ArrayList<>()); // chatId가 존재하면 해당 ID로 채팅 메시지 목록을 조회하는 findByChatId 메서드를 호출
    }                                                                          // chatId가 존재하지 않으면 빈 ArrayList를 반환합니다. 이 경우 채팅방이 없어서 메시지가 없다는 의미
}
