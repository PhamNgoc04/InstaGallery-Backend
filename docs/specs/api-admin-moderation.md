# API Dac Ta: Quan Tri He Thong va Moderation

Source hien tai:

- `AdminRoutes.kt`
- `ReportRoutes.kt`

Base path: `/api/v1`

Tat ca API admin can `Authorization: Bearer <accessToken>` voi role `ADMIN`.

## 1. Dashboard stats

### `GET /api/v1/admin/stats`

Lay thong so tong quan cho dashboard admin.

### `GET /api/v1/admin/stats/growth`

Lay du lieu tang truong theo ngay.

Query goi y:

| Query | Mo ta |
|---|---|
| `type` | `USERS` hoac `POSTS` |
| `days` | So ngay can xem, mac dinh tuy service |

## 2. User management

### `GET /api/v1/admin/users`

Lay danh sach user cho man hinh quan tri.

Query goi y:

| Query | Mo ta |
|---|---|
| `status` | Loc trang thai user neu backend service ho tro |

### `GET /api/v1/admin/users/{userId}`

Lay chi tiet mot user.

### `PUT /api/v1/admin/users/{userId}/ban`

Ban hoac unban user.

Request body:

```json
{
  "isBanned": true,
  "reason": "Vi pham tieu chuan cong dong."
}
```

## 3. Content moderation

### `DELETE /api/v1/admin/posts/{postId}`

Admin xoa post vi pham.

### `DELETE /api/v1/admin/comments/{commentId}`

Admin xoa comment vi pham.

## 4. Report moderation

### `POST /api/v1/reports`

User tao report noi dung vi pham.

Request body tuy theo `ReportRequest` hien tai, thuong gom:

```json
{
  "targetType": "POST",
  "targetId": 1,
  "reason": "Spam"
}
```

### `GET /api/v1/admin/reports`

Admin xem danh sach report.

### `PUT /api/v1/admin/reports/{reportId}`

Admin cap nhat trang thai/xu ly report.

## 5. Banned keywords

### `GET /api/v1/admin/banned-keywords`

Lay danh sach tu khoa/pattern bi cam.

### `POST /api/v1/admin/banned-keywords`

Them tu khoa/pattern bi cam.

Request body:

```json
{
  "wordOrRegex": "spam",
  "isRegex": false
}
```

### `DELETE /api/v1/admin/banned-keywords/{id}`

Xoa mot tu khoa/pattern bi cam.

## 6. Endpoint summary

| Method | Endpoint | Auth |
|---|---|:---:|
| GET | `/api/v1/admin/stats` | Admin |
| GET | `/api/v1/admin/stats/growth` | Admin |
| GET | `/api/v1/admin/users` | Admin |
| GET | `/api/v1/admin/users/{userId}` | Admin |
| PUT | `/api/v1/admin/users/{userId}/ban` | Admin |
| DELETE | `/api/v1/admin/posts/{postId}` | Admin |
| DELETE | `/api/v1/admin/comments/{commentId}` | Admin |
| POST | `/api/v1/reports` | User |
| GET | `/api/v1/admin/reports` | Admin |
| PUT | `/api/v1/admin/reports/{reportId}` | Admin |
| GET | `/api/v1/admin/banned-keywords` | Admin |
| POST | `/api/v1/admin/banned-keywords` | Admin |
| DELETE | `/api/v1/admin/banned-keywords/{id}` | Admin |
