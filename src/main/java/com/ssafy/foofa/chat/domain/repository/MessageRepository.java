package com.ssafy.foofa.chat.domain.repository;

import com.ssafy.foofa.chat.domain.Message;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MessageRepository extends MongoRepository<Message, String> {
}
