# Huong Dan Chay Backend

File nay la checklist ngan de chay lai backend InstaGallery tren may hien tai.

## 1. Dieu kien can co

- Docker Desktop dang mo
- JDK 17 da cai
- Dang o thu muc goc project: `D:\InstaGallery\instagallery-backend`

## 2. Chay backend moi lan bang Docker

Mo terminal trong VS Code va chay:

```powershell
docker compose up --build -d
```

Lenh nay se chay ca 3 service:

- Backend Ktor: `http://localhost:8080`
- MySQL: `localhost:3407`
- Redis: `localhost:6379`

Sau khi da chay lenh nay it nhat mot lan, cac container co `restart: unless-stopped`.
Lan sau chi can bat Docker Desktop, stack se tu start lai.

Neu chi sua code backend va muon build lai image, van dung chinh lenh tren.

## 3. Cac port dang dung

- Backend: `http://localhost:8080`
- MySQL Docker cua project: `localhost:3407`
- Redis Docker cua project: `localhost:6379`

## 4. Login tren Postman

Endpoint:

```text
POST http://localhost:8080/api/v1/auth/login
```

Body JSON:

```json
{
  "usernameOrEmail": "ngocpb04@gmail.com",
  "password": "ngoc123!"
}
```

Luu y:

- Route hien tai thuc te dang tim theo email.
- Neu bao `USER_NOT_FOUND` thi tai khoan chua ton tai trong database.

## 5. Neu chua co tai khoan thi register truoc

Endpoint:

```text
POST http://localhost:8080/api/v1/auth/register
```

Body JSON:

```json
{
  "email": "ngocpb04@gmail.com",
  "username": "ngocpham",
  "password": "ngoc123!",
  "fullName": "Ngoc Pham"
}
```

Sau khi register thanh cong, dung lai email va password do de login.

## 6. Neu muon nap du lieu mau

Chay lenh sau:

```powershell
Get-Content database_seed.sql | docker exec -i instagallery-backend-mysql-1 mysql -uig_user -p123456789 instagallery
```

Luu y:

- Seed chi co y nghia khi MySQL container cua project dang chay.
- Neu password trong seed khong khop, cach on dinh nhat van la tu register tai khoan moi.

## 7. Kiem tra nhanh khi co loi

### Backend khong len

Xem log backend:

```powershell
docker compose logs backend --tail 100
```

### MySQL chua san sang

Xem log:

```powershell
docker logs instagallery-backend-mysql-1 --tail 50
```

Khi thay `ready for connections` thi database da san sang.

### Xem container dang chay

```powershell
docker-compose ps
```

## 8. Dung he thong

Neu can dung toan bo container:

```powershell
docker-compose down
```

Neu muon xoa ca du lieu MySQL volume local:

```powershell
docker-compose down -v
```
