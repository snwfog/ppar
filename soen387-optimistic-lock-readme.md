# Optimistic Offline Lock

## Description

The optimistic offline lock is a currency pattern that insure the consistency of the database when there are multiple reader/writer access the same resource (aka. same row, same object) from the database. By requiring a lock before giving the permission to modify the database, the goal is to ensure that in a race scenario, one process performing an action with a side effect will not affect the consistency of the database versus another process with a side effect. 

Optimistic lock resolve the conflict at the time when the writer is commiting the changes, and assume that the user will usually not be making a change when the user checkout the resource. Whereas a persimistic lock assume that reading, or writing will be treated differently.

*In this assignment, the pattern is implemented with optimistic
locking, but don't distinguish between reading and writing*


