package me.jooomout.demorestapistudy.accounts;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.io.OutputStream;


/*
* 이렇게 만든걸 @JSONComponentㄹ 등록하면 이걸 리소스로 내보낼 때 마다 이렇게 수정돼서 나감
* 근데 다르게 사용하고 싶을 수도 있다.
* 고로 Event 객체에서 설정함
* */
public class AccountSerializer extends JsonSerializer<Account> {

    @Override
    public void serialize(Account value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeNumberField("id", value.getId());
        gen.writeEndObject();
    }
}
