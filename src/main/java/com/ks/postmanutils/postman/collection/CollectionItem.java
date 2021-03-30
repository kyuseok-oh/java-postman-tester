package com.ks.postmanutils.postman.collection;

import java.util.ArrayList;
import java.util.List;

import com.ks.postmanutils.postman.collection.item.ItemRequest;
import com.ks.postmanutils.postman.collection.item.ItemResponse;
import com.ks.postmanutils.postman.collection.item.ItemProtocolProfileBehavior;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter @ToString @NoArgsConstructor(access = AccessLevel.PRIVATE)
@Builder @AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class CollectionItem {
  private String name;
  private ItemProtocolProfileBehavior protocolProfileBehavior;
  @Builder.Default private List<CollectionItem> item = new ArrayList<>();
  private ItemRequest request;
  @Builder.Default private List<ItemResponse> response = new ArrayList<>();
  @Builder.Default private List<CollectionEvent> event = new ArrayList<>();
}
