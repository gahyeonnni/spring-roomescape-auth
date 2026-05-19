package roomescape.user.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.exception.ErrorCode;
import roomescape.exception.business.BusinessException;
import roomescape.exception.business.DuplicateEmailException;
import roomescape.exception.business.InvalidCredentialsException;
import roomescape.user.domain.Role;
import roomescape.user.domain.User;
import roomescape.user.domain.UserFactory;
import roomescape.user.dto.LoginRequest;
import roomescape.user.dto.SignupRequest;
import roomescape.user.dto.SignupResponse;
import roomescape.user.repository.UserRepository;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final UserFactory userFactory;

    public UserService(UserRepository userRepository, UserFactory userFactory) {
        this.userRepository = userRepository;
        this.userFactory = userFactory;
    }

    @Transactional
    public SignupResponse signup(SignupRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new DuplicateEmailException();
        }
        Role role = request.isAdmin() ? Role.ADMIN : Role.USER;
        User user = userFactory.create(request.name(), request.email(), request.password(), role);
        User saved = userRepository.save(user);
        return SignupResponse.from(saved);
    }

    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    public User login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(InvalidCredentialsException::new);
        if (!user.getPassword().equals(request.password())) {
            throw new InvalidCredentialsException();
        }
        return user;
    }
}