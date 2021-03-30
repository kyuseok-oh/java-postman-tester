package com.ks.postmanutils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.lang3.StringUtils;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ks.postmanutils.client.vo.OkHttpRequest;
import com.ks.postmanutils.exception.PostmanUtilException;
import com.ks.postmanutils.postman.collection.PostmanCollection;
import com.ks.postmanutils.postman.collection.item.PostmanCollectionItem;
import com.ks.postmanutils.postman.collection.item.PostmanCollectionItemBody;
import com.ks.postmanutils.postman.collection.item.PostmanCollectionItemHeader;
import com.ks.postmanutils.postman.collection.item.PostmanCollectionItemRequest;
import com.ks.postmanutils.postman.environment.PostmanEnvironment;
import com.ks.postmanutils.postman.environment.PostmanEnvironmentValue;
import lombok.Getter;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PostmanUtils {
  private final String postmanCollectionFilePath;
  private final String postmanEnvironmentFilePath;
  
  private static ObjectMapper objectMapper;
  private static OkHttpClient httpClient;
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
    List<PostmanCollectionItem> itemList = collection.getItem();
    if(itemList == null || itemList.isEmpty()) {
      return retList;
    }
    for(PostmanCollectionItem item : itemList) {
      this.makeRequestFromItem(retList, item);
    }
    return retList;
  }
  
  private void makeRequestFromItem(List<OkHttpRequest> retList, PostmanCollectionItem item) {
    if((item.getItem() != null) && (!item.getItem().isEmpty())) {
      List<PostmanCollectionItem> itemList = item.getItem();
      for(PostmanCollectionItem subItem : itemList) {
        this.makeRequestFromItem(retList, subItem);
      }
    }
    
    PostmanCollectionItemRequest postmanRequest = item.getRequest();
    
    if(postmanRequest == null) {
      return;
    }
    
    Request.Builder reqBuilder = new Request.Builder();
    
    // URL
    reqBuilder = reqBuilder.url(environmentStringIncluder(postmanRequest.getUrl().getRaw()));

    // Headers & mediaType
    List<PostmanCollectionItemHeader> headerList = postmanRequest.getHeader();
    MediaType mediaType = null;
    
    for(PostmanCollectionItemHeader header : headerList) {
      if(!header.getDisabled().equals(Boolean.TRUE)) {
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
    PostmanCollectionItemBody body = item.getRequest().getBody();
    if(body != null && body.getMode().equals("raw") && StringUtils.isNotBlank(body.getRaw())) {
      String rawBodyStr = environmentStringIncluder(body.getRaw());
      RequestBody reqBody = RequestBody.create(mediaType, rawBodyStr);
      reqBuilder = reqBuilder.method(postmanRequest.getMethod(), reqBody);
    } else {
      reqBuilder = reqBuilder.method(postmanRequest.getMethod(), null);
    }
    
    retList.add(OkHttpRequest.builder().request(reqBuilder.build()).build());
  }
  
  public List<PostmanUtilException> sendAllRequest(List<OkHttpRequest> reqList) {
    List<PostmanUtilException> exceptions = new ArrayList<>();
    reqList.forEach(req -> {
      try {
        Response response = PostmanUtils.getOkHttpClient().newCall(req.getRequest()).execute();
        req.setResponse(response);
      } catch (IOException e) {
        exceptions.add(new PostmanUtilException(e));
      }
    });
    return exceptions;
  }
  
  private String environmentStringIncluder(String original) {
    String ret = original;
    
    for(Entry<String, String> entry : this.environmentMap.entrySet() ){
      String key = entry.getKey();
      String matchKey = (new StringBuilder("{{")).append(key).append("}}").toString();
      ret = ret.replaceAll(matchKey, entry.getValue());
    }
    
    return ret;
  }
  
  private void importEnvironmentToMap(PostmanEnvironment env) {
    List<PostmanEnvironmentValue> values = env.getValues();
    for(PostmanEnvironmentValue value : values) {
      if(Boolean.TRUE.equals(value.getEnabled())) {
        this.environmentMap.put(value.getKey(), value.getValue());
      } else {
        // Do Nothing
      }
    }
  }
  
  private static OkHttpClient getOkHttpClient() {
    if(httpClient == null) {
      httpClient = new OkHttpClient().newBuilder().build();
    }
    return httpClient;
  }
  
  private static ObjectMapper getObjectMapper() {
    if (objectMapper == null) {
      objectMapper = new ObjectMapper();
    }
    return objectMapper;
  }
}
