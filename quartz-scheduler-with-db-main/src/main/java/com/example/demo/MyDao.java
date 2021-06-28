package com.example.demo;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class MyDao {
	
	@Value("${sample.datasource.username}")
	String userName;
	
	@Value("${sample.datasource.password}")
	String pass;
	
	@Value("${sample.datasource.jdbc-url}")
	String url;
	
	@Value("${sample.datasource.driverClassName}")
	String driver;
	
	private Connection getConnection(){
		Connection con = null;
		try{
			Class.forName(driver);
			con  = DriverManager.getConnection(url, userName, pass);
		}catch(Exception e){
			e.printStackTrace();
		}
		return con;
	}
	
	public ScheduleDTO getScheduleDTO(String scheduleName){
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		ScheduleDTO dto = null;
		try{
			con  = getConnection();
			stmt = con.createStatement();
			rs = stmt.executeQuery("select * from schedule_detail where schedule_name = '"+scheduleName+"';");
			if(rs != null){
				dto = new ScheduleDTO();
				while(rs.next()){
					dto.setCron(rs.getString("cron_exp"));
					dto.setName(rs.getString("schedule_name"));
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				stmt.close();
				con.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return dto;
	}
	
	
	
	public boolean update(String scheduleName, String fireTime, String nextFireTime){
		boolean added = false;
		Connection con = null;
		Statement stmt = null;
		try{
			con  = getConnection();
			stmt = con.createStatement();
			int i = stmt.executeUpdate("update schedule_detail set fire_time='"+fireTime+"', next_fire_time='"+nextFireTime+"' where schedule_name = '"+scheduleName+"';");
			if(i > 0){
				added = true;
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				stmt.close();
				con.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return added;
	}
	
	public boolean save(String scheduleName, String cronEx){
		boolean added = false;
		Connection con = null;
		Statement stmt = null;
		try{
			scheduleName = scheduleName.concat("_"+new Date().getTime());
			con  = getConnection();
			stmt = con.createStatement();
			int i = stmt.executeUpdate("insert into schedule_detail(schedule_name, cron_exp) values ('"+scheduleName+"', '"+cronEx+"');");
			if(i > 0){
				added = true;
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				stmt.close();
				con.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return added;
	}
	
}
