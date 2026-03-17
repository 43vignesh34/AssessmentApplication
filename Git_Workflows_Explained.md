# Professional Git Workflows: Branching, Merging, and Conflict Resolution

Git is an essential distributed version control system that enables team collaboration. This guide explores the "Parallel Universes" of development and how to manage them efficiently.

## 1. Branching (The Parallel Universe)
Right now, you are acting as a Solo Developer. You are making changes directly to the `main` branch. 

Imagine you get a job, and the Senior Engineer says: *"Vignesh, I need you to build the JWT Engine today."*

You don't want to write half the JWT engine, accidentally break the `UserService`, and then push that broken code to `main` where the rest of your team is working!

**The Solution:** You create a branch.
`git checkout -b feature/jwt-engine`

Think of a branch as a **Parallel Universe**. You just cloned the entire `AssessmentApplication` repository. Inside this `feature/jwt-engine` universe, you can create the `JwtService.java` file, delete the `UserController` by mistake, or set the whole server on fire. 

Meanwhile, back in the original `main` universe, your coworkers are happily adding new `Analytics` features. Their code compiles perfectly. They are completely unaware of the fire in your universe. 

## 2. Merging (Bringing the Universes Together)
You finished the JWT Engine in your parallel universe. The code compiles. You run `mvn test` and everything passes.

Now, you need to bring your awesome new `JwtService.java` code back into the official `main` universe so the company can deploy it.

**The Solution:** You merge.
- You switch back to the main timeline: `git checkout main`
- You tell Git to pull your alternate timeline in: `git merge feature/jwt-engine`

Git is smart. It looks at your branch, sees you created one new file (`JwtService`), and simply copies that file into `main`. The universes are now combined!

*(Note: In the real world, you don't use the command line terminal to merge. You go to GitHub, click "Create Pull Request", a Senior Engineer reviews your code, and then they click a green "Merge" button on the website!)*

## 3. Rebasing (Rewriting History)
This is the hardest concept, so let's use a specific example based on the `AssessmentApplication`.

### The Setup
You and your coworker (Bob) both arrive at work on **Monday morning.**

The `User.java` file currently looks like this on the `main` branch:
```java
public class User {
    private int id;
    private String username;
    private String password;
}
```

### The Parallel Universes
**Bob's Job:** He needs to add an `email` field to the `User` class.
*   He types: `git checkout -b feature/add-email`
*   He adds `private String email;` right below `password`.

**Your Job:** You need to add the Spring Security `Role` enum to the `User` class (Exactly what you did in Phase 2!)
*   You type: `git checkout -b feature/add-roles`
*   You add `private Role role;` right below `password`.

### The Problem (Tuesday)
Bob finishes his work fast. On Tuesday, he merges his code into `main`.

Now, the official `main` branch looks like this:
```java
public class User {
    private int id;
    private String username;
    private String password;
    private String email; // Bob's new code
}
```

Meanwhile, in your parallel universe (`feature/add-roles`), you haven't merged yet. Your local `User.java` file still looks like this:
```java
public class User {
    private int id;
    private String username;
    private String password;
    private Role role; // Your new code
}
```

### The Climax (Wednesday)
You finish your work and try to merge your branch into `main`. GitHub stops you and throws a **Merge Conflict**.

GitHub says: *"Wait! Both Vignesh and Bob tried to insert a brand new line of code into the exact same spot (right below `password`). I don't know whose code is supposed to go first! Should `email` be on top, or should `role` be on top? Help!"*

### The Solution: `git rebase`

Instead of panicking or manually trying to fix the conflict on GitHub, you use **Rebase**.

While on your local `feature/add-roles` branch, you type:
`git pull --rebase origin main`

Here is exactly what Git does behind the scenes, step-by-step:
1.  **Rewind:** Git literally un-does your `private Role role;` commit and saves it in a temporary clipboard. It puts your file back to exactly how it looked on Monday morning.
2.  **Fast-Forward:** Git downloads Bob's new `email` code from `main` and applies it to your computer.
    * *Your local file now has Bob's code.*
3.  **Replay:** Git takes your `Role` code out of the clipboard, and says: *"Okay Vignesh, pretend like Bob already finished his work. Now, let's pretend you are just starting your work today. Where do you want to put your `Role` code based on Bob's new file?"*

You choose to put your `Role` code below Bob's `email` code. You save the file.

### The Result
Your timeline has been rewritten. It no longer looks like you and Bob worked at the exact same time. It mathematically looks like Bob did his work on Monday, and you started *your* work on Wednesday.

Because the timelines are now perfectly sequential, you `git push` your branch, the Merge Conflict completely disappears, and your combined code successfully merges into `main`! 

*(This is why Senior Engineers love Rebase: It keeps the company's Git History in a perfect, straight, easy-to-read line, instead of a tangled web of parallel universes).*

---

## 4. Advanced Concepts for Enterprise Engineers

If you understand Branching, Merging, and Rebasing, you already know 90% of what you need for a daily Software Engineering job. However, to truly hit the ground running (and to pass Senior-level interview questions), here are 3 advanced concepts you should know.

### A. `git stash` (The Emergency Drawer)
**The Scenario:** You are halfway done building the `JwtService`. The code is completely broken and won't compile. Suddenly, your manager calls and says: *"Vignesh, Production is down! I need you to switch to the `main` branch and fix a typo immediately!"*

**The Problem:** You can't switch branches if you have unsaved, broken code laying around. But you also don't want to `commit` broken code to the repository.

**The Solution:** You type `git stash`. 
Git takes all your messy, unsaved code off your desk and shoves it into a hidden drawer. Your desk is now perfectly clean. You switch to `main`, fix the production bug, and switch back to your branch. Then you type `git stash pop`, and Git takes all your messy code out of the drawer and puts it exactly back where you left it.

### B. `git cherry-pick` (The Sniper)
**The Scenario:** Your coworker Bob is working on a massive, 3-week long branch to update the database. It is not ready to merge yet. However, inside that branch, Bob wrote one specific helper function (`StringUtils.java`) that you desperately need for your own `JwtService`.

**The Problem:** You can't merge Bob's entire branch into yours, because it's full of half-finished database code. You *only* want that one specific commit where he created the helper function.

**The Solution:** You find the exact 7-character Hash ID of Bob's commit (e.g., `A1B2C3D`). While on your branch, you type: `git cherry-pick A1B2C3D`.
Git acts like a sniper. It ignores the rest of Bob's branch, extracts *only* that specific commit, and seamlessly applies it to your branch.

### C. `git bisect` (The Detective)
**The Scenario:** Your application compiles perfectly, but a QA Tester reports that the "Login Button" is broken. You know the Login Button worked perfectly on Friday (100 commits ago!). You have a team of 10 people pushing code all weekend. You have no idea which of the 100 commits broke the button.

**The Problem:** Testing 100 commits one-by-one to find the bug will take you 8 hours.

**The Solution:** You type `git bisect start`.
You tell Git: *"The code is bad right now. But the code was good at commit `#123456`."*
Git acts like a detective doing a Binary Search. It automatically checks out the exact middle commit (Commit #50) and asks: *"Is the button broken here?"*
If you say yes, Git knows the bug happened between 1 and 50. It checks out Commit #25. It keeps cutting the timeline in half. Instead of testing 100 commits manually, `git bisect` guarantees you will find the exact line of code that broke the application in 6 or 7 steps.

### D. `git checkout -- <file>` (The Surgical Grabber)
**The Scenario:** You are working on an experimental branch (`feature/docker-test`) where you've broken half the project. However, you wrote one perfect documentation file (`cheatSheet.md`) that you want to bring to the `main` branch immediately.

**The Problem:** If you run `git merge feature/docker-test`, you will pull *all* your broken code into `main` along with the documentation file.

**The Solution:** You switch to your clean branch (`git checkout main`), and then you tell Git to act like a delivery driver by attaching file paths to the checkout command:
*   `git checkout feature/docker-test -- cheatSheet.md`

Git looks at the `--` (Double Dash) and knows you are NOT asking to switch branches. Instead, it reaches into the `feature/docker-test` branch, grabs that exact file, and drops a copy of it into your current `main` branch workspace without merging anything else!
