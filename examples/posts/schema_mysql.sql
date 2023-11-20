CREATE TABLE `usr_user` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `first_name` varchar(20) NOT NULL,
  `last_name` varchar(20) NOT NULL,
  `created_date` date NOT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE `post` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `post` varchar(100) NOT NULL,
  `user_id` int unsigned NOT NULL,
  `created_date` datetime NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_user_post` FOREIGN KEY (`user_id`) REFERENCES `usr_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE `comment` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `comment` varchar(80) NOT NULL,
  `user_id` int unsigned NOT NULL,
  `post_id` int unsigned NOT NULL,
  `created_date` datetime NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_user_comment` FOREIGN KEY (`user_id`) REFERENCES `usr_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_post_comment` FOREIGN KEY (`post_id`) REFERENCES `post` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
);