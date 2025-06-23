-- :name create-user!
-- :command :insert
INSERT INTO users (email, password)
VALUES (:email, :password);

-- :name get-user-by-email
-- :result :one
SELECT * FROM users
WHERE email = :email;