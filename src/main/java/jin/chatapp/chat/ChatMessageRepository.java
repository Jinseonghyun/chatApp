package jin.chatapp.chat;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

// 채팅 메시지를 데이터베이스에 저장하고 조회하는 기능을 제공
public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
    List<ChatMessage> findByChatId(String chatId); // chatId로 검색된 채팅 메시지들을 리스트 형태로 반환합니다. List는 여러 개의 채팅 메시지를 담을 수 있는 컬렉션
    // 채팅방 ID(chatId)를 기반으로 채팅 메시지를 검색합니다. Spring Data MongoDB는 메서드 이름에 따라 자동으로 쿼리를 생성 (chatId가 일치하는 모든 ChatMessage 문서를 반환)
}
