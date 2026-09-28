const fs = require("fs");
const path = require("path");

const outFile = path.join(__dirname, "..", "InstaGallery_Local.postman_collection.json");

const tokenScript = [
  "const json = pm.response.json();",
  "const data = json.data || {};",
  "if (data.token) {",
  "  pm.collectionVariables.set('JWT_TOKEN', data.token);",
  "  pm.environment.set('JWT_TOKEN', data.token);",
  "}",
  "if (data.refreshToken) {",
  "  pm.collectionVariables.set('REFRESH_TOKEN', data.refreshToken);",
  "  pm.environment.set('REFRESH_TOKEN', data.refreshToken);",
  "}",
  "if (data.userId) {",
  "  pm.collectionVariables.set('USER_ID', String(data.userId));",
  "}"
];

function item(spec) {
  const pathParts = spec.path.split("/").filter(Boolean).map((part) =>
    part.startsWith(":") ? part : part
  );
  const variables = pathParts
    .filter((part) => part.startsWith(":"))
    .map((part) => ({ key: part.slice(1), value: spec.vars?.[part.slice(1)] || "1" }));
  const query = (spec.query || []).map((entry) => {
    const row = { key: entry.key, value: entry.value };
    if (entry.description) row.description = entry.description;
    if (entry.disabled || entry.value === "") row.disabled = true;
    return row;
  });
  const activeQuery = query.filter((entry) => !entry.disabled);
  const rawQuery = activeQuery.length
    ? "?" + activeQuery.map((entry) => `${entry.key}=${encodeURIComponent(entry.value)}`).join("&")
    : "";
  const baseVar = spec.ws ? "{{WS_BASE_URL}}" : "{{BASE_URL}}";
  const request = {
    auth: spec.auth === "none"
      ? { type: "noauth" }
      : {
          type: "bearer",
          bearer: [{ key: "token", value: "{{JWT_TOKEN}}", type: "string" }]
        },
    method: spec.method,
    header: [],
    url: {
      raw: `${baseVar}${spec.path}${rawQuery}`,
      host: [baseVar],
      path: pathParts,
      query: query.length ? query : undefined,
      variable: variables.length ? variables : undefined
    },
    description: spec.description || spec.name
  };
  if (spec.headers) {
    request.header.push(...spec.headers);
  }
  if (spec.body !== undefined) {
    request.header.push({ key: "Content-Type", value: "application/json", type: "text" });
    request.body = {
      mode: "raw",
      raw: typeof spec.body === "string" ? spec.body : JSON.stringify(spec.body, null, 2),
      options: { raw: { language: "json" } }
    };
  }
  const result = { name: spec.name, request, response: [] };
  if (spec.saveToken) {
    result.event = [{ listen: "test", script: { type: "text/javascript", exec: tokenScript } }];
  }
  return result;
}

function folder(name, requests) {
  return { name, item: requests.map(item) };
}

const collection = {
  info: {
    _postman_id: "b3e2df14-995a-4ae8-b310-3975e971c1bb",
    name: "InstaGallery Local Development",
    description: "",
    schema: "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  item: [
    folder("0. System", [
      { name: "GET / - Server root", method: "GET", path: "/", auth: "none", description: "Plain text. Server is up." },
      { name: "GET /health", method: "GET", path: "/health", auth: "none", description: "Plain text health. Does not check MySQL." }
    ]),
    folder("1. Auth", [
      { name: "POST /api/v1/auth/register", method: "POST", path: "/api/v1/auth/register", auth: "none", saveToken: true, body: { email: "user@example.com", username: "demo_user", password: "demo123!", fullName: "Demo User", userType: "CLIENT" }, description: "userType is CLIENT or PHOTOGRAPHER. Saves access and refresh tokens. Rate limit 5/minute." },
      { name: "POST /api/v1/auth/login", method: "POST", path: "/api/v1/auth/login", auth: "none", saveToken: true, body: { usernameOrEmail: "user@example.com", password: "demo123!" }, description: "Saves JWT_TOKEN and REFRESH_TOKEN from data." },
      { name: "POST /api/v1/auth/refresh", method: "POST", path: "/api/v1/auth/refresh", auth: "none", saveToken: true, body: { refreshToken: "{{REFRESH_TOKEN}}" }, description: "Rotates the refresh token. The previous raw token cannot be used again." },
      { name: "POST /api/v1/auth/forgot-password", method: "POST", path: "/api/v1/auth/forgot-password", auth: "none", body: { email: "user@example.com" }, description: "Reset code is hashed. debugResetToken is returned only when EXPOSE_DEBUG_RESET_TOKEN=true." },
      { name: "POST /api/v1/auth/reset-password", method: "POST", path: "/api/v1/auth/reset-password", auth: "none", body: { resetToken: "123456", newPassword: "newDemo123!" } },
      { name: "POST /api/v1/auth/google", method: "POST", path: "/api/v1/auth/google", auth: "none", saveToken: true, body: { idToken: "google-id-token", nonce: null, userType: "CLIENT" }, description: "Send the Google ID token in idToken." },
      { name: "POST /api/v1/auth/2fa/verify-login", method: "POST", path: "/api/v1/auth/2fa/verify-login", auth: "none", description: "Returns 501 NOT_IMPLEMENTED." },
      { name: "POST /api/v1/auth/logout", method: "POST", path: "/api/v1/auth/logout", headers: [{ key: "X-Refresh-Token", value: "{{REFRESH_TOKEN}}", type: "text" }], description: "Requires Bearer access token and the raw refresh token in X-Refresh-Token." },
      { name: "PUT /api/v1/auth/change-password", method: "PUT", path: "/api/v1/auth/change-password", body: { oldPasswordHash: "demo123!", newPasswordHash: "newDemo123!" }, description: "Revokes refresh sessions. Log in again." },
      { name: "POST /api/v1/auth/2fa/setup", method: "POST", path: "/api/v1/auth/2fa/setup", description: "Returns 501 NOT_IMPLEMENTED." },
      { name: "POST /api/v1/auth/2fa/enable", method: "POST", path: "/api/v1/auth/2fa/enable", description: "Returns 501 NOT_IMPLEMENTED." }
    ]),
    folder("2. Users", [
      { name: "GET /api/v1/users/me", method: "GET", path: "/api/v1/users/me" },
      { name: "PUT /api/v1/users/me", method: "PUT", path: "/api/v1/users/me", body: { fullName: "Demo User", bio: "Hello InstaGallery", website: "https://example.com", gender: "female", phoneNumber: "0900000000", dateOfBirth: "2000-01-01", location: "Ho Chi Minh City" } },
      { name: "GET /api/v1/users/me/sessions", method: "GET", path: "/api/v1/users/me/sessions" },
      { name: "DELETE /api/v1/users/me/sessions/:id", method: "DELETE", path: "/api/v1/users/me/sessions/:id" },
      { name: "POST /api/v1/users/me/deactivate", method: "POST", path: "/api/v1/users/me/deactivate" },
      { name: "PUT /api/v1/users/me/avatar", method: "PUT", path: "/api/v1/users/me/avatar", description: "Mock response. Does not store a file." },
      { name: "PUT /api/v1/users/me/privacy", method: "PUT", path: "/api/v1/users/me/privacy", body: { isPrivate: false } },
      { name: "GET /api/v1/users/me/saved-posts", method: "GET", path: "/api/v1/users/me/saved-posts", query: [{ key: "page", value: "1" }, { key: "limit", value: "20" }] },
      { name: "GET /api/v1/users/me/liked-posts", method: "GET", path: "/api/v1/users/me/liked-posts", query: [{ key: "page", value: "1" }, { key: "limit", value: "20" }] },
      { name: "GET /api/v1/users/me/tagged-posts", method: "GET", path: "/api/v1/users/me/tagged-posts", query: [{ key: "page", value: "1" }, { key: "limit", value: "20" }] },
      { name: "GET /api/v1/users/me/comments", method: "GET", path: "/api/v1/users/me/comments", query: [{ key: "page", value: "1" }, { key: "limit", value: "20" }] },
      { name: "GET /api/v1/users/me/activity-log", method: "GET", path: "/api/v1/users/me/activity-log", query: [{ key: "page", value: "1" }, { key: "limit", value: "20" }] },
      { name: "GET /api/v1/users/me/blocked", method: "GET", path: "/api/v1/users/me/blocked" },
      { name: "GET /api/v1/users/suggestions", method: "GET", path: "/api/v1/users/suggestions", query: [{ key: "limit", value: "10" }] },
      { name: "GET /api/v1/users/me/follow-requests", method: "GET", path: "/api/v1/users/me/follow-requests" },
      { name: "POST /api/v1/users/me/follow-requests/:followerId/:action", method: "POST", path: "/api/v1/users/me/follow-requests/:followerId/:action", vars: { action: "accept" }, description: "action is accept or reject." },
      { name: "POST /api/v1/users/:id/follow", method: "POST", path: "/api/v1/users/:id/follow" },
      { name: "POST /api/v1/users/:id/block", method: "POST", path: "/api/v1/users/:id/block" },
      { name: "POST /api/v1/users/:id/mute", method: "POST", path: "/api/v1/users/:id/mute" },
      { name: "GET /api/v1/users/:id", method: "GET", path: "/api/v1/users/:id", auth: "none", description: "Public profile." },
      { name: "GET /api/v1/users/:id/followers", method: "GET", path: "/api/v1/users/:id/followers", auth: "none", query: [{ key: "page", value: "1" }, { key: "limit", value: "20" }] },
      { name: "GET /api/v1/users/:id/following", method: "GET", path: "/api/v1/users/:id/following", auth: "none", query: [{ key: "page", value: "1" }, { key: "limit", value: "20" }] }
    ]),
    folder("3. Posts", [
      { name: "POST /api/v1/posts", method: "POST", path: "/api/v1/posts", body: { caption: "Flow check", location: "Hanoi", visibility: "PUBLIC", media: [{ mediaFileUrl: "https://example.com/flow.jpg", mediaType: "IMAGE" }], tags: ["portrait"] }, description: "One JSON object. An array body is rejected." },
      { name: "GET /api/v1/posts/feed", method: "GET", path: "/api/v1/posts/feed", query: [{ key: "page", value: "1" }, { key: "limit", value: "10" }] },
      { name: "GET /api/v1/posts/:id", method: "GET", path: "/api/v1/posts/:id" },
      { name: "PUT /api/v1/posts/:id", method: "PUT", path: "/api/v1/posts/:id", body: { caption: "Updated caption", location: "Hanoi", visibility: "PUBLIC", tags: ["portrait"] } },
      { name: "DELETE /api/v1/posts/:id", method: "DELETE", path: "/api/v1/posts/:id" },
      { name: "GET /api/v1/posts/users/:userId/posts", method: "GET", path: "/api/v1/posts/users/:userId/posts", query: [{ key: "page", value: "1" }, { key: "limit", value: "20" }] },
      { name: "POST /api/v1/posts/:id/tags", method: "POST", path: "/api/v1/posts/:id/tags", body: { taggedUserId: 2 } },
      { name: "DELETE /api/v1/posts/:id/tags/:taggedUserId", method: "DELETE", path: "/api/v1/posts/:id/tags/:taggedUserId", vars: { taggedUserId: "2" } },
      { name: "PUT /api/v1/posts/:id/comment-settings", method: "PUT", path: "/api/v1/posts/:id/comment-settings", body: { commentSetting: "ALL" }, description: "commentSetting is ALL, FOLLOWING, or NONE." }
    ]),
    folder("4. Interactions", [
      { name: "POST /api/v1/posts/:id/like", method: "POST", path: "/api/v1/posts/:id/like" },
      { name: "GET /api/v1/posts/:id/likes", method: "GET", path: "/api/v1/posts/:id/likes", query: [{ key: "page", value: "1" }, { key: "limit", value: "20" }] },
      { name: "POST /api/v1/posts/:id/save", method: "POST", path: "/api/v1/posts/:id/save" },
      { name: "POST /api/v1/posts/:id/comments", method: "POST", path: "/api/v1/posts/:id/comments", body: { content: "Xin chao", parentId: null } },
      { name: "GET /api/v1/posts/:id/comments", method: "GET", path: "/api/v1/posts/:id/comments", query: [{ key: "page", value: "1" }, { key: "limit", value: "20" }] },
      { name: "POST /api/v1/posts/:id/share", method: "POST", path: "/api/v1/posts/:id/share", description: "Second share by the same user does not increase shareCount." },
      { name: "GET /api/v1/posts/:id/shares", method: "GET", path: "/api/v1/posts/:id/shares" },
      { name: "PUT /api/v1/comments/:commentId", method: "PUT", path: "/api/v1/comments/:commentId", body: { content: "Da sua binh luan" } },
      { name: "DELETE /api/v1/comments/:commentId", method: "DELETE", path: "/api/v1/comments/:commentId" },
      { name: "POST /api/v1/comments/:commentId/like", method: "POST", path: "/api/v1/comments/:commentId/like", description: "One comment_reactions row. Like again removes it. Like while disliked switches to LIKE." },
      { name: "POST /api/v1/comments/:commentId/dislike", method: "POST", path: "/api/v1/comments/:commentId/dislike", description: "Switches the same row to DISLIKE, or removes it if already disliked." }
    ]),
    folder("5. Media", [
      { name: "POST /api/v1/media/presigned-url", method: "POST", path: "/api/v1/media/presigned-url", body: { fileName: "flow.jpg", contentType: "image/jpeg", folder: "posts" } },
      { name: "PUT /api/v1/media/local-upload/:folder/:fileName", method: "PUT", path: "/api/v1/media/local-upload/:folder/:fileName", vars: { folder: "posts", fileName: "flow.jpg" }, description: "Binary upload of the file named by the presigned URL. Send the file as the body, with Authorization Bearer." },
      { name: "GET /api/v1/media/local-files/:folder/:fileName", method: "GET", path: "/api/v1/media/local-files/:folder/:fileName", auth: "none", vars: { folder: "posts", fileName: "flow.jpg" } },
      { name: "POST /api/v1/posts/:postId/media", method: "POST", path: "/api/v1/posts/:postId/media", body: { mediaFileUrl: "https://example.com/extra.jpg", mediaType: "IMAGE" } },
      { name: "PUT /api/v1/posts/:postId/media/reorder", method: "PUT", path: "/api/v1/posts/:postId/media/reorder", body: { mediaIds: [1, 2] } },
      { name: "DELETE /api/v1/posts/media/:mediaId", method: "DELETE", path: "/api/v1/posts/media/:mediaId" }
    ]),
    folder("6. Albums", [
      { name: "POST /api/v1/albums", method: "POST", path: "/api/v1/albums", body: { title: "Ky yeu", description: "Album demo", coverImageUrl: null, isPrivate: false }, description: "Album media points at posts, not individual photos." },
      { name: "GET /api/v1/albums", method: "GET", path: "/api/v1/albums", query: [{ key: "userId", value: "1", description: "Optional owner. Defaults to the caller." }] },
      { name: "GET /api/v1/albums/:id", method: "GET", path: "/api/v1/albums/:id" },
      { name: "PUT /api/v1/albums/:id", method: "PUT", path: "/api/v1/albums/:id", body: { title: "Ky yeu 2026", description: "Updated", isPrivate: false } },
      { name: "DELETE /api/v1/albums/:id", method: "DELETE", path: "/api/v1/albums/:id" },
      { name: "POST /api/v1/albums/:id/media", method: "POST", path: "/api/v1/albums/:id/media", body: { postIds: [1] } },
      { name: "DELETE /api/v1/albums/:id/media/:mediaId", method: "DELETE", path: "/api/v1/albums/:id/media/:mediaId" }
    ]),
    folder("7. Explore", [
      { name: "GET /api/v1/explore", method: "GET", path: "/api/v1/explore", auth: "none", query: [{ key: "page", value: "1" }, { key: "limit", value: "20" }, { key: "tag", value: "" }] },
      { name: "GET /api/v1/explore/trending", method: "GET", path: "/api/v1/explore/trending", auth: "none", query: [{ key: "limit", value: "10" }] },
      { name: "GET /api/v1/explore/hashtags/:tag", method: "GET", path: "/api/v1/explore/hashtags/:tag", auth: "none", vars: { tag: "portrait" }, query: [{ key: "page", value: "1" }, { key: "limit", value: "20" }] }
    ]),
    folder("8. Search", [
      { name: "GET /api/v1/search", method: "GET", path: "/api/v1/search", query: [{ key: "q", value: "portrait" }, { key: "type", value: "ALL" }, { key: "limit", value: "20" }], description: "Public. A valid Bearer token saves search history. Same user and query updates one row." },
      { name: "GET /api/v1/search/history", method: "GET", path: "/api/v1/search/history" },
      { name: "DELETE /api/v1/search/history", method: "DELETE", path: "/api/v1/search/history" },
      { name: "GET /api/v1/search/trending", method: "GET", path: "/api/v1/search/trending", auth: "none", query: [{ key: "limit", value: "10" }] }
    ]),
    folder("9. Chat", [
      { name: "POST /api/v1/chat/conversations", method: "POST", path: "/api/v1/chat/conversations", body: { targetUserId: 2 }, description: "Returns conversationId and isNew. One DIRECT thread per user pair." },
      { name: "GET /api/v1/chat/conversations", method: "GET", path: "/api/v1/chat/conversations", description: "Skips conversations hidden by the caller." },
      { name: "GET /api/v1/chat/conversations/:id/messages", method: "GET", path: "/api/v1/chat/conversations/:id/messages", query: [{ key: "page", value: "1" }, { key: "limit", value: "50" }], description: "Clears hidden_at for the caller." },
      { name: "PUT /api/v1/chat/conversations/:id/read", method: "PUT", path: "/api/v1/chat/conversations/:id/read" },
      { name: "POST /api/v1/chat/conversations/:id/messages", method: "POST", path: "/api/v1/chat/conversations/:id/messages", body: { content: "Xin chao", messageType: "TEXT" } },
      { name: "DELETE /api/v1/chat/conversations/:id", method: "DELETE", path: "/api/v1/chat/conversations/:id", description: "Sets hidden_at. Does not delete the membership row." },
      { name: "WS /api/v1/ws/chat", method: "GET", path: "/api/v1/ws/chat", auth: "none", ws: true, query: [{ key: "token", value: "{{JWT_TOKEN}}" }], description: "WebSocket at ws://localhost:8080/api/v1/ws/chat?token={{JWT_TOKEN}}. Open it as a WebSocket request in Postman. The token is verified with JwtManager." }
    ]),
    folder("10. Notifications", [
      { name: "GET /api/v1/notifications", method: "GET", path: "/api/v1/notifications", query: [{ key: "page", value: "1" }, { key: "limit", value: "20" }] },
      { name: "GET /api/v1/notifications/unread-count", method: "GET", path: "/api/v1/notifications/unread-count" },
      { name: "PUT /api/v1/notifications/:id/read", method: "PUT", path: "/api/v1/notifications/:id/read" },
      { name: "PUT /api/v1/notifications/read-all", method: "PUT", path: "/api/v1/notifications/read-all" },
      { name: "DELETE /api/v1/notifications/:id", method: "DELETE", path: "/api/v1/notifications/:id" }
    ]),
    folder("11. Devices", [
      { name: "POST /api/v1/devices/fcm-token", method: "POST", path: "/api/v1/devices/fcm-token", body: { token: "fcm-device-token", platform: "ANDROID", deviceId: "emulator", appVersion: "1.0.0" }, description: "Push delivery needs a Firebase service account. Without it, in-app notifications still save." },
      { name: "DELETE /api/v1/devices/fcm-token", method: "DELETE", path: "/api/v1/devices/fcm-token", body: { token: "fcm-device-token", platform: "ANDROID" } }
    ]),
    folder("12. Portfolios", [
      { name: "GET /api/v1/portfolios", method: "GET", path: "/api/v1/portfolios", auth: "none", query: [{ key: "location", value: "" }, { key: "specialty", value: "" }, { key: "page", value: "1" }, { key: "limit", value: "20" }] },
      { name: "GET /api/v1/portfolios/users/:userId", method: "GET", path: "/api/v1/portfolios/users/:userId", auth: "none" },
      { name: "GET /api/v1/portfolios/users/:userId/availability", method: "GET", path: "/api/v1/portfolios/users/:userId/availability", auth: "none" },
      { name: "GET /api/v1/portfolios/me", method: "GET", path: "/api/v1/portfolios/me" },
      { name: "PUT /api/v1/portfolios/me", method: "PUT", path: "/api/v1/portfolios/me", body: { bioProfessional: "Studio", hourlyRate: 150000, specialties: "portrait", equipment: "camera", location: "Hanoi" }, description: "Caller must be PHOTOGRAPHER." },
      { name: "POST /api/v1/portfolios/me/availability", method: "POST", path: "/api/v1/portfolios/me/availability", body: [{ type: "SPECIFIC_DATE", specificDate: "2026-10-15", startTime: "09:00", endTime: "17:00" }], description: "JSON array. Empty schedule closes the photographer." },
      { name: "GET /api/v1/portfolios/me/availability", method: "GET", path: "/api/v1/portfolios/me/availability" }
    ]),
    folder("13. Photographer services", [
      { name: "GET /api/v1/users/:photographerId/services", method: "GET", path: "/api/v1/users/:photographerId/services", auth: "none" },
      { name: "GET /api/v1/photographer/services", method: "GET", path: "/api/v1/photographer/services" },
      { name: "POST /api/v1/photographer/services", method: "POST", path: "/api/v1/photographer/services", body: { name: "Portrait 1h", category: "PORTRAIT", price: 1500000, currency: "VND", durationMinutes: 60, photoCount: 20, description: "Outdoor portrait", includes: ["editing"], isActive: true } },
      { name: "GET /api/v1/photographer/services/:id", method: "GET", path: "/api/v1/photographer/services/:id" },
      { name: "PUT /api/v1/photographer/services/:id", method: "PUT", path: "/api/v1/photographer/services/:id", body: { name: "Portrait 2h", category: "PORTRAIT", price: 2500000, currency: "VND", durationMinutes: 120, isActive: true } },
      { name: "PUT /api/v1/photographer/services/:id/status", method: "PUT", path: "/api/v1/photographer/services/:id/status", body: { isActive: true } },
      { name: "DELETE /api/v1/photographer/services/:id", method: "DELETE", path: "/api/v1/photographer/services/:id" }
    ]),
    folder("14. Bookings", [
      { name: "POST /api/v1/bookings", method: "POST", path: "/api/v1/bookings", body: { photographerId: 2, bookingDate: "2026-10-15T10:00:00", durationHours: 1, locationBooking: "Hanoi", details: "Outdoor", price: 1500000, currency: "VND" }, description: "bookingDate is yyyy-MM-ddTHH:mm:ss. Overlap returns BOOKING_SLOT_UNAVAILABLE. Empty schedule returns BOOKING_OUTSIDE_AVAILABILITY." },
      { name: "GET /api/v1/bookings", method: "GET", path: "/api/v1/bookings", query: [{ key: "page", value: "1" }, { key: "limit", value: "10" }, { key: "status", value: "" }] },
      { name: "GET /api/v1/bookings/:id", method: "GET", path: "/api/v1/bookings/:id" },
      { name: "PUT /api/v1/bookings/:id/status", method: "PUT", path: "/api/v1/bookings/:id/status", body: { status: "CONFIRMED", cancellationReason: null }, description: "PENDING, CONFIRMED, IN_PROGRESS, COMPLETED, CANCELLED, REJECTED." },
      { name: "POST /api/v1/bookings/:id/review", method: "POST", path: "/api/v1/bookings/:id/review", body: { score: 5, comment: "Rat tot" } },
      { name: "DELETE /api/v1/bookings/:id", method: "DELETE", path: "/api/v1/bookings/:id", description: "Cancels the booking and frees the time slot." }
    ]),
    folder("15. Ratings", [
      { name: "GET /api/v1/users/:photographerId/ratings", method: "GET", path: "/api/v1/users/:photographerId/ratings", auth: "none", query: [{ key: "page", value: "1" }, { key: "limit", value: "10" }] },
      { name: "POST /api/v1/users/:photographerId/ratings", method: "POST", path: "/api/v1/users/:photographerId/ratings", query: [{ key: "bookingId", value: "1" }], body: { score: 5, comment: "Rat tot" } },
      { name: "PUT /api/v1/ratings/:ratingId", method: "PUT", path: "/api/v1/ratings/:ratingId", body: { score: 4, comment: "Cap nhat" } },
      { name: "DELETE /api/v1/ratings/:ratingId", method: "DELETE", path: "/api/v1/ratings/:ratingId" }
    ]),
    folder("16. Reports", [
      { name: "POST /api/v1/reports", method: "POST", path: "/api/v1/reports", body: { targetType: "POST", targetId: 1, reason: "spam", description: "Noi dung lap" }, description: "Same reporter and target returns the existing report. targetType: POST, COMMENT, USER, BOOKING, MESSAGE, RATING." }
    ]),
    folder("17. Admin", [
      { name: "GET /api/v1/admin/stats", method: "GET", path: "/api/v1/admin/stats", description: "Role ADMIN." },
      { name: "GET /api/v1/admin/stats/growth", method: "GET", path: "/api/v1/admin/stats/growth", query: [{ key: "type", value: "USERS" }, { key: "days", value: "7" }] },
      { name: "GET /api/v1/admin/users", method: "GET", path: "/api/v1/admin/users", query: [{ key: "page", value: "1" }, { key: "limit", value: "20" }, { key: "search", value: "" }, { key: "status", value: "" }] },
      { name: "GET /api/v1/admin/users/:userId", method: "GET", path: "/api/v1/admin/users/:userId" },
      { name: "PUT /api/v1/admin/users/:userId/ban", method: "PUT", path: "/api/v1/admin/users/:userId/ban", body: { isBanned: true, reason: "spam" } },
      { name: "PUT /api/v1/admin/users/:userId/verification", method: "PUT", path: "/api/v1/admin/users/:userId/verification", body: { isVerified: true } },
      { name: "PUT /api/v1/admin/users/:userId/featured", method: "PUT", path: "/api/v1/admin/users/:userId/featured", body: { isFeatured: true } },
      { name: "GET /api/v1/admin/posts", method: "GET", path: "/api/v1/admin/posts", query: [{ key: "page", value: "1" }, { key: "limit", value: "20" }, { key: "search", value: "" }, { key: "status", value: "" }] },
      { name: "GET /api/v1/admin/posts/:postId", method: "GET", path: "/api/v1/admin/posts/:postId" },
      { name: "PUT /api/v1/admin/posts/:postId/status", method: "PUT", path: "/api/v1/admin/posts/:postId/status", body: { status: "HIDDEN" } },
      { name: "DELETE /api/v1/admin/posts/:postId", method: "DELETE", path: "/api/v1/admin/posts/:postId" },
      { name: "DELETE /api/v1/admin/comments/:commentId", method: "DELETE", path: "/api/v1/admin/comments/:commentId" },
      { name: "GET /api/v1/admin/bookings", method: "GET", path: "/api/v1/admin/bookings", query: [{ key: "page", value: "1" }, { key: "limit", value: "20" }, { key: "search", value: "" }, { key: "status", value: "" }] },
      { name: "GET /api/v1/admin/bookings/:bookingId", method: "GET", path: "/api/v1/admin/bookings/:bookingId" },
      { name: "PUT /api/v1/admin/bookings/:bookingId/status", method: "PUT", path: "/api/v1/admin/bookings/:bookingId/status", body: { status: "CONFIRMED" } },
      { name: "GET /api/v1/admin/ratings", method: "GET", path: "/api/v1/admin/ratings", query: [{ key: "page", value: "1" }, { key: "limit", value: "20" }, { key: "search", value: "" }, { key: "status", value: "" }] },
      { name: "PUT /api/v1/admin/ratings/:ratingId/status", method: "PUT", path: "/api/v1/admin/ratings/:ratingId/status", body: { status: "HIDDEN" }, description: "APPROVED, PENDING, or HIDDEN." },
      { name: "DELETE /api/v1/admin/ratings/:ratingId", method: "DELETE", path: "/api/v1/admin/ratings/:ratingId" },
      { name: "GET /api/v1/admin/media", method: "GET", path: "/api/v1/admin/media", query: [{ key: "page", value: "1" }, { key: "limit", value: "20" }, { key: "search", value: "" }] },
      { name: "DELETE /api/v1/admin/media/:mediaId", method: "DELETE", path: "/api/v1/admin/media/:mediaId" },
      { name: "GET /api/v1/admin/notifications", method: "GET", path: "/api/v1/admin/notifications", query: [{ key: "page", value: "1" }, { key: "limit", value: "20" }, { key: "search", value: "" }] },
      { name: "POST /api/v1/admin/notifications", method: "POST", path: "/api/v1/admin/notifications", body: { title: "Thong bao", body: "Noi dung", target: "ALL" } },
      { name: "GET /api/v1/admin/reports", method: "GET", path: "/api/v1/admin/reports", query: [{ key: "status", value: "PENDING" }, { key: "page", value: "1" }, { key: "limit", value: "20" }] },
      { name: "PUT /api/v1/admin/reports/:reportId", method: "PUT", path: "/api/v1/admin/reports/:reportId", body: { status: "RESOLVED", adminNote: "Da xu ly" }, description: "PENDING, REVIEWING, RESOLVED, DISMISSED." },
      { name: "GET /api/v1/admin/banned-keywords", method: "GET", path: "/api/v1/admin/banned-keywords" },
      { name: "POST /api/v1/admin/banned-keywords", method: "POST", path: "/api/v1/admin/banned-keywords", body: { wordOrRegex: "spam", isRegex: false } },
      { name: "DELETE /api/v1/admin/banned-keywords/:id", method: "DELETE", path: "/api/v1/admin/banned-keywords/:id" },
      { name: "GET /api/v1/admin/activity-logs", method: "GET", path: "/api/v1/admin/activity-logs", query: [{ key: "page", value: "1" }, { key: "limit", value: "20" }, { key: "search", value: "" }] }
    ])
  ],
  variable: [
    { key: "BASE_URL", value: "http://localhost:8080", type: "string" },
    { key: "WS_BASE_URL", value: "ws://localhost:8080", type: "string" },
    { key: "JWT_TOKEN", value: "", type: "string" },
    { key: "REFRESH_TOKEN", value: "", type: "string" },
    { key: "USER_ID", value: "", type: "string" }
  ]
};

const restCount = collection.item
  .flatMap((group) => group.item)
  .filter((entry) => entry.request.url.path.join("/").startsWith("api/v1") && !entry.name.startsWith("WS "))
  .length;
collection.info.description = `Local collection for http://localhost:8080. Register, login, refresh, and Google login save JWT_TOKEN and REFRESH_TOKEN. The server stores the refresh token as HMAC-SHA256 and returns the raw token once. Chat create returns conversationId and isNew. Booking time uses yyyy-MM-ddTHH:mm:ss. 2FA routes return 501. ${restCount} REST /api/v1 requests, plus root, health, and the WebSocket entry.`;

fs.writeFileSync(outFile, JSON.stringify(collection, null, 2) + "\n");
console.log("Wrote", outFile, "requests", collection.item.reduce((n, g) => n + g.item.length, 0), "rest", restCount);
