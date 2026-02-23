-- asset_tag のテストデータ
INSERT INTO asset_tag (asset_id, tag_id, source, confidence_score, deleted)
VALUES (1, 1, 'AI', 0.94, FALSE),
       (1, 6, 'AI', 0.92, FALSE),
       (1, 14, 'USER', NULL, FALSE),
       (1, 16, 'AI', 0.90, FALSE),
       (1, 26, 'AI', 0.95, FALSE);
INSERT INTO asset_tag (asset_id, tag_id, source, confidence_score, deleted)
VALUES (2, 2, 'AI', 0.96, FALSE),
       (2, 6, 'AI', 0.91, FALSE),
       (2, 24, 'AI', 0.93, FALSE),
       (2, 29, 'AI', 0.88, FALSE),
       (2, 26, 'AI', 0.94, FALSE);
INSERT INTO asset_tag (asset_id, tag_id, source, confidence_score, deleted)
VALUES (3, 3, 'AI', 0.90, FALSE),
       (3, 4, 'AI', 0.92, FALSE),
       (3, 18, 'AI', 0.89, FALSE),
       (3, 23, 'AI', 0.87, FALSE),
       (3, 15, 'USER', NULL, FALSE);
INSERT INTO asset_tag (asset_id, tag_id, source, confidence_score, deleted)
VALUES (4, 1, 'USER', NULL, FALSE),
       (4, 25, 'AI', 0.91, FALSE),
       (4, 11, 'AI', 0.85, FALSE),
       (4, 13, 'AI', 0.88, FALSE),
       (4, 26, 'AI', 0.93, FALSE);
INSERT INTO asset_tag (asset_id, tag_id, source, confidence_score, deleted)
VALUES (5, 5, 'AI', 0.92, FALSE),
       (5, 17, 'AI', 0.89, FALSE),
       (5, 21, 'AI', 0.86, FALSE),
       (5, 7, 'AI', 0.84, FALSE);
INSERT INTO asset_tag (asset_id, tag_id, source, confidence_score, deleted)
VALUES (6, 10, 'AI', 0.60, FALSE),
       (6, 30, 'AI', 0.55, FALSE);

INSERT INTO asset_tag (asset_id, tag_id, source, confidence_score, deleted)
VALUES (7, 1, 'AI', 0.60, FALSE),
       (8, 1, 'AI', 0.55, FALSE),
       (9, 1, 'AI', 0.55, FALSE),
       (10, 1, 'AI', 0.55, FALSE),
       (11, 1, 'AI', 0.55, FALSE),
       (12, 1, 'AI', 0.55, FALSE),
       (13, 1, 'AI', 0.55, FALSE),
       (14, 1, 'AI', 0.55, FALSE),
       (15, 1, 'AI', 0.55, FALSE)
;