package com.instagallery.repositories

import com.instagallery.database.tables.CommentsTable
import com.instagallery.database.tables.PostsTable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.SqlExpressionBuilder.isNull
import java.time.Instant

internal object CommentTreeVisibility {
    private data class CommentNode(
        val postId: Long,
        val commentId: Long,
        val parentId: Long?,
    )

    fun visibleCommentCount(postId: Long): Int {
        return visibleCommentCountsByPost(listOf(postId))[postId] ?: 0
    }

    fun visibleCommentCountsByPost(postIds: Collection<Long>): Map<Long, Int> {
        val distinctPostIds = postIds.distinct()
        if (distinctPostIds.isEmpty()) return emptyMap()

        val nodes = CommentsTable
            .selectAll()
            .where {
                (CommentsTable.postId inList distinctPostIds) and
                    CommentsTable.deletedAt.isNull()
            }
            .map { row ->
                CommentNode(
                    postId = row[CommentsTable.postId].value,
                    commentId = row[CommentsTable.id].value,
                    parentId = row[CommentsTable.parentCommentId]?.value,
                )
            }

        return nodes
            .groupBy { it.postId }
            .mapValues { (_, postNodes) -> visibleIds(postNodes).size }
    }

    fun syncAllPostCommentCounts() {
        val postIds = PostsTable
            .selectAll()
            .where { PostsTable.deletedAt.isNull() }
            .map { it[PostsTable.id].value }
        if (postIds.isEmpty()) return

        val nodesByPost = CommentsTable
            .selectAll()
            .where {
                (CommentsTable.postId inList postIds) and
                    CommentsTable.deletedAt.isNull()
            }
            .map { row ->
                CommentNode(
                    postId = row[CommentsTable.postId].value,
                    commentId = row[CommentsTable.id].value,
                    parentId = row[CommentsTable.parentCommentId]?.value,
                )
            }
            .groupBy { it.postId }

        val visibleIdsByPost = nodesByPost.mapValues { (_, postNodes) -> visibleIds(postNodes) }
        val allVisibleIds = visibleIdsByPost.values.flatten().toSet()
        val orphanIds = nodesByPost.values
            .flatten()
            .map { it.commentId }
            .filterNot { it in allVisibleIds }

        if (orphanIds.isNotEmpty()) {
            val now = Instant.now()
            CommentsTable.update({ CommentsTable.id inList orphanIds }) {
                it[CommentsTable.deletedAt] = now
                it[CommentsTable.updatedAt] = now
            }
        }

        postIds.forEach { postId ->
            PostsTable.update({ PostsTable.id eq postId }) {
                it[PostsTable.commentCount] = visibleIdsByPost[postId]?.size ?: 0
            }
        }

        val visibleReplyCounts = nodesByPost.values
            .flatten()
            .filter { it.commentId in allVisibleIds }
            .mapNotNull { node ->
                node.parentId
                    ?.takeIf { parentId -> parentId in allVisibleIds }
                    ?.let { parentId -> parentId to node.commentId }
            }
            .groupBy(keySelector = { it.first }, valueTransform = { it.second })
            .mapValues { (_, childIds) -> childIds.size }

        allVisibleIds.forEach { commentId ->
            CommentsTable.update({ CommentsTable.id eq commentId }) {
                it[CommentsTable.replyCount] = visibleReplyCounts[commentId] ?: 0
            }
        }
    }

    fun visibleSubtreeIds(postId: Long, rootCommentId: Long): Set<Long> {
        val nodes = CommentsTable
            .selectAll()
            .where {
                (CommentsTable.postId eq postId) and
                    CommentsTable.deletedAt.isNull()
            }
            .map { row ->
                CommentNode(
                    postId = row[CommentsTable.postId].value,
                    commentId = row[CommentsTable.id].value,
                    parentId = row[CommentsTable.parentCommentId]?.value,
                )
            }

        return subtreeIds(nodes, rootCommentId)
    }

    private fun visibleIds(nodes: List<CommentNode>): Set<Long> {
        val childrenByParent = nodes
            .mapNotNull { node -> node.parentId?.let { parentId -> parentId to node.commentId } }
            .groupBy(keySelector = { it.first }, valueTransform = { it.second })

        val visible = linkedSetOf<Long>()

        fun visit(commentId: Long) {
            if (!visible.add(commentId)) return
            childrenByParent[commentId].orEmpty().forEach(::visit)
        }

        nodes
            .asSequence()
            .filter { it.parentId == null }
            .map { it.commentId }
            .forEach(::visit)

        return visible
    }

    private fun subtreeIds(nodes: List<CommentNode>, rootCommentId: Long): Set<Long> {
        if (nodes.none { it.commentId == rootCommentId }) return emptySet()

        val childrenByParent = nodes
            .mapNotNull { node -> node.parentId?.let { parentId -> parentId to node.commentId } }
            .groupBy(keySelector = { it.first }, valueTransform = { it.second })

        val subtree = linkedSetOf<Long>()

        fun visit(commentId: Long) {
            if (!subtree.add(commentId)) return
            childrenByParent[commentId].orEmpty().forEach(::visit)
        }

        visit(rootCommentId)
        return subtree
    }
}
