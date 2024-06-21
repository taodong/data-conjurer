# Release Note v1.1.0

1. Extended dialect for more general support.
2. Added support for Time and Boolean data type.
3. Added property row serialization.
4. Added entity name alias support.
5. Fixed the big decimal serialization issue for date and datetime after correlation with the database.
6. Fixed issue of generating same value for chained date type.
7. Added String alternation support.
8. Added String functions for password encoding: BCRYPT(raw_pass), BCRYPT_STRENGTH(raw_pass, strength) and PBKDF2_SHA1(secret, salt, iterations, raw_pass).
9. Added Time function: TIME_BETWEEN(start, end).