-- Insert roles
INSERT INTO roles (name) VALUES ('ROLE_USER');
INSERT INTO roles (name) VALUES ('ROLE_ADMIN');

-- Insert admin user (password: admin123)
INSERT INTO users (email, password, first_name, last_name) 
VALUES ('admin@fruitshop.com', '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6', 'Admin', 'User');

-- Assign admin role
INSERT INTO user_roles (user_id, role_id) 
SELECT u.id, r.id FROM users u, roles r 
WHERE u.email = 'admin@fruitshop.com' AND r.name = 'ROLE_ADMIN';

-- Insert sample products
INSERT INTO products (name, description, price, stock_quantity, category, is_organic, seasonality, image_url) VALUES 
-- Fruits
('Apple', 'Fresh red apples from local orchards', 1.99, 100, 'Fruits', true, 'All Year', 'https://example.com/images/apple.jpg'),
('Banana', 'Sweet yellow bananas', 0.99, 150, 'Fruits', false, 'All Year', 'https://example.com/images/banana.jpg'),
('Orange', 'Juicy sweet oranges', 1.49, 80, 'Fruits', true, 'Winter', 'https://example.com/images/orange.jpg'),
('Strawberry', 'Fresh strawberries', 2.99, 50, 'Fruits', true, 'Spring', 'https://example.com/images/strawberry.jpg'),
('Mango', 'Ripe sweet mangoes', 2.49, 60, 'Fruits', false, 'Summer', 'https://example.com/images/mango.jpg'),
('Blueberry', 'Fresh blueberries', 3.99, 40, 'Fruits', true, 'Summer', 'https://example.com/images/blueberry.jpg'),

-- Vegetables
('Carrot', 'Fresh organic carrots', 1.29, 120, 'Vegetables', true, 'All Year', 'https://example.com/images/carrot.jpg'),
('Tomato', 'Ripe red tomatoes', 0.79, 200, 'Vegetables', false, 'Summer', 'https://example.com/images/tomato.jpg'),
('Spinach', 'Fresh organic spinach', 2.49, 80, 'Vegetables', true, 'All Year', 'https://example.com/images/spinach.jpg'),
('Broccoli', 'Fresh green broccoli', 1.99, 90, 'Vegetables', true, 'Fall', 'https://example.com/images/broccoli.jpg'),
('Bell Pepper', 'Colorful bell peppers', 1.49, 100, 'Vegetables', false, 'Summer', 'https://example.com/images/bell-pepper.jpg'),
('Cucumber', 'Fresh cucumbers', 0.99, 150, 'Vegetables', true, 'Summer', 'https://example.com/images/cucumber.jpg'),

-- Herbs
('Basil', 'Fresh organic basil', 1.99, 50, 'Herbs', true, 'Summer', 'https://example.com/images/basil.jpg'),
('Mint', 'Fresh mint leaves', 1.49, 60, 'Herbs', true, 'Spring', 'https://example.com/images/mint.jpg'),
('Cilantro', 'Fresh cilantro', 0.99, 70, 'Herbs', true, 'All Year', 'https://example.com/images/cilantro.jpg'); 