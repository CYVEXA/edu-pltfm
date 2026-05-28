-- Seed Initial Data
INSERT INTO users (email, password, full_name, role) VALUES ('student@test.com', 'password', 'Test Student', 'STUDENT');
INSERT INTO users (email, password, full_name, role) VALUES ('admin@cyvexa.com', 'admin123', 'System Admin', 'ADMIN');

-- Seed Initial Courses and Lessons have been disabled to keep database fresh
-- INSERT INTO courses (title, description, what_you_will_learn, outcomes, modules, thumbnail_uri, type) 
-- VALUES ('Cybersecurity Basics', 'Learn the fundamentals of protecting systems and networks.', 'Risk Assessment, Network Security, Encryption', 'Understanding of core security principles', 'Module 1: Introduction\nModule 2: Network Safety', 'https://images.unsplash.com/photo-1550751827-4bd374c3f58b?w=400', 'Free');

-- INSERT INTO courses (title, description, what_you_will_learn, outcomes, modules, thumbnail_uri, type) 
-- VALUES ('Ethical Hacking', 'Master the art of penetration testing and security auditing.', 'Nmap, Metasploit, Web Vulnerabilities', 'Hands-on hacking skills', 'Module 1: Setup\nModule 2: Exploitation', 'https://images.unsplash.com/photo-1563986768609-322da13575f3?w=400', 'Free');

-- Seed Lessons
-- INSERT INTO lessons (title, video_path, duration, course_id) VALUES ('Welcome to Cybersecurity', 'https://www.w3schools.com/html/mov_bbb.mp4', '05:00', 1);
-- INSERT INTO lessons (title, video_path, duration, course_id) VALUES ('Network Fundamentals', 'https://www.w3schools.com/html/movie.mp4', '15:00', 1);
-- INSERT INTO lessons (title, video_path, duration, course_id) VALUES ('Hacking Lab Setup', 'https://www.w3schools.com/html/mov_bbb.mp4', '10:00', 2);

-- Seed Enrollment
-- INSERT INTO enrollments (user_id, course_id, enrollment_date, progress) VALUES (1, 1, CURRENT_TIMESTAMP, 45);
