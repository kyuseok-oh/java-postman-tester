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
public final class ItemRequest {
  private String method;
  @Builder.Default private List<ItemHeader> header = new ArrayList<>();
  private ItemBody body;
  private ItemUrl url;
  private String description;
}
