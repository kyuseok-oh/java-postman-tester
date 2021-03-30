package com.ks.postmanutils.postman.collection.item;

import java.util.ArrayList;
import java.util.List;
import com.ks.postmanutils.postman.collection.PostmanCollectionEvent;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter @ToString @NoArgsConstructor(access = AccessLevel.PRIVATE)
@Builder @AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class PostmanCollectionItem {
  private String name;
  @Builder.Default private List<PostmanCollectionItem> item = new ArrayList<>();
  private PostmanCollectionItemRequest request;
  @Builder.Default private List<PostmanCollectionItemResponse> response = new ArrayList<>();
  @Builder.Default private List<PostmanCollectionEvent> event = new ArrayList<>();
}
