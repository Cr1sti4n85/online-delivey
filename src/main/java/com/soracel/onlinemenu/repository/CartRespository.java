package com.soracel.onlinemenu.repository;

import com.soracel.onlinemenu.entity.CartEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRespository extends MongoRepository<CartEntity, String> {
}
