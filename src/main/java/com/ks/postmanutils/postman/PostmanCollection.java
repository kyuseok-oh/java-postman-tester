package com.ks.postmanutils.postman;

import java.util.ArrayList;
import java.util.List;

import com.ks.postmanutils.postman.collection.CollectionEvent;
import com.ks.postmanutils.postman.collection.CollectionInfo;
import com.ks.postmanutils.postman.collection.CollectionItem;
import com.ks.postmanutils.postman.collection.CollectionVariable;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter @ToString @NoArgsConstructor(access = AccessLevel.PRIVATE)
@Builder @AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class PostmanCollection {
  private CollectionInfo info;
  @Builder.Default private List<CollectionItem> item = new ArrayList<>();
  @Builder.Default private List<CollectionEvent> event = new ArrayList<>();
  @Builder.Default private List<CollectionVariable> variable = new ArrayList<>();
}
