package com.ajs.arenasync.Exceptions;

public class ArenaSyncException extends RuntimeException{
    public ArenaSyncException(String message){
        super(message);
    }

    public ArenaSyncException(String message, Throwable cause){
        super(message, cause);
    }
}
