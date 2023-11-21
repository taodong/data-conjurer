CREATE TABLE `usr_user` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(20) NOT NULL,
  `first_name` VARCHAR(20) NOT NULL,
  `last_name` VARCHAR(20) NOT NULL,
  `created` TIMESTAMP NOT NULL,
  `modified` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE (`username`)
);

CREATE TABLE `product_category` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(20) NOT NULL,
  `desc` TEXT,
  `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
  `modified` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
);

CREATE TABLE `product` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(20) NOT NULL,
  `desc` TEXT NOT NULL,
  `sku` CHAR(10) NOT NULL,
  `category_id` INT UNSIGNED NOT NULL,
  `price` DECIMAL(5,2) NOT NULL,
  `modified` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE (`name`),
  CONSTRAINT `fk_category_product` FOREIGN KEY (`category_id`) REFERENCES `product_category` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE `order_detail` (
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
    `user_id` INT UNSIGNED NOT NULL,
    `status` VARCHAR(20) NOT NULL,
    `created` TIMESTAMP NOT NULL,
    `modified` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_user_order` FOREIGN KEY (`user_id`) REFERENCES `usr_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE `order_item` (
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
    `order_id` INT UNSIGNED NOT NULL,
    `product_id` INT UNSIGNED NOT NULL,
    `quantity` INT NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_order_item` FOREIGN KEY (`order_id`) REFERENCES `order_detail` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_product_item` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
);