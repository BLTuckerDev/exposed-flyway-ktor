CREATE TABLE "Notes" (
                         "id" INT AUTO_INCREMENT PRIMARY KEY,
                         "user_id" INT NULL, -- Nullable at first for safety
                         "title" VARCHAR(255) NOT NULL,
                         "content" TEXT NOT NULL,
                         "created_at" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         CONSTRAINT fk_notes_user_id FOREIGN KEY ("user_id") REFERENCES "Users"("id")
);
