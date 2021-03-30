package com.ks.postmanutils.postman.collection;

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
public final class CollectionEvent {
  private String listen;
  PostmanCollectionEventScript script;

  @Getter @ToString @NoArgsConstructor(access = AccessLevel.PRIVATE)
  @Builder @AllArgsConstructor(access = AccessLevel.PRIVATE)
  private static final class PostmanCollectionEventScript {
    private String id;
    private String type;
    @Builder.Default private List<String> exec = new ArrayList<>();
  }
}
