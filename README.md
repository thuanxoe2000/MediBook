**1. プロジェクト紹介**  
 このプロジェクトは、医療予約管理システム「MediBook」のバックエンド部分を担当しています。ユーザーは病院の検索、診療科や医師の選択、予約の作成・確認などを行うことができます。  
  
**2. 使用技術スタック**  
 -Java 21  
 -Spring Boot 3  
 -Spring Security (JWT)  
 -Spring Data JPA  
 -MySQL 8  
 -Lombok  
 -Maven  
 -RESTful API設計  
  
**3. セットアップ手順**  
 -リポジトリをクローン：git clone https://github.com/thuanxoe2000/MediBook.git  
 -MySQLでmedibookデータベースを作成: CREATE DATABASE medibook;  
 -application.propertiesを設定（例）:  
   spring.datasource.url=jdbc:mysql://localhost:3306/medibook  
   spring.datasource.username=root  
   spring.datasource.password=yourpassword  
   spring.jpa.hibernate.ddl-auto=update  
   spring.jpa.show-sql=true  
  
**4. ディレクトリ構成（全体）**
<pre>
    └───MediBook  
        ├───admin  
        ├───annotation  
        ├───configuration  
        ├───controller  
        ├───dto  
        │   ├───request  
        │   └───response  
        ├───entity  
        ├───enums  
        ├───exception  
        ├───mapper  
        ├───repository  
        ├───security  
        ├───service  
        └───util  
  </pre>
  
**5. 主なAPI一覧（一部）**  
認証系  
※ ユーザー登録後、システムから確認用リンクが登録メールアドレスに送信されます。リンクをクリックしてメール認証を完了しない限り、ログインはできません。  
 -POST /auth/signup ユーザー登録  
 -POST /api/login ログイン  
病院検索・予約  
 -GET /hospital/list 病院一覧取得  
 -GET /hospital/hospital-detail?id=1 病院詳細  
 -GET /timeslots/{id}/get-slots?date=2025-01-01&id=1 利用可能な時間帯取得  
 -POST /timeslots/book 予約作成  
  
**6. サンプルリクエストデータ**  
以下は API をテストする際に使用可能なサンプルリクエストです：  
 -signup.json:
<pre>
  {  
   "password": "12345678",  
   "confirmPassword": "12345678",  
   "name": "Nguyen Van A",  
   "email": "a@example.com",  
   "phoneNumber": "0123456789",  
   "gender": "MALE",  
   "address": "Hanoi"    
  }  
  </pre>
  
 -login.json:  
 <pre>
  {  
   "email": "a@example.com",  
   "password": "12345678"  
  }  
 </pre>
  
 -book-timeslot.json:  
 <pre>
  {  
   "dpmId": 1  
   "timeSlotId": 2,  
   "note": "高血圧の診断希望",  
  }
 </pre>
  
**7. 作者**  

 名前: NGUYEN VAN THUAN  
 Email: tuthanthuan123@gmail.com  
 GitHub: https://github.com/thuanxoe2000  
