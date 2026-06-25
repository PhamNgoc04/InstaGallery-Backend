# Admin Backend Context

Tai lieu nay danh cho frontend Admin Dashboard. Noi dung duoc dong bo voi source hien tai trong:

- `src/main/kotlin/com/instagallery/routes/AdminRoutes.kt`
- `src/main/kotlin/com/instagallery/routes/ReportRoutes.kt`
- `src/main/kotlin/com/instagallery/routes/AuthRoutes.kt`

Base URL local: `http://localhost:8080/api/v1`

## 1. Auth flow cho Admin

| Method | Endpoint | Auth | Ghi chu |
|---|---|:---:|---|
| POST | `/auth/login` | No | Dang nhap va nhan access/refresh token |
| POST | `/auth/refresh` | No | Lay access token moi |
| POST | `/auth/logout` | Yes | Dang xuat, co the can `X-Refresh-Token` |
| PUT | `/auth/change-password` | Yes | Doi mat khau |

Admin Dashboard can decode JWT de kiem tra claim `role = ADMIN`. Backend cung check role trong route admin.

## 2. Admin routes hien tai

Tat ca endpoint duoi day can header:

```http
Authorization: Bearer <accessToken>
```

| Method | Endpoint | Query / Body | Mo ta |
|---|---|---|---|
| GET | `/admin/stats` | None | Dashboard stats tong quan |
| GET | `/admin/stats/growth` | `?type=USERS/POSTS&days=7` | Du lieu chart tang truong |
| GET | `/admin/users` | `?status=...` | Danh sach user |
| GET | `/admin/users/{userId}` | None | Chi tiet user |
| PUT | `/admin/users/{userId}/ban` | `{ "isBanned": true, "reason": "..." }` | Ban/unban user |
| DELETE | `/admin/posts/{postId}` | None | Admin xoa post |
| DELETE | `/admin/comments/{commentId}` | None | Admin xoa comment |
| GET | `/admin/banned-keywords` | None | Danh sach tu khoa cam |
| POST | `/admin/banned-keywords` | `{ "wordOrRegex": "...", "isRegex": false }` | Them tu khoa cam |
| DELETE | `/admin/banned-keywords/{id}` | None | Xoa tu khoa cam |

## 3. Report moderation routes

Report routes nam trong `ReportRoutes.kt` nhung dung chung namespace admin.

| Method | Endpoint | Auth | Mo ta |
|---|---|:---:|---|
| POST | `/reports` | User | User tao report |
| GET | `/admin/reports` | Admin | Admin xem danh sach report |
| PUT | `/admin/reports/{reportId}` | Admin | Admin cap nhat/xu ly report |

## 4. Response wrapper

Backend tra ve wrapper kieu:

```json
{
  "status": "SUCCESS",
  "message": "...",
  "data": {}
}
```

Khi loi:

```json
{
  "status": "ERROR",
  "message": "...",
  "errorCode": "..."
}
```

## 5. DTO hints cho Admin UI

```typescript
interface UserDto {
  id: number;
  username: string;
  email: string;
  fullName: string;
  role: "USER" | "ADMIN";
  userType: "CLIENT" | "PHOTOGRAPHER";
  isActive: boolean;
  isVerified: boolean;
}

interface ReportDto {
  id: number;
  reporterId: number;
  targetType: "POST" | "COMMENT" | "USER";
  targetId: number;
  reason: string;
  status: "PENDING" | "REVIEWED" | "RESOLVED";
  createdAt: string;
}

interface BannedWordDto {
  id: number;
  wordOrRegex: string;
  isRegex: boolean;
  addedByAdminId?: number;
  createdAt: string;
}
```

## 6. Security notes

- Khong render Admin UI neu JWT khong co `role = ADMIN`.
- Neu API tra `401`, thu refresh token mot lan roi retry request goc.
- Neu refresh token fail, clear token va dua ve login.
- Khong goi cac route system/debug nhu `/reset-db`, `/init-db` tu Admin Dashboard production.
