package me.jooomout.demorestapistudy.validation;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.validation.DefaultMessageCodesResolver;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.validation.ObjectError;

public class MessageResolverTest{

    MessageCodesResolver codesResolver = new DefaultMessageCodesResolver();

    @Test
    void messageCodeResolverObject(){
        String[] messageCode = codesResolver.resolveMessageCodes("wrong", "eventDto");
        for (String code: messageCode){
            System.out.println(code);
        }

        Assertions.assertThat(messageCode).containsExactly("wrong.eventDto", "wrong");
    }
    @Test
    void messageCodesResolverField(){
        String[] messageCode = codesResolver.resolveMessageCodes("wrong", "eventDto", "basePrice", String.class);
        for (String code: messageCode){
            System.out.println(code);
        }
        //bindingResult.rejectValue("basePrice" ,"wrong");
    }
}
