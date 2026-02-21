package co.assets.manage.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;

@Slf4j
public class JsonUtil {
    private static final ObjectMapper objectMapper = ObjectMapperFactory.createObjectMapper();

    public static <T> T toObject(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public static <T> String toJson(T entity) {
        try {
            return objectMapper.writeValueAsString(entity);
        } catch (Exception e) {
            log.error("JsonUtil.toJson error:{}", e.getMessage(), e);
        }
        return "";
    }

    public static <T> List<T> json2list(String jsonArrayStr, Class<T> clazz)
            throws Exception {
        List<Map<String, Object>> list = (List<Map<String, Object>>) objectMapper.readValue(jsonArrayStr,
                new TypeReference<List<T>>() {
                });
        List<T> result = new ArrayList<>();
        for (Map<String, Object> map : list) {
            result.add(map2pojo(map, clazz));
        }
        return result;
    }
    public static <T> T map2pojo(Map map, Class<T> clazz) {
        return objectMapper.convertValue(map, clazz);
    }


    //对象转json字符串
    public static <T> String obj2String(T obj){
        if(obj == null){
            return null;
        }
        try {
            return obj instanceof String ? (String)obj :  objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.error("JsonUtil.obj2String error:{}", e.getMessage(), e);
            return null;
        }
    }
    //对象转json字符串 重载方法 格式化的jaon字符串 方便调试 实际开发取第一种方法
    public static <T> String obj2StringPretty(T obj){
        if(obj == null){
            return null;
        }
        try {
            return obj instanceof String ? (String)obj :  objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (Exception e) {
            log.error("JsonUtil.obj2StringPretty error:{}", e.getMessage(), e);
            return null;
        }
    }




    //json字符串转对象转
    public static <T> T string2Obj(String str,Class<T> clazz){
        if(!StringUtils.hasLength(str) || clazz == null){
            return null;
        }

        try {
            return clazz.equals(String.class)? (T) str : objectMapper.readValue(str,clazz);
        } catch (Exception e) {
//            log.warn("Parse String to Object error",e);
            log.error("JsonUtil string2Obj error:{}", e.getMessage(), e);
            return null;
        }
    }



    //json字符串转list<T>对象   和上面的一样
    public static <T> T string2Obj(String str,Class<?> collectionClass,Class<?>... elementClasses){
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(collectionClass,elementClasses);
        try {
            return objectMapper.readValue(str,javaType);
        } catch (Exception e) {
//            log.warn("Parse String to Object error",e);
            log.error("JsonUtil string2Obj error:{}", e.getMessage(), e);
            return null;
        }
    }

    public static <T> T json2obj(String jsonStr, Type targetType) {
        try {
            JavaType javaType = TypeFactory.defaultInstance().constructType(targetType);
            return objectMapper.readValue(jsonStr, javaType);
        } catch (Exception e) {
            throw new IllegalArgumentException("将JSON转换为对象时发生错误:" + jsonStr, e);
        }
    }

    public static <T> T toObj(String json, TypeReference<T> type) {
      try {
        return objectMapper.readValue(json, type);
      } catch (Exception e) {
        throw new IllegalArgumentException("将JSON转换为对象时发生错误:" + json, e);
      }
    }

    public static <T> Optional<T> objToPojo(Object obj, Class<T> clazz) {
        if (obj == null) {
            return Optional.empty();
        }

        try {
            T t = objectMapper.readValue(objectMapper.writeValueAsString(obj), new TypeReference<T>() {
                @Override
                public Type getType() {
                    return clazz;
                }
            });
            return Optional.of(t);
        } catch (Exception e) {
            log.error("parse object error", e);
            return Optional.empty();
        }
    }

    public static Map<String, Object> transBean2Map(Object obj) {
        return transBeanToMap(obj,false);
    }

    public static Map<String,Object> transBeanToMapIgnoreNull(Object obj){
        return transBeanToMap(obj,true);
    }

    private static Map<String,Object> transBeanToMap(Object bean,boolean ignore){
        if (bean == null) {
            return null;
        }
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();

                // 过滤class属性
                if (!key.equals("class")) {
                    // 得到property对应的getter方法
                    Method getter = property.getReadMethod();
                    Object value = getter.invoke(bean);
                    if(ignore){
                        if(!Objects.isNull(value)){
                            map.put(key, value);
                        }
                    }else{
                        map.put(key,value);
                    }

                }
            }
        } catch (Exception e) {
            System.out.println("transBean2Map Error " + e);
        }

        return map;
    }

}
