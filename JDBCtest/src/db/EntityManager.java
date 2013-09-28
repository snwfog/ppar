package db;

import java.sql.*;
import java.util.ArrayList;

import javax.sql.*;
import javax.naming.Context;
import javax.naming.InitialContext;

import bean.Entity;

public class EntityManager {
	
	Connection con = null;
	
	public EntityManager(){
		super();
	}
	
	private void makeConnection(){
		try{
			Context initContext = new InitialContext();
			Context envContext  = (Context)initContext.lookup("java:/comp/env");
			DataSource ds = (DataSource)envContext.lookup("");
			con = ds.getConnection();
	
		} catch (Exception e){
			System.out.println("exception caught" + e);
		}
	}
	
	public ArrayList<Entity> doWork() throws SQLException{
		ArrayList<Entity> entity = new ArrayList<Entity>();
		makeConnection();
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery("Select * from testdata");
		while (rs.next()){
			entity.add(new Entity(rs.getString("foo")));
		}
		return entity;
	}
}
