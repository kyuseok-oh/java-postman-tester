package com.ks.postmanutils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.lang3.StringUtils;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ks.postmanutils.client.vo.OkHttpRequest;
import com.ks.postmanutils.exception.PostmanUtilException;
import com.ks.postmanutils.postman.PostmanCollection;
import com.ks.postmanutils.postman.PostmanEnvironment;
import com.ks.postmanutils.postman.collection.CollectionItem;
import com.ks.postmanutils.postman.collection.item.ItemBody;
import com.ks.postmanutils.postman.collection.item.ItemHeader;
import com.ks.postmanutils.postman.collection.item.ItemRequest;
import com.ks.postmanutils.postman.environment.EnvironmentValue;

import kotlin.Pair;
import lombok.Getter;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PostmanUtils {
  private final String postmanCollectionFilePath;
  private final String postmanEnvironmentFilePath;
  
  private static ObjectMapper objectMapper;
  private OkHttpClient httpClient;
  private Map<String, String> environmentMap;
  
  @Getter private List<OkHttpRequest> requestList;
  
  public PostmanUtils(String postmanCollectionFilePath, String postmanEnvironmentFilePath) throws PostmanUtilException {
    this.postmanCollectionFilePath  = postmanCollectionFilePath;
    this.postmanEnvironmentFilePath = postmanEnvironmentFilePath;
    this.environmentMap = new HashMap<>();
    this.requestList = new ArrayList<>();
    this.loadingEnvironmentAndCollection();
  }
  
  private void loadingEnvironmentAndCollection() throws PostmanUtilException {
    try(
        InputStream collectionJsonStream = new FileInputStream(postmanCollectionFilePath);
        InputStream environmentJsonStream = new FileInputStream(postmanEnvironmentFilePath);
        ) {
      
      PostmanCollection postmanCollection = PostmanUtils.getObjectMapper().readValue(collectionJsonStream, PostmanCollection.class);
      PostmanEnvironment postmanEnvironment = PostmanUtils.getObjectMapper().readValue(environmentJsonStream, PostmanEnvironment.class);
      
      this.importEnvironmentToMap(postmanEnvironment);
      this.requestList = makeRequestFromCollection(postmanCollection);
      
    } catch (JsonParseException | JsonMappingException e) {
      throw new PostmanUtilException("JSON 파싱 중 에러 발생", e);
    } catch (FileNotFoundException e) {
      throw new PostmanUtilException("Postman Collection JSON 파일 로딩 중 경로 에러 발생", e);
    } catch (IOException e) {
      throw new PostmanUtilException("테스트 중 IOException 발생", e);
    }
  }
  
  private List<OkHttpRequest> makeRequestFromCollection(PostmanCollection collection) {
    List<OkHttpRequest> retList = new ArrayList<>();
    List<CollectionItem> itemList = collection.getItem();
    if(itemList == null || itemList.isEmpty()) {
      return retList;
    }
    for(CollectionItem item : itemList) {
      this.makeRequestFromItem(retList, item);
    }
    return retList;
  }
  
  private void makeRequestFromItem(List<OkHttpRequest> retList, CollectionItem item) {
	List<CollectionItem> itemList = item.getItem();
	if((itemList != null) && (!itemList.isEmpty())) {
      for(CollectionItem subItem : itemList) {
        this.makeRequestFromItem(retList, subItem);
      }
    }
    
    ItemRequest postmanRequest = item.getRequest();
    
    if(postmanRequest == null) {
      return;
    }
    
    Request.Builder reqBuilder = new Request.Builder();
    
    // URL
    reqBuilder = reqBuilder.url(environmentStringIncluder(postmanRequest.getUrl().getRaw()));

    // Headers & mediaType
    List<ItemHeader> headerList = postmanRequest.getHeader();
    MediaType mediaType = null;
    
    for(ItemHeader header : headerList) {
      if(!Boolean.TRUE.equals(header.getDisabled())) {
        String key = environmentStringIncluder(header.getKey());
        String value = environmentStringIncluder(header.getValue());
        if(key.equalsIgnoreCase("Content-Type")) {
          mediaType = MediaType.parse(value);
        }
        reqBuilder = reqBuilder.addHeader(key, value);
      }
    }
    
    if (mediaType == null) {
      mediaType = MediaType.get("text/plain");
    }
    
    // Body & Method
    ItemBody body = item.getRequest().getBody();
    if(body != null && body.getMode().equals("raw") && StringUtils.isNotBlank(body.getRaw())) {
      String rawBodyStr = environmentStringIncluder(body.getRaw());
      RequestBody reqBody = RequestBody.create(mediaType, rawBodyStr);
      reqBuilder = reqBuilder.method(postmanRequest.getMethod(), reqBody);
    } else {
      reqBuilder = reqBuilder.method(postmanRequest.getMethod(), null);
    }
    
    retList.add(OkHttpRequest.builder().request(reqBuilder.build()).build());
  }
  
  public List<PostmanUtilException> sendAllRequestIgnoreExcpetions(List<OkHttpRequest> reqList) {
    List<PostmanUtilException> exceptions = new ArrayList<>();
    reqList.forEach(req -> {
      try {
    	sendRequest(req);
      } catch (PostmanUtilException e) {
        exceptions.add(new PostmanUtilException(e));
      }
    });
    return exceptions;
  }
  
  public void sendAllRequest(List<OkHttpRequest> reqList) throws PostmanUtilException {
	for(OkHttpRequest req : reqList) {
	  sendRequest(req);
	}
  }
  
  public void sendRequest(OkHttpRequest request) throws PostmanUtilException {
	try {
	  Response response = this.getOkHttpClient().newCall(request.getRequest()).execute();
	  request.setResponse(response);
	} catch (IOException e) {
	  throw new PostmanUtilException("Error occurred during request.", e);
	}
  }
  
  private String environmentStringIncluder(String original) {
    String ret = original;
    
    for(Entry<String, String> entry : this.environmentMap.entrySet() ){
      String key = entry.getKey();
      String matchKey = (new StringBuilder("\\{\\{")).append(key).append("\\}\\}").toString();
      ret = ret.replaceAll(matchKey, entry.getValue());
    }
    
    return ret;
  }
  
  private void importEnvironmentToMap(PostmanEnvironment env) {
    List<EnvironmentValue> values = env.getValues();
    for(EnvironmentValue value : values) {
      if(Boolean.TRUE.equals(value.getEnabled())) {
        this.environmentMap.put(value.getKey(), value.getValue());
      } else {
        // Do Nothing
      }
    }
  }
  
  private OkHttpClient getOkHttpClient() {
    if(httpClient == null) {
      CookieJar cookieJar = new CookieJar() {
    	Map<String, List<Cookie>> cookieMap = new HashMap<>();
		
    	@Override
		public List<Cookie> loadForRequest(HttpUrl arg0) {
    		List<Cookie> cookieList;
    		if(cookieMap.containsKey(arg0.host())) {
    			cookieList = cookieMap.get(arg0.host());
    		} else {
    			cookieList = new ArrayList<>();
    			cookieMap.put(arg0.host(), cookieList);
    		}
            return cookieList;
		}

		@Override
		public void saveFromResponse(HttpUrl arg0, List<Cookie> arg1) {
			List<Cookie> cookieList;
			if(cookieMap.containsKey(arg0.host())) {
				cookieList = cookieMap.get(arg0.host());
				arg1.forEach(newCookie -> {
					List<Cookie> cookiesForRemove = new ArrayList<>();
					cookieList.forEach(oldCookie -> {
						if(newCookie.name().equals(oldCookie.name())) {
							cookiesForRemove.add(oldCookie);
						}
					});
					cookieList.removeAll(cookiesForRemove);
				});
				cookieList.addAll(arg1);
    		} else {
    			cookieMap.put(arg0.host(), arg1);
    		}
		}
      };
      httpClient = new OkHttpClient().newBuilder().cookieJar(cookieJar).build();
    }
    
    return httpClient;
  }
  
  public void clearCookies(HttpUrl arg0) {
	  this.getOkHttpClient().cookieJar().loadForRequest(arg0).clear();
  }
  
  private static ObjectMapper getObjectMapper() {
    if (objectMapper == null) {
      objectMapper = new ObjectMapper();
    }
    return objectMapper;
  }
}
