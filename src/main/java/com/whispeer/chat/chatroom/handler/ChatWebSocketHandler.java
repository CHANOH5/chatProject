package com.whispeer.chat.chatroom.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.whispeer.chat.chatroom.dto.ChatMessageDTO;
import com.whispeer.chat.chatroom.repository.ChatRoomRepository;
import com.whispeer.chat.chatroom.service.ChatMessageService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final Map<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();
    private final ChatMessageService chatMessageService;
    private final ObjectMapper objectMapper;


    public ChatWebSocketHandler(ChatMessageService chatMessageService, ObjectMapper objectMapper) {
        this.chatMessageService = chatMessageService;
        this.objectMapper = objectMapper;
    }

    // 연결 수립 시
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String userId = (String) session.getAttributes().get("userId");

        if (userId != null) {
            sessionMap.put(userId, session); // 사용자 세션 저장
            System.out.println("연결된 사용자ID: " + userId);
        } else {
            System.out.println("인증되지 않은 사용자의 연결 시도");
            session.close();
        }

    } // afterConnectionEstablished

    // 메시지 수신 시
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
//        System.out.println("받은 메시지: " + message.getPayload());
//        // echo: 받은 메시지를 다시 클라이언트로 보냄
//        session.sendMessage(new TextMessage("Echo: " + message.getPayload()));
        // 파싱

        ChatMessageDTO chatMessage = objectMapper.readValue(message.getPayload(), ChatMessageDTO.class);
        chatMessageService.save(chatMessage);

        // 공개 채팅방이면 전체 브로드캐스트 (추후 참여자 목록 기반으로)
        if (chatMessage.getChatRoomId() == 1L) {
            for (WebSocketSession s : sessionMap.values()) { // sessionMap에 등록된 사용자들 for문 돌면서
                if (s.isOpen()) { // 웹소켓 열려있는 상태인지 확인
                    s.sendMessage(new TextMessage(objectMapper.writeValueAsString(chatMessage))); // DTO를 JSON 문자열로 변환해서 WebSocket 전용 문자열 메시지로 감싸서 던짐
                }
            }
            return;
        }

        // 수신자에게 메시지 전송 ------- 추후 관리자와 채팅하기로 해야하니까 수정 필요
        WebSocketSession receiverSession = sessionMap.get(chatMessage.getReceiverId());
        if (receiverSession != null && receiverSession.isOpen()) {
            receiverSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(chatMessage)));
        } else {
            System.out.println(("수신자 세션이 없거나 닫혀있음: {}" + chatMessage.getReceiverId()));
        }

    } // handleTextMessage

    // 연결 종료 시
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String userId = (String) session.getAttributes().get("userId");
        sessionMap.remove(userId); // 세션 제거

        System.out.println("연결 종료된 사용자ID: " + userId);
    } // afterConnectionClosed

} // end class
