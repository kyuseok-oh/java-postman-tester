package com.ks.postmanutils.postman.collection.item.body;

import com.ks.postmanutils.postman.collection.item.body.options.OptionsRaw;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter @ToString @NoArgsConstructor(access = AccessLevel.PRIVATE)
@Builder @AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BodyOptions {
	private OptionsRaw raw;
}
