# java-postman-tester

## 1. What is java-postman-tester

***java-postman-tester*** is a library utility that makes it easy to write and test HTTP APIs through collection and environment files created with Postman.  

## 2. Features

***java-postman-tester*** has the following features:

* Converts collection and environment files created with Postman into Java objects.
* Send requests and receive responses for APIs of a specific or all Postman collection through the OkHttp library.
* As a utility class that is not static, it is possible to manage cookies that are not shared for each PostmanUtils class object.

## 3. Example

The code below is an example of setting up a dependency using maven.

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <groupId>com.ks</groupId>
  <artifactId>postman</artifactId>
  <version>0.0.1-SNAPSHOT</version>
  
  <dependencies>
      <dependency>
		  <groupId>com.ks</groupId>
		  <artifactId>postman-utils</artifactId>
		  <version>0.0.1-SNAPSHOT</version>
	  </dependency>
  </dependencies>
	
</project>
```

The example code below is a code that calls all APIs within the Postman Collection and Environment in three different ways.

```java
package postman;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import com.ks.postmanutils.PostmanUtils;
import com.ks.postmanutils.client.vo.OkHttpRequest;
import com.ks.postmanutils.exception.PostmanUtilException;

import okio.Buffer;

public class PostmanApiRunner {

	public static void main(String[] args) {
		// The three methods called below perform the same behavior.
		handleAllRequest();
		handleAllRequestIgnoreExcpetions();
		handleEachRequest();
	}
	
	/**
	 * Example method code that collectively executes all requests in the collection.
	 */
	public static void handleAllRequest() {
		String postmanCollectionFilePath = "C://example/example.postman_collection.json";
		String postmanEnvironmentFilePath = "C://example/example.postman_environment.json";
		
		try {
			PostmanUtils postmanUtils = new PostmanUtils(postmanCollectionFilePath, postmanEnvironmentFilePath);
			List<OkHttpRequest> reqList = postmanUtils.getRequestList();
			postmanUtils.sendAllRequest(reqList);
		} catch (PostmanUtilException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Example method code that collectively executes all requests in a collection without interruption due to exceptions while receiving exceptions during request as a single list.
	 */
	public static void handleAllRequestIgnoreExcpetions() {
		String postmanCollectionFilePath = "C://example/example.postman_collection.json";
		String postmanEnvironmentFilePath = "C://example/example.postman_environment.json";
		
		try {
			PostmanUtils postmanUtils = new PostmanUtils(postmanCollectionFilePath, postmanEnvironmentFilePath);
			List<OkHttpRequest> reqList = postmanUtils.getRequestList();
			postmanUtils.sendAllRequest(reqList);
		} catch (PostmanUtilException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Example method code that controls request and response respectively.
	 */
	public static void handleEachRequest() {
		String postmanCollectionFilePath = "C://example/example.postman_collection.json";
		String postmanEnvironmentFilePath = "C://example/example.postman_environment.json";
		String outputFile = "C://example/exampleOutput.txt";
		try (PrintWriter fout = new PrintWriter(new FileOutputStream(outputFile))) {
			PostmanUtils postmanUtils = new PostmanUtils(postmanCollectionFilePath, postmanEnvironmentFilePath);
			List<OkHttpRequest> reqList = postmanUtils.getRequestList();
			reqList.forEach(req ->{
				try {
					System.out.println(req.getRequest().toString());
					fout.println(req.getRequest().toString());
					
					req.getRequest().headers().forEach(h -> {
						System.out.println("Header : " + h.toString());
						fout.println("Header : " + h.toString());
					});
					Buffer buf = new Buffer();
					req.getRequest().body().writeTo(buf);
					String reqBody = "Body : " + buf.readUtf8();
					System.out.println(reqBody);
					fout.println(reqBody);
					postmanUtils.sendRequest(req);
					
					String res = req.getResponse().toString();
					String resBody = "Body : " + req.getResponse().body().string();
					System.out.println(res);
					System.out.println(resBody);
					System.out.println("==========================================================================================");
					fout.println(res);
					fout.println(resBody);
					fout.println("==========================================================================================");
				} catch (PostmanUtilException | IOException e) {
					e.printStackTrace();
				}
			});
		} catch (PostmanUtilException | FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}
```