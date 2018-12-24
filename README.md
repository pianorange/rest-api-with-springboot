# rest-api-with-springboot
study rest api with springboot

Springで提供されている色んな機能を使ってSelf-Describtive MessageとHATEOAS(Hypermedia as the engine of application state)を
満たせるRESTAPIを作る。 

- Spring Boot 
- Spring Data JPA 
- Spring HATEOAS 
- Spring REST Docs 
- Spring Security OAuth




###### REST 構成
_ _ _


<b> 資源(RESOURCE) - URI </b><br>
<b> 行為(Verb) - HTTP METHOD </b><br>
<b> 表現(Representations) </b><br>

###### REST の特徴
_ _ _

1. Uniform (uniform interface)
Uniform InterfaceはURIに指定したリソースに対した操作を統一された限定的なインターフェースで実行するアーキテクチャスタイルを指す。

2. Stateless
RESTは状態を持たない。つまり、作業のための状態情報を設定して管理しない。 Session情報またはCookie情報を別途に設定して管理しないためAPIサーバは外から受け入れたRequestだけを処理すればいい。そのため、サービスの自由度が高まってサーバで不要な情報を管理しないことによって具現が単純になる。

3. Cacheable
RESTの主な特徴はHTTPをそのまま使うため、ウェブで使われる既存のインフラがそのまま活用できる。それによってHTTPがもったCaching機能が適用できる。HTTPプロトコルで使われる [Last-Modified](https://developer.mozilla.org/ja/docs/Web/HTTP/Headers/Last-Modified)タグまたは[E-Tag](https://developers.google.com/web/fundamentals/performance/optimizing-content-efficiency/http-caching?hl=ko)を使うことでCachingができる。

4. Self-descriptiveness
REST API メッセージだけを見ても理解できる。

5. Client - Server 構造
REST サーバは API 提供、クライアントはユーザー認証やコンテキスト(Session, Login情報)などを直接管理する仕組みでそれぞれの役割が明らかに区分されるためクライアントとサーバで開発すべき内容がより明確になってお互いの依存性が減る。

6. 階層型構造
REST サーバは多重階層で構成されることができてセキュリティ、ロードバランシング、暗号化階層を追加し構造を柔軟に管理することができるとともにPROXY、GateWayみたいなネットワーク基盤の中間媒体が使えるようにします。

###### REST API デザインガイド
___

<b> REST API 設計ポイント二つ </b><br>

１．URIは情報のリソースを表現するべき <br>
２．リソースに対した行為は HTTP Method(GET, POST, PUT, DELETE)で表現する。　<br>


<b> REST API設計規則 </b>
___
1) URIは情報のリソースを表現するべき。（リソース名は動詞より名詞を使う）
    GET /members/delete/1
上記のような方式はRESTらしくない URI。 URIはリソースを表現するべきなのでdeleteみたいな行為に関した表現が含まれてはいけない。

2) リソースに関した行為は HTTP Method(GET, POST, PUT, DELETE など)で表現
上記のURIをHTTP Methodを使って修正すると下記のようになる。

    DELETE /members/1

会員情報を持ってくるときには GET, 会員を追加する行為を表現したい場合は、POST METHODを使って表現する。

会員情報を持ってくる URI

    GET /members/show/1     (x)
    GET /members/1          (o)

会員を追加する時

    GET /members/insert/2 (x)  - GET メソッドはリソース生成に使わない。
    POST /members/2       (o)

<b> [参考]HTTP METHODの役割</b>
___
POST, GET, PUT, DELETE この四つのMethodを持ってCRUDが表現できる。

- POST	: POSTを通じて該当URIにRequestするとリソースを生成する。
- GET	: GETを通じて該当リソースを参照する。
- PUT	: PUTを通じて該当リソースを修正する。
- DELETE: 	DELETEを通じて該当リソースを削除する。


##### HATEOAS principle

> Hypermedia As The Engine Of Application State (HATEOAS) is a component of the REST application architecture that distinguishes it 
> from other network application architectures.
> https://en.wikipedia.org/wiki/HATEOAS

- 링크 만드는 기능 
    - 문자열 가지고 만들기 
    - 컨트롤러와 메소드로 만들기  <br>
- 리소스 만드는 기능
    - 리소스: 데이터 + 링크 <br>
- 링크 찾아주는 기능 
    - Traverson
    - LinkDiscoverers 
- 링크 
    - HREF 
    - REL 
        - self 자기 자신에 대한 링크를 걸어 줄 때 
        - profile 이 글의 본문에 대한 링크를 걸어줄 때 


本アプリではSpring HATEOAS を使ってHATEOASを実現させる。


HATEOAS는 RESTful API를 사용하는 클라이언트가 전적으로 서버에 의해 동적으로 상호작용을 할 수 있다. 
쉽게 말하면 클라이언트가 서버에 요청시 서버는 요청에 의존되는 URI를 Response에 포함시켜 반환한다. 

  예를들면 사용자정보를 입력(POST)하는 요청 후 사용자를 조회(GET), 수정(PUT), 삭제(DELETE)할 수 있는 URI를 동적으로 알려주게 되는 것이다. 이렇게 동적으로 모든 요청에 의존되는 URI 정보를 보여준다면 아래와 같은 장점이 있을 것이다.

요청 URI정보가 변경되어도 클라이언트에서 동적으로 생성된 URI를 사용한다면, 클라이언트 입장에서는 URI 수정에 따른 코드 변경이 불필요하다.
URI정보를 통해 의존되는 요청을 예측가능하게 한다. 
기본 URI정보가 아니라 resource까지 포함된 URI를 보여주기 때문에 resource에 대한 확신을 갖게된다.
REST 클라이언트는 간단한 고정 URL을 통해 REST 응용 프로그램에 들어갑니다 . 클라이언트가 취할 모든 향후 조치 는 서버에서 리턴 된 자원 표현 내에서 발견됩니다 . 

예를 들어, [2] 이 GET 요청은 XML 표현으로 세부 사항을 요청하는 계정 자원을 가져옵니다.

```language
GET  / accounts / 12345  HTTP / 1.1 
HOST :  bank.example.com 
Accept :  application / xml 
...
```

응답은 다음과 같습니다.
```language
HTTP / 1.1  200  OK 
Content-Type :  application / xml 
Content-Length :  ...

<? xml version = "1.0"?> 
<account> 
    <account_number> 12345 </ account_number> 
    <balance  currency = "usd" > 100.00 </ balance> 
    <link  rel = "deposit"  href = "https : // bank.  href = "https://bank.example.com/accounts/12345/deposit " /> 
    <link  rel = "withdraw"  href = "https://bank.example.com/accounts/12345/withdraw"  />  
    <link  rel = "transfer"  href = " https://bank.example.com/accounts/12345/transfer "  /> 
    <link  rel = "close"  href = "https://bank.example.com/accounts/12345/status "  />
</ account>

```

응답에는 가능한 예금, 인출 또는 이체 또는 계정 폐쇄와 같은 가능한 후속 링크가 포함됩니다.
계정 정보가 나중에 검색되면 계정이 초과 저장됩니다.
```language
HTTP / 1.1  200  OK 
Content-Type: application/xml
Content-Length: ...

<? xml version = "1.0"?> 
<account> 
    <account_number> 12345 </ account_number> 
    <balance  currency = "usd" > -25.00 </ balance> 
    <link  rel = "deposit"  href = "https : // bank .example.com / accounts / 12345 / deposit "  /> 
</ account>
```

이제는 하나의 링크 만 사용할 수 있습니다. 더 많은 돈을 입금하십시오. 현재 상태 에서는 다른 링크를 사용할 수 없습니다. 따라서 응용 프로그램 상태의 엔진 이라는 용어 . 가능한 조치는 자원 상태가 다양 할 때마다 다릅니다.
```language
HTTP / 1.1  200  OK 
Content-Type: application/xml
Content-Length: ...

<? xml version = "1.0"?> 
<account> 
    <account_number> 12345 </ account_number> 
    <balance  currency = "usd" > -25.00 </ balance> 
    <link  rel = "deposit"  href = "https : // bank .example.com / accounts / 12345 / deposit "  /> 
</ account>


```
 
 @JsonUnwrapped 使用例
```language
public class EventResource extends ResourceSupport {

    private Event event;
    
    public EventResource(Event evnet) {
        this.event = evnet;
    }

    public Event getEvent() {
        return event;
    }
```
今の状態ではEventResourceクラス内でEventオブジェクト、Linksオブジェクトが分けられて表現される。
```language
   Body = {
        "event": {
            "id": 1,
                    "name": "Spring",
                    "description": "REST API Development",
                    "beginEnrollmentDateTime": "2018-11-10T12:10:00",
                    "closeEnrollmentDateTime": "2018-11-10T14:10:00",
                    "beginEventDateTime": "2018-11-11T12:10:00",
                    "endEventDateTime": "2018-11-11T12:10:00",
                    "location": "tokyo sibuya",
                    "basePrice": 10,
                    "maxPrice": 200,
                    "limitOfEnrollment": 100,
                    "offline": true,
                    "free": false,
                    "eventStatus": "DRAFT"
        },
        "_links": {
            "query-events": {
                "href": "http://localhost/api/events"
            },
            "self": {
                "href": "http://localhost/api/events/1"
            },
            "update-event": {
                "href": "http://localhost/api/events/1"
            }
        }
    }
```
 @JsonUnwrappedを使うと
```language
public class EventResource extends ResourceSupport {
    @JsonUnwrapped
    private Event event;
    
    public EventResource(Event evnet) {
        this.event = evnet;
    }

    public Event getEvent() {
        return event;
    }
```
下記のように、JsonSerializerによってEventBean単位でパーシングされずに＜br＞
EventBeanの変数を取り出してパーシングする。
```language
Body = {
    "id": 1,
    "name": "Spring",
    "description": "REST API Development",
    "beginEnrollmentDateTime": "2018-11-10T12:10:00",
    "closeEnrollmentDateTime": "2018-11-10T14:10:00",
    "beginEventDateTime": "2018-11-11T12:10:00",
    "endEventDateTime": "2018-11-11T12:10:00",
    "location": "tokyo sibuya",
    "basePrice": 10,
    "maxPrice": 200,
    "limitOfEnrollment": 100,
    "offline": true,
    "free": false,
    "eventStatus": "DRAFT",
    "_links": {
        "query-events": {
            "href": "http://localhost/api/events"
        },
        "self": {
            "href": "http://localhost/api/events/1"
        },
        "update-event": {
            "href": "http://localhost/api/events/1"
        }
    }
}
```

※　extends Resource<Event> を使って実装しても同じ結果になる
Resourceの中でパラメータでもらったcontentに@JsonUnwrapped使っているため
```language
public class EventResource extends Resource<Event> {
    
    public EventResource(Event content, Link... links) {
        super(content, links);
    }
}
```
###### spring Docs
SpringRESTDocs swagger 와 차이점

로직이 바뀌어서 태스트코드가 바뀌면
문서도 자동으로 함께 바뀌게 설정 가능. 

```language
[source,http,options="nowrap"]
----
HTTP/1.1 201 Created
Location: http://localhost:8080/api/events/1
Content-Length: 603
Content-Type: application/hal+json;charset=UTF-8

{"id":1,"name":"Spring","description":"REST API Development","beginEnrollmentDateTime":"2018-11-10T12:10:00","closeEnrollmentDateTime":"2018-11-10T14:10:00","beginEventDateTime":"2018-11-11T12:10:00","endEventDateTime":"2018-11-11T12:10:00","location":"tokyo sibuya","basePrice":10,"maxPrice":200,"limitOfEnrollment":100,"offline":true,"free":false,"eventStatus":"DRAFT","_links":{"self":[{"href":"http://localhost:8080/api/events/1"},{"href":"http://localhost:8080/api/events/1"}],"query-events":{"href":"http://localhost:8080/api/events"},"update-event":{"href":"http://localhost:8080/api/events/1"}}}
----

```

커스터마이징 컨피그 클래스 만들어서 RestDocs의 문서를 포맷팅할수있다.
RestDocsMockMvcConfigurationCustomizer 

```language
[source,http,options="nowrap"]
----
HTTP/1.1 201 Created
Content-Length: 790
Location: http://localhost:8080/api/events/1
Content-Type: application/hal+json;charset=UTF-8

{
  "id" : 1,
  "name" : "Spring",
  "description" : "REST API Development",
  "beginEnrollmentDateTime" : "2018-11-10T12:10:00",
  "closeEnrollmentDateTime" : "2018-11-10T14:10:00",
  "beginEventDateTime" : "2018-11-11T12:10:00",
  "endEventDateTime" : "2018-11-11T12:10:00",
  "location" : "tokyo sibuya",
  "basePrice" : 10,
  "maxPrice" : 200,
  "limitOfEnrollment" : 100,
  "offline" : true,
  "free" : false,
  "eventStatus" : "DRAFT",
  "_links" : {
    "self" : [ {
      "href" : "http://localhost:8080/api/events/1"
    }, {
      "href" : "http://localhost:8080/api/events/1"
    } ],
    "query-events" : {
      "href" : "http://localhost:8080/api/events"
    },
    "update-event" : {
      "href" : "http://localhost:8080/api/events/1"
    }
  }
}
----

```


테스트할것
- API문서 만들기
  -요청 본문 문서화 requestFields() + fieldWithPath() 
  -응답 본문 문서화responseFields() + fieldWithPath()
  -링크 문서화links() + linkWithRel()
      -self
      -query-events
      -update-event
      -profile 링크 추가
      
  -요청 헤더 문서화requestHeaders() + headerWithName() 
  -요청 필드 문서화
  -응답 헤더 문서화responseHedaers() + headerWithName()
  -응답 필드 문서화 
  
```language
    //in this case not include _links{ ] So Caused SnippetException

       //org.springframework.restdocs.snippet.SnippetException: The following parts of the payload were not documented:

       //resresponseFields() <- must include all response field

       relaxedResponseFields(

               fieldWithPath("id").description("id of event"),

               fieldWithPath("name").description("name of new event"),

               fieldWithPath("description").description("description of new event"),

               fieldWithPath("beginEnrollmentDateTime").description("date time of begin enrollment"),

               fieldWithPath("closeEnrollmentDateTime").description("date time of close enrollment"),

               fieldWithPath("beginEventDateTime").description("date time of begin event"),

               fieldWithPath("endEventDateTime").description("date time of end event"),
	
)
```
   					
Relaxed 접두어 
장점: 문서 일부분만 테스트 할 수 있다. 
단점: 정확한 문서를 생성하지 못한다
⇒APIの実装が変更された時にCodeの変化に合わせてRESTDOCKSにも反映されるので
使わないですべてのFieldを記述したほうがいい

```language
	<!--asciidoctor 使ってHTML生成-->
			<!--process-asciidoc設定はデフォルトでsrc/main/asciidoc 配下のすべての文書をHTML変換-->
			<!--格納先　target/generated-docs/index.html-->
			<plugin>
				<groupId>org.asciidoctor</groupId>
				<artifactId>asciidoctor-maven-plugin</artifactId>
				<version>1.5.3</version>
				<executions>
					<execution>
						<id>generate-docs</id>
						<phase>prepare-package</phase>
						<goals>
							<goal>process-asciidoc</goal>
						</goals>
						<configuration>
							<backend>html</backend>
							<doctype>book</doctype>
						</configuration>
					</execution>
				</executions>
				<dependencies>
					<dependency>
						<groupId>org.springframework.restdocs</groupId>
						<artifactId>spring-restdocs-asciidoctor</artifactId>
						<version>2.0.2.RELEASE</version>
					</dependency>
				</dependencies>
			</plugin>
			<!--copy-resources設定　target/generated-docs/index.htmlに生成されたResourceをtarge/static配下に格納してくれる-->
			<!--asciidoctorと同じ　<phase>prepare-package</phase>　なので順序が重要　生成して、コピーする流れで設定した格納先にコピーする-->
			<plugin>
				<artifactId>maven-resources-plugin</artifactId>
				<version>2.7</version>
				<executions>
					<execution>
						<id>copy-resources</id>
						<phase>prepare-package</phase>
						<goals>
							<goal>copy-resources</goal>
						</goals>
						<configuration>
							<outputDirectory>
								${project.build.outputDirectory}/static/docs
							</outputDirectory>
							<resources>
								<resource>
									<directory>
										${project.build.directory}/generated-docs
									</directory>
								</resource>
							</resources>
						</configuration>
					</execution>
				</executions>
			</plugin>

```
			
 asciidoc 追加後、
 
mvn package 
-> target/generated-docs/index.html
生成されたことを確認して、ファイル情報のPathをコピーし、ブラウザで
開いてみるとRESTDOCK文書が生成されていることが確認できる。

※このように生成されたRESTAPIDocksをResponseに含めるとRestfulのSelfdescriptive原則を
満たせることができる。

        eventResource.add(new Link("http://localhost:8080/docs/index.html#resources-events-create").withRel("profile"));

SpringBoot起動させると、targe/ static/docs配下に格納されているので
以下のパスで確認できる。 
http://localhost:8080/docs/index.html

※ここで言っているstaticフォルダはresoucese配下ではなくてビルドされたディレクトリの配下である


###### docker command 

```

$ docker --version
Docker version 18.03.0-ce, build 0520e24302

$ docker version
Client:
 Version:       18.03.0-ce
 API version:   1.37
 Go version:    go1.9.4
 Git commit:    0520e24302
 Built: Fri Mar 23 08:31:36 2018
 OS/Arch:       windows/amd64
 Experimental:  false
 Orchestrator:  swarm

Server: Docker Engine - Community
 Engine:
  Version:      18.09.0
  API version:  1.39 (minimum version 1.12)
  Go version:   go1.10.4
  Git commit:   4d60db4
  Built:        Wed Nov  7 00:52:55 2018
  OS/Arch:      linux/amd64
  Experimental: false

$ docker info
Containers: 0
 Running: 0
 Paused: 0
 Stopped: 0
Images: 0
Server Version: 18.09.0

$ docker run hello-world
Unable to find image 'hello-world:latest' locally
latest: Pulling from library/hello-world
d1725b59e92d: Pull complete
Digest: sha256:0add3ace90ecb4adbf7777e9aacf18357296e799f81cabc9fde470971e499788
Status: Downloaded newer image for hello-world:latest

$ docker image ls
REPOSITORY          TAG                 IMAGE ID            CREATED             SIZE
hello-world         latest              4ab4c602aa5e        3 months ago        1.84kB

//List the hello-world container (spawned by the image) which exits after displaying its message. If it were still running, you would //not need the --all option:
$ docker image ls --all
REPOSITORY          TAG                 IMAGE ID            CREATED             SIZE
hello-world         latest              4ab4c602aa5e        3 months ago        1.84kB

$ docker ps
CONTAINER ID        IMAGE               COMMAND             CREATED             STATUS              PORTS               NAMES


```


#### JEP 286: Local-Variable Type Inference
Java 10의 변화 중 코드 측면에서 가장 흥미로운 점을 하나 고르라면 당연 Local Variable Type Inference 입니다. 로컬변수 선언을 var 를 이용하여 기존의 엄격한 타입 선언 방식에서 탈피하여 컴파일러에게 타입을 추론하게할 수 있습니다. 기존에 lombok에서 제공하는 val/var 기능을 사용하고 계셨다면 크게 생소하진 않을 내용입니다.
```
var list = new ArrayList<String>();  // infers ArrayList<String>
var stream = list.stream();          // infers Stream<String>
```
Local Variable Type Inference는 다음과 같은 상황에서만 사용할 수 있습니다.

> - 초기화된 로컬 변수 선언 시 <br>
> - 반복문에서 지역변수 선언 시 (enhanced for loop 포함)

---

便利なツール <br>
JsonParser
http://jsonparseronline.com/

参考
https://docs.docker.com/v18.03/get-started/part2/#dockerfile
