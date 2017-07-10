import java.util.List;
import java.util.ArrayList;
import org.sql2o.*;

public class Category {
  private String name;
  private int id;

  public Category(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  // public static ArrayList<Category> all() {
  // }

  public int getId() {
    return id;
  }

//note that it is not static because we will call it on individual Category instances in order to return that specific Category's corresponding Tasks.
//Notice the SQL statement we're executing here is "SELECT * FROM tasks WHERE categoryId=:id". In "plain English" this would read "Select all the Tasks from the table named 'tasks' whose categoryId property matches this ID we're providing."
//This will return a List containing all Tasks whose categoryId property matches the id property of the Category we call this method upon.
  public List<Task> getTasks() {
    try(Connection con = DB.sql2o.open()) {
      String sql = "SELECT * FROM tasks WHERE categoryId=:id";
      return con.createQuery(sql)
        .addParameter("id", this.id)
        .executeAndFetch(Task.class);
    }
  }

//Here we are using a select query using where id=:id. We use .addParameter("id", id) to pass in the id argument to the sql query and then we run .executeAndFetchFirst(Category.class);. This will return the first item in the collection returned by our database, cast as a Category object. Finally, we return that Category.
  public static Category find(int id) {
      try(Connection con = DB.sql2o.open()) {
        String sql = "SELECT * FROM categories WHERE id=:id;";
        Category category = con.createQuery(sql)
          .addParameter("id", id)
          .executeAndFetchFirst(Category.class);
        return category;
      }
    }

//Return all Category information from our categories database table in the all() method:
//Here, we construct a basic SQL query requesting all id and description data from the categories table.
//We create a query and perform executeAndFetch(Category.class);. This will execute the SQL command and turn each row of data returned into an object based on the argument. In this case we pass Category.class, which creates Category objects and stores them in a List<Category>.
  public static List<Category> all() {
    String sql = "SELECT id, name FROM categories";
    try(Connection con = DB.sql2o.open()) {
      return con.createQuery(sql).executeAndFetch(Category.class);
    }
  }

  @Override
  public boolean equals(Object otherCategory) {
    if (!(otherCategory instanceof Category)) {
      return false;
    } else {
      Category newCategory = (Category) otherCategory;
      return this.getName().equals(newCategory.getName()) &&
             this.getId() == newCategory.getId();
    }
  }

//In String sql we use the placeholder :name.
//We replace the :name placeholder with this.name in the line .addParameter("name", this.name).
//We run .executeUpdate() to run the query.
//In our method call, con.createQuery(sql, true), we pass the argument true. This tells Sql2o to add the id, saved as the key, to Query.

//We add getKey(). This is saved as an Object with a numerical value. We can use type casting to save this object as an int, which we then save to our instance variable with this.id = (int)
  public void save() {
    try(Connection con = DB.sql2o.open()) {
      String sql = "INSERT INTO categories(name) VALUES (:name);";
      this.id = (int) con.createQuery(sql, true)
        .addParameter("name", this.name)
        .executeUpdate()
        .getKey();
    }
  }

}
