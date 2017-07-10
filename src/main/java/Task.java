import org.sql2o.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Task {
  private String description;
  private boolean completed;
  private LocalDateTime createdAt;
  private int id;
  private int categoryId;

  public Task(String description, int categoryId) {
    this.description = description;
    completed = false;
    createdAt = LocalDateTime.now();
    this.categoryId = categoryId;
  }

  public String getDescription() {
    return description;
  }

  public boolean isCompleted() {
    return completed;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

//This method will be responsible for returning all instances of the Task class. However, now that we're using a database to store our data, it will need to execute a SQL command to retrieve this information.
//We chain the executeAndFetch() method on that, passing Task.class as an argument. This executes the SQL command and instructs Java to transform the information we receive into Task objects. This will create a List<Task> object, which we return.
  public static List<Task> all() {
    String sql = "SELECT id, description, categoryId FROM tasks";
    try(Connection con = DB.sql2o.open()) {
     return con.createQuery(sql).executeAndFetch(Task.class);
    }
  }

  public int getId() {
   return id;
 }

//Here, we construct a SQL query stating that we'd like to return the entries from the tasks table whose id property matches the id property we provide.

//We use .addParameter("id", id) to pass in the id argument to the SQL query

//Then, we run .executeAndFetchFirst(Task.class);. This will return the first item in the collection returned by our database, cast as a Task object. That is, it returns the Task whose id matches the id we provided as an argument to our find() method.
 public static Task find(int id) {
   try(Connection con = DB.sql2o.open()) {
     String sql = "SELECT * FROM tasks where id=:id";
     Task task = con.createQuery(sql)
       .addParameter("id", id)
       .executeAndFetchFirst(Task.class);
     return task;
   }
 }

 @Override
 public boolean equals(Object otherTask){
   if (!(otherTask instanceof Task)) {
     return false;
   } else {
     Task newTask = (Task) otherTask;
     return this.getDescription().equals(newTask.getDescription()) &&
            this.getId() == newTask.getId() &&
            this.getCategoryId() == newTask.getCategoryId();
   }
 }

//Again, we attempt to establish a connection with our database using the line DB.sql2o.open().

//Once connected, we construct an SQL statement that uses the placeholder :description. We want to use placeholders whenever we enter data provided by a user directly into our database. This protects against SQL injection, which is when users attempt to sneak malicious SQL statements into a database through forms.

//We create a SQL query by calling createQuery() on our connection, passing in our SQL statement.

//We replace the :description placeholder with this.description using .addParameter("description", this.description).

//We then run .executeUpdate() to run the query.
//We pass an additional argument to the createQuery() method. In addition to providing it our sql command, we also provide it true. The second argument to this method is an option that instructs Sql2o to add the id from the database, saved as a key, to the Query object we are creating.

//We then add getKey() to the end of this method chain. This returns an Object with a numerical value. We typecast this object into an int at the very beginning of this chain of methods, when we state (int) con.createQuery(sql, true)

//The return value of this chain of methods is int representing the object's ID in our database. By including this.id = before the entire chain of methods, we set this Tasks id property to the return value of this logic.
public void save() {
  try(Connection con = DB.sql2o.open()) {
    String sql = "INSERT INTO tasks(description, categoryId) VALUES (:description, :categoryId);";
    this.id = (int) con.createQuery(sql, true)
      .addParameter("description", this.description)
      .addParameter("categoryId", this.categoryId)
      .executeUpdate()
      .getKey();
  }
}

public int getCategoryId() {
  return categoryId;
}

}
