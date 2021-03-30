package com.ks.postmanutils.postman.collection.item;

import com.ks.postmanutils.postman.collection.item.body.BodyOptions;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter @ToString @NoArgsConstructor(access = AccessLevel.PRIVATE)
@Builder @AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class ItemBody {
  private String mode;
  private String raw;
  private BodyOptions options;
}
