package com.github.mauricioaniche.ck;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;


import data.io.util.IOUtil;

public class DBUtil {

  private static String user = "root";
  private static String pwd = "123456";

  // LIU IP: 223.3.88.70
  private static String url = "jdbc:mysql://127.0.0.1:3306/smell";
  //private static String url = "jdbc:mysql://58.192.114.184:3306/smell"; // SERVRE: 58.192.114.184
  //private static String url = "jdbc:mysql://223.3.83.32:3306/smell"; // CHEN IP: 223.3.83.32
  private static String driver = "com.mysql.jdbc.Driver";

  public static void main(String[] arg) {
    //DBUtil.setDataBaseURL("jdbc:mysql://58.192.114.184:3306/smell"); // SERVRE: 58.192.114.184
    //DBUtil.setDataBaseURL("jdbc:mysql://223.3.88.70/smell");
    Connection connect = DBUtil.getConnection();

    try {
			/*DataWriter writer = new DataWriter("C:\\Desktop\\test.csv");
			writer.close();*/

			/*String[] projectNames = { "cassandra", "camel", "derby", "guava", "guice", "elasticsearch", "hibernate", "hive",
					"qpid", "wicket", "pig", "lucene", "checkstyle", "hbase" };*/
      String[] projectNames = {"camel"};
      Statement stmt = connect.createStatement();
      for (String projectName : projectNames) {
        String tableName = "table_" + projectName;
        String selectSQL = "DELETE  FROM " + tableName
            + " where locate('/test/', old_file_path) > 0 OR locate('/testing/', old_file_path) "
            + "OR locate('/test-framework/', old_file_path) OR locate('/example/', old_file_path) "
            + "OR locate('/examples/', old_file_path) OR locate('/demo/', old_file_path) OR locate('/benchmark/', old_file_path) "
            + "OR locate('/testng/', old_file_path)";

        int i = stmt.executeUpdate(selectSQL);// by update to delete elements
        System.out.println(i + " records are deleted from " + tableName);
      }

			/*//DELETE FROM  guava_smell WHERE id < 8000000
			String deleteSQL = "DELETE FROM " + tableName + " WHERE id < 8000000";
			
			boolean result = stmt.execute(deleteSQL);
			if (result == true) {
				System.out.println("no elements to delete");
			} else {
				System.out.println("delete successfully");
			}*/

			/* update sql
			
			tableName = "spark_smell";
			double ratio = 0;// entity_size
			//String updateSQL = "update " + tableName + " set ratio = " + ratio + " where changes = 0";
			//stmt.execute(updateSQL);*/

			/*String deleteSQL = "DELETE e1 FROM " + tableName + " e1," + tableName
					+ " e2 WHERE e1.Id > e2.Id AND e1.uniqueID = e2.uniqueID;";
			
			boolean result = stmt.execute(deleteSQL);
			if (result == true) {
				System.out.println("no elements to delete");
			} else {
				System.out.println("delete successfully");
			}*/

			/*String uniqueID = "";
			String changes = "100";
			String ratio = "0.25";
			String updateSQL = "update " + tableName + " set uniqueID = '" + uniqueID + "', changes = '" + changes
					+ "', ratio = '" + ratio + "' where uniqueID = '" + uniqueID + "'";
			stmt.execute(updateSQL);*/

      // delete sql
			/*String tableName = "gumtree_smell";
			String deleteSQL = "DELETE FROM " + tableName + " WHERE old_file_path = " + "'"
					+ "F:/commit-corpus/gumtree/gumtree2013-12-04-16-59-51-44c4144/client/src/main/java/fr/labri/gumtree/client/DiffOptions.java"
					+ "'";
			boolean result = stmt.execute(deleteSQL);
			if (result == true) {
				System.out.println("no elements to delete");
			} else {
				System.out.println("delete successfully");
			}*/

      // delete table
      //stmt.execute("DROP TABLE" + tableName);

			/*
			insert sql
			String insertSQL = "insert into " + tableName
						+ " (project_name,version_number,version_path,package_name,"
						+ "file_name,class_name,method_name,start_line,end_line)" + " values('" + projectname + "','"
						+ versionnumber + "','" + versionpath + "','" + packagename + "','" + filename + "','"
						+ classname + "','" + methodname + "','" + startline + "','" + endline + "')";
			stmt.execute(insertSQL);
			*/

      //query sql
			/*String querySQL = "select package_name, class_name, method_name, file_name from " + tableName
					+ " where project_name = 'je'";
			ResultSet rs2 = stmt.executeQuery(querySQL);
			
			rs2.close();*/

      stmt.close();
      connect.close();
    } catch (Exception e) {
      System.out.print("connect error!");
      e.printStackTrace();
    }
  }

  public static boolean whetherTableExistInDataBase(String tableName, Connection connect)
      throws SQLException {
    boolean exist = false;
    ResultSet rs = connect.getMetaData().getTables(null, null, tableName, null);
    while (rs.next()) {
      String tName = rs.getString("TABLE_NAME");
      if (tName != null && tName.equalsIgnoreCase(tableName)) {
        exist = true;
        break;
      }
    }
    return exist;
  }

  public static Connection getConnection() {
    Connection connect = null;
    try {
      Class.forName(driver); // Load JDBC Driven Program
      //System.out.println("Success to Load Mysql Driver.");
    } catch (Exception e) {
      System.out.print("Error to Load Mysql Driver.");
      e.printStackTrace();
    }
    try {
      connect = DriverManager.getConnection(url, user, pwd);
      // System.out.println("Success connect Mysql server!");
    } catch (Exception e) {
      System.out.print("Fail to Connect Mysql.");
      e.printStackTrace();
    }
    return connect;
  }

  public static void close(Connection conn) {
    if (conn != null) {
      try {
        conn.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }

  public static void runBatch(Connection connect, List<String> sqls) {
    int len = sqls.size();
    if (len == 0) {
      return;
    }

    boolean originalAutoCommit = false;
    boolean flag = true;
    try {
      originalAutoCommit = connect.getAutoCommit(); // remember original auto-commit mode
      connect.setAutoCommit(false);
      Statement stat = connect.createStatement();
      for (int i = 0; i < len; i++) {
        stat.addBatch(sqls.get(i));
      }
      stat.executeBatch();
      connect.commit();
      stat.close();
    } catch (SQLException e) {
      flag = false;
      try {
        connect.rollback();
      } catch (SQLException ex) {
      }
    } finally {
      try {
        connect.setAutoCommit(originalAutoCommit);
      } catch (SQLException e) {
        e.printStackTrace();
      }
      if (flag == false) {
        System.out.println("fail to run batch process");
        //System.out.println(sqls);
        IOUtil.catchError(sqls);
      }
    }
  }

  public static void setDataBaseURL(String url) {
    DBUtil.url = url;
  }

  /**
   * @param connect do not close connect
   */
  public static void createChangeTable(Connection connect, String tableName) {
    try {
      connect.setAutoCommit(false); // attention !!
      Statement stmt = connect.createStatement();

      if (DBUtil.whetherTableExistInDataBase(tableName, connect) == false) {
        String tableCreationSQL =
            "CREATE TABLE " + tableName + "(Id int(11) NOT NULL AUTO_INCREMENT, "
                + "old_version_id VARCHAR(100) DEFAULT NULL, "
                + "new_version_id VARCHAR(100) DEFAULT NULL, "
                + "old_file_path VARCHAR(2000) DEFAULT NULL, "
                + "new_file_path VARCHAR(2000) DEFAULT NULL,"
                + "smell_vector VARCHAR(1000) DEFAULT NULL,"
                + "structural_change_vector VARCHAR(1000) DEFAULT NULL,"
                + "file_code_churn INT(11) DEFAULT NULL," + "file_size INT(11) DEFAULT NULL,"
                + "from_ChangeDistiller VARCHAR(100) DEFAULT NULL," + "PRIMARY KEY (Id)"
                + ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
        int i = stmt.executeUpdate(tableCreationSQL);
        connect.commit();
        if (i == 0) {
          System.out.println("TABLE " + tableName + " was newly created");
        }
      } else {
        System.out.println("TABLE " + tableName + " exists");
      }
      stmt.close();
    } catch (SQLException e) {
      System.out.println("error: fail to creat TABLE " + tableName);
    }
  }

  public static void createSmellTable(Connection connect, String tableName) {
    try {
      connect.setAutoCommit(false); // attention !!
      Statement stmt = connect.createStatement();

      // file_structural_churn: INT(11) --> VARCHAR(2000)
      if (DBUtil.whetherTableExistInDataBase(tableName, connect) == false) {
        String tableCreationSQL =
            "CREATE TABLE " + tableName + "(Id int(11) NOT NULL AUTO_INCREMENT, "
                + "smell_type VARCHAR(100) DEFAULT NULL, "
                + "old_file_path VARCHAR(1000) DEFAULT NULL, "
                + "version_id VARCHAR(300) DEFAULT NULL, " + "class_name VARCHAR(500) DEFAULT NULL,"
                + "method_name VARCHAR(500) DEFAULT NULL," + "line_range VARCHAR(100) DEFAULT NULL,"
                + "PRIMARY KEY (Id)" + ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
        int i = stmt.executeUpdate(tableCreationSQL);
        connect.commit();
        if (i == 0) {
          System.out.println("TABLE " + tableName + " was newly created");
        }
      } else {
        System.out.println("TABLE " + tableName + " exists");
      }
      stmt.close();
    } catch (SQLException e) {
      System.out.println("error: fail to creat TABLE " + tableName);
    }
  }

  public static void clearDataBaseTable(Connection connect, String tableName) {
    try {
      connect.setAutoCommit(false); // attention !!
      Statement stmt = connect.createStatement();
      if (DBUtil.whetherTableExistInDataBase(tableName, connect) == true) {
        System.out.println("TABLE " + tableName + " exists");
        String tableSQL = "TRUNCATE TABLE " + tableName + ";";  // clear rather than delete table
        int i = stmt.executeUpdate(tableSQL);
        connect.commit();
        if (i == 0) { // returned value 0 is very confusing
          System.out.println("TABLE " + tableName + " was cleared");
        }
      }
      stmt.close();
    } catch (SQLException e) {
      System.out.println("error: fail to clear TABLE " + tableName);
    }
  }
}
