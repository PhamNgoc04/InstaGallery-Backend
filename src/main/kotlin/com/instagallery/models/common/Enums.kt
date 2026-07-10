package com.instagallery.models.common

enum class UserType { PHOTOGRAPHER, CLIENT }
enum class Role { USER, ADMIN }
enum class AuthProvider { LOCAL, GOOGLE, FACEBOOK, APPLE }
enum class PostVisibility { PUBLIC, PRIVATE, FRIENDS_ONLY }
enum class CommentVisibility { ALLOW_ALL, FOLLOWERS_ONLY, NO_ONE }
enum class MediaType { IMAGE, VIDEO }
enum class BookingStatus { PENDING, CONFIRMED, IN_PROGRESS, COMPLETED, CANCELLED, REJECTED }
enum class ConversationType { DIRECT, GROUP }
enum class ConversationRole { MEMBER, ADMIN }
enum class MessageType { TEXT, IMAGE, VIDEO, FILE, SYSTEM }

enum class NotificationType {
    NEW_LIKE, NEW_COMMENT, NEW_FOLLOWER, POST_SAVED, COMMENT_LIKED, BOOKING_REQUEST, BOOKING_CONFIRMED,
    BOOKING_IN_PROGRESS, BOOKING_COMPLETED, BOOKING_CANCELLED, BOOKING_REJECTED,
    NEW_MESSAGE, NEW_POST, REVIEW_RECEIVED, MENTION, SYSTEM
}
enum class NotificationTargetType { POST, COMMENT, USER, BOOKING, CONVERSATION }

enum class ActivityTargetType { POST, USER, COMMENT, BOOKING, MEDIA, SESSION }

enum class ReportTargetType { POST, COMMENT, USER, BOOKING, MESSAGE }
enum class ReportStatus { PENDING, REVIEWING, RESOLVED, DISMISSED }

enum class FollowRequestStatus { PENDING, ACCEPTED, REJECTED }
enum class AvailabilityType { RECURRING, SPECIFIC_DATE }
enum class DayOfWeekIso { MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY }
