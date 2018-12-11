# rest-api-with-springboot
study rest api with springboot

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
