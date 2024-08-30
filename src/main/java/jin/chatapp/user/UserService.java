package jin.chatapp.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;

    // 사용자 저장
    public void saveUser(User user) {  // 새로운 사용자를 저장하면서 초기 상태를 ONLINE으로 설정
        user.setStatus(Status.ONLINE);
        repository.save(user);   // 메서드를 호출하여 사용자를 데이터베이스에 저장
    }

    // 연결 끊기
    public void disconnect(User user) {   // 사용자가 연결을 끊을 때, 해당 사용자의 상태를 OFFLINE으로 업데이트
        var stredUser = repository.findById(user.getNickName())  // 사용자를 닉네임으로 조회
                .orElse(null);
        if (stredUser != null) {
            stredUser.setStatus(Status.OFFLINE); // 조회된 사용자가 존재하는 경우(stredUser != null), 상태를 Status.OFFLINE으로 업데이트합니다
            repository.save(stredUser);
        }
    }

    // 연결된 사용자 찾기
    public List<User> findConnectedUsers() {  // 현재 ONLINE 상태인 모든 사용자를 검색
        return repository.findAllByStatus(Status.ONLINE);  // 메서드를 호출하여 현재 연결된(온라인 상태인) 모든 사용자를 반환
    }
}
