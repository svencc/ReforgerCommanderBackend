package com.recom.persistence.user;

import com.recom.entity.User;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserPersistenceLayer {

    @NonNull
    private final UserRepository userRepository;

    public User save(@NonNull final User user) {
        return userRepository.save(user);
    }


    @Cacheable(cacheNames = "UserPersistenceLayer.findByUUID")
    public Optional<User> findByUUID(
            @NonNull final UUID uuid
    ) {
        return userRepository.findById(uuid);
    }

}