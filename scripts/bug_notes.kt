fun getFollowers(userId: Long, page: Int, limit: Int) {
    // val query = (FollowersTable innerJoin UsersTable).selectAll().where { FollowersTable.followingId eq userId }
    // This implicit join fails because FollowersTable has TWO references to UsersTable (followerId and followingId).
    // The innerJoin function doesn't know which one to pick by default, causing a ambiguity crash.
}
