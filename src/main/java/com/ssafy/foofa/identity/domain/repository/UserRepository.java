package com.ssafy.foofa.identity.domain.repository;

import com.ssafy.foofa.identity.domain.User;
import com.ssafy.foofa.identity.domain.enums.OauthProvider;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByOauthInfoProviderIdAndOauthInfoProvider(String providerId, OauthProvider provider);
}
