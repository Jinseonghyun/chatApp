package jin.chatapp.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.DefaultContentTypeResolver;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;

@Configuration    // Spring 컨텍스트에 대한 Bean 정의의 소스
@EnableWebSocketMessageBroker // 메시지 브로커가 지원하는 WebSocket 메시지 처리가 가능해집니다. 이는 일반적으로 실시간 통신을 용이하게 하기 위해 사용
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    // 한 클라이언트에서 다른 클라이언트로 메시지 라우팅을 담당하는 메시지 브로커를 구성
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/user");   // 브로커를 활성화합니다. 이 접두사가 있는 대상으로 전송된 메시지는 적절한 사용자 대기열로 라우팅
        registry.setApplicationDestinationPrefixes("/app");      // 애플리케이션 로직을 위한 메시지와 메시지 브로커를 위한 메시지를 구별하는 데 도움
        registry.setUserDestinationPrefix("/user");           // 사용자 간 메시징 시나리오에서 특정 사용자에게 메시지를 보내는 데 사용
    }

    // WebSocket 서버에 연결하는 데 사용할 WebSocket 끝점을 등록
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")  // /ws에 WebSocket 엔드포인트를 추가합니다. 이는 클라이언트가 WebSocket 연결을 시작하는 데 사용할 URL
                .withSockJS();  // 기본 WebSocket을 지원하지 않는 브라우저에 대해 SockJS 대체 옵션을 활성화합니다. SockJS는 WebSocket과 유사한 객체를 제공하는 JavaScript 라이브러리로, WebSocket을 지원하지 않는 브라우저에서 WebSocket 통신을 허용
    }

    //  메시지를 JSON과 변환하는 데 사용되는 메시지 변환기를 구성
    @Override
    public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
        DefaultContentTypeResolver resolver = new DefaultContentTypeResolver();  // 메시지의 콘텐츠 유형을 결정하는 확인자를 만듭니다.
        resolver.setDefaultMimeType(MimeTypeUtils.APPLICATION_JSON);   //기본 리소스 미디어 유형을 'application/json'으로 설정합니다. 즉, 메시지가 콘텐츠 유형을 명시적으로 지정하지 않으면 JSON으로 처
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter(); //  JSON 처리 라이브러리인 Jackson을 사용하여 JSON 메시지를 변환하는 MessageConverter를 생성
        converter.setObjectMapper(new ObjectMapper()); // JSON 변환을 위한 사용자 정의 ObjectMapper를 설정
        converter.setContentTypeResolver(resolver); // 콘텐츠 유형 확인자로 생성한 확인자를 설정
        messageConverters.add(converter);  // 메시지 변환기 목록에 사용자 정의 변환기를 추가
        return false;  // 'false'를 반환하면 기본 메시지 변환기를 재정의해서는 안 된다는 의미
    }
}