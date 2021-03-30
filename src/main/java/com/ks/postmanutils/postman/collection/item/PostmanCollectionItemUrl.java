package com.ks.postmanutils.postman.collection.item;

import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter @ToString @NoArgsConstructor(access = AccessLevel.PRIVATE)
@Builder @AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class PostmanCollectionItemUrl {
  private String raw;
  private String protocol;
  @Builder.Default private List<String> host = new ArrayList<>();
  private String port;
  @Builder.Default private List<String> path = new ArrayList<>();
  @Builder.Default private List<PostmanCollectionItemUrlQuery> query = new ArrayList<>();
  
  @Getter @ToString @NoArgsConstructor(access = AccessLevel.PRIVATE)
  @Builder @AllArgsConstructor(access = AccessLevel.PRIVATE)
  public static final class PostmanCollectionItemUrlQuery {
    private String key;
    private String value;
  }
}