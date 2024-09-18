package wang.liangchen.matrix.framework.springboot.tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Locale;

public class JsonTest {
    public static void main(String[] args) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String s = mapper.writeValueAsString(Locale.CHINA);
        System.out.println(s);
    }
}
