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
public final class PostmanCollectionItemRequest {
  private String method;
  @Builder.Default private List<PostmanCollectionItemHeader> header = new ArrayList<>();
  private PostmanCollectionItemBody body;
  private PostmanCollectionItemUrl url;
  private String description;
}
