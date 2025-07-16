package com.whispeer.chat.user.service;

import com.whispeer.chat.user.dto.UserDTO;
import com.whispeer.chat.user.dto.UserRegisterDTO;
import com.whispeer.chat.user.dto.UserResponseDTO;
import com.whispeer.chat.user.entity.UserEntity;
import com.whispeer.chat.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 회원가입, create
    public UserResponseDTO createUser(UserRegisterDTO dto) {

        UserEntity entity = DtoToEntity(dto);
        UserEntity savedEntity = userRepository.save(entity);

        return entityToDTO(savedEntity);
    } // createUser()

    // 모든 회원 조회
    public List<UserResponseDTO> findAll() {

        return userRepository.findAll().stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());

    } // findAll()

    // 특정 회원 조회

    // 회원 수정

    // 회원 삭제 -> status를 비활성화 상태로 변경함

    private UserResponseDTO entityToDTO(UserEntity entity) {

        return UserResponseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .nickname(entity.getNickname())
                .email(entity.getEmail())
                .isAnonymous(entity.getIsAnonymous())
                .role(entity.getRole())
                .profileImage(entity.getProfileImage())
                .status(entity.getStatus())
                .build();

    } // entityToDTO()

    private UserEntity DtoToEntity(UserRegisterDTO dto) {

        return UserEntity.builder()
                .id(dto.getId())
                .password(passwordEncoder.encode(dto.getPassword()))
                .name(dto.getName())
                .nickname(dto.getNickname())
                .email(dto.getEmail())
                .isAnonymous(0)
                .role(1)
                .profileImage(dto.getProfileImage())
                .status(1)
                .build();

    } // DtoToEntity()

} // end class
