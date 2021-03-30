package com.ks.postmanutils.postman.collection;

import java.util.ArrayList;
import java.util.List;
import com.ks.postmanutils.postman.collection.item.PostmanCollectionItem;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter @ToString @NoArgsConstructor(access = AccessLevel.PRIVATE)
@Builder @AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class PostmanCollection {
  private PostmanCollectionInfo info;
  @Builder.Default private List<PostmanCollectionItem> item = new ArrayList<>();
  @Builder.Default private List<PostmanCollectionEvent> event = new ArrayList<>();
  @Builder.Default private List<PostmanCollectionVariable> variable = new ArrayList<>();
}
