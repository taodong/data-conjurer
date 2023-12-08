CREATE TABLE IF NOT EXISTS `usr_user` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `first_name` varchar(20) NOT NULL,
  `last_name` varchar(20) NOT NULL,
  `created_date` date NOT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `post` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `post` varchar(100) NOT NULL,
  `user_id` int unsigned NOT NULL,
  `created_date` datetime NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_user_post` FOREIGN KEY (`user_id`) REFERENCES `usr_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS `comment` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `comment` varchar(80) NOT NULL,
  `user_id` int unsigned NOT NULL,
  `post_id` int unsigned NOT NULL,
  `created_date` datetime NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_user_comment` FOREIGN KEY (`user_id`) REFERENCES `usr_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_post_comment` FOREIGN KEY (`post_id`) REFERENCES `post` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS `comment_comment` (
    `id` int unsigned NOT NULL AUTO_INCREMENT,
    `parent` int unsigned NOT NULL,
    `child` int unsigned NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_parent_comment` FOREIGN KEY (`parent`) REFERENCES `comment` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_child_comment` FOREIGN KEY (`child`) REFERENCES `comment` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
)