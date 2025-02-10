package com.korea.shop.exception;

public class NotEoughStockException extends RuntimeException{

    public NotEoughStockException(){
        super();
    }

    public NotEoughStockException(String message){
        super(message);
    }
    public NotEoughStockException(String message, Throwable cause){
        super(message, cause);
    }
    public NotEoughStockException(Throwable cause){
        super(cause);
    }

}
