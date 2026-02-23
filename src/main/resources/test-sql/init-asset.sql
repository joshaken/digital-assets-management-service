-- assetのテストデータ
INSERT INTO asset (id, title, file_path, ai_tag_status, ai_tag_retry_count, deleted) VALUES
    (1, 'Business Executive Portrait for Commercial Campaign',
     'https://images.unsplash.com/photo-1556157382-97eda2d62296',
     'SUCCESS', 0, FALSE);
INSERT INTO asset (id, title, file_path, ai_tag_status, ai_tag_retry_count, deleted) VALUES
    (2, 'Nature Landscape Cinematic Documentary Shot',
     'https://images.unsplash.com/photo-1500530855697-b586d89ba3ee',
     'SUCCESS', 0, FALSE);
INSERT INTO asset (id, title, file_path, ai_tag_status, ai_tag_retry_count, deleted) VALUES
    (3, 'Journalist Interview in Urban Environment',
     'https://images.unsplash.com/photo-1529336953121-ad5a0d43d0d2',
     'SUCCESS', 1, FALSE);
INSERT INTO asset (id, title, file_path, ai_tag_status, ai_tag_retry_count, deleted) VALUES
    (4, 'Corporate Conference Hall Presentation',
     'https://images.unsplash.com/photo-1511578314322-379afb476865',
     'SUCCESS', 0, FALSE);
INSERT INTO asset (id, title, file_path, ai_tag_status, ai_tag_retry_count, deleted) VALUES
    (5, 'Behind The Scenes Film Production',
     'https://images.unsplash.com/photo-1492724441997-5dc865305da7',
     'SUCCESS', 0, FALSE);
INSERT INTO asset (id, title, file_path, ai_tag_status, ai_tag_retry_count, ai_tag_fail_reason, deleted) VALUES
    (6, 'Low Light Editorial Portrait',
     'https://images.unsplash.com/photo-1494790108377-be9c29b29330',
     'FAILED', 3, 'Vision model confidence below threshold', FALSE);