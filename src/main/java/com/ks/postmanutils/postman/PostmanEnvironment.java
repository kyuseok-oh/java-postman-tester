package com.ks.postmanutils.postman;

import java.util.ArrayList;
import java.util.List;

import com.ks.postmanutils.postman.environment.EnvironmentValue;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter @ToString @NoArgsConstructor(access = AccessLevel.PRIVATE)
@Builder @AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class PostmanEnvironment {
  private String id;
  private String name;
  @Builder.Default private List<EnvironmentValue> values = new ArrayList<>();
  private String _postman_variable_scope;
  private String _postman_exported_at;
  private String _postman_exported_using;
}
