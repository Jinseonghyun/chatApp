package jin.chatapp.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;

    // 사용자 저장
    public void saveUser(User user) {
        user.setStatus(Status.ONLINE);
        repository.save(user);
    }

    // 연결 끊기
    public void disconnect(User user) {
        var stredUser = repository.findById(user.getNickName())
                .orElse(null);
        if (stredUser != null) {
            stredUser.setStatus(Status.OFFLINE);
            repository.save(stredUser);
        }
    }

    // 연결된 사용자 찾기
    public List<User> findConnectedUsers() {
        return repository.findAllByStatus(Status.ONLINE);
    }
}
