-- Chat: one DIRECT thread per user pair, hide without deleting membership.
ALTER TABLE conversations
    ADD COLUMN direct_pair_key VARCHAR(64) NULL,
    ADD UNIQUE INDEX uk_direct_pair_key (direct_pair_key);

ALTER TABLE conversation_members
    ADD COLUMN hidden_at TIMESTAMP NULL;

-- One share row per user and post.
CREATE TABLE IF NOT EXISTS post_shares (
    user_id BIGINT NOT NULL,
    post_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, post_id),
    CONSTRAINT fk_post_shares_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_post_shares_post FOREIGN KEY (post_id) REFERENCES posts (id) ON DELETE CASCADE
);

-- One reaction per user and comment. Like and dislike cannot both exist.
CREATE TABLE IF NOT EXISTS comment_reactions (
    user_id BIGINT NOT NULL,
    comment_id BIGINT NOT NULL,
    reaction VARCHAR(10) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, comment_id),
    CONSTRAINT fk_comment_reactions_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_comment_reactions_comment FOREIGN KEY (comment_id) REFERENCES comments (id) ON DELETE CASCADE
);

INSERT IGNORE INTO comment_reactions (user_id, comment_id, reaction, created_at)
SELECT user_id, comment_id, 'LIKE', created_at FROM comment_likes;

INSERT IGNORE INTO comment_reactions (user_id, comment_id, reaction, created_at)
SELECT user_id, comment_id, 'DISLIKE', created_at FROM comment_dislikes;

-- One report per reporter and target. Keep the oldest row.
DELETE r1 FROM reports r1
INNER JOIN reports r2
    ON r1.reporter_id = r2.reporter_id
    AND r1.target_type = r2.target_type
    AND r1.target_id = r2.target_id
    AND r1.id > r2.id;

ALTER TABLE reports
    ADD UNIQUE INDEX uk_reporter_target (reporter_id, target_type, target_id);

-- One search-history row per user and query. Keep the newest row.
DELETE s1 FROM search_histories s1
INNER JOIN search_histories s2
    ON s1.user_id = s2.user_id
    AND s1.query_text = s2.query_text
    AND s1.searched_at < s2.searched_at;

DELETE s1 FROM search_histories s1
INNER JOIN search_histories s2
    ON s1.user_id = s2.user_id
    AND s1.query_text = s2.query_text
    AND s1.id > s2.id;

ALTER TABLE search_histories
    ADD UNIQUE INDEX uk_user_search_query (user_id, query_text);
