package com.ks.postmanutils.postman.collection.item;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter @ToString @NoArgsConstructor(access = AccessLevel.PRIVATE)
@Builder @AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class ItemHeader {
  private String key;
  private String value;
  private String type;
  private String name;
  private String description;
  private Boolean disabled;
}
