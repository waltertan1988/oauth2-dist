package org.walter.oauth2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.*;

@Slf4j
public class SerializerUtil {

    private static ObjectMapper objectMapper = new ObjectMapper();

    private static final String JDK_SERIALIZER_CHARSET = "ISO-8859-1";

    public static String toJson(Object object){

        if(object == null){
            return null;
        }

        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("", e);
            return null;
        }
    }

    public static <T> T fromJson(String json, Class<T> clz){

        if(null == json || clz == null){
            return null;
        }

        try {
            return objectMapper.readValue(json, clz);
        } catch (IOException e) {
            log.error("", e);
            return null;
        }
    }

    public static String serialize(Object object){
        try(ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(byteArrayOutputStream))
        {
            out.writeObject(object);
            return byteArrayOutputStream.toString(JDK_SERIALIZER_CHARSET);
        }catch (Exception e){
            return null;
        }
    }

    public static <T> T deserialize(String objectStr, Class<T> clz){
        try(ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(objectStr.getBytes(JDK_SERIALIZER_CHARSET));
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)){
            return clz.cast(objectInputStream.readObject());
        }catch (Exception e){
            return null;
        }
    }
}
