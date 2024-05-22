package org.example.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException{
    private final ErroCode errorCode;

    public CustomException(ErroCode errorCode){
        super(errorCode.getDetail());
        this.errorCode = errorCode;
    }
}
