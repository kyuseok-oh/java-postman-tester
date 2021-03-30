package com.ks.postmanutils.postman.collection.item;

import java.util.Map;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter @ToString @NoArgsConstructor(access = AccessLevel.PRIVATE)
@Builder @AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemProtocolProfileBehavior {
	private Map<String, String> disabledSystemHeaders;
}
