# Scripts
   
   Here, I memo scripts that I have used during development.
   
   ## Postgres
   
   ### Run Postgres Container
   
   docker
   
   |  docker option |   | 
   |---|---|
   |  -p | port  | 
   |  -e |  dockerimage内環境変数 | 
   | -d   |   image | 
   
   
   ```
   //docker run --name ndb -p 5432:5432 -e POSTGRES_PASSWORD=pass -d postgres
   $ docker run --name rest -p 5432:5432 -e POSTGRES_PASSWORD=pass -d postgres
   //実行結果NAMES「rest」コンテイナー生成された
   $ docker ps
   CONTAINER ID        IMAGE               COMMAND                  CREATED             STATUS              PORTS                    NAMES
   10114ff95faa        postgres            "docker-entrypoint.s…"   6 minutes ago       Up 6 minutes        0.0.0.0:5432->5432/tcp   rest
   
   ```
   
   
   This cmdlet will create Postgres instance so that you can connect to a database with:
   * database: postgres
   * username: postgres
   * password: pass
   * post: 5432
   
   ### Getting into the Postgres container
   
   |  docker option |   | 
   |---|---|
   | exec | exec some command | 
   |  -i |  interactive mode | 
   | - t  |   target container | 
   
   ```
   docker exec -i -t rest bash ( imagename + what kind of command) 
   $ docker exec -i -t rest bash
   $ su - postgres
   $ psql -d postgres -U postgres  (-u databaseName -U username)
   psql (11.1 (Debian 11.1-1.pgdg90+1))
   Type "help" for help.
   postgres-#
   # DB list \l  
   $postgres-# \l
                                    List of databases
      Name    |  Owner   | Encoding |  Collate   |   Ctype    |   Access privileges
   -----------+----------+----------+------------+------------+-----------------------
    postgres  | postgres | UTF8     | en_US.utf8 | en_US.utf8 |
    template0 | postgres | UTF8     | en_US.utf8 | en_US.utf8 | =c/postgres          +
              |          |          |            |            | postgres=CTc/postgres
    template1 | postgres | UTF8     | en_US.utf8 | en_US.utf8 | =c/postgres          +
              |          |          |            |            | postgres=CTc/postgres
   (3 rows)
   
   ```
   
   Then you will see the containers bash as a root user.
   
   
   ### Connect to a database
   
   ```
   psql -d postgres -U postgres
   ```
   
   ### Query Databases
   
   ```
   \l
   ```
   
   ### Query Tables
   
   ```
   \dt
   
   postgres-# \dt
            List of relations
    Schema | Name  | Type  |  Owner
   --------+-------+-------+----------
    public | event | table | postgres
   (1 row)
   ```
   
   ### Quit
   
   ```
   \q
   ```
   
   ## application.properties
   
   ### Datasource
   
   ```
   spring.datasource.username=postgres
   spring.datasource.password=pass
   spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
   spring.datasource.driver-class-name=org.postgresql.Driver
   ```
   
   ### Hibernate
   
   ```
   spring.jpa.hibernate.ddl-auto=create-drop
   spring.jpa.properties.hibernate.jdbc.lob.non_contextual_creation=true
   spring.jpa.properties.hibernate.format_sql=true
   
   logging.level.org.hibernate.SQL=DEBUG
   logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
   ```
   
   ### Test Databaseのapplication.properties設定
   >main/ resources 配下のapplication.propertiesを読み込んでからtest/resources 配下のapplication.propertiesを
   >確認するので、新たに設定した設定項目以外は同じく適用される。 
   ```
   spring.datasource.username=sa
   spring.datasource.password=
   spring.datasource.url=jdbc:h2:mem:testdb
   
   spring.datasource.driver-class-name=org.h2.Driver
   spring.datasource.hikari.jdbc-url=jdbc:h2:mem:testdb 
   spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect
   ```
   上記の内容は以下と同じ設定になる。
   ```
   spring.datasource.username=sa
   spring.datasource.password=
   spring.datasource.url=jdbc:h2:mem:testdb
   
   spring.datasource.driver-class-name=org.h2.Driver
   spring.datasource.hikari.jdbc-url=jdbc:h2:mem:testdb 
   spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect
   
   spring.jpa.hibernate.ddl-auto=create-drop
   spring.jpa.properties.hibernate.jdbc.lob.non_contextual_creation=true
   spring.jpa.properties.hibernate.format_sql=true
   
   logging.level.org.hibernate.SQL=DEBUG
   logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
   
   ```
   
   >テスト用 propertiesファイルを追加したい場合、application.propertiesと違う名前を指定して
   >ファイル生成 ： application-test.properties
   >testClass annotation追加 @ActiveProfiles("test")　<br> 
 　※この場合も追加的に設定しなかったapplication.propertiesの設定は共通設定される。
  ```
   @ActiveProfiles("test")
   public class EventControllerTests_SpringBootTest {
   ```