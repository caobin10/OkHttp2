package org.chuck.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * http://www.iteye.com/problems/95843
 * @author Chuck
 *
 */
public class JsonUtil {
//	private static DateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.CHINESE);
	private static DateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS",Locale.CHINESE);
	private static ObjectMapper mapper=new ObjectMapper();
	static{
//		mapper.enableDefaultTyping();
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
	}
	
	/**
	 * 对象序列化为JSON
	 * @param obj
	 * @param dateFormat
	 * @return
	 */
	public static <T> String toJson(T obj,DateFormat dateFormat){
		String json=null;
		try {
//			mapper.configure(SerializationFeature.FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS, false)
//					.setDateFormat(dateFormat);
			ObjectMapper copyMapper = mapper.copy();
			copyMapper.setDateFormat(dateFormat);
			json=copyMapper.writeValueAsString(obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return json;
	}
	
	public static <T> String toJson(T obj){
		return toJson(obj,dateFormat);
	}
	
	
	
	/**
	 * JSON反序列化为对象
	 * @param json
	 * @param cls
	 * @param ignoreUnknown
	 * @param dateFormat
	 * @return
	 */
	public static <T> T jsonToObj(String json,Class<T> cls,boolean ignoreUnknown,DateFormat dateFormat){		
		T model=null;
		try {
			if(!CharSeqUtil.isNullOrEmpty(json)){
				ObjectMapper copyMapper=mapper.copy();
				copyMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,!ignoreUnknown)
				.setDateFormat(dateFormat);
				model=copyMapper.readValue(json, cls);
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return model;
	}
	public static <T> T jsonToObj(String json,Class<T> cls,boolean ignoreUnknown){		
		return jsonToObj(json, cls, ignoreUnknown, dateFormat);
	}
	
	public static <T> T jsonToObj(String json,Class<T> cls){
		return jsonToObj(json, cls, true);
	}
	
	/**
	 * JSON反序列化为对象
	 * @param json
	 * @param cls
	 * @param ignoreUnknown
	 * @param dateFormat
	 * @return
	 */
	public static <T> List<T> jsonToObjs (String json,Class<T> cls,boolean ignoreUnknown,DateFormat dateFormat){
		List<T> objs=null;
		try {
			if(!CharSeqUtil.isNullOrEmpty(json)){
				ObjectMapper copyMapper=mapper.copy();
				objs = copyMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, !ignoreUnknown)
						.setDateFormat(dateFormat)
						.readValue(json, copyMapper.getTypeFactory().constructCollectionType(List.class, cls));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return objs;
	}
	
	public static <T> List<T> jsonToObjs (String json,Class<T> cls,boolean ignoreUnknown){
		return jsonToObjs(json, cls, ignoreUnknown, dateFormat);
	}
	
	public static <T> List<T> jsonToObjs (String json,Class<T> cls){
		return jsonToObjs(json, cls, false, dateFormat);
	}
	
	public static <T> Map<String, T> jsonToMap(String json,Class<T> cls){
		return jsonToMap(json, cls, dateFormat);
	}
	public static <T> Map<String, T> jsonToMap(String json,Class<T> cls,DateFormat dateFormat){
		Map<String, T> map=null;
		try {
			ObjectMapper copyMapper=mapper.copy();
			map=copyMapper.setDateFormat(dateFormat).readValue(json,
					copyMapper.getTypeFactory().constructMapType(Map.class,String.class, cls));
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return map;
	}

	public static <K,V> Map<K, V> jsonToMap(String json,Class<K> clsk,Class<V> clsv){
		return jsonToMap(json,Map.class,clsk,clsv,dateFormat);
	}
	public static <K,V> Map<K, V> jsonToMap(String json,Class<? extends Map> cls,Class<K> clsk,Class<V> clsv,DateFormat dateFormat){
		Map<K, V> map=null;
		try {
			ObjectMapper copyMapper=mapper.copy();
			map=copyMapper.setDateFormat(dateFormat).readValue(json,
					copyMapper.getTypeFactory().constructMapType(cls,clsk, clsv));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	public static <K,V> Map<K, List<V>> jsonToListValueMap(String json,Class<K> clsk,Class<V> clsv){
		return jsonToListValueMap(json, Map.class, clsk, clsv, dateFormat);
	}

	public static <K,V> Map<K, List<V>> jsonToListValueMap(String json,Class<? extends Map> cls,Class<K> clsk,Class<V> clsv,DateFormat dateFormat){
		Map<K, List<V>> map=null;
		try {
			ObjectMapper copyMapper=mapper.copy();
			map=copyMapper.setDateFormat(dateFormat).readValue(json,
					copyMapper.getTypeFactory().constructMapType(cls, copyMapper.constructType(clsk), copyMapper.getTypeFactory().constructCollectionType(List.class, clsv)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	public static <T> List<Map<String, T>> jsonToMaps(String json,Class<T> cls){
		List<Map<String, T>> maps=null;
		try {
			maps=jsonToMaps(json,Map.class,String.class,cls);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return maps;
	}

	public static <K,V> List<Map<K, V>> jsonToMaps(String json,Class<? extends Map> cls,Class<K> clsk,Class<V> clsv){
		List<Map<K,V>> maps=null;
		try {
			ObjectMapper copyMapper=mapper.copy();
			copyMapper.setDateFormat(dateFormat);
			maps=copyMapper.readValue(json, copyMapper.getTypeFactory().constructCollectionType(List.class, copyMapper.getTypeFactory().constructMapLikeType(cls,clsk,clsv)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return maps;
	}

	public static <T> Set<T> jsonToSet (String json,Class<T> cls){
		Set<T> objs=null;
		try {
			ObjectMapper copyMapper=mapper.copy();
			objs = copyMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
					.setDateFormat(dateFormat)
					.readValue(json, copyMapper.getTypeFactory().constructCollectionType(Set.class, cls));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return objs;
	}


}
