package jin.chatapp.chatroom;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    /**
     * ptional<String> - 메소드는 발견되거나 생성된 경우 chatId를 포함하는 Optional을 반환하고,
     * 채팅방이 존재하지 않는 경우 빈 Optional을 반환하고 createNewRoomIfNotExists를 반환, '거짓'입니다.
     */
    public Optional<String> getChatRoomId( // 두 사용자 간의 기존 채팅방의 'chatId'를 검색
            String senderId,
            String recipientId,
            boolean createNewRoomIfNotExists // 두 사용자 간의 기존 채팅방 ID를 검색합니다. 만약 채팅방이 존재하지 않으면, createNewRoomIfNotExists가 true일 때 새로운 채팅방을 생성
    ) {
        return chatRoomRepository
                .findBySenderIdAndRecipientId(senderId, recipientId) // senderId 및 recipientId로 ChatRoomRepository를 쿼리하여 기존 채팅방을 찾으려고 시도
                .map(ChatRoom::getChatId)     // 채채팅방이 발견되면, ChatRoom 객체에서 chatId를 추출합니다. map 메서드는 Optional에 값을 변환할 때 사용
                .or(() -> {        // 채팅방이 존재하지 않을 경우, or 메서드를 사용하여 대체 로직을 제공
                    if(createNewRoomIfNotExists) {        // 새로운 채팅방을 생성해야 하는 경우 createNewRoomIfNotExists가 true일 때 새로운 채팅방을 만듭니다.
                        var chatId = createChatId(senderId, recipientId); // 새로운 채팅방의 ID를 생성
                        return Optional.of(chatId); // 새로 생성된 채팅방 ID를 Optional로 반환
                    }

                    return  Optional.empty(); // 채팅방을 생성하지 않을 경우 빈 Optional을 반환
                });
    }

    private String createChatId(String senderId, String recipientId) {  // 두 사용자의 채팅방 ID를 생성하고 데이터베이스에 저장
        var chatId = String.format("%s_%s", senderId, recipientId); // senderId와 recipientId를 이용하여 채팅방 ID를 생성합니다. 이 ID는 두 사용자 ID를 언더스코어(_)로 연결한 형태

        ChatRoom senderRecipient = ChatRoom  // senderId가 발신자이고 recipientId가 수신자인 채팅방을 생성
                .builder()
                .chatId(chatId)
                .senderId(senderId)
                .recipientId(recipientId)
                .build();

        ChatRoom recipientSender = ChatRoom
                .builder()
                .chatId(chatId)
                .senderId(recipientId)
                .recipientId(senderId)
                .build();

        // 생성한 채팅방을 데이터베이스에 저장
        chatRoomRepository.save(senderRecipient);
        chatRoomRepository.save(recipientSender);

        return chatId;  // 생성한 채팅방 ID를 반환
    }
}
