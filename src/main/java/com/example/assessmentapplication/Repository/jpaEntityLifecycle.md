# JPA Entity Lifecycle Guide

## 1. The Foundation: What is the EntityManager? 🧠

Before understanding states, you must know the **EntityManager**. Think of it as a "Project Manager" for your database objects.

* **Persistence Context:** This is the EntityManager’s internal "cache" or "workspace." When an object is inside this context, the EntityManager tracks every change you make to it.
* **The Bridge:** It acts as the middleman between your Java code and the SQL database.

---

## 2. The Four Entity States

### A. New (Transient) 🆕

The object has been instantiated in Java but has never been associated with an EntityManager or a database row.

* **ID:** Usually `null`.
* **Tracking:** Spring/JPA does not know this object exists.
* **Example:**
```java
User user = new User();
user.setName("Alice");
// State: NEW
```

### B. Managed (Persistent) ✅

The entity is currently associated with an active EntityManager session.

* **Tracking:** Any changes made to the object's fields (via setters) will be automatically detected and saved to the database when the transaction commits (**Dirty Checking**).
* **Example:**
```java
@Transactional
public void updateUserName(Long id) {
    User user = entityManager.find(User.class, id); 
    // State: MANAGED
    user.setName("Bob"); 
    // No save() needed! SQL UPDATE happens automatically at method end.
}
```

### C. Detached 🔓

The entity has a database identity (an ID), but it is no longer being tracked by an EntityManager. This usually happens after a transaction closes or the session is cleared.

* **Tracking:** Changes made to a detached object are **not** reflected in the database.
* **Example:**
```java
User user = userService.getUserById(1L); 
// Transaction ends, EntityManager closes.
// State: DETACHED
user.setName("Charlie"); 
// Database still says "Bob".
```

### D. Removed 🗑️

The entity is still in the Persistence Context but is scheduled to be deleted from the database.

* **Example:**
```java
User user = entityManager.find(User.class, id);
entityManager.remove(user);
// State: REMOVED (Delete happens on commit)
```

---

## Summary Table

| State | In Database? | In Persistence Context? | Tracked for Changes? |
| --- | --- | --- | --- |
| **New** | No | No | No |
| **Managed** | Yes | **Yes** | **Yes** |
| **Detached** | Yes | No | No |
| **Removed** | Soon to be deleted | Yes (Marked) | No |
