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
public final class ItemResponse {
  private String name;
  private ItemRequest originalRequest;
  private String status;
  private Integer code;
  private String _postman_previewlanguage;
  @Builder.Default private List<ItemHeader> header = new ArrayList<>();
  @Builder.Default private List<ItemCookie> cookie = new ArrayList<>();
  private String body;
}
