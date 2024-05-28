package org.example.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.redis.Cart;
import org.example.exception.CustomException;
import org.example.exception.ErrorCode;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisClient {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final ObjectMapper mapper = new ObjectMapper();

    public <T> T get(Long key, Class<T> classType){
        return get(key.toString(), classType);
    } // Long으로 받으면 자잘한 버그가 생기므로 int로 변환해서 key를 저장하자.

    private <T> T get(String key, Class<T> classType){
        String redisValue = (String) redisTemplate.opsForValue().get(key);
        if(ObjectUtils.isEmpty(redisValue)){
            return null;
        }else{
            try {
                return mapper.readValue(redisValue, classType);
            } catch (JsonProcessingException e) {
                log.error("Parsing error", e);
                return null;
            }
        }
    }


    public void put(Long key, Cart cart){
        put(key.toString(), cart);
    }

    private void put(String key, Cart cart){
        try{
            redisTemplate.opsForValue().set(key, mapper.writeValueAsString(cart));

        } catch (JsonProcessingException e) {
            throw new CustomException(ErrorCode.CART_CHANGE_FAIL);
        }
    }
}
