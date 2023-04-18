package kr.codesqaud.cafe.user.service;

import kr.codesqaud.cafe.exception.DuplicateKeyException;
import kr.codesqaud.cafe.user.domain.User;
import kr.codesqaud.cafe.user.dto.SessionUser;
import kr.codesqaud.cafe.user.dto.UserAddForm;
import kr.codesqaud.cafe.user.dto.UserLoginForm;
import kr.codesqaud.cafe.user.dto.UserResponse;
import kr.codesqaud.cafe.user.repository.UserJdbcRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserJdbcRepository userRepository;

    public UserService(UserJdbcRepository userJdbcRepository) {
        this.userRepository = userJdbcRepository;
    }

    public String addUser(UserAddForm userAddForm) {
        if (userRepository.containsUserId(userAddForm.getUserId())) {
            throw new DuplicateKeyException("중복된 아이디가 이미 존재합니다.");
        }
        return userRepository.save(userAddForm.toUser());
    }

    public Optional<SessionUser> loginCheck(UserLoginForm userLoginForm) {
        if (!userRepository.containsUserId(userLoginForm.getUserId())) {
            return Optional.empty();
        }
        User user = userRepository.findByUserId(userLoginForm.getUserId());
        if (!userLoginForm.getPassword().equals(user.getPassword())) {
            return Optional.empty();
        }
        return Optional.ofNullable(SessionUser.from(user));
    }

    public UserResponse getUser(String userId) {
        return UserResponse.from(userRepository.findByUserId(userId));
    }

    public List<UserResponse> getUserList() {
        return userRepository.findAll().stream().map(UserResponse::from).collect(Collectors.toList());
    }
}
